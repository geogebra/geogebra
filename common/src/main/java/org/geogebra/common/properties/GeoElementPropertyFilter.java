package org.geogebra.common.properties;

import org.geogebra.common.kernel.geos.GeoElement;

public interface GeoElementPropertyFilter {
    boolean isAllowed(Property property, GeoElement geoElement);
}
