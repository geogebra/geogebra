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

package org.geogebra.common.properties.impl.algebra;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

/**
 * "Show auxiliary objects" property.
 */
public class ShowAuxiliaryProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {

	private App app;

	/**
	 * Constructs a property for showing or hiding auxiliary objects.
	 * @param app app
	 * @param localization localization
	 */
	public ShowAuxiliaryProperty(App app, Localization localization) {
		super(localization, "AuxiliaryObjects");
		this.app = app;
	}

	@Override
	public Boolean getValue() {
		return app.showAuxiliaryObjects();
	}

	@Override
	public void doSetValue(Boolean value) {
		app.setShowAuxiliaryObjects(value);
	}
}
