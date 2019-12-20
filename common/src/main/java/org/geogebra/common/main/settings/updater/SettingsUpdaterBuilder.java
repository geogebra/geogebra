package org.geogebra.common.main.settings.updater;

import org.geogebra.common.main.App;

/**
 * Builds the SettingsUpdater object.
 */
public class SettingsUpdaterBuilder {

	private App app;
	private FontSettingsUpdater fontSettingsUpdater;

	/**
	 * @param app app
	 */
	public SettingsUpdaterBuilder(App app) {
		this.app = app;
	}

	/**
	 * Builds the SettingsUpdater object.
	 * @return A SettingsUpdater instance built from a prototype (if it was set)
	 * or from a new SettingsUpdater instance.
	 */
	public SettingsUpdater newSettingsUpdater() {
		SettingsUpdater prototype = new SettingsUpdater();
		prototype.setEuclidianHost(app);
		prototype.setSettings(app.getSettings());
		prototype.setAppConfig(app.getConfig());
		prototype.setKernel(app.getKernel());
		prototype.setFontSettingsUpdater(getFontSettingsUpdater());
		prototype.setLabelSettingsUpdater(newLabelSettingsUpdater());
		return prototype;
	}

	private FontSettingsUpdater getFontSettingsUpdater() {
		return fontSettingsUpdater == null ? new FontSettingsUpdater(app)
				: fontSettingsUpdater;
	}

	private LabelSettingsUpdater newLabelSettingsUpdater() {
		return new LabelSettingsUpdater(app);
	}

	/**
	 * @param fontSettingsUpdater
	 *            font settings updater
	 * @return this
	 */
	public SettingsUpdaterBuilder withFontSettingsUpdater(
			FontSettingsUpdater fontSettingsUpdater) {
		this.fontSettingsUpdater = fontSettingsUpdater;
		return this;
	}
}
