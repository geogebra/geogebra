package geogebra.cas;

import static geogebra.test.util.IsEqualPolynomialEquation.equalToPolynomialEquation;
import static geogebra.test.util.IsEqualStringIgnoreWhitespaces.equalToIgnoreWhitespaces;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import geogebra.CommandLineArguments;
import geogebra.cas.logging.CASTestLogger;
import geogebra.common.cas.CASparser;
import geogebra.common.cas.mpreduce.CASmpreduce;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.arithmetic.Traversing.CommandCollector;
import geogebra.common.kernel.GeoGebraCasInterface;
import geogebra.common.main.App;
import geogebra.main.AppD;

import java.util.HashSet;
import java.util.Locale;

import javax.swing.JFrame;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Throwables;

public class GeoGebraCasIntegrationTest {

	static GeoGebraCasInterface cas;
	static Kernel kernel;
	/**
	 * Logs all tests which don't give the expected but a valid result.
	 */
	static CASTestLogger logger;
	public static boolean silent = false;
	@BeforeClass
	public static void setupCas() {
		AppD app = new AppD(new CommandLineArguments(
				silent?new String[]{"--silent"}:new String[0]), new JFrame(), false);		
		if(silent)
			App.logger = null;
		app.setLanguage(Locale.GERMANY);
		//app.fillCasCommandDict();
		// app.getKernel()
		kernel = app.getKernel();
		arbconst =  new MyArbitraryConstant(new GeoCasCell(kernel.getConstruction()));
		cas = kernel.getGeoGebraCAS();
		logger = new CASTestLogger();
	}

	/**
	 * handles the logs about test warnings
	 */
	@AfterClass
	public static void handleLogs() {
		if(!silent)
			logger.handleLogs();
	}
	private static MyArbitraryConstant arbconst;
	/**
	 * Executes the given expression in the CAS.
	 * 
	 * @param input
	 *            The expression to be evaluated, in geogebra's CAS syntax.
	 * @return The string returned by GeogebraCAS.
	 */
	private static String executeInCAS(String input) throws Throwable {
		CASparser parser = (CASparser) cas.getCASparser();
		ValidExpression inputVe = parser
				.parseGeoGebraCASInputAndResolveDummyVars(input);
		String result = cas.evaluateGeoGebraCAS(inputVe, arbconst,StringTemplate.numericDefault);

		if (result == null || result.length() <= 0) {
			return "";
		}

		// Parse input into valid expression.
		ExpressionValue outputVe = parser.parseGeoGebraCASInput(result);

		// Resolve Variable objects in ValidExpression as GeoDummy objects.
		outputVe = parser.resolveVariablesForCAS(outputVe);
		boolean includesNumericCommand = false;
		HashSet<Command> commands = new HashSet<Command>();
		inputVe.traverse(CommandCollector.getCollector(commands));
		if(!commands.isEmpty()){
				for (Command cmd : commands) {
					String cmdName = cmd.getName();
					// Numeric used
					includesNumericCommand = includesNumericCommand
							|| ("Numeric".equals(cmdName) && cmd.getArgumentNumber()>1);
				}
		}
		return outputVe.toString(includesNumericCommand?StringTemplate.testNumeric:StringTemplate.testTemplate);
	}

	private static void t(String input, String expectedResult,
			String... validResults) {
		try {
			GeoCasCell f = new GeoCasCell(kernel.getConstruction());
			kernel.getConstruction().addToConstructionList(f,false);
			f.setInput(input);
			f.computeOutput();
			boolean includesNumericCommand = false;
			HashSet<Command> commands = new HashSet<Command>();
			f.getInputVE().traverse(CommandCollector.getCollector(commands));
			if(!commands.isEmpty()){
					for (Command cmd : commands) {
						String cmdName = cmd.getName();
						// Numeric used
						includesNumericCommand = includesNumericCommand
								|| ("Numeric".equals(cmdName) && cmd.getArgumentNumber()>1);
					}
			}
			String result = f.getOutputValidExpression().toString(includesNumericCommand?StringTemplate.testNumeric:StringTemplate.testTemplate);
			assertThat(
					result,
					equalToIgnoreWhitespaces(logger, input, expectedResult,
							validResults));
		} catch (Throwable t) {
			Throwables.propagate(t);
		}
	}

	/**
	 * For comparing polynomial equations<br/>
	 * Tests if the given input is equal to the expected result ignoring the
	 * ordering of the terms on each side
	 * 
	 * @param input
	 *            The polynomial equation in GeogebraCAS syntax
	 * @param expectedResult
	 *            The regular expression that the output should match
	 */
	@SuppressWarnings("unused")
	private static void pe(String input, String expectedResult) {
		try {
			String result = executeInCAS(input);
			assertThat(result, equalToPolynomialEquation(expectedResult));
		} catch (Throwable t) {
			Throwables.propagate(t);
		}
	}

	/**
	 * Tests if the given input produces a result that matches a given regular
	 * expression.
	 * 
	 * @param input
	 *            The input expression in GeogebraCAS syntax.
	 * @param expectedPattern
	 *            The regular expression that the output should match.
	 * @param readablePattern
	 *            The pattern that is used as "expected" value when the input
	 *            doesn't match the output. If null, expectedPattern will be
	 *            used.
	 */
	private static void r(String input, String expectedPattern,
			String readablePattern) {
		try {
			String result = executeInCAS(input);
			boolean isMatch = result.matches(expectedPattern);
			if (readablePattern != null)
				Assert.assertTrue("\nExpected: " + readablePattern + "\ngot: "
						+ result, isMatch);
			else
				Assert.assertTrue("\nExpected: " + expectedPattern + "\ngot: "
						+ result, isMatch);
		} catch (Throwable t) {
			Throwables.propagate(t);
		}
	}

	/**
	 * Tests that the given input produces a result that matches a given output.
	 * 
	 * The pattern for the output is basically a normal regular expression,
	 * except that many RE special characters will be taken literally, e.g.
	 * braces, parenthesis etc. Additionally, all whitespace will be optional
	 * and character- class boxes (e.g. [a-z]) do not work!
	 * 
	 * @param input
	 *            The input expression in GeogebraCAS syntax.
	 * @param expectedPattern
	 *            The simplified regular expression that the output should
	 *            match.
	 */
	private static void s(String input, String expectedPattern) {
		String originalPattern = expectedPattern;
		String newPattern = expectedPattern;
		newPattern = newPattern.replace("{", "\\{");
		newPattern = newPattern.replace("}", "\\}");
		newPattern = newPattern.replace("(", "\\(");
		newPattern = newPattern.replace(")", "\\)");

		// + and * can have both arithmetic meaning (addition/multiplication),
		// as well as be part of a multiplier in a regex (e.g. in \d+ ).
		// Likewise, brackets [] can be part of a character class box or of a
		// function call in Geogebra. We escape these signs so they'll retain
		// their
		// function call meaning, not their character-class box meaning!
		newPattern = newPattern.replaceAll("([^\\\\].|^.)\\+", "$1\\\\+");
		newPattern = newPattern.replaceAll("([^\\\\].|^.)\\*", "$1\\\\*");
		newPattern = newPattern.replaceAll("([^\\\\].|^.)\\^", "$1\\\\^");
		newPattern = newPattern.replaceAll("INDEX", "\\\\{?\\\\d+\\\\}?");
		newPattern = newPattern.replace(" ", "\\s*"); // make whitespace
														// optional
		App.debug(newPattern);
		r(input, newPattern, originalPattern);
	}

	// Test Section

	/* Assignment */

	/**
	 * Checks if the assignment of variables by using the ':=' operator is
	 * working
	 */
	@Test
	public void Assignment_0() {
		t("a", "a");
		t("a := 1", "1");
		t("a", "1");
		
		// Tidy up
		try {
			t("Delete[a]","true");
		} catch (Throwable t) {
			Throwables.propagate(t);
		}
	}
	


	/* Simplification of Terms */

	/* One variable */

	@Test
	public void SimplificationOfTerms_ConstantsOnly() {
		t("((15 + 9) * 3.5 - 2 * 0.5) / 4", "83 / 4");
	}

	@Test
	public void SimplificationOfTerms_OneVariable() {
		t("x + x", "2 * x");
	}

	/* Two variables */

	@Test
	public void SimplificationOfTerms_TwoVariables() {
		t("6a * 3b - (5a b + b * 2a)", "11 * a * b");
	}

	/* Power of one Variable */

	@Test
	public void SimplificationOfTerms_PowerOfOneVariable() {
		t("x*x", "x^(2)");
	}

	/* Power of two Variables */

	@Test
	public void SimplificationOfTerms_PowerOfTwoVariables_0() {
		t("x^2 y^2 (x y)^2", "x^(4) * y^(4)");
	}

	@Test
	public void SimplificationOfTerms_PowerOfTwoVariables_1() {
		t("((a + b)^2) / c^2", "(a^(2) + 2 * a * b + b^(2)) / c^(2)");
	}

	@Test
	public void SimplificationOfTerms_PowerOfTwoVariables_2() {
		t("((a + b) / (c + d))^2",
				"(a^(2) + 2 * a * b + b^(2)) / (c^(2) + 2 * c * d + d^(2))");
	}

	/* Ordering of Powers */

	@Test
	public void SimplificationOfTerms_OrderingOfPowers() {
		t("(a^2 - 3 b) * (-3 a + 5 b^2)",
				"-3 * a^(3) + 5 * a^(2)* b^(2) + 9 * a * b - 15 * b^(3)");
	}

	/* Polynomial Division */

	@Test
	public void SimplificationOfTerms_PolynomialDivision_0() {
		t("x^2 / x", "x");
	}

	@Test
	public void SimplificationOfTerms_PolynomialDivision_1() {
		t("(7x^3-14 x^2)/(6x-12)", "7 * x^(2) / 6", "(7 * x^(2)) / 6");
	}

	@Test
	public void SimplificationOfTerms_PolynomialDivision_2() {
		t("(x^2 - y^2) / (x - y)", "x + y");
	}

	/* Simplification of Equations */

	/* Constants only */

	@Test
	public void SimplificationOfEquations_ConstantsOnly_0() {
		t("3 + 4 = 9 - 2", "7 = 7");
	}

	@Test
	public void SimplificationOfEquations_ConstantsOnly_1() {
		t("3 + 4 = 10 - 2", "7 = 8");
	}

	@Test
	public void SimplificationOfEquations_ConstantsOnly_2() {
		t("(39 / 9 = 4 + 1 / 3) 3", "13 = 13");
	}

	@Test
	public void SimplificationOfEquations_ConstantsOnly_3() {
		t("(2 + 3 = 4 + 1 / 3) 3 ", "15 = 13");
	}

