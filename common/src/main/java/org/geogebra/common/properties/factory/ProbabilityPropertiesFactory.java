package org.geogebra.common.properties.factory;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertiesRegistry;

public class ProbabilityPropertiesFactory extends DefaultPropertiesFactory {
	@Override
	public List<PropertiesArray> createProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry) {
		return Arrays.asList(createGeneralProperties(app, localization, propertiesRegistry));
	}
}
