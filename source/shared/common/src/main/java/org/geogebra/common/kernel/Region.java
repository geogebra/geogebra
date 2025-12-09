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

package org.geogebra.common.kernel;

import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * @author Mathieu Blossier
 * 
 */

public interface Region extends GeoElementND {

	/**
	 * Sets coords of P when the coords of P have changed. Afterwards P lies in
	 * this region.
	 * 
	 * @param P
	 *            point
	 * 
	 */
	public void pointChangedForRegion(GeoPointND P);

	/**
	 * Sets coords of P when this region has changed. Afterwards P lies in this
	 * region.
	 * 
	 * @param P
	 *            point
	 * 
	 *
	 */
	public void regionChanged(GeoPointND P);

	/**
	 * Returns true if the given point lies inside this Region.
	 * 
	 * @param P
	 *            point
	 * @return true if the given point lies inside this Region.
	 */
	public boolean isInRegion(GeoPointND P);

	/**
	 * Per default, this method will just call {@link #isInRegion(GeoPointND)}
	 * <p>
	 * If overridden, this method makes sure that real coordinates are used
	 * @param P Point
	 * @return True if the given point lies inside this Region
	 */
	public default boolean isInRegionInRealCoords(GeoPointND P) {
		return isInRegion(P);
	}

	/**
	 * says if the point (x0,y0) is in the region
	 * 
	 * @param x0
	 *            x-coord of the point
	 * @param y0
	 *            y-coord of the point
	 * @return true if the point (x0,y0) is in the region
	 */
	public boolean isInRegion(double x0, double y0);

}
