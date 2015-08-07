package com.rubyhuntersky.rxactivity;

import android.app.Activity;
import android.os.Bundle;
import rx.Observable;

/**
 * @author wehjin
 * @since 8/5/15
 */
public class ObservableActivity extends Activity {

    private ActivityScopeHelper<ObservableActivity> scopeHelper = new ActivityScopeHelper<>(this);

    public <A extends ObservableActivity, T> ScopedObservable<A, T> observeWithActivity(
            final Observable<T> observable) {
        return scopeHelper.observeWithActivity(observable);
    }

    public <T> Observable.Transformer<T, T> observeWhileResumed() {
        return scopeHelper.observeWhileResumed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scopeHelper.onResume();
    }

    @Override
    protected void onPause() {
        scopeHelper.onPause();
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scopeHelper.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        scopeHelper.onDestroy();
        super.onDestroy();
    }
}
