package org.geogebra.web.html5.gui.view;

import org.gwtproject.dom.client.Element;

/**
 * Icon definition.
 */
public interface IconSpec {

	/**
	 * @return DOM element for this icon
	 */
	Element toElement();

	/**
	 * Derive icon with different color
	 * @param color fill color
	 * @return derived icon definition
	 */
	IconSpec withFill(String color);
}
