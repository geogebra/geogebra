package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.properties.BooleanProperty;

/**
 * Fix object
 */
public class FixObject extends AbstractGeoElementProperty implements BooleanProperty {

    public FixObject(GeoElement geoElement) {
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

    @Override
    public boolean isApplicableTo(GeoElement element) {
        if (element instanceof GeoList) {
            return isApplicableTo((GeoList) element);
        }
        AppConfig config = element.getApp().getConfig();
        if (hasFunctionProperties(element) && config.isObjectDraggingRestricted()) {
            return false;
        }
        return element.showFixUnfix();
    }

    private boolean hasFunctionProperties(GeoElement element) {
        if (element instanceof GeoList && !isApplicableTo(element)) {
            return false;
        } else return element.isFunctionOrEquationFromUser();
    }

    @Override
    boolean isApplicableTo(GeoList list) {
        GeoElement elementForProperties = list.getGeoElementForPropertiesDialog();
        return elementForProperties instanceof GeoFunction;
    }
}
