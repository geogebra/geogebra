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
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class ButtonIconShownProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {
	private final GeoElement geoElement;
	private boolean iconShown;

	/**
	 * Constructs the property for the given element with the provided localization.
	 * @param localization {@link Localization}
	 * @param geoElement geo element
	 */
	public ButtonIconShownProperty(Localization localization, GeoElement geoElement) {
		super(localization, "");
		this.geoElement = geoElement;
		iconShown = !"".equals(geoElement.getImageFileName());
	}

	@Override
	protected void doSetValue(Boolean hasIcon) {
		if (!hasIcon) {
			geoElement.setImageFileName("");
		}
		iconShown = hasIcon;
	}

	@Override
	public Boolean getValue() {
		return iconShown;
	}
}
