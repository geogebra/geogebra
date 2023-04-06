package org.geogebra.common.properties;

import org.geogebra.common.awt.GColor;

/**
 * A property whose value is a color.
 */
public interface ColorProperty extends ValuedProperty<GColor> {

	/**
	 * Gets the available color values for this property.
	 *
	 * @return color array
	 */
	GColor[] getAvailableColors();
}
