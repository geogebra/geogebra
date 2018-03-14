package org.geogebra.common.properties.impl;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.AbstractEnumerableProperty;

public class AngleUnitProperty extends AbstractEnumerableProperty {

    private Kernel kernel;

    public AngleUnitProperty(Kernel kernel, Localization localization) {
        super(localization, "AngleUnit");
        this.kernel = kernel;
        setupValues(localization);
    }

    private void setupValues(Localization localization) {
        String degree = localization.getMenu("Degree");
        String radiant = localization.getMenu("Radiant");
        setValues(new String[] { degree, radiant });
    }

    @Override
    protected void setValueSafe(String value, int index) {
        int angleUnit = index == 0 ? Kernel.ANGLE_DEGREE : Kernel.ANGLE_RADIANT;
        kernel.setAngleUnit(angleUnit);
        kernel.updateConstruction();
    }

    @Override
    public int getCurrent() {
        return kernel.getAngleUnit() == Kernel.ANGLE_DEGREE ? 0 : 1;
    }
}
