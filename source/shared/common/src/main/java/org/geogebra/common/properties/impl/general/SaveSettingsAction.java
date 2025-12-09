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
