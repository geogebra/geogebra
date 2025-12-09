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

package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.kernel.matrix.Coords;

/**
 * @author mathieu
 *
 */
public interface GeoPolyhedronInterface extends GeoElementND {
	/**
	 * Sets the point size (and/or visibility)
	 * 
	 * @param size
	 *            new point size
	 */
	public void setPointSizeOrVisibility(int size);

	/**
	 * calc pseudo centroid coords (based on segments average)
	 * 
	 * @param coords
	 *            output coords
	 */
	public void pseudoCentroid(Coords coords);
}
