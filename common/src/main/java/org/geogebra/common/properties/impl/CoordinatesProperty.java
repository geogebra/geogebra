package org.geogebra.common.properties.impl;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.AbstractEnumerableProperty;

public class CoordinatesProperty extends AbstractEnumerableProperty {

    private Kernel kernel;

    public CoordinatesProperty(Kernel kernel, Localization localization) {
        super(localization, "Coordinates");
        this.kernel = kernel;
        setValuesAndLocalize(new String[] {
                "A = (x, y)",
                "A(x | y)",
                "A: (x, y)"
        });
    }

    @Override
    protected void setValueSafe(String value, int index) {
        kernel.setCoordStyle(index);
        kernel.updateConstruction();
    }

    @Override
    public int getCurrent() {
        return kernel.getCoordStyle();
    }
}
