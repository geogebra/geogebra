package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * interface to merge AlgoJointPoints and AlgoJointPoints3D
 *
 */
public interface AlgoJoinPointsSegmentInterface {

	/**
	 * 
	 * @return polygon/polyhedron of this algo (or null)
	 */
	public GeoElement getPoly();

	/**
	 * modify input points
	 * 
	 * @param A
	 *            first point
	 * @param B
	 *            second point
	 */
	public void modifyInputPoints(GeoPointND A, GeoPointND B);

	public void compute();

}
