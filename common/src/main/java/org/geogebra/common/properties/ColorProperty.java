package org.geogebra.common.properties;

import org.geogebra.common.awt.GColor;

/**
 * A property that describes the color.
 */
public interface ColorProperty extends Property {

	/**
	 * The color value of the property.
	 *
	 * @return the color
	 */
	GColor getColor();

	/**
	 * Set the color value of the property.
	 *
	 * @param color the color
	 */
	void setColor(GColor color);

	/**
	 * Get the available colors for this property
	 *
	 * @return color array
	 */
	GColor[] getColors();
}
