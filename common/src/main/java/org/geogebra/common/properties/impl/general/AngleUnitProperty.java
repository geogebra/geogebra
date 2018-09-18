package org.geogebra.common.properties.impl.general;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.AbstractEnumerableProperty;

/**
 * Property for setting the angle unit.
 */
public class AngleUnitProperty extends AbstractEnumerableProperty {

    private Kernel kernel;

    /**
     * Constructs an angle unit property.
     *
     * @param kernel       kernel
     * @param localization localization
     */
    public AngleUnitProperty(Kernel kernel, Localization localization) {
        super(localization, "AngleUnit");
        this.kernel = kernel;
        if (kernel.getApplication().has(Feature.MOB_ANGLE_DEGREES_MINUTES_SECONDS)) {
			setValuesAndLocalize(new String[] { "Degree", "Radiant",
					"DegreesMinutesSeconds" });
        } else {
            setValuesAndLocalize(new String[]{"Degree", "Radiant"});
        }
    }

    @Override
    protected void setValueSafe(String value, int index) {
        int angleUnit;
        switch (index) {
            case 1:
                angleUnit = Kernel.ANGLE_RADIANT;
                break;
            case 2:
                if (kernel.getApplication().has(Feature.MOB_ANGLE_DEGREES_MINUTES_SECONDS)) {
                    angleUnit = Kernel.ANGLE_DEGREES_MINUTES_SECONDS;
                } else {
                    angleUnit = Kernel.ANGLE_DEGREE;
                }
                break;
            case 0:
            default:
                angleUnit = Kernel.ANGLE_DEGREE;
                break;
        }
        kernel.setAngleUnit(angleUnit);
		kernel.updateConstruction(false);
    }

    @Override
    public int getIndex() {
        switch (kernel.getAngleUnit()) {
            case Kernel.ANGLE_RADIANT:
                return 1;
            case Kernel.ANGLE_DEGREES_MINUTES_SECONDS:
                if (kernel.getApplication().has(Feature.MOB_ANGLE_DEGREES_MINUTES_SECONDS)) {
                    return 2;
                }
                return 0;
            case Kernel.ANGLE_DEGREE:
            default:
                return 0;
        }
    }
}
