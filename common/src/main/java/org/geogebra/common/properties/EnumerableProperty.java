package org.geogebra.common.properties;

/**
 * A property that has enumerable string values.
 */
public interface EnumerableProperty extends Property {

    /**
     * Get the possible localized values for this property.
     *
     * @return possible values of the property
     */
    String[] getValues();

    /**
     * Get the index of the current value.
     * See {@link EnumerableProperty#getValues()}.
     *
     * @return the index of the current value
     */
    int getIndex();

    /**
     * Sets the index of the current value.
     * See {@link EnumerableProperty#getValues()}.
     *
     * @param index the index of the current value
     */
    void setIndex(int index);
}
