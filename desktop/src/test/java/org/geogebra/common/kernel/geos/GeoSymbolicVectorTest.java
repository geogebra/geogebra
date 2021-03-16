package org.geogebra.common.kernel.geos;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.junit.Test;

public class GeoSymbolicVectorTest extends BaseSymbolicTest {

	@Test
	public void testLowercaseIsVector() {
		String[] inputs = {"u = (1, 2)", "v = (3, 4, 5)", "w = (a, b, 5)",
				"a = (r+ 3, t + 2, f + 1)"};
		for (String input: inputs) {
			GeoSymbolic symbolic = add(input);
			assertIsVector(symbolic);
		}
	}

	@Test
	public void testAdditionSubtractionResultsInVector() {
		add("a = (1, 2)");
		add("b = (v, w)");
		add("c = (2, 4, u)");
		String[] variables = {"a", "b", "c"};
		String[] operations = {"+", "-"};
		for (String var1: variables) {
			for (String var2: variables) {
				for (String op: operations) {
					GeoSymbolic symbolic = add(var1 + op + var2);
					assertIsVector(symbolic);
				}
			}
		}
	}

	@Test
	public void testCrossProductResultsInVector() {
		String crossProduct = "\u2297";
		add("a = (f, g)");
		add("b = (h, q, z)");
		String[] inputs = {"a" + crossProduct + "b", "b" + crossProduct + "a",
				"b" + crossProduct + "b"};
		for (String input: inputs) {
			GeoSymbolic symbolic = add(input);
			assertIsVector(symbolic);
		}
	}

	@Test
	public void testScalarMultiplicationResultsInVector() {
		add("a = (1, 2)");
		add("b = (v, w)");
		add("c = (2, 4, u)");
		String[] variables = {"a", "b", "c"};
		String[] scalars = {"1", "-5", "p", "999"};
		for (String variable: variables) {
			for (String scalar: scalars) {
				GeoSymbolic symbolic = add(scalar + " " + variable);
				assertIsVector(symbolic);
			}
		}
	}

	@Test
	public void testDotProduct() {
		t("Dot[Vector[(1,2)],Vector[(3,4)]]", "11");
		t("Dot[Vector[(p,q)],Vector[(r,s)]]", "p * r + q * s");
	}

	@Test
	public void testCrossProduct() {
		t("Cross[Vector[(1,2)],Vector[(3,4)]]", "-2");
		t("Cross[Vector[(p,q)], Vector[(r,s)]]", "p * s - q * r");
	}

	@Test
	public void testVectors() {
		// these should give Vector not point
		t("Length(Vector((3,4)))", "5");
		t("x(Vector((3,4)))", "3");
		t("y(Vector((3,4)))", "4");
		t("z(Vector((3,4)))", "0");
		t("x(Vector((3,4,5)))", "3");
		t("y(Vector((3,4,5)))", "4");
		t("z(Vector((3,4,5)))", "5");
		t("abs(Vector((1,2)))", "sqrt(5)");
		t("UnitVector((1,2))", "(1 / 5 * sqrt(5), 2 / 5 * sqrt(5))");
		t("UnitVector((p,q))", "(p / sqrt(p^(2) + q^(2)), q / sqrt(p^(2) + q^(2)))");
		t("UnitPerpendicularVector((1,2))", "(-2 / sqrt(5), 1 / sqrt(5))");
		t("UnitPerpendicularVector((p,q))",
				"((-q) / sqrt(p^(2) + q^(2)), p / sqrt(p^(2) + q^(2)))");
		t("PerpendicularVector((1,2))", "(-2, 1)");
		t("PerpendicularVector((p,q))", "(-q, p)");
		t("Dot((p,q),(r,s))", "p * r + q * s");
		t("Dot((1,2),(3,4))", "11");
	}

	private void assertIsVector(GeoSymbolic symbolic) {
		assertThat(symbolic.getTwinGeo(),
				anyOf(nullValue(), instanceOf(GeoVectorND.class)));
		ExpressionValue value = symbolic.getValue().unwrap();
		assertThat(value, instanceOf(MyVecNDNode.class));
		assertThat(((MyVecNDNode) value).isCASVector(), is(true));
	}
}
