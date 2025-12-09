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

package org.geogebra.common.kernel;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.test.TestErrorHandler;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class ScreenReaderTest {

	static AppCommon app;

	@BeforeClass
	public static void initialize() {
		app = AppCommonFactory.create3D();
	}

	@Before
	public void cleanup() {
		app.getKernel().clearConstruction(true);
	}

	private static void tsc(String string, String expected) {
		GeoElementND geo = eval(string);
		assertEquals(expected,
				geo.toValueString(StringTemplate.screenReaderAscii).trim().replaceAll(" +", " "));
	}

	private static GeoElementND eval(String string) {
		AlgebraProcessor ap = app.getKernel().getAlgebraProcessor();
		GeoElementND[] result = ap.processAlgebraCommandNoExceptionHandling(string, false,
				TestErrorHandler.INSTANCE,
				new EvalInfo(true).withSymbolic(true).addDegree(true),
				null);
		return result[0];
	}

	@Test
	public void testLaTeXWithZero() {
		String funct = "sin((2x)/(3) (4-5)) + 0";
		tsc(funct,
				"sin open parenthesis start fraction 2 times x over 3 end fraction "
						+ "times open parenthesis 4 minus 5 close parenthesis close parenthesis "
						+ "plus 0");
	}

	@Test
	public void testLaTeXWithNegative() {
		String funct = "sin((2x)/(3) (4-5)) + -2";
		tsc(funct,
				"sin open parenthesis start fraction 2 times x over 3 end fraction times"
						+ " open parenthesis 4 minus 5 close parenthesis close parenthesis"
						+ " minus 2");
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
		tsc("1*(x+0)", "1 times open parenthesis x plus 0 close parenthesis");
		tsc("1*(x+0)/1", "1 times start fraction x plus 0 over 1 end fraction");
	}

	@Test
	public void testFraction() {
		tsc("1/2", "0.5");
		tsc("1+1/2", "start fraction 3 over 2 end fraction");
	}

	@Test
	public void testDegree() {
		tsc("x + pi deg", "x plus pi degrees");
		tsc("pi deg", "pi degrees");
	}
}
