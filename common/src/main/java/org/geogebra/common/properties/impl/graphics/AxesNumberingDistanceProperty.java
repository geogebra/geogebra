package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.AbstractProperty;
import org.geogebra.common.properties.BooleanProperty;

/**
 * This property controls the distance of the axes numbering.
 */
public class AxesNumberingDistanceProperty extends AbstractProperty implements BooleanProperty {

    private EuclidianSettings euclidianSettings;
    private App app;

    /**
     * Constructs an Axes numbering distance property.
     *
     * @param localization localization for the title
     */
    AxesNumberingDistanceProperty(Localization localization, EuclidianSettings
            euclidianSettings, App app) {
        super(localization, "Automatic");
        this.euclidianSettings = euclidianSettings;
        this.app = app;
    }

    @Override
    public boolean getValue() {
        boolean[] axesAutomaticDistances = euclidianSettings.getAutomaticAxesNumberingDistances();

        for (boolean auto : axesAutomaticDistances) {
            if (!auto) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void setValue(boolean automatic) {
        if (automatic) {
            setAutoDistance();
        } else {
            setCustomDistance();
        }
    }

    private void setCustomDistance() {
        int length = euclidianSettings.getAutomaticAxesNumberingDistances().length;
        for (int i = 0; i < length; i++) {
            euclidianSettings.setAxisNumberingDistance(i, app.getKernel().getAlgebraProcessor()
                    .evaluateToNumeric("0.5", ErrorHelper.silent()));
        }
    }

    private void setAutoDistance() {
        int length = euclidianSettings.getAutomaticAxesNumberingDistances().length;
        for (int i = 0; i < length; i++) {
            euclidianSettings.setAutomaticAxesNumberingDistance(true, i, true);
        }
    }
}
