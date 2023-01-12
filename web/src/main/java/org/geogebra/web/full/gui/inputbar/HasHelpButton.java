package org.geogebra.web.full.gui.inputbar;

import org.gwtproject.user.client.ui.IsWidget;

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
	IsWidget getHelpToggle();

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

	/**
	 * Sets a comma separated list of undefined variables.
	 * 
	 * @param vars
	 *            variable names
	 */
	void setUndefinedVariables(String vars);
}
