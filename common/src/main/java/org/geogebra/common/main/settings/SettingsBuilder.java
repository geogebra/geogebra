package org.geogebra.common.main.settings;

import org.geogebra.common.main.App;

public class SettingsBuilder {

	private App app;
	private DefaultSettings defaultSettings;

	public SettingsBuilder(App app) {
		this.app = app;
		defaultSettings = app.getDefaultSettings();
	}

	public Settings newSettings() {
		Settings settings = new Settings(app, getEuclidianLength());
		settings.setFontSettings(newFontSettings());
		settings.setLabelSettings(newLabelSettings());
		settings.setAlgebraSettings(newAlgebraSettings());
		return settings;
	}

	private int getEuclidianLength() {
		return 3;
	}

	protected DefaultSettings getDefaultSettings() {
		return defaultSettings;
	}

	protected FontSettings newFontSettings() {
		return new FontSettings(defaultSettings);
	}

	private LabelSettings newLabelSettings() {
		return new LabelSettings();
	}

	protected AlgebraSettings newAlgebraSettings() {
		return new AlgebraSettings();
	}
}
