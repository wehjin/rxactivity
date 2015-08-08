package com.rubyhuntersky.rxactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import rx.Observable;

/**
 * @author wehjin
 * @since 8/5/15
 */
@SuppressWarnings("rawtypes")
abstract public class ObservableActivity<A extends ObservableActivity> extends Activity {

    @SuppressWarnings("unchecked")
    private ActivityScopeHelper<A> scopeHelper = new ActivityScopeHelper<>((A) this);

    public <T> ScopedObservable<A, T> observeWithActivity(final Observable<T> observable) {
        return scopeHelper.observeWithActivity(observable);
    }

    public <T> Observable.Operator<T, T> observeWhileResumed() {
        return scopeHelper.observeWhileResumed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scopeHelper.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        scopeHelper.onActivityResult(requestCode, resultCode, data);
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        scopeHelper.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        scopeHelper.onDestroy();
        super.onDestroy();
    }
}
