package org.geogebra.web.html5.gui.laf;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.main.AppConfig;
import org.geogebra.web.html5.gui.zoompanel.FullScreenHandler;

/**
 * Vendor dependent settings
 */
public interface VendorSettings {

	/**
	 * License URL should contain ? so that params can be appended.
	 *
	 * @return base URL of the license
	 */
	String getLicenseURL();

	/**
	 * Allows overriding the app title
	 *
	 * @param config
	 *            app configuration
	 * @return app title translation key
	 */
	String getAppTitle(AppConfig config);

	/**
	 * @return view preferences
	 */
	ViewPreferences getViewPreferences();

	/**
	 * Gets helper for toggling emulated fullscreen when running in an iframe,
	 * only available for specific cases.
	 * 
	 * @return helper for fullscreen or null.
	 */
	FullScreenHandler getFullscreenHandler();

	/**
	 * Returns the primary color.
	 *
	 * @return the primary color
	 */
	GColor getPrimaryColor();

	/**
	 * @return if main menu button should be external.
	 */
	boolean isMainMenuExternal();

	/**
	 * Transforms a localization key to the vendor specific version.
	 *
	 * @param key the default menu localization key
	 * @return vendor specific localization key
	 */
	String getMenuLocalizationKey(String key);

	/**
	 * Transforms the style name to the vendor specific version.
	 *
	 * @param styleName the default style name
	 * @return the vendor specific style name
	 */
	String getStyleName(String styleName);
}
