package org.geogebra.common.util;

/**
 * Scientific number format (fixed number of significant digits).
 */
public interface ScientificFormatAdapter {

	/**
	 * @return number of significant digits
	 */
	int getSigDigits();

	/**
	 * @param sigDigits number of significant digits
	 */
	void setSigDigits(int sigDigits);

	/**
	 * @param mWidth maximum width
	 */
	void setMaxWidth(int mWidth);

	/**
	 * @param d number
	 * @return formatted number
	 */
	String format(double d);

}
