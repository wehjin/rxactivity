package com.rubyhuntersky.rxactivity;

import org.junit.Before;
import org.junit.Test;

import rx.functions.Func1;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * @author wehjin
 * @since 8/8/15.
 * <p/>
 * TODO: flatMap, subscribe(Action2,Action2, Action1)
 */
public class ScopedObservableTest {

    public static final String SCOPE1 = "Hello";
    public static final String VALUE1 = "Jeff";
    public static final String VALUE2 = "Emily";
    private ScopedObservable<String, String> just1;
    private AssertingObserver observer1;

    @Before
    public void setUp() throws Exception {
        just1 = ScopedObservable.just(SCOPE1, VALUE1);
        observer1 = new AssertingObserver(SCOPE1, VALUE1);
    }

    @Test
    public void testMap() throws Exception {
        final ScopedObservable<String, String> map = just1.map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                return VALUE2;
            }
        });
        map.subscribe(observer1.withExpectedValue(VALUE2));
    }

    @Test
    public void testJust() throws Exception {
        just1.subscribe(observer1);
    }

    private static class AssertingObserver implements ScopedObserver<String, String> {

        private final String expectedScope;
        private final String expectedValue;

        public AssertingObserver(String expectedScope, String expectedValue) {
            this.expectedScope = expectedScope;
            this.expectedValue = expectedValue;
        }

        @Override
        public void onNext(String scope, String value) {
            assertEquals(expectedScope, scope);
            assertEquals(expectedValue, value);
        }

        @Override
        public void onCompleted(String scope) {
            assertEquals(expectedScope, scope);
        }

        @Override
        public void onError(String scope, Throwable throwable) {
            fail(throwable.getMessage());
        }

        public AssertingObserver withExpectedValue(String expectedValue) {
            return new AssertingObserver(expectedScope, expectedValue);
        }
    }
}