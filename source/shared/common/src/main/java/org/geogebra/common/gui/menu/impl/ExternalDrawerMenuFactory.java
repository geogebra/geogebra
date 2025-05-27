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
