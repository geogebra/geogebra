package org.geogebra.common.properties.factory;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.general.AngleUnitProperty;
import org.geogebra.common.properties.impl.general.FontSizeProperty;
import org.geogebra.common.properties.impl.general.LanguageProperty;
import org.geogebra.common.properties.impl.general.RoundingProperty;

public class ScientificPropertiesFactory implements PropertiesFactory {

	@Override
	public List<PropertiesArray> createProperties(App app, Localization localization,
			LanguageProperty.OnLanguageSetCallback onLanguageSetCallback) {
		return Arrays.asList(createGeneralProperties(app, localization, onLanguageSetCallback));
	}

	private PropertiesArray createGeneralProperties(
			App app,
			Localization localization,
			LanguageProperty.OnLanguageSetCallback onLanguageSetCallback) {
		Kernel kernel = app.getKernel();
		String name = localization.getMenu("General");
		return new PropertiesArray(name,
				new AngleUnitProperty(kernel, localization),
				new RoundingProperty(app, localization),
				new FontSizeProperty(
						localization,
						app.getSettings().getFontSettings(),
						app.getSettingsUpdater().getFontSettingsUpdater()),
				new LanguageProperty(app, localization, onLanguageSetCallback));
	}
}
