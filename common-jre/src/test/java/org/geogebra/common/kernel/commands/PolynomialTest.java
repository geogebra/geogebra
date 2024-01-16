package org.geogebra.common.kernel.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
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

	@Test
	public void testMultiVariablesXYPolynomials() {
		add("f(x,y)=x+y");
		GeoFunctionNVar poly = add("Polynomial(f)");
		assertEquals("x + y", poly.toValueString(StringTemplate.defaultTemplate));
	}

	@Test
	public void testMoreThanTwoVariablesShouldBeUndefined() {
		add("f(x,y,z)=x+y+z");
		GeoFunctionNVar poly = add("Polynomial(f)");
		assertEquals("?", poly.toValueString(StringTemplate.defaultTemplate));
	}

	@Test
	public void testMultiCharVariables() {
		add("f(abc,def)=(abc+def)^(2)");
		GeoFunctionNVar poly = add("Polynomial(f)");
		assertEquals("abc\u00B2 + 2abc def + def\u00B2", poly.toValueString(StringTemplate.defaultTemplate));
	}

	@Test
	public void testMultiVariablePolynomials() {
		add("f(a,b)=a+b");
		GeoFunctionNVar poly = add("Polynomial(f)");
		assertEquals("a + b", poly.toValueString(StringTemplate.defaultTemplate));
	}
}