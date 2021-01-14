package org.geogebra.common.kernel;

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.test.TestErrorHandler;
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
				TestErrorHandler.INSTANCE,
				new EvalInfo(true).withFractions(true).addDegree(true),
				null);
		return result[0];
	}

	@Test
	public void testLaTeXWithZero() {
		String funct = "sin((2x)/(3) (4-5)) + 0";
		tsc(funct,
				"sin open parenthesis start fraction 2 times x over 3 end fraction "
						+ "times open parenthesis 4 minus 5 close parenthesis close parenthesis");
	}

	@Test
	public void testLaTeXWithNegative() {
		String funct = "sin((2x)/(3) (4-5)) + -2";
		tsc(funct,
				"sin open parenthesis start fraction 2 times x over 3 end fraction times"
						+ " open parenthesis 4 minus 5 close parenthesis close parenthesis minus 2");
	}

	@Test
	public void testLaTeXWithOneTimes() {
		String funct = "1 * (sin((2x)/(3) (4-5)) + -2)";
		tsc(funct,
				"1 times open parenthesis sin open parenthesis start fraction 2 times x over"
						+ " 3 end fraction times open parenthesis 4 minus 5 close parenthesis close"
						+ " parenthesis minus 2 close parenthesis");
	}

	@Test
	public void testFunctions() {
		tsc("x^2+2x-1", "x squared plus 2 times x minus 1");
		tsc("sqrt(x+1)", "start square root x plus 1 end square root");
		tsc("(x+1)/(x-1)",
				"start fraction x plus 1 over x minus 1 end fraction");
		tsc("sin(2x)", "sin open parenthesis 2 times x close parenthesis");
		tsc("1*(x+0)", "1 times open parenthesis x close parenthesis");
		tsc("1*(x+0)/1", "1 times start fraction x over 1 end fraction");
	}

	@Test
	public void testFraction() {
		tsc("1/2", "0.5");
		tsc("1+1/2", "start fraction 3 over 2 end fraction");
	}
}
