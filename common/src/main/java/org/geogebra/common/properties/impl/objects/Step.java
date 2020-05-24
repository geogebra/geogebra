package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Step
 */
public class Step extends RangelessDecimalProperty {

    private SliderPropertyDelegate delegate;

    public Step(GeoNumeric numeric) {
        super("AnimationStep", numeric);
        delegate = new SliderPropertyDelegate(this);
    }

    @Override
    public Double getValue() {
        return getElement().getAnimationStep();
    }

    @Override
    public void setValue(Double value) {
        GeoNumeric numeric = getElement();
        numeric.setAnimationStep(value);
        numeric.getApp().setPropertiesOccured();
    }

    @Override
    boolean isApplicableTo(GeoElement element) {
        return delegate.isSlider(element);
    }
}
