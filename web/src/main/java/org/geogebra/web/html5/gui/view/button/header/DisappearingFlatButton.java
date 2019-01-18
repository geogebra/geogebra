package org.geogebra.web.html5.gui.view.button.header;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.resources.client.ResourcePrototype;

/**
 * This button disappears when disabled.
 */
public class DisappearingFlatButton extends FlatButton {

	/**
	 * @param app The app.
	 * @param icon The icon.
	 * @param iconSize The size f the icon.
	 */
	public DisappearingFlatButton(App app, ResourcePrototype icon, int iconSize) {
		super(app, icon, iconSize);
	}

	@Override
	public void setEnabled(boolean enabled) {
		Dom.toggleClass(this, "hidden", !enabled);
	}
}
