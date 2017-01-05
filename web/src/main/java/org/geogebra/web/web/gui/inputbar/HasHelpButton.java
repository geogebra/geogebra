package org.geogebra.web.web.gui.inputbar;

import com.google.gwt.user.client.ui.UIObject;

/**
 * UI element with help button
 *
 */
public interface HasHelpButton {

	/**
	 * Update the icon to info / error
	 * 
	 * @param error
	 *            whether error should be shown
	 */
	void updateIcons(boolean error);

	/**
	 * @return command at caret
	 */
	String getCommand();

	/**
	 * @return help toggle button
	 */
	UIObject getHelpToggle();

}
