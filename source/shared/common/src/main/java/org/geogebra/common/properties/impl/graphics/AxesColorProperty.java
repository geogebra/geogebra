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

import org.geogebra.common.awt.GColor;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.DefaultColorValues;

public class AxesColorProperty extends AbstractEnumeratedProperty<GColor>
		implements ColorProperty {
	private final EuclidianSettings settings;

	/**
	 * Creates a color property for axes
	 * @param loc localization
	 * @param settings view settings
	 */
	public AxesColorProperty(Localization loc, EuclidianSettings settings) {
		super(loc, "Color");
		this.settings = settings;
		setValues(DefaultColorValues.NEUTRAL);
	}

	@Override
	protected void doSetValue(GColor value) {
		settings.setAxesColor(value);
	}

	@Override
	public GColor getValue() {
		return settings.getAxesColor();
	}
}
