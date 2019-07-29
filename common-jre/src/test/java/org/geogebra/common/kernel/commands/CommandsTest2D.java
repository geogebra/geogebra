package org.geogebra.common.kernel.commands;

import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.App;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.junit.BeforeClass;
import org.junit.Test;

public class CommandsTest2D {

	private static App app;
	private static AlgebraProcessor ap;

	@BeforeClass
	public static void setup() {
		app = new AppCommon(new LocalizationCommon(2), new AwtFactoryCommon());
		app.setLanguage("en");
		ap = app.getKernel().getAlgebraProcessor();
	}

	private static void t(String input, String expect) {
		AlgebraTestHelper.testSyntaxSingle(input, new String[] { expect }, ap,
				StringTemplate.testTemplate);
	}

	@Test
	public void orthogonalLineTest() {
		t("OrthogonalLine((0,0),x=y)", "x + y = 0");
		t("OrthogonalLine((0,0),x=y,space)", "x + y = 0");
	}
}
