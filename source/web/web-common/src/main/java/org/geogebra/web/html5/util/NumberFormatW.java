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

import org.geogebra.common.util.NumberFormatAdapter;

import jsinterop.base.JsPropertyMap;

/**
 * @author gabor@geogebra.org
 *
 *         <p>
 *         GWT NumberFormat class wrapped in supertype
 *         </p>
 *
 */
public class NumberFormatW implements NumberFormatAdapter {

	private int maximumFractionDigits;
	private NumberFormat nf;

	/**
	 * @param digits
	 *            number of digits
	 */
	public NumberFormatW(String pattern, int digits) {
		maximumFractionDigits = digits;
		JsPropertyMap<Object> props = JsPropertyMap.of("maximumFractionDigits", digits);
		props.set("useGrouping", false);
		if (pattern != null && pattern.contains("E")) {
			props.set("notation", "scientific");
		}
		nf = new NumberFormat("en-US", props);
	}

	@Override
	public int getMaximumFractionDigits() {
		return maximumFractionDigits;
	}

	@Override
	public String format(double value) {
		return nf.format(value);

	}

}
