package org.geogebra.common.properties.factory;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertyCollection;
import org.geogebra.common.properties.impl.general.LanguageProperty;

/**
 * Creates properties for the GeoGebra application.
 */
public interface PropertiesFactory {

	/**
	 * Creates general properties.
	 *
	 * @param app                   properties for app
	 * @param localization          localization for properties
	 * @param onLanguageSetCallback callback when language is set
	 * @return an array of general properties
	 */
	PropertyCollection createGeneralProperties(
			App app,
			Localization localization,
			LanguageProperty.OnLanguageSetCallback onLanguageSetCallback);

	/**
	 * Creates algebra specific properties.
	 *
	 * @param app          properties for app
	 * @param localization localization for properties
	 * @return an array of algebra specific properties
	 */
	PropertyCollection createAlgebraProperties(App app, Localization localization);

	/**
	 * Creates graphics specific properties.
	 *
	 * @param app          properties for app
	 * @param localization localization for properties
	 * @return an array of graphics specific properties
	 */
	PropertyCollection createGraphicsProperties(App app, Localization localization);
}
