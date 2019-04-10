package org.geogebra.common.main.settings.updater;

import org.geogebra.common.main.App;

public class SettingsUpdaterBuilder {

	private App app;

	public SettingsUpdaterBuilder(App app) {
		this.app = app;
	}

	public SettingsUpdater newSettingsUpdater() {
		SettingsUpdater settingsUpdater = new SettingsUpdater();
		settingsUpdater.setDefaultSettings(app.getDefaultSettings());
		settingsUpdater.setFontSettingsUpdater(newFontSettingsUpdater());
		return settingsUpdater;
	}

	protected FontSettingsUpdater newFontSettingsUpdater() {
		return new FontSettingsUpdater(app);
	}

	protected App getApp() {
		return app;
	}
}
