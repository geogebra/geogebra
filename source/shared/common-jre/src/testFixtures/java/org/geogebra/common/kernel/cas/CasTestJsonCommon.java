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

package org.geogebra.common.kernel.cas;

import static org.geogebra.test.matcher.IsEqualStringIgnoreWhitespaces.equalToIgnoreWhitespaces;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeFalse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geogebra.common.cas.giac.Ggb2giac;
import org.geogebra.common.kernel.GeoGebraCasInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.test.CASTestLogger;
import org.junit.Test;

/**
 * Runs categorized tests from giacTests.js
 *
 * Note: no need for Before / BeforeClass, testCat clears everything
 */
public abstract class CasTestJsonCommon {

	protected static HashMap<String, ArrayList<CasTest>> testcases = new HashMap<>();
	protected static GeoGebraCasInterface cas;
	protected static Kernel kernel;
	protected static App app;
	protected static CASTestLogger logger = new CASTestLogger();
	protected static List<String> missing = new ArrayList<>();

	static class CasTest {
		protected String input;
		protected String output;
		protected String rounding;

		public CasTest(String input, String output, String rounding) {
			this.input = input;
			this.output = output;
			this.rounding = rounding;
		}
	}

	protected static String skipComments(List<String> lines) {
		StringBuilder noComments = new StringBuilder();
		for (String part : lines) {
			if (!part.trim().startsWith("//")) {
				noComments.append(part);
			}
		}
		return noComments.toString();
	}

	protected static void addTestcases(String json) throws JSONException {
		JSONArray testsJSON = new JSONArray(
				json.substring("var __giac = ".length()));
		assertNotSame(0, testsJSON.length());
		int i = 1;

		while (i < testsJSON.length()) {
			Object testVal = testsJSON.opt(i);

			i++;
			if (!(testVal instanceof JSONObject)) {
				Log.error("Invalid JSON:" + testVal);
				continue;
			}
			JSONObject test = (JSONObject) testVal;
			String cat = "general";
			if (test.has("cat")) {
				cat = test.getString("cat");
			} else {
				fail("Missing category:" + testVal);
			}
			if (!testcases.containsKey(cat)) {
				testcases.put(cat, new ArrayList<>());
			}
			if (test.has("round")) {
				testcases.get(cat).add(new CasTest(test.getString("cmd"),
						test.getString("round"), null));
			} else {
				testcases.get(cat)
						.add(new CasTest(test.getString("cmd"),
								test.getString("result"),
								test.optString("rounding")));
			}
		}
	}

	protected static void handleParsingError(Exception e, String json) {
		String msg = e.getMessage();
		String marker = "at character ";
		int pos = msg.indexOf(marker);
		if (pos > 0) {
			String characterNo = msg.substring(pos + marker.length(),
					msg.indexOf(" ", pos + marker.length()));
			int err = Integer.parseInt(characterNo);
			int sampleLength = 50;
			fail("JSON parsing error at '" + json.substring(err - sampleLength / 2,
					err + sampleLength / 2) + "'");
		} else {
			fail(msg);
		}
	}

	protected static void checkMissingCategories() {
		for (String key : Ggb2giac.getMap().keySet()) {
			if (testcases != null && testcases.get(key) == null
					&& testcases.get(key.substring(0, key.indexOf("."))) == null
					&& forCAS(key)
					&& !"Binomial.2".equals(key)
					&& !"ExpSimplify.1".equals(key)
					&& !"SolveODEPoint.2".equals(key)
					&& !"SolveQuartic.1".equals(key)) {
				missing.add(key);
			}
		}
	}

	private static boolean forCAS(String key) {
		return !"Cell.2".equals(key) && !"CellRange.2".equals(key)
				&& !"Column.1".equals(key) && !"CopyFreeObject.1".equals(key)
				&& !"Object.1".equals(key) && !"Row.1".equals(key)
				&& !"Segment.2".equals(key);
	}

