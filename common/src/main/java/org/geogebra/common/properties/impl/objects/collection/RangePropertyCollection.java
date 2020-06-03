package org.geogebra.common.properties.impl.objects.collection;

import java.util.Collection;

import org.geogebra.common.properties.RangeProperty;

public class RangePropertyCollection<T extends Number & Comparable<T>>
        extends NumericPropertyCollection<T> implements RangeProperty<T> {

    public RangePropertyCollection(Collection<? extends RangeProperty<T>> propertyCollection) {
        super(propertyCollection);
    }

    @Override
    public T getStep() {
        RangeProperty<T> rangeProperty = (RangeProperty<T>) property;
        return rangeProperty.getStep();
    }
}
