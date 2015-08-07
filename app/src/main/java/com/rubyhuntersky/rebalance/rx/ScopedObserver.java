package com.rubyhuntersky.rebalance.rx;

/**
 * @author wehjin
 * @since 8/5/15
 */
public interface ScopedObserver<S, T> {

    void onNext(S activity, T value);
    void onCompleted(S scope);
    void onError(S activity, Throwable throwable);
}
