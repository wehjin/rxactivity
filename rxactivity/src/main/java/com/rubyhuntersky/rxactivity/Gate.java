package com.rubyhuntersky.rxactivity;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * @author wehjin
 * @since 8/6/15
 */
public class Gate {

    private BehaviorSubject<Boolean> isOpen;
    private boolean isLocked;

    public Gate(boolean isOpen) {
        this.isOpen = BehaviorSubject.create(isOpen);
    }

    public Observable<Boolean> isOpen() {
        return this.isOpen.distinctUntilChanged();
    }

    public void open() {
        this.isOpen.onNext(true);
    }

    public void close() {
        this.isOpen.onNext(false);
    }

    public void lock() {
        this.isLocked = true;
        this.isOpen.onCompleted();
        this.isOpen = null;
    }
}
