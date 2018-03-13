package org.geogebra.common.properties;

import org.geogebra.common.main.Localization;

public abstract class AbstractEnumerableProperty extends AbstractProperty implements EnumerableProperty {

    private Object[] values;

    public AbstractEnumerableProperty(Localization localization, String name, String type) {
        super(localization, name, type);
    }

    protected void setValues(Object[] values) {
        this.values = values;
    }

    @Override
    public Object[] getValues() {
        return values;
    }

    @Override
    public void setValue(Object value) {
        if (values == null) {
            throw new RuntimeException("Set values must be called in the constructor.");
        }
        for (int i = 0; i < values.length; i++) {
            if (values[i] == value) {
                setValueSafe(value, i);
                return;
            }
        }
        throw new RuntimeException("The property value should be one of its own values.");
    }

    protected abstract void setValueSafe(Object value, int index);
}
