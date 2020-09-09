package org.geogebra.common.util.debug.crashlytics;

public class CrashlyticsHelper {

    private static CrashlyticsInterface crashlytics;

    public static void setCrashlytics(CrashlyticsInterface crashlytics) {
        CrashlyticsHelper.crashlytics = crashlytics;
    }

    public static void log(String message) {
        if (crashlytics != null) {
            crashlytics.log(message);
        }
    }

    public static void recordException(Throwable exception) {
        if (crashlytics != null) {
            crashlytics.recordException(exception);
        }
    }
}
