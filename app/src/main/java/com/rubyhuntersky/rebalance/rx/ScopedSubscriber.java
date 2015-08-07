package com.rubyhuntersky.rebalance.rx;

import rx.Subscription;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wehjin
 * @since 8/5/15
 */
public class ScopedSubscriber<A, T> implements ScopedObserver<A, T>, Subscription {

    private final ScopedObserver<A, ? super T> observer;
    private List<Subscription> subscriptions = new ArrayList<>();
    protected boolean isUnsubscribed = false;
    protected boolean isEnded = false;

    public ScopedSubscriber(ScopedObserver<A, ? super T> observer) {
        this.observer = observer;
    }

    void add(Subscription subscription) {
        subscriptions.add(subscription);
    }

    @Override
    public void unsubscribe() {
        if (isUnsubscribed) {
            return;
        }
        isUnsubscribed = true;
        final List<Subscription> todo = new ArrayList<>(subscriptions);
        subscriptions.clear();
        for (Subscription subscription : todo) {
            subscription.unsubscribe();
        }
    }

    @Override
    public boolean isUnsubscribed() {
        return isUnsubscribed;
    }

    @Override
    public void onNext(A scope, T value) {
        if (isUnsubscribed || isEnded) {
            return;
        }
        if (observer == null) {
            return;
        }
        observer.onNext(scope, value);
    }

    @Override
    public void onError(A scope, Throwable t) {
        if (isUnsubscribed || isEnded) {
            return;
        }
        isEnded = true;
        if (observer == null) {
            return;
        }
        observer.onError(scope, t);
    }

    @Override
    public void onCompleted(A scope) {
        if (isUnsubscribed || isEnded) {
            return;
        }
        isEnded = true;
        if (observer == null) {
            return;
        }
        observer.onCompleted(scope);
    }
}
