package org.geogebra.common.properties.impl;

import org.geogebra.common.gui.menubar.OptionsMenu;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.AbstractEnumerableProperty;

import java.util.ArrayList;

public class RoundingProperty extends AbstractEnumerableProperty {

    private App app;
    private int figuresIndex;

    public RoundingProperty(App app, Localization localization) {
        super(localization, "Rounding", "OneOfMultiple");

        this.app = app;

        setupValues(localization);
    }

    private void setupValues(Localization localization) {
        String[] values = localization.getRoundingMenu();
        ArrayList<String> list = new ArrayList<>(values.length - 1);
        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            if (value.equals(Localization.ROUNDING_MENU_SEPARATOR)) {
                figuresIndex = i;
            } else {
                list.add(value);
            }
        }

        setValues(list.toArray());
    }

    @Override
    protected void setValueSafe(Object value, int index) {
        boolean figures = index >= figuresIndex;
        OptionsMenu.setRounding(app, figures ? index : index + 1, figures);
    }
}
