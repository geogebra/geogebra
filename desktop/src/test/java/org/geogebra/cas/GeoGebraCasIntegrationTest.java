package org.geogebra.cas;

import java.util.HashSet;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.cas.view.CASCellProcessor;
import org.geogebra.common.cas.view.CASInputHandler;
import org.geogebra.common.kernel.GeoGebraCasInterface;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Traversing.CommandCollector;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.debug.Log;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import com.himamis.retex.editor.share.util.Unicode;

@SuppressWarnings("javadoc")
public class GeoGebraCasIntegrationTest extends BaseCASIntegrationTest {
	private static final String GermanSolve = "L\u00f6se";

	/**
	 * Executes the given expression in the CAS.
	 * 
	 * @param input
	 *            The expression to be evaluated, in geogebra's CAS syntax.
	 * @return The string returned by GeogebraCAS.
	 */
	private static String executeInCAS(String input) throws Throwable {
		GeoGebraCasInterface cas = kernel.getGeoGebraCAS();
		CASparser parser = (CASparser) cas.getCASparser();
		ValidExpression inputVe = parser
				.parseGeoGebraCASInputAndResolveDummyVars(input, kernel, null);
		String result = cas.evaluateGeoGebraCAS(inputVe, arbconst,
				StringTemplate.numericDefault, null, kernel);

		if (result == null || result.length() <= 0) {
			return "";
		}

		// Parse input into valid expression.
		ExpressionValue outputVe = parser.parseGeoGebraCASInput(result, null);

		// Resolve Variable objects in ValidExpression as GeoDummy objects.
		parser.resolveVariablesForCAS(outputVe, kernel);
		boolean includesNumericCommand = false;
		HashSet<Command> commands = new HashSet<>();
		inputVe.traverse(CommandCollector.getCollector(commands));
		if (!commands.isEmpty()) {
			for (Command cmd : commands) {
				String cmdName = cmd.getName();
				// Numeric used
				includesNumericCommand = includesNumericCommand
						|| ("Numeric".equals(cmdName)
								&& cmd.getArgumentNumber() > 1);
			}
		}
		return outputVe
				.toString(includesNumericCommand ? StringTemplate.testNumeric
						: StringTemplate.testTemplate);
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
	private static String checkRegex(String input, String expectedPattern,
			String readablePattern) {
		try {
			String result = executeInCAS(input);
			if (result.matches(expectedPattern)) {
				return null;
			}
			if (readablePattern != null) {
				return "\nExpected: " + readablePattern + "\ngot: " + result;
			}
			return "\nExpected: " + expectedPattern + "\ngot: ";
		} catch (Throwable t) {
			return t.getClass().getName() + ":" + t.getMessage();
		}
	}

	private static void r(String input, String expectedPattern,
			String readablePattern) {
		String error = checkRegex(input, expectedPattern, readablePattern);
		if (error != null) {
			Assert.fail(error);
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
		Log.debug(newPattern);
		r(input, newPattern, expectedPattern);
	}

	// 100 seconds max per method tested
	@SuppressWarnings("deprecation")
	@Rule
	public Timeout globalTimeout = new Timeout(50000);

	// Self Test Section

	/* Forgetting before tests */

	@Test
	public void selftest_Forget_0() {
		t("f(x) := x^2 + p * x + q", "x^(2) + p * x + q", "p * x + q + x^(2)",
				"x^(2) + x * p + q");
	}

	@Test
	public void selftest_Forget_1() {
		t("f(x)", "f(x)");
	}

	/* Remembering during tests */

	@Test
	public void selftest_Remember_0() {
		t("f(x) := x^2 + p * x + q", "x^(2) + p * x + q", "p * x + q + x^(2)",
				"x^(2) + x * p + q");
		t("f(x)", "x^(2) + p * x + q", "p * x + q + x^(2)",
				"x^(2) + x * p + q");
	}

	/**
	 * Checks if the assignment of variables by using the ':=' operator is
	 * working
	 */
	@Test
	public void assignment_0() {
		t("testvar", "testvar");
		t("testvar := 1", "1");
		t("testvar", "1");

		// Tidy up
		try {
			t("Delete[testvar]", "true");
		} catch (Throwable t) {
			propagate(t);
		}
	}
	/* Simplification of Terms */

	@Test
	public void simplificationOfTerms_OrderingOfPowers_1() {
		t("f(x) := a * x^3 + b * x^2 + c * x + d",
				"a * x^(3) + b * x^(2) + c * x + d");
	}

	@Test
	public void simplificationOfTerms_OrderingOfPowers_2() {
		t("x^3 + c * x^2 + a*x + b", "x^(3) + c * x^(2) + a * x + b");
	}

	@Test
	public void simplificationOfTerms_OrderingOfPowers_3() {
		t("x^2 + a * x", "x^(2) + a * x");
	}

	/* Polynomial Division */
	@Test
	public void simplificationOfTerms_PolynomialDivision_2() {
		t("(x^2 - y^2) / (x - y)", "x + y");
	}

	/* Several Variables */

	@Test
	public void simplificationOfEquations_SeveralVariables_0() {
		t("(a - 7 x)^2 = b - 56 x y + c",
				"(a - 7 * x)^(2) = -56 * x * y + b + c");
	}

	/* Parametrics */

	/* Parametric Term */

	@Test
	public void parametric_Term_0() {
		t("(3, 2) + t * (5, 1)", "(5 * t + 3, t + 2)");
	}

	@Test
	public void parametric_Term_1() {
		t("(3, 2) + t * (0, 0)", "(3, 2)");
	}

	@Test
	public void parametric_Term_2() {
		t("(0, 0) + t * (5, 1)", "(5 * t, t)");
	}

	@Test
	public void parametric_Term_3() {
		t("(3, sqrt(2)) + t * (sqrt(5), 1)", "(t * sqrt(5) + 3, t + sqrt(2))",
				"(sqrt(5) * t + 3, t + sqrt(2))");
	}

	@Test
	public void parametric_Term_4() {
		t("Numeric[(3, sqrt(2)) + t * (sqrt(5), 1)]",
				"(2.2360679775 * t + 3, t + 1.414213562373)");
	}

	@Test
	public void parametric_Term_5() {
		tk("(3, sqrt(2)) + t * (sqrt(5), 1)",
				"(3, sqrt(2)) + t * (sqrt(5), 1)");
	}

	/* Parametric Function */

	@Test
	public void parametric_Function_0() {
		t("f(t) := (3, 2) + t * (5, 1)", "(5 * t + 3, t + 2)");
	}

	@Test
	public void parametric_Function_1() {
		t("f(t) := (3, 2) + t * (0, 0)", "(3, 2)");
	}

	@Test
	public void parametric_Function_2() {
		t("f(t) := (0, 0) + t * (5, 1)", "(5 * t, t)");
	}

	@Test
	public void parametric_Function_3() {
		t("f(t) := (3, sqrt(2)) + t * (sqrt(5), 1)",
				"(t * sqrt(5) + 3, t + sqrt(2))",
				"(sqrt(5) * t + 3, t + sqrt(2))");
	}

	@Test
	public void parametric_Function_4() {
		t("f(t) := Numeric[(3, sqrt(2)) + t * (sqrt(5), 1), 10]",
				"(2.236067977 * t + 3, t + 1.414213562)");
	}

	@Test
	public void parametric_Function_5() {
		tk("f(t) := (3, sqrt(2)) + t * (sqrt(5), 1)",
				"(3, sqrt(2)) + t * (sqrt(5), 1)");
	}

	/* Parametric Equation Elaborate */

	@Test
	public void parametric_EquationE_0() {
		t("(x, y) = (3, 2) + t * (5, 1)", "(x, y) = (5 * t + 3, t + 2)");
	}

	@Test
	public void parametric_EquationE_1() {
		t("(x, y) = (3, 2) + t * (0, 0)", "(x, y) = (3, 2)");
	}

	@Test
	public void parametric_EquationE_2() {
		t("(x, y) = (0, 0) + t * (5, 1)", "(x, y) = (5 * t, t)");
	}

	@Test
	public void parametric_EquationE_3() {
		t("(x, y) = (3, sqrt(2)) + t * (sqrt(5), 1)",
				"(x, y) = (t * sqrt(5) + 3, t + sqrt(2))",
				"(x, y) = (sqrt(5) * t + 3, t + sqrt(2))");
	}

	@Test
	public void parametric_EquationE_4() {
		t("Numeric[(x, y) = (3, sqrt(2)) + t * (sqrt(5), 1)]",
				"(x, y) = (2.2360679775 * t + 3, t + 1.414213562373)");
	}

	@Test
	public void parametric_EquationE_5() {
		tk("(x, y) = (3, sqrt(2)) + t * (sqrt(5), 1)",
				"(x, y) = (3, sqrt(2)) + t * (sqrt(5), 1)");
	}

	/* Parametric Equation Abbreviation */

	@Test
	public void parametric_EquationA_0() {
		t("X = (3, 2) + t * (5, 1)", "X = (5 * t + 3, t + 2)");
	}

	@Test
	public void parametric_EquationA_1() {
		t("X = (3, 2) + t * (0, 0)", "X = (3, 2)");
	}

	@Test
	public void parametric_EquationA_2() {
		t("X = (0, 0) + t * (5, 1)", "X = (5 * t, t)");
	}

	@Test
	public void parametric_EquationA_3() {
		t("X = (3, sqrt(2)) + t * (sqrt(5), 1)",
				"X = (t * sqrt(5) + 3, t + sqrt(2))",
				"X = (sqrt(5) * t + 3, t + sqrt(2))");
	}

	@Test
	public void parametric_EquationA_4() {
		t("Numeric[X = (3, sqrt(2)) + t * (sqrt(5), 1)]",
				"X = (2.2360679775 * t + 3, t + 1.414213562373)");
	}

	@Test
	public void parametric_EquationA_5() {
		tk("X = (3, sqrt(2)) + t * (sqrt(5), 1)",
				"X = (3, sqrt(2)) + t * (sqrt(5), 1)");
	}

	/* Labeled Parametric Equation */

	@Test
	public void parametric_EquationL_1() {
		t("f: X = (3, 2) + t * (5, 1)", "X = (5 * t + 3, t + 2)");
	}

	@Test
	public void parametric_EquationL_2() {
		t("f: (x, y) = (3, sqrt(2)) + t * (sqrt(5), 1)",
				"(x, y) = (t * sqrt(5) + 3, t + sqrt(2))",
				"(x, y) = (sqrt(5) * t + 3, t + sqrt(2))");
	}

	@Test
	public void parametric_EquationL_3() {
		t("f: X = (3, sqrt(2)) + t * (sqrt(5), 1)",
				"X = (t * sqrt(5) + 3, t + sqrt(2))",
				"X = (sqrt(5) * t + 3, t + sqrt(2))");
	}

	@Test
	public void parametric_EquationL_4() {
		t("f: Numeric[(x, y) = (3, sqrt(2)) + t * (sqrt(5), 1)]",
				"(x, y) = (2.2360679775 * t + 3, t + 1.414213562373)");
	}

	@Test
	public void parametric_EquationL_5() {
		t("f: Numeric[X = (3, sqrt(2)) + t * (sqrt(5), 1)]",
				"X = (2.2360679775 * t + 3, t + 1.414213562373)");
	}

	@Test
	public void parametric_EquationL_6() {
		tk("f: (x, y) = (3, sqrt(2)) + t * (sqrt(5), 1)",
				"(x, y) = (3, sqrt(2)) + t * (sqrt(5), 1)");
	}

	@Test
	public void parametric_EquationL_7() {
		tk("f: X = (3, sqrt(2)) + t * (sqrt(5), 1)",
				"X = (3, sqrt(2)) + t * (sqrt(5), 1)");
	}

	/* Parametric Term Multiple Parameters */

	@Test
	public void parametric_TermM_0() {
		t("(3, 2) + t * (5, 1) + s * (-1, 7)",
				"(-s + 5 * t + 3, 7 * s + t + 2)",
				"(3, 2) + s * (-1, 7) + t * (5, 1)");
	}

	@Test
	public void parametric_TermM_1() {
		t("(3, 2) + t * (0, 0) + s * (-1, 7)", "(-s + 3, 7 * s + 2)",
				"(3, 2) + s * (-1, 7)");
	}

	@Test
	public void parametric_TermM_2() {
		t("(3, 2) + t * (0, 0) + s * (0, 0)", "(3, 2)");
	}

	@Test
	public void parametric_TermM_3() {
		t("(0, 0) + t * (5, 1) + s * (-1, 7)", "(-s + 5 * t, 7 * s + t)",
				"s * (-1, 7) + t * (5, 1)");
	}

	/* Parametric Function Multiple Parameters */

	@Test
	public void parametric_FunctionM_0() {
		t("f(t, s) := (3, 2) + t * (5, 1) + s * (-1, 7)",
				"(-s + 5 * t + 3, 7 * s + t + 2)");
	}

	@Test
	public void parametric_FunctionM_1() {
		t("f(t, s) := (3, 2) + t * (0, 0) + s * (-1, 7)",
				"(-s + 3, 7 * s + 2)");
	}

	@Test
	public void parametric_FunctionM_2() {
		t("f(t, s) := (3, 2) + t * (0, 0) + s * (0, 0)", "(3, 2)");
	}

	@Test
	public void parametric_FunctionM_3() {
		t("f(t, s) := (0, 0) + t * (5, 1) + s * (-1, 7)",
				"(-s + 5 * t, 7 * s + t)");
	}

	/* Parametric Equation Elaborate Multiple Parameters */

	@Test
	public void parametric_EquationEM_0() {
		t("(x, y) = (3, 2) + t * (5, 1) + s * (-1, 7)",
				"(x, y) = (-s + 5 * t + 3, 7 * s + t + 2)");
	}

	@Test
	public void parametric_EquationEM_1() {
		t("(x, y) = (3, 2) + t * (0, 0) + s * (-1, 7)",
				"(x, y) = (-s + 3, 7 * s + 2)");
	}

	@Test
	public void parametric_EquationEM_2() {
		t("(x, y) = (3, 2) + t * (0, 0) + s * (0, 0)", "(x, y) = (3, 2)");
	}

	@Test
	public void parametric_EquationEM_3() {
		t("(x, y) = (0, 0) + t * (5, 1) + s * (-1, 7)",
				"(x, y) = (-s + 5 * t, 7 * s + t)");
	}

	/* Parametric Equation Abbreviation Multiple Parameters */

	@Test
	public void parametric_EquationAM_0() {
		t("X = (3, 2) + t * (5, 1) + s * (-1, 7)",
				"X = (-s + 5 * t + 3, 7 * s + t + 2)");
	}

	@Test
	public void parametric_EquationAM_1() {
		t("X = (3, 2) + t * (0, 0) + s * (-1, 7)", "X = (-s + 3, 7 * s + 2)");
	}

	@Test
	public void parametric_EquationAM_2() {
		t("X = (3, 2) + t * (0, 0) + s * (0, 0)", "X = (3, 2)");
	}

	@Test
	public void parametric_EquationAM_3() {
		t("X = (0, 0) + t * (5, 1) + s * (-1, 7)",
				"X = (-s + 5 * t, 7 * s + t)");
	}

	/* Labeled Parametric Equation Multiple Parameters */

	@Test
	public void parametric_EquationLM_0() {
		t("f: (x, y) = (3, 2) + t * (5, 1) + s * (-1, 7)",
				"(x, y) = (-s + 5 * t + 3, 7 * s + t + 2)");
	}

	@Test
	public void parametric_EquationLM_1() {
		t("f: X = (3, 2) + t * (5, 1) + s * (-1, 7)",
				"X = (-s + 5 * t + 3, 7 * s + t + 2)");
	}

	@Test
	public void parametric_EquationLM_2() {
		t("f: (x, y) = (3, sqrt(2)) + t * (sqrt(5), 1) + s * (-1, sqrt(7))",
				"(x, y) = (-s + t * sqrt(5) + 3, s * sqrt(7) + t + sqrt(2))",
				"(x, y) = (-s + sqrt(5) * t + 3, sqrt(7) * s + t + sqrt(2))");
	}

	@Test
	public void parametric_EquationLM_3() {
		t("f: X = (3, sqrt(2)) + t * (sqrt(5), 1) + s * (-1, sqrt(7))",
				"X = (-s + t * sqrt(5) + 3, s * sqrt(7) + t + sqrt(2))",
				"X = (-s + sqrt(5) * t + 3, sqrt(7) * s + t + sqrt(2))");
	}

	@Test
	public void parametric_EquationLM_4() {
		t("f: Numeric[(x, y) = (3, sqrt(2)) + t * (sqrt(5), 1) + s * (-1, sqrt(7))]",
				"(x, y) = (-s + 2.2360679775 * t + 3, 2.645751311065 * s + t + 1.414213562373)");
	}

	@Test
	public void parametric_EquationLM_5() {
		t("f: Numeric[X = (3, sqrt(2)) + t * (sqrt(5), 1) + s * (-1, sqrt(7))]",
				"X = (-s + 2.2360679775 * t + 3, 2.645751311065 * s + t + 1.414213562373)");
	}

	@Test
	public void parametric_EquationLM_6() {
		tk("f: (x, y) = (3, sqrt(2)) + t * (sqrt(5), 1) + s * (-1, sqrt(7))",
				"(x, y) = (3, sqrt(2)) + t * (sqrt(5), 1) + s * (-1, sqrt(7))");
	}

	@Test
	public void parametric_EquationLM_7() {
		tk("f: X = (3, sqrt(2)) + t * (sqrt(5), 1) + s * (-1, sqrt(7))",
				"X = (3, sqrt(2)) + t * (sqrt(5), 1) + s * (-1, sqrt(7))");
	}

	/* Absolute Value */

	/* Absolute Value of Constants */

	// # TODO Extend

	@Test
	public void complexNumbers() {
		t("(5 + 3  \u03af) + Conjugate(5 + 3  \u03af)", "10");
	}

	/* Coefficients */

	/* CompleteSquare */

	@Test
	public void completeSquare_0() {
		t("CompleteSquare[3 x^4 + x^2]", "3 * (x^(2) + 1 / 6)^(2) - 1 / 12");
	}

	@Test
	public void completeSquare_1() {
		t("CompleteSquare[x^4+x^2+1]", "(x^(2) + 1 / 2)^(2) + 3 / 4");
	}

	@Test
	public void completeSquare_2() {
		t("CompleteSquare[x^6+x^3]", "(x^(3) + 1 / 2)^(2) - 1 / 4");
	}

	@Test
	public void completeSquare_3() {
		t("CompleteSquare[x^6+x^3+1]", "(x^(3) + 1 / 2)^(2) + 3 / 4");
	}

	@Test
	public void completeSquare_4() {
		t("CompleteSquare[-9x^12-8x^6-9]", "-9 * (x^(6) + 4 / 9)^(2) - 65 / 9");
	}

	@Test
	public void completeSquare_5() {
		t("CompleteSquare[(-6 x^18 - 9x^9 + 2)]",
				"-6 * (x^(9) + 3 / 4)^(2) + 43 / 8");
	}

	@Test
	public void completeSquare_6() {
		t("CompleteSquare[(-6 x^2 - 9x + 2)]", "-6 * (x + 3 / 4)^(2) + 43 / 8");
	}

	@Test
	public void completeSquare_7() {
		t("CompleteSquare[-10x^7+3]", "?");
	}

	@Test
	public void completeSquare_8() {
		t("CompleteSquare[((-9) * x^(10)) + 4]", "-9 * x^(10) + 4");
	}

	@Test
	public void completeSquare_9() {
		t("CompleteSquare[-3x^2+5x+8]", "-3 * (x - 5 / 6)^(2) + 121 / 12");
	}

	@Test
	public void completeSquare_10() {
		t("CompleteSquare[3x^2+5x+8]", "3 * (x + 5 / 6)^(2) + 71 / 12");
	}

	/* CommonDenomiator */

	/* Cross */

	/* CSolutions */

	/* One Equation and one Variable */

	@Test
	public void cSolutions_OneVariable_0() {
		t("CSolutions[x^2 = -1]", "{\u03af, -\u03af}", "{-\u03af, \u03af}");
	}

	@Test
	public void cSolutions_OneVariable_1() {
		t("CSolutions[x^2 + 1 = 0]", "{\u03af, -\u03af}", "{-\u03af, \u03af}");
	}

	@Test
	public void cSolutions_OneVariable_2() {
		t("CSolutions[a^2 = -1, a]", "{\u03af, -\u03af}", "{-\u03af, \u03af}");
	}

	/* Several Equations and Variables */

	@Test
	public void cSolutions_Several_0() {
		t("CSolutions[{y^2 = x - 1, x = 2 * y - 1}, {x, y}]",
				"{{1 - 2 * \u03af, 1 - \u03af}, {1 + 2 * \u03af, 1 + \u03af}}",
				"{{1 + 2 * \u03af, 1 + \u03af}, {1 - 2 *  \u03af, 1 - \u03af}}");
	}

	/* CSolve */

	/* One Equation and one Variable */

	@Test
	public void cSolve_OneVariable_0() {
		t("CSolve[x^2 = -1]", "{x = \u03af, x = -\u03af}",
				"{x = -\u03af, x = \u03af}");
	}

	@Test
	public void cSolve_OneVariable_1() {
		t("CSolve[x^2 + 1 = 0, x]", "{x = \u03af, x = -\u03af}",
				"{x = -\u03af, x = \u03af}");
	}

	@Test
	public void cSolve_OneVariable_2() {
		t("CSolve[a^2 = -1, a]", "{a = \u03af, a = -\u03af}",
				"{a = -\u03af, a = \u03af}");
	}

	/* Several Equations and Variables */

	@Test
	public void cSolve_Several_0() {
		t("CSolve[{y^2 = x - 1, x = 2 * y - 1}, {x, y}]",
				"{{x = 1 - 2 * \u03af, y = 1 - \u03af}, {x = 1 + 2 * \u03af, y = 1 + \u03af}}",
				"{{x = 1 + 2 * \u03af, y = 1 + \u03af}, {x = 1 - 2 * \u03af, y = 1 - \u03af}}");
	}

	/* Delete */

	@Test
	public void delete_0() {
		t("a := 4", "4");
		t("a", "4");
		t("Delete[a]", "true");
		t("a", "a");
	}

	/* Dot */

	@Test
	public void dot_1() {
		t("(1,2) * (a,b)", "a+2*b");
	}

	/* Element */

	/* Singledimensional List */

	/*
	 * Note:
	 * 
	 * Although Geogebra itself supports the element command for
	 * multidimensional lists, Geogebra CAS does not.
	 */

	/* Expand */

	@Test
	public void expand_4() {
		t("Expand[(x^6 + 6 x^5 + 30 x^4 + 120 x^3 + 360 x^2 + 720 x + 720) / 720]",
				"1/ 720 * x^(6) + 1 / 120 * x^(5) + 1 / 24 * x^(4) + 1 / 6 *x^(3) + 1 / 2 * x^(2) + x + 1");
	}

	@Test
	public void expand_5() {
		t("Expand[(2 x - 1)^2 + 2 x + 3]", "4 * x^(2) - 2 * x + 4");
	}

	@Test
	public void expand_6() {
		t("Expand[(a + b)^2] / (a + b)", "a + b");
	}

	/* Fit Sin */

	// TODO Check whether we can have this at all.

	// @Test
	// public void FitSin_0() {
	// t("FitSin[{(1, 1), (2, 2), (3, 1), (4, 0), (5, 1), (6, 2)}]", "1 +
	// sin((pi / 2) * x + pi / 2)");
	// }

	// TODO Abstract, as well?

	/* Cumulative */

	/* Identity */

	@Test
	public void identity_1() {
		t("dim := 3", "3");
		t("Identity[dim]", "{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}}");
		t("Delete[dim]", "true");
		t("dim := 4", "4");
		t("Identity[dim]",
				"{{1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}}");
		t("Delete[dim]", "true");
	}

	@Test
	public void identity_2() {
		t("A := {{2, 17, -3}, {b, c, 0}, {f, 0, 1}}",
				"{{2, 17, -3}, {b, c, 0}, {f, 0, 1}}");
		t("A^0", "{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}}");

		// Tidy up
		try {
			executeInCAS("Delete[A]");
		} catch (Throwable t) {
			propagate(t);
		}
	}

	/* Integral */

	/* Indefinite Integral */

	@Test
	public void integral_Indefinite_0() {
		s("Integral[cos(x)]", "sin(x) + c_INDEX");
	}

	@Test
	public void integral_Indefinite_1() {
		// s("Integral[cos(a * t), t]", "sin (a * t) / a + c_INDEX");
		s("Integral[cos(a * t), t]", "sin (a * t) / a + c_INDEX");
	}

	@Test
	public void integral_Indefinite_2() {
		s("Integral[-x^3 + x^2]", "(-1) / 4 * x^(4) + 1 / 3 * x^(3) + c_INDEX");
	}

	/* LeftSide */

	@Test
	public void leftSide_0() {
		in("lsa:x+y+z=1");
		t("LeftSide[lsa]", "x + y + z");
		t("RightSide[lsa]", "1");
		in("lsb:xx+yy+zz=1");
		t("LeftSide[lsb]", "x^(2) + y^(2) + z^(2)");
		t("RightSide[lsb]", "1");
		t("Coefficients[lsb]", "{1, 1, 1, -1, 0, 0, 0, 0, 0, 0}");
		in("lsb2:xx+2yy+3zz+4xy+5zy+6xz+7x+8y+9z=4");
		t("LeftSide[lsb2]",
				"x^(2) + 2 * y^(2) + 3 * z^(2) + 4 * x * y + 6 * x * z + 5 * y * z + 7 * x + 8 * y + 9 * z");
		t("RightSide[lsb2]", "4");
		t("Coefficients[lsb2]", "{1, 2, 3, -4, 4, 6, 5, 7, 8, 9}");
		in("lsb3:xx+yy=1");
		t("LeftSide[lsb3]", "x^(2) + y^(2) - 1");
		t("RightSide[lsb3]", "0");
		t("Coefficients[lsb3]", "{1, 1, -1, 0, 0, 0}");
		in("lsc:x+y+0z=1");
		t("LeftSide[lsc]", "x + y");
		t("RightSide[lsc]", "1");
		in("lsd:x+y=1");
		t("LeftSide[lsd]", "x + y");
		t("RightSide[lsd]", "1");
	}

	@Test
	/**
	 * Tests for Solve(line/circle/quadric) defined in the Algebra View
	 * 
	 * https://dev.geogebra.org/trac/changeset/67205
	 * https://dev.geogebra.org/trac/changeset/67218
	 * 
	 */
	public void solveAlgebraView() {
		in("solveline1:y=x");
		in("solveline2:y=-x");
		in("solvecircle1:x^2+y^2=4");
		in("solvecircle2:(x-1)^2+y^2=4");
		in("solvequaric1:x^2+y^2+z^2=3");
		in("solvequaric2:x^2+3y^2-z^2=3");
		in("solvequaric3:x^2+2y^2+3z^2=6");

		t("Solve({solveline1,solveline2},{x,y})", "{{x = 0, y = 0}}");
		t("Solve({solveline1,solvecircle1},{x,y})",
				"{{x = sqrt(2), y = sqrt(2)}, {x = -sqrt(2), y = -sqrt(2)}}");
		t("Solve({solvecircle2,solvecircle1},{x,y})",
				"{{x = 1 / 2, y = sqrt(15) / 2}, {x = 1 / 2, y = (-sqrt(15)) / 2}}");
		// order not deterministic: only count elements
		t("Length(Solve({solvequaric1,solvequaric2,solvequaric3},{x,y,z}))",
				"8");
	}

	/**
	 * Make an object in the Algebra View (to test AV -> CAS handling)
	 * 
	 * @param string
	 *            AV input
	 */
	private static void in(String string) {
		kernel.getAlgebraProcessor().processAlgebraCommand(string,
				false);
	}

	/* Normal */

	@Test
	public void normal_1() {
		t("NSolve[Normal[0, 1, eps]-Normal[0, 1, -eps]=0.5]",
				"{eps = 0.6744897501961}");
	}

	/* Two Variables */
	@Test
	public void numericEvaluation_NumericEvaluation_TwoVariables_1() {
		t("Numeric[0.2 * (a^2 - 3 b) * (-3 a + 5 b^2)]",
				"-0.6 * a^(3) + 1 * a^(2) * b^(2) + 1.8 * a * b - 3 * b^(3)",
				"a^(2) * b^(2) - 0.6 * a^(3) - 3 * b^(3) + 1.8 * a * b",
				"-0.6 * a^(3) + a^(2) * b^(2) + 1.8 * a * b - 3 * b^(3)");
	}

	/* PerpendicularVector */

	@Test
	public void perpendicularVector_0() {
		t("PerpendicularVector[(3, 2)]", "(-2, 3)");
	}

	@Test
	public void perpendicularVector_1() {
		try {
			executeInCAS("Delete[a]");
			executeInCAS("Delete[b]");
		} catch (Throwable t) {
			propagate(t);
		}
		t("PerpendicularVector[(a, b)]", "(-b, a)");
	}

	/* Root */

	@Test
	public void root_0() {
		t("Root[x^3 - 3 * x^2 - 4 * x + 12]", "{x = -2, x = 2, x = 3}");
	}

	/* RandomBinomial */

	@Test
	public void randomBinomial_0() {
		r("RandomBinomial[3, 0.1]", "[0123]", "Any one of {0, 1, 2, 3}.");
	}

	/* RandomElement */

	@Test
	public void randomElement_0() {
		r("RandomElement[{3, 2, -4, 7}]", "([237]|-4)",
				"Any one of {-4, 2, 3, 7}.");
	}

	/* RandomNormal */

	@Test
	public void randomNormal_0() {
		// TODO Currently extremely weak test.
		s("RandomNormal[3, 0.1]", "\\d+\\.\\d+");
	}

	/* RandomPoisson */

	@Test
	public void randomPoisson_0() {
		// TODO Currently weak test.
		s("RandomPoisson[3]", "\\d+");
	}

	/* RandomPolynomial */

	@Test
	public void randomPolynomial_0() {
		/* Beware: This test is random based. */
		for (int i = 0; i < 100; i++) {
			s("RandomPolynomial[0, 1, 2]", "[12]");
		}
	}

	@Test
	public void randomPolynomial_1() {
		/* Beware: This test is random based. */
		for (int i = 0; i < 100; i++) {
			r("RandomPolynomial[2, 1, 2]",
					"(2\\s\\*\\s)?x\\^\\(2\\)\\s\\+\\s(2\\s\\*\\s)?x\\s\\+\\s[12]",
					" A polynomial in x of degree 2 with all coefficients from {1, 2}.");

		}
	}

	@Test
	public void randomPolynomial_2() {
		/* Beware: This test is random based. */
		for (int i = 0; i < 100; i++) {
			s("RandomPolynomial[a, 0, 1, 2]", "[12]");
		}
	}

	@Test
	public void randomPolynomial_3() {
		/* Beware: This test is random based. */
		for (int i = 0; i < 100; i++) {
			r("RandomPolynomial[a, 2, 1, 2]",
					"(2\\s\\*\\s)?a\\^\\(2\\)\\s\\+\\s(2\\s\\*\\s)?a\\s\\+\\s[12]",
					" A polynomial in a of degree 2 with all coefficients from {1, 2}.");
		}
	}

	/* Sample */

	@Test
	public void sample_0() {
		/* Beware: This test is random based. */
		for (int i = 0; i < 100; i++) {
			s("Sample[{1, 2, 3, 4, 5}, 5]",
					"{[12345], [12345], [12345], [12345], [12345]}");
		}
	}

	@Test
	public void sample_1() {
		/* Beware: This test is random based. */
		for (int i = 0; i < 100; i++) {
			r("Sample[{-5, 2, a, 7, c}, 3]",
					"\\{(([ac27]|-5),\\s){2}([ac27]|-5)\\}",
					"A list containing three elements out of {a, c, -5, 2, 7}, where elements may be contained several times.");
		}
	}

	@Test
	public void sample_2() {
		/* Beware: This test is random based. */
		for (int i = 0; i < 100; i++) {
			r("Sample[{1, 2, 3, 4, 5}, 5, true]", "\\{([1-5],\\s){4}[1-5]\\}",
					"A list containing five elements out of {1, 2, 3, 4, 5}, where elements may be contained several times.");
		}
	}

	@Test
	public void sample_3() {
		/* Beware: This test is random based. */
		// TODO Check for multiple elements.
		for (int i = 0; i < 100; i++) {
			r("Sample[{{1, 2, 3}, 4, 5, 6, 7, 8}, 3, false]",
					"\\{(([4-8]|\\{1,\\s2,\\s3\\}),\\s){2}([4-8]|\\{1,\\s2,\\s3\\})\\}",
					"A list containing three elements out of {{1, 2, 3}, 4, 5, 6, 7, 8}, where each element may be contained only once.");
		}
	}

	/* Shuffle */

	@Test
	public void shuffle_0() {
		/* Beware: This test is random based. */
		// TODO Check for missing / multiple elements.
		r("Shuffle[{3, 5, 1, 7, 3}]", "\\{([1357],\\s){4}[1357]\\}",
				"An arbitrary permutation of the list {1, 3, 3, 5, 7}.");
	}

	/* Simplify */

	@Test
	public void simplify_0() {
		t("Simplify[3 * x + 4 * x + a * x]", "x * (a + 7)", "a * x + 7 * x", "x * a + 7 * x");
	}

	/* Solutions */

	// TODO Extend.

	@Test
	public void solutions_0() {
		t("Solutions[x^2 = 4 * x]", "{0, 4}");
	}

	@Test
	public void solutions_1() {
		t("Solutions[x * a^2 = 4 * a, a]", "{4 / x, 0}", "{0, 4 / x}");
	}

	@Test
	public void solutions_2() {
		t("Solutions[{x = 4 * x + y , y + x = 2}, {x, y}]", "{{-1, 3}}");
	}

	@Test
	public void solutions_3() {
		t("Solutions[{2 * a^2 + 5 * a + 3 = b, a + b = 3}, {a, b}]",
				"{{0, 3}, {-3, 6}}", "{{-3, 6}, {0, 3}}");
	}

	/* Solve */

	/* One Variable */
	@Test
	public void solve_OneVariable_3() {
		t("Solve[{sin(x)=1,x>0,x<pi}]",
				"{x = 1 / 2 * " + Unicode.PI_STRING + "}");
	}

	/* One Variable, variable Coefficients */

	@Test
	public void solve_OneVariableVC_4() {
		t("Solve[-(10 c + 3) / (2 (4 c^2 - 9)) = -1 / (2 (2 c - 3)), c]",
				"{c = 0}");
	}

	@Test
	public void solve_OneVariableVC_10() {
		t("Solve[0.5 N0 = N0 exp(-0.3 t), t]", "{t = 10 / 3 * log(2)}",
				"{t = (10 * log(2)) / 3}");
	}

	@Test
	public void solve_OneVariableVC_11() {
		t("Solve[x^2 = a]", "{x = sqrt(a), x = -sqrt(a)}",
				"{x = -sqrt(a), x = sqrt(a)}");
	}

	@Test
	public void solve_OneVariableVC_12() {
		t("Solve[x^2 - 2 a x + (a^2 - 1)]", "{x = a - 1, x = a + 1}",
				"{x = a + 1, x = a - 1}");
	}

	/* Trigonometric Problems */

	@Test
	public void solve_Trig_0() {
		// "{x = (4 * k_INDEX * pi - pi) / 4}"
		s("Solve[3 * tan(x) + 3 = 0]", "{x = k_INDEX * " + Unicode.PI_STRING
				+ " - 1 / 4 * " + Unicode.PI_STRING + "}");
		s("Solve[e^(-x/4)*(sin(x)+4*cos(x))]",
				"{x = k_INDEX * " + Unicode.PI_STRING + " - tan"
						+ Unicode.SUPERSCRIPT_MINUS_ONE_STRING + "(4)}");
		// "{x = k_INDEX * " + Unicode.PI_STRING + " - arctan(4)}");
	}

	@Test
	public void solve_Trig_1() {
		// s("Solve[2*cos(x)^2+sqrt(2)*cos(x)-2,x]","{x = (8 * k_INDEX * pi +
		// pi) / 4, x = (8 * k_INDEX * pi - pi) / 4}");
		s("Solve[2 * cos(x)^2 + sqrt(2) * cos(x) - 2, x]",
				"{x = 2 * k_INDEX * " + Unicode.PI_STRING + " - 1 / 4 * "
						+ Unicode.PI_STRING + ", x = 2 * k_INDEX * "
						+ Unicode.PI_STRING + " + 1 / 4 * " + Unicode.PI_STRING
						+ "}");
	}

	/* Several Equations and Variables */

	@Test
	public void solve_Several_0() {
		t("Solve[{x = 4 x + y , y + x = 2}, {x, y}]", "{{x = -1, y = 3}}");
	}

	@Test
	public void solve_Several_1() {
		t("Solve[{2a^2 + 5a + 3 = b, a + b = 3}, {a, b}]",
				"{{a = 0, b = 3}, {a = -3, b = 6}}",
				"{{a = -3, b = 6}, {a = 0, b = 3}}");
	}

	@Test
	public void solve_Several_2() {
		t("Solve[{2a^2 + 5a + 3 = b, a + b = 3, a = b}, {a, b}]", "{}");
	}

	@Test
	public void solve_Several_3() {
		t("Solve[13 = 3 + 5 t + 10 s, {t, s}]", "{{t = - 2 * s + 2, s = s}}");
	}

	@Test
	public void solve_Several_4() {
		t("Solve[{13 = 3 + 5 t + 10 s}, {t, s}]", "{{t = - 2 * s + 2, s = s}}");
	}

	@Test
	public void solve_Several_5() {
		t("Solve[{a + b = 0, c = 0}]", "{{a = -b, b = b}}");
	}

	@Test
	public void solve_Several_6() {
		t("Solve[{a + b = 0, c = 0}, {a, b}]", "{{a = -b, b = b}}");
	}

	@Test
	public void solve_Several_7() {
		t("Solve[{a + b = 0, c = 0}, {a, c}]", "{{a = -b, c = 0}}");
	}

	@Test
	public void solve_Several_8() {
		t("Solve[{a + b = 0, c = 0}, {c, b}]", "{{c = 0, b = -a}}");
	}

	@Test
	public void solve_Several_9() {
		t("Solve[{a + b = 0, c = 0}, {a, b, c}]", "{{a = -b, b = b, c = 0}}");
	}

	@Test
	public void solve_Several_10() {
		t("Solve[{a + b = 0, b = b, c = 0}]", "{{a = -b, b = b, c = 0}}");
	}

	@Test
	public void solve_Several_11() {
		t("Solve[{a + b = 0, b = b, c = 0}, {a, b, c}]",
				"{{a = -b, b = b, c = 0}}");
	}

	@Test
	public void solve_Several_12() {
		t("Solve[{c = 0, a + b = 0, b = b}]", "{{a = -b, b = b, c = 0}}");
	}

	@Test
	public void solve_Several_13() {
		t("Solve[a + b = 0]", "{a = -b}");
	}

	@Test
	public void solve_Several_14() {
		t("Solve[{a + b = 0}]", "{a = -b}");
	}

	@Test
	public void solve_Several_15() {
		t("Solve[{a + b = 0}, {a, b}]", "{{a = -b, b = b}}");
	}

	// solves wrt {a,b}
	@Test
	public void solve_Several_16() {
		t("Solve[{c = 0, a + b = 0}]", "{{a = -b, b = b}}");
	}

	@Test
	public void solve_Several_17() {
		t("Solve[{a + b = 0, c^2 - 1 = 0}]", "{{a = -b, b = b}}");
	}

	@Test
	public void solve_Several_18() {
		t("Solve[{c^2 - 1 = 0, a + b = 0}]", "{{a = -b, b = b}}");
	}

	@Test
	public void solve_Several_19() {
		t("Solve[{c^2 - 1 = 0, a + b = 0}, c]", "{c = -1, c = 1}");
	}

	@Test
	public void solve_Several_20() {
		t("Solve[{c^2 - 1 = 0, a + b = 0}, {c}]", "{c = -1, c = 1}");
	}

	@Test
	public void solve_Several_21() {
		t("Solve[{c^2 - 1 = 0, a + b = 0}, {c, b}]",
				"{{c = 1, b = -a}, {c = -1, b = -a}}");
	}

	@Test
	public void solve_Several_22() {
		t("Solve[8 = 3 + 5 t^2 + 10 s, {t, s}]",
				"{{t = t, s = (-1) / 2 * t^(2) + 1 / 2}}");
	}

	@Test
	public void solve_Several_23() {
		t("Solve[{x = 3 + 5 t, y = 2 + t, x = 8 + 10 s, y = 3 + 2 s}, {x, y, t, s}]",
				"{{x = 10 * s + 8, y = 2 * s + 3, t = 2 * s + 1, s = s}}");
	}

	@Test
	public void solve_Poly_Deg5() {
		t("Solve[(22a^5+135a^3)/125=1.75*1784/125,a]", "{a = 2.31205450048}",
				"{a = 2.312054500480007}");
	}

	/* Parametric Equations One Parameter */

	@Test
	public void solve_ParametricEOP_0() {
		t("Solve[(3, 2) = (3, 2) + t * (5, 1), t]", "{t = 0}");
		t("Solve[(3t+5,2t-3)=(20,7),t]", "{t = 5}");
	}

	@Test
	public void solve_ParametricEOP_1() {
		t("Solve[(3, 2) = (3, 2) + t * (5, 1)]", "{t = 0}");
	}

	@Test
	public void solve_ParametricEOP_2() {
		t("Solve[(3, 2) + t * (5, 1) = (3, 2)]", "{t = 0}");
	}

	@Test
	public void solve_ParametricEOP_3() {
		t("Solve[(-2, 1) = (3, 2) + t * (5, 1)]", "{t = -1}");
	}

	@Test
	public void solve_ParametricEOP_4() {
		t("Solve[(5.5, 2.5) = (3, 2) + t * (5, 1)]", "{t = 1 / 2}");
	}

	@Test
	public void solve_ParametricEOP_5() {
		t("Numeric[Solve[(5.5, 2.5) = (3, 2) + t * (5, 1)]]", "{t = 0.5}");
	}

	@Test
	public void solve_ParametricEOP_6() {
		// Please note that the language is German. "L\u00f6se" is "Solve" in
		// German.
		tk("Solve[(5.5, 2.5) = (3, 2) + t * (5, 1)]",
				GermanSolve + "((5.5, 2.5) = (3, 2) + t * (5, 1))");
	}

	/* Parametric Function One Parameter */

	@Test
	public void solve_ParametricFOP_0() {
		t("f(t) := (3, 2) + t * (5, 1)", "(5 * t + 3, t + 2)");
		t("Solve[f(t) = (8, 3)]", "{t = 1}");
	}

	@Test
	public void solve_ParametricFOP_1() {
		t("f(t) := (3, 2) + t * (5, 1)", "(5 * t + 3, t + 2)");
		t("Solve[(8, 3) = f(t)]", "{t = 1}");
	}

	@Test
	public void solve_ParametricFOP_2() {
		t("f(t) := (3, 2) + t * (5, 1)", "(5 * t + 3, t + 2)");
		t("Solve[f(t) = (5.5, 2.5)]", "{t = 1 / 2}");
	}

	@Test
	public void solve_ParametricFOP_3() {
		t("f(t) := (3, 2) + t * (5, 1)", "(5 * t + 3, t + 2)");
		t("Numeric[Solve[f(t) = (5.5, 2.5)]]", "{t = 0.5}");
	}

	@Test
	public void solve_ParametricFOP_4() {
		t("f(t) := (3, 2) + t * (5, 1)", "(5 * t + 3, t + 2)");
		// Please note that the language is German. "L\u00f6se" is "Solve" in
		// German.
		tk("Solve[f(t) = (5.5, 2.5)]", GermanSolve + "(f(t) = (5.5, 2.5))");
	}

	/* Parametric Equation Multiple Parameters */

	@Test
	public void solve_ParametricEMP_0() {
		t("Solve[(3, 2) = (3, 2) + t * (5, 1) + s * (-1, 7), {s, t}]",
				"{{s = 0, t = 0}}");
	}

	@Test
	public void solve_ParametricEMP_1() {
		t("Solve[(-3, 8) = (3, 2) + t * (5, 1) + s * (-1, 7), {s, t}]",
				"{{s = 1, t = -1}}");
	}

	@Test
	public void solve_ParametricEMP_2() {
		t("Solve[(-3, 8) = (3, 2) + t * (5, 1) + s * (-1, 7), {t, s}]",
				"{{t = -1, s = 1}}");
		t("Solve[{X=(t,t),X=(2s-1,3s+3)}]", "{{s = -4, t = -9}}");
	}

	@Test
	public void solve_ParametricEMP_3() {
		t("Solve[(13, 4) = (3, 2) + t * (5, 1) + s * (10, 2), {s, t}]",
				"{{s = (-1) / 2 * t + 1, t = t}}");
	}

	@Test
	public void solve_ParametricEMP_4() {
		t("Solve[(13, 4) = (3, 2) + t * (5, 1) + s * (10, 2), {t, s}]",
				"{{t = -2 * s + 2, s = s}}");
	}

	@Test
	public void solve_ParametricEMP_5() {
		t("Solve[(13, 4) = (3, 2) + t * (5, 1) + s * (10, 2), s]",
				"{s = (-1) / 2 * t + 1}");
	}

	@Test
	public void solve_ParametricEMP_6() {
		t("Solve[(13, 4) = (3, 2) + t * (5, 1) + s * (10, 2), t]",
				"{t = -2 * s + 2}");
	}

	@Test
	public void solve_ParametricEMP_7() {
		t("Solve[(13, 5) = (3, 2) + t * (5, 1) + s * (10, 2), {s, t}]", "{}");
	}

	/* Parametric Function Multiple Parameters */

	@Test
	public void solve_ParametricFMP_0() {
		t("f(t, s) := (3, 2) + t * (5, 1) + s * (-1, 7)",
				"(-s + 5 * t + 3, 7 * s + t + 2)");
		t("Solve[f(t, s) = (3, 2), {s, t}]", "{{s = 0, t = 0}}");
	}

	@Test
	public void solve_ParametricFMP_1() {
		t("f(t, s) := (3, 2) + t * (5, 1) + s * (-1, 7)",
				"(-s + 5 * t + 3, 7 * s + t + 2)");
		t("Solve[f(t, s) = (-3, 8), {s, t}]", "{{s = 1, t = -1}}");
	}

	@Test
	public void solve_ParametricFMP_2() {
		t("f(t, s) := (3, 2) + t * (5, 1) + s * (-1, 7)",
				"(-s + 5 * t + 3, 7 * s + t + 2)");
		t("Solve[f(t, s) = (-3, 8), {t, s}]", "{{t = -1, s = 1}}");
	}

	@Test
	public void solve_ParametricFMP_3() {
		t("f(t, s) := (3, 2) + t * (5, 1) + s * (-1, 7)",
				"(-s + 5 * t + 3, 7 * s + t + 2)");
		t("Solve[f(s, t) = (-3, 8), {s, t}]", "{{s = -1, t = 1}}");
	}

	@Test
	public void solve_ParametricFMP_4() {
		t("f(t, s) := (3, 2) + t * (5, 1) + s * (10, 2)",
				"(10 * s + 5 * t + 3, 2 * s + t + 2)");
		t("Solve[f(t, s) = (13, 4), {s, t}]",
				"{{s = (-1) / 2 * t + 1, t = t}}");
	}

	@Test
	public void solve_ParametricFMP_5() {
		t("f(t, s) := (3, 2) + t * (5, 1) + s * (10, 2)",
				"(10 * s + 5 * t + 3, 2 * s + t + 2)");
		t("Solve[f(t, s) = (13, 4), {t, s}]", "{{t = -2 * s + 2, s = s}}",
				"{{t = 2 - 2 * s, s = s}}");
	}

	@Test
	public void solve_ParametricFMP_6() {
		t("f(t, s) := (3, 2) + t * (5, 1) + s * (10, 2)",
				"(10 * s + 5 * t + 3, 2 * s + t + 2)");
		t("Solve[f(t, s) = (13, 4), s]", "{s = (-1) / 2 * t + 1}");
	}

	@Test
	public void solve_ParametricFMP_7() {
		t("f(t, s) := (3, 2) + t * (5, 1) + s * (10, 2)",
				"(10 * s + 5 * t + 3, 2 * s + t + 2)");
		t("Solve[f(t, s) = (13, 4), t]", "{t = -2 * s + 2}", "{t = 2 - 2 * s}");
	}

	@Test
	public void solve_ParametricFMP_8() {
		t("f(t, s) := (3, 2) + t * (5, 1) + s * (10, 2)",
				"(10 * s + 5 * t + 3, 2 * s + t + 2)");
		t("Solve[f(t, s) = (13, 5), {s, t}]", "{}");
	}

	@Test
	public void solve_ParametricFMP_9() {
		t("f(t, s) := (3, 2) + t * (5, 1) + s * (-1, 7)",
				"(-s + 5 * t + 3, 7 * s + t + 2)");
		t("Solve[f(t, s) = (7, -8), {t, s}]", "{{t = 1 / 2, s = (-3) / 2}}");
	}

	@Test
	public void solve_ParametricFMP_10() {
		t("f(t, s) := (3, 2) + t * (5, 1) + s * (-1, 7)",
				"(-s + 5 * t + 3, 7 * s + t + 2)");
		t("Numeric[Solve[f(t, s) = (7, -8), {t, s}]]", "{{t = 0.5, s = -1.5}}");
	}

	@Test
	public void solve_ParametricFMP_11() {
		t("f(t, s) := (3, 2) + t * (5, 1) + s * (-1, 7)",
				"(-s + 5 * t + 3, 7 * s + t + 2)");
		// Please note that the language is German. "L\u00f6se" is "Solve" in
		// German.
		tk("Solve[f(t, s) = (7, -8), {t, s}]",
				GermanSolve + "(f(t, s) = (7, -8), {t, s})");
	}

	/* Parametric Equations Twosided */

	@Test
	public void solve_ParametricET_0() {
		t("Solve[(3, 2) + t (5, 1) = (4, 1) + s (1, -1), {t, s}]",
				"{{t = 0, s = -1}}");
	}

	@Test
	public void solve_ParametricET_1() {
		t("Solve[(3, 2) + t (5, 1) = (8, 3) + s (10, 2), {t, s}]",
				"{{t = 2 * s + 1, s = s}}");
	}

	@Test
	public void solve_ParametricET_2() {
		t("Solve[(3, 2) + t (5, 1) = (4, 1) + s (10, 2), {t, s}]", "{}");
	}

	@Test
	public void solve_ParametricET_3() {
		t("Solve[(3, 2) + t (5, 1) = (4, 1) + s (2, -2), {t, s}]",
				"{{t = 0, s = (-1) / 2}}");
	}

	@Test
	public void solve_ParametricET_4() {
		t("Numeric[Solve[(3, 2) + t (5, 1) = (4, 1) + s (2, -2), {t, s}]]",
				"{{t = 0, s = -0.5}}");
	}

	@Test
	public void solve_ParametricET_5() {
		// Please note that the language is German. "L\u00f6se" is "Solve" in
		// German.
		tk("Solve[(3, 2) + t (5, 1) = (4, 1) + s (2, -2), {t, s}]", GermanSolve
				+ "((3, 2) + t * (5, 1) = (4, 1) + s * (2, -2), {t, s})");
	}

	/* Multiple Parametric Equations Eloquent */

	@Test
	public void solve_ParametricMEE_0() {
		t("Solve[{(x, y) = (3, 2) + t (5, 1), (x, y) = (4, 1) + s (1, -1)}, {x, y, t, s}]",
				"{{x = 3, y = 2, t = 0, s = -1}}");
	}

	@Test
	public void solve_ParametricMEE_1() {
		t("Solve[{(x, y) = (3, 2) + t (5, 1), (x, y) = (8, 3) + s (10, 2)}, {x, y, t, s}]",
				"{{x = 10 * s + 8, y = 2 * s + 3, t = 2 * s + 1, s = s}}");
	}

	@Test
	public void solve_ParametricMEE_2() {
		t("Solve[{(x, y) = (3, 2) + t (5, 1), (x, y) = (4, 1) + s (10, 2)}, {x, y, t, s}]",
				"{}");
	}

	@Test
	public void solve_ParametricMEE_3() {
		t("Solve[{(x, y) = (3, 2) + t (5, 1), (x, y) = (4, 1) + s (2, -2)}, {x, y, t, s}]",
				"{{x = 3, y = 2, t = 0, s = (-1) / 2}}");
	}

	@Test
	public void solve_ParametricMEE_4() {
		t("Numeric[Solve[{(x, y) = (3, 2) + t (5, 1), (x, y) = (4, 1) + s (2, -2)}, {x, y, t, s}]]",
				"{{x = 3, y = 2, t = 0, s = -0.5}}");
	}

	@Test
	public void solve_ParametricMEE_5() {
		// Please note that the language is German. "L\u00f6se" is "Solve" in
		// German.
		tk("Solve[{(x, y) = (3, 2) + t (5, 1), (x, y) = (4, 1) + s (2, -2)}, {x, y, t, s}]",
				GermanSolve
						+ "({(x, y) = (3, 2) + t * (5, 1), (x, y) = (4, 1) + s * (2, -2)}, {x, y, t, s})");
	}

	/* Multiple Parametric Equations Abbreviation */

	@Test
	public void solve_ParametricMEA_0() {
		t("Solve[{X = (3, 2) + t (5, 1), X = (4, 1) + s (1, -1)}, {t, s}]",
				"{{t = 0, s = -1}}");
	}

	@Test
	public void solve_ParametricMEA_1() {
		t("Solve[{X = (3, 2) + t (5, 1), X = (8, 3) + s (10, 2)}, {t, s}]",
				"{{t = 2 * s + 1, s = s}}");
	}

	@Test
	public void solve_ParametricMEA_2() {
		t("Solve[{X = (3, 2) + t (5, 1), X = (4, 1) + s (10, 2)}, {t, s}]",
				"{}");
	}

	@Test
	public void solve_ParametricMEA_3() {
		t("Solve[{X = (3, 2) + t (5, 1), X = (4, 1) + s (2, -2)}, {t, s}]",
				"{{t = 0, s = (-1) / 2}}", "{{t = 0, s = -1 / 2}}");
	}

	@Test
	public void solve_ParametricMEA_4() {
		t("Numeric[Solve[{X = (3, 2) + t (5, 1), X = (4, 1) + s (2, -2)}, {t, s}]]",
				"{{t = 0, s = -0.5}}");
	}

	@Test
	public void solve_ParametricMEA_5() {
		// Please note that the language is German. "L\u00f6se" is "Solve" in
		// German.
		tk("Solve[{X = (3, 2) + t (5, 1), X = (4, 1) + s (2, -2)}, {t, s}]",
				GermanSolve
						+ "({X = (3, 2) + t * (5, 1), X = (4, 1) + s * (2, -2)}, {t, s})");
	}

	/* Multiple Parametric Equations Labeled */

	@Test
	public void solve_ParametricMEL_0() {
		t("f: (x, y) = (3, 2) + t (5, 1)", "(x, y) = (5 * t + 3, t + 2)");
		t("g: (x, y) = (4, 1) + s (2, -2)", "(x, y) = (2 * s + 4, -2 * s + 1)");
		t("Solve[{f, g}, {t, s, x, y}]",
				"{{t = 0, s = (-1) / 2, x = 3, y = 2}}");
	}

	@Test
	public void solve_ParametricMEL_1() {
		t("f: X = (3, 2) + t (5, 1)", "X = (5 * t + 3, t + 2)");
		t("g: X = (4, 1) + s (2, -2)", "X = (2 * s + 4, -2 * s + 1)");
		t("Solve[{f, g}, {t, s}]", "{{t = 0, s = (-1) / 2}}");
	}

	@Test
	public void solve_ParametricMEL_2() {
		t("f: (x, y) = (3, 2) + t (5, 1)", "(x, y) = (5 * t + 3, t + 2)");
		t("g: (x, y) = (4, 1) + s (2, -2)", "(x, y) = (2 * s + 4, -2 * s + 1)");
		t("Numeric[Solve[{f, g}, {t, s, x, y}]]",
				"{{t = 0, s = -0.5, x = 3, y = 2}}");
	}

	@Test
	public void solve_ParametricMEL_3() {
		t("f: X = (3, 2) + t (5, 1)", "X = (5 * t + 3, t + 2)");
		t("g: X = (4, 1) + s (2, -2)", "X = (2 * s + 4, -2 * s + 1)");
		t("Numeric[Solve[{f, g}, {t, s}]]", "{{t = 0, s = -0.5}}");
	}

	@Test
	public void solve_ParametricMEL_4() {
		t("f: (x, y) = (3, 2) + t (5, 1)", "(x, y) = (5 * t + 3, t + 2)");
		t("g: (x, y) = (4, 1) + s (2, -2)", "(x, y) = (2 * s + 4, -2 * s + 1)");
		// Please note that the language is German. "L\u00f6se" is "Solve" in
		// German.
		tk("Solve[{f, g}, {t, s, x, y}]",
				GermanSolve + "({f, g}, {t, s, x, y})");
	}

	@Test
	public void solve_ParametricMEL_5() {
		t("f: X = (3, 2) + t (5, 1)", "X = (5 * t + 3, t + 2)");
		t("g: X = (4, 1) + s (2, -2)", "X = (2 * s + 4, -2 * s + 1)");
		// Please note that the language is German. "L\u00f6se" is "Solve" in
		// German.
		tk("Solve[{f, g}, {t, s}]", GermanSolve + "({f, g}, {t, s})");
	}

	/* Multiple Parametric Functions */

	@Test
	public void solve_ParametricMF_0() {
		t("f(t) := (3, 2) + t (5, 1)", "(5 * t + 3, t + 2)");
		t("g(s) := (4, 1) + s (1, -1)", "(s + 4, -s + 1)");
		t("Solve[f(u) = g(v), {u, v}]", "{{u = 0, v = -1}}");
	}

	@Test
	public void solve_ParametricMF_1() {
		t("f(t) := (3, 2) + t (5, 1)", "(5 * t + 3, t + 2)");
		t("g(s) := (8, 3) + s (10, 2)", "(10 * s + 8, 2 * s + 3)");
		t("Solve[f(u) = g(v), {u, v}]", "{{u = 2 * v + 1, v = v}}");
	}

	@Test
	public void solve_ParametricMF_2() {
		t("f(t) := (3, 2) + t (5, 1)", "(5 * t + 3, t + 2)");
		t("g(s) := (8, 3) + s (10, 2)", "(10 * s + 8, 2 * s + 3)");
		t("Solve[f(u) = g(v), {t, s}]", "{}");
	}

	@Test
	public void solve_ParametricMF_3() {
		t("f(t) := (3, 2) + t (5, 1)", "(5 * t + 3, t + 2)");
		t("g(s) := (4, 1) + s (2, -2)", "(2 * s + 4, -2 * s + 1)");
		t("Solve[f(u) = g(v), {u, v}]", "{{u = 0, v = (-1) / 2}}");
	}

	@Test
	public void solve_ParametricMF_4() {
		t("f(t) := (3, 2) + t (5, 1)", "(5 * t + 3, t + 2)");
		t("g(s) := (4, 1) + s (2, -2)", "(2 * s + 4, -2 * s + 1)");
		t("Numeric[Solve[f(u) = g(v), {u, v}]]", "{{u = 0, v = -0.5}}");
	}

	@Test
	public void solve_ParametricMF_5() {
		t("f(t) := (3, 2) + t (5, 1)", "(5 * t + 3, t + 2)");
		t("g(s) := (4, 1) + s (2, -2)", "(2 * s + 4, -2 * s + 1)");
		// Please note that the language is German. "L\u00f6se" is "Solve" in
		// German.
		tk("Solve[f(u) = g(v), {u, v}]", GermanSolve + "(f(u) = g(v), {u, v})");
	}

	@Test
	public void solve_ParametricMF_6() {
		t("f(t) := (1, 2) + t * (2, 8)", "(2 * t + 1, 8 * t + 2)");
		t("g(t) := (1, 1) + t * (2, 1)", "(2 * t + 1, t + 1)");
		t("Solve[f(t) = g(s), {t, s}]", "{{t = (-1) / 7, s = (-1) / 7}}");
	}

	/* Parametrics Three Dimensions */

	@Test
	public void solve_ParametricTD_0() {
		t("Solve[(2, 3, -1) = (3, 1, 2) + t (-2, 4, -6), t]", "{t = 1 / 2}");
	}

	@Test
	public void solve_ParametricTD_1() {
		t("Numeric[Solve[(2, 3, -1) = (3, 1, 2) + t (-2, 4, -6), t]]",
				"{t = 0.5}");
	}

	@Test
	public void solve_ParametricTD_2() {
		// Please note that the language is German. "L\u00f6se" is "Solve" in
		// German.
		tk("Solve[(2, 3, -1) = (3, 1, 2) + t (-2, 4, -6), t]",
				GermanSolve + "((2, 3, -1) = (3, 1, 2) + t * (-2, 4, -6), t)");
	}

	@Test
	public void solve_ParametricTD_3() {
		t("f(t) := (3, 1, 2) + t (-2, 4, -6)",
				"(3 - 2 * t, 1 + 4 * t, 2 - 6 * t)",
				"(-2 * t + 3, 4 * t + 1, -6 * t + 2)");
		t("Solve[f(t) = (2, 3, -1), t]", "{t = 1 / 2}");
	}

	@Test
	public void solve_ParametricTD_4() {
		t("f(t) := (3, 1, 2) + t (-2, 4, -6)",
				"(3 - 2 * t, 1 + 4 * t, 2 - 6 * t)",
				"(-2 * t + 3, 4 * t + 1, -6 * t + 2)");
		t("Numeric[Solve[f(t) = (2, 3, -1), t]]", "{t = 0.5}");
	}

	@Test
	public void solve_ParametricTD_5() {
		t("f(t) := (3, 1, 2) + t (-2, 4, -6)",
				"(3 - 2 * t, 1 + 4 * t, 2 - 6 * t)",
				"(-2 * t + 3, 4 * t + 1, -6 * t + 2)");
		// Please note that the language is German. "L\u00f6se" is "Solve" in
		// German.
		tk("Solve[f(t) = (2, 3, -1), t]",
				GermanSolve + "(f(t) = (2, 3, -1), t)");
	}

	@Test
	public void solve_ParametricTD_6() {
		t("Solve[{(x, y, z) = (3, 1, 2) + t (-2, 4, -6), (x, y, z) = (3, 7, -4) + s (1, 4, -3)}, {x, y, z, t, s}]",
				"{{x = 2, y = 3, z = -1, t = 1 / 2, s = -1}}");
	}

	@Test
	public void solve_ParametricTD_7() {
		t("Numeric[Solve[{(x, y, z) = (3, 1, 2) + t (-2, 4, -6), (x, y, z) = (3, 7, -4) + s (1, 4, -3)}, {x, y, z, t, s}]]",
				"{{x = 2, y = 3, z = -1, t = 0.5, s = -1}}");
	}

	@Test
	public void solve_ParametricTD_8() {
		// Please note that the language is German. "L\u00f6se" is "Solve" in
		// German.
		tk("Solve[{(x, y, z) = (3, 1, 2) + t (-2, 4, -6), (x, y, z) = (3, 7, -4) + s (1, 4, -3)}, {x, y, z, t, s}]",
				GermanSolve
						+ "({(x, y, z) = (3, 1, 2) + t * (-2, 4, -6), (x, y, z) = (3, 7, -4) + s * (1, 4, -3)}, {x, y, z, t, s})");
	}

	@Test
	public void solve_ParametricTD_9() {
		t("Solve[{X = (3, 1, 2) + t (-2, 4, -6), X = (3, 7, -4) + s (1, 4, -3)}, {t, s}]",
				"{{t = 1 / 2, s = -1}}");
	}

	@Test
	public void solve_ParametricTD_10() {
		t("Numeric[Solve[{X = (3, 1, 2) + t (-2, 4, -6), X = (3, 7, -4) + s (1, 4, -3)}, {t, s}]]",
				"{{t = 0.5, s = -1}}");
	}

	@Test
	public void solve_ParametricTD_11() {
		// Please note that the language is German. "L\u00f6se" is "Solve" in
		// German.
		tk("Solve[{X = (3, 1, 2) + t (-2, 4, -6), X = (3, 7, -4) + s (1, 4, -3)}, {t, s}]",
				GermanSolve
						+ "({X = (3, 1, 2) + t * (-2, 4, -6), X = (3, 7, -4) + s * (1, 4, -3)}, {t, s})");
	}

	@Test
	public void solve_ParametricTD_12() {
		t("f(t) := (3, 1, 2) + t (-2, 4, -6)",
				// "(-2 * t + 3, 4 * t + 1, -6 * t + 2)",
				"(3 - 2 * t, 1 + 4 * t, 2 - 6 * t)");
		t("g(t) := (3, 7, -4) + t (1, 4, -3)",
				"(t + 3, 4 * t + 7, -3 * t - 4)");
		t("Solve[f(u) = g(v), {u, v}]", "{{u = 1 / 2, v = -1}}");
	}

	@Test
	public void solve_ParametricTD_13() {
		t("f(t) := (3, 1, 2) + t (-2, 4, -6)",
				"(3 - 2 * t, 1 + 4 * t, 2 - 6 * t)",
				"(-2 * t + 3, 4 * t + 1, -6 * t + 2)");
		t("g(t) := (3, 7, -4) + t (1, 4, -3)",
				"(t + 3, 4 * t + 7, -3 * t - 4)");
		t("Numeric[Solve[f(u) = g(v), {u, v}]]", "{{u = 0.5, v = -1}}");
	}

	@Test
	public void solve_ParametricTD_14() {
		t("f(t) := (3, 1, 2) + t (-2, 4, -6)",
				"(3 - 2 * t, 1 + 4 * t, 2 - 6 * t)",
				"(-2 * t + 3, 4 * t + 1, -6 * t + 2)");
		t("g(t) := (3, 7, -4) + t (1, 4, -3)",
				"(t + 3, 4 * t + 7, -3 * t - 4)");
		// Please note that the language is German. "L\u00f6se" is "Solve" in
		// German.
		tk("Solve[f(u) = g(v), {u, v}]", GermanSolve + "(f(u) = g(v), {u, v})");
	}

	@Test
	public void solve_ParametricTD_15() {
		t("f(t, s) := (3, 1, 2) + t (-2, 4, -6) + s *(1, 0, 0)",
				"(3 - 2 * t + s, 1 + 4 * t, 2 - 6 * t)",
				"(s - 2 * t + 3, 4 * t + 1, -6 * t + 2)");
		t("g(t) := (4, 7, -4) + t (1, 4, -3)",
				"(t + 4, 4 * t + 7, -3 * t - 4)");
		t("Solve[f(u, v) = g(w), {u, v, w}]", "{{u = 1 / 2, v = 1, w = -1}}");
	}

	@Test
	public void solve_ParametricTD_16() {
		t("f(t, s) := (3, 1, 2) + t (-2, 4, -6) + s *(1, 0, 0)",
				"(3 - 2 * t + s, 1 + 4 * t, 2 - 6 * t)",
				"(s - 2 * t + 3, 4 * t + 1, -6 * t + 2)");
		t("g(t) := (4, 7, -4) + t (1, 4, -3)",
				"(t + 4, 4 * t + 7, -3 * t - 4)");
		t("Numeric[Solve[f(u, v) = g(w), {u, v, w}]]",
				"{{u = 0.5, v = 1, w = -1}}");
	}

	@Test
	public void solve_ParametricTD_17() {
		t("f(t, s) := (3, 1, 2) + t (-2, 4, -6) + s *(1, 0, 0)",
				"(3 - 2 * t + s, 1 + 4 * t, 2 - 6 * t)",
				"(s - 2 * t + 3, 4 * t + 1, -6 * t + 2)");
		t("g(t) := (4, 7, -4) + t (1, 4, -3)",
				"(t + 4, 4 * t + 7, -3 * t - 4)");
		// Please note that the language is German. "L\u00f6se" is "Solve" in
		// German.
		tk("Solve[f(u, v) = g(w), {u, v, w}]",
				GermanSolve + "(f(u, v) = g(w), {u, v, w})");
	}

	/* SolveODE */

	@Test
	public void substitute_5() {
		// Substitute with Keep Input should substitute without evaluation.
		tk("Substitute[1 + 2 + x + 3, {x=7}]", "1 + 2 + 7 + 3");
	}
	/* Tangent */

	/* Point on the Conic */

	@Test
	public void tangent_PointOnConic_0() {
		t("c := Ellipse[(1, 1), (3, 2), (2, 3)]",
				"8 * sqrt(10) * x^(2) - 32 * sqrt(10) * x + 8 * sqrt(10) * y^(2) - 24 * sqrt(10) * y + 32 * sqrt(10) + 12 * x^(2) - 16 * x * y - 24 * x + 24 * y^(2) - 40 * y = 0",
				"8 * x^(2) * sqrt(10) + 12 * x^(2) - 32 * x * sqrt(10) - 16 * x * y - 24 * x + 8 * sqrt(10) * y^(2) - 24 * sqrt(10) * y + 32 * sqrt(10) + 24 * y^(2) - 40 * y = 0");
		t("P := (2, 0)", "(2, 0)");
		t("Tangent[P, c]", "{y = -2 * sqrt(10) + 6 + (sqrt(10) - 3) * x}",
				"{y = (sqrt(10) - 3) x - 2 * sqrt(10) + 6}");
	}

	@Test
	public void tangent_PointOnConic_1() {
		t("c := Ellipse[(1, 1), (3, 2), (2, 3)]",
				"8 * sqrt(10) * x^(2) - 32 * sqrt(10) * x + 8 * sqrt(10) * y^(2) - 24 * sqrt(10) * y + 32 * sqrt(10) + 12 * x^(2) - 16 * x * y - 24 * x + 24 * y^(2) - 40 * y = 0",
				"8 * x^(2) * sqrt(10) + 12 * x^(2) - 32 * x * sqrt(10) - 16 * x * y - 24 * x + 8 * sqrt(10) * y^(2) - 24 * sqrt(10) * y + 32 * sqrt(10) + 24 * y^(2) - 40 * y = 0");
		t("P := (2, 3)", "(2, 3)");
		t("Tangent[P, c]", "{y = -2 * sqrt(10) + 9 + (sqrt(10) - 3) * x}",
				"{y = (sqrt(10) - 3) x - 2 * sqrt(10) + 9}");
	}

	@Test
	public void tangent_PointOnConic_2() {
		// Tangent through the very same point (same object) we used to create
		// the ellipse.
		t("A := (1, 1)", "(1, 1)");
		t("B := (3, 2)", "(3, 2)");
		t("C := (2, 3)", "(2, 3)");
		t("c := Ellipse[A, B, C]",
				"8 * sqrt(10) * x^(2) - 32 * sqrt(10) * x + 8 * sqrt(10) * y^(2) - 24 * sqrt(10) * y + 32 * sqrt(10) + 12 * x^(2) - 16 * x * y - 24 * x + 24 * y^(2) - 40 * y = 0",
				"8 * x^(2) * sqrt(10) + 12 * x^(2) - 32 * x * sqrt(10) - 16 * x * y - 24 * x + 8 * sqrt(10) * y^(2) - 24 * sqrt(10) * y + 32 * sqrt(10) + 24 * y^(2) - 40 * y = 0");
		t("Tangent[C, c]", "{y = -2 * sqrt(10) + 9 + (sqrt(10) - 3) * x}",
				"{y = (sqrt(10) - 3) x - 2 * sqrt(10) + 9}");
	}

	// TODO Add tests for other conics.

	/* Point not on the Conic */

	@Test
	public void tangent_PointOffConic_0() {
		t("c := Ellipse[(1, 1), (3, 2), (2, 3)]",
				"8 * sqrt(10) * x^(2) - 32 * sqrt(10) * x + 8 * sqrt(10) * y^(2) - 24 * sqrt(10) * y + 32 * sqrt(10) + 12 * x^(2) - 16 * x * y - 24 * x + 24 * y^(2) - 40 * y = 0",
				"8 * x^(2) * sqrt(10) + 12 * x^(2) - 32 * x * sqrt(10) - 16 * x * y - 24 * x + 8 * sqrt(10) * y^(2) - 24 * sqrt(10) * y + 32 * sqrt(10) + 24 * y^(2) - 40 * y = 0");
		t("P := (0, (-3 * sqrt(10) * sqrt(224 * sqrt(10) + 687) * sqrt(31) + 672 * sqrt(10) - 11 * sqrt(224 * sqrt(10) + 687) * sqrt(31) + 2061) / (448 * sqrt(10) + 1374))",
				"(0, (-sqrt(2 * sqrt(10) + 3) + 3) / 2)");
		t("Tangent[P, c]",
				"{y = (-sqrt(2 * sqrt(10) + 3) + 3) / 2, y = 5.558213948645 * x - 0.02680674287356}",
				"{y = (-sqrt(2 * sqrt(10) + 3) + 3) / 2, y = 5.558213948644 * x - 0.02680674287311}",
				"{y = (-sqrt(2 * sqrt(10) + 3) + 3) / 2, y = 5.558213948645 * x - 0.02680674287353}",
				"{y = (-sqrt(2 * sqrt(10) + 3) + 3) / 2, y = 5.558213948644 * x - 0.02680674287309}");
	}

	@Test
	public void tangent_1254() {
		t("Tangent[(0.2, 10), sqrt(1 - x^2)]",
				"y = (-sqrt(6)) / 12 * x + 5 * sqrt(6) / 12",
				"y = (-1) / 5 * sqrt(24 / 25)^(-1) * (x - 1 / 5) + sqrt(24 / 25)",
				"y = (((-sqrt(6))) / 12 * x) + (5 * sqrt(6) / 12)");
	}

	@Test
	public void tangent_1255() {
		t("Tangent[0.2, sqrt(1 - x^2)]",
				"y = (-sqrt(6)) / 12 * x + 5 * sqrt(6) / 12",
				"y = (-1) / 5 * sqrt(24 / 25)^(-1) * (x - 1 / 5) + sqrt(24 / 25)",
				"y = (((-sqrt(6))) / 12 * x) + (5 * sqrt(6) / 12)");
	}

	@Test
	public void tangent_1256() {
		t("Tangent[a, sqrt(1 - x^2)]",
				"y = (a * sqrt(-a^(2) + 1) * x - sqrt(-a^(2) + 1)) / (a^(2) - 1)",
				"y = -a * sqrt(1 - a^(2))^(-1) * (x - a) + sqrt(1 - a^(2))",
				"y = (((a * sqrt((-a^(2)) + 1)) * x) - sqrt((-a^(2)) + 1)) / (a^(2) - 1)");
	}

	@Test
	public void tangent_1257() {
		t("Tangent[(1 / sqrt(2), 1 / sqrt(2)), x^2 + y^2 = 1]",
				"{y = -x + sqrt(2)}");
	}

	@Test
	public void tangent_1258() {
		t("Tangent[(1, 0), x^2 + y^2 = 1]", "{x = 1}");
	}

	@Test
	public void tangent_1259() {
		t("Tangent[(1, 0), x^3 + y^3 = 1]", "{x = 1}");
	}

	@Test
	public void tangent_1260() {
		t("Tangent[(1, 1), x^3 + y^3 = 1]", "{x = 1, y = 1}", "{y = 1, x = 1}");
	}

	@Test
	public void tangent_1261() {
		t("Tangent[(a, sqrt(1 - a^2)), x^2 + y^2 = 1]",
				"{y = a * sqrt(-a^(2) + 1) / (a^(2) - 1) * x - sqrt(-a^(2) + 1) / (a^(2) - 1)}");
	}

	@Test
	public void tangent_1262() {
		t("Tangent[(a, cbrt(1 - a^3)), x^3 + y^3 = 1]",
				"{y = a^(2) * cbrt(-a^(3) + 1) / (a^(3) - 1) * x + (-a^(3) + 1) * cbrt(-a^(3) + 1) / (a^(6) - 2 * a^(3) + 1)}",
				"{y = (a^(2) * (-a^(3) + 1)^(1/3) / (a^(3) - 1) * x + (-a^(3) + 1) * (-a^(3) + 1)^(1/3) / (a^(6) - 2 * a^(3) + 1))}");
	}

	@Test
	public void tangent_1263() {
		t("Tangent[(0, 0) ,x^2 - y^3 + 2y^2 - y = 0]", "{y = 0}");
	}

	@Test
	public void tangent_1264() {
		// singular point (two tangents) so ? is correct
		t("Tangent[(0, 1), x^2 - y^3 + 2y^2 - y = 0]", "?");
	}

	/* TaylorPolynomial (alias TaylorSeries) */

	/* Variable not specified */
	private static void propagate(Throwable t) {
		throw new RuntimeException(t);
	}

	/* UnitPerpendicularVector (alias UnitOrthogonalVector) */

	/* Adding Vectors */

	@Test
	public void vectors_AddingVectors_0() {
		t("(1, 2) + (3, 4)", "(4, 6)");
	}

	@Test
	public void vectors_AddingVectors_1() {
		t("(1, 2) + {3, 3}", "(4, 5)");
	}

	@Test
	public void vectors_AddingVectors_2() {
		t("{a, b} + (3, 2)", "(a + 3, b + 2)");
	}

	/* Subtracting Vectors */

	@Test
	public void vectors_SubtractingVectors_0() {
		t("(1, 2) - (3, 4)", "(-2, -2)");
	}

	@Test
	public void vectors_SubtractingVectors_1() {
		t("(1, 2) - {3, 3}", "(-2, -1)");
	}

	@Test
	public void vectors_SubtractingVectors_2() {
		t("{a, b} - (3, 2)", "(a - 3, b - 2)");
	}

	/* Multiplying Vectors: Scalar Product */

	@Test
	public void vectors_ScalarProduct_0() {
		t("(1, 2) * (3, 4)", "11");
	}

	@Test
	public void vectors_ScalarProduct_1() {
		t("{1, 2} * (3, 4)", "11");
	}

	@Test
	public void vectors_ScalarProduct_2() {
		t("(1, 2) * {3, 4}", "11");
	}

	@Test
	public void vectors_ScalarProduct_3() {
		t("(x, 2)*{3,4}", "3*x+8");
	}

	/* Multiplying Vectors: Matrices */

	@Test
	public void vectors_MatrixTimesVector_0() {
		t("{{a, b}, {c, d}} * (x, y)", "(a * x + b * y, c * x + d * y)");
	}

	@Test
	public void vectors_MatrixTimesVector_1() {
		t("{{1, 2}, {2, 1}} * (3, 4)", "(11, 10)");
	}

	@Test
	public void vectors_MatrixTimesVector_2() {
		t("(a, b) * {{1, 2}, {3, 4}}", "(a + 3 * b, 2 * a + 4 * b)");
	}

	/* Ticket */

	/*
	 * Ticket 697: Solve[ {x -1 = 1, y+x = 3}, {x, y}] puts answer in () not {}
	 */

	// TODO Add test! Forget about the existing one.

	@Test
	public void ticket_Ticket697_0() {
		tk("f(x) := x^2", "x^(2)");
		t("f'(x)", "2 * x");
	}

	/* Ticket 801: Numeric factorization */

	@Test
	public void ticket_Ticket801_0() {
		t("Factor(x^2 - 2)", "x^(2) - 2");
	}

	/* Ticket 1477: e becomes E in CAS */

	/* Ticket 2481: NSolves gives wrong solution */

	@Test
	public void ticket_Ticket2481_0() {
		t("Numeric[NSolve[13^(x+1)-2*13^x=1/5*5^x,x], 11]",
				"{x = -4.1939143755}");

		/*
		 * Autotest and offline evaluation results differ in this test case.
		 * 
		 * Since minor differences are to be expected with numeric algorithms,
		 * this is not considered an error. Therefore the number of significant
		 * figures of this test has been decreased artificially.
		 * 
		 * The original test was t("[NSolve[13^(x+1)-2*13^x=1/5*5^x,x]",
		 * "{x = -4.193914375465535}");. The result from autotest was {x =
		 * -4.193914375476052}. The exact result is {x = (-ln(55)) / (ln(13) -
		 * ln(5))}.
		 */
	}

	/* Ticket 2651: Function not Evaluated */

	@Test
	public void ticket_Ticket2651_0() {
		t("f(x) := FitPoly[{(-1, -1), (0, 1), (1, 1), (2, 5)}, 3]",
				"x^(3) - x^(2) + 1");
		t("f(-1)", "-1");
	}

	/* Ticket 3370: Integral symbol for integrals in CAS */

	@Test
	public void ticket_Ticket3370_0() {
		GeoCasCell f = new GeoCasCell(kernel.getConstruction());
		kernel.getConstruction().addToConstructionList(f, false);
		f.setInput("Integral[x^2, x, 1, 2]");
		f.setEvalCommand("Keepinput");
		f.computeOutput();

		Assert.assertEquals(
				"\\mathbf{\\int\\limits_{1}^{2}x^{2}\\,\\mathrm{d}x}",
				f.getLaTeXOutput());
	}

	@Test
	public void ticket_Ticket3370_1() {
		GeoCasCell f = new GeoCasCell(kernel.getConstruction());
		kernel.getConstruction().addToConstructionList(f, false);
		f.setInput("Integral[f(y), y, somevar, g(h)]");
		f.setEvalCommand("Keepinput");
		f.computeOutput();

		Assert.assertEquals(
				"\\mathbf{\\int\\limits_{somevar}^{g\\left(h \\right)}f\\left(y \\right)\\,\\mathrm{d}y}",
				f.getLaTeXOutput());
	}

	@Test
	public void ticket_Ticket3370_2() {
		GeoCasCell f = new GeoCasCell(kernel.getConstruction());
		kernel.getConstruction().addToConstructionList(f, false);
		f.setInput("Sum[x^2, x, 1, 2]");
		f.setEvalCommand("Keepinput");
		f.computeOutput();

		Assert.assertEquals("\\mathbf{\\sum_{x=1}^{2}x^{2}}",
				f.getLaTeXOutput());
	}

	@Test
	public void ticket_Ticket3370_3() {
		GeoCasCell f = new GeoCasCell(kernel.getConstruction());
		kernel.getConstruction().addToConstructionList(f, false);
		f.setInput("Sum[f(y), y, somevar, g(h)]");
		f.setEvalCommand("Keepinput");
		f.computeOutput();

		Assert.assertEquals(
				"\\mathbf{\\sum_{y=somevar}^{g\\left(h \\right)}f\\left(y \\right)}",
				f.getLaTeXOutput());
	}

	/* Ticket 3377: Expand Improvements */

	@Test
	public void ticket_Ticket3377_0() {
		t("Expand[sqrt(3) * sqrt(3 + x - 1)]", "sqrt(3) * sqrt(x + 2)");
	}

	@Test
	public void ticket_Ticket3377_1() {
		t("Expand[a / c + b / d]", "(a * d + b * c) / (c * d)");
	}

	@Test
	public void ticket_Ticket3377_2() {
		t("Factor[a / c + b / d]", "(a * d + c * b) / (d * c)",
				"(a * d + b * c) / (c * d)");
	}

	/* Ticket 3381: Problem with Solve and exponential function */

	@Test
	public void ticket_Ticket3381_0() {
		t("Solve[{a = 2, 12 * sqrt(3) * a * b^2 * exp(-3 * b) - 6 * sqrt(3) * a * b *exp(-3 * b) = 0}, {a, b}]",
				"{{a = 2, b = 0}, {a = 2, b = 1 / 2}}");
	}

	/* Ticket 3385: Intersection and Union in CAS */

	@Test
	public void ticket_Ticket3385_3() {
		t("a " + ExpressionNodeConstants.strIS_ELEMENT_OF + "{a, b, c}",
				"true");
	}

	@Test
	public void ticket_Ticket3385_4() {
		t("d " + ExpressionNodeConstants.strIS_ELEMENT_OF + " {a, b, c}",
				"false");
	}

	@Test
	public void ticket_Ticket3385_5() {
		t("{} " + ExpressionNodeConstants.strIS_SUBSET_OF + " {}", "true");
	}

	@Test
	public void ticket_Ticket3385_6() {
		t("{a, b} " + ExpressionNodeConstants.strIS_SUBSET_OF + "{a, b, c}",
				"true");
	}

	@Test
	public void ticket_Ticket3385_7() {
		t("{a, b, c} " + ExpressionNodeConstants.strIS_SUBSET_OF + " {a, b, c}",
				"true");
	}

	@Test
	public void ticket_Ticket3385_8() {
		t("{a, b, c} " + ExpressionNodeConstants.strIS_SUBSET_OF + " {a, b}",
				"false");
	}

	@Test
	public void ticket_Ticket3385_9() {
		t("{} " + ExpressionNodeConstants.strIS_SUBSET_OF_STRICT + " {}",
				"false");
	}

	@Test
	public void ticket_Ticket3385_10() {
		t("{a, b} " + ExpressionNodeConstants.strIS_SUBSET_OF_STRICT
				+ " {a, b, c}", "true");
	}

	@Test
	public void ticket_Ticket3385_11() {
		t("{a, b, c} " + ExpressionNodeConstants.strIS_SUBSET_OF_STRICT
				+ " {a, b, c}", "false");
	}

	/* Ticket 3524: Solve fails for large numbers and definition as function */

	// TODO What is the correct result for f'(x) = 0 and g(x) = 0 (should be
	// identical)?

	/* Ticket 3525: Simplification improvements in Giac */
	/*
	 * @Test public void ticket_Ticket3525_0 () { t(
	 * "c := Ellipse[(1, 1), (3, 2), (2, 3)]",
	 * "(8 * sqrt(10) + 12) * x^(2) - 16 * x * y - (32 * sqrt(10) + 24) * x + (8 * sqrt(10) + 24) * y^(2) - (24 * sqrt(10) + 40) * y + 32 * sqrt(10) = 0"
	 * ,
	 * "8 * x^(2) * sqrt(10) + 12 * x^(2) - 32 * x * sqrt(10) - 16 * x * y - 24 * x + 8 * sqrt(10) * y^(2) - 24 * sqrt(10) * y + 32 * sqrt(10) + 24 * y^(2) - 40 * y = 0"
	 * ); t("f(x) := Element[Solve[c, y], 1]",
	 * "((-4 * sqrt(10) + 6) * x - sqrt(10) - 45 - 3 * sqrt(-(26 * sqrt(10) + 54) * x^(2) + (104 * sqrt(10) + 216) * x - 38 * sqrt(10) - 5)) / (-6 * sqrt(10) - 22)"
	 * ,
	 * "(x * (-4 * sqrt(10) + 6) - sqrt(10) - 3 * sqrt(x^(2) * (-sqrt(10) * 26 - 54) + x * (sqrt(10) * 104 + 216) - sqrt(10) * 38 - 5) - 45) / (-6 * sqrt(10) - 22)"
	 * ); t("f(RightSide[Element[Solve[f'(x) = 0, x], 1]])", // y-coord of top
	 * of ellipse (2.33, 3.03) ie approx 3.03
	 * "(sqrt(10) * 93 * sqrt(31 * (sqrt(10) * 224 + 687)) + 341 * sqrt(31 * (sqrt(10) * 224 + 687)) + sqrt(10) * 20832 + 63891) / (sqrt(10) * 13888 + 42594)"
	 * ); }
	 * 
	 * /* Ticket 3554: f '(x) doesn't work in Keep Input mode
	 */

	@Test
	public void ticket_Ticket3554_0() {
		tk("f(x) := x^2", "x^(2)");
		t("f'(x)", "2 * x");
	}

	@Test
	public void ticket_Ticket3554_1() {
		tk("f(x) := x^2", "x^(2)");
		t("Numeric[f'(x)]", "2 * x");
	}

	@Test
	public void ticket_Ticket3554_2() {
		tk("f(x) := x^2", "x^(2)");
		tk("f'(x)", "f'(x)");
	}

	/* Ticket 3557: Substitute and Simplification */

	@Test
	public void ticket_Ticket3557_0() {
		// TODO Add Test.
	}

	/* Ticket 3558: Curve always on X - Axis */

	// TODO This actually is a bug in its own right. But a different one.

	@Test
	public void ticket_Ticket3558_0() {
		t("Curve[t, 5 t, t, 0, 10]", "y - 5  * x = 0");
	}

	/* Ticket 3563: Solve Yields Empty Set for under-defined Systems */

	@Test
	public void ticket_Ticket3563_1() {
		t("Solve[{b + a = 0, c^2 - 1 = 0}]", "{{a = -b, b = b}}");
	}

	@Test
	public void ticket_Ticket3563_2() {
		t("Solve[{c^2 - 1 = 0, b + a = 0}]", "{{a = -b, b = b}}");
	}

	@Test
	public void ticket_Ticket3563_3() {
		t("Solve[{c^2 - 1 = 0, b + a = 0, x = 0}]", "{{x = 0, a = -b, b = b}}");
	}

	@Test
	public void ticket_Ticket3563_4() {
		t("Solve[{c^2 - 1 = 0, b + a = 0, x = 0}, b]", "{b = -a}");
	}

	@Test
	public void ticket_Ticket3563_5() {
		t("Solve[{c^2 - 1 = 0, b + a = 0, x = 0}, c]", "{c = -1, c = 1}");
	}

	/* Ticket 3579: Keepinput Being Kept */

	/**
	 * Test is ignored. Keepinput is no user command anymore, internal use seems
	 * to meet our expectations.
	 * 
	 * Therefore we don't want to mess with this anytime soon, except somebody
	 * complains.
	 */
	@Test
	public void ticket_Ticket3579_0() {
		tk("f(x) := x * x", "x * x");
	}

	/* Ticket 3594: Problem with Solve[{$1, $2}] and Solve tool */

	@Test
	public void ticket_Ticket3594_0() {
		// Test case for dynamic row references.

		t("x + 2 y = 3", "x + 2 * y = 3");
		t("4 x + 5 y = 6", "4 * x + 5 * y = 6");
		t("Solve[{$1, $2}]", "{{x = -1, y = 2}}");

		// Please note that static row references cannot be tested here
		// for we are bypassing the CASInputHandler,
		// which is employed to resolve them.
	}

	/* Test cases for tickets that never were created */

	/*
	 * "f(x):=" not being shown in the output when changing a cell into a
	 * definition via the marble
	 */

	@Test
	public void ticket_NoTicket_0() {
		GeoCasCell f = new GeoCasCell(kernel.getConstruction());
		kernel.getConstruction().addToConstructionList(f, false);
		f.setInput("f(x) := a * x^3 + b * x^2 + c * x + d");
		f.computeOutput();

		Assert.assertEquals(
				"f(x):=a x" + Unicode.SUPERSCRIPT_3 + " + b x"
						+ Unicode.SUPERSCRIPT_2 + " + c x + d",
				f.getOutput(StringTemplate.defaultTemplate));
	}

	/* Test for GGB-1636 */
	@Test
	public void ticket_Ticket1636_0() {
		t("Sequence[Vector[(i, i)],i,1,3]", "{(1, 1), (2, 2), (3, 3)}");
		t("Element[$1,1]", "(1, 1)");
		GeoCasCell gv = (GeoCasCell) kernel.lookupLabel("$2");
		gv.plot();
		Assert.assertTrue(gv.getTwinGeo().isGeoVector());
	}

	/*
	 * Examples from the article
	 * "GeoGebraCAS - Vom symbolischen Notizblock zur dynamischen CAS Ansicht"
	 * published in "CAS Rundbrief".
	 */

	/* Figure 2: "Gleichungsumformungen" (Manipulation of Equations) */

	@Test
	public void casRundbrief_Figure2_0() {
		tk("x - 1/2 = 2 x + 3", "x - 1 / 2 = 2 * x + 3");
	}

	@Test
	public void casRundbrief_Figure2_1() {
		tk("(x - 1 / 2 = 2x + 3) + 1/2", "(x - 1 / 2 = 2 * x + 3) + 1/2");
	}

	@Test
	public void casRundbrief_Figure2_2() {
		t("(x - 1 / 2 = 2x + 3) + 1 / 2", "x = 2 * x + 7 / 2",
				"x = (4 * x + 7) / 2", "x = 7 / 2 + 2 * x");
	}

	@Test
	public void casRundbrief_Figure2_3() {
		t("Numeric[(x - 1 / 2 = 2x + 3) + 1/2]", "x = 2 * x + 3.5");
	}

	/*
	 * Figure 3: "Einfache Exponentialgleichungen" (Simple Exponential
	 * Equations)
	 */

	@Test
	public void casRundbrief_Figure3_0() {
		t("f(t) := 100 * 1.5^t", "100 * (3 / 2) ^ (t)");
	}

	@Test
	public void casRundbrief_Figure3_1() {
		// Depends on CASRundbrief_Figure3_0.
		t("f(t) := 100 * 1.5^t", "100 * (3 / 2) ^ (t)");

		t("f(2)", "225");
	}

	@Test
	public void casRundbrief_Figure3_2() {
		// Depends on CASRundbrief_Figure3_0.
		t("f(t) := 100 * 1.5^t", "100 * (3 / 2) ^ (t)");

		t("Numeric[Solve[f(t) = 225, t]]", "{t = 2}");
	}

	@Test
	public void casRundbrief_Figure3_3() {
		// Depends on CASRundbrief_Figure3_0.
		t("f(t) := 100 * 1.5^t", "100 * (3 / 2) ^ (t)");

		// Do the same using symbolic evaluation.
		t("Solve[f(t) = 225, t]", "{t = 2}");

		// TODO Remove lines below.
		// Old test:
		// t("Solve[f(t) = 225, t]", "{t = 2}", "{t = log(9 / 4) / log(3 /
		// 2)}");
	}

	@Test
	public void casRundbrief_Figure3_4() {
		t("Solve[225 = c * 1.5^2, c]", "{c = 100}");
	}

	@Test
	public void casRundbrief_Figure3_5() {
		t("Solve[225 = 100 * a^2, a]", "{a = (-3) / 2 , a = 3 / 2}");
	}

	/*
	 * Figure 4: "L\u00f6sung mit unbelegten Variablen" (Solution with undefined
	 * variables)
	 */

	@Test
	public void casRundbrief_Figure4_0() {
		t("f(t) := c * a^t", "a^(t) * c");
	}

	@Test
	public void casRundbrief_Figure4_1() {
		// Depends on CASRundbrief_Figure4_0.
		t("f(t) := c * a^t", "a^(t) * c");

		t("Solve[f(2) = 225, a]",
				"{a = -15 * sqrt(c) / c, a = 15 * sqrt(c) / c}",
				"{a = 15 * sqrt(c) / c, a = -15 * sqrt(c) / c}");
	}

	/*
	 * Figure 5: "Computeralgebra und Geometrie" (Computer Algebra and
	 * Geometrics)
	 */

	@Test
	public void casRundbrief_Figure5_0() {
		t("f(x) := (2x^2 - 3x + 4) / 2", "x^(2) - 3 / 2 * x + 2");
	}

	@Test
	public void casRundbrief_Figure5_1() {
		// Suppress output using semicolon.
		// Warning: This does not affect the output here!
		// Therefore this test does not test suppression of the output,
		// but just semicolon at the end of the input not breaking anything
		// here.
		t("f(x) := (2x^2 - 3x + 4) / 2;", "x^(2) - 3 / 2 * x + 2");
	}

	@Test
	public void casRundbrief_Figure5_2() {
		t("g(x) := (x + 4) / 2", "1 / 2 * x + 2");
	}

	@Test
	public void casRundbrief_Figure5_3() {
		// Depends on CASRundbrief_Figure5_1.
		t("f(x) := (2x^2 - 3x + 4) / 2;", "x^(2) - 3 / 2 * x + 2");
		// Depends on CASRundbrief_Figure5_2.
		t("g(x) := (x + 4) / 2", "1 / 2 * x + 2");

		t("h(x) := f(x) - g(x)", "x^(2) - 2 * x");
	}

	@Test
	public void casRundbrief_Figure5_4() {
		// Depends on CASRundbrief_Figure5_1.
		t("f(x) := (2x^2 - 3x + 4) / 2;", "x^(2) - 3 / 2 * x + 2");
		// Depends on CASRundbrief_Figure5_2.
		t("g(x) := (x + 4) / 2", "1 / 2 * x + 2");
		// Depends on CASRundbrief_Figure5_3.
		t("h(x) := f(x) - g(x)", "x^(2) - 2 * x");

		t("Factor[h(x)]", "x * (x - 2)", "(x - 2) * x");
	}

	@Test
	public void casRundbrief_Figure5_5() {
		// Depends on CASRundbrief_Figure5_1.
		t("f(x) := (2x^2 - 3x + 4) / 2;", "x^(2) - 3 / 2 * x + 2");
		// Depends on CASRundbrief_Figure5_2.
		t("g(x) := (x + 4) / 2", "1 / 2 * x + 2");
		// Depends on CASRundbrief_Figure5_3.
		t("h(x) := f(x) - g(x)", "x^(2) - 2 * x");

		t("Solve[h(x) = 0, x]", "{x = 0, x = 2}");
	}

	@Test
	public void casRundbrief_Figure5_6() {
		// Depends on CASRundbrief_Figure5_1.
		t("f(x) := (2x^2 - 3x + 4) / 2;", "x^(2) - 3 / 2 * x + 2");
		// Depends on CASRundbrief_Figure5_2.
		t("g(x) := (x + 4) / 2", "1 / 2 * x + 2");

		t("Intersect[f(x), g(x)]", "{(0, 2), (2, 3)}", "{(2, 3), (0, 2)}");
	}

	/* Figure 6: "Umgekehrte Kurvendiskussion" (Backward Curve Sketching) */

	@Test
	public void casRundbrief_Figure6_0() {
		t("f(x) := a x^3 + b x^2 + c x + d",
				"a * x^(3) + b * x^(2) + c * x + d");
	}

	@Test
	public void casRundbrief_Figure6_1() {
		// Depends on CASRundbrief_Figure6_0.
		t("f(x) := a x^3 + b x^2 + c x + d",
				"a * x^(3) + b * x^(2) + c * x + d");

		// Suppress output using semicolon.
		// Warning: This does not affect the output here!
		// Therefore this test does not test suppression of the output,
		// but just semicolon at the end of the input not breaking anything
		// here.
		t("g_1: f(1) = 1;", "a + b + c + d = 1");
	}

	@Test
	public void casRundbrief_Figure6_2() {
		// Depends on CASRundbrief_Figure6_0.
		t("f(x) := a x^3 + b x^2 + c x + d",
				"a * x^(3) + b * x^(2) + c * x + d");

		// Suppress output using semicolon.
		// Warning: This does not affect the output here!
		// Therefore this test does not test suppression of the output,
		// but just semicolon at the end of the input not breaking anything
		// here.
		t("g_2: f(2) = 2;", "8 * a + 4 * b + 2 * c + d = 2");
	}

	@Test
	public void casRundbrief_Figure6_3() {
		// Depends on CASRundbrief_Figure6_0.
		t("f(x) := a x^3 + b x^2 + c x + d",
				"a * x^(3) + b * x^(2) + c * x + d");

		// Suppress output using semicolon.
		// Warning: This does not affect the output here!
		// Therefore this test does not test suppression of the output,
		// but just semicolon at the end of the input not breaking anything
		// here.
		t("g_3: f'(1) = 0;", "3 * a + 2 * b + c = 0");
	}

	@Test
	public void casRundbrief_Figure6_4() {
		// Depends on CASRundbrief_Figure6_0.
		t("f(x) := a x^3 + b x^2 + c x + d",
				"a * x^(3) + b * x^(2) + c * x + d");

		// Suppress output using semicolon.
		// Warning: This does not affect the output here!
		// Therefore this test does not test suppression of the output,
		// but just semicolon at the end of the input not breaking anything
		// here.
		t("g_4: f''(1) = 0;", "6 * a + 2 * b = 0");
	}

	@Test
	public void casRundbrief_Figure6_5() {
		// Depends on CASRundbrief_Figure6_0.
		t("f(x) := a x^3 + b x^2 + c x + d",
				"a * x^(3) + b * x^(2) + c * x + d");
		// Depends on CASRundbrief_Figure6_1.
		t("g_1: f(1) = 1;", "a + b + c + d = 1");
		// Depends on CASRundbrief_Figure6_2.
		t("g_2: f(2) = 2;", "8 * a + 4 * b + 2 * c + d = 2");
		// Depends on CASRundbrief_Figure6_3.
		t("g_3: f'(1) = 0;", "3 * a + 2 * b + c = 0");
		// Depends on CASRundbrief_Figure6_4.
		t("g_4: f''(1) = 0;", "6 * a + 2 * b = 0");

		t("Solve[{g_1, g_2, g_3, g_4}, {a, b, c, d}]",
				"{{a = 1, b = -3, c = 3, d = 0}}");
	}

	/* Figure 7: "Matrix in der CAS Ansicht" (Matrix in the CAS View) */

	@Test
	public void casRundbrief_Figure7_0() {
		t("A := {{2, 3, 2}, {1, 1, 1}, {0, -1, 3}}",
				"{{2, 3, 2}, {1, 1, 1}, {0, -1, 3}}");
	}

	/*
	 * Figure 8: "Gleichungssystem mittels Matrizen" (System of Equations via
	 * Matrices)
	 */

	@Test
	public void casRundbrief_Figure8_0() {
		// Suppress output using semicolon.
		// Warning: This does not affect the output here!
		// Therefore this test does not test suppression of the output,
		// but just semicolon at the end of the input not breaking anything
		// here.
		t("A := {{2, 3, 2}, {1, 1, 1}, {0, -1, 3}};",
				"{{2, 3, 2}, {1, 1, 1}, {0, -1, 3}}");
	}

	@Test
	public void casRundbrief_Figure8_1() {
		// Suppress output using semicolon.
		// Warning: This does not affect the output here!
		// Therefore this test does not test suppression of the output,
		// but just semicolon at the end of the input not breaking anything
		// here.
		t("B := {{3}, {2}, {7}};", "{{3}, {2}, {7}}");
	}

	@Test
	public void casRundbrief_Figure8_2() {
		// Suppress output using semicolon.
		// Warning: This does not affect the output here!
		// Therefore this test does not test suppression of the output,
		// but just semicolon at the end of the input not breaking anything
		// here.
		t("X := {{x}, {y}, {z}};", "{{x}, {y}, {z}}");
	}

	@Test
	public void casRundbrief_Figure8_3() {
		// Depends on CASRundbrief_Figure8_0.
		t("A := {{2, 3, 2}, {1, 1, 1}, {0, -1, 3}};",
				"{{2, 3, 2}, {1, 1, 1}, {0, -1, 3}}");
		// Depends on CASRundbrief_Figure8_1.
		t("B := {{3}, {2}, {7}};", "{{3}, {2}, {7}}");
		// Depends on CASRundbrief_Figure8_2.
		t("X := {{x}, {y}, {z}};", "{{x}, {y}, {z}}");

		t("A * X = B",
				"{{2 * x + 3 * y + 2 * z}, {x + y + z}, {-y + 3 * z}} = {{3}, {2}, {7}}");
	}

	/*
	 * Figure 9: "L\u00f6sung des Gleichungssystemes" (Solution of the System of
	 * Equations)
	 */

	@Test
	public void casRundbrief_Figure9_0() {
		// Depends on CASRundbrief_Figure8_0.
		t("A := {{2, 3, 2}, {1, 1, 1}, {0, -1, 3}};",
				"{{2, 3, 2}, {1, 1, 1}, {0, -1, 3}}");
		// Depends on CASRundbrief_Figure8_1.
		t("B := {{3}, {2}, {7}};", "{{3}, {2}, {7}}");

		t("Invert[A] * B", "{{1}, {-1}, {2}}");
	}

	@Test
	public void quickStart() {
		t("f(x) := x^2 - 3/2 * x + 2", "x^(2) - 3 / 2 * x + 2",
				"(2* x^(2) - 3 * x + 4) / 2");
		t("g(x) := 1/2 * x + 2", "1 / 2 * x + 2", "(x + 4) / 2");
		t("h(x):=f(x)-g(x)", "x^(2) - 2 * x");
		t("Factor[h(x)]", "x * (x - 2)", "(x - 2) * x");
		t("Solve[h(x) = 0, x]", "{x = 0, x = 2}", "{x = 2, x = 0}");
		t("S:=Intersect[f(x),g(x)]", "{(0, 2), (2, 3)}", "{(2, 3), (0, 2)}");
		t("Delete[f]", "true");
		t("Delete[g]", "true");
		t("Delete[h]", "true");
	}

	@Test
	public void solveArbconst() {
		t("a(t):=2t+3", "2 * t + 3");
		t("v(t):=Integral[a(t),t]", "t^(2) + 3 * t + c_1");
		t("Solve[v(0)=0,c_1]", "{c_1 = 0}");
	}

	@Test
	public void assignSolve() {
		t("f(x):=a*x^2+3x-5", "a * x^(2) + 3 * x -5");
		t("Solve[f(x)=0]",
				"{x = (sqrt(20 * a + 9) - 3) / (2 * a), x = (-sqrt(20 * a + 9) - 3) / (2 * a)}");
		t("Solve[20a + 9 = 0]", "{a = (-9) / 20}");
	}

	@Test
	public void applyFunctionToList() {
		// https://help.geogebra.org/topic/applying-a-function-to-a-list
		// make sure all 4 operations are tested
		t("f(x):=(x+3)*cos(3x+2)+1/10", "(x + 3) * cos(3 * x + 2) + 1 / 10");
		t("g(x):=2-sqrt(4+8x-5x^2)", "-sqrt(-5 * x^(2) + 8 * x + 4) + 2");
		t("h(x):=3 * x^2 - x / 2 + 2*sin(x)",
				"3 * x^(2) + 2 * sin(x) - 1 / 2 * x");
		// t("f({1,2,3,4}",
		// "{(4 * cos(5)) + 1 / 10, (5 * cos(8)) + 1 / 10, (6 * cos(11)) + 1 /
		// 10, (7 * cos(14)) + 1 / 10}");
		// t("g({1,2,3,4}",
		// "{(-sqrt(7)) + 2, 2, ((-sqrt(17)) * " + Unicode.IMAGINARY
		// + ") + 2, (((-sqrt(11)) * 2) * " + Unicode.IMAGINARY
		// + ") + 2}");
		// t("h({1,2,3,4}",
		// "{(2 * sin(1)) + 5 / 2, (2 * sin(2)) + 11, (2 * sin(3)) + 51 / 2, (2
		// * sin(4)) + 46}");
	}

	@Test
	public void piecewiseIntegration() {
		t("f(x):=x^2", "x^(2)");
		t("g(x):=1/x", "1/x");
		t("h(x):=If(0<x<=2,x^2, x>2, 1/x)", "Wenn(0 < x " + Unicode.LESS_EQUAL
				+ " 2, x^(2), x > 2, 1 / x)");
		t("h2(x):=If(x<=2,x^2, x>2, 1/x)",
				"Wenn(x " + Unicode.LESS_EQUAL + " 2, x^(2), x > 2, 1 / x)");
		t("h3(x):=If(0<x<=2,f(x), x>2, g(x))", "Wenn(0 < x "
				+ Unicode.LESS_EQUAL + " 2, x^(2), x > 2, 1 / x)");
		t("h4(x):=If(0<x<=2,f(x), 2<x<4, g(x))", "Wenn(0 < x "
				+ Unicode.LESS_EQUAL + " 2, x^(2), 2 < x < 4, 1 / x)");
		t("Integral(h(x),1,3)", "-log(2) + log(3) + 7 / 3", "2.738798441441");
		t("Integral(h2(x),1,3)", "-log(2) + log(3) + 7 / 3");
		t("Integral(h3(x),1,3)", "-log(2) + log(3) + 7 / 3", "2.738798441441");
		t("Integral(h4(x),1,3)", "-log(2) + log(3) + 7 / 3", "2.738798441441");
	}

	@Test
	public void exponentialEqs() {
		t("Solve[7^(2 x - 5) 5^x = 9^(x + 1), x]",
				"{x = (5 * log(7) + log(9)) / (log(5) + 2 * log(7) - log(9))}");
		t("Solve[13^(x+1)-2*13^x=(1/5)*5^x,x]",
				"{x = (-log(11) - log(5)) / (log(13) - log(5))}");

		// These take too long (more than 1 minute)
		// t("Solve[{6.7 * 10^9 = c * a^2007, 3 * 10^8 = c * a^950}, {c, a}]",
		// "{{c = 300000000 / ((67 / 3)^(1 / 1057))^(950), a = 67 / (3 * ((67 /
		// 3)^(1 / 1057))^(1056))}}");
		// t("Solve[{6.7 * 10^9 = c * a^2007, 3 * 10^8 = c * a^950}, {a, c}]",
		// "{{c = 300000000 / ((67 / 3)^(1 / 1057))^(950), a = 67 / (3 * ((67 /
		// 3)^(1 / 1057))^(1056))}}");
	}

	@Test
	public void fDashedTest() {
		t("f(x):=g(x)/x", "g(x) / x");
		t("f'(x)", "(-g(x) + x * g'(x)) / x^(2)");
		t("f'(x+1)", "(-g(x + 1) + g'(x + 1) * (x + 1)) / (x + 1)^(2)",
				"(-g(x + 1) + (x + 1) * g'(x + 1)) / (x + 1)^(2)");
	}

	@Test
	/** GGB-1663 */
	public void eNotationTest() {
		t("a:=1/1E8", "1 / 100000000");
		t("b:=1/1E-8", "100000000");
	}

	@Test
	public void vectorPointTest() {
		t("v:=(1,1)", "(1,1)");
		t("V:=v+v", "(2,2)");
		Assert.assertEquals(GeoClass.VECTOR,
				kernel.lookupLabel("V").getGeoClassType());

		t("w:=(1,1,1)", "(1,1,1)");
		t("W:=w+w", "(2,2,2)");
		Assert.assertEquals(GeoClass.VECTOR3D,
				kernel.lookupLabel("W").getGeoClassType());
	}

	@Test
	public void orthogonalVectorFallbackTest() {
		t("E:=Plane[(1,-2,3), (-2, 0,1),(0,3,2)]", "8x - y - 13z = -29",
				"x * 8 + y * (-1) + z * (-13) = -29");
		t("PerpendicularVector[E]", "(8, -1, -13)");
	}

	@Test
	public void quadricReloadTest() {
		t("a:=2", "2");
		t("K:=x^2+y^2+z^2=a", "x^(2) + y^(2) + z^(2) = 2");
		Assert.assertEquals("Sphere", kernel.lookupCasCellLabel("K")
				.getTwinGeo().getTypeString());
		String xml = getApp().getXML();
		kernel.clearConstruction(true);
		getApp().setXML(xml, true);
		Assert.assertEquals("Sphere", kernel.lookupCasCellLabel("K")
				.getTwinGeo().getTypeString());
	}

	@Test
	public void renameCellShouldNotReplaceInput() {
		t("a(x) := x^2 * x", "x^(3)");
		t("b(x) := -a(x)", "-x^(3)");

		GeoCasCell a = kernel.lookupCasCellLabel("a");
		String input = "c(x) := x^2 * x";
		ta(a, false, input, "x^(3)");
		new CASCellProcessor(kernel.getLocalization()).fixInput(a, input,
				false);

		Assert.assertEquals("c(x) := x^2 * x",
				a.getInput(StringTemplate.defaultTemplate));
		Assert.assertFalse(a.isError());
	}

	@Test
	public void symbolicDerivative() {
		t("f(x) := h(x)", "h(x)");
		t("f''(x)", "h''(x)");
		t("f'(x)", "h'(x)");
	}

	@Test
	public void tOfXshouldBefFunction() {
		t("f(x) := x^2-4", "x^(2) - 4");
		t("t(x) := Tangent(2,f)", "4 * x - 8");
		t("Solve(t(x)=-4)", "{x = 1}");
		t("m := Tangent(2,f)", "y = 4 * x - 8");
		t("Solve(m(x)=-4)", "{x = 1}");
	}

	@Test
	public void intersectShouldAcceptInputBarPlanes() {
		in("e:x+0z=1");
		in("f:y+z=2");
		t("Intersect(e,f)",
				"X = (1, 1, 1) + " + Unicode.lambda + " * (0, -1, 1)");
		t("Intersect(Plane(x+0z=1),Plane(y+z=2))",
				"X = (1, 1, 1) + " + Unicode.lambda + " * (0, -1, 1)");
	}

	@Test
	public void checkNsolveExpansion() {
		CASInputHandler cih = new CASInputHandler(
				new CASViewNoGui(getApp(), "Sum(T/2^n,n,3,10)=1500000"));
		cih.processCurrentRow("NSolve", false);
		t("$1", "{T = 1204705882353 / 200000}");
		// .getOutput(StringTemplate.defaultTemplate), "");
	}

	/** See APPS-801 */
	@Test
	public void marbleForLeftSideShouldCreateFunction() {
		t("f:LeftSide(1=x)", "1");
		GeoCasCell casCell = kernel.lookupCasCellLabel("f");
		casCell.plot();
		Assert.assertEquals(
				casCell.getTwinGeo().getGeoClassType(),
				GeoClass.FUNCTION);
	}
}
