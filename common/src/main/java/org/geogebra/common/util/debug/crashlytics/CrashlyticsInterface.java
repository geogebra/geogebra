package org.geogebra.common.util.debug.crashlytics;

/**
 * Crashlytics interface
 */
public interface CrashlyticsInterface {

    /**
     * @param message log message
     */
    void log(String message);

    /**
     * @param exception exception
     */
    void recordException(Throwable exception);
}
