package org.geogebra.common.properties;

/**
 * Holds a reference to the array of the properties and to the name of this properties collection.
 */
public class PropertiesArray implements PropertyCollection {

    private String name;
    private Property[] properties;

    /**
     * @param name name
     * @param properties properties
     */
    public PropertiesArray(String name, Property... properties) {
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
        return properties;
    }
}
