package com.rubyhuntersky.rxactivity;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;

/**
 * @author wehjin
 * @since 8/6/15
 */
public class GateOperator<T> implements Observable.Operator<T, T> {

    private final Observable<Boolean> gate;

    public GateOperator(Observable<Boolean> gate) {
        this.gate = gate;
    }

    @Override
    public Subscriber<? super T> call(final Subscriber<? super T> lowerSubscriber) {
        return new Subscriber<T>() {

            private StoreAndForward<T> storeAndForward;
            private Subscription gateSubscription;
            private boolean isEnded;

            @Override
            public void onStart() {
                super.onStart();
                storeAndForward = new StoreAndForward<>(lowerSubscriber);
                gateSubscription = gate.subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        if (isEnded) {
                            return;
                        }
                        stop();
                        lowerSubscriber.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (isEnded) {
                            return;
                        }
                        stop();
                        lowerSubscriber.onError(e);
                    }

                    @Override
                    public void onNext(Boolean isOpen) {
                        storeAndForward.setGate(isOpen);
                    }

                    private void stop() {
                        isEnded = true;
                        gateSubscription = null;
                    }
                });
                lowerSubscriber.add(gateSubscription);
            }

            @Override
            public void onCompleted() {
                if (isEnded) {
                    return;
                }
                stop();
                lowerSubscriber.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                if (isEnded) {
                    return;
                }
                stop();
                lowerSubscriber.onError(e);
            }

            @Override
            public void onNext(T t) {
                storeAndForward.addValue(t);
            }

            private void stop() {
                isEnded = true;
                gateSubscription.unsubscribe();
                gateSubscription = null;
            }
        };
    }

    private static class StoreAndForward<T> {

        private final Observer<? super T> observer;
        private Boolean isOpen;
        private List<T> storedPayloads = new ArrayList<>();

        public StoreAndForward(Observer<? super T> observer) {
            this.observer = observer;
        }

        public void setGate(boolean isOpen) {
            if (this.isOpen != null && this.isOpen == isOpen) {
                return;
            }
            this.isOpen = isOpen;
            if (isOpen) {
                final List<T> toForward = storedPayloads;
                storedPayloads = new ArrayList<>();
                for (T value : toForward) {
                    observer.onNext(value);
                }
            }
        }

        public void addValue(T value) {
            if (isOpen == null || !isOpen) {
                storedPayloads.add(value);
                return;
            }
            observer.onNext(value);
        }
    }
}
