package org.geogebra.common.properties;

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNumericProperty;
import org.geogebra.common.properties.util.StringPropertyWithSuggestions;

public abstract class NumericPropertyWithSuggestions extends AbstractNumericProperty implements
		StringPropertyWithSuggestions {
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
}
