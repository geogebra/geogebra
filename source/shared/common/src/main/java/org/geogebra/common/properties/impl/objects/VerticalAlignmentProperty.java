package org.geogebra.common.properties.impl.objects;

import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.kernel.geos.properties.VerticalAlignment;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.TextFormatterDelegate;

public class VerticalAlignmentProperty extends AbstractEnumeratedProperty<VerticalAlignment>
		implements IconsEnumeratedProperty<VerticalAlignment> {

	private static final PropertyResource[] icons = {
			PropertyResource.ICON_ALIGNMENT_TOP, PropertyResource.ICON_ALIGNMENT_MIDDLE,
			PropertyResource.ICON_ALIGNMENT_BOTTOM
	};

	private final GeoElementDelegate delegate;

	/**
	 * @param localization the localization used
	 * @param element the element
	 */
	public VerticalAlignmentProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "stylebar.VerticalAlign");
		delegate = new TextFormatterDelegate(element);
		setValues(List.of(VerticalAlignment.TOP,
				VerticalAlignment.MIDDLE,
				VerticalAlignment.BOTTOM));
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
	protected void doSetValue(VerticalAlignment value) {
		HasTextFormatter element = (HasTextFormatter) delegate.getElement();
		HasTextFormat formatter = element.getFormatter();
		if (getLocalization() != null && formatter != null && !value.equals(formatter
				.getVerticalAlignment())) {
			formatter.setVerticalAlignment(value);
		}
		((GeoElement) element).updateVisualStyle(GProperty.COMBINED);
	}

	@Override
	public VerticalAlignment getValue() {
		HasTextFormatter element = (HasTextFormatter) delegate.getElement();
		HasTextFormat formatter = element.getFormatter();
		if (formatter != null) {
			return formatter.getVerticalAlignment();
		}
		return VerticalAlignment.BOTTOM;
	}
}
