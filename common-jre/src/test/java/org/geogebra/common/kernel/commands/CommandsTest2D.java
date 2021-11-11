package org.geogebra.common.kernel.commands;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.App;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.junit.Before;
import org.junit.Test;

public class CommandsTest2D {

	private AlgebraProcessor ap;

	@Before
	public void setup() {
		App app = AppCommonFactory.create();
		app.setLanguage("en");
		ap = app.getKernel().getAlgebraProcessor();
	}

	private void t(String input, String expect) {
		AlgebraTestHelper.checkSyntaxSingle(input, new String[] { expect }, ap,
				StringTemplate.testTemplate);
	}

	@Test
	public void orthogonalLineTest() {
		t("OrthogonalLine((0,0),x=y)", "x + y = 0");
		t("OrthogonalLine((0,0),x=y,space)", "x + y = 0");
	}
}
