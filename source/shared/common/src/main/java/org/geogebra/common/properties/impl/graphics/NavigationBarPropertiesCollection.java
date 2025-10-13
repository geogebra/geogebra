package org.geogebra.common.properties.impl.graphics;

import java.util.ArrayList;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;

public class NavigationBarPropertiesCollection extends AbstractPropertyCollection<Property> {

	/**
	 * @param localization localization
	 * @param settings view settings
	 */
	public NavigationBarPropertiesCollection(Localization localization, App app, int viewID,
			EuclidianSettings settings) {
		super(localization, "");

		ArrayList<Property> properties = new ArrayList<>();
		properties.add(new NavigationBarProperty(localization, app, viewID));
		properties.add(new NavigationBarPlayButtonProperty(localization, app, viewID,
				settings));
		properties.add(new NavigationBarConstructionProtocolButtonProperty(localization, app,
				viewID, settings));
		setProperties(properties.toArray(new Property[0]));
	}
}
