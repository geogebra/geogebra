package org.geogebra.web.full.main.activity;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.settings.config.AppConfigCas;
import org.geogebra.common.main.settings.config.AppConfigGeometry;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.geogebra.common.main.settings.config.AppConfigGraphing3D;
import org.geogebra.common.main.settings.config.AppConfigProbability;
import org.geogebra.common.main.settings.config.AppConfigScientific;
import org.geogebra.common.main.settings.config.AppConfigUnrestrictedGraphing;
import org.geogebra.web.full.main.HeaderResizer;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.GlobalHeader;

/**
 * Activity class for the GeoGebra Suite app
 */
public class SuiteActivity extends BaseActivity {

	private ScientificActivity scientificSubApp;

	/**
	 * New Suite activity
	 */
	public SuiteActivity(SuiteSubApp subAppCode, boolean casDisabled) {
		super(getAppConfig(subAppCode, casDisabled));
	}

	private static AppConfig getAppConfig(SuiteSubApp subAppCode, boolean casDisabled) {
		switch (subAppCode) {
		default:
		case GRAPHING:
			if (casDisabled) {
				return new AppConfigGraphing(GeoGebraConstants.SUITE_APPCODE);
			} else {
				return new AppConfigUnrestrictedGraphing(GeoGebraConstants.SUITE_APPCODE);
			}
		case GEOMETRY:
			return new AppConfigGeometry(GeoGebraConstants.SUITE_APPCODE);
		case CAS:
			return new AppConfigCas(GeoGebraConstants.SUITE_APPCODE);
		case G3D:
			return new AppConfigGraphing3D(GeoGebraConstants.SUITE_APPCODE);
		case PROBABILITY:
			return new AppConfigProbability(GeoGebraConstants.SUITE_APPCODE);
		case SCIENTIFIC:
			return new AppConfigScientific(GeoGebraConstants.SUITE_APPCODE);
		}
	}

	@Override
	public BaseActivity getSubapp() {
		if (GeoGebraConstants.SCIENTIFIC_APPCODE.equals(getConfig().getSubAppCode())) {
			if (scientificSubApp == null) {
				scientificSubApp = new ScientificActivity();
				GlobalHeader.INSTANCE.initButtonsIfOnHeader();
			}
			return scientificSubApp;
		}
		return this;
	}

	@Override
	public HeaderResizer getHeaderResizer(GeoGebraFrameW frame) {
		if (GeoGebraConstants.SCIENTIFIC_APPCODE.equals(getConfig().getSubAppCode())) {
			return getSubapp().getHeaderResizer(frame);
		}
		return super.getHeaderResizer(frame);
	}

	@Override
	public void start(AppW app) {
		super.start(app);
		getSubapp().initTableOfValues(app);
	}
}
