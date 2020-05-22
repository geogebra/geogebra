package org.geogebra.common.properties;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Property interface for properties that are related to GeoElements.
 */
public interface GeoElementProperty extends Property {

    /**
     * @param element Element with properties.
     * @return True if the element has the property.
     */
    boolean isApplicableTo(GeoElement element);
}
