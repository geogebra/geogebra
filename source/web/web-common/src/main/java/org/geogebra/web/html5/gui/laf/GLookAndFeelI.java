package org.geogebra.web.html5.gui.laf;

import org.geogebra.common.GeoGebraConstants.Platform;
import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.ResourceAction;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;

import elemental2.promise.Promise;

/**
 * Look and feel properties.
 */
public interface GLookAndFeelI {
	int COMMAND_LINE_HEIGHT = 43;
	int TOOLBAR_HEIGHT = 53;

	boolean isSmart();

	boolean supportsGoogleDrive();

	boolean isTablet();

	String getType();

	boolean undoRedoSupported();

	/**
	 * Add window closing handler to prevent losing unsaved changes.
	 * @param app application
	 */
	void addWindowClosingHandler(AppW app);

	void removeWindowClosingHandler();

	boolean copyToClipboardSupported();

	Object getLoginListener();

	boolean autosaveSupported();

	boolean exportSupported();

	boolean isEmbedded();

	boolean printSupported();

	/**
	 * @param dim dimension
	 * @param appName app name
	 * @return platform
	 */
	Platform getPlatform(int dim, String appName);

	/**
	 * Store language code for next app start.
	 * @param language language code
	 */
	void storeLanguage(String language);

	Promise<String> loadLanguage();

	/**
	 * Toggle fullscreen mode
	 * @param b whether to go fullscreen
	 */
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

	/**
	 * Override action for a resource.
	 * @param action basic action
	 * @return overridden action
	 */
	ResourceAction getDisplayAction(ResourceAction action);

	/**
	 * Should be combined with app.isExamLocked() to check that the environment is
	 * currently locked.
	 * @return whether this environment supports locking
	 */
	default boolean hasLockedEnvironment() {
		return false;
	}

	/**
	 * @return whether help and feedback should be shown in main menu or not
	 */
	default boolean hasHelpMenu() {
		return Browser.isGeogebraOrInternalHost();
	}
}
