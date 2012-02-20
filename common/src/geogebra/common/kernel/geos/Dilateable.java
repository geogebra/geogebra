package geogebra.common.kernel.geos;

import geogebra.common.kernel.arithmetic.NumberValue;
/**
 * Interface for geos which can be dilated from point
 *
 */
public interface Dilateable extends ToGeoElement{
	/**
	 * Dilates the element
	 * @param r ratio
	 * @param S point
	 */
	public void dilate(NumberValue r, GeoPoint2 S);
}
