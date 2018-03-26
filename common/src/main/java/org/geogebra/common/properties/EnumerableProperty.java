package org.geogebra.common.properties;

/**
 * A property that has enumerable string values.
 */
public interface EnumerableProperty extends StringProperty {

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
    int getCurrent();
}
