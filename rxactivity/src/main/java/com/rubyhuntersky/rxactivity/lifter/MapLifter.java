package com.rubyhuntersky.rxactivity.lifter;

import com.rubyhuntersky.rxactivity.ScopedObservable;
import com.rubyhuntersky.rxactivity.ScopedObserver;
import com.rubyhuntersky.rxactivity.ScopedSubscriber;

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
            public void onNext(A scope, T value) {
                lowerSubscriber.onNext(scope, mapper.call(value));
            }

            @Override
            public void onCompleted(A scope) {
                lowerSubscriber.onCompleted(scope);
            }

            @Override
            public void onError(A scope, Throwable throwable) {
                lowerSubscriber.onError(scope, throwable);
            }
        });
    }
}
