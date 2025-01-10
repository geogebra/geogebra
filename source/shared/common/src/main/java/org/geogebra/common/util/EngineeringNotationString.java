package org.geogebra.common.util;

import java.util.function.Function;

import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Class that creates an engineering notation based on a numeric input<br/>
 * The engineering notation is similar to the scientific notation (m*10^n), with n
 * being restricted to multiples of three (3) only. <br/>
 */
public final class EngineeringNotationString {

	/**
	 * @param value Value
	 * @param stringType StringType
	 * @param formatBaseNumber Function used for formatting the base number
	 * @return The formatted engineering notation string
	 */
	public static String format(double value, StringType stringType,
			Function<Double, String> formatBaseNumber) {
		if (value == 0) {
			return formatEngineeringNotation("0", 0, stringType);
		}

		String sign = value < 0 ? "-" : "";
		double positiveValue = Math.abs(value);

		int exponent = (int) Math.floor(Math.log10(positiveValue));
		exponent = adjustExponent(exponent);

		double scaledValue = positiveValue / Math.pow(10, exponent);
		String baseNumber = sign + formatBaseNumber.apply(scaledValue);

		return formatEngineeringNotation(baseNumber, exponent, stringType);
	}

	private static int adjustExponent(int exponent) {
		int adjustment = exponent % 3;
		if (adjustment < 0) {
			adjustment += 3;
		}
		return exponent - adjustment;
	}

	private static String formatEngineeringNotation(String baseNumber, int exponent,
			StringType stringType) {
		if (stringType == StringType.LATEX) {
			return baseNumber + " \\cdot 10^{" + exponent + "}";
		}
		return baseNumber + " " + Unicode.CENTER_DOT + " 10" + getExponentString(exponent);
	}

	private static String getExponentString(int exponent) {
		StringBuilder exponentString = new StringBuilder();
		String exponentDigits = Integer.toString(exponent);
		for (char c : exponentDigits.toCharArray()) {
			if (c == '-') {
				exponentString.append(Unicode.SUPERSCRIPT_MINUS);
			} else {
				exponentString.append(Unicode.numberToSuperscript(c - '0'));
			}
		}
		return exponentString.toString();
	}
}