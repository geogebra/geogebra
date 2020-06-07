package org.geogebra.common.properties.impl.objects.collection;

import java.util.Collection;

import org.geogebra.common.properties.RangeProperty;

/**
 * Handles a collection of RangeProperty objects as a single RangeProperty.
 */
public class RangePropertyCollection<T extends Number & Comparable<T>>
        extends NumericPropertyCollection<T> implements RangeProperty<T> {

    /**
     * @param propertyCollection properties to handle
     */
    public RangePropertyCollection(Collection<? extends RangeProperty<T>> propertyCollection) {
        super(propertyCollection);
    }

    @Override
    public T getStep() {
        RangeProperty<T> rangeProperty = (RangeProperty<T>) getFirstProperty();
        return rangeProperty.getStep();
    }
}
