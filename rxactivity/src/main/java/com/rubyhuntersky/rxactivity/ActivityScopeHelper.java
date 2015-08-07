package com.rubyhuntersky.rxactivity;

import android.app.Activity;
import android.os.Bundle;

import rx.Observable;
import rx.Observer;

/**
 * @author wehjin
 * @since 8/6/15
 */
public class ActivityScopeHelper<A extends Activity> {

    private static final ScopeFinder<Activity> SCOPES = new ScopeFinder<>();
    private static final GateFactory GATES = new GateFactory();
    public static final String RESUMED_GATE = "resumed-gate";

    private final A activity;
    private long activityId;
    private ScopeFinder.Updater<Activity> scopeUpdater;
    private boolean isResumed;
    private GateFactory.Updater gatesUpdater;

    public ActivityScopeHelper(A activity) {
        this.activity = activity;
    }

    public <A extends Activity, T> ScopedObservable<A, T> observeWithActivity(final Observable<T> observable) {
        return ScopedObservable.create(new ScopedObservable.OnSubscribe<A, T>() {
            @Override
            public void call(final ScopedSubscriber<A, T> subscriber) {
                final ScopeFinder.Tracking<Activity> tracking = SCOPES.track(activityId, activity);
                subscriber.add(observable.subscribe(new Observer<T>() {
                    @Override
                    public void onCompleted() {
                        //noinspection unchecked
                        subscriber.onCompleted((A) tracking.release());
                    }

                    @Override
                    public void onError(Throwable e) {
                        //noinspection unchecked
                        subscriber.onError((A) tracking.release(), e);
                    }

                    @Override
                    public void onNext(T t) {
                        //noinspection unchecked
                        subscriber.onNext((A) tracking.getLatest(), t);
                    }
                }));
            }
        });
    }

    /**
     * Usage:  Observable.from("Hello").compose(activity.observeWhileResumed()).subscribe();
     */
    public <T> Observable.Transformer<T, T> observeWhileResumed() {
        return new GateTransformer<>(GATES.startGate(RESUMED_GATE, activityId, isResumed));
    }

    public void onResume() {
        this.isResumed = true;
        gatesUpdater.update(RESUMED_GATE, true);
    }

    public void onPause() {
        gatesUpdater.update(RESUMED_GATE, false);
        this.isResumed = false;
    }

    public void onCreate(Bundle savedInstanceState) {
        activityId = savedInstanceState == null ? SCOPES.getNextId() : savedInstanceState.getLong("scope-id");
        scopeUpdater = SCOPES.getUpdater(activityId);
        scopeUpdater.update(activity);
        gatesUpdater = GATES.getUpdater(activityId);
    }

    public void onDestroy() {
        if (activity.isFinishing()) {
            scopeUpdater.clear();
            gatesUpdater.clear(RESUMED_GATE);
        } else {
            scopeUpdater.update(null);
            gatesUpdater.update(RESUMED_GATE, false);
        }
    }
}
