package org.geogebra.web.html5.main.settings;

import org.geogebra.common.main.settings.DefaultSettings;
import org.geogebra.common.main.settings.FontSettings;

/**
 * Font settings on web.
 */
public class FontSettingsW extends FontSettings {

	private static final int MIN_APP_FONT_SIZE = 12;

	FontSettingsW(DefaultSettings defaultSettings) {
		super(defaultSettings);
	}

	@Override
	public int getAppFontSize() {
		return Math.max(super.getAppFontSize(), MIN_APP_FONT_SIZE);
	}
}
