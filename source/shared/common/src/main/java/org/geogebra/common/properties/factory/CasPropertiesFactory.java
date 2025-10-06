package org.geogebra.common.properties.factory;

import static org.geogebra.common.properties.factory.PropertiesRegistration.registerProperties;

import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.impl.general.CoordinatesProperty;
import org.geogebra.common.properties.impl.general.FontSizeProperty;
import org.geogebra.common.properties.impl.general.LanguageProperty;
import org.geogebra.common.properties.impl.general.RoundingIndexProperty;
import org.geogebra.common.util.NonNullList;

public class CasPropertiesFactory extends DefaultPropertiesFactory {

	@Override
	protected PropertiesArray createGeneralProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry) {
		Kernel kernel = app.getKernel();
		Settings settings = app.getSettings();
		return new PropertiesArray("General", localization,
				PreviewFeature.isAvailable(PreviewFeature.SETTINGS_VIEW)
				? registerProperties(propertiesRegistry, NonNullList.of(
						new LanguageProperty(app, localization),
						new RoundingIndexProperty(app, localization),
						new CoordinatesProperty(kernel, localization),
						new FontSizeProperty(localization, settings.getFontSettings(),
								app.getFontSettingsUpdater()),
						app.getPlatform().isMobile() ? null : createSaveRestoreSettingsProperties(
								app, localization)))
				: registerProperties(propertiesRegistry, List.of(
						new LanguageProperty(app, localization),
						new RoundingIndexProperty(app, localization),
						new CoordinatesProperty(kernel, localization),
						new FontSizeProperty(localization, settings.getFontSettings(),
								app.getFontSettingsUpdater()))));
	}
}
