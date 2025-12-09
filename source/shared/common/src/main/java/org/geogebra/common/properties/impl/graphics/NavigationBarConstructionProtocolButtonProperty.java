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

package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class NavigationBarConstructionProtocolButtonProperty extends AbstractValuedProperty<Boolean>
	implements BooleanProperty, SettingsDependentProperty {
	private final App app;
	private final int viewID;
	private final EuclidianSettings evSettings;

	/**
	 * Creates a property enabling/disabling the construction protocol button in the navigation bar
	 * @param localization localization
	 * @param app application
	 * @param viewID euclidian view ID
	 */
	public NavigationBarConstructionProtocolButtonProperty(Localization localization, App app,
			int viewID, EuclidianSettings settings) {
		super(localization, "ConstructionProtocolButton");
		this.app = app;
		this.viewID = viewID;
		this.evSettings = settings;
	}

	@Override
	protected void doSetValue(Boolean value) {
		ConstructionProtocolNavigation consNav = app.getGuiManager()
				.getConstructionProtocolNavigation(viewID);
		consNav.setConsProtButtonVisible(!consNav.isConsProtButtonVisible());
		app.setUnsaved();
	}

	@Override
	public Boolean getValue() {
		ConstructionProtocolNavigation cpn = app.getGuiManager()
				.getConstructionProtocolNavigation(viewID);
		return cpn == null || cpn.isConsProtButtonVisible();
	}

	@Override
	public boolean isEnabled() {
		return app.showConsProtNavigation(viewID);
	}

	@Override
	public AbstractSettings getSettings() {
		return evSettings;
	}
}
