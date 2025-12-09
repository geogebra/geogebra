/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
