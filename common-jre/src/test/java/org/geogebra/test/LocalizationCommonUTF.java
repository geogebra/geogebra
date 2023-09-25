package org.geogebra.test;

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
}