	protected void testCat(String name) {
		kernel.clearConstruction(true);
		if (testcases.get(name) == null) {
			fail("No testcase for " + name);
		}
		ArrayList<CasTest> cases = testcases.remove(name);
		runCases(cases);
	}

	protected void runCases(ArrayList<CasTest> cases) {
		assertNotEquals(0, cases.size());
		StringBuilder failures = new StringBuilder();
		for (CasTest cmd : cases) {
			Log.debug(cmd.input);
			if (!StringUtil.empty(cmd.rounding)) {
				app.setRounding(cmd.rounding);
			} else {
				app.setRounding("2");
			}
			t(failures, cmd.input, cmd.output);
		}
		assertEquals("", failures.toString());
	}

	private static void t(StringBuilder failures, String input,
			String expectedResult) {
		String[] validResults = expectedResult.split("\\|OR\\|");
		ta(failures, input, validResults, validResults);
	}

	private static void ta(StringBuilder failures,
			String input, String[] expectedResult, String... validResults) {
		String result;

		try {
			GeoCasCell f = new GeoCasCell(kernel.getConstruction());
			kernel.getConstruction().addToConstructionList(f, false);

			f.setInput(input);
			f.computeOutput();
			if ("GEOGEBRAERROR".equals(expectedResult[0])) {
				assertNull(f.getInputVE());
				return;
			}
			assertNotNull("Input should be parsed:" + input, f.getInputVE());
			boolean includesNumericCommand = f.includesNumericCommand();
			if (f.getValue() == null) {
				result = f.getOutput(StringTemplate.testTemplate);
			} else if (f.getValue()
					.unwrap() instanceof GeoElement) {
				result = f.getValue()
						.toValueString(StringTemplate.testTemplateJSON);
			} else {
				result = f.getValue()
						.traverse(getGGBVectAdder())
						.toString(includesNumericCommand
								? StringTemplate.testNumeric
								: StringTemplate.testTemplateJSON);
			}
		} catch (Exception | MyError t) {
			String sts = stacktrace(t);

			result = t.getClass().getName() + ":" + t.getMessage() + sts;
		}
		for (int i = 0; i < expectedResult.length; i++) {
			if ("RANDOM".equals(expectedResult[i])) {
				return;
			}
			try {
				result = normalizeActual(result);
				assertThat(result,
						equalToIgnoreWhitespaces(logger, input,
								normalizeExpected(expectedResult[i]),
								validResults));
				return;
			} catch (Throwable t) {
				if (i == expectedResult.length - 1) {
					Log.debug(t);
					String expected = expectedResult[0] == null ? "null"
							: normalizeExpected(expectedResult[0]);
					failures.append("\n  in: ").append(input)
							.append("\n exp: ").append(expected)
							.append("\n out: ").append(result)
							.append("\n raw: ").append(toRaw(input)).append('\n');
				}
			}
		}
	}

	private static String toRaw(String input) {
		try {
			return app.getKernel().getParser().parseGeoGebraCAS(input, null)
					.toString(StringTemplate.giacTemplate);
		} catch (ParseException e) {
			Log.debug(e);
		}
		return "?";
	}

	private static String normalizeActual(String result) {
		return result.replaceAll("c_[0-9]", "c_0")
				.replaceAll("k_[0-9]", "k_0")
				.replaceAll("c_\\{[0-9]+\\}", "c_0")
				.replaceAll("k_\\{[0-9]+\\}", "k_0")
				.replace("arccos", "acos").replace("arctan", "atan")
				.replace("Wenn(", "If(").replace("arcsin", "asin")
				.replace("NteWurzel", "nroot");
	}

	private static String normalizeExpected(String s) {
		return s.replaceAll("c_[0-9]+", "c_0")
				.replaceAll("n_[0-9]+", "k_0");
	}

