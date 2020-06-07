package org.geogebra.common.properties.impl.objects.collection;

import java.util.Collection;

import org.geogebra.common.properties.EnumerableProperty;

/**
 * Handles a collection of EnumerableProperty objects as a single EnumerableProperty.
 */
public class EnumerablePropertyCollection
        extends AbstractPropertyCollection<EnumerableProperty, Integer>
        implements EnumerableProperty {

    /**
     * @param propertyCollection properties to handle
     */
    public EnumerablePropertyCollection(
            Collection<? extends EnumerableProperty> propertyCollection) {
        super(propertyCollection.toArray(new EnumerableProperty[0]));
    }

    @Override
    public String[] getValues() {
        return getFirstProperty().getValues();
    }

    @Override
    public int getIndex() {
        return reduceValue();
    }

    @Override
    public void setIndex(int index) {
        setProperties(index);
    }

    @Override
    Integer defaultValue() {
        return 0;
    }

    @Override
    void setPropertyValue(EnumerableProperty property, Integer value) {
        property.setIndex(value);
    }

    @Override
    Integer getPropertyValue(EnumerableProperty property) {
        return property.getIndex();
    }
}
