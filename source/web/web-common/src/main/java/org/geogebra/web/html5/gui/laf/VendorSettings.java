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

package org.geogebra.web.html5.gui.laf;

import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.util.lang.Language;
import org.geogebra.web.html5.gui.zoompanel.FullScreenHandler;

/**
 * Vendor dependent settings
 */
public interface VendorSettings {

	/**
	 * Allows overriding the app title
	 *
	 * @param config
	 *            app configuration
	 * @return app title translation key
	 */
	String getAppTitle(AppConfig config);

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

	/**
	 * after session expired user will be logged out
	 * @return if the user session can expire (only mebis)
	 */
	boolean canSessionExpire();

	/**
	 *
	 * @return if bitmaps (png, jpg, etc) needs a security check.
	 */
	boolean hasBitmapSecurity();

	/**
	 * @param language active language
	 * @return list of protractor tools for given language
	 */
	List<Integer> getProtractorTools(Language language);
}
