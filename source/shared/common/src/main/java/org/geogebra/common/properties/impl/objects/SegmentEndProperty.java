package org.geogebra.common.properties.impl.objects;

import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasSegmentStyle;
import org.geogebra.common.kernel.geos.SegmentStyle;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.SegmentPropertyDelegate;

public class SegmentEndProperty extends AbstractEnumeratedProperty<SegmentStyle>
		implements IconsEnumeratedProperty<SegmentStyle> {

	private static final PropertyResource[] icons = {
			PropertyResource.ICON_SEGMENT_END_DEFAULT,
			PropertyResource.ICON_SEGMENT_END_LINE,
			PropertyResource.ICON_SEGMENT_END_ARROW,
			PropertyResource.ICON_SEGMENT_END_CROWS_FOOT,
			PropertyResource.ICON_SEGMENT_END_ARROW_OUTLINE,
			PropertyResource.ICON_SEGMENT_END_ARROW_FILLED,
			PropertyResource.ICON_SEGMENT_END_CIRCLE_OUTLINE,
			PropertyResource.ICON_SEGMENT_END_CIRCLE,
			PropertyResource.ICON_SEGMENT_END_SQUARE_OUTLINE,
			PropertyResource.ICON_SEGMENT_END_SQUARE,
			PropertyResource.ICON_SEGMENT_END_DIAMOND_OUTLINE,
			PropertyResource.ICON_SEGMENT_END_DIAMOND
	};

	private final GeoElementDelegate delegate;

	/**
	 * @param localization the localization used
	 * @param element the element
	 */
	public SegmentEndProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "stylebar.LineEndStyle");
		delegate = new SegmentPropertyDelegate(element);
		setValues(List.of(SegmentStyle.DEFAULT, SegmentStyle.LINE,
				SegmentStyle.ARROW, SegmentStyle.CROWS_FOOT,
				SegmentStyle.ARROW_OUTLINE, SegmentStyle.ARROW_FILLED,
				SegmentStyle.CIRCLE_OUTLINE, SegmentStyle.CIRCLE,
				SegmentStyle.SQUARE_OUTLINE, SegmentStyle.SQUARE,
				SegmentStyle.DIAMOND_OUTLINE, SegmentStyle.DIAMOND));
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return icons;
	}

	@Override
	public @CheckForNull String[] getToolTipLabels() {
		return null;
	}

	@Override
	protected void doSetValue(SegmentStyle value) {
		HasSegmentStyle element = (HasSegmentStyle) delegate.getElement();
		element.setEndStyle(value);
		element.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public SegmentStyle getValue() {
		return ((HasSegmentStyle) delegate.getElement()).getEndStyle();
	}
}

