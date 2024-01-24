package org.geogebra.common.kernel.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.junit.Test;

/**
 * Test inputs related to polynomials.
 */
public class PolynomialTest extends BaseUnitTest {

	@Test
	public void testPolynomialMaxDegree() {
		getApp().enableCAS(false);
		GeoFunction function = add("x^300");
		assertTrue(function.isPolynomialFunction(false, false));
		function = add("x^301");
		assertFalse(function.isPolynomialFunction(false, false));
	}
}
