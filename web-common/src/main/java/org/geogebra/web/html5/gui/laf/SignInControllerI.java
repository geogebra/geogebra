package org.geogebra.web.html5.gui.laf;

import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.gwtproject.user.client.ui.Button;

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
