package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.AbstractProperty;
import org.geogebra.common.properties.StringProperty;

/**
 * This property controls the label on an axis.
 */
public class AxisLabelProperty extends AbstractProperty implements StringProperty {

    private EuclidianSettings euclidianSettings;
    private int axis;

    /**
     * Constructs an xAxis property.
     * @param localization localization for the title
     * @param euclidianSettings euclidian settings
     * @param axis the axis for label
     */
    public AxisLabelProperty(Localization localization, EuclidianSettings euclidianSettings, String label, int axis) {
        super(localization, label);
        this.euclidianSettings = euclidianSettings;
        this.axis = axis;
    }

    @Override
    public String getValue() {
        return euclidianSettings.getAxesLabels()[axis];
    }

    @Override
    public void setValue(String value) {
        euclidianSettings.setAxisLabel(axis, value);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