	/* One Variable */

	@Test
	public void SimplificationOfEquations_OneVariable_0() {
		t("x + x = 3 x - x", "2 * x = 2 * x");
	}

	@Test
	public void SimplificationOfEquations_OneVariable_1() {
		t("x + x = 4 x - x", "2 * x = 3 * x");
	}

	@Test
	public void SimplificationOfEquations_OneVariable_2() {
		t("(2 x + 3 = 4 + 1 / 3) 3", "6 * x + 9 = 13");
	}

	@Test
	public void SimplificationOfEquations_OneVariable_3() {
		t("(3 - 5 / x = 7) x", "3 * x - 5 = 7 * x");
	}

	@Test
	public void SimplificationOfEquations_OneVariable_4() {
		t("(3 - 5 / (x + 1) = 7) (x + 1)", "3 * x - 2 = 7 * x + 7");
	}

	/* Several Variables */

	@Test
	public void SimplificationOfEquations_SeveralVariables_0() {
		StringBuilder expectedResult = new StringBuilder("");
		if (CASmpreduce.getVarOrderingNumber("ggbtmpvara") < CASmpreduce
				.getVarOrderingNumber("ggbtmpvarx")) {
			expectedResult.append("a^(2) - 14 * a * x + 49 * x^(2) = ");
		} else {
			expectedResult.append("49 * x^(2) - 14 * x * a  + a^(2) = ");
		}

		if (CASmpreduce.getVarOrderingNumber("ggbtmpvarb") < CASmpreduce
				.getVarOrderingNumber("ggbtmpvarc")
				&& CASmpreduce.getVarOrderingNumber("ggbtmpvarc") < CASmpreduce
						.getVarOrderingNumber("ggbtmpvarx")
				&& CASmpreduce.getVarOrderingNumber("ggbtmpvarx") < CASmpreduce
						.getVarOrderingNumber("ggbtmpvary")) {
			expectedResult.append("b + c - 56 * x * y");
		} else if (CASmpreduce.getVarOrderingNumber("ggbtmpvarx") < CASmpreduce
				.getVarOrderingNumber("ggbtmpvary")
				&& CASmpreduce.getVarOrderingNumber("ggbtmpvary") < CASmpreduce
						.getVarOrderingNumber("ggbtmpvarb")
				&& CASmpreduce.getVarOrderingNumber("ggbtmpvarb") < CASmpreduce
						.getVarOrderingNumber("ggbtmpvarc")) {
			expectedResult.append("-56 * x * y + b + c");
		} else {
			fail("Expected result for this variable ordering not defined yet.");
		}

		t("(a - 7 x)^2 = b - 56 x y + c", expectedResult.toString());
	}

	@Test
	public void SimplificationOfEquations_SeveralVariables_1() {
		StringBuilder expectedResult = new StringBuilder("");

		if (CASmpreduce.getVarOrderingNumber("ggbtmpvara") < CASmpreduce
				.getVarOrderingNumber("ggbtmpvarb")
				&& CASmpreduce.getVarOrderingNumber("ggbtmpvarb") < CASmpreduce
						.getVarOrderingNumber("ggbtmpvarc")
				&& CASmpreduce.getVarOrderingNumber("ggbtmpvarc") < CASmpreduce
						.getVarOrderingNumber("ggbtmpvarx")) {
			expectedResult.append("(a * x^(2) + b * x + c) / a = 0");
		} else if (CASmpreduce.getVarOrderingNumber("ggbtmpvarx") < CASmpreduce
				.getVarOrderingNumber("ggbtmpvara")
				&& CASmpreduce.getVarOrderingNumber("ggbtmpvara") < CASmpreduce
						.getVarOrderingNumber("ggbtmpvarb")
				&& CASmpreduce.getVarOrderingNumber("ggbtmpvarb") < CASmpreduce
						.getVarOrderingNumber("ggbtmpvarc")) {
			expectedResult.append("(x^(2) * a + x * b + c) / a = 0");
		} else {
			fail("Expected result for this variable ordering not defined yet.");
		}

		t("(a x^2 + b x + c = 0) / a", expectedResult.toString());
	}
	

	/* Absolute Value */

	/* Absolute Value of Constants */

	@Test
	public void AbsoluteValues_ConstantsOnly_0() {
		t("Abs(17)", "17");
	}

	@Test
	public void AbsoluteValues_ConstantsOnly_1() {
		t("Abs(0)", "0");
	}

	@Test
	public void AbsoluteValues_ConstantsOnly_2() {
		t("Abs(-5)", "5");
	}

	/* Absolute Value of Variables */

	@Test
	public void AbsoluteValues_Variables_0() {
		t("Abs(x)", "abs(x)");
	}

	@Test
	public void AbsoluteValues_Variables_1() {
		t("Abs(-x)", "abs(x)");
	}
	

	/* Binomial Coefficient (alias Binomial) */

	@Test
	public void BinomialCoefficient_0() {
		t("BinomialCoefficient[4, 5]", "0");
	}

	@Test
	public void BinomialCoefficient_1() {
		t("BinomialCoefficient[5, 3]", "10");
	}

	@Test
	public void BinomialCoefficient_2() {
		t("BinomialCoefficient[n, 0]", "1");
	}

	@Test
	public void BinomialCoefficient_3() {
		t("BinomialCoefficient[n, 1]", "n");
	}

	@Test
	public void BinomialCoefficient_4() {
		t("BinomialCoefficient[n, 3]", "(n^(3) - 3 * n^(2) + 2 * n) / 6");
	}

	@Test
	public void BinomialCoefficient_5() {
		t("BinomialCoefficient[n, n - 3]", "(n^(3) - 3 * n^(2) + 2 * n) / 6");
	}

	@Test
	public void BinomialCoefficient_6() {
		t("BinomialCoefficient[n, n - 1]", "n");
	}

	@Test
	public void BinomialCoefficient_7() {
		t("BinomialCoefficient[n, n]", "1");
	}

	@Test
	public void BinomialCoefficient_8() {
		t("BinomialCoefficient[n, 3]", "(n^(3) - 3 * n^(2) + 2 * n) / 6");
	}

	@Test
	public void BinomialCoefficient_9() {
		t("BinomialCoefficient[154, 53]",
				"766790648099833656282026107194109056868560");
	}
	

	/* BinomialDist */

	/* Non-Cumulative */

	@Test
	public void BinomialDist_NonCumulative_0() {
		t("BinomialDist[3, 0.9, 0, false]", "1 / 1000");
	}

	@Test
	public void BinomialDist_NonCumulative_1() {
		t("BinomialDist[3, 0.9, 1, false]", "27 / 1000");
	}

	@Test
	public void BinomialDist_NonCumulative_2() {
		t("BinomialDist[3, 0.9, 2, false]", "243 / 1000");
	}

	@Test
	public void BinomialDist_NonCumulative_3() {
		t("BinomialDist[3, 0.9, 3, false]", "729 / 1000");
	}

	@Test
	public void BinomialDist_NonCumulative_4() {
		t("BinomialDist[3, 0.9, 4, false]", "0");
	}
	

	/* Cumulative */

	@Test
	public void BinomialDist_Cumulative_0() {
		t("BinomialDist[3, 0.9, 0, true]", "1 / 1000");
	}

	@Test
	public void BinomialDist_Cumulative_1() {
		t("BinomialDist[3, 0.9, 1, true]", "7 / 250");
	}

	@Test
	public void BinomialDist_Cumulative_2() {
		t("BinomialDist[3, 0.9, 2, true]", "271 / 1000");
	}

	@Test
	public void BinomialDist_Cumulative_3() {
		t("BinomialDist[3, 0.9, 3, true]", "1");
	}

	@Test
	public void BinomialDist_Cumulative_4() {
		t("BinomialDist[3, 0.9, 4, true]", "1");
	}
	

	/* Complex Numbers */

	// # TODO Extend

	@Test
	public void ComplexNumbers() {
		t("(5 + 3  \u03af) + Conjugate(5 + 3  \u03af)", "10");
	}
	

	/* Cauchy */

	@Test
	public void Cauchy_0() {
		t("Cauchy[1, 2, 3]", "3 / 4");
	}
	

	/* CFactor */

	@Test
	public void CFactor_0() {
		t("CFactor[x^2 + 4]", "(x + 2 * \u03af) * (x - 2 * \u03af)");
	}

	@Test
	public void CFactor_1() {
		t("CFactor[a^2 + x^2, a]", "(\u03af * x + a) * (- \u03af * x + a)",	"(a - x * \u03af) * (a + x * \u03af)");
	}

	@Test
	public void CFactor_2() {
		t("CFactor[a^2 + x^2, x]", "(x + a * \u03af) * (x - a * \u03af)",
				"(x + \u03af * a) * (x - \u03af * a)");
	}


	/* ChiSquared */

	/* Exact Evaluation */

	@Test
	public void ChiSquared_Exact_0() {
		t("ChiSquared[4, 3]", "gamma(2, 3 / 2)");
	}

	/* Numeric Evaluation */
	
	@Test
	public void ChiSquared_Numeric_0() {
		t("Numeric[ChiSquared[4, 3], 3]", "0.442");
	}
	
	
	/* Coefficients */

	@Test
	public void Coefficients_0() {
		t("Coefficients[x^3 - 3 x^2 + 3 x]", "{1, -3, 3, 0}");
	}
	
	@Test
	public void Coefficients_1() {
		t("Coefficients[x^3 - 3 x^2 + 3 x, a]", "{x^(3) - 3 * x^(2) + 3 * x}");
	}
	
	@Test
	public void Coefficients_2() {
		t("Coefficients[a^3 - 3 a^2 + 3 a, a]", "{1, -3, 3, 0}");
	}
	
	@Test
	public void Coefficients_3() {
		t("Coefficients[a^3 - 3 a^2 + 3 a, x]", "{a^(3) - 3 * a^(2) + 3 * a}");
	}
	

	/* CommonDenomiator */

	@Test
	public void CommonDenomiator_0() {
		t("CommonDenominator[(5 x + 3 x) / 5 x, 2 / (5 x - 5)]", "5 * x - 5");
	}

	@Test
	public void CommonDenomiator_1() {
		t("CommonDenominator[5 / 3, 1.35]", "60");
	}

	@Test
	public void CommonDenomiator_2() {
		t("CommonDenominator[3 / (2 x + 1), 3 / (4 x^2 + 4 x + 1)]",
				"4 * x^(2) + 4 * x + 1");
	}
	

	/* Covariance */

	@Test
	public void Covariance_0() {
		t("Covariance[{1, 2, 3}, {1, 3, 7}]", "2");
	}

	@Test
	public void Covariance_1() {
		t("Covariance[{(1, 1), (2, 3), (3, 7)}]", "2");
	}
	

