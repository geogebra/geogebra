package org.geogebra.common.properties.impl.objects.collection;

import java.util.Collection;

import org.geogebra.common.properties.BooleanProperty;
import org.geogebra.common.properties.Property;

public class BooleanPropertyCollection implements BooleanProperty {

    private Collection<BooleanProperty> propertyCollection;

    public BooleanPropertyCollection(Collection<BooleanProperty> propertyCollection) {
        this.propertyCollection = propertyCollection;
    }

    @Override
    public boolean getValue() {
        boolean value = true;
        for (BooleanProperty property : propertyCollection) {
            value = value && property.getValue();
        }
        return value;
    }

    @Override
    public void setValue(boolean value) {
        for (BooleanProperty property : propertyCollection) {
            property.setValue(value);
        }
    }

    @Override
    public String getName() {
        return propertyCollection.iterator().next().getName();
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
