package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;

/**
 * Collection of advanced settings for a graphics view.
 */
public class AdvancedPropertiesCollection extends AbstractPropertyCollection<Property> {
	/**
	 * @param localization localization
	 */
	public AdvancedPropertiesCollection(Localization localization, EuclidianSettings settings) {
		super(localization, "Advanced");
		setProperties(new Property[]{new BackgroundColorProperty(localization, settings)});
	}
}
