package org.geogebra.common.properties.impl.objects.collection;

import java.util.Collection;

import org.geogebra.common.properties.StringProperty;

/**
 * Handles a collection of StringProperty objects as a single StringProperty.
 */
public class StringPropertyCollection
        extends AbstractPropertyCollection<StringProperty, String> implements StringProperty {

    /**
     * @param propertyCollection properties to handle
     */
    public StringPropertyCollection(Collection<? extends StringProperty> propertyCollection) {
        super(propertyCollection.toArray(new StringProperty[0]));
    }

    @Override
    public String getValue() {
        return reduceValue();
    }

    @Override
    public void setValue(String value) {
        setProperties(value);
    }

    @Override
    public boolean isValid(String value) {
        boolean isValid = true;
        for (StringProperty property : properties) {
            isValid = isValid && property.isValid(value);
        }
        return isValid;
    }

    @Override
    String defaultValue() {
        return "";
    }

    @Override
    void setPropertyValue(StringProperty property, String value) {
        property.setValue(value);
    }

    @Override
    String getPropertyValue(StringProperty property) {
        return property.getValue();
    }
}
