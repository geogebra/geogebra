package org.geogebra.common.properties.impl.objects;

import java.util.List;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.LineStylePropertyDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Line style
 */
public class LineStyleProperty extends AbstractEnumeratedProperty<Integer>
		implements IconsEnumeratedProperty<Integer> {

	private static final PropertyResource[] icons = {
			PropertyResource.ICON_LINE_TYPE_FULL, PropertyResource.ICON_LINE_TYPE_DASHED_DOTTED,
			PropertyResource.ICON_LINE_TYPE_DASHED_LONG, PropertyResource.ICON_LINE_TYPE_DOTTED,
			PropertyResource.ICON_LINE_TYPE_DASHED_SHORT
	};

	private final GeoElementDelegate delegate;

	/***/
	public LineStyleProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "LineStyle");
		delegate = new LineStylePropertyDelegate(element);
		setValues(List.of(
				EuclidianStyleConstants.LINE_TYPE_FULL,
				EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED,
				EuclidianStyleConstants.LINE_TYPE_DASHED_LONG,
				EuclidianStyleConstants.LINE_TYPE_DOTTED,
				EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT
		));
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return icons;
	}

	@Override
	protected void doSetValue(Integer value) {
		GeoElement element = delegate.getElement();
		element.setLineType(value);
		element.updateVisualStyleRepaint(GProperty.LINE_STYLE);
	}

	@Override
	public Integer getValue() {
		return delegate.getElement().getLineType();
	}

	@Override
	public boolean isEnabled() {
		return delegate.isEnabled();
	}
}
