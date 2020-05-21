package org.geogebra.common.properties;

import java.util.Arrays;
import java.util.Collection;

/**
 * Holds a reference to the collection of the properties and to the name of this collection.
 */
public abstract class AbstractPropertyCollection implements PropertyCollection {

    static final Property[] EMPTY_ARRAY = new Property[0];

    private String name;
    private Collection<Property> properties;

    AbstractPropertyCollection(String name, Property... properties) {
        this.name = name;
        this.properties = Arrays.asList(properties);
    }

    AbstractPropertyCollection(String name, Collection<Property> properties) {
        this.name = name;
        this.properties = properties;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Property[] getProperties() {
        return properties.toArray(EMPTY_ARRAY);
    }

    Collection<Property> getPropertyCollection() {
        return properties;
    }
}
