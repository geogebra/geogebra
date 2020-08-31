package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.StringProperty;
import org.geogebra.common.properties.impl.AbstractProperty;

/**
 * This property controls the label on an axis.
 */
public class AxisLabelProperty extends AbstractProperty
        implements StringProperty {

    private EuclidianSettings euclidianSettings;
    private int axis;

    /**
     * Constructs an xAxis property.
     *
     * @param localization      localization for the title
     * @param euclidianSettings euclidian settings
     * @param label             the name of the axis
     * @param axis              the axis for label
     */
    public AxisLabelProperty(Localization localization,
            EuclidianSettings euclidianSettings, String label, int axis) {
        super(localization, label);
        this.euclidianSettings = euclidianSettings;
        this.axis = axis;
    }

    @Override
    public String getValue() {
        if (!isEnabled()) {
            return EuclidianSettings.getDefaultAxisLabel(axis);
        }
        String axisLabel = euclidianSettings.getAxesLabels()[axis];
        return axisLabel == null ? "" : axisLabel;
    }

    @Override
    public void setValue(String value) {
        euclidianSettings.setAxisLabel(axis, value);
    }

    @Override
    public boolean isValid(String value) {
        return true;
    }

    @Override
    public boolean isEnabled() {
        String[] labels = euclidianSettings.getAxesLabels();
        boolean enabled = false;
        for (int i = 0; i < euclidianSettings.getDimension(); i++) {
            enabled |= labels[i] != null;
        }
        return enabled;
    }
}
