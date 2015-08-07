package com.rubyhuntersky.rxactivity;

/**
 * @author wehjin
 * @since 8/5/15
 */
public interface ScopedObserver<S, T> {

    void onNext(S scope, T value);
    void onCompleted(S scope);
    void onError(S scope, Throwable throwable);
}
