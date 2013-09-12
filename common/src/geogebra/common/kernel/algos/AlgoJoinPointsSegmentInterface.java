package geogebra.common.kernel.algos;

import geogebra.common.kernel.geos.GeoElement;

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

}
