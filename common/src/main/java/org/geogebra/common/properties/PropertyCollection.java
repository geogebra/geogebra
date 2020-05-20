package org.geogebra.common.properties;

/**
 * This property has a collection of properties. This can be though of as a container
 * for sub properties that are related to eachother.
 */
public interface PropertyCollection extends Property {

    /**
     * Returns the properties that belong to this property group.
     *
     * @return a list of properties.
     */
    Property[] getProperties();
}
