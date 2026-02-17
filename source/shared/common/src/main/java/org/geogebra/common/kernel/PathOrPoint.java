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
import org.geogebra.common.util.DoubleUtil;

/**
 * PathOrPoint needed as well as Path so that points can be elements of compound
 * paths eg {(2,3), (4,5), Segment[(6,7),(8,9)] } see GeoList.pointChanged()
 */

public interface PathOrPoint extends GeoElementND {

	/**
	 * Sets coords of P and its path parameter when the coords of P have
	 * changed. Afterwards P lies on this path.
	 * 
	 * Note: P.setCoords() is not called!
	 * 
	 * @param PI
	 *            point P
	 */
	void pointChanged(GeoPointND PI);

	/**
	 * Sets coords of P and its path parameter when this path has changed.
	 * Afterwards P lies on this path.
	 * 
	 * Note: P.setCoords() is not called!
	 * 
	 * @param PI
	 *            point P
	 */
	void pathChanged(GeoPointND PI);

	/**
	 * Returns true iff the given point lies on this path.
	 * 
	 * @param PI
	 *            point
	 * @param eps
	 *            precision
	 * @return true iff the given point lies on this path.
	 */
	boolean isOnPath(GeoPointND PI, double eps);

	/**
	 * Returns the smallest possible parameter value for this path (may be
	 * Double.NEGATIVE_INFINITY)
	 * 
	 * @return minimum parameter value for this path
	 */
	double getMinParameter();

	/**
	 * Returns the largest possible parameter value for this path (may be
	 * Double.POSITIVE_INFINITY)
	 * 
	 * @return maximum parameter value for this path
	 */
	double getMaxParameter();

	/**
	 * Returns whether this path is closed (i.e. its first and last point are
	 * equal).
	 * 
	 * @return true iff closed
	 */
	boolean isClosedPath();

	/**
	 * Returns a PathMover object for this path.
	 * 
	 * @return a PathMover object for this path.
	 */
	PathMover createPathMover();

	/**
	 * @param parameter path parameter
	 * @param increment increment
	 * @return whether adding increment would have no effect
	 */
	default boolean cannotAdd(PathParameter parameter, double increment) {
		double threshold = increment < 0 ? getMinParameter() : getMaxParameter();
		return (getMaxParameter() - getMinParameter() > Kernel.MIN_PRECISION)
				&& DoubleUtil.isEqual(parameter.t, threshold);
	}

}
