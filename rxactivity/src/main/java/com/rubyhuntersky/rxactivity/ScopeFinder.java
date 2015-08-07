package com.rubyhuntersky.rxactivity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wehjin
 * @since 8/6/15
 */
public class ScopeFinder<T> {

    private long nextId = 1;
    private Map<Long, T> scopes = new HashMap<>();
    private Map<Long, Integer> watchCounts = new HashMap<>();

    public long getNextId() {
        return nextId++;
    }

    public Updater<T> getUpdater(final long scopeId) {
        return new Updater<T>() {
            @Override
            public void update(T scope) {
                if (!scopes.containsKey(scopeId)) {
                    return;
                }
                scopes.put(scopeId, scope);
            }

            @Override
            public void clear() {
                scopes.remove(scopeId);
                watchCounts.remove(scopeId);
            }
        };
    }

    public Tracking<T> track(final long scopeId, T scope) {
        scopes.put(scopeId, scope);
        if (!watchCounts.containsKey(scopeId)) {
            watchCounts.put(scopeId, 0);
        }
        watchCounts.put(scopeId, watchCounts.get(scopeId) + 1);
        return new Tracking<T>() {

            @Override
            public T getLatest() {
                return scopes.get(scopeId);
            }

            @Override
            public T release() {
                final T latest = getLatest();
                if (watchCounts.containsKey(scopeId)) {
                    watchCounts.put(scopeId, watchCounts.get(scopeId) - 1);
                    if (watchCounts.get(scopeId) < 1) {
                        watchCounts.remove(scopeId);
                        scopes.remove(scopeId);
                    }
                }
                return latest;
            }
        };
    }

    public interface Updater<T> {

        void update(T scope);
        void clear();
    }

    public interface Tracking<T> {

        T getLatest();

        T release();
    }
}
