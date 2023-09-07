package org.geogebra.common.properties;

/**
 * A property whose values have icons associated with.
 */
public interface IconsEnumeratedProperty<V> extends EnumeratedProperty<V> {

    /**
     * Returns an array with the icon resources. The identifiers are usually
     * tied to a specific property.
     *
     * @return an array of identifiers
     */
    PropertyResource[] getValueIcons();
}
