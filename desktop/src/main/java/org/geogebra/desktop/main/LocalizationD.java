package org.geogebra.desktop.main;

import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.jre.headless.Utf8Control;

/**
 * Desktop localization
 */
public class LocalizationD extends LocalizationCommon {

	/**
	 * @param dimension 3 for 3D
	 */
	public LocalizationD(int dimension) {
		super(dimension);
		setResourceBundleControl(new Utf8Control());
	}
}
