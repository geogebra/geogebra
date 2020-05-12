package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.geogebra3D.euclidian3D.ar.ARManagerInterface;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.AbstractProperty;
import org.geogebra.common.properties.BooleanProperty;

public class ARRatioProperty extends AbstractProperty implements BooleanProperty {

    private ARManagerInterface arManager;

    /**
     * Constructs an AR Ratio property.
     *
     * @param localization
     *            localization for the title
     * @param arManager
     *            AR Manager
     */
    ARRatioProperty(Localization localization, ARManagerInterface arManager) {
        super(localization, "Show");
        this.arManager = arManager;
    }

    @Override
    public boolean getValue() {
        return arManager.isRatioShown();
    }

    @Override
    public void setValue(boolean value) {
        arManager.setRatioIsShown(value);
    }
}
