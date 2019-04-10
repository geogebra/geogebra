package org.geogebra.common.main.settings.updater;

import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.DefaultSettings;

public abstract class SettingsUpdaterBuilder {

	private App app;

	public SettingsUpdaterBuilder(App app) {
		this.app = app;
	}

	protected abstract DefaultSettings getDefaultSettings();

	public SettingsUpdater newSettingsUpdater() {
		SettingsUpdater settingsUpdater = new SettingsUpdater();
		settingsUpdater.setDefaultSettings(getDefaultSettings());
		settingsUpdater.setFontSettingsUpdater(newFontSettingsUpdater());
		return settingsUpdater;
	}

	private FontSettingsUpdater newFontSettingsUpdater() {
		return new FontSettingsUpdater(app);
	}
}
