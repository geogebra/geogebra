package org.geogebra.common.properties;

import org.geogebra.common.main.Localization;

public abstract class AbstractBooleanProperty extends AbstractProperty implements BooleanProperty {

    public AbstractBooleanProperty(Localization localization, String name) {
        super(localization, name);
    }

    @Override
    public void setValue(String value) {
        if (TRUE.equals(value)) {
            setValueSafe(true);
        } else if (FALSE.equals(value)) {
            setValueSafe(false);
        } else {
            throw new RuntimeException("setValue must be one of TRUE or FALSE");
        }
    }

    protected abstract void setValueSafe(boolean value);
}
