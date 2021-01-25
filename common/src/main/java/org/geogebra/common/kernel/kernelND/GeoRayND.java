package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * @author mathieu
 *
 *         Interface for ray in any dimension
 */

public interface GeoRayND extends GeoLineND {

	/**
	 * @return true if this should stay a ray after transform
	 */
	boolean keepsTypeOnGeometricTransform();

	/**
	 * @return unlabeled copy of Ray with free input Point & Vector
	 */
	GeoElement copyFreeRay();

}