	/* Cross */

	@Test
	public void Cross_0() {
		t("Cross[{1, 3, 2}, {0, 3, -2}]", "{-12, 2, 3}");
	}

	@Test
	public void Cross_1() {
		t("Cross[{a, b, c}, {d, e, f}]",
				"{b * f - c * e, -a * f + c * d, a * e - b * d}");
	}
	
	@Test
	public void Cross_2() {
		t("Cross[(a, b), (d, e)]",
				"a * e - b * d");
	}
	
	@Test
	public void Cross_3() {
		t("(a, b, c)\u2297 (d, e, f)",
				"(b * f - c * e, -a * f + c * d, a * e - b * d)");
	}
	
	@Test
	public void Cross_4() {
		t("(1,2)\u2297 (3,4)",
				"-2");
	}
	
	
	/* CSolutions */

	/* One Equation and one Variable */

	@Test
	public void CSolutions_OneVariable_0() {
		t("CSolutions[x^2 = -1]", "{ \u03af, - \u03af}");
	}

	@Test
	public void CSolutions_OneVariable_1() {
		t("CSolutions[x^2 + 1 = 0]", "{ \u03af, - \u03af}");
	}

	@Test
	public void CSolutions_OneVariable_2() {
		t("CSolutions[a^2 = -1, a]", "{ \u03af, - \u03af}");
	}

	/* Several Equations and Variables */

	@Test
	public void CSolutions_Several_0() {
		t("CSolutions[{y^2 = x - 1, x = 2 * y - 1}, {x, y}]",
				"{{1 + 2 *  \u03af, 1 +  \u03af}, {1 - 2 *  \u03af, 1 -  \u03af}}");
	}


	/* CSolve */

	/* One Equation and one Variable */

	@Test
	public void CSolve_OneVariable_0() {
		t("CSolve[x^2 = -1]", "{x =  \u03af, x = - \u03af}");
	}

	@Test
	public void CSolve_OneVariable_1() {
		t("CSolve[x^2 + 1 = 0, x]", "{x =  \u03af, x = - \u03af}");
	}

	@Test
	public void CSolve_OneVariable_2() {
		t("CSolve[a^2 = -1, a]", "{a =  \u03af, a = - \u03af}");
	}

	/* Several Equations and Variables */

	@Test
	public void CSolve_Several_0() {
		t("CSolve[{y^2 = x - 1, x = 2 * y - 1}, {x, y}]",
				"{{x = 1 + 2 *  \u03af, y = 1 +  \u03af}, {x = 1 - 2 *  \u03af, y = 1 -  \u03af}}");
	}


	/* Degree */

	 @Test public void Degree_0() {
		 t("Degree[x^4 + 2 x^2]", "4"); 
	}

	@Test
	public void Degree_1() {
		t("Degree[a x^4 + 2 x^2, x]", "4");
	}

	@Test
	public void Degree_2() {
		t("Degree[a x^4 + 2 x^2, a]", "1");
	}

	@Test
	public void Degree_3() {
		t("Degree[x^4 y^3 + 2 x^2 y^3, x]", "4");
	}

	@Test
	public void Degree_4() {
		t("Degree[x^4 y^3 + 2 x^2 y^3, y]", "3");
	}

	
	/* Delete */
	
	@Test
	public void Delete_0() {
		t("a := 4","4");
		t("a","4");
		t("Delete[a]", "true");
		t("a", "a");
	}


	/* Denominator */

	@Test
	public void Denominator_0() {
		t("Denominator[2 / 3 + 1 / 15]", "15");
	}

	@Test
	public void Denominator_1() {
		t("Denominator[5 / (x^2 + 2)]", "x^(2) + 2");
	}
	

	/* Derivative */

	@Test
	public void Derivative_0() {
		t("Derivative[x^2]", "2 * x");
	}

	@Test
	public void Derivative_1() {
		try {
			executeInCAS("Delete[a]");
		} catch (Throwable t) {
			Throwables.propagate(t);
		}
		t("Derivative[a x^3]", "3 * a * x^(2)");
	}

	@Test
	public void Derivative_2() {
		t("Derivative[a x^3, a]", "x^(3)");
	}

	@Test
	public void Derivative_3() {
		t("Derivative[a x^3, x, 2]", "6 * x * a", "6 * a * x");
	}
	

	/* Determinant */

	@Test
	public void Determinant_0() {
		t("Determinant[{{1, 2}, {3, 4}}]", "-2");
	}

	@Test
	public void Determinant_1() {
		t("Determinant[{{1, a}, {b, 4}}]", "-a * b + 4");
	}
	

	/* Dimension */

	@Test
	public void Dimension_0() {
		t("Dimension[{1, 2, 0, -4, 3}]", "5");
	}

	@Test
	public void Dimension_1() {
		t("Dimension[{{1, 2}, {0, -4}, {3, a}}]", "{3, 2}");
	}

	@Test
	public void Dimension_2() {
		t("Dimension[{{a, b}, {c, d}, {e, f}}]", "{3, 2}");
	}
	

	/* Div */

	@Test
	public void Div_0() {
		t("Div[16, 3]", "5");
	}

	@Test
	public void Div_1() {
		t("Div[x^2 + 3 x + 1, x - 1]", "x + 4");
	}
	

	/* Division */

	@Test
	public void Division_0() {
		t("Division[16, 3]", "{5, 1}");
	}

	@Test
	public void Division_1() {
		t("Division[x^2 + 3 x + 1, x - 1]", "{x + 4, 5}");
	}
	

	/* Divisors */

	@Test
	public void Divisiors_0() {
		t("Divisors[15]", "4");
	}
	

	/* DivisorsList */

	@Test
	public void DivisorsList_0() {
		t("DivisorsList[15]", "{1, 3, 5, 15}");
	}
	

	/* DivisorsSum */

	@Test
	public void DivisorsSum_0() {
		t("DivisorsSum[15]", "24");
	}
	

	/* Dot */

	@Test
	public void Dot_0() {
		t("Dot[{1, 3, 2}, {0, 3, -2}]", "5");
	}
	
	@Test
	public void Dot_1() {
		t("(1,2) * (a,b)","a+2*b");
	}
	

	/* Element */

	/* Singledimensional List */

	@Test
	public void Element_ListSD_0() {
		t("Element[{1, 3, 2}, 2]", "3");
	}

	@Test
	public void Element_ListSD_1() {
		t("Element[{a, b, c}, 2]", "b");
	}
	

	/* Matrix */

	@Test
	public void Element_Matrix_0() {
		t("Element[{{1, 3, 2}, {0, 3, -2}}, 2, 3]", "-2");
	}

	@Test
	public void Element_Matrix_1() {
		t("Element[{{a, b, c}, {d, e, f}}, 2, 3]", "f");
	}

	/*
	 * Note:
	 * 
	 * Although Geogebra itself supports the element command for
	 * multidimensional lists, Geogebra CAS does not.
	 */

	
	/* Expand */

	@Test
	public void Expand_0() {
		t("Expand[((a + b) / c)^2]", "(a^(2) + 2 * a * b + b^(2)) / c^(2)");
	}

	@Test
	public void Expand_1() {
		t("Expand[((a + b) / (c + d))^2]",
				"(a^(2) + 2 * a * b + b^(2)) / (c^(2) + 2 * c * d + d^(2))");
	}

	// TODO Simplify this ridiculously complex test! 
	
	@Test
	public void Expand_2() {
		t("Expand[Factor[a x^2 + b x^2]]", "a * x^(2) + b * x^(2)");
	}

	@Test
	public void Expand_3() {
		t("Expand[((a + b) / (c + d))^2]",
				"a^(2) / (c^(2) + 2 * c * d + d^(2)) + 2 * a * b / (c^(2) + 2 * c * d + d^(2)) + b^(2) / (c^(2) + 2 * c * d + d^(2))");
	}

	@Test
	public void Expand_4() {
		t("Expand[(x^6 + 6 x^5 + 30 x^4 + 120 x^3 + 360 x^2 + 720 x + 720) / 720]",
				"1/ 720 * x^(6) + 1 / 120 * x^(5) + 1 / 24 * x^(4) + 1 / 6 *x^(3) + 1 / 2 * x^(2) + x + 1");
	}

	@Test
	public void Expand_5() {
		t("Expand[(2 x - 1)^2 + 2 x + 3]", "4 * x^(2) - 2 * x + 4");
	}
	

	/* Exponential */

	@Test
	public void Exponential_0() {
		t("Exponential[2, 1]", "(ℯ^(2) - 1) / ℯ^(2)");
	}
	

	/* FDistribution */
	
	@Test
	public void FDistribution_0() {
		t("Fdistribution[5, 7, 3]", "betaRegularized(5/2, 7/2, 15/22)"); 
	}
	
	@Test
	public void FDistribution_1() {
		t("Numeric[Fdistribution[5, 7, 3], 14]", "0.90775343897945");
	}
	

	/* Factor */

	/* Factor with Constants */

	@Test
	public void Factor_ConstantsOnly_0() {
		t("Factor[-15]", "-3 * 5");
	}

	@Test
	public void Factor_ConstantsOnly_1() {
		t("Factor[15]", "3 * 5");
	}

	@Test
	public void Factor_ConstantsOnly_2() {
		t("Factor[1024]", "2^(10)");
	}

	@Test
	public void Factor_ConstantsOnly_3() {
		t("Factor[42]", "2 * 3 * 7");
	}

	/* Factor with Variables */

	@Test
	public void Factor_Variables_0() {
		t("Factor[x^2 + x - 6]", "(x + 3) * (x - 2)");
	}

	@Test
	public void Factor_Variables_1() {
		t("Factor[x^2 - y^2]", "(x + y) * (x - y)");
	}

	@Test
	public void Factor_Variables_2() {
		t("Factor[9 a^2 - 3 a b]", "3 * (3 * a - b) * a");
	}

	@Test
	public void Factor_Variables_3() {
		t("Factor[9 a^2 - 3 a^2 b]", "-3 * (b - 3) * a^(2)");
	}

	@Test
	public void Factor_Variables_4() {
		t("Factor[9 a^2 b^3 - 3 a b^2 c]", "3 * (3 * a * b - c) * a * b^(2)");
	}

	@Test
	public void Factor_Variables_5() {
		t("Factor[9x^2 - 25 y^2] ", "(3 * x + 5 * y) * (3 * x - 5 * y)");
	}

	@Test
	public void Factor_Variables_6() {
		t("Factor[5 a x+5 b x-2 b y-2 a y]", "(a + b) * (5 * x - 2 * y)");
	}

