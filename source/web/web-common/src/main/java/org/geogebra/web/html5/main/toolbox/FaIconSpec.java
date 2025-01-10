package org.geogebra.web.html5.main.toolbox;

import org.geogebra.web.html5.gui.view.IconSpec;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.DOM;

public class FaIconSpec implements IconSpec {
	String name;

	public FaIconSpec(String name) {
		this.name = name;
	}

	@Override
	public Element toElement() {
		Element icon = DOM.createElement("I");
		icon.setClassName(name);
		icon.addClassName("fa-light");
		return icon;
	}

	@Override
	public IconSpec withFill(String color) {
		// not needed, solved through css
		return this;
	}
}
