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

package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

/**
 * Show in AV
 */
public class ShowInAVProperty extends AbstractValuedProperty<Boolean> implements BooleanProperty {

	private final GeoElement element;

	/***/
	public ShowInAVProperty(Localization localization, GeoElement element) {
		super(localization, "ShowInAlgebraView");
		this.element = element;
	}

	@Override
	public Boolean getValue() {
		return !element.isAuxiliaryObject();
	}

	@Override
	public void doSetValue(Boolean show) {
		element.setAuxiliaryObject(!show);
		element.updateRepaint();

		App app = element.getApp();
		app.updateGuiForShowAuxiliaryObjects();
	}
}