	@Test
	public void Factor_Variables_7() {
		t("Factor[36 r^2 + 24 r t + 4 t^2]", "4 * (3 * r + t)^(2)");
	}

	@Test
	public void Factor_Variables_8() {
		t("Factor[15 + 3 b^2 - 10 a - 2 a b^2]", "(-2 * a + 3) * (b^(2) + 5)");
	}

	@Test
	public void Factor_Variables_9() {
		t("Factor[((a + b) / (c + d))^2]", "(a + b)^(2) / (c + d)^(2)");
	}

	@Test
	public void Factor_Variables_10() {
		t("Factor[(a^2 - 2 * a * b + b^2)^2]", "(a - b)^(4)");
	}
	
	@Test
	public void Factor_Variables_11() {
		t("Factor[x^2 - y^2, x]", "(x + y) * (x - y)");
	}

	@Test
	public void Factor_Variables_12() {
		t("Factor[x^2 - y^2, y]", "(-x - y) * (-x + y)");
	}
	

	/* Factors */

	/* Factors with Constants */

	@Test
	public void Factors_ConstantsOnly_0() {
		t("Factors[-15]", "{{-3, 1}, {5, 1}}");
	}

	@Test
	public void Factors_ConstantsOnly_1() {
		t("Factors[15]", "{{3, 1}, {5, 1}}");
	}

	@Test
	public void Factors_ConstantsOnly_2() {
		t("Factors[1024]", "{{2, 10}}");
	}

	@Test
	public void Factors_ConstantsOnly_3() {
		t("Factors[42]", "{{2, 1}, {3, 1}, {7, 1}}");
	}

	/* Factors of Polynomials */

	@Test
	public void Factors_Variables_0() {
		t("Factors[x^8 - 1]",
				"{{x^(4) + 1, 1}, {x^(2) + 1, 1}, {x + 1, 1}, {x - 1, 1}}");
	}
	

	/* First */

	/* Constants Only */

	@Test
	public void First_ConstantsOnly_0() {
		t("First[{1, 4, 3}]", "{1}");
	}

	@Test
	public void First_ConstantsOnly_1() {
		t("First[{1, 4, 3}, 2]", "{1, 4}");
	}

	/* Variables */

	@Test
	public void First_Variables_0() {
		t("First[{a, b, c, d}]", "{a}");
	}

	@Test
	public void First_Variables_1() {
		t("First[{a, b, c, d}, 2]", "{a, b}");
	}
	

	/* Fit Exp */

	@Test
	public void FitExp_0() {
		t("Numeric[FitExp[{(0, 1), (2, 4)}], 15]", "ℯ^(0.693147180559945 * x)");
	}
	

	/* Fit Log */

	@Test
	public void FitLog_0() {
		t("FitLog[{(ℯ,1), (ℯ^2, 4)}]", "3 * log(x) - 2");
	}
	

	/* Fit Poly */

	@Test
	public void FitPoly_0() {
		t("FitPoly[{(-1, -1), (0, 1), (1, 1), (2, 5)}, 3]", "x^(3) - x^(2) + 1");
	}
	

	/* Fit Pow */

	@Test
	public void FitPow_0() {
		t("FitPow[{(1, 1), (3, 2), (7, 4)}]",
				"0.974488577374291 * x^(0.7084753128560123)");
	}
	

	/* Fit Sin */
	
	// TODO Check whether we can have this at all.

	//@Test
	//public void FitSin_0() {
	//	t("FitSin[{(1, 1), (2, 2), (3, 1), (4, 0), (5, 1), (6, 2)}]", "1 + sin((π / 2) * x + π / 2)");
	//}

	// TODO Abstract, as well?

	
	/* FractionalPart */

	@Test
	public void FractionalPart_0() {
		t("FractionalPart[6/5]", "1 / 5");
	}

	@Test
	public void FractionalPart_1() {
		t("FractionalPart[1/5 + 3/2 + 2]", "7 / 10");
	}

	@Test
	public void FractionalPart_2() {
		t("FractionalPart[-14/5]", "(-4) / 5");
	}
	

	/* GCD */

	/* Constants only */

	@Test
	public void GCD_ConstantsOnly_0() {
		t("GCD[12, 15]", "3");
	}

	@Test
	public void GCD_ConstantsOnly_1() {
		t("GCD[{12, 30, 18}]", "6");
	}

	/* Variables */

	@Test
	public void GCD_Variables_0() {
		t("GCD[x^2 + 4 x + 4, x^2 - x - 6]", "x + 2");
	}

	@Test
	public void GCD_Variables_1() {
		t("GCD[{x^2 + 4 * x + 4, x^2 - x - 6, x^3 - 4 * x^2 - 3 * x + 18}]",
				"x + 2");
	}
	

	/* HyperGeometric */

	/* Non-Cumulative */

	@Test
	public void HyperGeometric_NonCumulative_0() {
		t("HyperGeometric[10, 2, 2, 0, false]", "28 / 45");
	}

	@Test
	public void HyperGeometric_NonCumulative_1() {
		t("HyperGeometric[10, 2, 2, 1, false]", "16 / 45");
	}

	@Test
	public void HyperGeometric_NonCumulative_2() {
		t("HyperGeometric[10, 2, 2, 2, false]", "1 / 45");
	}

	@Test
	public void HyperGeometric_NonCumulative_3() {
		t("HyperGeometric[10, 2, 2, 3, false]", "0");
	}

	/* Cumulative */

	@Test
	public void HyperGeometric_Cumulative_0() {
		t("HyperGeometric[10, 2, 2, 0, true]", "28 / 45");
	}

	@Test
	public void HyperGeometric_Cumulative_1() {
		t("HyperGeometric[10, 2, 2, 1, true]", "44 / 45");
	}

	@Test
	public void HyperGeometric_Cumulative_2() {
		t("HyperGeometric[10, 2, 2, 2, true]", "1");
	}

	@Test
	public void HyperGeometric_Cumulative_3() {
		t("HyperGeometric[10, 2, 2, 3, true]", "1");
	}
	
	
	/* Identity */

	@Test
	public void Identity_0() {
		t("Identity[3]", "{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}}");
	}

	@Test
	public void Identity_1() {
		t("a := 3", "3");
		t("Identity[a]", "{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}}");
		t("Delete[a]","true");
		t("a := 4", "4");
		t("Identity[a]", "{{1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}}");
		t("Delete[a]","true");
		// Tidy up
		try {
			executeInCAS("Delete[a]");
		} catch (Throwable t) {
			Throwables.propagate(t);
		}
	}

	@Test
	public void Identity_2() {
		t("A := {{2, 17, -3}, {b, c, 0}, {f, 0, 1}}", "{{2, 17, -3}, {b, c, 0}, {f, 0, 1}}");
		t("A^0", "{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}}");
		
		// Tidy up
		try {
			executeInCAS("Delete[A]");
		} catch (Throwable t) {
			Throwables.propagate(t);
		}
	}
	

	/* Imaginary */

	@Test
	public void Imaginary_0() {
		t("Imaginary[17 + 3  \u03af]", "3");
	}
	

	/* ImplicitDerivative */

	@Test
	public void ImplicitDerivative_0() {
		t("ImplicitDerivative[x + y + 5, y, x]", "-1");
	}

	@Test
	public void ImplicitDerivative_1() {
		t("ImplicitDerivative[x^2 + y^2, y, x]", "(-x) / y");
	}
	
	/* Integral */

	/* Indefinite Integral */
	
	@Test
	public void Integral_Indefinite_0() {
		s("Integral[cos(x)]", "sin(x) + c_INDEX");
	}

	@Test
	public void Integral_Indefinite_1() {
		s("Integral[cos(a * t), t]", "sin (a * t) / a + c_INDEX");
	}

	@Test
	public void Integral_Indefinite_2() {
		s("Integral[-x^3 + x^2]", "(-1) / 4 * x^(4) + 1 / 3 * x^(3) + c_INDEX");
	}

	/* Definite Integral */

	@Test
	public void Integral_Definite_0() {
		t("Integral[cos(x), x, a, b]", "-sin(a) + sin(b)");
	}

	@Test
	public void Integral_Definite_1() {
		t("Integral[cos(t), t, a, b]", "-sin(a) + sin(b)");
	}
	

	/* IntegralBetween */

	@Test
	public void IntegralBetween_0() {
		t("IntegralBetween[sin(x), cos(x), π / 4, π * 5 / 4]", "2 * sqrt(2)");
	}

	@Test
	public void IntegralBetween_1() {
		t("IntegralBetween[a * sin(t), a * cos(t), t, π / 4, π * 5 / 4]",
				"2 * sqrt(2) * a", "2 * a * sqrt(2)");
	}
	

	/* Invert */

	/* Constants Only */

	@Test
	public void Invert_ConstantsOnly_0() {
		t("Invert[{{1, 2}, {3, 4}}]", "{{-2, 1}, {3 / 2, (-1) / 2}}");
	}

	/* Variables */

	@Test
	public void Invert_Variables_0() {
		t("Invert[{{a, b}, {c, d}}]",
				"{{d / (a * d - b * c), (-b) / (a * d - b * c)}, {(-c) / (a * d - b * c), a / (a * d - b * c)}}");
	}
	

	/* IsPrime */

	@Test
	public void IsPrime_0() {
		t("IsPrime[-11]", "false");
	}

	@Test
	public void IsPrime_1() {
		t("IsPrime[-10]", "false");
	}

	@Test
	public void IsPrime_2() {
		t("IsPrime[-2]", "false");
	}

	@Test
	public void IsPrime_3() {
		t("IsPrime[-1]", "false");
	}

	@Test
	public void IsPrime_4() {
		t("IsPrime[0]", "false");
	}

	@Test
	public void IsPrime_5() {
		t("IsPrime[1]", "false");
	}

	@Test
	public void IsPrime_6() {
		t("IsPrime[2]", "true");
	}

	@Test
	public void IsPrime_7() {
		t("IsPrime[10]", "false");
	}

	@Test
	public void IsPrime_8() {
		t("IsPrime[11]", "true");
	}
	

	/* Last */

	/* Constants Only */

	@Test
	public void Last_ConstantsOnly_0() {
		t("Last[{1, 4, 3}]", "{3}");
	}

	@Test
	public void Last_ConstantsOnly_1() {
		t("Last[{1, 4, 3}, 2]", "{4, 3}");
	}

	/* Variables */

	@Test
	public void Last_Variables_0() {
		t("Last[{a, b, c, d}]", "{d}");
	}

	@Test
	public void Last_Variables_1() {
		t("Last[{a, b, c, d}, 2]", "{c, d}");
	}
	

	/* LeftSide */

	@Test
	public void LeftSide_0() {
		t("LeftSide[x + 2 = 3x + 1]", "x + 2");
	}

