package org.geogebra.common;

import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.settings.config.AppConfigDefault;
import org.geogebra.test.LocalizationCommonUTF;

public class AppCommonFactory {

	public static AppCommon create() {
		return create(new AppConfigDefault());
	}

	/**
	 * @return app instance for 2D testing
	 */
	public static AppCommon create(AppConfig appConfig) {
		return new AppCommon(new LocalizationCommonUTF(2), new AwtFactoryCommon(), appConfig);
	}

	public static AppCommon3D create3D() {
		return create3D(new AppConfigDefault());
	}

	/**
	 * @return app instance for 3d testing
	 */
	public static AppCommon3D create3D(AppConfig appConfig) {
		return new AppCommon3D(new LocalizationCommonUTF(3), new AwtFactoryCommon(), appConfig);
	}
}
