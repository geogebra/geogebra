package org.geogebra.common.kernel.commands;

import static org.geogebra.test.TestStringUtil.unicode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Locale;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class ParserTest {
	private AppDNoGui app;
	private Parser parser;

	@Before
	public void setup() {
		app = new AppDNoGui(new LocalizationD(3), false);
		parser = app.getKernel().getParser();
		app.setLanguage(Locale.US);
	}

	@Test
	public void testBrackets() {
		try {

			long l = System.currentTimeMillis();

			parseExpression("{{{{{{{{{{{{{{{{{{{{{{{}}}}}}}}}}}}}}}}}}}}}}}");

			parseExpression("(((((((((((((((((((((((1)))))))))))))))))))))))");
			parseExpression(
					"If[x>1,If[x>2,If[x>3,If[x>4,If[x>1,If[x>2,If[x>3,If[x>4,If[x>1,If[x>2,If[x>3,If[x>4,If[x>1,If[x>2,If[x>3,If[x>4,If[x>1,If[x>2,If[x>3,If[x>4,If[x>1,If[x>2,If[x>3,If[x>4,42"
							+ "]]]]]]]]]]]]]]]]]]]]]]]]");
			l = System.currentTimeMillis() - l;
			Log.debug("TIME" + l);
			assertTrue("Too long:" + l, l < 400);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testExceptions() {
		shouldBeException("1.2.3", "MyError");
		shouldBeException("1+", "ParseException");
		shouldBeException("-", "ParseException");
		shouldBeException("(", "BracketsError");
		shouldBeException("{-", "BracketsError");
	}

	@Test
	public void shouldKeppPriorityUnaryBinary() {
		checkSameStructure("x(x+1)^2", "x*(x+1)^2");
		checkSameStructure(Unicode.SQUARE_ROOT + "x(x+1)", "sqrt(x)*(x+1)");
		checkSameStructure("x(x+1)!", "x*(x+1)!");
		checkSameStructure("cos^2(x)", "cos(x)^2");
		checkSameStructure("sin" + Unicode.SUPERSCRIPT_2 + "(x)", "sin(x)^2");
	}

	@Test
	public void testSpecialVectors() {
		checkSameStructure("A(1|2)", "(1,2)");
		checkSameStructure("A(1|2|3)", "(1,2,3)");
		checkSameStructure("A(1;pi/2)", "(1;pi/2)");
	}

	@Test
	public void testPiMultiplication() {
		checkSameStructure(Unicode.PI_STRING + "(1.3)",
				Unicode.PI_STRING + " 1.3");
		checkSameStructure("pi(1.3)", Unicode.PI_STRING + " 1.3");
		shouldReparseAs(Unicode.PI_STRING + "8",  Unicode.PI_STRING + " * 8");
		shouldReparseAs("2" + Unicode.PI_STRING + "8",
				"2" + Unicode.PI_STRING + " * 8");
		// APPS-804
		shouldReparseAs(Unicode.PI_STRING + "8.1",
				Unicode.PI_STRING + " * 8.1");
		shouldReparseAs("2" + Unicode.PI_STRING + "8.1",
				 unicode("2@pi * 8.1"));
	}

	@Test
	public void testPiPower() {
		shouldReparseAs("pixxyyy", unicode("@pi x^2 y^3"));
		shouldReparseAs(Unicode.PI_STRING + "3^2", unicode("@pi * 3^2"));
	}

	@Test
	public void testPower() {
		shouldReparseAs("f(k,y,z)=kyz^6", unicode("k y z^6"));
	}

	@Test
	public void testTrigPower() {
		shouldReparseAs("sinxy^2",
				unicode("sin(x y^2)"));
		shouldReparseAs("sinxxx^2",
				unicode("sin(x^2 x^2)"));
	}

	@Test
	public void testLogPower() {
		shouldReparseAs("xln(x)^2",
				unicode("x (ln(x))^2"));
	}

	private void checkSameStructure(String string, String string2) {
		Assert.assertEquals(reparse(string, StringTemplate.maxPrecision),
				reparse(string2, StringTemplate.maxPrecision));
	}

	private String reparse(String string, StringTemplate tpl) {
		return reparse(app, string, tpl, false);
	}

	private static String reparse(App app, String string, StringTemplate tpl,
								  boolean multipleUnassignedAllowed) {
		String reparse1 = "";
		try {
			ValidExpression v1 = parseExpression(app, string);
			FunctionVariable xVar = new FunctionVariable(app.getKernel(), "x"),
					yVar = new FunctionVariable(app.getKernel(), "y"),
					zVar = new FunctionVariable(app.getKernel(), "z");
			EvalInfo info = multipleUnassignedAllowed
					? new EvalInfo(false).withMultipleUnassignedAllowed()
					: new EvalInfo(false);

			v1.resolveVariables(info);
			v1.wrap().replaceXYZnodes(xVar, yVar, zVar,
					new ArrayList<ExpressionNode>());
			app.getKernel().getConstruction().registerFunctionVariable(null);
			reparse1 = v1.toString(tpl);
		} catch (ParseException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return reparse1;
	}

	private void shouldBeException(String string,
			String exceptionClass) {
		Throwable p = null;
		try {
			parseExpression(string);
		} catch (Throwable e) {
			p = e;
		}
		Assert.assertEquals(exceptionClass, p.getClass().getSimpleName());
	}

	/**
	 *  
	 */
	@Test
	public void testInvalid() {
		long l = System.currentTimeMillis();
		try {

			parseExpression(
					"x/(x/(x/(x/(x/(x/(x/(x/(x/(x/(x/(x/(x/(x/(x/(x/(x/(x/(x/(x/(x/()))))))))))))))))))))");

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		l = System.currentTimeMillis() - l;
		Log.debug("TIME" + l);
		assertTrue("Too long:" + l, l < 4000);
	}

	/**
	 * Test for || brackets
	 */
	@Test
	public void testAbsValue() {

		try {
			parseExpression("|1|");
			parseExpression("(1|1|1)");
			parseExpression("(a||b)");
		} catch (ParseException e) {
			assertNull(e);
		}
		try {
			parseExpression("|1|2|3|");
		} catch (ParseException e) {
			assertNotNull(e);
		}
	}

	@Test

	public void testLogFunction() {
		shouldReparseAs("log_{10}(x)", "log(10, x)");
		// bug?
		// shouldReparseAs("log_10(x)", "");
		shouldReparseAs("log_3(x)", "log(3, x)");
		shouldReparseAs("log_{3}(x)", "log(3, x)");
		shouldReparseAs("ln(x)", "ln(x)");
		shouldReparseAs("ld(x)", "ld(x)");
		shouldReparseAs("lg(x)", "lg(x)");
		shouldReparseAs("log(x)", "lg(x)");
		shouldReparseAs("log_" + Unicode.EULER_STRING + "(x)",
				"log(" + Unicode.EULER_STRING + ", x)");
		shouldReparseAs("log_{" + Unicode.EULER_STRING + "}(x)",
				"log(" + Unicode.EULER_STRING + ", x)");
	}

	@Test
	public void testLogFunctionFromFile() {
		app.getKernel().setLoadingMode(true);
		shouldReparseAs("log(x)", "ln(x)");
		shouldReparseAs("log(5,x)", "log(5, x)");
		app.getKernel().setLoadingMode(false);
	}

	@Test
	public void multiplicationByTrigShouldChangeToApplication() {
		app.getKernel().getAlgebraProcessor().processAlgebraCommand("a=1", false);
		shouldReparseAs("cos x", "cos(x)");
		// shouldReparseAs("cos 9x", "cos(9 x)"); TODO

		shouldReparseAs("cos7t/t", "cos(7t) / t");
		shouldReparseAs("cos3x", "cos(3x)");
		shouldReparseAs("cos3a", "cos(3a)");
		shouldReparseAs("f(n)=cos3n", "cos(3n)");
		shouldReparseAs("cos3n", "cos(3n)");
		shouldReparseAs("x*cos3x", "x cos(3x)");
		shouldReparseAs("3x*cosx", "3x cos(x)");
		shouldReparseAs("3x*cos3x", "3x cos(3x)");
		shouldReparseAs("ln3", "ln(3)");
		// ln|y+6| not supported in parser; AV editor prduces ln abs(y+6) anyway
		shouldReparseAs("ln abs(y+6)", "ln(abs(y + 6))");
		shouldReparseAs("cos33" + Unicode.DEGREE_STRING,
				"cos(33" + Unicode.DEGREE_STRING + ")");
		shouldReparseAs("3cos33" + Unicode.DEGREE_STRING,
				"3cos(33" + Unicode.DEGREE_STRING + ")");
	}

	@Test
	public void inverseTrigShouldUseDegrees() {
		shouldReparseAs("atanx", "atand(x)");
	}

	@Test
	public void multiplicationByTrigPowerShouldChangeToApplication() {
		String sinCubedX = "sin" + Unicode.SUPERSCRIPT_3 + "(x)";
		shouldReparseAs("sin" + Unicode.SUPERSCRIPT_3 + "(x)", sinCubedX);
		shouldReparseAs("sin^3(x)", sinCubedX);
		shouldReparseAs("sin" + Unicode.SUPERSCRIPT_3 + " x", sinCubedX);
		shouldReparseAs("sin^3 x", sinCubedX);
		shouldReparseAs("e^(-t)9sin" + Unicode.SUPERSCRIPT_8 + "cost",
			Unicode.EULER_STRING + "^(-t) * 9sin" + Unicode.SUPERSCRIPT_8 + "(cos(t))");
	}

	@Test
	public void powerShouldHavePrecedence() {
		shouldReparseAs("sin 2^2", unicode("sin(2^2)"));
		shouldReparseAs("sin2^2", unicode("sin(2^2)"));
		shouldReparseAs("sin3x^2", unicode("sin(3x^2)"));
	}

	@Test
	public void multiplicationShouldResolvedToChainedTrig() {
		app.getKernel().getConstruction().registerFunctionVariable("t");
		shouldReparseAs(app, "e^(-t)9sin" + Unicode.SUPERSCRIPT_8 + "tcost",
				Unicode.EULER_STRING + "^(-t) * 9sin"
				+ Unicode.SUPERSCRIPT_8 + "(t cos(t))");
	}

	@Test
	public void shouldKeepPriorityTwoBinary() {
		Kernel kernel = app.getKernel();
		for (Operation top : Operation.values()) {
			if (!binary(top)) {
				continue;
			}
			for (Operation bottom : Operation.values()) {
				if (!binary(bottom)) {
					continue;
				}

				ExpressionNode ex = new ExpressionNode(kernel,
						new Variable(kernel, "a"), bottom,
						new Variable(kernel, "b"));
				ExpressionNode left = new ExpressionNode(kernel,
						new Variable(kernel, "c"), top, ex);
				checkStable(left);

				ExpressionNode right = new ExpressionNode(kernel, ex, top,
						new Variable(kernel, "c"));
				checkStable(right);
				ExpressionNode both = new ExpressionNode(kernel, ex, top, ex);
				checkStable(both);
			}
		}
	}

	@Test
	public void commaParsingShouldWorkInGerman() {
		app.setLanguage(new Locale("de"));
		shouldReparseAs("3,141", "3.141");
		shouldReparseAs("3,5", "3.5");
		shouldReparseAs("1,2 + 1,4", "1.2 + 1.4");
		shouldReparseAs("(1,2) + 1,4", "(1, 2) + 1.4");
		shouldReparseAs("3,5>x", "Wenn(5 > x, 3)");
	}

	@Test
	public void shouldKeepMultiplicationFromLeft() {
		String f1 = reparse(app, "F(x,A,B)=BAxe^(-Bx)-Ae^(-Bx)",
				StringTemplate.xmlTemplate, true);
		assertEquals("(((B * A) * x) * " + Unicode.EULER_STRING
						+ "^((-((B * x))))) - (A * " + Unicode.EULER_STRING + "^((-((B * x)))))",
				f1);
		String f2 = reparse(app, "F(x,A,B)=B A x e^(-B x)-A e^(-B x)",
				StringTemplate.xmlTemplate, true);
		// brackets in exponent slightly different
		assertEquals("(((B * A) * x) * " + Unicode.EULER_STRING
						+ "^(((-B) * x))) - (A * " + Unicode.EULER_STRING + "^(((-B) * x)))",
				f2);
	}

	@Test
	public void checkValidLabels() {
		assertValidLabel("aa");
		assertValidLabel("aa8");
		assertValidLabel("aa_7");
		assertValidLabel("aa_{72}''");
		assertValidLabel(Unicode.PI_STRING + 8);
	}

	@Test
	public void shouldHandleDecimalsInLabels() {
		shouldReparseAs("x1.3=7", "x * 1.3 = 7");
		shouldReparseAs("x1.3=y", "x * 1.3 = y");
		shouldReparseAs("x_{1.3}=7", "7");
	}

	@Test
	public void testTrigPowerPriorities() {
		shouldReparseAs("sin^(-1)(x)^2", unicode("(sin^-1(x))^2"));
		shouldReparseAs("sin^(-1)((x)^2)", unicode("sin^-1(x^2)"));
		shouldReparseAs("sin^(-1)x^2", unicode("sin^-1(x^2)"));
		shouldReparseAs("(sin^(-1)(x))^2", unicode("(sin^-1(x))^2"));
	}

	@Test
	public void shouldParseNegativeLogPowerAsReciprocal() {
		shouldReparseAs("ln^(-1)(x)^2", unicode("((ln(x))^-1)^2"));
	}

	private void assertValidLabel(String s) {
		try {
			assertEquals(s, parser.parseLabel(s));
		} catch (ParseException e) {
			fail("Unexpected parser exception " + e);
		}
	}

	static void shouldReparseAs(App app, String string, String expected) {
		Assert.assertEquals(expected,
				reparse(app, string, StringTemplate.editTemplate, true));
	}

	private void shouldReparseAs(String string, String expected) {
		Assert.assertEquals(expected,
				reparse(string, StringTemplate.editTemplate));
	}

	private static boolean binary(Operation op) {
		return !Operation.isSimpleFunction(op) && op != Operation.IF_LIST
				&& op != Operation.DOLLAR_VAR_COL && op != Operation.DOLLAR_VAR_ROW_COL
				&& op != Operation.DOLLAR_VAR_ROW && op != Operation.XOR
				&& op != Operation.AND_INTERVAL && op != Operation.ELEMENT_OF
				&& op != Operation.DIFF && op != Operation.FREEHAND
				&& op != Operation.DATA && op != Operation.MATRIXTOVECTOR
				&& op != Operation.NO_OPERATION
				&& op != Operation.MULTIPLY_OR_FUNCTION && op != Operation.BETA
				&& op != Operation.BETA_INCOMPLETE
				&& op != Operation.BETA_INCOMPLETE_REGULARIZED
				&& op != Operation.GAMMA_INCOMPLETE_REGULARIZED
				&& op != Operation.FUNCTION && op != Operation.FUNCTION_NVAR
				&& op != Operation.VEC_FUNCTION && op != Operation.DERIVATIVE
				&& op != Operation.IF && op != Operation.IF_SHORT
				&& op != Operation.IF_ELSE && op != Operation.SUM
				&& op != Operation.INVERSE_NORMAL;
	}

	private void checkStable(ExpressionNode left) {
		String str = null;
		try {
			str = left.toString(StringTemplate.editTemplate);
			ExpressionNode ve = (ExpressionNode) parseExpression(str);
			String combo = left.getOperation() + "," + ve.getOperation();

			if ("SQRT_SHORT,SQRT".equals(combo) || "PLUS,MINUS".equals(combo)
					|| "PLUS,PLUSMINUS".equals(combo)
					|| "DIVIDE,MULTIPLY".equals(combo)
					|| "VECTORPRODUCT,MULTIPLY".equals(combo)) {
				return;
			}
			Log.debug(str);
			Assert.assertEquals(left.getOperation(), ve.getOperation());

		} catch (ParseException e) {
			fail(str);
		}
	}

	private ValidExpression parseExpression(String string)
			throws ParseException {
		return parseExpression(app, string);
	}

	private static ValidExpression parseExpression(App app, String string)
			throws ParseException {
		return app.getKernel().getParser().parseGeoGebraExpression(string);
	}
}