	@Test
	public void LeftSide_1() {
		t("LeftSide[{a^2 + b^2 = c^2, x + 2 = 3 x + 1}]",
				"{a^(2) + b^(2), x + 2}");
	}

	@Test
	public void LeftSide_2() {
		t("LeftSide[{a^2 + b^2 = c^2, x + 2 = 3 x + 1}, 1]", "a^(2) + b^(2)");
	}
	

	/* Length */

	@Test
	public void Length_0() {
		t("Length[2 x, 0, 1]", "sqrt(5)");
	}

	@Test
	public void Length_1() {
		t("Length[2 a, a, 0, 1]", "sqrt(5)");
	}
	

	/* Limits */

	/* Limit */

	@Test
	public void Limits_Limit_0() {
		t("Limit[a sin(x)/x, 0]", "a");
	}

	@Test
	public void Limits_Limit_1() {
		t("Limit[a sin(v)/v, v, 0]", "a");
	}

	/* LimitAbove */

	@Test
	public void Limits_LimitAbove_0() {
		t("LimitAbove[1 / x, 0]", "Infinity");
	}

	@Test
	public void Limits_LimitAbove_1() {
		t("LimitAbove[1 / a, a, 0]", "Infinity");
	}

	/* LimitBelow */

	@Test
	public void Limits_LimitBelow_0() {
		t("LimitBelow[1 / x, 0]", "-Infinity");
	}

	@Test
	public void Limits_LimitBelow_1() {
		t("LimitBelow[1 / a, a, 0]", "-Infinity");
	}
	

	/* MatrixRank */

	@Test
	public void MatrixRank_0() {
		t("MatrixRank[{{0, 0}, {0, 0}}]", "0");
	}

	@Test
	public void MatrixRank_1() {
		t("MatrixRank[{{2, 2}, {1, 1}}]", "1");
	}

	@Test
	public void MatrixRank_2() {
		t("MatrixRank[{{1, 2}, {3, 4}}]", "2");
	}
	

	/* Max */

	@Test
	public void Max_0() {
		t("Max[12, 15]", "15");
	}

	@Test
	public void Max_1() {
		t("Max[{-2, 12, -23, 17, 15}]", "17");
	}
	

	/* Mean */

	@Test
	public void Mean_0() {
		t("Mean[{1, 2, 3, 5, 44}]", "11");
	}

	@Test
	public void Mean_1() {
		t("Mean[{1, 8}]", "9 / 2");
	}
	

	/* Median */

	@Test
	public void Median_0() {
		t("Median[{1, 2, 3}]", "2");
	}

	@Test
	public void Median_1() {
		t("Median[{1, 1, 8, 8}]", "9 / 2");
	}
	

	/* Min */

	@Test
	public void Min_0() {
		t("Min[12, 15]", "12");
	}

	@Test
	public void Min_1() {
		t("Min[{-2, 12, -23, 17, 15}]", "-23");
	}
	

	/* MixedNumber */

	@Test
	public void MixedNumber_0() {
		t("MixedNumber[-3.5]", "-3 - 1 / 2");
	}

	@Test
	public void MixedNumber_1() {
		t("MixedNumber[3.5]", "3 + 1 / 2");
	}

	@Test
	public void MixedNumber_2() {
		t("MixedNumber[12 / 3]", "4");
	}

	@Test
	public void MixedNumber_3() {
		t("MixedNumber[12 / 14]", "6 / 7");
	}
	

	/* Mod */

	@Test
	public void Mod_0() {
		t("Mod[9, 4]", "1");
	}

	@Test
	public void Mod_1() {
		t("Mod[x^3 + x^2 + x + 6, x^2 - 3]", "4 * x + 9");
	}
	

	/* NIntegral */

	@Test
	public void NIntegral_0() {
		t("NIntegral[ℯ^(-x^2), 0, 1]", "0.746824132812427");
	}

	@Test
	public void NIntegral_1() {
		t("NIntegral[ℯ^(-a^2), a, 0, 1]", "0.746824132812427");
	}
	

	/* NRoot */

	@Test
	public void NRoot_0() {
		t("NRoot[16, 4]", "2");
	}

	@Test
	public void NRoot_1() {
		t("NRoot[x^8, 2]", "abs(x)^(4)");
	}
	

	/* NextPrime */

	@Test
	public void NextPrime_0() {
		t("NextPrime[-10]", "2");
	}

	@Test
	public void NextPrime_1() {
		t("NextPrime[10000]", "10007");
	}
	

	/* Normal */

	@Test
	public void Normal_0() {
		t("Normal[2, 0.5, 1]", "sqrt(2) / (sqrt(π) * ℯ^(2))");
	}
	

	/* Numerator */

	@Test
	public void Numerator_0() {
		t("Numerator[(3x² + 1) / (2x - 1)]", "3 * x^(2) + 1");
	}

	@Test
	public void Numerator_1() {
		t("Numerator[2/3 + 1/15]", "11");
	}
	

	/* Numeric Evaluation */

	/* Constants only */

	@Test
	public void NumericEvaluation_ConstantsOnly_0() {
		t("Numeric[3 / 2]", "1.5");
	}

	@Test
	public void NumericEvaluation_ConstantsOnly_1() {
		t("Numeric[((15 + 9) * 3.5 - 2 * 0.5) / 4]", "20.75");
	}

	@Test
	public void NumericEvaluation_ConstantsOnly_2() {
		t("Numeric[π,15]", "3.14159265358979");
	}

	@Test
	public void NumericEvaluation_ConstantsOnly_3() {
		t("Numeric[sin(1), 20 ]", "0.84147098480789650665");
	}

	/* One Variable */

	@Test
	public void NumericEvaluation_OneVariable_0() {
		t("Numeric[x + x / 2]", "1.5 * x");
	}

	@Test
	public void NumericEvaluation_OneVariable_1() {
		t("Numeric[x + 0.2 x]", "1.2 * x");
	}

	/* Two Variables */

	@Test
	public void NumericEvaluation_TwoVariables_0() {
		t("Numeric[6 a * 3 b - (5 a b + b * 2a) / 2]", "14.5 * a * b");
	}

	@Test
	public void NumericEvaluation_NumericEvaluation_TwoVariables_1() {
		t("Numeric[0.2 * (a^2 - 3 b) * (-3 a + 5 b^2)]",
				"-0.6 * a^(3) + a^(2) * b^(2) + 1.8 * a * b - 3 * b^(3)");
	}

	/* Ordering of Powers */

	@Test
	public void NumericEvaluation_OrderingOfPowers() {
		t("Numeric[0.2 * (a^2 - 3 b) * (-3 a + 5 b^2)]",
				"-0.6 * a^(3) + a^(2) *  b^(2) + 1.8 * a * b - 3 * b^(3)");
	}

	/* Precision */

	@Test
	public void NumericEvaluation_Precision_0() {
		t("Numeric[10 + 22/7, 6]", "13.1429");
	}

	@Test
	public void NumericEvaluation_Precision_1() {
		t("Numeric[2 a - a/3, 2]", "1.7 * a");
	}

	@Test
	public void NumericEvaluation_Precision_2() {
		t("Numeric[2 a - a/3, 3]", "1.67 * a");
	}

	/* PartialFractions */

	@Test
	public void PartialFractions_0() {
		t("PartialFractions[x^2 / (x^2 - 2x + 1)]",
				"1 + 2 / (x - 1) + 1 / (x - 1)^(2)");
	}

	@Test
	public void PartialFractions_1() {
		t("PartialFractions[a^2 / (a^2 - 2a + 1), a]",
				"1 + 2 / (a - 1) + 1 / (a-1)^(2)");
	}
	

	/* PerpendicularVector */

	@Test
	public void PerpendicularVector_0() {
		t("PerpendicularVector[(3, 2)]", "(-2, 3)");
	}

	@Test
	public void PerpendicularVector_1() {
		try {
			executeInCAS("Delete[a]");
			executeInCAS("Delete[b]");
		} catch (Throwable t) {
			Throwables.propagate(t);
		}
		t("PerpendicularVector[(a, b)]", "(-b, a)");
	}
	

	/* PreviousPrime */

	@Test
	public void PreviousPrime_0() {
		t("PreviousPrime[-10]", "NaN");
	}

	@Test
	public void PreviousPrime_1() {
		t("PreviousPrime[-1]", "NaN");
	}

	@Test
	public void PreviousPrime_2() {
		t("PreviousPrime[0]", "NaN");
	}

	@Test
	public void PreviousPrime_3() {
		t("PreviousPrime[1]", "NaN");
	}

	@Test
	public void PreviousPrime_4() {
		t("PreviousPrime[2]", "NaN");
	}

	@Test
	public void PreviousPrime_5() {
		t("PreviousPrime[3]", "2");
	}

	@Test
	public void PreviousPrime_6() {
		t("PreviousPrime[10]", "7");
	}

	@Test
	public void PreviousPrime_7() {
		t("PreviousPrime[10000]", "9973");
	}
	

	/* Product */

	@Test
	public void Product_0() {
		t("Product[{1, 2, x}]", "2 * x");
	}

	@Test
	public void Product_1() {
		t("Product[x + 1, x, 2, 3]", "12");
	}
	

	/* Root */

	@Test
	public void Root_0() {
		t("Root[x^3 - 3 * x^2 - 4 * x + 12]", "{x = 3, x = 2, x = -2}");
	}
	

	/* RandomBinomial */

	@Test
	public void RandomBinomial_0() {
		r("RandomBinomial[3, 0.1]", "[0123]", "Any one of {0, 1, 2, 3}.");
	}
	

	/* RandomElement */

	@Test
	public void RandomElement_0() {
		r("RandomElement[{3, 2, -4, 7}]", "([237]|-4)",
				"Any one of {-4, 2, 3, 7}.");
	}
	

	/* RandomNormal */

	@Test
	public void RandomNormal_0() {
		// TODO Currently extremely weak test.
		s("RandomNormal[3, 0.1]", "\\d+\\.\\d+");
	}
	

	/* RandomPoisson */

	@Test
	public void RandomPoisson_0() {
		// TODO Currently weak test.
		s("RandomPoisson[3]", "\\d+");
	}
	

	/* RandomPolynomial */

	@Test
	public void RandomPolynomial_0() {
		/* Beware: This test is random based. */
		for (int i = 0; i < 100; i++) {
			s("RandomPolynomial[0, 1, 2]", "[12]");
		}
	}

	@Test
	public void RandomPolynomial_1() {
		/* Beware: This test is random based. */
		for (int i = 0; i < 100; i++) {
			r("RandomPolynomial[2, 1, 2]",
					"(2\\s\\*\\s)?x\\^\\(2\\)\\s\\+\\s(2\\s\\*\\s)?x\\s\\+\\s[12]",
					" A polynomial in x of degree 2 with all coefficients from {1, 2}.");

		}
	}

