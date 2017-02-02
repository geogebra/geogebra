package org.geogebra.web.web.gui.inputbar;

import com.google.gwt.user.client.ui.UIObject;

/**
 * UI element with help button
 *
 */
public interface HasHelpButton {



	/**
	 * @return command at caret
	 */
	String getCommand();

	/**
	 * @return help toggle button
	 */
	UIObject getHelpToggle();

	/**
	 * Update the icon to info / error
	 * 
	 * @param msg
	 *            input error
	 */
	void setError(String msg);

	/**
	 * @param command
	 *            command name of error
	 */
	void setCommandError(String command);

}
