package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError;
import org.geogebra.common.properties.StringProperty;

/**
 * Name
 */
public class Name extends AbstractGeoElementProperty implements StringProperty {

    public Name(GeoElement geoElement) {
        super(geoElement.translatedTypeString(), geoElement);
    }

    @Override
    public String getValue() {
        return getElement().getLabelSimple();
    }

    @Override
    public void setValue(String name) {
        if (name == null || name.isEmpty() || name.equals(getValue())) {
            return;
        }

        GeoElement element = getElement();
        App app = element.getApp();
        try {
            if (LabelManager.isValidLabel(name, element.getKernel(), element)) {
                element.rename(name);
                element.setAlgebraLabelVisible(true);
                element.getKernel().notifyUpdate(element);
                element.updateRepaint();
                app.setPropertiesOccured();
            }
        } catch (MyError e) {
            app.showError(e.getLocalizedMessage());
        }
    }

    @Override
    public boolean isValid(String value) {
        return false;
    }

    @Override
    public boolean isApplicableTo(GeoElement element) {
        String label = element.isAlgebraLabelVisible() ? element.getLabelSimple() : "";
        return label != null;
    }
}