	private static Traversing getGGBVectAdder() {
		return ev -> {
			if (ev.unwrap() instanceof MyVecNDNode
					&& ((MyVecNDNode) ev.unwrap()).isCASVector()) {
				return new Variable(kernel, "ggbvect").wrap()
						.apply(Operation.FUNCTION, ev);

			}
			return ev;
		};
	}

	/**
	 * @param t stacktrace
	 * @return flat string
	 */
	public static String stacktrace(Throwable t) {
		StringBuilder sts = new StringBuilder();
		StackTraceElement[] st = t.getStackTrace();

		for (int i = 0; i < 10 && i < st.length; i++) {
			StackTraceElement stElement = st[i];
			sts.append(stElement.getClassName()).append(":")
					.append(stElement.getMethodName())
					.append(stElement.getLineNumber()).append("\n");
		}
		return sts.toString();
	}

	/**
	 * Test a category that does not work properly with clang compiler (Android, Mac)
	 * @param category category name
	 */
	protected abstract void testCatNoClang(String category);

	protected void testCatNoArm(String category) {
		if (!"aarch64".equals(System.getProperty("os.arch"))) {
			testCat(category);
		} else {
			testcases.remove(category);
			assumeFalse(true); // mark test as skipped
		}
	}

	protected void testCatNoWindows(String category) {
		String os = System.getProperty("os.version");;
		if (os != null && StringUtil.toLowerCaseUS(os).startsWith("windows")) {
			testCat(category);
		} else {
			testcases.remove(category);
			assumeFalse(true); // mark test as skipped
		}
	}

	@Test
	public void testIntegralNoArm() {
		testCatNoArm("IntegralNoArm");
	}

	@Test
	public void testComplexRootSquareRoots() {
		testCat("ComplexRootSquareRoots");
	}

	@Test
	public void testAssume() {
		testCat("Assume.2");
	}

	@Test
	public void testText() {
		testCat("Text.1");
	}

	/**
	 * IsDefined()
	 */
	@Test
	public void testDefined() {
		testCat("Defined.1");
	}

	@Test
	public void testIntegral1() {
		testCat("Integral.1");
	}

	@Test
	public void testIntegral2() {
		testCat("Integral.2");
	}

	@Test
	public void testIntegralFinite3() {
		testCat("Integral.3");
	}

	@Test
	public void testIntegralFinite4() {
		testCat("Integral.4");
	}

	@Test
	public void testScientificText1() {
		testCat("ScientificText.1");
	}

	@Test
	public void testScientificText2() {
		testCat("ScientificText.2");
	}

	@Test
	public void testFactor1() {
		testCat("Factor.1");
	}

	@Test
	public void testFactor2() {
		testCat("Factor.2");
	}

	@Test
	public void testCoefficients() {
		testCat("Coefficients");
	}

	@Test
	public void testAppend() {
		testCat("Append.2");
	}

	@Test
	public void testBinomialCoefficient() {
		testCat("nCr.2");
		testCat("BinomialCoefficient.2");
	}

	@Test
	public void testBinomialDist() {
		testCat("BinomialDist");
	}

	@Test
	public void testInverseBinomial() {
		testCat("InverseBinomial.3");
	}

	@Test
	public void testCauchy() {
		testCat("Cauchy.3");
	}

	@Test
	public void testCFactor1() {
		testCat("CFactor.1");
	}

	@Test
	public void testCFactor2() {
		testCat("CFactor.2");
	}

	@Test
	public void testNumeric1() {
		testCat("Numeric.1");
	}

	@Test
	public void testNumeric2() {
		testCat("Numeric.2");
	}

	@Test
	public void testCompleteSquare() {
		testCat("CompleteSquare.1");
	}

	@Test
	public void testCommonDenominator() {
		testCat("CommonDenominator.2");
	}

	@Test
	public void testCovariance() {
		testCat("Covariance");
	}

	@Test
	public void testCross() {
		testCat("Cross.2");
	}

