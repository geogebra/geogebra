package org.geogebra.common.properties.factory;

import java.util.List;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertiesRegistry;

/**
 * Creates properties for the GeoGebra application.
 */
public interface PropertiesFactory {

	/**
	 * Creates properties list.
	 * @param app properties for app
	 * @param localization localization for properties
	 * @param propertiesRegistry the properties registry
	 * @return a list of properties
	 */
	List<PropertiesArray> createProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry);
}
