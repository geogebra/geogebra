package org.geogebra.common.util;

/**
 * Time format adapter.
 */
public interface TimeFormatAdapter {

    /**
     * @param localeStr locale string (e.g. en_US)
     * @param timeIntervalMs length of time interval in ms
     * @return time interval description in local format
     */
    String format(String localeStr, long timeIntervalMs);

}
