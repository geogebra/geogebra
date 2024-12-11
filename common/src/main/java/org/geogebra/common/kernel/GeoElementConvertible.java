package org.geogebra.common.kernel;

import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Can be converted to GeoElement
 */
public interface GeoElementConvertible {

    /**
     * @param cons construction for the new GeoElement
     * @return new GeoElement
     */
    GeoElement toGeoElement(Construction cons);
}
