package org.geogebra.common.properties.impl.objects.collection;

import java.util.Collection;

import org.geogebra.common.properties.GeoElementProperty;
import org.geogebra.common.properties.NumericProperty;
import org.geogebra.common.properties.Property;

public class NumericPropertyCollection<T extends Number & Comparable<T>>
        implements NumericProperty<T>, GeoElementProperty {

    private Collection<? extends NumericProperty<T>> propertyCollection;
    NumericProperty<T> property;

    public NumericPropertyCollection(Collection<? extends NumericProperty<T>> propertyCollection) {
        this.propertyCollection = propertyCollection;
        property = propertyCollection.iterator().next();
    }

    @Override
    public T getMin() {
        return property.getMin();
    }

    @Override
    public T getMax() {
        return property.getMax();
    }

    @Override
    public T getValue() {
        return property.getValue();
    }

    @Override
    public void setValue(T value) {
        for (NumericProperty<T> property : propertyCollection) {
            property.setValue(value);
        }
    }

    @Override
    public String getName() {
        return property.getName();
    }

    @Override
    public boolean isEnabled() {
        boolean isEnabled = true;
        for (Property property : propertyCollection) {
            isEnabled = isEnabled && property.isEnabled();
        }
        return isEnabled;
    }
}
