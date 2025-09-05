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
import org.geogebra.common.properties.impl.general.FontSizeProperty;
import org.geogebra.common.properties.impl.general.LanguageProperty;
import org.geogebra.common.properties.impl.graphics.AxesBoldProperty;
import org.geogebra.common.properties.impl.graphics.AxesColorProperty;
import org.geogebra.common.properties.impl.graphics.AxesLineStyleProperty;
import org.geogebra.common.properties.impl.graphics.AxesVisibilityProperty;
import org.geogebra.common.properties.impl.graphics.BackgroundPropertyCollection;
import org.geogebra.common.properties.impl.graphics.LabelStylePropertyCollection;
import org.geogebra.common.properties.impl.graphics.RulingPropertiesCollection;

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
					? registerProperties(propertiesRegistry,
							new LanguageProperty(app, localization),
							new FontSizeProperty(localization, app.getSettings().getFontSettings(),
									app.getFontSettingsUpdater()),
							createSaveRestoreSettingsProperties(app, localization))
					: registerProperties(propertiesRegistry,
							new LanguageProperty(app, localization),
							new FontSizeProperty(localization, app.getSettings().getFontSettings(),
									app.getFontSettingsUpdater())));
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
						axisExpandableProperty(0, "xAxis", app, localization, activeView),
						axisExpandableProperty(1, "yAxis", app, localization, activeView))
		);
	}
}
