package com.rubyhuntersky.rxactivitytest;

import com.rubyhuntersky.rxactivity.observer.LastValueObserver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
    private Observable<String> hey;

    @Before
    public void setUp() throws Exception {
        hey = Observable.just(HEY);
    }

    @Test
    public void testObserveWhileResumed_deliversValueAndCompletedWhileResumed() throws Exception {
        final MainActivity activity = Robolectric.setupActivity(MainActivity.class);
        final Observable<String> heyWhileResumed = hey.lift(activity.<String>observeWhileResumed());
        final LastValueObserver<String> lastValue = new LastValueObserver<>();
        heyWhileResumed.subscribe(lastValue);
        assertTrue(lastValue.isCompleted);
        assertNull(lastValue.error);
        assertEquals(HEY, lastValue.lastValue);
    }

    @Test
    public void testObserveWhileResumed_delaysDeliveryWhiledStarted() throws Exception {
        ActivityController<MainActivity> controller = Robolectric.buildActivity(MainActivity.class).create().start();
        MainActivity activity = controller.get();
        final Observable<String> heyWhileResumed = hey.lift(activity.<String>observeWhileResumed());
        final LastValueObserver<String> lastValue = new LastValueObserver<>();

        heyWhileResumed.subscribe(lastValue);
        assertFalse(lastValue.isCompleted);
        assertNull(lastValue.error);
        assertNull(lastValue.lastValue);

        controller.resume();
        assertTrue(lastValue.isCompleted);
        assertNull(lastValue.error);
        assertEquals(HEY, lastValue.lastValue);
    }

    @Test
    public void testObserveWhileResumed_delaysDeliveryWhilePaused() throws Exception {
        ActivityController<MainActivity> controller = Robolectric.buildActivity(MainActivity.class)
                                                                 .create()
                                                                 .start()
                                                                 .resume()
                                                                 .pause();
        MainActivity activity = controller.get();
        final Observable<String> heyWhileResumed = hey.lift(activity.<String>observeWhileResumed());
        final LastValueObserver<String> lastValue = new LastValueObserver<>();

        heyWhileResumed.subscribe(lastValue);
        assertFalse(lastValue.isCompleted);
        assertNull(lastValue.error);
        assertNull(lastValue.lastValue);

        controller.resume();
        assertTrue(lastValue.isCompleted);
        assertNull(lastValue.error);
        assertEquals(HEY, lastValue.lastValue);
    }
}