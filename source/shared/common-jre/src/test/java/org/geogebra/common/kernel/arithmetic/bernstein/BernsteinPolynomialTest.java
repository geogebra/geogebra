package org.geogebra.common.kernel.arithmetic.bernstein;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.arithmetic.BoundsRectangle;
import org.geogebra.common.kernel.arithmetic.Polynomial;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.common.util.debug.Log;
import org.junit.Before;
import org.junit.Test;

public class BernsteinPolynomialTest extends BaseUnitTest {

	private BernsteinPolynomial bernstein;
	private GeoImplicitCurve curve;
	private EuclidianView view;
	private BernsteinPolynomialConverter converter;

	@Before
	public void setUp() {
		add("ZoomIn(0,0,1,1)");
		view = getApp().getEuclidianView1();
		converter = new BernsteinPolynomialConverter();
	}

	private void newBernsteinPolynomialPolynomialFrom(String definition) {
		GeoElement geo = add(definition);
		if (geo.isGeoImplicitCurve()) {
			curve = ((GeoImplicitCurve) geo);
		}
		bernstein = converter.from(geo, new BoundsRectangle(0, 1, 0, 1));
	}

	@Test
	public void testEvaluatingBernsteinForm() {
		shouldEvaluateTheSame("3x^3 + 2x^2 + x - 1=0");
		shouldEvaluateTheSame("3x^3 + 2x^2 + 6x + 11=0");
		shouldEvaluateTheSame("2x^4 + 5x^3 + x^2 + x - 1=0");
		shouldEvaluateTheSame("x^3+x=0");
		shouldEvaluateTheSame("x^3+1=0");
		shouldEvaluateTheSame("x^4+5x^3=0");
	}

	private void shouldEvaluateTheSame(String definition) {
		newBernsteinPolynomialPolynomialFrom(definition);
		for (double v = -10.0; v < 10.0; v += 0.01) {
			assertEquals(curve.evaluate(v, 0), bernstein.evaluate(v), 1E-8);
		}
	}

	@Test
	public void testEvaluate() {
		shouldEvaluate("3x^3 + 2x^2 + x - 1=0", 0.5);
	}

	private void shouldEvaluate(String definition, double v) {
		newBernsteinPolynomialPolynomialFrom(definition);
		assertEquals(curve.evaluate(v, 0), bernstein.evaluate(v), 1E-8);
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
		BernsteinPolynomial bernsteinPolynomial = converter.fromPolynomial(polynomial,
				0, 2, new BoundsRectangle(0, 1, 0, 1));
		assertEquals("y\u00B2 + y (1 - y)", bernsteinPolynomial.toString());
	}

	@Test
	public void testToString() {
		newBernsteinPolynomialPolynomialFrom("3x^3 + 2x^2 + x - 1=0");
		assertEquals("5x\u00B3 + x\u00B2 (1 - x) - 2x (1 - x)\u00B2 - (1 - x)\u00B3",
				bernstein.toString());
	}

	@Test
	public void testBernsteinFromCoefficients() {
		bernsteinShouldBe("2x\u00B2", 0, 0, 2);
		bernsteinShouldBe("4x\u00B2 + 2x (1 - x)", 0, 2, 2);
		bernsteinShouldBe("6x\u00B2 + 6x (1 - x) + 2(1 - x)\u00B2", 2, 2, 2);
		bernsteinShouldBe("14x\u00B3 + 22x\u00B2 (1 - x) + 17x (1 - x)\u00B2"
				+ " + 5(1 - x)\u00B3", 5, 2, 3, 4);
		bernsteinShouldBe("6x + 2(1 - x)", 2, 4);
		bernsteinShouldBe("20x\u00B2 + 10x (1 - x) + 2(1 - x)\u00B2", 2, 6, 12);
	}

	@Test
	public void testDerivatives() {
		derivativeShouldBe("4", 2, 4);
		derivativeShouldBe("4x", 0, 0, 2);
		derivativeShouldBe("6x + 2(1 - x)", 0, 2, 2);
		derivativeShouldBe("6x + 2(1 - x)", 2, 2, 2);
		derivativeShouldBe("20x\u00B2 + 10x (1 - x) + 2(1 - x)\u00B2", 5, 2, 3, 4);
	}

