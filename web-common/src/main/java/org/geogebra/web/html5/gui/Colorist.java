package org.geogebra.web.html5.gui;


import org.gwtproject.dom.client.Element;

public class Colorist {

	public static final Colorist INSTANCE = new Colorist();

	public void colorUIElement(Element uiElement, Shades shade) {
		uiElement.addClassName(shade.getClassName());
	}
}
