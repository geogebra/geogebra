package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.properties.ElementProperty;
import org.geogebra.common.properties.NumericProperty;

/**
 * Property for triangle size of the Slope() command output
 **/
public class SlopeSizeProperty extends ElementProperty<Integer> implements NumericProperty {

    /**
     * Constructs an abstract property.
     *
     * @param app for localization and notifications
     */
    public SlopeSizeProperty(App app) {
        super(app, "Size");
    }

    @Override
    public Integer getValue(GeoElementND geo) {
        return ((GeoNumeric) geo).getSlopeTriangleSize();
    }

    @Override
    public Integer getDefaultValue() {
        return 1;
    }

    @Override
    public void setValue(GeoElementND geo, Integer value) {
        ((GeoNumeric) geo).setSlopeTriangleSize(value + getMin());
        geo.updateVisualStyleRepaint(GProperty.COMBINED);
    }

    @Override
    public boolean isEnabled(GeoElementND geo) {
        return Algos.isUsedFor(Commands.Slope, geo);
    }

    @Override
    public int getMin() {
        return 1;
    }

    @Override
    public int getMax() {
        return 10;
    }
}
