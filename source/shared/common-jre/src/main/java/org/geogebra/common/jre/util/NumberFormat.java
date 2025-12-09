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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.geogebra.common.util.NumberFormatAdapter;

/** Decimal format for Desktop and android */
public class NumberFormat extends DecimalFormat implements NumberFormatAdapter {

	private static final long serialVersionUID = 1L;

	/** default format */
	public NumberFormat() {
		super();
		setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
		setRoundingMode(RoundingMode.HALF_UP);
	}

	/**
	 * Format with a pattern
	 * 
	 * @param pattern
	 *            format pattern
	 * @param maxDigits
	 *            maximumDigits
	 * */
	public NumberFormat(String pattern, int maxDigits) {
		super(pattern);
		setMaximumFractionDigits(maxDigits);
		setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
	}

}
