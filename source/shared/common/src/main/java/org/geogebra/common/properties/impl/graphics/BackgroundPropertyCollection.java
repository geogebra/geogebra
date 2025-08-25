package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;

public class BackgroundPropertyCollection extends AbstractPropertyCollection<Property> {

	/**
	 * Creates a property collection for graphics background
	 * @param localization localization
	 * @param settings euclidian settings
	 */
	public BackgroundPropertyCollection(Localization localization, EuclidianSettings settings) {
		super(localization, "Background");
		setProperties(new Property[]{new BackgroundColorProperty(localization, settings)});
	}
}