	@Test
	public void testComplexRoot() {
		testCat("ComplexRoot.1");
	}

	@Test
	public void testCSolutions() {
		testCat("CSolutions");
	}

	@Test
	public void testCSolve() {
		testCat("CSolve");
	}

	@Test
	public void testDegree() {
		testCat("Degree");
	}

	@Test
	public void testDenominator() {
		testCat("Denominator.1");
	}

	@Test
	public void testDerivative() {
		testCat("Derivative");
	}

	@Test
	public void testDeterminant() {
		testCat("Determinant.1");
	}

	@Test
	public void testDimension() {
		testCat("Dimension.1");
	}

	@Test
	public void testDiv() {
		testCat("Div.2");
	}

	@Test
	public void testDivision() {
		testCat("Division.2");
	}

	@Test
	public void testDivisors() {
		testCat("Divisors.1");
	}

	@Test
	public void testDivisorsList() {
		testCat("DivisorsList.1");
	}

	@Test
	public void testDivisorsSum() {
		testCat("DivisorsSum.1");
	}

	@Test
	public void testDot() {
		testCat("Dot.2");
	}

	@Test
	public void testElement() {
		testCat("Element");
	}

	@Test
	public void testExpand() {
		testCat("Expand.1");
	}

	@Test
	public void testExponential() {
		testCat("Exponential.2");
	}

	@Test
	public void testFactors() {
		testCat("Factors.1");
	}

	@Test
	public void testFDistribution() {
		testCat("FDistribution.3");
	}

	@Test
	public void testFlatten() {
		testCat("Flatten.1");
	}

	@Test
	public void testFirst() {
		testCat("First");
	}

	@Test
	public void testFitExp() {
		testCat("FitExp.1");
	}

	@Test
	public void testFitLog() {
		testCat("FitLog.1");
	}

	@Test
	public void testFitPoly() {
		testCat("FitPoly.2");
	}

	@Test
	public void testFitPow() {
		testCat("FitPow.1");
	}

	@Test
	public void testGamma() {
		testCat("Gamma.3");
	}

	@Test
	public void testGCD() {
		testCat("GCD");
	}

	@Test
	public void testHyperGeometric() {
		testCat("HyperGeometric.5");
	}

	@Test
	public void testIdentity() {
		testCat("Identity.1");
	}

	@Test
	public void testIf() {
		testCat("If.2");
		testCat("If.3");
	}

	@Test
	public void testImplicitDerivative() {
		testCat("ImplicitDerivative");
	}

	@Test
	public void testIntegralBetween() {
		testCat("IntegralBetween");
	}

	@Test
	public void testIntersect() {
		testCat("Intersect.2");
	}

	@Test
	public void testIteration() {
		testCat("Iteration.3");
	}

	@Test
	public void testIterationList() {
		testCat("IterationList.3");
	}

	@Test
	public void testPointList() {
		testCat("PointList.1");
	}

	@Test
	public void testRootList() {
		testCat("RootList.1");
	}

	@Test
	public void testInvert() {
		testCat("Invert.1");
	}

	@Test
	public void testInflectionPoint() {
		testCat("TurningPoint.1");
	}

	@Test
	public void testIsPrime() {
		testCat("IsPrime.1");
	}

	@Test
	public void testJoin() {
		testCat("Join.N");
	}

	@Test
	public void testLine() {
		testCat("Line.2");
		testCat("OrthogonalLine.2");
	}

	@Test
	public void testLast() {
		testCat("Last");
	}

	@Test
	public void testLCM() {
		testCat("LCM");
	}

	@Test
	public void testLeftSide() {
		testCat("LeftSide");
	}

	@Test
	public void testLength() {
		testCat("Length");
	}

	@Test
	public void testLimit() {
		testCat("Limit");
	}

	@Test
	public void testLimitBelow() {
		testCat("LimitBelow");
	}

