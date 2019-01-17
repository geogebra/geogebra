package org.geogebra.common.kernel;

import org.geogebra.commands.TestErrorHandler;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class ScreenReaderTest {

	static AppDNoGui app;

	@BeforeClass
	public static void initialize() {
		app = new AppDNoGui(new LocalizationD(3), true);
	}

	@Before
	public void cleanup() {
		app.getKernel().clearConstruction(true);
	}

	private static void tsc(String string, String string2) {
		GeoElementND geo = eval(string);
		Assert.assertEquals(string2,
				geo.toValueString(StringTemplate.screenReader).trim().replaceAll(" +", " "));
	}

	private static GeoElementND eval(String string) {
		AlgebraProcessor ap = app.getKernel().getAlgebraProcessor();
		GeoElementND[] result = ap.processAlgebraCommandNoExceptionHandling(string, false,
				new TestErrorHandler(), new EvalInfo(true).withFractions(true).addDegree(true),
				null);
		return result[0];
	}

	@Test
	public void testLaTeXWithZero() {
		String funct = "sin((2x)/(3) (4-5)) + 0";
		tsc(funct,
				"sin open parenthesis start fraction 2 times x over 3 end fraction times open parenthesis -1 close parenthesis close parenthesis");
	}

	@Test
	public void testLaTeXWithNegative() {
		String funct = "sin((2x)/(3) (4-5)) + -2";
		tsc(funct,
				"sin open parenthesis start fraction 2 times x over 3 end fraction times open parenthesis -1 close parenthesis close parenthesis - 2");
	}

	@Test
	public void testLaTeXWithOneTimes() {
		String funct = "1 * (sin((2x)/(3) (4-5)) + -2)";
		tsc(funct,
				"1 times open parenthesis sin open parenthesis start fraction 2 times x over 3 end fraction times open parenthesis -1 close parenthesis close parenthesis - 2 close parenthesis");
	}
}
