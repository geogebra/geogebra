package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.GeoElementProperty;

public class NotApplicablePropertyException extends RuntimeException {

    private final String message;

    NotApplicablePropertyException(GeoElement element, GeoElementProperty property) {
        message =
                "The GeoElement with label "
                        + element.getLabelSimple()
                        + " has no property named "
                        + property.getName();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
