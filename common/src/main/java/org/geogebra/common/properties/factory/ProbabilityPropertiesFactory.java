package org.geogebra.common.properties.factory;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.general.LanguageProperty;

public class ProbabilityPropertiesFactory extends DefaultPropertiesFactory {
	@Override
	public List<PropertiesArray> createProperties(App app, Localization localization,
			LanguageProperty.OnLanguageSetCallback onLanguageSetCallback) {
		return Arrays.asList(createGeneralProperties(app, localization, onLanguageSetCallback));
	}
}
