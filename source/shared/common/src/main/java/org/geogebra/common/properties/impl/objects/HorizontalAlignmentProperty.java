package org.geogebra.common.properties.impl.objects;

import java.util.List;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.kernel.geos.properties.HorizontalAlignment;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.TextFormatterDelegate;

public class HorizontalAlignmentProperty extends AbstractEnumeratedProperty<HorizontalAlignment>
		implements IconsEnumeratedProperty<HorizontalAlignment> {

	private static final PropertyResource[] icons = {
			PropertyResource.ICON_ALIGNMENT_LEFT, PropertyResource.ICON_ALIGNMENT_CENTER,
			PropertyResource.ICON_ALIGNMENT_RIGHT
	};

	private final GeoElementDelegate delegate;

	/**
	 * Constructs an AbstractEnumeratedProperty.
	 * @param localization the localization used
	 * @param element the name of the property
	 */
	public HorizontalAlignmentProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "stylebar.HorizontalAlign");
		delegate = new TextFormatterDelegate(element);
		setValues(List.of(HorizontalAlignment.LEFT,
				HorizontalAlignment.CENTER,
				HorizontalAlignment.RIGHT));
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return icons;
	}

	@Override
	protected void doSetValue(HorizontalAlignment value) {
		HasTextFormatter element = (HasTextFormatter) delegate.getElement();
		if (getLocalization() != null && !value.equals(element.getFormatter()
				.getHorizontalAlignment())) {
			element.getFormatter().setHorizontalAlignment(value);
		}
		((GeoElement) element).updateVisualStyle(GProperty.COMBINED);
	}

	@Override
	public HorizontalAlignment getValue() {
		return ((HasTextFormatter) delegate.getElement()).getFormatter().getHorizontalAlignment();
	}
}
