package org.geogebra.web.web.gui.properties;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * Button for properties stylebar
 *
 */
public class PropertiesButton extends MenuItem {

	/**
	 * @param text
	 *            content
	 * @param cmd
	 *            action
	 */
	public PropertiesButton(String text, Command cmd) {
		super(text, true, cmd);
    }

}
