package com.rubyhuntersky.rxactivity;

/**
 * @author wehjin
 * @since 8/7/15
 */
public interface Savable<T> {

    long save();
    T get();
}
