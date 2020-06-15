package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.properties.IconsEnumerableProperty;
import org.geogebra.common.properties.PropertyResource;

/**
 * Line style
 */
public class LineStyleProperty
		extends AbstractGeoElementProperty implements IconsEnumerableProperty {

	private static final PropertyResource[] icons = {
			PropertyResource.ICON_LINE_TYPE_FULL, PropertyResource.ICON_LINE_TYPE_DASHED_DOTTED,
			PropertyResource.ICON_LINE_TYPE_DASHED_LONG, PropertyResource.ICON_LINE_TYPE_DOTTED,
			PropertyResource.ICON_LINE_TYPE_DASHED_SHORT
	};

	public LineStyleProperty(GeoElement geoElement) throws NotApplicablePropertyException {
		super("Properties.Style", geoElement);
	}

	@Override
	public String[] getValues() {
		return null;
	}

	@Override
	public int getIndex() {
		return getElement().getLineType();
	}

	@Override
	public void setIndex(int style) {
		GeoElement element = getElement();
		element.setLineType(style);
		element.updateVisualStyleRepaint(GProperty.LINE_STYLE);
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
		return element.showLineProperties();
	}
}
