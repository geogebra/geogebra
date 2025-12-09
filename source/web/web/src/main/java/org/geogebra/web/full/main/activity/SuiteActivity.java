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
		if (isSubAppScientific()) {
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
		if (isSubAppScientific()) {
			return getSubapp().getHeaderResizer(frame);
		}
		return super.getHeaderResizer(frame);
	}

	private boolean isSubAppScientific() {
		return GeoGebraConstants.SCIENTIFIC_APPCODE.equals(getConfig().getSubAppCode());
	}

	@Override
	public boolean useValidInput() {
		return !isSubAppScientific();
	}

	@Override
	public void start(AppW app) {
		super.start(app);
		getSubapp().initTableOfValues(app);
	}
}
