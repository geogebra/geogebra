package org.geogebra.common.properties;

import org.geogebra.common.awt.GColor;

/**
 * A property that describes the color.
 */
public interface ColorProperty extends ValuedProperty<GColor> {

	/**
	 * Get the available colors for this property
	 * @return color array
	 */
	GColor[] getColors();
}
