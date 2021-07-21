package org.geogebra.web.html5.gui.laf;

/**
 * Controller for sign in dialog
 */
public interface SignInControllerI {
	/**
	 * Show login dialog (user initiated)
	 */
	void login();

	/**
	 * Log in initiated by the app, can't open popups
	 */
	void loginFromApp();

}
