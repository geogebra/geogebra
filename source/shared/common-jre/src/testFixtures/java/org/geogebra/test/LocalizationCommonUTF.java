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
