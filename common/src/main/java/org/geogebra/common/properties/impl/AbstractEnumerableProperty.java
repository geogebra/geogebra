package org.geogebra.common.properties.impl;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.EnumerableProperty;

/**
 * Helps implementing enumerable properties. Handles the indexing of values.
 */
public abstract class AbstractEnumerableProperty extends AbstractProperty
        implements EnumerableProperty {

    private String[] values;

    /**
     * Constructs an AbstractEnumerableProperty
     *
     * @param localization the localization used
     * @param name         the name of the property
     */
    public AbstractEnumerableProperty(Localization localization, String name) {
        super(localization, name);
    }

    /**
     * Use this method to set the values of the property. These values are
     * not localized.
     *
     * @param values localized values of this property
     */
    protected void setValues(String... values) {
        this.values = values;
    }

    @Override
    public String[] getValues() {
        Localization localization = getLocalization();
        String[] localizedValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            localizedValues[i] = localization.getMenu(values[i]);
        }
        return localizedValues;
    }

    @Override
    public void setIndex(int index) {
        if (values == null) {
            throw new RuntimeException("Set values must be called in the constructor.");
        }
        if (index < 0 || index >= values.length) {
            throw new RuntimeException("Index must be between (0, values.length-1)");
        }
        setValueSafe(values[index], index);
    }

    /**
     * Set the value of this property. Index is between 0 and length of values.
     *
     * @param value value of the property
     * @param index the index of the values
     */
    protected abstract void setValueSafe(String value, int index);
}
