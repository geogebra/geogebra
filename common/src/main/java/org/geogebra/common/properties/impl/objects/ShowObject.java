package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.BooleanProperty;

/**
 * Show object
 */
public class ShowObject extends AbstractGeoElementProperty implements BooleanProperty {

    public ShowObject(GeoElement geoElement) {
        super("Show", geoElement);
    }

    @Override
    public boolean getValue() {
        return getElement().isEuclidianVisible();
    }

    @Override
    public void setValue(boolean show) {
        GeoElement element = getElement();
        element.setEuclidianVisible(show);
        element.updateRepaint();
        element.getApp().setPropertiesOccured();
    }

    @Override
    public boolean isApplicableTo(GeoElement element) {
        return true;
    }
}
