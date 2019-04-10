package org.geogebra.common.main.settings.updater;

import org.geogebra.common.main.settings.DefaultSettings;

public class SettingsUpdater {

	private DefaultSettings defaultSettings;
	private FontSettingsUpdater fontSettingsUpdater;

	SettingsUpdater() {}

	void resetSettingsOnAppStart() {

	}

	public void resetSettingsAfterClearAll() {
		fontSettingsUpdater.setAppFontSizeAndUpdateViews(defaultSettings.getAppFontSize());
	}

	void setDefaultSettings(DefaultSettings defaultSettings) {
		this.defaultSettings = defaultSettings;
	}

	void setFontSettingsUpdater(FontSettingsUpdater fontSettingsUpdater) {
		this.fontSettingsUpdater = fontSettingsUpdater;
	}

	public FontSettingsUpdater getFontSettingsUpdater() {
		return fontSettingsUpdater;
	}
}
