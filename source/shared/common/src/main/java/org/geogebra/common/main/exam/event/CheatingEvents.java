package org.geogebra.common.main.exam.event;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Stores the cheating events.
 */
public class CheatingEvents {

    private final List<CheatingEvent> events;

    private boolean isScreenLocked = true;
    private boolean isOnWindow = true;
    private boolean isAirplaneModeEnabled = true;
    private boolean isWifiEnabled;
    private boolean isBluetoothEnabled;

    public Delegate delegate;

    public interface Delegate {
        void cheatingEventAdded(CheatingEvent cheatingEvent);
    }

    public CheatingEvents() {
        events = new LinkedList<>();
    }

    public List<CheatingEvent> getEvents() {
        return events;
    }

    public boolean isEmpty() {
        return events.isEmpty();
    }

    public int size() {
        return events.size();
    }

    /**
     * Adds a screen locked event.
     */
    public void addScreenLockedEvent() {
        if (!isScreenLocked) {
            addCheatingEvent(CheatingAction.TASK_LOCKED);
            isScreenLocked = true;
        }
    }

    /**
     * Adds a screen unlocked event.
     */
    public void addScreenUnlockedEvent() {
        if (isScreenLocked) {
            addCheatingEvent(CheatingAction.TASK_UNLOCKED);
            isScreenLocked = false;
        }
    }

    /**
     * Adds a window entered event.
     */
    public void addWindowEnteredEvent() {
        if (!isOnWindow) {
            addCheatingEvent(CheatingAction.WINDOW_ENTERED);
            isOnWindow = true;
        }
    }

    /**
     * Adds a window left event.
     */
    public void addWindowLeftEvent() {
        if (isOnWindow) {
            addCheatingEvent(CheatingAction.WINDOW_LEFT);
            isOnWindow = false;
        }
    }

    /**
     * Adds a WiFi enabled event.
     */
    public void addWifiEnabledEvent() {
        if (!isWifiEnabled) {
            addCheatingEvent(CheatingAction.WIFI_ENABLED);
            isWifiEnabled = true;
        }
    }

    /**
     * Adds a WiFi disabled event.
     */
    public void addWifiDisabledEvent() {
        if (isWifiEnabled) {
            addCheatingEvent(CheatingAction.WIFI_DISABLED);
            isWifiEnabled = false;
        }
    }

    /**
     * Adds an airplane mode enabled event.
     */
    public void addAirplaneModeEnabledEvent() {
        if (!isAirplaneModeEnabled) {
            addCheatingEvent(CheatingAction.AIRPLANE_MODE_ON);
            isAirplaneModeEnabled = true;
        }
    }

    /**
     * Adds an airplane mode disabled event.
     */
    public void addAirplaneModeDisabledEvent() {
        if (isAirplaneModeEnabled) {
            addCheatingEvent(CheatingAction.AIRPLANE_MODE_OFF);
            isAirplaneModeEnabled = false;
        }
    }

    /**
     * Adds a bluetooth enabled event.
     */
    public void addBluetoothEnabledEvent() {
        if (!isBluetoothEnabled) {
            addCheatingEvent(CheatingAction.BLUETOOTH_ENABLED);
            isBluetoothEnabled = true;
        }
    }

    /**
     * Adds a bluetooth disabled event.
     */
    public void addBluetoothDisabledEvent() {
        if (isBluetoothEnabled) {
            addCheatingEvent(CheatingAction.BLUETOOTH_DISABLED);
            isBluetoothEnabled = false;
        }
    }

    private void addCheatingEvent(CheatingAction action) {
        CheatingEvent cheatingEvent = new CheatingEvent(action, new Date());
        events.add(cheatingEvent);
        if (delegate != null) {
            delegate.cheatingEventAdded(cheatingEvent);
        }
    }
}
