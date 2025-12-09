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
