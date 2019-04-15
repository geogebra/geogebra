package org.geogebra.common.geogebra3D.main.settings.updater;

import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.updater.FontSettingsUpdater;
import org.geogebra.common.main.settings.updater.SettingsUpdaterBuilder;

/**
 * Builds the SettingsUpdater object for the 3D apps.
 */
public class SettingsUpdaterBuilder3D extends SettingsUpdaterBuilder {

	public SettingsUpdaterBuilder3D(App app) {
		super(app);
	}

	@Override
	protected FontSettingsUpdater newFontSettingsUpdater() {
		return new FontSettingsUpdater3D(getApp());
	}
}
