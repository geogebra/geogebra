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

package org.geogebra.common.euclidian;

/**
 * Passes text to native screen reader.
 */
public interface ScreenReaderAdapter {

	/**
	 * Read text if possible.
	 * 
	 * @param text
	 *            text to be read
	 */
	void readText(String text);

	/**
	 * Read text if possible after a short delay
	 *
	 * @param text
	 *            text to be read
	 */
    void readDelayed(String text);

	/**
	 * Cancel currently scheduled read text action.
	 */
	void cancelReadDelayed();
}
