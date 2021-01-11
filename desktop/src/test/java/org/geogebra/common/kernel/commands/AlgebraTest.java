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
		CommandsTest.testSyntax(s, AlgebraTestHelper.getMatchers(expected), app,
				ap,
				StringTemplate.xmlTemplate);
	}

	protected void tRound(String s, String... expected) {
		CommandsTest.testSyntax(s, AlgebraTestHelper.getMatchers(expected), app,
				ap,
				StringTemplate.editTemplate);
	}

	protected void t(String s, Matcher<String> expected) {
		CommandsTest.testSyntax(s, Collections.singletonList(expected), app, ap,
				StringTemplate.xmlTemplate);
	}
}
