package org.geogebra.common.properties.factory;

import static org.geogebra.common.properties.factory.PropertiesRegistration.registerProperties;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.impl.general.CoordinatesProperty;
import org.geogebra.common.properties.impl.general.FontSizeProperty;
import org.geogebra.common.properties.impl.general.LabelingProperty;
import org.geogebra.common.properties.impl.general.LanguageProperty;
import org.geogebra.common.properties.impl.general.RoundingIndexProperty;

public class CasPropertiesFactory extends DefaultPropertiesFactory {

	@Override
	protected PropertiesArray createGeneralProperties(
			App app,
			Localization localization,
			PropertiesRegistry propertiesRegistry) {
		Kernel kernel = app.getKernel();
		String name = localization.getMenu("General");
		Settings settings = app.getSettings();
		return new PropertiesArray(name,
				registerProperties(propertiesRegistry,
						new RoundingIndexProperty(app, localization),
						new LabelingProperty(localization, settings.getLabelSettings()),
						new CoordinatesProperty(kernel, localization),
						new FontSizeProperty(
								localization,
								settings.getFontSettings(),
								app.getSettingsUpdater().getFontSettingsUpdater()),
						new LanguageProperty(app, localization)));
	}
}
