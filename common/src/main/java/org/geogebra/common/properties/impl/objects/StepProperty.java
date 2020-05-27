package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Step
 */
public class StepProperty extends RangelessDecimalProperty {

    public StepProperty(GeoNumeric numeric) {
        super("AnimationStep", numeric);
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
}
