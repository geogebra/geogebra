package org.geogebra.common.kernel.commands;

import static org.geogebra.test.TestStringUtil.unicode;
import static org.hamcrest.MatcherAssert.assertThat;
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
		assertEquals("x + y", polynomial("f"));
		add("g(x,y)=0");
		assertEquals("0", polynomial("g"));
	}

	@Test
	public void testMultiVariablesXYNonPolynomials() {
		assertEquals("?", polynomial("(x+y)^-2"));
		assertEquals("?", polynomial("(x+y)^(3/2)"));
	}

	@Test
	public void testMultiVariablesVarDegreePolynomials() {
		add("k=-4");
		GeoFunctionNVar varDegree = add("Polynomial((x+y)^k)");
		assertThat(varDegree, hasValue("?"));
		add("SetValue(k,2)");
		assertThat(varDegree, hasValue(unicode("x^2 + 2x y + y^2")));
	}

	private String polynomial(String function) {
		return add("Polynomial(" + function + ")")
				.toValueString(StringTemplate.defaultTemplate);
	}

	@Test
	public void testMoreThanTwoVariablesShouldBeUndefined() {
		add("f(x,y,z)=x+y+z");
		GeoFunctionNVar poly = add("Polynomial(f)");
		assertEquals("?", poly.toValueString(StringTemplate.defaultTemplate));
		assertEquals("undefined", poly.getAlgebraDescriptionForPreviewOutput());
		assertEquals("?", poly.toOutputValueString(StringTemplate.defaultTemplate));
		assertEquals("?", poly.getLaTeXDescriptionRHS(true,
				StringTemplate.defaultTemplate));
	}

	@Test
	public void testMultiCharVariables() {
		add("f(abc,def)=(abc+def)^(2)");
		assertEquals("abc\u00B2 + 2abc def + def\u00B2", polynomial("f"));
	}

	@Test
	public void testMultiVariablePolynomials() {
		add("f(a,b)=a+b");
		assertEquals("a + b", polynomial("f"));
	}

	@Test
	public void testExtraBrackets() {
		assertEquals("-x y", polynomial("-x y"));
		assertEquals("-x\u00b2 y", polynomial("-x^(2) y"));
		assertEquals("-5x y", polynomial("-5x y"));
		assertEquals("-5x\u00b3 y", polynomial("-5x^(3) y"));
	}
}