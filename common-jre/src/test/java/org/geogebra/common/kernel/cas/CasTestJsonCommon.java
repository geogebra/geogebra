package org.geogebra.common.kernel.cas;

import static org.geogebra.test.matcher.IsEqualStringIgnoreWhitespaces.equalToIgnoreWhitespaces;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.geogebra.common.cas.giac.Ggb2giac;
import org.geogebra.common.kernel.GeoGebraCasInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.test.CASTestLogger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class CasTestJsonCommon {

	protected static HashMap<String, ArrayList<CasTest>> testcases = new HashMap<>();
	protected static GeoGebraCasInterface cas;
	protected static Kernel kernel;
	protected static App app;
	protected static CASTestLogger logger = new CASTestLogger();
	protected static String missing = null;

	static class CasTest {
		public CasTest(String input, String output, String rounding) {
			this.input = input;
			this.output = output;
			this.rounding = rounding;
		}

		protected String input;
		protected String output;
		protected String rounding;
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
		Assert.assertNotSame(0, testsJSON.length());
		int i = 1;

		while (i < testsJSON.length()) {
			Object testVal = testsJSON.opt(i);

			i++;
			if (!(testVal instanceof JSONObject)) {
				System.err.println("Invalid JSON:" + testVal);
				continue;
			}
			JSONObject test = (JSONObject) testVal;
			String cat = "general";
			if (test.has("cat")) {
				cat = test.getString("cat");
			} else {
				Assert.fail("Missing category:" + testVal.toString());
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

	protected static void handleParsingError(Throwable e, String json) {
		String msg = e.getMessage();
		String marker = "at character ";
		int pos = msg.indexOf(marker);
		if (pos > 0) {
			String characterNo = msg.substring(pos + marker.length(),
					msg.indexOf(" ", pos + marker.length()));
			int err = Integer.parseInt(characterNo);
			int sampleLength = 50;
			Assert.fail("JSON parsing error at '" + json.substring(err - sampleLength / 2,
					err + sampleLength / 2) + "'");
		} else {
			Assert.fail(msg);
		}
	}

	protected static void checkMissingCategories() {
		for (String key : Ggb2giac.getMap(app).keySet()) {
			if (testcases.get(key) == null
					&& testcases.get(key.substring(0, key.indexOf("."))) == null
					&& forCAS(key) && !"ApproximateSolution.3".equals(key)
					&& !"AssumeInteger.2".equals(key)
					&& !"Binomial.2".equals(key)
					&& !"CorrectSolution.3".equals(key)
					&& !"Eliminate.2".equals(key) && !"ExpandOnly.1".equals(key)
					&& !"GroebnerDegRevLex.1".equals(key)
					&& !"GroebnerDegRevLex.2".equals(key)
					&& !"GroebnerLex.1".equals(key)
					&& !"GroebnerLex.2".equals(key)
					&& !"GroebnerLexDeg.1".equals(key)
					&& !"GroebnerLexDeg.2".equals(key)
					&& !"InverseBinomial.3".equals(key)
					&& !"Radius.1".equals(key) && !"Random.2".equals(key)
					&& !"Regroup.1".equals(key)
					&& !"SolveODEPoint.2".equals(key)
					&& !"SolveQuartic.1".equals(key)
					&& !"TurningPoint.1".equals(key)
					&& !"UnitOrthogonalVector.1".equals(key)) {
				missing = key;
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
			Assert.fail("No testcase for " + name);
		}
		ArrayList<CasTest> cases = testcases.get(name);
		Assert.assertNotEquals(0, cases.size());
		testcases.remove(name);
		StringBuilder[] failures = new StringBuilder[] { new StringBuilder(),
				new StringBuilder() };
		for (CasTest cmd : cases) {
			if (!StringUtil.empty(cmd.rounding)) {
				app.setRounding(cmd.rounding);
			} else {
				app.setRounding("2");
			}
			t(failures, cmd.input, cmd.output);
		}
		Assert.assertEquals(failures[0].toString(), failures[1].toString());
	}

	private static void t(StringBuilder[] failures, String input,
			String expectedResult) {
		String[] validResults = expectedResult.split("\\|OR\\|");
		ta(failures, input, validResults, validResults);
	}

	private static void ta(StringBuilder[] failures,
			String input, String[] expectedResult, String... validResults) {
		String result;

		try {
			GeoCasCell f = new GeoCasCell(kernel.getConstruction());
			kernel.getConstruction().addToConstructionList(f, false);

			f.setInput(input);

			f.computeOutput();

			boolean includesNumericCommand = false;
			HashSet<Command> commands = new HashSet<>();
			if (f.getInputVE() == null) {
				Assert.assertEquals("Input should be parsed", "GEOGEBRAERROR",
						expectedResult[0]);
				return;
			}
			f.getInputVE().traverse(Traversing.CommandCollector.getCollector(commands));

			if (!commands.isEmpty()) {
				for (Command cmd : commands) {
					String cmdName = cmd.getName();
					// Numeric used
					includesNumericCommand = includesNumericCommand
							|| ("Numeric".equals(cmdName)
							&& cmd.getArgumentNumber() > 1);
				}
			}
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
		} catch (Throwable t) {
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
				// if (!(t instanceof AssertionError)) {
				t.printStackTrace();
				// }
				if (i == expectedResult.length - 1) {
					failures[0].append(expectedResult[0] == null ? "null"
							: normalizeExpected(expectedResult[0]));
					failures[0].append(" input: ").append(input).append('\n');
					failures[1].append(result).append('\n');
				}
			}
		}
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
		return new Traversing() {
			@Override
			public ExpressionValue process(ExpressionValue ev) {
				if (ev.unwrap() instanceof MyVecNDNode
						&& ((MyVecNDNode) ev.unwrap()).isCASVector()) {
					return new Variable(kernel, "ggbvect").wrap()
							.apply(Operation.FUNCTION, ev);

				}
				return ev;
			}
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
	 * Kill all CAS cells.
	 */
	@Before
	public void clearConstruction() {
		app.getKernel().clearConstruction(true);
	}

	/**
	 * Test a category that does not work properly with clang compiler (Android, Mac)
	 * @param category category name
	 */
	protected abstract void testCatNoClang(String category);

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
	public void testScientificText() {
		testCat("ScientificText.1");
		testCat("ScientificText.2");
	}

	@Test
	public void testFactor() {
		testCat("Factor");
	}

	@Test
	public void testCoefficients() {
		testCat("Coefficients");
	}

	@Test
	public void testAppend() {
		testCat("Append");
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
	public void testCauchy() {
		testCat("Cauchy");
	}

	@Test
	public void testCFactor() {
		testCat("CFactor");
	}

	@Test
	public void testNumeric() {
		testCat("Numeric");
	}

	@Test
	public void testCompleteSquare() {
		testCat("CompleteSquare");
	}

	@Test
	public void testCommonDenominator() {
		testCat("CommonDenominator");
	}

	@Test
	public void testCovariance() {
		testCat("Covariance");
	}

	@Test
	public void testCross() {
		testCat("Cross");
	}

	@Test
	public void testComplexRoot() {
		testCat("ComplexRoot");
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
		testCat("Denominator");
	}

	@Test
	public void testDerivative() {
		testCat("Derivative");
	}

	@Test
	public void testDeterminant() {
		testCat("Determinant");
	}

	@Test
	public void testDimension() {
		testCat("Dimension");
	}

	@Test
	public void testDiv() {
		testCat("Div");
	}

	@Test
	public void testDivision() {
		testCat("Division");
	}

	@Test
	public void testDivisors() {
		testCat("Divisors");
	}

	@Test
	public void testDivisorsList() {
		testCat("DivisorsList");
	}

	@Test
	public void testDivisorsSum() {
		testCat("DivisorsSum");
	}

	@Test
	public void testDot() {
		testCat("Dot");
	}

	@Test
	public void testElement() {
		testCat("Element");
	}

	@Test
	public void testExpand() {
		testCat("Expand");
	}

	@Test
	public void testExponential() {
		testCat("Exponential");
	}

	@Test
	public void testFactors() {
		testCat("Factors");
	}

	@Test
	public void testFDistribution() {
		testCat("FDistribution");
	}

	@Test
	public void testFlatten() {
		testCat("Flatten");
	}

	@Test
	public void testFirst() {
		testCat("First");
	}

	@Test
	public void testFitExp() {
		testCat("FitExp");
	}

	@Test
	public void testFitLog() {
		testCat("FitLog");
	}

	@Test
	public void testFitPoly() {
		testCat("FitPoly");
	}

	@Test
	public void testFitPow() {
		testCat("FitPow");
	}

	@Test
	public void testGamma() {
		testCat("Gamma");
	}

	@Test
	public void testGCD() {
		testCat("GCD");
	}

	@Test
	public void testHyperGeometric() {
		testCat("HyperGeometric");
	}

	@Test
	public void testIdentity() {
		testCat("Identity");
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
		testCat("Intersect");
	}

	@Test
	public void testIteration() {
		testCat("Iteration");
	}

	@Test
	public void testIterationList() {
		testCat("IterationList");
	}

	@Test
	public void testPointList() {
		testCat("PointList");
	}

	@Test
	public void testRootList() {
		testCat("RootList");
	}

	@Test
	public void testInvert() {
		testCat("Invert");
	}

	@Test
	public void testInflectionPoint() {
		testCat("InflectionPoint");
	}

	@Test
	public void testIsPrime() {
		testCat("IsPrime");
	}

	@Test
	public void testJoin() {
		testCat("Join");
	}

	@Test
	public void testLine() {
		testCat("Line");
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
		testCat("MatrixRank");
	}

	@Test
	public void testMean() {
		testCat("mean.1");
		testCat("Mean.1");
	}

	@Test
	public void testMedian() {
		testCat("Median");
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
		testCat("Mod");
	}

	@Test
	public void testNextPrime() {
		testCat("NextPrime");
	}

	@Test
	public void testNIntegral() {
		testCat("NIntegral");
	}

	@Test
	public void testNormal() {
		testCat("Normal");
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
	public void testNSolve1310() {
		testCat("NSolve1310");
	}

	@Test
	public void testNumerator() {
		testCat("Numerator");
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
		testCat("Function");
	}

	@Test
	public void testSVD() {
		testCat("SVD");
	}

	@Test
	public void testSolveODE2() {
		testCat("SolveODE2");
	}

	@Test
	public void testOrthogonalVector() {
		testCat("OrthogonalVector");
	}

	@Test
	public void testPascal() {
		testCat("Pascal");
	}

	@Test
	public void testPoisson() {
		testCat("Poisson");
	}

	@Test
	public void testPreviousPrime() {
		testCat("PreviousPrime");
	}

	@Test
	public void testPrimeFactors() {
		testCat("PrimeFactors");
	}

	@Test
	public void testProduct() {
		testCat("Product");
	}

	@Test
	public void testMixedNumber() {
		testCat("MixedNumber");
	}

	@Test
	public void testRandomBetween() {
		testCat("RandomBetween");
	}

	@Test
	public void testRandomBinomial() {
		testCat("RandomBinomial");
	}

	@Test
	public void testRandomElement() {
		testCat("RandomElement");
	}

	@Test
	public void testRandomPoisson() {
		testCat("RandomPoisson");
	}

	@Test
	public void testRandomNormal() {
		testCat("RandomNormal");
	}

	@Test
	public void testRandomPolynomial() {
		testCat("RandomPolynomial");
	}

	@Test
	public void testRationalize() {
		testCat("Rationalize");
	}

	@Test
	public void testReverse() {
		testCat("Reverse");
	}

	@Test
	public void testRightSide() {
		testCat("RightSide");
	}

	@Test
	public void testRoot() {
		testCat("Root");
	}

	@Test
	public void testReducedRowEchelonForm() {
		testCat("ReducedRowEchelonForm");
	}

	@Test
	public void testSample() {
		testCat("Sample");
	}

	@Test
	public void testSort() {
		testCat("Sort");
	}

	@Test
	public void testSampleVariance() {
		testCat("SampleVariance");
	}

	@Test
	public void testSampleSD() {
		testCat("stdevp.1");
		testCat("SampleSD.1");
	}

	@Test
	public void testSequence() {
		testCat("Sequence");
	}

	@Test
	public void testSD() {
		testCat("stdev.1");
		testCat("SD.1");
	}

	@Test
	public void testShuffle() {
		testCat("Shuffle"); // TODO
	}

	@Test
	public void testSimplify() {
		testCat("Simplify");
	}

	@Test
	public void testExtremum() {
		testCat("Extremum");
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
		testCat("SolveCubic");
	}

	@Test
	public void testSolveODE() {
		testCat("SolveODE");
	}

	@Test
	public void testSubstitute() {
		testCat("Substitute");
	}

	@Test
	public void testSum() {
		testCat("Sum");
	}

	@Test
	public void testTangent() {
		testCat("Tangent");
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
		testCat("TDistribution");
	}

	@Test
	public void testToComplex() {
		testCat("ToComplex");
	}

	@Test
	public void testToExponential() {
		testCat("ToExponential");
	}

	@Test
	public void testToPolar() {
		testCat("ToPolar");
	}

	@Test
	public void testToPoint() {
		testCat("ToPoint");
	}

	@Test
	public void testTranspose() {
		testCat("Transpose");
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
		testCat("Unique");
	}

	@Test
	public void testUnitPerpendicularVector() {
		testCat("UnitPerpendicularVector");
	}

	@Test
	public void testUnitVector() {
		testCat("UnitVector");
	}

	@Test
	public void testVariance() {
		testCat("Variance");
	}

	@Test
	public void testWeibull() {
		testCat("Weibull");
	}

	@Test
	public void testZipf() {
		testCat("Zipf");
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
		testCat("ChiSquared");
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
		testCat("Round");
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
	public void testCIFactor() {
		testCat("CIFactor");
	}

	@Test
	public void testIFactor() {
		testCat("IFactor");
	}

	@Test
	public void testCurve() {
		testCat("Curve");
	}

	@Test
	public void testRadius() {
		testCat("Radius");
	}

	@Test
	public void testCenter() {
		testCat("Center.1");
		testCat("Center.2");
	}

	@Test
	public void testCircumference() {
		testCat("Circumference");
	}

	@Test
	public void testDistance() {
		testCat("Distance");
	}

	@Test
	public void testAngle() {
		testCat("Angle");
	}

	@Test
	public void testCircle() {
		testCat("Circle");
	}

	@Test
	public void testAngularBisector() {
		testCat("AngularBisector");
	}

	@Test
	public void testArea() {
		testCat("Area");
	}

	@Test
	public void testLineBisector() {
		testCat("LineBisector");
	}

	@Test
	public void testEllipse() {
		testCat("Ellipse");
	}

	@Test
	public void testConic() {
		testCat("Conic");
	}

	@Test
	public void testHyperbola() {
		testCat("Hyperbola");
	}

	@Test
	public void testIntersection() {
		testCat("Intersection");
	}

	@Test
	public void testUnion() {
		testCat("Union");
	}

	@Test
	public void testexp() {
		testCat("exp");
	}

	@Test
	public void testPolynomial() {
		testCat("Polynomial");
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
		testCatNoClang("SolveLambertIneq");
	}

	@Test
	public void testEvaluateOrdering() {
		testCatNoClang("EvaluateOrdering");
	}

	@Test
	public void testZip() {
		testCat("Zip");
	}

	@Test
	public void testTranslate() {
		testCat("Translate");
	}

	@Test
	public void testFromBase() {
		testCat("FromBase");
	}

	@Test
	public void testToBase() {
		testCat("ToBase");
	}

	@Test
	public void testIndexOf() {
		testCat("IndexOf");
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
}
