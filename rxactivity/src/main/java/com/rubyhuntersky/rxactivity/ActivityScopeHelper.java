package com.rubyhuntersky.rxactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

/**
 * @author wehjin
 * @since 8/6/15
 */
public class ActivityScopeHelper<A extends Activity> {

    private static final Func0<BehaviorSubject<Boolean>> NEW_BOOLEAN_SUBJECT = new Func0<BehaviorSubject<Boolean>>() {
        @Override
        public BehaviorSubject<Boolean> call() {
            return BehaviorSubject.create(false);
        }
    };
    private static final Func0<PublishSubject<ActivityResult>> NEW_ACTIVITY_RESULT_SUBJECT
            = new Func0<PublishSubject<ActivityResult>>() {
        @Override
        public PublishSubject<ActivityResult> call() {
            return PublishSubject.create();
        }
    };
    private static final ScopeFinder<Activity> SCOPES = new ScopeFinder<>();
    private static final Savings SAVINGS = new Savings();
    public static final String SCOPE_ID = "scope-id";
    public static final String ACTIVITY_RESULT_SUBJECT_ID = "activity-result-subject-id";
    public static final String IS_RESUMED_SUBJECT_ID = "is-resumed-subject-id";

    private final A activity;
    private long activityId;
    private ScopeFinder.Updater<Activity> scopeUpdater;
    private Savable<PublishSubject<ActivityResult>> activityResultStream;
    private Savable<BehaviorSubject<Boolean>> isResumedStream;

    public ActivityScopeHelper(A activity) {
        this.activity = activity;
    }

    public void onCreate(Bundle savedInstanceState) {
        activityId = savedInstanceState == null ? SCOPES.getNextId() : savedInstanceState.getLong(SCOPE_ID);
        scopeUpdater = SCOPES.getUpdater(activityId);
        scopeUpdater.update(activity);
        activityResultStream = restoreOrCreate(savedInstanceState, ACTIVITY_RESULT_SUBJECT_ID,
                                               NEW_ACTIVITY_RESULT_SUBJECT);
        isResumedStream = restoreOrCreate(savedInstanceState, IS_RESUMED_SUBJECT_ID, NEW_BOOLEAN_SUBJECT);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        activityResultStream.get().onNext(new ActivityResult(requestCode, resultCode, data));
    }

    public void onResume() {
        isResumedStream.get().onNext(true);
    }

    public void onPause() {
        isResumedStream.get().onNext(false);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(SCOPE_ID, activityId);
        save(outState, ACTIVITY_RESULT_SUBJECT_ID, activityResultStream);
        save(outState, IS_RESUMED_SUBJECT_ID, isResumedStream);
    }

    public void onDestroy() {
        if (activity.isFinishing()) {
            activityResultStream.get().onCompleted();
            isResumedStream.get().onCompleted();
            scopeUpdater.clear();
        } else {
            scopeUpdater.update(null);
        }
    }

    public ScopedObservable<A, ActivityResult> getActivityResult(final Intent intent, final int requestCode) {
        return observeWithActivity(Observable.create(new Observable.OnSubscribe<ActivityResult>() {
            @Override
            public void call(final Subscriber<? super ActivityResult> subscriber) {
                activity.startActivityForResult(intent, requestCode);
                subscriber.add(activityResultStream.get().filter(new Func1<ActivityResult, Boolean>() {
                    @Override
                    public Boolean call(ActivityResult activityResult) {
                        return activityResult.requestCode == requestCode;
                    }
                }).take(1).subscribe(subscriber));
            }
        }));
    }

    public <T> ScopedObservable<A, T> observeWithActivity(final Observable<T> observable) {
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

    @NonNull
    public <T> Observable.Transformer<T, T> observeWhileResumed() {
        return new GateTransformer<>(isResumedStream.get());
    }

    private <T> Savable<T> restoreOrCreate(Bundle bundle, String bundleKey, Func0<T> onCreate) {
        return SAVINGS.restoreOrCreate(bundle, bundleKey, onCreate);
    }

    private <T> void save(Bundle bundle, String bundleKey, Savable<T> savable) {
        bundle.putLong(bundleKey, savable.save());
    }

    protected <T> Savable<Subscription> subscribe(Observable<T> observable, Observer<T> observer) {
        return SAVINGS.toSavable(observable.subscribe(observer));
    }

    protected Savable<Subscription> resubscribe(long saveId) {
        return SAVINGS.restore(saveId);
    }
}
