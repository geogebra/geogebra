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
        String secsS = String.valueOf(secs);
        if (secs < 10) {
            secsS = "0" + secsS;
        }
        return mins + ":" + secsS;
    }
}
