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

import org.geogebra.common.util.ScientificFormatAdapter;

/**
 * Implementation of scientific format adapter for the JRE.
 */
public class ScientificFormat extends ScientificFormatAdapter {

	private DecimalFormat decimalFormat;

	/**
	 * Sets the significant digits, maximum allowable width and number
	 * formatting style (SciNote == true for Pure formatting).
	 * 
	 * @param sigDigit
	 *            significant digits
	 * @param maxWidth
	 *            maximum width
	 * @param sciNote
	 *            whether to use scientific notation
	 */
	public ScientificFormat(int sigDigit, int maxWidth, boolean sciNote) {
		super(sciNote, maxWidth);
		setSigDigits(sigDigit);
	}

	/**
	 * Returns the number of significant digits
	 */
	@Override
	public int getSigDigits() {
		return sigDigits;
	}

	/**
	 * Sets the number of significant digits for the formatted number
	 */
	@Override
	public void setSigDigits(int sigDigit) {
		super.setSigDigits(sigDigit);
		decimalFormat = getDecimalFormat(this.sigDigits);
	}

	private static DecimalFormat getDecimalFormat(int sigDig) {
		String buffer = "0." + "0".repeat(Math.max(0, sigDig - 1))
				+ "E0";
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
		symbols.setNaN("NaN");
		DecimalFormat format = new DecimalFormat(buffer, symbols);
		format.setRoundingMode(RoundingMode.HALF_UP);
		return format;
	}

	/**
	 * Format the number using scientific notation
	 */
	@Override
	public String format(double d) {
		String preliminaryResult = decimalFormat.format(d);
		return prettyPrint(preliminaryResult);
	}

}
