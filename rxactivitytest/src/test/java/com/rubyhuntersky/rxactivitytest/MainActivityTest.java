package com.rubyhuntersky.rxactivitytest;

import com.rubyhuntersky.rxactivity.observer.LastValueObserver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author wehjin
 * @since 8/8/15.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 16)
public class MainActivityTest {

    public static final String HEY = "Hey";

    @Test
    public void testObserveWhileResumed_deliversValueAndCompleted() throws Exception {
        final MainActivity activity = Robolectric.setupActivity(MainActivity.class);
        final Observable<String> hey = Observable.just(HEY).lift(activity.<String>observeWhileResumed());
        final LastValueObserver<String> observer = new LastValueObserver<>();
        hey.subscribe(observer);
        assertTrue(observer.isCompleted);
        assertNull(observer.error);
        assertEquals(HEY, observer.lastValue);
    }
}