package org.geogebra.common.properties.impl.objects;

import java.util.List;

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
	 * Constructs an AbstractEnumeratedProperty.
	 * @param localization the localization used
	 * @param element the name of the property
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
	protected void doSetValue(VerticalAlignment value) {
		HasTextFormatter element = (HasTextFormatter) delegate.getElement();
		if (getLocalization() != null && !value.equals(element.getFormatter()
				.getVerticalAlignment())) {
			element.getFormatter().setVerticalAlignment(value);
		}
		((GeoElement) element).updateVisualStyle(GProperty.COMBINED);
	}

	@Override
	public VerticalAlignment getValue() {
		return ((HasTextFormatter) delegate.getElement()).getFormatter().getVerticalAlignment();
	}
}
