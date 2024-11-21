package org.geogebra.common.kernel.arithmetic;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.QuadraticEquationRepresentable;
import org.geogebra.common.kernel.StringTemplate;
import org.junit.Test;

public class RequiresBracketsTest extends BaseUnitTest {

	@Test
	public void testFunctionMinusFunction() {
		add("f(x) = 3x - 1");
		add("g(x) = -2x + 4");

		add("eq1: f - g = 0");

		lookup("eq1").setToStringMode(LinearEquationRepresentable.Form.USER.rawValue);

		assertEquals("3x - 1 - (-2 x + 4) = 0",
				lookup("eq1").toValueString(StringTemplate.algebraTemplate));
	}

	@Test
	public void testFunctionsTimesFunction() {
		add("f(x) = 3x - 1");
		add("g(x) = x");

		add("eq0: f * f = 0");
		add("eq1: f * g = 0");
		add("eq2: g * f = 0");
		add("eq3: g * g = 0");

		lookup("eq0").setToStringMode(QuadraticEquationRepresentable.Form.USER.rawValue);
		lookup("eq1").setToStringMode(QuadraticEquationRepresentable.Form.USER.rawValue);
		lookup("eq2").setToStringMode(QuadraticEquationRepresentable.Form.USER.rawValue);
		lookup("eq3").setToStringMode(QuadraticEquationRepresentable.Form.USER.rawValue);

		assertEquals("(3x - 1) (3x - 1) = 0",
				lookup("eq0").toValueString(StringTemplate.algebraTemplate));
		assertEquals("(3x - 1) x = 0",
				lookup("eq1").toValueString(StringTemplate.algebraTemplate));
		assertEquals("x (3x - 1) = 0",
				lookup("eq2").toValueString(StringTemplate.algebraTemplate));
		assertEquals("x x = 0",
				lookup("eq3").toValueString(StringTemplate.algebraTemplate));
	}

	@Test
	public void testMultiplyListElements() {
		add("a = {1, x + 2, 3, x + 4}");
		add("b = {5, 10, x + 15, x + 20}");

		add("l1 = a * b");

		assertEquals("{5, (x + 2) * 10, 3 (x + 15), (x + 4) (x + 20)}",
				lookup("l1").toValueString(StringTemplate.algebraTemplate));
	}
}
