package org.geogebra.web.full.gui.util;

import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.client.Style.Unit;

/**
 * Resizing utilities
 */
public class Resizer {

	/**
	 * @param element
	 *            element
	 * @param width
	 *            pixel width
	 */
	public static void setPixelWidth(Element element, int width) {
		if (element != null) {
			element.getStyle().setWidth(width, Unit.PX);
		}
	}

}
