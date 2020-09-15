package org.geogebra.common.util.debug.crashlytics;

/**
 * Helps using Crashlytics features in common.
 */
public class CrashlyticsLogger {

    private static CrashlyticsInterface crashlytics;

    public static void setCrashlytics(CrashlyticsInterface crashlytics) {
        CrashlyticsLogger.crashlytics = crashlytics;
    }

    /**
     * @param message log message
     */
    public static void log(String message) {
        if (crashlytics != null) {
            crashlytics.log(message);
        }
    }

    /**
     * @param exception exception
     */
    public static void recordException(Throwable exception) {
        if (crashlytics != null) {
            crashlytics.recordException(exception);
        }
    }
}
