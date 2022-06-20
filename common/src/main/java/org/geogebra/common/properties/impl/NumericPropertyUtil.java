package org.geogebra.common.properties.impl;

import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.util.NumberFormatAdapter;

import com.himamis.retex.editor.share.util.Unicode;

public class NumericPropertyUtil {

	private static final double EPS = 0.00001;

	private final AlgebraProcessor algebraProcessor;
	private final NumberFormatAdapter numberFormatter;

	/**
	 * @param algebraProcessor algebra processor
	 */
	public NumericPropertyUtil(AlgebraProcessor algebraProcessor) {
		this.algebraProcessor = algebraProcessor;
		this.numberFormatter = FormatFactory.getPrototype().getNumberFormat(2);
	}

	/**
	 * @param value text to be evaluated
	 * @return number result of the evaluation
	 */
	public GeoNumberValue parseInputString(String value) {
		String trimmedValue = value.trim();
		if ("".equals(trimmedValue)) {
			return null;
		}
		return algebraProcessor.evaluateToNumeric(trimmedValue, true);
	}

	/**
	 * @param number number to be formatted
	 * @return formatted number as text
	 */
	public String getFormatted(double number) {
		if (equals(number, 0)) {
			return numberFormatter.format(0);
		} else if (equals(number, Math.PI)) {
			return Unicode.PI_STRING;
		} else if (number % Math.PI == 0) {
			return numberFormatter.format(number / Math.PI) + Unicode.PI_STRING;
		} else if (equals(number, Math.PI / 2)) {
			return Unicode.PI_STRING + "/2";
		} else if (equals(number, Math.PI / 4)) {
			return Unicode.PI_STRING + "/4";
		} else {
			return numberFormatter.format(number);
		}
	}

	private boolean equals(double d1, double d2) {
		return Math.abs(d1 - d2) < EPS;
	}
}
