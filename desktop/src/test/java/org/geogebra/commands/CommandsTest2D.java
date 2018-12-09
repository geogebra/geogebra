package org.geogebra.commands;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.main.App;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.junit.BeforeClass;
import org.junit.Test;

public class CommandsTest2D extends AlgebraTest {

	private static App app;
	private static AlgebraProcessor ap;

	@BeforeClass
	public static void setup() {
		app= new AppDNoGui(new LocalizationD(2), false, 2);
		app.setLanguage("en");
		ap = app.getKernel().getAlgebraProcessor();
	}

	public static void t(String input, String expect) {
		AlgebraTest.testSyntaxSingle(input, new String[] { expect }, ap,
				StringTemplate.testTemplate);
	}

	@Test
	public void orthogonalLineTest() {
		t("OrthogonalLine((0,0),x=y)", "x + y = 0");
		t("OrthogonalLine((0,0),x=y,space)", "x + y = 0");
	}
}