	@Test
	public void testLimitAbove() {
		testCat("LimitAbove");
	}

	@Test
	public void testMax() {
		testCat("Max");
	}

	@Test
	public void testMatrixRank() {
		testCat("MatrixRank.1");
	}

	@Test
	public void testMean() {
		testCat("mean.1");
		testCat("Mean.1");
	}

	@Test
	public void testMedian() {
		testCat("Median.1");
		testCat("Median.2");
	}

	@Test
	public void testMin() {
		testCat("Min");
	}

	@Test
	public void testMidpoint() {
		testCat("Midpoint");
	}

	@Test
	public void testMod() {
		testCat("Mod.2");
	}

	@Test
	public void testNextPrime() {
		testCat("NextPrime.1");
	}

	@Test
	public void testNIntegral() {
		testCat("NIntegral");
	}

	@Test
	public void testNormal3() {
		testCat("Normal.3");
	}

	@Test
	public void testNormal4() {
		testCat("Normal.4");
	}

	@Test
	public void testInverseNormal() {
		testCat("InverseNormal.3");
	}

	@Test
	public void testnPr() {
		testCat("nPr");
	}

	@Test
	public void testNSolutions() {
		testCat("NSolutions");
	}

	@Test
	public void testNSolve() {
		testCat("NSolve");
	}

	@Test
	public void testNSolveFlaky() {
		// 50 is a tradeoff between speed and probability of spotting the bug
		// increase to 5000 for deterministic but slow test
		for (int i = 0; i < 50; i++) {
			runCases(testcases.get("NSolveFlaky"));
			app.getKernel().getGeoGebraCAS().getCurrentCAS().clearCache();
		}
		testcases.remove("NSolveFlaky");
	}

	@Test
	public void testNSolve1310() {
		testCat("NSolve1310");
	}

	@Test
	public void testNumerator() {
		testCat("Numerator.1");
	}

	@Test
	public void testPartialFractions() {
		testCat("PartialFractions");
	}

	@Test
	public void testPerpendicularVector() {
		testCat("PerpendicularVector");
	}

	@Test
	public void testPoint() {
		testCat("Point");
	}

	@Test
	public void testFunction() {
		testCat("Function.3");
	}

	@Test
	public void testSVD() {
		testCat("SVD.1");
	}

	@Test
	public void testOrthogonalVector() {
		testCat("OrthogonalVector.1");
	}

	@Test
	public void testPascal() {
		testCat("Pascal.4");
	}

	@Test
	public void testPoisson() {
		testCat("Poisson.3");
	}

	@Test
	public void testPreviousPrime() {
		testCat("PreviousPrime.1");
	}

	@Test
	public void testPrimeFactors() {
		testCat("PrimeFactors.1");
	}

	@Test
	public void testProduct() {
		testCat("Product");
	}

	@Test
	public void testMixedNumber() {
		testCat("MixedNumber.1");
	}

	@Test
	public void testRandomBetween() {
		testCat("Random.2");
	}

	@Test
	public void testRandomBinomial() {
		testCat("RandomBinomial.2");
	}

	@Test
	public void testRandomElement() {
		testCat("RandomElement.1");
	}

	@Test
	public void testRandomPoisson() {
		testCat("RandomPoisson.1");
	}

	@Test
	public void testRandomNormal() {
		testCat("RandomNormal.2");
	}

	@Test
	public void testRandomPolynomial() {
		testCat("RandomPolynomial");
	}

	@Test
	public void testRationalize() {
		testCat("Rationalize.1");
	}

	@Test
	public void testReverse() {
		testCat("Reverse.1");
	}

	@Test
	public void testRightSide() {
		testCat("RightSide");
	}

	@Test
	public void testRoot() {
		testCat("Root.1");
	}

	@Test
	public void testReducedRowEchelonForm() {
		testCat("ReducedRowEchelonForm.1");
	}

