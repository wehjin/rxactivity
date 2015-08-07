package com.rubyhuntersky.rebalance.rx;

import android.util.Pair;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wehjin
 * @since 8/6/15
 */
public class GateTransformer<T> implements Observable.Transformer<T, T> {

    private final Gate gate;

    public GateTransformer(Gate gate) {
        this.gate = gate;
    }

    @Override
    public Observable<T> call(Observable<T> upperObservable) {
        return Observable.combineLatest(gate.isOpen(), upperObservable, new Func2<Boolean, T, Pair<Boolean, T>>() {
            @Override
            public Pair<Boolean, T> call(Boolean isOpen, T payload) {
                return new Pair<>(isOpen, payload);
            }
        }).flatMap(new Func1<Pair<Boolean, T>, Observable<T>>() {

            private Boolean wasOpen;
            private T previousPayload;
            private List<T> storedPayloads = new ArrayList<T>();

            @Override
            public Observable<T> call(Pair<Boolean, T> pair) {
                final Boolean isOpen = pair.first;
                final T payload = pair.second;
                if (wasOpen == null) {
                    wasOpen = isOpen;
                    previousPayload = payload;
                    if (isOpen) {
                        return Observable.just(payload);
                    } else {
                        storedPayloads.add(payload);
                        return Observable.empty();
                    }
                } else if (isOpen != wasOpen) {
                    wasOpen = isOpen;
                    if (isOpen) {
                        // Newly open.
                        if (storedPayloads.isEmpty()) {
                            return Observable.empty();
                        } else {
                            final Observable<T> next = Observable.from(storedPayloads);
                            storedPayloads = new ArrayList<T>();
                            return next;
                        }
                    } else {
                        // Newly closed.
                        return Observable.empty();
                    }
                } else if (payload != previousPayload) {
                    previousPayload = payload;
                    if (isOpen) {
                        return Observable.just(payload);
                    } else {
                        storedPayloads.add(payload);
                        return Observable.empty();
                    }
                } else {
                    return Observable.empty();
                }
            }
        });
    }
}
