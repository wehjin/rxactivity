package com.rubyhuntersky.rxactivity;

import com.rubyhuntersky.rxactivity.lifter.MapLifter;
import com.rubyhuntersky.rxactivity.observer.ActionScopedObserver;

import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func1;

/**
 * @author wehjin
 * @since 8/5/15
 */
public class ScopedObservable<Scope, T> {

    private final OnSubscribe<Scope, T> onSubscribe;

    public ScopedObservable(OnSubscribe<Scope, T> onSubscribe) {
        this.onSubscribe = onSubscribe;
    }

    public static <Scope, T> ScopedObservable<Scope, T> just(final Scope scope, final T value) {
        return create(new OnSubscribe<Scope, T>() {
            @Override
            public void call(ScopedSubscriber<Scope, T> subscriber) {
                subscriber.onNext(scope, value);
                subscriber.onCompleted(scope);
            }
        });
    }

    public static <Scope, T> ScopedObservable<Scope, T> create(OnSubscribe<Scope, T> onSubscribe) {
        return new ScopedObservable<>(onSubscribe);
    }

    public <LowerT> ScopedObservable<Scope, LowerT> map(Func1<T, LowerT> mapper) {
        return this.lift(new MapLifter<Scope, LowerT, T>(mapper));
    }

    public <U> ScopedObservable<Scope, U> lift(final Lifter<Scope, U, T> lifter) {
        return create(new OnSubscribe<Scope, U>() {
            @Override
            public void call(ScopedSubscriber<Scope, U> lower) {
                lower.add(ScopedObservable.this.subscribe(lifter.call(lower)));
            }
        });
    }

    public Subscription subscribe(final ScopedObserver<Scope, ? super T> observer) {
        final ScopedSubscriber<Scope, T> subscriber = new ScopedSubscriber<>(observer);
        onSubscribe.call(subscriber);
        return subscriber;
    }

    public Subscription subscribe(final Action2<Scope, ? super T> onNext, final Action2<Scope, Throwable> onError,
          final Action1<Scope> onCompleted) {
        return subscribe(new ActionScopedObserver<>(onNext, onCompleted, onError));
    }


    public interface OnSubscribe<Scope, T> extends Action1<ScopedSubscriber<Scope, T>> {

    }

    public interface Lifter<Scope, U, T> extends Func1<ScopedSubscriber<Scope, U>, ScopedSubscriber<Scope, T>> {

    }

}