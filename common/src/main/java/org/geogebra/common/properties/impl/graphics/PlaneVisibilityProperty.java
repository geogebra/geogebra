package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.properties.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractProperty;

public class PlaneVisibilityProperty extends AbstractProperty implements BooleanProperty {

    private EuclidianSettings3D euclidianSettings;

    /**
     * Constructs an abstract property.
     *
     * @param localization      this is used to localize the name
     * @param euclidianSettings euclidian settings
     */
    public PlaneVisibilityProperty(Localization localization,
            EuclidianSettings3D euclidianSettings) {
        super(localization, "ShowPlane");
        this.euclidianSettings = euclidianSettings;
    }

    @Override
    public boolean getValue() {
        return euclidianSettings.getShowPlate();
    }

    @Override
    public void setValue(boolean value) {
        euclidianSettings.setShowPlate(value);
    }
}
