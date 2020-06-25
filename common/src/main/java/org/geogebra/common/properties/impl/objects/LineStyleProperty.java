package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.IconsEnumerableProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumerableProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.LineStylePropertyDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Line style
 */
public class LineStyleProperty extends AbstractEnumerableProperty
		implements IconsEnumerableProperty {

	private static final PropertyResource[] icons = {
			PropertyResource.ICON_LINE_TYPE_FULL, PropertyResource.ICON_LINE_TYPE_DASHED_DOTTED,
			PropertyResource.ICON_LINE_TYPE_DASHED_LONG, PropertyResource.ICON_LINE_TYPE_DOTTED,
			PropertyResource.ICON_LINE_TYPE_DASHED_SHORT
	};

	private static final int[] lineTypes = new int[] { EuclidianStyleConstants.LINE_TYPE_FULL,
			EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED,
			EuclidianStyleConstants.LINE_TYPE_DASHED_LONG,
			EuclidianStyleConstants.LINE_TYPE_DOTTED,
			EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT};

	private final GeoElementDelegate delegate;

	/***/
	public LineStyleProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Properties.Style");
		delegate = new LineStylePropertyDelegate(element);
		setValues(new String[icons.length]);
	}

	@Override
	public PropertyResource[] getIcons() {
		return icons;
	}

	@Override
	protected void setValueSafe(String value, int index) {
		GeoElement element = delegate.getElement();
		element.setLineType(lineTypes[index]);
		element.updateVisualStyleRepaint(GProperty.LINE_STYLE);
	}

	@Override
	public int getIndex() {
		switch (delegate.getElement().getLineType()) {
			case EuclidianStyleConstants.LINE_TYPE_FULL:
				return 0;
			case EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED:
				return 1;
			case EuclidianStyleConstants.LINE_TYPE_DASHED_LONG:
				return 2;
			case EuclidianStyleConstants.LINE_TYPE_DOTTED:
				return 3;
			case EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT:
				return 4;
			default:
				return 0;
		}
	}

	@Override
	public boolean isEnabled() {
		return delegate.isEnabled();
	}
}
