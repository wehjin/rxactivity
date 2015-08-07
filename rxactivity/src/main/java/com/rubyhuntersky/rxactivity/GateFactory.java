package com.rubyhuntersky.rxactivity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wehjin
 * @since 8/6/15
 */
public class GateFactory {

    public Map<Long, Map<String, Gate>> byOwner = new HashMap<>();

    public Gate startGate(String type, long ownerId, boolean defaultOpen) {
        Map<String, Gate> byType = byOwner.get(ownerId);
        if (byType == null) {
            byType = new HashMap<>();
            byOwner.put(ownerId, byType);
        }
        Gate gate = byType.get(type);
        if (gate == null) {
            gate = new Gate(defaultOpen);
            byType.put(type, gate);
        }
        return gate;
    }

    public Updater getUpdater(final long ownerId) {
        return new Updater() {
            public void update(String type, boolean isOpen) {
                final Map<String, Gate> byType = byOwner.get(ownerId);
                if (byType == null) {
                    return;
                }
                final Gate gate = byType.get(type);
                if (gate == null) {
                    return;
                }
                if (isOpen) {
                    gate.open();
                } else {
                    gate.close();
                }
            }

            @Override
            public void clear(String type) {
                final Map<String, Gate> byType = byOwner.get(ownerId);
                if (byType == null) {
                    return;
                }
                final Gate removed = byType.remove(type);
                if (removed == null) {
                    return;
                }
                removed.lock();
            }
        };
    }

    public interface Updater {

        void update(String type, boolean isOpen);
        void clear(String type);
    }
}