	@Test
	public void testSample() {
		testCat("Sample");
	}

	@Test
	public void testSort() {
		testCat("Sort.1");
	}

	@Test
	public void testSampleVariance() {
		testCat("SampleVariance.1");
	}

	@Test
	public void testSampleSD() {
		testCat("stdevp.1");
		testCat("stdevp.2");
		testCat("SampleSD.1");
	}

	@Test
	public void testSequence() {
		testCat("Sequence");
	}

	@Test
	public void testSD() {
		testCat("stdev.1");
		testCat("stdev.2");
		testCat("SD.1");
	}

	@Test
	public void testShuffle() {
		testCat("Shuffle.1"); // TODO
	}

	@Test
	public void testSimplify() {
		testCat("Simplify.1");
	}

	@Test
	public void testExtremum1() {
		testCat("Extremum.1");
	}

	@Test
	public void testExtremum3() {
		testCat("Extremum.3");
	}

	@Test
	public void testDegreeConst() {
		testCat("DegreeConst");
	}

	@Test
	public void testEvaluate2() {
		testCat("Evaluate2");
	}

	@Test
	public void testSolveLogic() {
		testCat("SolveLogic");
	}

	@Test
	public void testFactorExponential() {
		testCat("FactorExponential");
	}

	@Test
	public void testVector() {
		testCat("Vector");
	}

	@Test
	public void testTrigCombine() {
		testCat("TrigCombine.1");
		testCat("TrigCombine.2");
	}

	@Test
	public void testSolutions() {
		testCat("Solutions");
	}

	@Test
	public void testSolve() {
		testCat("Solve");
	}

	@Test
	public void testSolveCubic() {
		testCat("SolveCubic.1");
	}

	@Test
	public void testSolveODE3() {
		testCat("SolveODE.3");
	}

	@Test
	public void testSolveODE1() {
		testCat("SolveODE.1");
	}

	@Test
	public void testSolveODE4() {
		testCat("SolveODE.4");
	}

	@Test
	public void testSolveODE5() {
		testCat("SolveODE.5");
	}

	@Test
	public void testSolveODE2() {
		testCat("SolveODE.2");
	}

	@Test
	public void testSubstitute2() {
		testCat("Substitute.2");
	}

	@Test
	public void testSubstitute3() {
		testCat("Substitute.3");
	}

	@Test
	public void testSubstituteN() {
		testCat("Substitute.N");
	}

	@Test
	public void testSum() {
		testCat("Sum");
	}

	@Test
	public void testTangent() {
		testCat("Tangent.2");
	}

	@Test
	public void testTake() {
		testCat("Take");
	}

	@Test
	public void testTaylorPolynomial() {
		testCat("TaylorSeries.3");
		testCat("TaylorSeries.4");
	}

	@Test
	public void testTDistribution() {
		testCat("TDistribution.2");
	}

	@Test
	public void testToComplex() {
		testCat("ToComplex.1");
	}

	@Test
	public void testToExponential() {
		testCat("ToExponential.1");
	}

	@Test
	public void testToPolar() {
		testCat("ToPolar.1");
	}

	@Test
	public void testToPoint() {
		testCat("ToPoint.1");
	}

	@Test
	public void testTranspose() {
		testCat("Transpose.1");
	}

	@Test
	public void testTrigExpand() {
		testCat("TrigExpand.1");
		testCat("TrigExpand.2");
		testCat("TrigExpand.3");
		testCat("TrigExpand.4");
	}

	@Test
	public void testTrigSimplify() {
		testCat("TrigSimplify.1");
	}

	@Test
	public void testUnique() {
		testCat("Unique.1");
	}

	@Test
	public void testUnitPerpendicularVector() {
		testCat("UnitOrthogonalVector.1");
	}

	@Test
	public void testUnitVector() {
		testCat("UnitVector.1");
	}

	@Test
	public void testVariance() {
		testCat("Variance.1");
	}

