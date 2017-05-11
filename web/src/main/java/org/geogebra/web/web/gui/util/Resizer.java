package org.geogebra.web.web.gui.util;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;

public class Resizer {

	public static void setPixelWidth(Element inputParent, int width) {
		if (inputParent != null) {
			inputParent.getStyle().setWidth(width, Unit.PX);
		}
	}

}
