package org.geogebra.common.properties;

/**
 * A property that is represented as a String.
 */
public interface StringProperty extends Property {

    /**
     * Returns the current value of the property.
     *
     * @return the current value of the property
     */
    String getValue();

    /**
     * Set the value of the property.
     *
     * @param value the new value for the property
     */
    void setValue(String value);

    /**
     * Validates the value and returns a boolean whether it was valid or not.
     *
     * @return a boolean whether the value is valid or not
     */
    boolean isValid(String value);
}
