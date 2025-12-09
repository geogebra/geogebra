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

package org.geogebra.common.jre.util;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.geogebra.common.util.TimeFormatAdapter;

/**
 * Time interval format for Desktop and Android
 */
public class TimeFormat implements TimeFormatAdapter {

    @Override
    public String format(String languageTag, long timeIntervalMs) {
		return String.format(Locale.forLanguageTag(languageTag), "%02d:%02d",
				TimeUnit.MILLISECONDS.toMinutes(timeIntervalMs),
				TimeUnit.MILLISECONDS.toSeconds(timeIntervalMs) - TimeUnit.MINUTES
						.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeIntervalMs))
        );
    }
}