	@Test
	public void testWeibull() {
		testCat("Weibull.3");
	}

	@Test
	public void testZipf() {
		testCat("Zipf.4");
	}

	@Test
	public void testsin() {
		testCat("sin");
	}

	@Test
	public void testassignment() {
		testCat("assignment");
	}

	@Test
	public void testEvaluate() {
		testCat("Evaluate");
	}

	@Test
	public void testEvaluateLogic() {
		testCat("EvaluateLogic");
	}

	@Test
	public void testAbs() {
		testCat("Abs");
	}

	@Test
	public void testvecExpr() {
		testCat("vec expr");
	}

	@Test
	public void testChiSquared() {
		testCat("ChiSquared.2");
	}

	@Test
	public void testFractionalPart() {
		testCat("FractionalPart");
	}

	@Test
	public void testDelete() {
		testCat("Delete");
	}

	@Test
	public void testImaginary() {
		testCat("Imaginary");
	}

	@Test
	public void testNRoot() {
		testCat("NRoot");
	}

	@Test
	public void testReal() {
		testCat("Real");
	}

	@Test
	public void testRound() {
		testCat("Round.1");
		testCat("Round.2");
	}

	@Test
	public void testFloor() {
		testCat("Floor");
	}

	@Test
	public void testCeil() {
		testCat("Ceil");
	}

	@Test
	public void testlistExpr() {
		testCat("list expr");
	}

	@Test
	public void testexpr() {
		testCat("expr");
	}

	@Test
	public void testtan() {
		testCat("tan");
	}

	@Test
	public void testcot() {
		testCat("cot");
	}

	@Test
	public void testAsymptote() {
		testCat("Asymptote");
	}

	@Test
	public void testconjugate() {
		testCat("conjugate");
	}

	@Test
	public void testarg() {
		testCat("arg");
	}

	@Test
	public void testln() {
		testCat("ln");
	}

	@Test
	public void testCIFactor1() {
		testCat("CIFactor.1");
	}

	@Test
	public void testCIFactor2() {
		testCat("CIFactor.2");
	}

	@Test
	public void testIFactor1() {
		testCat("IFactor.1");
	}

	@Test
	public void testIFactor2() {
		testCat("IFactor.2");
	}

	@Test
	public void testCurve() {
		testCat("Curve");
	}

	@Test
	public void testRadius() {
		testCat("Radius.1");
	}

	@Test
	public void testCenter() {
		testCat("Center.1");
		testCat("Center.2");
	}

	@Test
	public void testCircumference() {
		testCat("Circumference.1");
	}

	@Test
	public void testDistance() {
		testCat("Distance.2");
	}

	@Test
	public void testAngle() {
		testCat("Angle");
	}

	@Test
	public void testCircle() {
		testCat("Circle.2");
	}

	@Test
	public void testAngularBisector() {
		testCat("AngularBisector");
	}

	@Test
	public void testArea() {
		testCat("Area.1");
	}

	@Test
	public void testLineBisector() {
		testCat("LineBisector.2");
	}

	@Test
	public void testEllipse() {
		testCat("Ellipse.3");
	}

	@Test
	public void testConic() {
		testCat("Conic.5");
	}

	@Test
	public void testHyperbola() {
		testCat("Hyperbola.3");
	}

	@Test
	public void testIntersection() {
		testCat("Intersection.2");
	}

	@Test
	public void testUnion() {
		testCat("Union.2");
	}

	@Test
	public void testexp() {
		testCat("exp");
	}

	@Test
	public void testPolynomial() {
		testCat("Polynomial.N");
	}

	@Test
	public void testabs() {
		testCat("abs");
	}

	@Test
	public void testlength() {
		testCat("length");
	}

	@Test
	public void testSolveAssume() {
		testCat("SolveAssume");
	}

	@Test
	public void testSolveTrig() {
		testCat("SolveTrig");
	}

