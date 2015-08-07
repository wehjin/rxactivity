package com.rubyhuntersky.rebalance.rx.lifter;

import com.rubyhuntersky.rebalance.rx.ScopedObservable;
import com.rubyhuntersky.rebalance.rx.ScopedObserver;
import com.rubyhuntersky.rebalance.rx.ScopedSubscriber;

import rx.functions.Func1;

/**
 * @author wehjin
 * @since 8/5/15
 */
public class MapLifter<A, U, T> implements ScopedObservable.Lifter<A, U, T> {

    private final Func1<T, U> mapper;

    public MapLifter(Func1<T, U> mapper) {
        this.mapper = mapper;
    }

    @Override
    public ScopedSubscriber<A, T> call(final ScopedSubscriber<A, U> lowerSubscriber) {
        return new ScopedSubscriber<>(new ScopedObserver<A, T>() {
            @Override
            public void onNext(A activity, T value) {
                lowerSubscriber.onNext(activity, mapper.call(value));
            }

            @Override
            public void onCompleted(A scope) {
                lowerSubscriber.onCompleted(scope);
            }

            @Override
            public void onError(A activity, Throwable throwable) {
                lowerSubscriber.onError(activity, throwable);
            }
        });
    }
}
