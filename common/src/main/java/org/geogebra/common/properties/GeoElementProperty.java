package org.geogebra.common.properties;

import java.util.List;

import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Property interface for properties that are related to GeoElements.
 */
public interface GeoElementProperty extends Property {

	/**
	 * Set the list of geo elements to check this property for.
	 *
	 * @param geoElements list of elements that the property is for
	 */
	void setGeoElements(List<GeoElementND> geoElements);

}