	@Test
	public void testSolveUnderdetermined() {
		testCat("SolveUnderdetermined");
	}

	@Test
	public void testSolveIneq() {
		testCat("SolveIneq");
	}

	@Test
	public void testSolveLambertIneq() {
		testCat("SolveLambertIneq");
	}

	@Test
	public void testEvaluateOrdering() {
		testCatNoClang("EvaluateOrdering");
	}

	@Test
	public void testZip() {
		testCat("Zip.N");
	}

	@Test
	public void testTranslate() {
		testCat("Translate.2");
	}

	@Test
	public void testFromBase() {
		testCat("FromBase.2");
	}

	@Test
	public void testToBase() {
		testCat("ToBase.2");
	}

	@Test
	public void testIndexOf() {
		testCat("IndexOf.2");
	}

	@Test
	public void testPlane() {
		testCat("Plane.1");
		testCat("Plane.3");
	}

	@Test
	public void testCountIf() {
		testCat("CountIf.2");
		testCat("CountIf.3");
	}

	@Test
	public void testKeepIf() {
		testCat("KeepIf.2");
		testCat("KeepIf.3");
	}

	@Test
	public void testLaplace1() {
		testCat("Laplace.1");
	}

	@Test
	public void testLaplace2() {
		testCat("Laplace.2");
	}

	@Test
	public void testLaplace3() {
		testCat("Laplace.3");
	}

	@Test
	public void testInverseLaplace() {
		testCat("InverseLaplace.1");
		testCat("InverseLaplace.2");
		testCat("InverseLaplace.3");
	}

	@Test
	public void testEigenvalues() {
		testCat("Eigenvalues.1");
		testCat("Eigenvectors.1");
	}

	@Test
	public void testJordan() {
		testCat("JordanDiagonalization.1");
	}

	@Test
	public void testRandomUniform() {
		testCat("RandomUniform.2");
	}

	@Test
	public void testIntegralSymbolic() {
		testCat("IntegralSymbolic.1");
		testCat("IntegralSymbolic.2");
	}

	@Test
	public void testInequalityOperation() {
		testCat("InequalityOperation");
	}

	@Test
	public void testRemoveUndefined() {
		testCat("RemoveUndefined.1");
	}

	@Test
	public void testIsInteger() {
		testCat("IsInteger.1");
	}

	@Test
	public void testPlotSolve() {
		testCat("PlotSolve.1");
	}

	@Test
	public void testQuartile1() {
		testCat("Q1.1");
	}

	@Test
	public void testQuartile3() {
		testCat("Q3.1");
	}

	@Test
	public void testExtendedGCD2() {
		testCat("ExtendedGCD.2");
	}

	@Test
	public void testModularExponent3() {
		testCat("ModularExponent.3");
	}

	@Test
	public void testCharacteristicPolynomial() {
		testCat("CharacteristicPolynomial.1");
	}

	@Test
	public void testMinimalPolynomial() {
		testCat("MinimalPolynomial.1");
	}

	@Test
	public void testLUDecomposition() {
		testCat("LUDecomposition.1");
	}

	@Test
	public void testQRDecomposition() {
		testCat("QRDecomposition.1");
	}

	@Test
	public void testDirac() {
		testCat("Dirac.1");
	}

	@Test
	public void testHeaviside() {
		testCat("Heaviside.1");
	}

	@Test
	public void testGroebnerLex() {
		testCat("GroebnerLex.1");
		testCat("GroebnerLex.2");
	}

	@Test
	public void testGroebnerLexDeg() {
		testCat("GroebnerLexDeg.1");
		testCat("GroebnerLexDeg.2");
	}

	@Test
	public void testGroebnerDegRevLex() {
		testCat("GroebnerDegRevLex.1");
		testCat("GroebnerDegRevLex.2");
	}

	@Test
	public void testEliminate() {
		testCat("Eliminate.2");
	}
}
