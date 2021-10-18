package org.geogebra.web.html5.util;

import org.geogebra.common.util.ScientificFormatAdapter;

/**
 * This code formats numbers in Scientific Notation. The input Number object is
 * returned as a ScientificFormated string. There are two output styles: Pure
 * and Standard scientific notation. Pure formatted numbers have precisely the
 * number of digits specified by the significant digits (sigDig) parameter and
 * always specify a Base 10 Exponential(E). Standard formated numbers have the
 * number of digits specified by the significant digits (sigDig) parameter but
 * will not have a Base 10 Exponential(E) if the number of digits in the
 * mantissa <= maxWidth.
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
	private MyNumberFormat decimalFormat;

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
		decimalFormat = null;
	}

	/**
	 * Sets the maximum allowable length of the formattted number mantissa
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
	 * and always specify a Base 10 Exponential(E). Standard formated numbers
	 * have the number of digits specified by the significant digits (sigDig)
	 * parameter but will not have a Base 10 Exponential(E) if the number of
	 * digits in the mantissa <= maxWidth.
	 * 
	 * @param sciNote
	 *            scientific notation flag
	 */
	public void setScientificNotationStyle(boolean sciNote) {
		this.sciNote = sciNote;
	}

	private static MyNumberFormat getDecimalFormat(int sigDig) {
		StringBuilder buffer = new StringBuilder("0.");
		for (int i = 1; i < sigDig; i++) {
			buffer.append('0');
		}
		buffer.append("E0");
		return new MyNumberFormat(buffer.toString());
	}

	/**
	 * Format the number using scientific notation
	 */
	@Override
	public String format(double d) {
		return format(d, sigDigit);
	}

	private String format(double d, int sigDig) {
		// Delegate the hard part to decimalFormat
		if (decimalFormat == null) {
			decimalFormat = getDecimalFormat(sigDigit);
		}
		MyNumberFormat format = (sigDig == sigDigit) ? decimalFormat
		        : getDecimalFormat(sigDig);

		String preliminaryResult = format.format(d);
		if (sciNote) {
			return preliminaryResult;
		}

		int ePos = preliminaryResult.indexOf('E');
		int exponent = Integer.parseInt(preliminaryResult.substring(ePos + 1)) + 1;
		if (exponent > maxWidth) {
			return preliminaryResult;
		}
		if (exponent < -maxWidth + sigDig + 1) {
			return preliminaryResult;
		}

		// We need to fix up the result

		int sign = preliminaryResult.charAt(0) == '-' ? 1 : 0;
		StringBuffer result = new StringBuffer(preliminaryResult.substring(
		        sign, sign + 1) + preliminaryResult.substring(sign + 2, ePos));

		if (exponent >= sigDig) {
			for (int i = sigDig; i < exponent; i++) {
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
	// /**
	// * Format a number plus error using scientific notation
	// */
	// public String formatError(double d,double dx)
	// {
	// return format(dx, resolveErrorSigDigit(d, dx));
	// }

}