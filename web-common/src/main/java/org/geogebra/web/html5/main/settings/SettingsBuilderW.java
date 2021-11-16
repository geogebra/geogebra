package org.geogebra.web.html5.main.settings;

import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.FontSettings;
import org.geogebra.common.main.settings.SettingsBuilder;

/**
 * This class builds the settings object for web.
 */
public class SettingsBuilderW extends SettingsBuilder {

	public SettingsBuilderW(App app) {
		super(app);
	}

	@Override
	protected FontSettings newFontSettings() {
		return new FontSettingsW(getDefaultSettings());
	}
}
