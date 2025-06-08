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

	/**
	 * @return whether this is SMART Notebook integration
	 */
	boolean isSmart();

	/**
	 * @return whether saving to and loading from Google Drive is supported
	 */
	boolean supportsGoogleDrive();

	/**
	 * @return whether this is for tablets
	 */
	boolean isTablet();

	/**
	 * @return type name, only for API usage telemetry
	 */
	String getType();

	/**
	 * @return whether GeoGebra's own undo stack is supported
	 */
	boolean undoRedoSupported();

	/**
	 * Add window closing handler to prevent losing unsaved changes.
	 * @param app application
	 */
	void addWindowClosingHandler(AppW app);

	/**
	 * Remove window closing handler if supported.
	 * @see #addWindowClosingHandler(AppW)
	 */
	void removeWindowClosingHandler();

	/**
	 * @return whether copy to clipboard is supported
	 */
	boolean copyToClipboardSupported();

	/**
	 * @return name of a global JS function that should be called after login
	 */
	String getLoginListener();

	/**
	 * @return whether auto-save is supported
	 */
	boolean autosaveSupported();

	/**
	 * @return whether export functionality is supported
	 */
	boolean exportSupported();

	/**
	 * @return whether the app is embedded in another system (SMART Notebook, PowerPoint)
	 */
	boolean isEmbedded();

	/**
	 * @return whether print is supported
	 */
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

	/**
	 * @return user preferred language as promise (async in Chrome app)
	 */
	Promise<String> loadLanguage();

	/**
	 * Toggle fullscreen mode
	 * @param b whether to go fullscreen
	 */
	void toggleFullscreen(boolean b);

	/**
	 * @return whether we're in a browser that supports exam mode (ChromeOS app, Electron)
	 */
	boolean isOfflineExamSupported();

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

	/**
	 * @return whether login through external provider (Google, MS, ...) is allowed
	 */
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
