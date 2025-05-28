package org.geogebra.common.main.error;

import javax.annotation.CheckForNull;

import org.geogebra.common.util.AsyncOperation;

/**
 * Class capable of displaying error messages in the UI
 */
public interface ErrorHandler {
	/**
	 * Display the error message to the user
	 * 
	 * @param msg
	 *            localized error message
	 */
	void showError(@CheckForNull String msg);

	/**
	 * Show command help dialog
	 * 
	 * @param command
	 *            internal command name
	 * @param message
	 *            localized message
	 */
	void showCommandError(String command, String message);

	/**
	 * @return command from
	 */
	String getCurrentCommand();

	/**
	 * @param string
	 *            comma separated undefined variables
	 * @param callback
	 *            function to be called after sliders are created OR declined
	 *            takes array of Strings, at index 0 should be
	 *            AlgebraProcessor.CREATE_SLIDER if sliders were created
	 * 
	 * @return whether callback still needs to be handled
	 */
	boolean onUndefinedVariables(String string,
			AsyncOperation<String[]> callback);

	/**
	 * Remove any error messages currently displayed
	 */
	void resetError();

}
