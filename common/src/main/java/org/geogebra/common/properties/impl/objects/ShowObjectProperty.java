package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.BooleanProperty;

/**
 * Show object
 */
public class ShowObjectProperty extends AbstractGeoElementProperty implements BooleanProperty {

    public ShowObjectProperty(GeoElement geoElement) throws NotApplicablePropertyException {
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
    }

    @Override
    boolean isApplicableTo(GeoElement element) {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
