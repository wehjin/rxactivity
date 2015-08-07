package com.rubyhuntersky.rebalance.rx;

import com.rubyhuntersky.rebalance.rx.lifter.MapLifter;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func1;

/**
 * @author wehjin
 * @since 8/5/15
 */
public class ScopedObservable<S, T> {

    private final OnSubscribe<S, T> onSubscribe;

    public ScopedObservable(OnSubscribe<S, T> onSubscribe) {
        this.onSubscribe = onSubscribe;

        Observable.just("X");
    }

    public static <A, T> ScopedObservable<A, T> create(OnSubscribe<A, T> onSubscribe) {
        return new ScopedObservable<>(onSubscribe);
    }

    public <U> ScopedObservable<S, U> map(Func1<T, U> mapper) {
        return this.lift(new MapLifter<S, U, T>(mapper));
    }

    public <U> ScopedObservable<S, U> lift(final Lifter<S, U, T> lifter) {
        return create(new OnSubscribe<S, U>() {
            @Override
            public void call(ScopedSubscriber<S, U> lower) {
                lower.add(ScopedObservable.this.subscribe(lifter.call(lower)));
            }
        });
    }

    public Subscription subscribe(final ScopedObserver<S, ? super T> observer) {
        final ScopedSubscriber<S, T> subscriber = new ScopedSubscriber<>(observer);
        this.onSubscribe.call(subscriber);
        return subscriber;
    }

    public Subscription subscribe(final Action2<S, T> onNext, final Action2<S, Throwable> onError,
                                  final Action1<S> onCompleted) {
        return this.subscribe(new ScopedObserver<S, T>() {
            @Override
            public void onNext(S scope, T value) {
                if (onNext == null) {
                    return;
                }
                onNext(scope, value);
            }

            @Override
            public void onCompleted(S scope) {
                if (onCompleted == null) {
                    return;
                }
                onCompleted(scope);
            }

            @Override
            public void onError(S scoope, Throwable throwable) {
                if (onError == null) {
                    return;
                }
                onError(scoope, throwable);
            }
        });
    }

    public interface OnSubscribe<A, T> extends Action1<ScopedSubscriber<A, T>> {

    }

    public interface Lifter<A, U, T> extends Func1<ScopedSubscriber<A, U>, ScopedSubscriber<A, T>> {

    }

}