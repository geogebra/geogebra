package org.geogebra.common.euclidian;

import org.geogebra.common.kernel.geos.GeoElement;

public interface GeoElementVisibilityFilter {
    boolean isAllowed(GeoElement geoElement);
}
