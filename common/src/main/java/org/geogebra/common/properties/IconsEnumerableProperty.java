package org.geogebra.common.properties;

/**
 * A property which values are also represented as icons.
 */
public interface IconsEnumerableProperty extends EnumerableProperty {

    /**
     * Returns an array with the icon resources. The identifiers are usually
     * tied to a specific property.
     *
     * @return an array of identifiers
     */
    PropertyResource[] getIcons();
}
