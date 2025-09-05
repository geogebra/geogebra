package org.geogebra.common.properties.impl.objects;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class CaptionProperty extends AbstractValuedProperty<String> implements StringProperty {
	private final GeoElement element;

	/**
	 * @param localization this is used to localize the name
	 * @param element the construction element
	 */
	public CaptionProperty(Localization localization, GeoElement element) {
		super(localization, "Caption");
		this.element = element;
	}

	@Override
	public @CheckForNull String validateValue(String value) {
		return null;
	}

	@Override
	protected void doSetValue(String value) {
		element.setCaption(value);
	}

	@Override
	public String getValue() {
		return element.getRawCaption();
	}
}
