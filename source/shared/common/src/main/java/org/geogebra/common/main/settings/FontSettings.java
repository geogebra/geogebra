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

package org.geogebra.common.main.settings;

/**
 * Font settings.
 */
public class FontSettings implements Resettable {

	private DefaultSettings defaultSettings;
	private int appFontSize;
	private int guiFontSize;

	/**
	 * This constructor is protected because it should be called only by the SettingsBuilder.
	 * @param defaultSettings default settings
	 */
	protected FontSettings(DefaultSettings defaultSettings) {
		this.defaultSettings = defaultSettings;
		initSizes();
	}

	private void initSizes() {
		appFontSize = defaultSettings.getAppFontSize();
		guiFontSize = defaultSettings.getGuiFontSize();
	}

	public int getAppFontSize() {
		return appFontSize;
	}

	public void setAppFontSize(int appFontSize) {
		this.appFontSize = appFontSize;
	}

	public int getGuiFontSize() {
		return guiFontSize;
	}

	/**
	 * @return If the gui font size is not initialized (the value is -1)
	 * then returns the app font size,
	 * otherwise returns the gui font size.
	 */
	public int getGuiFontSizeSafe() {
		return guiFontSize == -1 ? appFontSize : guiFontSize;
	}

	public void setGuiFontSize(int guiFontSize) {
		this.guiFontSize = guiFontSize;
	}

	public int getAlgebraFontSize() {
		return getAppFontSize() + 2;
	}

	@Override
	public void resetDefaults() {
		initSizes();
	}
}
