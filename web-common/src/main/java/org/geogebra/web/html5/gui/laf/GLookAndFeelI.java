package org.geogebra.web.html5.gui.laf;

import org.geogebra.common.GeoGebraConstants.Platform;
import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.ResourceAction;
import org.geogebra.web.html5.main.AppW;

public interface GLookAndFeelI {
	int COMMAND_LINE_HEIGHT = 43;
	int TOOLBAR_HEIGHT = 53;

	boolean isSmart();

	boolean supportsGoogleDrive();

	boolean isTablet();

	String getType();

	boolean undoRedoSupported();

	void addWindowClosingHandler(AppW app);

	void removeWindowClosingHandler();

	boolean copyToClipboardSupported();

	Object getLoginListener();

	boolean autosaveSupported();

	boolean exportSupported();

	boolean isEmbedded();

	boolean examSupported();

	boolean printSupported();

	Platform getPlatform(int dim, String appName);

	void storeLanguage(String language);

	void toggleFullscreen(boolean b);

	boolean isOfflineExamSupported();

	boolean hasHeader();

	/**
	 * @return whether login/logout button should be inside the app
	 */
	boolean hasLoginButton();

	/**
	 * @param app
	 *            application
	 * @return signin controller
	 */
	SignInControllerI getSignInController(App app);

	/**
	 * @return client id
	 */
	String getClientId();

	boolean isExternalLoginAllowed();

	ResourceAction getDisplayAction(ResourceAction action);
}
