package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.dialog.options.model.PointStyleModel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.RangeProperty;

/**
 * Point size
 */
public class PointSize extends AbstractGeoElementProperty implements RangeProperty<Integer> {

    public PointSize(GeoElement geoElement) {
        super("Size", geoElement);
    }

    @Override
    public Integer getMin() {
        return 1;
    }

    @Override
    public Integer getMax() {
        return 9;
    }

    @Override
    public Integer getValue() {
        if (getElement() instanceof PointProperties) {
            return ((PointProperties) getElement()).getPointSize();
        }
        return EuclidianStyleConstants.DEFAULT_POINT_SIZE;
    }

    @Override
    public void setValue(Integer size) {
        GeoElement element = getElement();
        setSize(element, size);
        element.updateRepaint();
        element.getApp().setPropertiesOccured();
    }

    private void setSize(GeoElement element, int size) {
        if (element instanceof GeoList) {
            GeoList list = (GeoList) element;
            for (int i = 0; i < list.size(); i++) {
                setSize(list.get(i), size);
            }
        } else if (element instanceof PointProperties) {
            ((PointProperties) element).setPointSize(size);
        }
    }

    @Override
    public Integer getStep() {
        return 1;
    }

    @Override
    public boolean isApplicableTo(GeoElement element) {
        if (isTextOrInput(element)) {
            return false;
        }
        if (element instanceof GeoList) {
            return isApplicableTo(element);
        }
        EuclidianView euclidianView = element.getApp().getActiveEuclidianView();
        return PointStyleModel.match(element) && euclidianView.canShowPointStyle();
    }
}
