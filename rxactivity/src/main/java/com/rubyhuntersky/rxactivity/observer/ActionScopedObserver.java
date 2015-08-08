package com.rubyhuntersky.rxactivity.observer;

import com.rubyhuntersky.rxactivity.ScopedObserver;

import rx.functions.Action1;
import rx.functions.Action2;

/**
 * @author wehjin
 * @since 8/8/15.
 */
public class ActionScopedObserver<Scope, T> implements ScopedObserver<Scope, T> {
    private final Action2<Scope, ? super T> onNext;
    private final Action1<Scope> onCompleted;
    private final Action2<Scope, Throwable> onError;

    public ActionScopedObserver(Action2<Scope, ? super T> onNext, Action1<Scope> onCompleted,
          Action2<Scope, Throwable> onError) {
        this.onNext = onNext;
        this.onCompleted = onCompleted;
        this.onError = onError;
    }

    @Override
    public void onNext(Scope scope, T value) {
        if (onNext == null) {
            return;
        }
        onNext.call(scope, value);
    }

    @Override
    public void onCompleted(Scope scope) {
        if (onCompleted == null) {
            return;
        }
        onCompleted.call(scope);
    }

    @Override
    public void onError(Scope scope, Throwable throwable) {
        if (onError == null) {
            throw new RuntimeException(throwable);
        }
        onError.call(scope, throwable);
    }
}
