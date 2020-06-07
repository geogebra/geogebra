package org.geogebra.common.properties.impl.objects.collection;

import java.util.Collection;

import org.geogebra.common.properties.NumericProperty;

/**
 * Handles a collection of NumericProperty objects as a single NumericProperty.
 */
public class NumericPropertyCollection<T extends Number & Comparable<T>>
        extends AbstractPropertyCollection<NumericProperty<T>, T> implements NumericProperty<T> {

    /**
     * @param propertyCollection properties to handle
     */
    public NumericPropertyCollection(Collection<? extends NumericProperty<T>> propertyCollection) {
        super(propertyCollection.toArray(new NumericProperty[0]));
    }

    @Override
    public T getMin() {
        return getFirstProperty().getMin();
    }

    @Override
    public T getMax() {
        return getFirstProperty().getMax();
    }

    @Override
    public T getValue() {
        return reduceValue();
    }

    @Override
    public void setValue(T value) {
        setProperties(value);
    }

    @Override
    T defaultValue() {
        return getFirstProperty().getValue();
    }

    @Override
    void setPropertyValue(NumericProperty<T> property, T value) {
        property.setValue(value);
    }

    @Override
    T getPropertyValue(NumericProperty<T> property) {
        return property.getValue();
    }
}
