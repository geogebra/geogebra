/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.inputbar;

import org.gwtproject.user.client.ui.IsWidget;

/**
 * UI element with help button
 *
 */
public interface HasHelpButton {

	/**
	 * @return command at caret if commands are allowed, "" otherwise
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
