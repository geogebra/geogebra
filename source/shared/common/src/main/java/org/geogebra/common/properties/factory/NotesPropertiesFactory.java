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

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.PropertyCollectionWithLead;
import org.geogebra.common.properties.impl.general.AppFontSizeProperty;
import org.geogebra.common.properties.impl.general.LanguageProperty;
import org.geogebra.common.properties.impl.graphics.AxesBoldProperty;
import org.geogebra.common.properties.impl.graphics.AxesColorProperty;
import org.geogebra.common.properties.impl.graphics.AxesLineStyleProperty;
import org.geogebra.common.properties.impl.graphics.AxesVisibilityProperty;
import org.geogebra.common.properties.impl.graphics.BackgroundPropertyCollection;
import org.geogebra.common.properties.impl.graphics.LabelStylePropertyCollection;
import org.geogebra.common.properties.impl.graphics.RulingPropertiesCollection;
import org.geogebra.common.util.NonNullList;

public class NotesPropertiesFactory extends DefaultPropertiesFactory {

	@Override
	public List<PropertiesArray> createProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry) {
		return Arrays.asList(
				createGeneralProperties(app, localization, propertiesRegistry),
				PreviewFeature.isAvailable(PreviewFeature.SETTINGS_VIEW)
						? createStructuredGraphicsProperties(app, localization, propertiesRegistry)
						: createGraphicsProperties(app, localization, propertiesRegistry));
	}

	@Override
	protected PropertiesArray createGeneralProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry) {
			return new PropertiesArray("General", localization,
					PreviewFeature.isAvailable(PreviewFeature.SETTINGS_VIEW)
					? registerProperties(propertiesRegistry, NonNullList.of(
							new LanguageProperty(app, localization),
							new AppFontSizeProperty(localization, app.getSettings()
									.getFontSettings(), app.getFontSettingsUpdater()),
							app.getPlatform().isMobile() ? null
									: createSaveRestoreSettingsProperties(app, localization)))
					: registerProperties(propertiesRegistry, List.of(
							new LanguageProperty(app, localization),
							new AppFontSizeProperty(localization, app.getSettings()
									.getFontSettings(), app.getFontSettingsUpdater()))));
	}

	@Override
	protected PropertiesArray createStructuredGraphicsProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry) {
		EuclidianView activeView = app.getActiveEuclidianView();
		EuclidianSettings euclidianSettings = activeView.getSettings();
		return new PropertiesArray("DrawingPad", localization,
				registerProperties(propertiesRegistry,
						new BackgroundPropertyCollection(localization, euclidianSettings),
						new RulingPropertiesCollection(localization, euclidianSettings, activeView),
						new PropertyCollectionWithLead(localization, "Axes",
								new AxesVisibilityProperty(localization, euclidianSettings),
								new AxesColorProperty(localization, euclidianSettings),
								new AxesLineStyleProperty(localization, euclidianSettings),
								new AxesBoldProperty(localization, euclidianSettings),
								new LabelStylePropertyCollection(localization, euclidianSettings)
						),
						axisExpandableProperty2D(0, "xAxis", app, localization, activeView),
						axisExpandableProperty2D(1, "yAxis", app, localization, activeView))
		);
	}
}
