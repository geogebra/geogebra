package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.properties.AbstractProperty;
import org.geogebra.common.properties.BooleanProperty;

public class PlaneVisibilityProperty extends AbstractProperty implements BooleanProperty {

    private EuclidianSettings3D euclidianSettings;

    /**
     * Constructs an abstract property.
     *
     * @param localization      this is used to localize the name
     * @param euclidianSettings euclidian settings
     */
    public PlaneVisibilityProperty(Localization localization, EuclidianSettings euclidianSettings) {
        super(localization, "ShowPlane");
        this.euclidianSettings = (EuclidianSettings3D) euclidianSettings;
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
