package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Max
 */
public class MaxProperty extends RangelessDecimalProperty {

    private SliderPropertyDelegate delegate;

    public MaxProperty(GeoNumeric numeric) throws NotApplicablePropertyException {
        super("Maximum.short", numeric);
    }

    @Override
    public Double getValue() {
        return getElement().getIntervalMax();
    }

    @Override
    public void setValue(Double value) {
        GeoNumeric numeric = getElement();
        numeric.setIntervalMax(value);
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

    @Override
    public boolean isEnabled() {
        return true;
    }
}
