package org.geogebra.common.kernel.commands;

import java.util.Arrays;
import java.util.Locale;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.BeforeClass;

public class AlgebraTest extends Assert {

	static AlgebraProcessor ap;
	static AppDNoGui app;

	@BeforeClass
	public static void setup() {
		app = AlgebraTest.createApp();
		ap = app.getKernel().getAlgebraProcessor();
	}

	/**
	 * @return test app
	 */
	public static AppDNoGui createApp() {
		AppDNoGui app2 = new AppDNoGui(new LocalizationD(3), false);
		app2.setLanguage(Locale.US);

		// make sure x=y is a line, not plane
		app2.getGgbApi().setPerspective("1");
		// Setting the general timeout to 11 seconds. Feel free to change this.
		app2.getKernel().getApplication().getSettings().getCasSettings()
				.setTimeoutMilliseconds(11000);
		return app2;
	}

	protected static void t(String s, String... expected) {
		CommandsTest.testSyntax(s, AlgebraTestHelper.getMatchers(expected), app,
				ap,
				StringTemplate.xmlTemplate);
	}

	protected static void tRound(String s, String... expected) {
		CommandsTest.testSyntax(s, AlgebraTestHelper.getMatchers(expected), app,
				ap,
				StringTemplate.editTemplate);
	}

	protected static void t(String s, Matcher<String> expected) {
		CommandsTest.testSyntax(s, Arrays.asList(expected), app, ap,
				StringTemplate.xmlTemplate);
	}
}
