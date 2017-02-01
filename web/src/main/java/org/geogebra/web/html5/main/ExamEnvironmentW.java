package org.geogebra.web.html5.main;

import org.geogebra.common.main.App;
import org.geogebra.common.main.ExamEnvironment;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.debug.Log;

/**
 *
 */
public class ExamEnvironmentW extends ExamEnvironment {

    private App app;
    private boolean wasAirplaneModeOn, wasWifiEnabled, wasTaskLocked, wasBluetoothEnabled, wasScreenOn;

	/**
	 * @param app
	 *            application
	 */
    public ExamEnvironmentW(App app) {
        super();
        this.app = app;
    }

    @Override
	public void setStart(long time) {
        super.setStart(time);
        // airplane mode should be on when started
        wasAirplaneModeOn = true;
        // wifi should be disabled when started
        wasWifiEnabled = false;
        // bluetooth should be disabled when started
        wasBluetoothEnabled = false;
        // screen should be on when started
        wasScreenOn = true;
        // no cheat at start
        isCheating = false;

        if (app.getVersion().isAndroidWebview()) {
            setJavascriptTargetToExamEnvironment();
            exportGeoGebraAndroidMethods();
            if (checkLockTaskAvailable()) {
                // task should locked when started
                wasTaskLocked = true;
                watchTaskLock();
            }
            if (app.has(Feature.EXAM_ANDROID_CHECK_SCREEN_STATE)){
                watchScreenState();
            }
        }
    }

    private GTimer checkTaskLockTimer = null;

    private void watchTaskLock() {
        Log.debug("watch task lock");

        // set timer to check continuously if task is locked
        if (checkTaskLockTimer != null && checkTaskLockTimer.isRunning()) {
            checkTaskLockTimer.stop();
        }
        checkTaskLockTimer = app.newTimer(new GTimer.GTimerListener() {
            @Override
            public void onRun() {
                if (checkTaskLocked()) {
                    taskLocked();
                } else {
                    taskUnlocked();
                }
            }
        }, 1000);
        checkTaskLockTimer.startRepeat();
    }


    private GTimer checkScreenState = null;

    private void watchScreenState() {
        Log.debug("watch screen state");

        // set timer to check continuously screen stated
        if (checkScreenState != null && checkScreenState.isRunning()) {
            checkScreenState.stop();
        }
        checkScreenState = app.newTimer(new GTimer.GTimerListener() {
            @Override
            public void onRun() {
                if (isScreenOff()){
                    screenOff();
                } else {
                    screenOn();
                }
            }
        }, 1000);
        checkScreenState.startRepeat();
    }

	/**
	 * @return whether task locking API is available
	 */
    public static native boolean checkLockTaskAvailable() /*-{
		return $wnd.GeoGebraExamAndroidJsBinder.checkLockTaskAvailable();
	}-*/;

	/**
	 * @return whether task is locked
	 */
    public static native boolean checkTaskLocked() /*-{
		return $wnd.GeoGebraExamAndroidJsBinder.checkTaskLocked();
	}-*/;

	/**
	 * Lock task over API
	 */
    public static native void startLockTask() /*-{
		$wnd.GeoGebraExamAndroidJsBinder.startLockTask();
	}-*/;

	/**
	 * @return whether screen is inactive
	 */
    public static native boolean isScreenOff() /*-{
		return $wnd.GeoGebraExamAndroidJsBinder.isScreenOff();
	}-*/;


    @Override
	public void exit() {
        if (app.getVersion().isAndroidWebview()) {
            // stop timer if necessary
            if (checkTaskLockTimer != null && checkTaskLockTimer.isRunning()) {
                checkTaskLockTimer.stop();
            }
            setJavascriptTargetToNone();
        }
        super.exit();
    }

	/**
	 * @return set target to none
	 */
    public static native boolean setJavascriptTargetToNone() /*-{
		return $wnd.GeoGebraExamAndroidJsBinder.setJavascriptTargetToNone();
	}-*/;

    private static native boolean setJavascriptTargetToExamEnvironment() /*-{
		return $wnd.GeoGebraExamAndroidJsBinder
				.setJavascriptTargetToExamEnvironment();
	}-*/;

    private native void exportGeoGebraAndroidMethods() /*-{
		var that = this;
		$wnd.examEnvironment_airplaneModeTurnedOn = $entry(function() {
			that.@org.geogebra.web.html5.main.ExamEnvironmentW::airplaneModeTurnedOn()();
		});
		$wnd.examEnvironment_airplaneModeTurnedOff = $entry(function() {
			that.@org.geogebra.web.html5.main.ExamEnvironmentW::airplaneModeTurnedOff()();
		});
		$wnd.examEnvironment_wifiEnabled = $entry(function() {
			that.@org.geogebra.web.html5.main.ExamEnvironmentW::wifiEnabled()();
		});
		$wnd.examEnvironment_wifiDisabled = $entry(function() {
			that.@org.geogebra.web.html5.main.ExamEnvironmentW::wifiDisabled()();
		});
		$wnd.examEnvironment_bluetoothEnabled = $entry(function() {
			that.@org.geogebra.web.html5.main.ExamEnvironmentW::bluetoothEnabled()();
		});
		$wnd.examEnvironment_bluetoothDisabled = $entry(function() {
			that.@org.geogebra.web.html5.main.ExamEnvironmentW::bluetoothDisabled()();
		});

	}-*/;

