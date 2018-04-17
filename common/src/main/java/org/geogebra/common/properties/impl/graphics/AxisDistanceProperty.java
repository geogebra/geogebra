package org.geogebra.common.properties.impl.graphics;

import com.himamis.retex.editor.share.util.Unicode;

import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.AbstractProperty;
import org.geogebra.common.properties.StringProperty;
import org.geogebra.common.util.NumberFormatAdapter;

/**
 * This property controls the distance of an axis numbering
 */
public class AxisDistanceProperty extends AbstractProperty implements StringProperty {

    private static final double EPS = 0.00001;

    private EuclidianSettings euclidianSettings;
    private App app;
    private int axis;
    private NumberFormatAdapter numberFormatter;

    /**
     * Constructs an xAxis property.
     *
     * @param localization      localization for the title
     * @param euclidianSettings euclidian settings
     * @param app               App
     * @param label             label of the axis
     * @param axis              the axis for the numbering distance will be set
     */
    AxisDistanceProperty(Localization localization, EuclidianSettings
            euclidianSettings, App app, String label, int axis) {
        super(localization, label);
        this.euclidianSettings = euclidianSettings;
        this.app = app;
        this.axis = axis;
        this.numberFormatter = FormatFactory.getPrototype().getNumberFormat(2);
    }

    @Override
    public String getValue() {
        if (euclidianSettings.getAxisNumberingDistance(axis) != null) {
            return getFormatted(euclidianSettings.getAxisNumberingDistance(axis).getDouble());
        } else {
            return getFormatted(app.getActiveEuclidianView().getAxesNumberingDistances()[axis] /
                    2);
        }
    }

    @Override
    public void setValue(String value) {
        GeoNumberValue distance = !value.trim().equals("") ? getNumberValue(value) : null;
        euclidianSettings.setAxisNumberingDistance(axis, distance);
    }

    @Override
    public boolean isValid(String value) {
        return getNumberValue(value) != null;
    }

    @Override
    public boolean isEnabled() {
        boolean[] axesAutomaticDistances = euclidianSettings.getAutomaticAxesNumberingDistances();

        for (boolean auto : axesAutomaticDistances) {
            if (!auto) {
                return true;
            }
        }
        return false;
    }

    private GeoNumberValue getNumberValue(String value) {
        return app.getKernel().getAlgebraProcessor()
                .evaluateToNumeric(value.trim(), ErrorHelper.silent());
    }

    private String getFormatted(double distance) {
        if (distance == 0) {
            return numberFormatter.format(0);
        } else if (equals(distance, Math.PI)) {
            return Unicode.PI_STRING;
        } else if (distance % Math.PI == 0) {
            return numberFormatter.format(distance / Math.PI) + Unicode.PI_STRING;
        } else if (equals(distance, Math.PI / 2)) {
            return Unicode.PI_STRING + "/2";
        } else if (equals(distance, Math.PI / 4)) {
            return Unicode.PI_STRING + "/4";
        } else {
            return numberFormatter.format(distance);
        }
    }

    private boolean equals(double d1, double d2) {
        return Math.abs(d1 - d2) < EPS;
    }
}
