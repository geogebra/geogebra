package org.geogebra.desktop.main.settings.updater;

import org.geogebra.common.main.settings.updater.FontSettingsUpdater;
import org.geogebra.common.main.settings.updater.SettingsUpdaterBuilder;
import org.geogebra.desktop.main.AppD;

/**
 * Builds the SettingsUpdater for desktop.
 */
public class SettingsUpdaterBuilderD extends SettingsUpdaterBuilder {

	/**
	 * @param app app
	 */
	public SettingsUpdaterBuilderD(AppD app) {
		super(app);
	}

	@Override
	protected FontSettingsUpdater newFontSettingsUpdater() {
		return new FontSettingsUpdaterD(getApp());
	}

	@Override
	protected AppD getApp() {
		return (AppD) super.getApp();
	}
}
