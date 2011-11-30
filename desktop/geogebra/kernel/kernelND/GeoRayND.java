package geogebra.kernel.kernelND;

import geogebra.common.kernel.kernelND.GeoLineND;

/**
 * @author mathieu
 *
 * Interface for ray in any dimension
 */

public interface GeoRayND extends GeoLineND{

	public boolean keepsTypeOnGeometricTransform();

}