    /**
     * this method is called through js (see exportGeoGebraAndroidMethods())
     */
    public void airplaneModeTurnedOff() {
        Log.debug("ExamEnvironmentW: airplane mode turned off");
        if (getStart() > 0) {
            initLists();
            if (wasAirplaneModeOn) {
                cheatingTimes.add(System.currentTimeMillis());
                cheatingEvents.add(CheatingEvent.AIRPLANE_MODE_OFF);
                wasAirplaneModeOn = false;
                Log.debug("STARTED CHEATING: airplane mode off");
            }
        }
    }

    /**
     * this method is called through js (see exportGeoGebraAndroidMethods())
     */
    public void airplaneModeTurnedOn() {
        Log.debug("ExamEnvironmentW: airplane mode turned on");
        if (getStart() > 0) {
            initLists();
            if (!wasAirplaneModeOn) {
                cheatingTimes.add(System.currentTimeMillis());
                cheatingEvents.add(CheatingEvent.AIRPLANE_MODE_ON);
                wasAirplaneModeOn = true;
                isCheating = true;
                Log.debug("STOPPED CHEATING: airplane mode on");
            }
        }
    }

    /**
     * this method is called through js (see exportGeoGebraAndroidMethods())
     */
    public void wifiEnabled() {
        Log.debug("ExamEnvironmentW: wifi enabled");
        if (getStart() > 0) {
            initLists();
            if (!wasWifiEnabled) {
                cheatingTimes.add(System.currentTimeMillis());
                cheatingEvents.add(CheatingEvent.WIFI_ENABLED);
                wasWifiEnabled = true;
                isCheating = true;
                Log.debug("STARTED CHEATING: wifi enabled");
            }
        }
    }

    /**
     * this method is called through js (see exportGeoGebraAndroidMethods())
     */
    public void wifiDisabled() {
        Log.debug("ExamEnvironmentW: wifi disabled");
        if (getStart() > 0) {
            initLists();
            if (wasWifiEnabled) {
                cheatingTimes.add(System.currentTimeMillis());
                cheatingEvents.add(CheatingEvent.WIFI_DISABLED);
                wasWifiEnabled = false;
                Log.debug("STOPPED CHEATING: wifi disabled");
            }
        }
    }


	/**
	 * Run this when unlocked task detected; notifies about cheating
	 */
    public void taskUnlocked() {
        if (getStart() > 0) {
            if (wasTaskLocked) {
                initLists();
                cheatingTimes.add(System.currentTimeMillis());
                cheatingEvents.add(CheatingEvent.TASK_UNLOCKED);
                wasTaskLocked = false;
                isCheating = true;
                Log.debug("STARTED CHEATING: task unlocked");
            }
        }
    }


	/**
	 * If task was previously unlocked, add cheating end to the log
	 */
    public void taskLocked() {
        if (getStart() > 0) {
            if (!wasTaskLocked) {
                initLists();
                cheatingTimes.add(System.currentTimeMillis());
                cheatingEvents.add(CheatingEvent.TASK_LOCKED);
                wasTaskLocked = true;
                Log.debug("STOPPED CHEATING: task locked");
            }
        }
    }


    /**
     * this method is called through js (see exportGeoGebraAndroidMethods())
     */
    public void bluetoothEnabled() {
        Log.debug("ExamEnvironmentW: bluetooth enabled");
        if (getStart() > 0) {
            initLists();
            if (!wasBluetoothEnabled) {
                cheatingTimes.add(System.currentTimeMillis());
                cheatingEvents.add(CheatingEvent.BLUETOOTH_ENABLED);
                wasBluetoothEnabled = true;
                isCheating = true;
                Log.debug("STARTED CHEATING: bluetooth enabled");
            }
        }
    }

    /**
     * this method is called through js (see exportGeoGebraAndroidMethods())
     */
    public void bluetoothDisabled() {
        Log.debug("ExamEnvironmentW: bluetooth disabled");
        if (getStart() > 0) {
            initLists();
            if (wasBluetoothEnabled) {
                cheatingTimes.add(System.currentTimeMillis());
                cheatingEvents.add(CheatingEvent.BLUETOOTH_DISABLED);
                wasBluetoothEnabled = false;
                Log.debug("STOPPED CHEATING: bluetooth disabled");
            }
        }
    }

    private boolean isCheating;

    @Override
	public boolean isCheating() {
        if (app.has(Feature.EXAM_ANDROID_CHECK_SCREEN_STATE) && app.getVersion().isAndroidWebview()) {
            return isCheating;
        }

        return super.isCheating();
    }


    @Override
	protected void addCheatingWindowsLeft(long time){
        if (app.getVersion().isAndroidWebview()) {
            if (isScreenOff()) {
                screenOff();
            }
        }
        super.addCheatingWindowsLeft(time);
    }

	/**
	 * Callback for screen going off
	 */
    public void screenOff() {
        if (getStart() > 0) {
            if (wasScreenOn) {
                initLists();
                long time = System.currentTimeMillis();
                cheatingTimes.add(time);
                cheatingEvents.add(CheatingEvent.SCREEN_OFF);
                wasScreenOn = false;
                Log.debug("screen: off");
            }
        }
    }


	/**
	 * Callback for screen going on
	 */
    public void screenOn() {
        if (getStart() > 0) {
            if (!wasScreenOn) {
                initLists();
                cheatingTimes.add(System.currentTimeMillis());
                cheatingEvents.add(CheatingEvent.SCREEN_ON);
                wasScreenOn = true;
                Log.debug("screen: on");
            }
        }
    }


}