	@Test
	public void RandomPolynomial_2() {
		/* Beware: This test is random based. */
		for (int i = 0; i < 100; i++) {
			s("RandomPolynomial[a, 0, 1, 2]", "[12]");
		}
	}

	@Test
	public void RandomPolynomial_3() {
		/* Beware: This test is random based. */
		for (int i = 0; i < 100; i++) {
			r("RandomPolynomial[a, 2, 1, 2]",
					"(2\\s\\*\\s)?a\\^\\(2\\)\\s\\+\\s(2\\s\\*\\s)?a\\s\\+\\s[12]",
					" A polynomial in a of degree 2 with all coefficients from {1, 2}.");
		}
	}
	

	/* Rationalize */

	@Test
	public void Rationalize_0() {
		t("Rationalize[-3.5]", "(-7) / 2");
	}

	@Test
	public void Rationalize_1() {
		t("Rationalize[0]", "0");
	}

	@Test
	public void Rationalize_2() {
		t("Rationalize[1]", "1");
	}

	@Test
	public void Rationalize_3() {
		t("Rationalize[3.5]", "7 / 2");
	}
	

	/* Real */

	@Test
	public void Real_0() {
		t("Real[17 + 3  \u03af]", "17");
	}
	

	/* ReducedRowEchelonForm */

	@Test
	public void ReducedRowEchelonForm_0() {
		t("ReducedRowEchelonForm[{{0, 0}, {0, 0}}]", "{{0, 0}, {0, 0}}");
	}

	@Test
	public void ReducedRowEchelonForm_1() {
		t("ReducedRowEchelonForm[{{2, 2}, {1, 1}}]", "{{1, 1}, {0, 0}}");
	}

	@Test
	public void ReducedRowEchelonForm_2() {
		t("ReducedRowEchelonForm[{{1, 2}, {3, 4}}]", "{{1, 0}, {0, 1}}");
	}

	@Test
	public void ReducedRowEchelonForm_3() {
		t("ReducedRowEchelonForm[{{1, 6, 4}, {2, 8, 9}, {4, 5, 6}}]",
				"{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}}");
	}
	

	/* RightSide */
	@Test
	public void LeftSide_0a() {
		t("RightSide[x + 2 = 3x + 1]", "3 * x + 1");
	}

	@Test
	public void LeftSide_1a() {
		t("RightSide[{a^2 + b^2 = c^2, x + 2 = 3x + 1}]", "{c^(2), 3 * x + 1}");
	}
	

	/* Rounding */

	/* Round */

	@Test
	public void Rounding_Round_0() {
		t("Round(-5.5)", "-5");
	}

	@Test
	public void Rounding_Round_1() {
		t("Round(-5.4)", "-5");
	}

	@Test
	public void Rounding_Round_2() {
		t("Round(-4.5)", "-4");
	}

	@Test
	public void Rounding_Round_3() {
		t("Round(-4.4)", "-4");
	}

	@Test
	public void Rounding_Round_4() {
		t("Round(4.4)", "4");
	}

	@Test
	public void Rounding_Round_5() {
		t("Round(4.5)", "5");
	}

	@Test
	public void Rounding_Round_6() {
		t("Round(5.4)", "5");
	}

	@Test
	public void Rounding_Round_7() {
		t("Round(5.5)", "6");
	}

	/* Floor */

	@Test
	public void Rounding_Floor_0() {
		t("Floor(-5.5)", "-6");
	}

	@Test
	public void Rounding_Floor_1() {
		t("Floor(-5.4)", "-6");
	}

	@Test
	public void Rounding_Floor_2() {
		t("Floor(-4.5)", "-5");
	}

	@Test
	public void Rounding_Floor_3() {
		t("Floor(-4.4)", "-5");
	}

	@Test
	public void Rounding_Floor_4() {
		t("Floor(4.4)", "4");
	}

	@Test
	public void Rounding_Floor_5() {
		t("Floor(4.5)", "4");
	}

	@Test
	public void Rounding_Floor_6() {
		t("Floor(5.4)", "5");
	}

	@Test
	public void Rounding_Floor_7() {
		t("Floor(5.5)", "5");
	}

	/* Ceil */

	@Test
	public void Rounding_Ceil_0() {
		t("Ceil(-5.5)", "-5");
	}

	@Test
	public void Rounding_Ceil_1() {
		t("Ceil(-5.4)", "-5");
	}

	@Test
	public void Rounding_Ceil_2() {
		t("Ceil(-4.5) ", "-4");
	}

	@Test
	public void Rounding_Ceil_3() {
		t("Ceil(-4.4)", "-4");
	}

	@Test
	public void Rounding_Ceil_4() {
		t("Ceil(4.4)", "5");
	}

	@Test
	public void Rounding_Ceil_5() {
		t("Ceil(4.5)", "5");
	}

	@Test
	public void Rounding_Ceil_6() {
		t("Ceil(5.4)", "6");
	}

	@Test
	public void Rounding_Ceil_7() {
		t("Ceil(5.5)", "6");
	}
	

	/* SD */

	@Test
	public void SD_0() {
		t("SD[{1, 2, 3, 4, 5}]", "sqrt(2)");
	}
	

	/* Sample */

	@Test
	public void Sample_0() {
		/* Beware: This test is random based. */
		for (int i = 0; i < 100; i++) {
			s("Sample[{1, 2, 3, 4, 5}, 5]",
					"{[12345], [12345], [12345], [12345], [12345]}");
		}
	}

	@Test
	public void Sample_1() {
		/* Beware: This test is random based. */
		for (int i = 0; i < 100; i++) {
			r("Sample[{-5, 2, a, 7, c}, 3]",
					"\\{(([ac27]|-5),\\s){2}([ac27]|-5)\\}",
					"A list containing three elements out of {a, c, -5, 2, 7}, where elements may be contained several times.");
		}
	}

	@Test
	public void Sample_2() {
		/* Beware: This test is random based. */
		for (int i = 0; i < 100; i++) {
			r("Sample[{1, 2, 3, 4, 5}, 5, true]",
					"\\{([1-5],\\s){4}[1-5]\\}",
					"A list containing five elements out of {1, 2, 3, 4, 5}, where elements may be contained several times.");
		}
	}

	@Test
	public void Sample_3() {
		/* Beware: This test is random based. */
		// TODO Check for multiple elements.
		for (int i = 0; i < 100; i++) {
			r("Sample[{{1, 2, 3}, 4, 5, 6, 7, 8}, 3, false]",
					"\\{(([4-8]|\\{1,\\s2,\\s3\\}),\\s){2}([4-8]|\\{1,\\s2,\\s3\\})\\}",
					"A list containing three elements out of {{1, 2, 3}, 4, 5, 6, 7, 8}, where each element may be contained only once.");
		}
	}
	

	/* SampleSD */

	@Test
	public void SampleSD_0() {
		// TODO Result unchecked.
		t("SampleSD[{1, 2, 3}]", "1");
	}

	@Test
	public void SampleSD_1() {
		// TODO Result unchecked.
		t("SampleSD[{1, 2, a}]", "sqrt(a^(2) - 3 * a + 3) / sqrt(3)");
	}
	

	/* SampleVariance */

	@Test
	public void SampleVariance_0() {
		// TODO Result unchecked.
		t("SampleVariance[{x, y, z}]",
				"(x^(2) - x * y - x * z + y^(2) - y * z + z^(2)) / 3");
	}
	

	/* Sequence */

	@Test
	public void Sequence_0() {
		t("Sequence[5]", "{1, 2, 3, 4, 5}");
	}

	@Test
	public void Sequence_1() {
		t("Sequence[x^i, i, 1, 10]",
				"{x, x^(2), x^(3), x^(4), x^(5), x^(6), x^(7), x^(8), x^(9), x^(10)}");
	}

	@Test
	public void Sequence_2() {
		t("Sequence[x^i, i, 1, 10, 2]", "{x, x^(3), x^(5), x^(7), x^(9)}");
	}
	

	/* Shuffle */

	@Test
	public void Shuffle_0() {
		/* Beware: This test is random based. */
		// TODO Check for missing / multiple elements.
		r("Shuffle[{3, 5, 1, 7, 3}]", "\\{([1357],\\s){4}[1357]\\}",
				"An arbitrary permutation of the list {1, 3, 3, 5, 7}.");
	}
	

	/* Simplify */

	@Test
	public void Simplify_0() {
		t("Simplify[3 * x + 4 * x + a * x]", "x * a + 7 * x", "a * x + 7 * x");
	}
	

	/* Solutions */

	// TODO Extend.

	@Test
	public void Solutions_0() {
		t("Solutions[x^2 = 4x]", "{4, 0}");
	}

	@Test
	public void Solutions_1() {
		t("Solutions[x * a^2 = 4a, a]", "{4 / x, 0}");
	}

	
	@Test
	public void Solutions_2() {
		t("Solutions[{x = 4 x + y , y + x = 2}, {x, y}]", "{{-1, 3}}");
	}

	@Test
	public void Solutions_3() {
		t("Solutions[{2a^2 + 5a + 3 = b, a + b = 3}, {a, b}]",
				"{{0, 3}, {-3, 6}}");
	}
	

	/* Solve */

	/* Constants only */

	@Test
	public void Solve_ConstantsOnly_0() {
		s("Solve[1 = 1]", "{x = c_INDEX}");
	}

	@Test
	public void Solve_ConstantsOnly_1() {
		t("Solve[1 = 2]", "{}");
	}

	@Test
	public void Solve_ConstantsOnly_2() {
		s("Solve[1 = 1, x]", "{x = c_INDEX}");
	}

	@Test
	public void Solve_ConstantsOnly_3() {
		t("Solve[1 = 2, x]", "{}");
	}

	@Test
	public void Solve_ConstantsOnly_4() {
		s("Solve[{1 = 1, 2 = 2}, {x, y}]",
				"{{x = c_INDEX, y = c_INDEX}}");
	}

	@Test
	public void Solve_ConstantsOnly_5() {
		t("Solve[{1 = 2, 2 = 3}, {x, y}]", "{}");
	}

	/* One Variable */

	@Test
	public void Solve_OneVariable_0() {
		t("Solve[x = 1]", "{x = 1}");
	}

	@Test
	public void Solve_OneVariable_1() {
		t("Solve[x = 1, x]", "{x = 1}");
	}

	@Test
	public void Solve_OneVariable_2() {
		t("Solve[2 x + 3 = 4 + 1 / 3, x]", "{x = 2 / 3}");
	}

