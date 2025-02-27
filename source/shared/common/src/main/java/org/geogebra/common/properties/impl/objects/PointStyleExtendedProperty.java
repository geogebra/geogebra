package org.geogebra.common.properties.impl.objects;

import java.util.List;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.PointStylePropertyDelegate;

/**
 * Point style
 */
public class PointStyleExtendedProperty extends AbstractEnumeratedProperty<Integer>
		implements IconsEnumeratedProperty<Integer> {

	private static final PropertyResource[] icons = {
			PropertyResource.ICON_POINT_STYLE_DOT,
			PropertyResource.ICON_POINT_STYLE_CIRCLE,
			PropertyResource.ICON_POINT_STYLE_FILLED_DIAMOND,
			PropertyResource.ICON_POINT_STYLE_EMPTY_DIAMOND,
			PropertyResource.ICON_POINT_STYLE_CROSS,
			PropertyResource.ICON_POINT_STYLE_PLUS,
			PropertyResource.ICON_POINT_STYLE_NO_OUTLINE,
			PropertyResource.ICON_POINT_STYLE_TRIANGLE_NORTH,
			PropertyResource.ICON_POINT_STYLE_TRIANGLE_SOUTH,
			PropertyResource.ICON_POINT_STYLE_TRIANGLE_WEST,
			PropertyResource.ICON_POINT_STYLE_TRIANGLE_EAST
	};

	private final GeoElementDelegate delegate;

	/***/
	public PointStyleExtendedProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "PointStyle");
		delegate = new PointStylePropertyDelegate(element);
		setValues(List.of(
				EuclidianStyleConstants.POINT_STYLE_DOT,
				EuclidianStyleConstants.POINT_STYLE_CIRCLE,
				EuclidianStyleConstants.POINT_STYLE_FILLED_DIAMOND,
				EuclidianStyleConstants.POINT_STYLE_EMPTY_DIAMOND,
				EuclidianStyleConstants.POINT_STYLE_CROSS,
				EuclidianStyleConstants.POINT_STYLE_PLUS,
				EuclidianStyleConstants.POINT_STYLE_NO_OUTLINE,
				EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH,
				EuclidianStyleConstants.POINT_STYLE_TRIANGLE_SOUTH,
				EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST,
				EuclidianStyleConstants.POINT_STYLE_TRIANGLE_EAST
		));
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return icons;
	}

	@Override
	protected void doSetValue(Integer value) {
		GeoElement element = delegate.getElement();
		if (element instanceof PointProperties) {
			((PointProperties) element).setPointStyle(value);
			element.updateVisualStyleRepaint(GProperty.POINT_STYLE);
		}
	}

	@Override
	public Integer getValue() {
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
