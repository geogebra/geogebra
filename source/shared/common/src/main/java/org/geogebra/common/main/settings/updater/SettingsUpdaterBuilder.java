package org.geogebra.common.main.settings.updater;

import org.geogebra.common.main.App;

/**
 * Builds the SettingsUpdater object.
 */
public class SettingsUpdaterBuilder {

	private App app;
	private FontSettingsUpdater fontSettingsUpdater;
	SettingsUpdater prototype;

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
		if (prototype == null) {
			prototype = new SettingsUpdater();
		}
		prototype.setEuclidianHost(app);
		prototype.setSettings(app.getSettings());
		prototype.setAppConfig(app.getConfig());
		prototype.setKernel(app.getKernel());
		prototype.setFontSettingsUpdater(getFontSettingsUpdater());
		return prototype;
	}

	private FontSettingsUpdater getFontSettingsUpdater() {
		return fontSettingsUpdater == null ? new FontSettingsUpdater(app)
				: fontSettingsUpdater;
	}

	/**
	 * Sets a prototype to override reset behaviors.
	 * Used on Android.
	 * @param prototype updater prototype
	 */
	public void setPrototype(SettingsUpdater prototype) {
		this.prototype = prototype;
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
