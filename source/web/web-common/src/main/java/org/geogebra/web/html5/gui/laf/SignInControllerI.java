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
	 * Actively listen for cookie change
	 */
	void initLoginTimer();
}
