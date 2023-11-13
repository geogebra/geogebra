package org.geogebra.common.util;

/**
 * Default Time interval formatter, return the time as string eg: 12:02
 */
public class DefaultTimeFormat implements TimeFormatAdapter {

    @Override
    public String format(String languageTag, long timeIntervalMs) {
        int secs = (int) timeIntervalMs / 1000;
        int mins = secs / 60;
        secs -= mins * 60;
        String secsS = secs + "";
        if (secs < 10) {
            secsS = "0" + secsS;
        }
        return mins + ":" + secsS;
    }
}
