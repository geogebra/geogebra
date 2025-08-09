package org.geogebra.common.kernel.arithmetic.bernstein;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.arithmetic.BoundsRectangle;
import org.geogebra.common.kernel.arithmetic.Polynomial;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class BernsteinPolynomial2DTest extends BaseUnitTest {

	private BernsteinPolynomial2D bernstein;
	private GeoImplicitCurve curve;
	private BernsteinPolynomialConverter converter;

	@Before
	public void setUp() {
		add("ZoomIn(0,0,1,1)");
		converter = new BernsteinPolynomialConverter();
	}

	private void newBernsteinPolynomialPolynomialFrom(String definition) {
		GeoElement geo = add(definition);
		if (geo.isGeoImplicitCurve()) {
			curve = (GeoImplicitCurve) geo;
		}
		bernstein = converter.bernsteinPolynomial2DFrom(geo,
				new BoundsRectangle(0, 1, 0, 1));
	}

	@Test
	public void testTwoVars() {
		newBernsteinPolynomialPolynomialFrom("x^3 + 2x*y^2 + 2x + y=0");
		assertEquals("(6y\u00B2 + 7y (1 - y) + 3(1 - y)\u00B2) x\u00B3 + (11y\u00B2 + "
						+ "11y (1 - y) + 4(1 - y)\u00B2) x\u00B2 (1 - x) + (7y\u00B2 + 7y (1 - y)"
						+ " + 2(1 - y)\u00B2) x (1 - x)\u00B2 + (y\u00B2"
						+ " + y (1 - y)) (1 - x)\u00B3",
				bernstein.toString());
	}

	@Test
	public void testTwoVars2() {
		newBernsteinPolynomialPolynomialFrom("x + x*y + y");
		assertEquals("(3y + (1 - y)) x + (y) (1 - x)", bernstein.toString());
	}

	@Test
	public void testOneVariableToBernsteinPolynomial() {
		Polynomial polynomial = new Polynomial(getKernel(), "y");
		BernsteinPolynomial1D bernsteinPolynomial =
				converter.from1DPolynomial(polynomial, 0, 2, new BoundsRectangle(
						0, 1, 0, 1));
		assertEquals("y\u00B2 + y (1 - y)", bernsteinPolynomial.toString());
	}

	@Test
	public void testEvaluate() {
		shouldEvaluateTheSame("x^3 + 2x*y^2 + 2x + y=0");
		shouldEvaluateTheSame("4x^3 + x*y^2 + 5x + y=0");
		shouldEvaluateTheSame("x^6 - 4*y^3 + 3*x^4*y=0 ");
	}

	private void shouldEvaluateTheSame(String definition) {
		newBernsteinPolynomialPolynomialFrom(definition);
		for (double x = -10; x < 10; x += 0.1) {
			for (double y = -10; y < 10; y += 0.1) {
				assertEquals(curve.evaluate(x, y), bernstein.evaluate(x, y), 1E-4);

			}
		}
	}

	@Test
	public void testToString() {
		newBernsteinPolynomialPolynomialFrom("x^6 - 4y^3 + 3x^4*y=0");
		assertEquals("(9y\u00B2 (1 - y) + 6y (1 - y)\u00B2 + (1 - y)\u00B3) x\u2076 "
				+ "+ (- 18y\u00B3 + 12y\u00B2 (1 - y) + 6y (1 - y)\u00B2) x\u2075 (1 - x) "
				+ "+ (- 57y\u00B3 + 6y\u00B2 (1 - y) + 3y (1 - y)\u00B2) x\u2074 (1 - x)\u00B2 + "
				+ "(- 80y\u00B3) x\u00B3 (1 - x)\u00B3 + (- 60y\u00B3) x\u00B2 (1 - x)\u2074 + "
				+ "(- 24y\u00B3) x (1 - x)\u2075 + (- 4y\u00B3) (1 - x)\u2076",
				bernstein.toString());
		assertEquals(323084, bernstein.evaluate(8, 5), 1E-6);
	}

	@Test
	public void testSpit2Var() {
		spit2D("x^2 + y^2");
		spit2D("x^3 + y^3 = 0");
		spit2D("x^6 + 2x^2y^3 + 5y^4 = 0");
	}

	private void spit2D(String definition) {
		newBernsteinPolynomialPolynomialFrom(definition);
		BernsteinPolynomial2D[][] splits = bernstein.split();
		for (int i = 0; i < 10; i++) {
			double x = i / 5.0;
			for (int j = 0; j < 10; j++) {
				double y = j / 5.0;
				if (splits[0] != null) {
					if (splits[0][0] != null) {
						splitShouldBeSame(splits[0][0].evaluate(x, y), x / 2, y / 2);
						splitShouldBeSame(splits[0][0].evaluate(x, y), x / 2, y / 2);
					}
					if (splits[0][1] != null) {
						splitShouldBeSame(splits[0][1].evaluate(x, y), x / 2, (y + 1) / 2);
						splitShouldBeSame(splits[0][1].evaluate(x, y), x / 2, (y + 1) / 2);
					}
				}
			}
		}
	}

	private void splitShouldBeSame(double actual, double x, double y) {
		if (bernstein == null) {
			return;
		}
		assertEquals(bernstein.evaluate(x, y), actual, 1E-6);
	}

	@Test
	public void testSubstituteY() {
		newBernsteinPolynomialPolynomialFrom("x^3 + y^3 = 0");
		assertEquals("9x\u00B3 + 24x\u00B2 (1 - x) + 24x (1 - x)\u00B2"
						+ " + 8(1 - x)\u00B3",
				bernstein.substitute("y", 2).toString());
	}

	@Test
	public void testSubstituteX() {
		newBernsteinPolynomialPolynomialFrom("x^3 + y^3 = 0");
		assertEquals("y\u00B3", bernstein.substitute("x", 0).toString());
		assertEquals("0", bernstein.substitute("x", 1).toString());
		assertEquals("9y\u00B3 + 24y\u00B2 (1 - y) + 24y (1 - y)\u00B2"
						+ " + 8(1 - y)\u00B3",
				bernstein.substitute("x", 2).toString());
	}

	@Test
	public void testSimplifiedEvaluation() {
		newBernsteinPolynomialPolynomialFrom("x^6 + 2x^2y^3 + 5y^4 = 0");
		BernsteinPolynomial2D b2var = (BernsteinPolynomial2D) bernstein;
		double expected00 = b2var.evaluate(0, 0);
		double expected10 = b2var.evaluate(1, 0);
		double expected11 = b2var.evaluate(1, 1);
		double expected01 = b2var.evaluate(0, 1);
		BernsteinPolynomial1D[] coeffs = b2var.dividedCoeffs;
		assertEquals(expected00, coeffs[0].evaluate(0), 0);
		assertEquals(expected01, coeffs[0].evaluate(1), 0);
		assertEquals(expected10, coeffs[6].evaluate(0), 0);
		assertEquals(expected11, coeffs[6].evaluate(1), 0);
	}
}
