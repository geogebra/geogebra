package org.geogebra.common.properties;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Filter that determines whether a given {@link Property}
 * is allowed for a specific {@link GeoElement}.
 */
public interface GeoElementPropertyFilter {
    /**
     * Determines if the specified {@link Property} is allowed for the given {@link GeoElement}.
     * @param property the {@link Property} to be evaluated
     * @param geoElement the {@link GeoElement} to which the property applies
     * @return {@code true} if the property is allowed for the geo element, {@code false} otherwise
     */
    boolean isAllowed(Property property, GeoElement geoElement);
}
