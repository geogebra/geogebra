/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.properties.factory;

import static org.geogebra.common.properties.factory.PropertiesRegistration.registerProperties;

import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.impl.general.AngleUnitProperty;
import org.geogebra.common.properties.impl.general.AppFontSizeProperty;
import org.geogebra.common.properties.impl.general.LanguageProperty;
import org.geogebra.common.properties.impl.general.RoundingIndexProperty;
import org.geogebra.common.util.NonNullList;

public class ScientificPropertiesFactory extends DefaultPropertiesFactory {

	@Override
	public List<PropertiesArray> createProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry) {
		return List.of(createGeneralProperties(app, localization, propertiesRegistry));
	}

	@Override
	protected PropertiesArray createGeneralProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry) {
		Kernel kernel = app.getKernel();
		return new PropertiesArray("General", localization,
				PreviewFeature.isAvailable(PreviewFeature.SETTINGS_VIEW)
				? registerProperties(propertiesRegistry, NonNullList.of(
						new LanguageProperty(app, localization),
						new RoundingIndexProperty(app, localization),
						new AngleUnitProperty(kernel, localization),
						new AppFontSizeProperty(localization, app.getSettings().getFontSettings(),
								app.getFontSettingsUpdater()),
						app.getPlatform().isMobile() ? null : createSaveRestoreSettingsProperties(
								app, localization)))
				: registerProperties(propertiesRegistry, List.of(
						new LanguageProperty(app, localization),
						new RoundingIndexProperty(app, localization),
						new AngleUnitProperty(kernel, localization),
						new AppFontSizeProperty(localization, app.getSettings().getFontSettings(),
								app.getFontSettingsUpdater()))));
	}
}
