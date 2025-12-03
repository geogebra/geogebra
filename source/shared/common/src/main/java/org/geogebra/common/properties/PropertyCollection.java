package org.geogebra.common.properties;

/**
 * This property has a collection of properties. This can be though of as a container
 * for sub properties that are related to each-other.
 */
public interface PropertyCollection<P extends Property> extends Property {

    /**
     * Returns the properties that belong to this property group.
     *
     * @return an array of properties.
     */
    P[] getProperties();
}
