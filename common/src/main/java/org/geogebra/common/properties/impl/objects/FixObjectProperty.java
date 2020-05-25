package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.BooleanProperty;

/**
 * Fix object
 */
public class FixObjectProperty extends AbstractGeoElementProperty implements BooleanProperty {

    public FixObjectProperty(GeoElement geoElement) {
        super("fixed", geoElement);
    }

    @Override
    public boolean getValue() {
        return getElement().isLocked();
    }

    @Override
    public void setValue(boolean fixObject) {
        GeoElement element = getElement();
        element.setFixed(fixObject);
        element.getApp().setPropertiesOccured();
    }
}
