package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Max
 */
public class Max extends RangelessDecimalProperty {

    public Max(GeoNumeric numeric) {
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
        numeric.getApp().setPropertiesOccured();
    }
}
