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

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class AxesBoldProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {
	private EuclidianSettings euclidianSettings;

	/**
	 * Creates bold property for axes
	 * @param localization localization
	 * @param euclidianSettings euclidian settings
	 */
	public AxesBoldProperty(Localization localization, EuclidianSettings euclidianSettings) {
		super(localization, "Bold");
		this.euclidianSettings = euclidianSettings;
	}

	@Override
	protected void doSetValue(Boolean value) {
		euclidianSettings.setBoldAxes(value);
	}

	@Override
	public Boolean getValue() {
		return euclidianSettings.areAxesBold();
	}
}
