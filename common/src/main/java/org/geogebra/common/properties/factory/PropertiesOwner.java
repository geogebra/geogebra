package org.geogebra.common.properties.factory;

import java.util.List;

import org.geogebra.common.properties.PropertyCollection;

/**
 * Implemented by those classes that have settings properties.
 */
public interface PropertiesOwner {

    /**
     * @return The list of property collections
     */
    List<PropertyCollection> getProperties();
}
