package geogebra.common.kernel.kernelND;


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

}
