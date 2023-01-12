package org.geogebra.common.jre.util;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.geogebra.common.util.TimeFormatAdapter;

/**
 * Time interval format for Desktop and Android
 */
public class TimeFormat implements TimeFormatAdapter {

    @Override
    public String format(String localeStr, String pattern, long timeMs) {
		return String.format(new Locale(localeStr), pattern,
				TimeUnit.MILLISECONDS.toMinutes(timeMs),
				TimeUnit.MILLISECONDS.toSeconds(timeMs) - TimeUnit.MINUTES
						.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeMs))
        );
    }
}
