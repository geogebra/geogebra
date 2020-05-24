package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Max
 */
public class Max extends RangelessDecimalProperty {

    private SliderPropertyDelegate delegate;

    public Max(GeoNumeric numeric) {
        super("Maximum.short", numeric);
        delegate = new SliderPropertyDelegate(this);
    }

    @Override
    public Double getValue() {
        return getElement().getIntervalMax();
    }

    @Override
    public void setValue(Double value) {
        GeoNumeric numeric = getElement();
        numeric.setIntervalMax(value);
        numeric.getApp().setPropertiesOccured();
    }

    @Override
    boolean isApplicableTo(GeoElement element) {
        return delegate.isSlider(element);
    }
}
