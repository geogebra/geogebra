package org.geogebra.common.properties.util;

import java.util.List;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public abstract class StringPropertyWithSuggestions extends AbstractValuedProperty<String>
		implements StringProperty {

	/**
	 * @param localization localization
	 * @param name name
	 */
	public StringPropertyWithSuggestions(Localization localization, String name) {
		super(localization, name);
	}

	/**
	 * @return list of suggested values
	 */
	public abstract List<String> getSuggestions();
}
