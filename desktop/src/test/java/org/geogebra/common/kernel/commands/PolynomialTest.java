package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.desktop.headless.AppDNoGui;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test inputs related to polynomials.
 */
public class PolynomialTest extends AlgebraTest {

	static AlgebraProcessor ap;
	static AppDNoGui app;

	@BeforeClass
	public static void setup() {
		app = AlgebraTest.createApp();
		ap = app.getKernel().getAlgebraProcessor();
	}

	@Test
	public void testPolynomialMaxDegree() {
		app.enableCAS(false);
		GeoFunction function = processComandCreatesFunction("x^1000");
		assertTrue(function.isPolynomialFunction(false, false));
		function = processComandCreatesFunction("x^1001");
		assertFalse(function.isPolynomialFunction(false, false));
	}

	private GeoFunction processComandCreatesFunction(String command) {
		GeoElementND[] elementNDS = ap.processAlgebraCommand(command, false);
		return (GeoFunction) elementNDS[0];
	}
}
