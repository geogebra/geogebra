package org.geogebra.web.html5.main.settings;

import org.geogebra.common.main.settings.DefaultSettings;

/**
 * Default settings for web.
 */
public class DefaultSettingsW implements DefaultSettings {

	private static final int APP_FONT_SIZE = 16;
	private static final int GUI_FONT_SIZE = 14;

	@Override
	public int getAppFontSize() {
		return APP_FONT_SIZE;
	}

	@Override
	public int getGuiFontSize() {
		return GUI_FONT_SIZE;
	}
}
