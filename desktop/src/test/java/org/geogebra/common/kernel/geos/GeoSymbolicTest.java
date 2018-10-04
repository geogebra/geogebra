package org.geogebra.common.kernel.geos;

import org.geogebra.commands.AlgebraTest;
import org.geogebra.commands.CommandsTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.main.App;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GeoSymbolicTest {
	private static App app;
	private static AlgebraProcessor ap;

	@BeforeClass
	public static void setup(){
		app = AlgebraTest.createApp();
		app.getKernel().setSymbolicMode(SymbolicMode.SYMBOLIC_AV);
		ap = app.getKernel().getAlgebraProcessor();
	}

	public static void t(String input, String... expected) {
		CommandsTest.testSyntaxSingle(input, expected, ap,
				StringTemplate.testTemplate);
	}

	@Before
	public void clean() {
		app.getKernel().clearConstruction(true);
	}

	@Test
	public void expression() {
		t("a=p+q", "p + q");
	}

	@Test
	public void dependentExpression() {
		t("a=p+q", "p + q");
		t("b=2*a", "2 * p + 2 * q");
	}

	@Test
	public void latex() {
		t("a=sqrt(8)", "sqrt(2) * 2");
		String text = getLatex("a");
		Assert.assertEquals("a \\, = \\,\\sqrt{2} \\cdot 2", text);
	}

	private static String getLatex(String string) {
		GeoElement geo1 = app.getKernel().lookupLabel(string);
		return geo1.getLaTeXAlgebraDescription(
				geo1.needToShowBothRowsInAV() != DescriptionMode.DEFINITION,
				StringTemplate.latexTemplate);
	}

	@Test
	public void variables() {
		t("f(x,y)=x+y", "x + y");
		Assert.assertEquals("f\\left(x, y \\right) \\, = \\,x + y",
				getLatex("f"));
	}

}
