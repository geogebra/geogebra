package org.geogebra.common.properties;

/**
 * A property that is either true or false.
 */
public interface BooleanProperty extends Property {

    /**
     * The boolean value of the property.
     *
     * @return value of the property.
     */
    boolean getValue();

    /**
     * Set the boolean value of the property.
     *
     * @param value the value of the property
     */
    void setValue(boolean value);
}
