package org.geogebra.common.kernel;

import org.geogebra.common.kernel.geos.ToGeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * PathOrPoint needed as well as Path so that points can be elements of compound
 * paths eg {(2,3), (4,5), Segment[(6,7),(8,9)] } see GeoList.pointChanged()
 */

public interface PathOrPoint extends ToGeoElement {

	/*
	 * static final int PATH_LINE = 0; static final int PATH_SEGMENT = 1; static
	 * final int PATH_ELLIPSE = 10; static final int PATH_HYPERBOLA = 11; static
	 * final int PATH_PARABOLA = 12; static final int PATH_POINT = 13; static
	 * final int PATH_EMPTY = 14; static final int PATH_FUNCTION = 20;
	 */

	/**
	 * Sets coords of P and its path parameter when the coords of P have
	 * changed. Afterwards P lies on this path.
	 * 
	 * Note: P.setCoords() is not called!
	 * 
	 * @param PI
	 *            point P
	 */
	public void pointChanged(GeoPointND PI);

	/**
	 * Sets coords of P and its path parameter when this path has changed.
	 * Afterwards P lies on this path.
	 * 
	 * Note: P.setCoords() is not called!
	 * 
	 * @param PI
	 *            point P
	 */
	public void pathChanged(GeoPointND PI);

	/**
	 * Returns true iff the given point lies on this path.
	 * 
	 * @param PI
	 *            point
	 * @param eps
	 *            precision
	 * @return true iff the given point lies on this path.
	 */
	public boolean isOnPath(GeoPointND PI, double eps);

	/**
	 * Returns the smallest possible parameter value for this path (may be
	 * Double.NEGATIVE_INFINITY)
	 * 
	 * @return minimum parameter value for this path
	 */
	public double getMinParameter();

	/**
	 * Returns the largest possible parameter value for this path (may be
	 * Double.POSITIVE_INFINITY)
	 * 
	 * @return maximum parameter value for this path
	 */
	public double getMaxParameter();

	/**
	 * Returns whether this path is closed (i.e. its first and last point are
	 * equal).
	 * 
	 * @return true iff closed
	 */
	public boolean isClosedPath();

	/**
	 * Returns a PathMover object for this path.
	 * 
	 * @return a PathMover object for this path.
	 */
	public PathMover createPathMover();

}
