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

package org.geogebra.web.html5.util;

import org.geogebra.common.util.ScientificFormatAdapter;

import jsinterop.base.JsPropertyMap;

/**
 * Implementation of ScientificFormatAdapter for GWT.
 */
public class ScientificFormat extends ScientificFormatAdapter {

	private NumberFormat numberFormat;

	/**
	 * Sets the significant digits, maximum allowable width and number
	 * formatting style (SciNote == true for Pure formatting).
	 * 
	 * @param sigDigit
	 *            significant digits
	 * @param maxWidth
	 *            max width
	 * @param sciNote
	 *            whether to use scientific notation
	 */
	public ScientificFormat(int sigDigit, int maxWidth, boolean sciNote) {
		super(sciNote, maxWidth);
		setSigDigits(sigDigit);
	}

	/**
	 * Sets the number of significant digits for the formatted number
	 */
	@Override
	public void setSigDigits(int sigDigit) {
		super.setSigDigits(sigDigit);
		// fractional digits = sig. digits - 1 (same in JRE alternative)
		JsPropertyMap<Object> props = JsPropertyMap.of("maximumFractionDigits", sigDigit - 1,
				"minimumFractionDigits", sigDigit - 1,
				"roundingMode", "halfExpand");
		props.set("notation", "scientific");
		numberFormat = new NumberFormat("en-US", props);
	}

	/**
	 * Format the number using scientific notation
	 */
	@Override
	public String format(double d) {
		// Delegate the hard part to Intl.NumberFormat
		String preliminaryResult = numberFormat.format(d);
		return prettyPrint(preliminaryResult);
	}

}
