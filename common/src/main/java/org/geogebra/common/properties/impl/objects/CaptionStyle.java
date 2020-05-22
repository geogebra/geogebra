package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.properties.EnumerableProperty;

/**
 * Caption style
 */
public class CaptionStyle extends AbstractGeoElementProperty implements EnumerableProperty {

    private static final int LABEL_HIDDEN = 0;

    private static final String[] captionStyleNames = {
            "Hidden",
            "Name",
            "NameAndValue",
            "Value",
            "Caption"
    };

    public CaptionStyle(GeoElement geoElement) {
        super("stylebar.Caption", geoElement);
    }

    @Override
    public String[] getValues() {
        return captionStyleNames;
    }

    @Override
    public int getIndex() {
        return getElement().getLabelMode();
    }

    @Override
    public void setIndex(int captionStyle) {
        GeoElement element = getElement();
        element.setLabelMode(captionStyle);
        element.setLabelVisible(captionStyle != LABEL_HIDDEN);
        element.updateRepaint();
        element.getApp().setPropertiesOccured();
    }

    @Override
    public boolean isApplicableTo(GeoElement element) {
        return !isTextOrInput(element);
    }
}
