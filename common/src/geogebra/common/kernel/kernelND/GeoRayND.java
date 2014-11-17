package geogebra.common.kernel.kernelND;

import geogebra.common.kernel.geos.GeoElement;


/**
 * @author mathieu
 *
 * Interface for ray in any dimension
 */

public interface GeoRayND extends GeoLineND{

	/**
	 * @return true if this should stay a ray after transform
	 */
	public boolean keepsTypeOnGeometricTransform();

	/**
	 * @return copy of Ray with free input Point & Vector
	 */
	public GeoElement copyFreeRay();

}
