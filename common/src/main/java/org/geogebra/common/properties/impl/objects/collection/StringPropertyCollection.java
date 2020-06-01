package org.geogebra.common.properties.impl.objects.collection;

import java.util.Collection;

import org.geogebra.common.properties.GeoElementProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.StringProperty;

public class StringPropertyCollection implements StringProperty, GeoElementProperty {

    private Collection<? extends StringProperty> propertyCollection;
    private StringProperty property;

    public StringPropertyCollection(Collection<? extends StringProperty> propertyCollection) {
        this.propertyCollection = propertyCollection;
        property = propertyCollection.iterator().next();
    }

    @Override
    public String getValue() {
        return property.getValue();
    }

    @Override
    public void setValue(String value) {
        for (StringProperty property : propertyCollection) {
            property.setValue(value);
        }
    }

    @Override
    public boolean isValid(String value) {
        boolean isValid = true;
        for (Property property : propertyCollection) {
            isValid = isValid && property.isEnabled();
        }
        return isValid;
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
