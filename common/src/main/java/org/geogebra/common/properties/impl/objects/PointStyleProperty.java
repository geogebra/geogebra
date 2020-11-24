package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.IconsEnumerableProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumerableProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.PointStylePropertyDelegate;

/**
 * Point style
 */
public class PointStyleProperty extends AbstractEnumerableProperty
		implements IconsEnumerableProperty {

	private static final PropertyResource[] icons = {
			PropertyResource.ICON_POINT_STYLE_DOT, PropertyResource.ICON_POINT_STYLE_CROSS,
			PropertyResource.ICON_POINT_STYLE_CIRCLE, PropertyResource.ICON_POINT_STYLE_PLUS,
			PropertyResource.ICON_POINT_STYLE_FILLED_DIAMOND
	};

	private final GeoElementDelegate delegate;

	/***/
	public PointStyleProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Properties.Style");
		delegate = new PointStylePropertyDelegate(element);
		setValues(new String[icons.length]);
	}

	@Override
	public PropertyResource[] getIcons() {
		return icons;
	}

	@Override
	protected void setValueSafe(String value, int index) {
		GeoElement element = delegate.getElement();
		if (element instanceof PointProperties) {
			((PointProperties) element).setPointStyle(index);
			element.updateVisualStyleRepaint(GProperty.POINT_STYLE);
		}
	}

	@Override
	public int getIndex() {
		GeoElement element = delegate.getElement();
		if (element instanceof PointProperties) {
			int pointStyle = ((PointProperties) element).getPointStyle();
			return pointStyle >= icons.length ? 0 : pointStyle;
		}
		return -1;
	}

	@Override
	public boolean isEnabled() {
		return delegate.isEnabled();
	}
}
