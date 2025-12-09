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

import java.util.function.Predicate;

import org.geogebra.common.util.debug.Log;

public class Validation {

	/**
	 * Validate input as double, update text field if invalid.
	 * @param tf
	 *            text field
	 * @param def
	 *            default
	 * @return parsed input if valid, default otherwise
	 */
	public static double validateDouble(TextObject tf, double def) {
		return validateDouble(tf, def, d -> true);
	}

	/**
	 * Validate input as double, update text field if invalid.
	 * @param tf
	 *            text field
	 * @param def
	 *            default
	 * @return parsed input if valid and positive, default otherwise
	 */
	public static double validateDoublePositive(TextObject tf, double def) {
		return validateDouble(tf, def, d -> d > 0);
	}

	private static double validateDouble(TextObject tf, double def, Predicate<Double> check) {
		double val = Double.NaN;
		try {
			val = Double.parseDouble(tf.getText());
		} catch (NumberFormatException e) {
			Log.debug("invalid number:" + tf.getText());
		}
		if (!Double.isNaN(val) && !Double.isInfinite(val)
				&& check.test(val)) {
			return val;
		}
		tf.setText(def + "");
		return def;
	}

}
