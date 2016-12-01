package org.geogebra.web.html5.main;

import org.geogebra.common.main.App;
import org.geogebra.common.main.ExamEnvironment;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.debug.Log;

/**
 *
 */
public class ExamEnvironmentW extends ExamEnvironment {

    private App app;
    private boolean wasAirplaneModeOn, wasWifiEnabled, wasTaskLocked;

    public ExamEnvironmentW(App app) {
        super();
        this.app = app;
    }

    public void setStart(long time) {
        super.setStart(time);
        // airplane mode should be on when started
        wasAirplaneModeOn = true;
        // wifi should be disabled when started
        wasWifiEnabled = false;
        if (app.getVersion().isAndroidWebview()) {
            setJavascriptTargetToExamEnvironment();
            exportGeoGebraAndroidMethods();
            if (checkLockTaskAvailable()) {
                // task should locked when started
                wasTaskLocked = true;
                watchTaskLock();
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

    public static native boolean checkLockTaskAvailable() /*-{
        return $wnd.GeoGebraExamAndroidJsBinder.checkLockTaskAvailable();
	}-*/;

    public static native boolean checkTaskLocked() /*-{
        return $wnd.GeoGebraExamAndroidJsBinder.checkTaskLocked();
	}-*/;

    public static native void startLockTask() /*-{
		$wnd.GeoGebraExamAndroidJsBinder.startLockTask();
	}-*/;


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

    public static native boolean setJavascriptTargetToNone() /*-{
        return $wnd.GeoGebraExamAndroidJsBinder.setJavascriptTargetToNone();
    }-*/;

    private static native boolean setJavascriptTargetToExamEnvironment() /*-{
        return $wnd.GeoGebraExamAndroidJsBinder.setJavascriptTargetToExamEnvironment();
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


    public void taskUnlocked() {
        if (getStart() > 0) {
            if (wasTaskLocked) {
                initLists();
                cheatingTimes.add(System.currentTimeMillis());
                cheatingEvents.add(CheatingEvent.TASK_UNLOCKED);
                wasTaskLocked = false;
                Log.debug("STARTED CHEATING: task unlocked");
            }
        }
    }


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

}