	private void derivativeShouldBe(String expected, double... coeffs) {
		new1varFromCoeffs(coeffs);
		assertEquals(expected, bernstein.derivative().toString());
	}

	private void bernsteinShouldBe(String expected, double... coeffs) {
		new1varFromCoeffs(coeffs);

		assertEquals(expected, bernstein.toString());
	}

	private void new1varFromCoeffs(double... coeffs) {
		BernsteinBuilder1Var builder = new BernsteinBuilder1Var();
		bernstein =
				builder.build(coeffs, coeffs.length - 1,
						'x', view.getXmin(), view.getXmax()
				);
	}

	@Test
	public void secondDerivative() {
		derivativeShouldBe("6x + 2(1 - x)", 0, 2, 2);
		assertEquals("4", bernstein.derivative().derivative().toString());
	}

	@Test
	public void testTwoVarEvaluate() {
		shouldTwoVarEvaluateTheSame("x^3 + 2x*y^2 + 2x + y=0");
		shouldTwoVarEvaluateTheSame("4x^3 + x*y^2 + 5x + y=0");
		shouldTwoVarEvaluateTheSame("x^6 - 4*y^3 + 3*x^4*y=0 ");
	}

	private void shouldTwoVarEvaluateTheSame(String definition) {
		newBernsteinPolynomialPolynomialFrom(definition);
		BernsteinPolynomial2Var twoVar = (BernsteinPolynomial2Var) bernstein;
		for (double x = -10; x < 10; x += 0.1) {
			for (double y = -10; y < 10; y += 0.1) {
				assertEquals(curve.evaluate(x, y), twoVar.evaluate(x, y), 1E-4);

			}
		}
	}

	@Test
	public void testTwoVarPartialXDerivatives() {
		shouldPartialDerivativeBe("(18y\u00B3 + 42y\u00B2 (1 - y) + 30y (1 - y)\u00B2"
						+ " + 6(1 - y)\u00B3) x\u2075 + (24y\u00B3 + 48y\u00B2 (1 - y)"
						+ " + 24y (1 - y)\u00B2) x\u2074 (1 - x) + (12y\u00B3 + 24y\u00B2 (1 - y)"
				+ " + 12y (1 - y)\u00B2) x\u00B3 (1 - x)\u00B2",
				"x^6 - 4y^3 + 3x^4*y=0", "x");

		shouldPartialDerivativeBe("(7y\u00B2 + 10y (1 - y) + 5(1 - y)\u00B2) x\u00B2"
						+ " + (8y\u00B2 + 8y (1 - y) + 4(1 - y)\u00B2) x (1 - x) + (4y\u00B2"
						+ " + 4y (1 - y) + 2(1 - y)\u00B2) (1 - x)\u00B2",
				"x^3 +2x*y^2 +2x + y = 0", "x");
		shouldPartialDerivativeBe("2y + (1 - y)", "x + x*y + y", "x");
	}

	private void shouldPartialDerivativeBe(String expected, String definition, String variable) {
		newBernsteinPolynomialPolynomialFrom(definition);
		assertEquals(expected, bernstein.derivative(variable).toString());
	}

	@Test
	public void testTwoVarPartialYDerivatives() {
		shouldPartialDerivativeBe("(5y + (1 - y)) x\u00B3 + (11y + 3(1 - y)) x\u00B2"
						+ " (1 - x) + (7y + 3(1 - y)) x (1 - x)\u00B2 "
						+ "+ (y + (1 - y)) (1 - x)\u00B3",
				"x^3 +2x*y^2 +2x + y = 0", "y");

		shouldPartialDerivativeBe("2x + (1 - x)", "x + x*y + y", "y");
	}

