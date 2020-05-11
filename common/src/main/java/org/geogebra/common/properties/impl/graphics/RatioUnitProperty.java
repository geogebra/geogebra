package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.ar.ARManagerInterface;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.AbstractEnumerableProperty;

public class RatioUnitProperty extends AbstractEnumerableProperty {

    private ARManagerInterface arManager;

    /**
     * Constructs an ratio unit property.
     *
     * @param arManager      AR Manager
     * @param localization  localization
     */
    RatioUnitProperty(Localization localization, ARManagerInterface arManager) {
        super(localization, "Unit");
        this.arManager = arManager;
        setValuesAndLocalize(new String[]{"cm", "inch"});

    }

    @Override
    protected void setValueSafe(String value, int index) {
        int lengthUnit;
        switch (index) {
            case 1:
                lengthUnit = EuclidianView3D.RATIO_UNIT_INCHES;
                break;
            case 0:
            default:
                lengthUnit = EuclidianView3D.RATIO_UNIT_METERS_CENTIMETERS_MILLIMETERS;
                break;
        }
        arManager.setARRatioMetricSystem(lengthUnit);
    }

    @Override
    public int getIndex() {
        switch (arManager.getARRatioMetricSystem()) {
            case EuclidianView3D.RATIO_UNIT_INCHES:
                return 1;
            case EuclidianView3D.RATIO_UNIT_METERS_CENTIMETERS_MILLIMETERS:
            default:
                return 0;
        }
    }

    @Override
    public boolean isEnabled() {
        return arManager.isRatioShown();
    }
}
