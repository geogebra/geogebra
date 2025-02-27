package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.TextFormatterDelegate;

public class UnderlineProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {
	private final GeoElementDelegate delegate;

	/**
	 * Underline property
	 * @param localization localization
	 * @param element element
	 */
	public UnderlineProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Underline");
		delegate = new TextFormatterDelegate(element);
	}

	@Override
	protected void doSetValue(Boolean value) {
		HasTextFormatter element = (HasTextFormatter) delegate.getElement();
		if (getLocalization() != null && !value.equals(element.getFormatter()
				.getFormat("underline", false))) {
			element.getFormatter().format("underline", value);
		}
		((GeoElement) element).updateVisualStyle(GProperty.COMBINED);
	}

	@Override
	public Boolean getValue() {
		HasTextFormatter element = (HasTextFormatter) delegate.getElement();
		return element.getFormatter().getFormat("underline", false);
	}
}
