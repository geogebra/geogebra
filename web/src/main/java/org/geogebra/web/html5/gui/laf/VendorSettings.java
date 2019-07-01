package org.geogebra.web.html5.gui.laf;

import org.geogebra.common.main.AppConfig;
import org.geogebra.web.full.css.StylesProvider;
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
	 *
	 * @return the error key if a video cannot be accessed.
	 */
	String getVideoAccessErrorKey();

	/**
	 * Returns the translation key for the error message
	 * describing that the browser is not supported.
	 *
	 * @return the translation key
	 */
	String getUnsupportedBrowserErrorKey();

	/**
	 * Return vendor specific StylesProvider.
	 *
	 * @return StylesProvider
	 */
	StylesProvider getStylesProvider();
}
