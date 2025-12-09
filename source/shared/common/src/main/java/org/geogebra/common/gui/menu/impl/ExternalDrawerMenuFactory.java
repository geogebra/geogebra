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

package org.geogebra.common.gui.menu.impl;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.menu.MenuItem;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;

/**
 * Menu factory for external websites
 */
public class ExternalDrawerMenuFactory extends DefaultDrawerMenuFactory {

	/**
	 * Create a new DrawerMenuFactory.
	 * @param platform platform
	 * @param version version
	 * @param versionNumber version number
	 * @param logInOperation if loginOperation is not null, it creates menu options that require
	 * login based on the {@link LogInOperation#isLoggedIn()} method.
	 * @param createExamEntry whether the factory should create the start exam button
	 * @param enableFileFeatures whether to show sign-in related file features
	 * @param isSuiteApp whether it is the Suite app
	 * @param createSwitchCalcEntry whether the factory should create switch calculator entry
	 */
	public ExternalDrawerMenuFactory(GeoGebraConstants.Platform platform,
			GeoGebraConstants.Version version,
			String versionNumber,
			LogInOperation logInOperation,
			boolean createExamEntry,
			boolean enableFileFeatures,
			boolean isSuiteApp,
			boolean createSwitchCalcEntry) {
		super(platform, version, versionNumber, logInOperation,
				createExamEntry, enableFileFeatures, isSuiteApp, createSwitchCalcEntry);
	}

	@Override
	protected MenuItem showHelpAndFeedback() {
		return null;
	}
}
