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

package org.geogebra.common.awt;

/**
 * Path iterator.
 */
public interface GPathIterator {

	int WIND_EVEN_ODD = 0;
	int WIND_NON_ZERO = 1;

	int SEG_MOVETO = 0;
	int SEG_LINETO = 1;
	int SEG_QUADTO = 2;
	int SEG_CUBICTO = 3;
	int SEG_CLOSE = 4;

	/**
	 * @return the winding rule
	 */
	int getWindingRule();

	/**
	 * @return true iff there are no segments left
	 */
	boolean isDone();

	/**
	 * Go to next segment.
	 */
	void next();

	// public int currentSegment(float[] coords);

	/**
	 * @param coords output array for coordinates
	 * @return segment type
	 */
	int currentSegment(double[] coords);

}
