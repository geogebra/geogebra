package org.geogebra.web.html5.util;

import org.geogebra.common.util.ScientificFormatAdapter;

import elemental2.core.JsNumber;
import jsinterop.base.Js;

/**
 * This code formats numbers in Scientific Notation. The input Number object is
 * returned as a string in scientific format. There are two output styles: Pure
 * and Standard scientific notation. Pure formatted numbers have precisely the
 * number of digits specified by the significant digits (sigDig) parameter and
 * always specify a Base 10 Exponential(E). Standard formatted numbers have the
 * number of digits specified by the significant digits (sigDig) parameter but
 * will not have a Base 10 Exponential(E) if the number of digits in the
 * mantissa &lt;= maxWidth.
 *
 * @author Paul Spence
 * @author Mark Donszelmann
 * @version $Id: ScientificFormat.java,v 1.4 2009-06-22 02:18:22 hohenwarter Exp
 *          $
 */

public class ScientificFormat implements ScientificFormatAdapter {
	/**
	 * The number of significant digits the number is formatted to is recorded
	 * by sigDigit. The maximum width allowed for the returned String is
	 * recorded by MaxWidth
	 */
	private int sigDigit = 5;
	private int maxWidth = 8;
	private boolean sciNote = false;

	/**
	 * Default scientific format
	 */
	public ScientificFormat() {

	}

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
		setSigDigits(sigDigit);
		setMaxWidth(maxWidth);
		setScientificNotationStyle(sciNote);
	}

	/**
	 * Returns the number of significant digits
	 */
	@Override
	public int getSigDigits() {
		return sigDigit;
	}

	/**
	 * Sets the number of significant digits for the formatted number
	 */
	@Override
	public void setSigDigits(int sigDigit) {
		if (sigDigit < 1) {
			throw new IllegalArgumentException("sigDigit");
		}
		this.sigDigit = sigDigit;
	}

	/**
	 * Sets the maximum allowable length of the formatted number mantissa
	 * before exponential notation is used.
	 */
	@Override
	public void setMaxWidth(int mWidth) {
		if (mWidth < 3) {
			throw new IllegalArgumentException("maxWidth");
		}
		maxWidth = mWidth;
	}

	/**
	 * Sets the format style used. There are two output styles: Pure and
	 * Standard scientific notation. Pure formatted numbers have precisely the
	 * number of digits specified by the significant digits (sigDig) parameter
	 * and always specify a Base 10 Exponential(E). Standard formatted numbers
	 * have the number of digits specified by the significant digits (sigDig)
	 * parameter but will not have a Base 10 Exponential(E) if the number of
	 * digits in the mantissa &lt;= maxWidth.
	 * 
	 * @param sciNote
	 *            scientific notation flag
	 */
	public void setScientificNotationStyle(boolean sciNote) {
		this.sciNote = sciNote;
	}

	/**
	 * Format the number using scientific notation
	 */
	@Override
	public String format(double d) {
		// Delegate the hard part to toExponential; fractional digits = sig. digits - 1
		String preliminaryResult = toExponential(d, sigDigit - 1);
		return prettyPrint(preliminaryResult);
	}

	// visible for tests
	protected String prettyPrint(String preliminaryResult) {
		if (sciNote) {
			return preliminaryResult
					.replace('e', 'E').replace("+", "");
		}

		int ePos = preliminaryResult.indexOf('e');
		int exponent = Integer.parseInt(preliminaryResult.substring(ePos + 1)) + 1;
		if (exponent > maxWidth || exponent < -maxWidth + sigDigit + 1) {
			return preliminaryResult
					.replace('e', 'E').replace("+", "");
		}

		// We need to fix up the result

		int sign = preliminaryResult.charAt(0) == '-' ? 1 : 0;
		// remove the dot
		StringBuilder result = new StringBuilder(preliminaryResult.charAt(
				sign) + preliminaryResult.substring(sign + 2, ePos));

		if (exponent >= sigDigit) {
			for (int i = sigDigit; i < exponent; i++) {
				result.append('0');
			}
		} else if (exponent < 0) {
			result.insert(0, ".");
			for (int i = exponent; i < 0; i++) {
				result.insert(1, '0');
			}
		} else {
			result.insert(exponent, '.');
		}
		if (sign > 0) {
			result.insert(0, '-');
		}
		return result.toString();
	}

	private static String toExponential(double d, int fractionalDigits) {
		JsNumber num = Js.uncheckedCast(d);
		return num.toExponential(fractionalDigits);
	}

}