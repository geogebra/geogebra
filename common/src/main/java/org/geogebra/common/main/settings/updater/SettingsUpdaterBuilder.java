package org.geogebra.common.main.settings.updater;

import org.geogebra.common.main.App;

/**
 * Builds the SettingsUpdater object.
 */
public class SettingsUpdaterBuilder {

	private App app;
	private SettingsUpdater prototype;

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
		initPrototypeIfNull();
		prototype.setEuclidianHost(app);
		prototype.setDefaultSettings(app.getDefaultSettings());
		prototype.setSettings(app.getSettings());
		prototype.setAppConfig(app.getConfig());
		prototype.setKernel(app.getKernel());
		prototype.setFontSettingsUpdater(newFontSettingsUpdater());
		prototype.setLabelSettingsUpdater(newLabelSettingsUpdater());
		return prototype;
	}

	private void initPrototypeIfNull() {
		if (prototype == null) {
			prototype = new SettingsUpdater();
		}
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

	/**
	 * Sets the prototype which will be used in the newSettingsUpdater method
	 * for building the SettingsUpdater object.
	 * @param prototype The newSettingsUpdater method will build on this object
	 *                    and it will return this object after all the attributes has been set.
	 */
	public void setPrototype(SettingsUpdater prototype) {
		this.prototype = prototype;
	}
}
