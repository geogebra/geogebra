package org.geogebra.web.full.main.activity;

import org.geogebra.common.main.settings.config.AppConfigSuite;

/**
 * Activity class for the GeoGebra Suite app
 */
public class SuiteActivity extends BaseActivity {

	/**
	 * New Suite activity
	 */
	public SuiteActivity() {
		super(new AppConfigSuite());
	}
}