	@Test
	public void testTwoVarToString() {
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
	public void testSpit() {
		double[] bcoeffs = new double[]{2, 8, 12, 7};
		bernstein = new BernsteinPolynomial1Var(bcoeffs, 'x' , 0, 1);
		Log.debug("Original: " + bernstein);
		BernsteinPolynomial[] splits = bernstein.split();
		Log.debug("splits[0]: " + splits[0]);
		Log.debug("splits[1]: " + splits[1]);

		assertEquals(bernstein.evaluate(0), splits[0].evaluate(0), 0);
		assertEquals(bernstein.evaluate(.25), splits[0].evaluate(0.5), 0);
		assertEquals(bernstein.evaluate(.5), splits[0].evaluate(1), 0);

		assertEquals(bernstein.evaluate(.5), splits[1].evaluate(0), 0);
		assertEquals(bernstein.evaluate(.75), splits[1].evaluate(0.5), 0);
		assertEquals(bernstein.evaluate(1), splits[1].evaluate(1), 0);
	}

	@Test
	public void testSpit2Var() {
		spit2D("x^2 + y^2");
		spit2D("x^3 + y^3 = 0");
		spit2D("x^6 + 2x^2y^3 + 5y^4 = 0");
	}

	private void spit2D(String definition) {
		newBernsteinPolynomialPolynomialFrom(definition);
		BernsteinPolynomial[][] splits = bernstein.split2D();
		for (int i = 0; i < 10; i++) {
			double x = i / 5.0;
			for (int j = 0; j < 10; j++) {
				double y = j / 5.0;
				splitShouldBeSame(splits[0][0].evaluate(x, y), x / 2, y / 2);
				splitShouldBeSame(splits[0][1].evaluate(x, y), x / 2, (y + 1) / 2);
				splitShouldBeSame(splits[0][0].evaluate(x, y), x / 2, y / 2);
				splitShouldBeSame(splits[0][1].evaluate(x, y), x / 2, (y + 1) / 2);
			}
		}
	}

	private void splitShouldBeSame(double actual, double x, double y) {
		assertEquals(bernstein.evaluate(x, y), actual, 1E-6);
	}

	@Test
	public void testHasNoSolution() {
		shouldHaveNoSolution(2, 8, 12, 7);
		shouldHaveNoSolution(-2, -3, -0.75, -7);
	}

	@Test
	public void testMightHaveSolution() {
		mightHaveSolution(2, -8, 12, 7);
		mightHaveSolution(-1, 8, 12, 7);
		mightHaveSolution(-0.2, -8, -12, 7);
	}

	private void mightHaveSolution(double... bcoeffs) {
		testSolution(false, bcoeffs);
	}

	private void shouldHaveNoSolution(double... bcoeffs) {
		testSolution(true, bcoeffs);
	}

	private void testSolution(boolean shouldHave, double... bcoeffs) {
		bernstein = new BernsteinPolynomial1Var(bcoeffs, 'x', 0, 1);
		assertEquals(shouldHave, bernstein.hasNoSolution());
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
	public void testSimplifiedEvaluation1Var() {
		newBernsteinPolynomialPolynomialFrom("3x^3 + 2x^2 + x - 1=0");
		BernsteinPolynomial1Var b1var = (BernsteinPolynomial1Var) bernstein;
		double expected = b1var.evaluate(1);
		double[] coeffs = b1var.dividedCoeffs;
		assertEquals(expected, coeffs[3], 0);
		assertEquals(bernstein.evaluate(0), coeffs[0], 0);
	}

	@Test
	public void testSimplifiedEvaluation2Var() {
		newBernsteinPolynomialPolynomialFrom("x^6 + 2x^2y^3 + 5y^4 = 0");
		BernsteinPolynomial2Var b2var = (BernsteinPolynomial2Var) bernstein;
		double expected00 = b2var.evaluate(0, 0);
		double expected10 = b2var.evaluate(1, 0);
		double expected11 = b2var.evaluate(1, 1);
		double expected01 = b2var.evaluate(0, 1);
		BernsteinPolynomial[] coeffs = b2var.dividedCoeffs;
		assertEquals(expected00, coeffs[0].evaluate(0), 0);
		assertEquals(expected01, coeffs[0].evaluate(1), 0);
		assertEquals(expected10, coeffs[6].evaluate(0), 0);
		assertEquals(expected11, coeffs[6].evaluate(1), 0);
	}
}
