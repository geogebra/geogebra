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

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class NavigationBarProperty extends AbstractValuedProperty<Boolean>
	implements BooleanProperty {
	private final App app;
	private final int viewID;

	/**
	 * Creates a property enabling/disabling the navigation bar
	 * @param localization localization
	 * @param app application
	 * @param viewID view ID
	 */
	public NavigationBarProperty(Localization localization, App app, int viewID) {
		super(localization, "ConstructionProtocolNavigation");
		this.app = app;
		this.viewID = viewID;
	}

	@Override
	protected void doSetValue(Boolean value) {
		app.toggleShowConstructionProtocolNavigation(viewID);
	}

	@Override
	public Boolean getValue() {
		return app.showConsProtNavigation(viewID);
	}
}
