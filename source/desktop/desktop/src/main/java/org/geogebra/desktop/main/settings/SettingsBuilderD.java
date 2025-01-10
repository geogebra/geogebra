package org.geogebra.desktop.main.settings;

import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.AlgebraSettings;
import org.geogebra.common.main.settings.SettingsBuilder;

/**
 * This class builds the settings object for desktop.
 */
public class SettingsBuilderD extends SettingsBuilder {

	public SettingsBuilderD(App app) {
		super(app);
	}

	@Override
	protected AlgebraSettings newAlgebraSettings() {
		return new AlgebraSettingsD();
	}
}
