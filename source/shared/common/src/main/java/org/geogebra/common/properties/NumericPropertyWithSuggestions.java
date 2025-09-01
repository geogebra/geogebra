package org.geogebra.common.properties;

import java.util.List;

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNumericProperty;

public abstract class NumericPropertyWithSuggestions extends AbstractNumericProperty {
	/**
	 * @param algebraProcessor algebra processor
	 * @param localization localization
	 * @param name name
	 */
	public NumericPropertyWithSuggestions(
			AlgebraProcessor algebraProcessor,
			Localization localization, String name) {
		super(algebraProcessor, localization, name);
	}

	/**
	 * @return list of suggested values
	 */
	public abstract List<String> getSuggestions();
}
