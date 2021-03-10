package org.geogebra.web.full.main.activity;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.settings.config.AppConfigCas;
import org.geogebra.common.main.settings.config.AppConfigGeometry;
import org.geogebra.common.main.settings.config.AppConfigGraphing3D;
import org.geogebra.common.main.settings.config.AppConfigUnrestrictedGraphing;

/**
 * Activity class for the GeoGebra Suite app
 */
public class SuiteActivity extends BaseActivity {

	/**
	 * New Suite activity
	 */
	public SuiteActivity(String subAppCode) {
		super(getAppConfig(subAppCode));
	}

	private static AppConfig getAppConfig(String subAppCode) {
		switch (subAppCode) {
		default:
		case GeoGebraConstants.GRAPHING_APPCODE:
			return new AppConfigUnrestrictedGraphing(GeoGebraConstants.SUITE_APPCODE);
		case GeoGebraConstants.GEOMETRY_APPCODE:
			return new AppConfigGeometry(GeoGebraConstants.SUITE_APPCODE);
		case GeoGebraConstants.CAS_APPCODE:
			return new AppConfigCas(GeoGebraConstants.SUITE_APPCODE);
		case GeoGebraConstants.G3D_APPCODE:
			return new AppConfigGraphing3D(GeoGebraConstants.SUITE_APPCODE);
		}
	}
}
