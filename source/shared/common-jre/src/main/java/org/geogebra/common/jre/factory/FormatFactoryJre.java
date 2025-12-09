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

package org.geogebra.common.jre.factory;

import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.jre.util.NumberFormat;
import org.geogebra.common.jre.util.ScientificFormat;
import org.geogebra.common.jre.util.TimeFormat;
import org.geogebra.common.util.NumberFormatAdapter;
import org.geogebra.common.util.ScientificFormatAdapter;
import org.geogebra.common.util.TimeFormatAdapter;

/**
 * Format factory for Desktop and Android
 *
 */
public class FormatFactoryJre extends FormatFactory {

	@Override
	public ScientificFormatAdapter getScientificFormat(int sigDigit,
			int maxWidth, boolean sciNote) {
		return new ScientificFormat(sigDigit, maxWidth, sciNote);
	}

	@Override
	public NumberFormatAdapter getNumberFormat(int digits) {
		NumberFormat ret = new NumberFormat();
		ret.setMaximumFractionDigits(digits);
		ret.setGroupingUsed(false);
		return ret;
	}

	@Override
	public NumberFormatAdapter getNumberFormat(String s, int i) {
		return new NumberFormat(s, i);
	}

    @Override
    public TimeFormatAdapter getTimeFormat() {
        return new TimeFormat();
    }
}
