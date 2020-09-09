package org.geogebra.common.util.debug.crashlytics;

public interface CrashlyticsInterface {
    void log(String message);
    void recordException(Throwable exception);
}
