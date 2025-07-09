package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.impl.collections.AbstractPropertyCollection;

/**
 * Collection of dimension properties of a graphics view.
 */
public class DimensionPropertiesCollection extends AbstractPropertyCollection<Property> {

	/**
	 * @param localization localization
	 */
	public DimensionPropertiesCollection(Localization localization) {
		super(localization, "Dimension");
		setProperties(new Property[0]);
	}
}
