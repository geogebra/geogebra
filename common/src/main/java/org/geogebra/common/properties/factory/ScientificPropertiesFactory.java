package org.geogebra.common.properties.factory;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.general.AngleUnitProperty;
import org.geogebra.common.properties.impl.general.FontSizeProperty;
import org.geogebra.common.properties.impl.general.LanguageProperty;
import org.geogebra.common.properties.impl.general.RoundingProperty;

public class ScientificPropertiesFactory implements PropertiesFactory {

	@Override
	public PropertiesArray createGeneralProperties(
			App app,
			Localization localization,
			LanguageProperty.OnLanguageSetCallback onLanguageSetCallback) {
		Kernel kernel = app.getKernel();
		String name = localization.getMenu("General");
		return new PropertiesArray(name,
				new AngleUnitProperty(kernel, localization),
				new RoundingProperty(app, localization),
				new FontSizeProperty(app, localization),
				new LanguageProperty(app, localization, onLanguageSetCallback));
	}

	@Override
	public PropertiesArray createAlgebraProperties(App app, Localization localization) {
		return null;
	}

	@Override
	public PropertiesArray createGraphicsProperties(App app, Localization localization) {
		return null;
	}
}
