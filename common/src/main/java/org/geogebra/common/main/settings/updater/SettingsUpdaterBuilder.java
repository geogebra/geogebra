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
		settingsUpdater.setSettings(app.getSettings());
		settingsUpdater.setAppConfig(app.getConfig());
		settingsUpdater.setKernel(app.getKernel());
		settingsUpdater.setFontSettingsUpdater(newFontSettingsUpdater());
		settingsUpdater.setLabelSettingsUpdater(newLabelSettingsUpdater());
		return settingsUpdater;
	}

	protected FontSettingsUpdater newFontSettingsUpdater() {
		return new FontSettingsUpdater(app);
	}

	private LabelSettingsUpdater newLabelSettingsUpdater() {
		return new LabelSettingsUpdater(app);
	}

	protected App getApp() {
		return app;
	}
}
