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

package org.geogebra.common.main.settings;

import org.geogebra.common.main.App;

/**
 * Builds the Settings object.
 */
public class SettingsBuilder {

	private App app;
	private DefaultSettings defaultSettings;

	/**
	 * @param app app
	 */
	public SettingsBuilder(App app) {
		this.app = app;
		defaultSettings = app.getDefaultSettings();
	}

	/**
	 * Builds and returns the Settings object.
	 * @return The Settings object.
	 */
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
