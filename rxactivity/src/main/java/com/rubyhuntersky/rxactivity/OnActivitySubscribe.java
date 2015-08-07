package com.rubyhuntersky.rxactivity;

import rx.functions.Action1;

/**
 * @author wehjin
 * @since 8/5/15
 */
public interface OnActivitySubscribe<A extends ObservableActivity, T> extends Action1<ScopedSubscriber<A, T>> {

}
