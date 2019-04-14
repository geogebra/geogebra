package org.geogebra.common.main.settings.updater;

import org.geogebra.common.main.App;

public class SettingsUpdaterBuilder {

	private App app;
	private SettingsUpdater prototype;

	public SettingsUpdaterBuilder(App app) {
		this.app = app;
	}

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

	public void setPrototype(SettingsUpdater prototype) {
		this.prototype = prototype;
	}
}
