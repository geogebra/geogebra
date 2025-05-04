package org.geogebra.common.util;

/**
 * Number formatter with fixed number of decimal places.
 */
public interface NumberFormatAdapter {

	/**
	 * @return number of fractional digits
	 */
	int getMaximumFractionDigits();

	/**
	 * @param value value to format
	 * @return formatted value
	 */
	String format(double value);

}
