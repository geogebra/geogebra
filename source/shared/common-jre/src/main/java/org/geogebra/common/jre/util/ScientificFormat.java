package org.geogebra.common.jre.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Locale;

import org.geogebra.common.util.ScientificFormatAdapter;

/**
 * This code formats numbers in Scientific Notation. The input Number object is
 * returned as a string in a scientific format. There are two output styles: Pure
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

public class ScientificFormat extends Format
		implements ScientificFormatAdapter {
	/**
	 * The number of significant digits the number is formatted to is recorded
	 * by sigDigit. The maximum width allowed for the returned String is
	 * recorded by MaxWidth
	 */
	private int sigDigit = 5;
	private int maxWidth = 8;
	private boolean sciNote = false; // set to true for pure Scientific Notation
	private DecimalFormat decimalFormat;
	private static final long serialVersionUID = -1182686857248711235L;

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
		setSigDigits(sigDigit);
		setMaxWidth(maxWidth);
		setScientificNotationStyle(sciNote);
	}

	/**
	 * Implementation of inherited abstract method. Checks to see if object to
	 * be formatted is of type Number. If so casts the Number object to double
	 * and calls the format method. Returns the result.
	 */
	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo,
			FieldPosition pos) {
		if (obj instanceof Number) {
			String result = format(((Number) obj).doubleValue());
			return toAppendTo.append(result);
		}
		throw new IllegalArgumentException(
				"Cannot format given Object as a Number");
	}

	/**
	 * Dummy implementation of inherited abstract method.
	 */
	@Override
	public Object parseObject(String source, ParsePosition pos) {
		return null;
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
	public void setSigDigits(int SigDigit) {
		if (SigDigit < 1) {
			throw new IllegalArgumentException("sigDigit");
		}
		sigDigit = SigDigit;
		decimalFormat = null;
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
	protected void setScientificNotationStyle(boolean sciNote) {
		this.sciNote = sciNote;
	}

	private static DecimalFormat getDecimalFormat(int sigDig) {
		StringBuffer buffer = new StringBuffer("0.");
		for (int i = 1; i < sigDig; i++) {
			buffer.append('0');
		}
		buffer.append("E0");
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
		symbols.setNaN("NaN");
		return new DecimalFormat(buffer.toString(), symbols);
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
		DecimalFormat format = (sigDig == sigDigit) ? decimalFormat
				: getDecimalFormat(sigDig);

		String preliminaryResult = format.format(d);
		if (sciNote) {
			return preliminaryResult;
		}

		int ePos = preliminaryResult.indexOf('E');
		if (ePos < 0) {
			return preliminaryResult;
		}
		int exponent = Integer.parseInt(preliminaryResult.substring(ePos + 1))
				+ 1;
		if (exponent > maxWidth || exponent < -maxWidth + sigDig + 1) {
			return preliminaryResult;
		}

		// We need to fix up the result

		int sign = preliminaryResult.charAt(0) == '-' ? 1 : 0;
		StringBuffer result = new StringBuffer(
				preliminaryResult.substring(sign, sign + 1)
						+ preliminaryResult.substring(sign + 2, ePos));

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