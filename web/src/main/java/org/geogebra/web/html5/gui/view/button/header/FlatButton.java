package org.geogebra.web.html5.gui.view.button.header;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.view.button.StandardButton;

import com.google.gwt.resources.client.ResourcePrototype;

/**
 * Button with flat style.
 */
public class FlatButton extends StandardButton {

	/**
	 * @param app
	 * @param icon
	 * @param iconSize
	 */
	public FlatButton(App app, ResourcePrototype icon, int iconSize) {
		super(icon, null, iconSize, app);
		addStyleName("flatButtonHeader");
	}
}