	@Test
	public void Solve_OneVariable_3() {
		t("Solve[3 - 5 / x = 7, x]", "{x = (-5) / 4}");
	}

	@Test
	public void Solve_OneVariable_4() {
		t("Solve[(x + 7)/ 3 - (4 - x) / 4 - 3 = 3 x / 8, x]", "{x = 8}");
	}

	@Test
	public void Solve_OneVariable_5() {
		t("Solve[x^2 - 5 x + 6 = 0, x]", "{x = 3, x = 2}");
	}

	@Test
	public void Solve_OneVariable_6() {
		t("Solve[x^2 - 4 = 0, x]", "{x = 2, x = -2}");
	}

	@Test
	public void Solve_OneVariable_7() {
		t("Solve[x^2 - 4 x - 1 = 0, x]", "{x = sqrt(5) + 2, x = -sqrt(5) + 2}");
	}

	@Test
	public void Solve_OneVariable_8() {
		t("Solve[x^2 - 4 x + 4 = 0, x]", "{x = 2}");
	}

	@Test
	public void Solve_OneVariable_9() {
		t("Solve[x^2 + 1 = 0, x]", "{}");
	}

	/* One Variable, variable Coefficients */

	@Test
	public void Solve_OneVariableVC_0() {
		t("Solve[a x^2 + b x + c, x]",
				"{x = (sqrt(-4 * a * c + b^(2)) - b) / (2 * a), x = (-sqrt(-4 * a * c + b^(2)) - b) / (2 * a)}",
				"{x = (-b+sqrt(-4 * a * c + b^(2))) / (2 * a), x = (-b-sqrt(-4 * a * c + b^(2))) / (2 * a)}");
	}

	@Test
	public void Solve_OneVariableVC_1() {
		t("Solve[1 / p - (a - k^2) / a = k^2 / (p a), a]", "{a = k^(2)}");
	}

	@Test
	public void Solve_OneVariableVC_2() {
		t("Solve[1 / p - (a - k^2) / a = k^2 / (p a), k]",
				"{k = sqrt(a), k = -sqrt(a)}");
	}

	@Test
	public void Solve_OneVariableVC_3() {
		t("Solve[1 / p - (a - k^2) / a = k^2 / (p a), p]", "{p = 1}");
	}

	@Test
	public void Solve_OneVariableVC_4() {
		t("Solve[-(10 c + 3) / (2 (4 c^2 - 9)) = -1 / (2 (2 c - 3)), c]",
				"{c = 0}");
	}

	@Test
	public void Solve_OneVariableVC_5() {
		t("Solve[a = b^c, a]", "{a = b^(c)}");
	}

	@Test
	public void Solve_OneVariableVC_6() {
		t("Solve[a = b^c, b]", "{b = a^(1 / c)}");
	}

	@Test
	public void Solve_OneVariableVC_7() {
		t("Solve[a = b^c, c]", "{c = log(a) / log(b)}");
	}

	@Test
	public void Solve_OneVariableVC_8() {
		// TODO Resolve whether we can have this.
		t("Solve[0 = b^c, b]", "{b = 0}");
	}

	@Test
	public void Solve_OneVariableVC_9() {
		t("Solve[-1 = b^c, b]", "{}");
	}

	@Test
	public void Solve_OneVariableVC_10() {
		t("Solve[0.5 N0 = N0 exp(-0.3 t), t]", "{t = (10 * log(2)) / 3}",
				"{t = 10 * log(cbrt(2))}");
	}
	
	@Test
	public void Solve_Trig_0() {
		s("Solve[3*tan(x)+3=0]", "{x = (4 * k_INDEX * π - π) / 4}");
	}

	@Test
	public void Solve_Trig_1() {
		s("Solve[2*cos(x)^2+sqrt(2)*cos(x)-2,x]",
				"{x = (8 * k_INDEX * π + π) / 4, x = (8 * k_INDEX * π - π) / 4}");
	}

	/* Several Equations and Variables */

	@Test
	public void Solve_Several_0() {
		t("Solve[{x = 4 x + y , y + x = 2}, {x, y}]", "{{x = -1, y = 3}}");
	}

	@Test
	public void Solve_Several_1() {
		t("Solve[{2a^2 + 5a + 3 = b, a + b = 3}, {a, b}]",
				"{{a = 0, b = 3}, {a = -3, b = 6}}");
	}

	@Test
	public void Solve_Several_2() {
		t("Solve[{2a^2 + 5a + 3 = b, a + b = 3, a = b}, {a, b}]", "{}");
	}
	

	/* SolveODE */
	
	@Test
	public void SolveODE_0() {
		s("SolveODE[y / x]", "y = x * c_INDEX");
	}

	@Test
	public void SolveODE_1() {
		s("SolveODE[y / x, y, x]", "y = x * c_INDEX");
	}
	

	/* Substitute */

	@Test
	public void Substitute_0() {
		t("Substitute[3 m - 3, 3 m - 3, a]", "a");
	}

	@Test
	public void Substitute_1() {
		t("Substitute[(3 m - 3)^2, 3 m - 3, a]", "a^(2)");
	}

	@Test
	public void Substitute_2() {
		t("Substitute[(3 m - 3)^2 - (n + 3)^2, 3 m - 3, a]",
				"a^(2) - n^(2) -6 * n - 9");
	}

	@Test
	public void Substitute_3() {
		t("Substitute[(3 m - 3)^2 - (m + 3)^2, m, a]", "8 * a^(2) - 24 * a");
	}
	
	@Test
	public void Substitute_4() {
		try {
			executeInCAS("Delete[a]");
			executeInCAS("Delete[b]");
			executeInCAS("Delete[c]");
			executeInCAS("Delete[x]");
			executeInCAS("Delete[y]");
			executeInCAS("Delete[z]");
		} catch (Throwable t) {
			Throwables.propagate(t);
		}
		t("Substitute[2x + 3y - z, {x=a, y=2, z=b}]", "2 * a - b + 6");
	}
	

	/* Sum */

	@Test
	public void Sum_0() {
		t("Substitute[3 m - 3, 3 m - 3, a]", "a");
	}

	@Test
	public void Sum_1() {
		t("Sum[i^2, i, 1, 3]", "14");
	}

	@Test
	public void Sum_2() {
		t("Sum[r^i, i, 0, n]", "(r^(n + 1) - 1) / (r - 1)",
				"(r^(n) * r - 1) / (r - 1)");
	}

	@Test
	public void Sum_3() {
		t("Sum[(1/3)^i, i, 0, Infinity]", "3 / 2");
	}
	

	/* Take */

	@Test
	public void Take_0() {
		t("Take[{1, 2, a, 4, 5}, 2, 4]", "{2, a, 4}");
	}
	

	/* TaylorPolynomial (alias TaylorSeries) */

	/* Variable not specified */

	@Test
	public void TaylorPolynomial_VariableNotSpecified_0() {
		try {
			executeInCAS("Delete[a]");
			executeInCAS("Delete[x]");
		} catch (Throwable t) {
			Throwables.propagate(t);
		}
		
		t("TaylorPolynomial[x^2, 3, 1]", "9 +6 * (x - 3)");
	}

	@Test
	public void TaylorPolynomial_VariableNotSpecified_1() {
		try {
			executeInCAS("Delete[a]");
			executeInCAS("Delete[x]");
		} catch (Throwable t) {
			Throwables.propagate(t);
		}
		
		t("TaylorPolynomial[x^2, a, 1]", "a^(2) + 2 * a*(x-a)");
	}
	
	/* Variable specified */

	@Test
	public void TaylorPolynomial_VariableSpecified_0() {
		t("TaylorPolynomial[x^3, x, 1, 1]", "1 + 3*(x - 1)");
	}

	@Test
	public void TaylorPolynomial_VariableSpecified_1() {
		t("TaylorPolynomial[x^3, x, 1, 2]", "1 + 3*(x-1)+3*(x-1)^(2)");
	}

	@Test
	public void TaylorPolynomial_VariableSpecified_2() {
		t("TaylorPolynomial[x^3, x, 0, 3]", "x^(3)");
	}

	@Test
	public void TaylorPolynomial_VariableSpecified_3() {
		t("TaylorPolynomial[x^3, x, 1, 3]", "1 + 3 * (x - 1) + 3 * (x - 1)^(2) + (x - 1)^(3)");
	}

	@Test
	public void TaylorPolynomial_VariableSpecified_4() {
		t("TaylorPolynomial[x^3 sin(y), x, 3, 2]",
				"27*sin(y)+27*sin(y)*(x-3)+9*sin(y)*(x-3)^(2)");
	}

	@Test
	public void TaylorPolynomial_VariableSpecified_5() {
		t("TaylorPolynomial[x^3 sin(y), y, 3, 1]",
				"sin(3) * x^(3) + cos(3) * x^(3) * (y-3)");
	}

	@Test
	public void TaylorPolynomial_VariableSpecified_6() {
		t("TaylorPolynomial[x^3 sin(y), y, 3, 2]",
				"sin(3) * x^(3) + cos(3) * x^(3) * (y - 3) - (sin(3) * x^(3)) / 2 * (y - 3)^(2)");
	}


	/* ToComplex */

	@Test
	public void ToComplex_0() {
		t("ToComplex[(3, 2)]", "3 + 2 *  \u03af", "2 *  \u03af + 3");
	}
	

	/* ToExponential */

	@Test
	public void ToExponential_0() {
		t("ToExponential[1 +  \u03af]", "sqrt(2) * ℯ^(( \u03af * π) / 4)");
	}
	

	/* ToPoint */

	@Test
	public void ToPoint_0() {
		t("ToPoint[3 + 2 \u03af]", "(3, 2)");
	}
	

	/* ToPolar */

	@Test
	public void ToPolar_0() {
		t("ToPolar[(1, sqrt(3))]", "(2; π / 3)");
	}

	@Test
	public void ToPolar_1() {
		t("ToPolar[1 + sqrt(3) *  \u03af]", "(2; π / 3)");
	}
	

	/* Transpose */

	@Test
	public void Transpose_0() {
		t("Transpose[{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}]",
				"{{1, 4, 7}, {2, 5, 8}, {3, 6, 9}}");
	}

	@Test
	public void Transpose_1() {
		t("Transpose[{{a, b}, {c, d}}]", "{{a, c}, {b, d}}");
	}
	

	/* Unique */

	@Test
	public void Unique_0() {
		t("Unique[{1, 2, 4, 1, 4}]", "{1, 2, 4}");
	}

	@Test
	public void Unique_1() {
		t("Unique[{1, x, x, 1, a}]", "{1, x, a}");
	}
	

	/* UnitPerpendicularVector (alias UnitOrthogonalVector) */

