package org.geogebra.common.properties.factory;

import static org.geogebra.common.properties.factory.PropertiesRegistration.registerProperties;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.impl.general.AngleUnitProperty;
import org.geogebra.common.properties.impl.general.FontSizeProperty;
import org.geogebra.common.properties.impl.general.LanguageProperty;
import org.geogebra.common.properties.impl.general.RoundingIndexProperty;

public class ScientificPropertiesFactory implements PropertiesFactory {

	@Override
	public List<PropertiesArray> createProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry) {
		return Arrays.asList(createGeneralProperties(app, localization, propertiesRegistry));
	}

	private PropertiesArray createGeneralProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry) {
		Kernel kernel = app.getKernel();
		String name = localization.getMenu("General");
		return new PropertiesArray(name,
				registerProperties(propertiesRegistry,
						new AngleUnitProperty(kernel, localization),
						new RoundingIndexProperty(app, localization),
						new FontSizeProperty(
								localization,
								app.getSettings().getFontSettings(),
								app.getSettingsUpdater().getFontSettingsUpdater()),
						new LanguageProperty(app, localization)));
	}
}
