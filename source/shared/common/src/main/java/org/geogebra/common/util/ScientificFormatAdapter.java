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

/**
 * This code formats numbers in Scientific Notation. The input Number object is
 * returned as a string in a scientific format. There are two output styles: Pure
 * and Standard scientific notation. Pure formatted numbers have precisely the
 * number of digits specified by the significant digits (sigDig) parameter and
 * always specify a Base 10 Exponential(E). Standard formatted numbers have the
 * number of digits specified by the significant digits (sigDig) parameter but
 * will not have a Base 10 Exponential(E) if the number of digits in the
 * mantissa &lt;= maxWidth.
 * <p>
 * This class works as an adapter, wrapping a platform-specific number formatter.
 * </p>
 *
 * @author Paul Spence
 * @author Mark Donszelmann
 */
public abstract class ScientificFormatAdapter {
	/**
	 * The number of significant digits the number is formatted to is recorded
	 * by sigDigit. The maximum width allowed for the returned String is
	 * recorded by MaxWidth
	 */
	protected int sigDigits;
	protected int maxWidth;
	protected final boolean sciNote;

	protected ScientificFormatAdapter(boolean sciNote, int maxWidth) {
		this.sciNote = sciNote;
		setMaxWidth(maxWidth);
	}

	/**
	 * @return number of significant digits
	 */
	public int getSigDigits() {
		return sigDigits;
	}

	/**
	 * @param sigDigits number of significant digits
	 */
	public void setSigDigits(int sigDigits) {
		if (sigDigits < 1) {
			throw new IllegalArgumentException("sigDigits");
		}
		this.sigDigits = sigDigits;
	}

	/**
	 * @param mWidth maximum width
	 */
	public final void setMaxWidth(int mWidth) {
		if (mWidth < 3) {
			throw new IllegalArgumentException("maxWidth");
		}
		this.maxWidth = mWidth;
	}

	/**
	 * @param d number
	 * @return formatted number
	 */
	public abstract String format(double d);

	/**
	 * @param preliminaryResult string in scientific notation
	 * @return simplified string
	 */
	public String prettyPrint(String preliminaryResult) {
		if (sciNote) {
			return preliminaryResult;
		}

		int ePos = preliminaryResult.indexOf('E');
		if (ePos < 0) {
			return preliminaryResult;
		}
		int exponent = Integer.parseInt(preliminaryResult.substring(ePos + 1))
				+ 1;
		if (exponent > maxWidth || exponent < -maxWidth + sigDigits + 1) {
			return preliminaryResult;
		}

		// We need to fix up the result

		int sign = preliminaryResult.charAt(0) == '-' ? 1 : 0;
		// remove the dot
		StringBuilder result = new StringBuilder(preliminaryResult.length())
				.append(preliminaryResult.charAt(sign))
				.append(preliminaryResult, sign + 2, ePos);

		if (exponent >= sigDigits) {
			result.append("0".repeat(exponent - sigDigits));
		} else if (exponent <= 0) {
			result.insert(0, "0.");
			result.insert(2, "0".repeat(-exponent));
		} else {
			result.insert(exponent, ".");
		}
		if (sign > 0) {
			result.insert(0, '-');
		}
		return result.toString();
	}

}
