package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Min
 */
public class MinProperty extends RangelessDecimalProperty {

    private SliderPropertyDelegate delegate;

    public MinProperty(GeoNumeric numeric) {
        super("Minimum.short", numeric);
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
    boolean isApplicableTo(GeoElement element) {
        return getDelegate().isSlider(element);
    }

    private SliderPropertyDelegate getDelegate() {
        if (delegate == null) {
            delegate = new SliderPropertyDelegate(this);
        }
        return delegate;
    }
}
