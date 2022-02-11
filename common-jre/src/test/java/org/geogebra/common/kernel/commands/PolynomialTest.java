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
		GeoFunction function = add("x^1000");
		assertTrue(function.isPolynomialFunction(false, false));
		function = add("x^1001");
		assertFalse(function.isPolynomialFunction(false, false));
	}
}
