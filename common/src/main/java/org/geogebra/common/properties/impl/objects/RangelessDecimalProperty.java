package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.properties.NumericProperty;

/**
 * Abstract implementation of a Double value property that has the maximal range of a Double value:
 * Double.MIN_VALUE < --- > Double.MAX_VALUE
 * (so it's practically rangeless).
 */
public abstract class RangelessDecimalProperty
        extends AbstractGeoElementProperty implements NumericProperty<Double> {

    protected RangelessDecimalProperty(String name, GeoNumeric numeric) {
        super(name, numeric);
    }

    @Override
    public Double getMin() {
        return Double.MIN_VALUE;
    }

    @Override
    public Double getMax() {
        return Double.MAX_VALUE;
    }

    @Override
    GeoNumeric getElement() {
        return (GeoNumeric) super.getElement();
    }
}