	@Test
	public void UnitPerpendicularVector_0() {
		t("UnitPerpendicularVector[(3, 4)]", "((-4) / 5, 3 / 5)");
	}

	@Test
	public void UnitPerpendicularVector_1() {
		t("UnitPerpendicularVector[(a, b)]",
				"((-b) / sqrt(a^(2) + b^(2)) , a / sqrt(a^(2) + b^(2)))");
	}
	

	/* UnitVector */

	@Test
	public void UnitVector_0() {
		t("UnitVector[(3, 4)]", "(3 / 5, 4 / 5)");
	}

	@Test
	public void UnitVector_1() {
		t("UnitVector[(a, b)]",
				"(a / sqrt(a^(2) + b^(2)), b / sqrt(a^(2) + b^(2)))",
				"((sqrt(a^(2) + b^(2)) * a) / (a^(2) + b^(2)), (sqrt(a^(2) + b^(2)) *b) / (a^(2) + b^(2)))");
	}

	@Test
	public void UnitVector_2() {
		t("UnitVector[(2, 4, 4)]", "(1 / 3, 2 / 3, 2 / 3)");
	}
	

	/* Variance */

	@Test
	public void Variance_0() {
		t("Variance[{1, 2, 3}]", "2 / 3");
	}

	@Test
	public void Variance_1() {
		t("Variance[{1, 2, a}]", "(2 * a^(2) - 6 * a + 6) / 9");
	}
	
	// TODO Find out what the following is about.
	
	//MyVector Package
	
	@Test
	public void Vectors_AddingVectors_1() {
		t("(1, 2)+(3,4)","(4,6)");
	}
	
	@Test
	public void Vectors_AddingVectors_2() {
		t("(1, 2)+{3,3}","(4,5)");
	}
	
	@Test
	public void Vectors_AddingVectors_3() {
		t("{a, b}+(3,2)","(a+3,b+2)");
	}
	
	@Test
	public void Vectors_SubtractingVectors_1() {
		t("(1, 2)-(3,4)","(-2,-2)");
	}
	
	@Test
	public void Vectors_SubtractingVectors_2() {
		t("(1, 2)-{3,3}","(-2,-1)");
	}
	
	@Test
	public void Vectors_SubtractingVectors_3() {
		t("{a, b}-(3,2)","(a-3,b-2)");
	}
	
	@Test
	public void Vectors_ScalarProduct_1() {
		t("(1, 2)*(3,4)","11");
	}
	
	@Test
	public void Vectors_ScalarProduct_2() {
		t("{1, 2}*(3,4)","11");
	}
	
	@Test
	public void Vectors_ScalarProduct_3() {
		t("(1, 2)*{3,4}","11");
	}
	
	@Test
	public void Vectors_ScalarProduct_4() {
		t("(x, 2)*{3,4}","3*x+8");
	}
	
	@Test
	public void Vectors_MatrixTimesVector_1() {
		t("{{a,b},{c,d}}*(x,y)","(a*x+b*y,c*x+d*y)");
	}
	
	@Test
	public void Vectors_MatrixTimesVector_2() {
		t("{{1,2},{2,1}}*(3,4)","(11,10)");
	}
	
	@Test
	public void Vectors_MatrixTimesVector_3() {
		t("(a,b)*{{1,2},{3,4}}","(a+3*b,2*a+4*b)");
	}
	
	
	/* Ticket */

	/* Ticket 1274: Derivative of exp(2x) wrong */

	@Test
	public void Ticket_Ticket1274_0() {
		t("Derivative[ℯ^(2 * x)]", "2 * (ℯ^(x))^(2)", "2 * ℯ^(2 * x)");
	}

	/* Ticket 1336: Support for brackets with built-in functions */

	@Test
	public void Ticket_Ticket1336_0() {
		t("Abs[17]", "17");
	}

	@Test
	public void Ticket_Ticket1336_1() {
		t("Round[4.4]", "4");
	}

	@Test
	public void Ticket_Ticket1336_2() {
		t("Ceil[4.4]", "5");
	}

	@Test
	public void Ticket_Ticket1336_3() {
		t("Floor[4.4]", "4");
	}

	@Test
	public void Ticket_Ticket1336_4() {
		t("(5 + 3  \u03af) + Conjugate[5 + 3  \u03af]", "10");
	}

	/* Ticket 1477: e becomes E in CAS */

	@Test
	public void Ticket_Ticket1477() {
		t("e", "e");
	}
	
	/* Ticket 1899: Solve[2^x = 8] yields unwanted result */
	
	@Test
	public void Ticket_Ticket1899() {
		t("Solve[2^x = 8]", "{x = 3}", "{x = log(8) / log(2)}");
	}
	
	/* Ticket 2481: NSolves gives wrong solution */
	
	@Test
	public void Ticket_Ticket2481() {
		t("Numeric[NSolve[13^(x+1)-2*13^x=1/5*5^x,x], 11]", "{x = -4.1939143755}");
		
		/*
		 * Autotest and offline evaluation results differ in this test case.
		 * 
		 * Since minor differences are to be expected with numeric algorithms,
		 * this is not considered an error. Therefore the number of significant
		 * figures of this test has been decreased artificially.
		 * 
		 * The original test was
		 * 		t("[NSolve[13^(x+1)-2*13^x=1/5*5^x,x]", "{x = -4.193914375465535}");.
		 * The result from autotest was
		 * 		{x = -4.193914375476052}.
		 * The exact result is
		 * 		{x = (-ln(55)) / (ln(13) - ln(5))}.
		 */
	}
	
	/* Ticket 2651: Function not Evaluated */

	@Test
	public void Ticket_Ticket2651() {
		t("f(x) := FitPoly[{(-1, -1), (0, 1), (1, 1), (2, 5)}, 3]", "x^(3) - x^(2) + 1");
		t("f(-1)", "-1");
		
		// Tidy up
		try {
			t("Delete[f]","true");
		} catch (Throwable t) {
			Throwables.propagate(t);
		}
	}
	
	@Test
	public void Rubrik1 () {
		t("KeepInput[x-1/2=2x+3]","x - 1 / 2 = 2 * x + 3");
		t("KeepInput[(x-1/2=2x+3)+1/2]","(x - 1 / 2 = 2 * x + 3)+1/2");
		t("(x-1/2=2x+3)+1/2","x = (4 * x + 7) / 2");
		t("(x-1/2=2x+3)+1/2","x = (4 * x + 7) / 2");
		t("Numeric[(x-1/2=2x+3)+1/2]","x = 2 * x + 3.5");
	}
	
	@Test
	public void Rubrik2 () {
		t("f(t):=100*1.5^t","100 * (3 / 2) ^ (t)");
		t("f(2)","225");
		t("Solve[f(t)=225,t]","{t = 2}","{t = log(9 / 4)/log(3 / 2)}");
		t("Numeric[Solve[f(t)=225,t]]","{t = 2}");
		t("Solve[225=c*1.5^2,c]","{c = 100}");
		t("Solve[225=100*a^2,a]","{a = 3 / 2 , a = (-3) / 2}");		
	}
	
	@Test
	public void Rubrik3 () {
		t("f(t):=c*a^t","a ^ (t) * c");
		t("Solve(f(2)=225,a)","{a = 15 * sqrt(1 / c), a = -15 * sqrt(1 / c)}");
	}
	
	@Test
	public void Rubrik4 () {
		//t("f(x):=a * x^3 + b * x^2 + c * x + d","a * x^(3) + b * x^(2) + c * x + d");
		GeoCasCell f = new GeoCasCell(kernel.getConstruction());
		kernel.getConstruction().addToConstructionList(f,false);
		f.setInput("f(x):=a * x^3 + b * x^2 + c * x + d");
		f.computeOutput();
		
		Assert.assertEquals(f.getOutput(StringTemplate.defaultTemplate),
				"f(x):=a x³ + b x² + c x + d");
		t("g1:=f(1)=1","a + b + c + d = 1");
		t("g2:=f(2) = 2","8 * a + 4 * b + 2 * c + d = 2");
		t("g3:=f'(1) = 0","3 * a + 2 * b + c = 0");
		t("g4:=f''(1) = 0","6 * a + 2 * b = 0");
		t("Solve({g1,g2,g3,g4},{a,b,c,d})","{{a = 1, b = -3, c = 3, d = 0}}");
	}
	
	@Test
	public void Rubrik56() {
		t("A:={{2,3,2},{1,1,1},{0,-1,3}}","{{2,3,2},{1,1,1},{0,-1,3}}");
		t("B:={{3},{2},{7}}","{{3},{2},{7}}");
		t("X:={{x},{y},{z}}","{{x},{y},{z}}");
		t("A*X=B","{{2 * x + 3*y + 2 * z},{x + y +z},{ - y + 3 * z}}={{3},{2},{7}}");
		
	}
	@Test
	public void Rubrik7() {
		t("A:={{2,3,2},{1,1,1},{0,-1,3}}","{{2,3,2},{1,1,1},{0,-1,3}}");
		t("B:={{3},{2},{7}}","{{3},{2},{7}}");
		t("Invert[A] * B","{{1},{-1},{2}}");
	}
	@Test
	public void QuickStart(){
		t("f(x) := x^2 - 3/2 * x + 2","(2* x^(2) - 3 * x + 4) / 2");
		t("g(x) := 1/2 * x + 2","(x + 4) / 2");
		t("h(x):=f(x)-g(x)","x^(2) - 2 * x");
		t("Factor[h(x)]","(x - 2) * x","x * (x - 2)");
		t("Solve[h(x)=0,x]","{x = 2, x = 0}","{x = 0, x = 2}");
		t("S:=Intersect[f(x),g(x)]","{(2,3),(0,2)}");
	}	

	@Test
	public void ExponentialEqs(){
		kernel.getApplication().getSettings().getCasSettings().setTimeoutMilliseconds(60000);
		cas.getCurrentCAS().settingsChanged(kernel.getApplication().getSettings().getCasSettings());
		t("Solve[7^(2x-5) 5^x = 9^(x+1),x]","{x = (-log(151263)) / (log(9) - 2 * log(7) - log(5))}");
		t("Solve[13^(x+1)-2*13^x=(1/5)*5^x,x]","{x = (-log(55)) / (log(13) - log(5))}");
		t("Solve[{6.7*10^9=c*a^2007,3*10^8=c*a^950},{c,a}]","{{c = (300000000 * 3^(950 / 1057)) / 67^(950 / 1057), a = 67^(1 / 1057) / 3^(1 / 1057)}}");
		kernel.getApplication().getSettings().getCasSettings().setTimeoutMilliseconds(5000);
		cas.getCurrentCAS().settingsChanged(kernel.getApplication().getSettings().getCasSettings());
	}
}
