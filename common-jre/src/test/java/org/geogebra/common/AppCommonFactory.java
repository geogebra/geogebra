package org.geogebra.common;

import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.headless.LocalizationCommon;

public class AppCommonFactory {
	/**
	 * @return app instance for 2D testing
	 */
	public static AppCommon create() {
		return new AppCommon(new LocalizationCommon(2), new AwtFactoryCommon());
	}

}
