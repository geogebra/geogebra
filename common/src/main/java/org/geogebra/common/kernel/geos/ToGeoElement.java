package org.geogebra.common.kernel.geos;

/**
 * Objects convertible to GeoElement
 */
public interface ToGeoElement {
	/**
	 * @return GeoElement equivalent of this object
	 */
	public GeoElement toGeoElement();
}
