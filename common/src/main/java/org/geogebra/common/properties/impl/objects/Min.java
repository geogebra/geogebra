package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Min
 */
public class Min extends RangelessDecimalProperty {

    private SliderPropertyDelegate delegate;

    public Min(GeoNumeric numeric) {
        super("Minimum.short", numeric);
        delegate = new SliderPropertyDelegate(this);
    }

    @Override
    public Double getValue() {
        return getElement().getIntervalMin();
    }

    @Override
    public void setValue(Double value) {
        GeoNumeric numeric = getElement();
        numeric.setIntervalMin(value);
        numeric.getApp().setPropertiesOccured();
    }

    @Override
    public boolean isApplicableTo(GeoElement element) {
        return delegate.isSlider(element);
    }
}
