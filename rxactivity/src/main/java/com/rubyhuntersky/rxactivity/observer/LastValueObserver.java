package com.rubyhuntersky.rxactivity.observer;

import rx.Observer;

/**
 * @author wehjin
 * @since 8/8/15.
 */
public class LastValueObserver<T> implements Observer<T> {

    public T lastValue;
    public Throwable error;
    public boolean isCompleted;

    @Override
    public void onCompleted() {
        this.isCompleted = true;
    }

    @Override
    public void onError(Throwable e) {
        this.error = e;
    }

    @Override
    public void onNext(T value) {
        this.lastValue = value;
    }
}
