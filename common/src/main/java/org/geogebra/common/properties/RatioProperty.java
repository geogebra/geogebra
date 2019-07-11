package org.geogebra.common.properties;

public interface RatioProperty extends Property{

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

    /**
     * Returns the current units.
     *
     * @return the current units of the ratio
     */
    String getUnits();
}
