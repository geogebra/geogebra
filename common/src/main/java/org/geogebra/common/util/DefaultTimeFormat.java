package org.geogebra.common.util;

import java.util.Locale;

/**
 * Default Time interval formatter, return the time as string eg: 12:02
 */
public class DefaultTimeFormat implements TimeFormatAdapter {

    @Override
    public String format(Locale locale, String pattern, long timeMs) {
        int secs = (int) timeMs / 1000;
        int mins = secs / 60;
        secs -= mins * 60;
        String secsS = secs + "";
        if (secs < 10) {
            secsS = "0" + secsS;
        }
        return mins + ":" + secsS;
    }
}
