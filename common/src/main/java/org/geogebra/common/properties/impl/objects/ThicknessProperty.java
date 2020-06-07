package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.gui.dialog.options.model.LineStyleModel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.properties.RangeProperty;

/**
 * Line thickness
 */
public class ThicknessProperty
        extends AbstractGeoElementProperty implements RangeProperty<Integer> {

    public ThicknessProperty(GeoElement geoElement) throws NotApplicablePropertyException {
        super("Thickness", geoElement);
    }

    @Override
    public Integer getMin() {
        return getElement().getMinimumLineThickness();
    }

    @Override
    public Integer getMax() {
        return 9;
    }

    @Override
    public Integer getValue() {
        return getElement().getLineThickness();
    }

    @Override
    public void setValue(Integer size) {
        GeoElement element = getElement();
        setThickness(element, size);
        element.updateRepaint();
    }

    private void setThickness(GeoElement element, int size) {
        if (element instanceof GeoList) {
            GeoList list = (GeoList) element;
            for (int i = 0; i < list.size(); i++) {
                setThickness(list.get(i), size);
            }
        } else if (LineStyleModel.match(element)) {
            element.setLineThickness(size);
        }
    }

    @Override
    public Integer getStep() {
        return 1;
    }

    @Override
    boolean isApplicableTo(GeoElement element) {
        if (isTextOrInput(element)) {
            return false;
        }
        if (element instanceof GeoList) {
            return isApplicableToGeoList((GeoList) element);
        }
        return element.showLineProperties();
    }
}
