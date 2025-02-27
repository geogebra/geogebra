package org.geogebra.common.properties.impl.objects;

import java.util.List;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public abstract class FlagListProperty extends AbstractValuedProperty<List<Boolean>> {

	/**
	 * Constructs an abstract property.
	 * @param localization this is used to localize the name
	 * @param name the name to be localized
	 */
	public FlagListProperty(Localization localization, String name) {
		super(localization, name);
	}

	public abstract List<String> getFlagNames();
}
