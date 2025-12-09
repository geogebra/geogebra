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

package org.geogebra.test;

import java.util.List;

import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.jre.headless.Utf8Control;

public class LocalizationCommonUTF extends LocalizationCommon {
	/**
	 * @param dimension 3 for 3D
	 */
	public LocalizationCommonUTF(int dimension) {
		super(dimension);
		setResourceBundleControl(new Utf8Control());
	}

	@Override
	public boolean hasAllLanguages() {
		return true;
	}

	protected void reportError(String key) {
		if (!List.of("Checkbox", "Dropdown", "ScreenReader.degrees",
				"Unchecked", "Pressed", "double", "triple", "ScreenReader.plus",
				"Name.shape", "Selected", "ScreenReader.degree", "prime",
				"InlineText", "AofB", "ScreenReader.startRoot", "ScreenReader.squared",
				"ScreenReader.startPower", "ScreenReader.endRoot", "ScreenReader.cubed",
				 "ScreenReader.minus", "ScreenReader.startSqrtCbrt",
				 "ScreenReader.endSqrt",
				"ScreenReader.endPower", "ScreenReader.startFraction", "ScreenReader.times",
				"ScreenReader.endFraction", "ScreenReader.fractionOver").contains(key)) {
			throw new AssertionError("Key not found " + key);
		}
	}
}
