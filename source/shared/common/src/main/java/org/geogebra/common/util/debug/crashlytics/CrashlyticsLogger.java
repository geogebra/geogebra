/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
