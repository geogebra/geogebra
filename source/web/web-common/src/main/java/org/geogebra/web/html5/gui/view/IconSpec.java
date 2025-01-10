package org.geogebra.web.html5.gui.view;

import org.gwtproject.dom.client.Element;

public interface IconSpec {
	Element toElement();

	IconSpec withFill(String color);
}
