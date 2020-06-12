package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.dialog.options.model.PointStyleModel;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.properties.IconsEnumerableProperty;
import org.geogebra.common.properties.PropertyResource;

/**
 * Point style
 */
public class PointStyleProperty
		extends AbstractGeoElementProperty implements IconsEnumerableProperty {

	private static final PropertyResource[] icons = {
			PropertyResource.ICON_POINT_STYLE_DOT, PropertyResource.ICON_POINT_STYLE_CROSS,
			PropertyResource.ICON_POINT_STYLE_CIRCLE, PropertyResource.ICON_POINT_STYLE_PLUS,
			PropertyResource.ICON_POINT_STYLE_FILLED_DIAMOND
	};

	public PointStyleProperty(GeoElement geoElement) throws NotApplicablePropertyException {
		super("Properties.Style", geoElement);
	}

	@Override
	public String[] getValues() {
		return null;
	}

	@Override
	public int getIndex() {
		return getElement() instanceof GeoPoint ? ((GeoPoint) getElement()).getPointStyle() : -1;
	}

	@Override
	public void setIndex(int pointStyle) {
		GeoElement element = getElement();
		if (element instanceof PointProperties) {
			((PointProperties) element).setPointStyle(pointStyle);
			element.updateVisualStyleRepaint(GProperty.POINT_STYLE);
		}
	}

	@Override
	public PropertyResource[] getIcons() {
		return icons;
	}

	@Override
	boolean isApplicableTo(GeoElement element) {
		if (isTextOrInput(element)) {
			return false;
		}
		if (element instanceof GeoList) {
			return isApplicableToGeoList((GeoList) element);
		}
		EuclidianView euclidianView = element.getApp().getActiveEuclidianView();
		return PointStyleModel.match(element) && euclidianView.canShowPointStyle();
	}
}
