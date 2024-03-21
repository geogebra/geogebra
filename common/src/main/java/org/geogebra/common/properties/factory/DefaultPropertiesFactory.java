package org.geogebra.common.properties.factory;

import static org.geogebra.common.properties.factory.PropertiesRegistration.registerProperties;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AlgebraSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.properties.PropertiesRegistry;
import org.geogebra.common.properties.impl.algebra.AlgebraDescriptionProperty;
import org.geogebra.common.properties.impl.algebra.ShowAuxiliaryProperty;
import org.geogebra.common.properties.impl.algebra.SortByProperty;
import org.geogebra.common.properties.impl.general.AngleUnitProperty;
import org.geogebra.common.properties.impl.general.CoordinatesProperty;
import org.geogebra.common.properties.impl.general.FontSizeProperty;
import org.geogebra.common.properties.impl.general.LabelingProperty;
import org.geogebra.common.properties.impl.general.LanguageProperty;
import org.geogebra.common.properties.impl.general.RoundingIndexProperty;
import org.geogebra.common.properties.impl.graphics.AxesVisibilityProperty;
import org.geogebra.common.properties.impl.graphics.DistancePropertyCollection;
import org.geogebra.common.properties.impl.graphics.GraphicsActionsPropertyCollection;
import org.geogebra.common.properties.impl.graphics.GridStyleProperty;
import org.geogebra.common.properties.impl.graphics.GridVisibilityProperty;
import org.geogebra.common.properties.impl.graphics.LabelsPropertyCollection;
import org.geogebra.common.properties.impl.graphics.PointCapturingProperty;

/**
 * Creates properties for the GeoGebra application.
 */
public class DefaultPropertiesFactory implements PropertiesFactory {

	@Override
	public List<PropertiesArray> createProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry) {
		return Arrays.asList(
				createGeneralProperties(app, localization, propertiesRegistry),
				createGraphicsProperties(app, localization, propertiesRegistry),
				createAlgebraProperties(app, localization, propertiesRegistry));
	}

	/**
	 * Creates general properties.
	 * @param app properties for app
	 * @param localization localization for properties
	 * @param onLanguageSetCallback callback when language is set
	 * @return an array of general properties
	 */
	protected PropertiesArray createGeneralProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry) {
		Kernel kernel = app.getKernel();
		String name = localization.getMenu("General");
		Settings settings = app.getSettings();
		return new PropertiesArray(name,
				registerProperties(propertiesRegistry,
						new RoundingIndexProperty(app, localization),
						new AngleUnitProperty(kernel, localization),
						new LabelingProperty(localization, settings.getLabelSettings()),
						new CoordinatesProperty(kernel, localization),
						new FontSizeProperty(
								localization,
								settings.getFontSettings(),
								app.getSettingsUpdater().getFontSettingsUpdater()),
						new LanguageProperty(app, localization)));
	}

	/**
	 * Creates algebra specific properties.
	 * @param app properties for app
	 * @param localization localization for properties
	 * @return an array of algebra specific properties
	 */
	protected PropertiesArray createAlgebraProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry) {
		AlgebraSettings algebraSettings = app.getSettings().getAlgebra();
		Kernel kernel = app.getKernel();
		String name = localization.getMenu("Algebra");
		if (app.has(Feature.MOB_PROPERTY_SORT_BY)) {
			return new PropertiesArray(name,
					registerProperties(propertiesRegistry,
							new AlgebraDescriptionProperty(kernel, localization),
							new SortByProperty(algebraSettings, localization),
							new ShowAuxiliaryProperty(app, localization))
			);
		} else {
			return new PropertiesArray(name,
					registerProperties(propertiesRegistry,
							new AlgebraDescriptionProperty(kernel, localization),
							new ShowAuxiliaryProperty(app, localization)));
		}
	}

	/**
	 * Creates graphics specific properties.
	 * @param app properties for app
	 * @param localization localization for properties
	 * @return an array of graphics specific properties
	 */
	protected PropertiesArray createGraphicsProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry) {
		EuclidianView activeView = app.getActiveEuclidianView();
		EuclidianSettings euclidianSettings = activeView.getSettings();
		return new PropertiesArray(localization.getMenu("DrawingPad"),
				registerProperties(propertiesRegistry,
						new GraphicsActionsPropertyCollection(app, localization),
						new AxesVisibilityProperty(localization, euclidianSettings),
						new GridVisibilityProperty(localization, euclidianSettings),
						new GridStyleProperty(localization, euclidianSettings),
						new PointCapturingProperty(app, localization),
						new DistancePropertyCollection(app, localization, euclidianSettings),
						new LabelsPropertyCollection(localization, euclidianSettings))
		);
	}
}