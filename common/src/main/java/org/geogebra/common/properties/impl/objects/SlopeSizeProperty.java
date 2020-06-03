package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.properties.GeoElementProperty;
import org.geogebra.common.properties.RangeProperty;

/**
 * Property for triangle size of the Slope() command output
 **/
public class SlopeSizeProperty
		extends AbstractGeoElementProperty implements RangeProperty<Integer>, GeoElementProperty {

    /**
     * @param slope slope
     */
    public SlopeSizeProperty(GeoNumeric slope) {
        super("Size", slope);
        if (!isApplicableTo(slope)) {
            throw new NotApplicablePropertyException(slope, this);
        }
    }

    @Override
    GeoNumeric getElement() {
        return (GeoNumeric) super.getElement();
    }

    @Override
    public Integer getValue() {
        return getElement().getSlopeTriangleSize();
    }

    @Override
    public void setValue(Integer value) {
        GeoNumeric slope = getElement();
        slope.setSlopeTriangleSize(value);
        slope.updateVisualStyleRepaint(GProperty.COMBINED);
        slope.getApp().setPropertiesOccured();
    }

    @Override
    public Integer getMin() {
        return 1;
    }

    @Override
    public Integer getMax() {
        return 10;
    }

    @Override
    public Integer getStep() {
        return 1;
    }

    @Override
    public boolean isEnabled() {
        return Algos.isUsedFor(Commands.Slope, getElement());
    }

    @Override
    boolean isApplicableTo(GeoElement element) {
        if (!isEnabled() || isTextOrInput(element)) {
            return false;
        }
        if (element instanceof GeoList) {
            return isApplicableToGeoList((GeoList) element);
        }
        return true;
    }
}
