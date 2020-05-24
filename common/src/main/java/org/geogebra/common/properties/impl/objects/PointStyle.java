package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.gui.dialog.options.model.PointStyleModel;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.properties.EnumerableProperty;

/**
 * Point style
 */
public class PointStyle extends AbstractGeoElementProperty implements EnumerableProperty {

    private static final String[] styles = new String[0];

    public PointStyle(GeoElement geoElement) {
        super("Properties.Style", geoElement);
    }

    @Override
    public String[] getValues() {
        return styles;
    }

    @Override
    public int getIndex() {
        return getElement() instanceof GeoPoint ? ((GeoPoint) getElement()).getPointStyle() : -1;
    }

    @Override
    public void setIndex(int pointStyle) {
        GeoElement element = getElement();
        if (element instanceof PointProperties) {
            ((PointProperties) element).setPointStyle(pointStyle);
            element.updateVisualStyleRepaint(GProperty.POINT_STYLE);
            element.getApp().setPropertiesOccured();
        }
    }

    @Override
    boolean isApplicableTo(GeoElement element) {
        if (isTextOrInput(element)) {
            return false;
        }
        if (element instanceof GeoList) {
            return isApplicableTo(element);
        }
        return PointStyleModel.match(element);
    }
}
