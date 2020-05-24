package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.properties.BooleanProperty;

/**
 * Show in AV
 */
public class ShowInAV extends AbstractGeoElementProperty implements BooleanProperty {

    public ShowInAV(GeoElement geoElement) {
        super("ShowInAlgebraView", geoElement);
    }

    @Override
    public boolean getValue() {
        return !getElement().isAuxiliaryObject();
    }

    @Override
    public void setValue(boolean show) {
        GeoElement element = getElement();
        element.setAuxiliaryObject(!show);
        element.updateRepaint();

        App app = element.getApp();
        app.setPropertiesOccured();
        app.updateGuiForShowAuxiliaryObjects();
    }

    @Override
    boolean isApplicableTo(GeoElement element) {
        return true;
    }
}
