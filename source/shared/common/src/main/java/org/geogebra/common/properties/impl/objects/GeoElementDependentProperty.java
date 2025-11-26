package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.Property;

/**
 * Property that may change value when the underlying {@link GeoElement} changes.
 */
public interface GeoElementDependentProperty extends Property {

	/**
	 * @return the geo element which this property is dependent on.
	 */
	GeoElement getGeoElement();
}
