package org.geogebra.common.properties.impl.general;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractActionableProperty;

public class RestoreSettingsAction extends AbstractActionableProperty {
	private final App app;

	/**
	 * Creates a RestoreSettingsAction property.
	 * @param app application
	 * @param loc localization
	 */
	public RestoreSettingsAction(App app, Localization loc) {
		super(loc, "RestoreSettings");
		this.app = app;
	}

	@Override
	protected void doPerformAction() {
		app.restoreSettings();
	}
}
