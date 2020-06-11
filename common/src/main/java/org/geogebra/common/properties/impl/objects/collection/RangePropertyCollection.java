package org.geogebra.common.properties.impl.objects.collection;

import org.geogebra.common.properties.RangeProperty;

/**
 * Handles a collection of RangeProperty objects as a single RangeProperty.
 */
public class RangePropertyCollection<T extends RangeProperty<V>, V extends Number & Comparable<V>>
        extends NumericPropertyCollection<T, V> implements RangeProperty<V> {

    /**
     * @param properties properties to handle
     */
    public RangePropertyCollection(T[] properties) {
        super(properties);
    }

    @Override
    public V getStep() {
        return getFirstProperty().getStep();
    }
}
