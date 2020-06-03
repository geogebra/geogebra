package org.geogebra.common.properties.impl.objects;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.properties.AbstractNumericProperty;
import org.geogebra.common.properties.GeoElementProperty;
import org.geogebra.common.properties.IntegerProperty;
import org.geogebra.common.properties.util.GeoListPropertyHelper;
import org.geogebra.common.properties.util.GeoPropertyDelegate;

/**
 * Property for triangle size of the Slope() command output
 **/
public class SlopeSizeProperty extends AbstractNumericProperty<Integer> implements IntegerProperty,
        GeoElementProperty, GeoPropertyDelegate<Integer> {

    private GeoListPropertyHelper<Integer> propertyHelper;
    private GeoElementSlopeSizeProperty geoElementSlopeSizeProperty;

    private static class GeoElementSlopeSizeProperty extends AbstractGeoElementProperty {

        GeoElementSlopeSizeProperty(GeoElement geoElement) {
            super("Size", geoElement);
        }

        @Override
        boolean isApplicableTo(GeoElement element) {
            if (isTextOrInput(element)) {
                return false;
            }
            if (element instanceof GeoList) {
                return isApplicableToGeoList((GeoList) element);
            }
            return true;
        }
    }

    /**
     * Creates a new slope size property.
     *
     * @param app app
     */
    public SlopeSizeProperty(App app) {
        super(app.getLocalization(), "Size");
        propertyHelper = new GeoListPropertyHelper<>(app, this);
    }

    /**
     * @param slope slope
     */
    public SlopeSizeProperty(GeoNumeric slope) {
        this(slope.getApp());
        setSlope(slope);
        if (!isApplicableTo(slope)) {
            throw new NotApplicablePropertyException(slope, this);
        }
    }

    private void setSlope(GeoNumeric element) {
        List<GeoElementND> list = new ArrayList<>();
        list.add(element);
        propertyHelper.setGeoElements(list);
    }

    /**
     * @param geoElements geoElements
     */
    public void setGeoElements(List<GeoElementND> geoElements) {
        propertyHelper.setGeoElements(geoElements);
    }

    @Override
    protected void setValueSafe(Integer value) {
        propertyHelper.setValue(value);
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
    public Integer getValue() {
        return propertyHelper.getValue();
    }

    @Override
    public boolean isEnabled() {
        return propertyHelper.isEnabled();
    }

    @Override
    public Integer getPropertyValue(GeoElementND element) {
        return ((GeoNumeric) element).getSlopeTriangleSize();
    }

    @Override
    public void setPropertyValue(GeoElementND element, Integer value) {
        ((GeoNumeric) element).setSlopeTriangleSize(value);
        element.updateVisualStyleRepaint(GProperty.COMBINED);
    }

    @Override
    public boolean hasProperty(GeoElementND element) {
        return Algos.isUsedFor(Commands.Slope, element);
    }

    private boolean isApplicableTo(GeoElement element) {
        return isEnabled() && getGeoElementSlopeSizeProperty(element).isApplicableTo(element);
    }

    private GeoElementSlopeSizeProperty getGeoElementSlopeSizeProperty(GeoElement element) {
        if (geoElementSlopeSizeProperty == null) {
            geoElementSlopeSizeProperty = new GeoElementSlopeSizeProperty(element);
        }
        return geoElementSlopeSizeProperty;
    }
}
