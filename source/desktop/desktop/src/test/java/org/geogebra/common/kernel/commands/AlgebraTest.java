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

package org.geogebra.common.kernel.commands;

import java.util.Collections;
import java.util.Locale;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.settings.config.AppConfigDefault;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.hamcrest.Matcher;
import org.junit.Before;

public class AlgebraTest {

	protected AlgebraProcessor ap;
	protected AppDNoGui app;

	@Before
	public void setup() {
		app = AlgebraTest.createApp();
		ap = app.getKernel().getAlgebraProcessor();
	}

	/**
	 * @return test app
	 */
	public static AppDNoGui createApp(AppConfig config) {
		AppDNoGui app = new AppDNoGui(new LocalizationD(3), false);
		app.setLanguage(Locale.US);
		app.setConfig(config);

		// make sure x=y is a line, not plane
		app.getGgbApi().setPerspective("1");
		// Setting the general timeout to 11 seconds. Feel free to change this.
		app.getKernel().getApplication().getSettings().getCasSettings()
				.setTimeoutMilliseconds(11000);
		return app;
	}

	/**
	 * @return test app
	 */
	public static AppDNoGui createApp() {
		return createApp(new AppConfigDefault());
	}

	protected void t(String s, String... expected) {
		CommandsTestCommon.testSyntax(s, AlgebraTestHelper.getMatchers(expected), app,
				ap,
				StringTemplate.xmlTemplate);
	}

	protected void tRound(String s, String... expected) {
		CommandsTestCommon.testSyntax(s, AlgebraTestHelper.getMatchers(expected), app,
				ap,
				StringTemplate.editTemplate);
	}

	protected void t(String s, Matcher<String> expected) {
		CommandsTestCommon.testSyntax(s, Collections.singletonList(expected), app, ap,
				StringTemplate.xmlTemplate);
	}
}
