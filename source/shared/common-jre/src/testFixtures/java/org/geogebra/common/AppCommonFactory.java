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

package org.geogebra.common;

import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.settings.config.AppConfigDefault;
import org.geogebra.test.LocalizationCommonUTF;

public class AppCommonFactory {

	/**
	 * @return 2D app with default (Classic) config
	 */
	public static AppCommon create() {
		return create(new AppConfigDefault());
	}

	/**
	 * @return app instance for 2D testing
	 */
	public static AppCommon create(AppConfig appConfig) {
		return new AppCommon(new LocalizationCommonUTF(2), new AwtFactoryCommon(), appConfig);
	}

	/**
	 * @return 3D app with default (classic) config
	 */
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
