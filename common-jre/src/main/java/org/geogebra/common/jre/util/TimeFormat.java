package org.geogebra.common.jre.util;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.geogebra.common.util.TimeFormatAdapter;

/**
 * Time interval format for Desktop and Android
 */
public class TimeFormat implements TimeFormatAdapter {

    @Override
    public String format(String localeStr, long timeIntervalMs) {
		return String.format(new Locale(localeStr), "%02d:%02d",
				TimeUnit.MILLISECONDS.toMinutes(timeIntervalMs),
				TimeUnit.MILLISECONDS.toSeconds(timeIntervalMs) - TimeUnit.MINUTES
						.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeIntervalMs))
        );
    }
}
