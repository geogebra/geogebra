package org.geogebra.common.main.exam.event;

import java.util.LinkedList;
import java.util.List;

public class CheatingEvents {

    private List<CheatingEvent> events;

    private boolean isScreenOn = true;
    private boolean isScreenLocked = true;
    private boolean isOnWindow = true;
    private boolean isAirplaneModeEnabled = true;
    private boolean isWifiEnabled;
    private boolean isBluetoothEnabled;

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

    public void addScreenOnEvent() {
        if (!isScreenOn) {
            addCheatingEvent(CheatingAction.SCREEN_ON);
            isScreenOn = true;
        }
    }

    public void addScreenOffEvent() {
        if (isScreenOn) {
            addCheatingEvent(CheatingAction.SCREEN_OFF);
            isScreenOn = false;
        }
    }

    public void addScreenLockedEvent() {
        if (!isScreenLocked) {
            addCheatingEvent(CheatingAction.TASK_LOCKED);
            isScreenLocked = true;
        }
    }

    public void addScreenUnlockedEvent() {
        if (isScreenLocked) {
            addCheatingEvent(CheatingAction.TASK_UNLOCKED);
            isScreenLocked = false;
        }
    }

    public void addWindowEnteredEvent() {
        if (!isOnWindow) {
            addCheatingEvent(CheatingAction.WINDOW_ENTERED);
            isOnWindow = true;
        }
    }

    public void addWindowLeftEvent() {
        if (isOnWindow) {
            addCheatingEvent(CheatingAction.WINDOW_LEFT);
            isOnWindow = false;
        }
    }

    public void addWifiEnabledEvent() {
        if (!isWifiEnabled) {
            addCheatingEvent(CheatingAction.WIFI_ENABLED);
            isWifiEnabled = true;
        }
    }

    public void addWifiDisabledEvent() {
        if (isWifiEnabled) {
            addCheatingEvent(CheatingAction.WIFI_DISABLED);
            isWifiEnabled = false;
        }
    }

    public void addAirplaneModeEnabledEvent() {
        if (!isAirplaneModeEnabled) {
            addCheatingEvent(CheatingAction.AIRPLANE_MODE_ON);
            isAirplaneModeEnabled = true;
        }
    }

    public void addAirplaneModeDisabledEvent() {
        if (isAirplaneModeEnabled) {
            addCheatingEvent(CheatingAction.AIRPLANE_MODE_OFF);
            isAirplaneModeEnabled = false;
        }
    }

    public void addBluetoothEnabledEvent() {
        if (!isBluetoothEnabled) {
            addCheatingEvent(CheatingAction.BLUETOOTH_ENABLED);
            isBluetoothEnabled = true;
        }
    }

    public void addBluetoothDisabledEvent() {
        if (isBluetoothEnabled) {
            addCheatingEvent(CheatingAction.BLUETOOTH_DISABLED);
            isBluetoothEnabled = false;
        }
    }

    private void addCheatingEvent(CheatingAction action) {
        events.add(new CheatingEvent(action, System.currentTimeMillis()));
    }
}
