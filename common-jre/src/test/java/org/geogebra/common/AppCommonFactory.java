package org.geogebra.common;

import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.test.LocalizationCommonUTF;

public class AppCommonFactory {

	/**
	 * @return app instance for 2D testing
	 */
	public static AppCommon create() {
		return new AppCommon(new LocalizationCommonUTF(2), new AwtFactoryCommon());
	}

	/**
	 * @return app instance for 3d testing
	 */
	public static AppCommon3D create3D() {
		return new AppCommon3D(new LocalizationCommonUTF(3), new AwtFactoryCommon());
	}
}
