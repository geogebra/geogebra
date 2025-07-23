package org.geogebra.common.properties.impl.general;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractActionableProperty;

public class SaveSettingsAction extends AbstractActionableProperty {
	private final App app;

	/**
	 * Creates a SaveSettingsAction property.
	 * @param app application
	 * @param localization localization
	 */
	public SaveSettingsAction(App app, Localization localization) {
		super(localization, "Settings.Save");
		this.app = app;
	}

	@Override
	protected void doPerformAction() {
		app.saveSettings();
	}
}
