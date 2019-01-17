package org.geogebra.web.html5.gui.view.button.header;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.view.button.StandardButton;

import com.google.gwt.resources.client.ResourcePrototype;

public class FlatButton extends StandardButton {

	public FlatButton(App app, ResourcePrototype icon, int iconSize) {
		super(icon, null, iconSize, app);
		addStyleName("flatButtonHeader");
	}
}
