package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;

/**
 * Color property
 */
public class ColorProperty extends AbstractGeoElementProperty {

    public ColorProperty(GeoElement geoElement) {
        super("stylebar.Color", geoElement);
    }

    /**
     * @return color
     */
    public GColor getColor() {
        return getElement().getObjectColor();
    }

    /**
     * @param color color
     */
    public void setColor(GColor color) {
        GeoElement element = getElement();
        App app = element.getApp();
        EuclidianStyleBarStatic.applyColor(
                color, element.getAlphaValue(), app, app.getSelectionManager().getSelectedGeos());
        app.setPropertiesOccured();
    }
}
