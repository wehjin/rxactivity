package com.rubyhuntersky.rxactivity;

import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import rx.functions.Func0;

/**
 * @author wehjin
 * @since 8/7/15
 */
public class Savings {

    private static final Random RANDOM = new Random();
    private final Map<Long, Object> values = new HashMap<>();

    public <T> Savable<T> toSavable(final T t) {
        return getSavable(t);
    }

    public <T> Savable<T> restore(long restoreId) {
        final Object value = values.get(restoreId);
        values.remove(restoreId);
        //noinspection unchecked
        return value == null ? null : getSavable((T) value);
    }

    public <T> Savable<T> restoreOrCreate(Bundle bundle, String bundleKey, Func0<T> onCreate) {
        return bundle == null ? getSavable(onCreate.call()) : this.<T>restore(bundle.getLong(bundleKey));
    }

    @NonNull
    private <T> Savable<T> getSavable(final T value) {
        return new Savable<T>() {

            long restoreId = 0;

            private void initRestoreId() {
                if (restoreId != 0) {
                    return;
                }

                while (restoreId == 0) {
                    restoreId = RANDOM.nextLong();
                }
            }

            @Override
            public long save() {
                initRestoreId();
                values.put(restoreId, value);
                return restoreId;
            }

            @Override
            public T get() {
                return value;
            }
        };
    }
}
