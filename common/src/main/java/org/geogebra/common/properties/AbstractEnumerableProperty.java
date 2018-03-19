package org.geogebra.common.properties;

import org.geogebra.common.main.Localization;

public abstract class AbstractEnumerableProperty extends AbstractProperty implements EnumerableProperty {

    private String[] values;

    public AbstractEnumerableProperty(Localization localization, String name) {
        super(localization, name);
    }

    protected void setValues(String[] values) {
        this.values = values;
    }

    protected void setValuesAndLocalize(String[] values) {
        setValues(values);
        localizeValues();
    }

    private void localizeValues() {
        Localization localization = getLocalization();
        for (int i = 0; i < values.length; i++) {
            values[i] = localization.getMenu(values[i]);
        }
    }

    @Override
    public String[] getValues() {
        return values;
    }

    @Override
    public void setValue(String value) {
        if (values == null) {
            throw new RuntimeException("Set values must be called in the constructor.");
        }
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(value)) {
                setValueSafe(value, i);
                return;
            }
        }
        throw new RuntimeException("The property value should be one of its own values.");
    }

    protected abstract void setValueSafe(String value, int index);
}
