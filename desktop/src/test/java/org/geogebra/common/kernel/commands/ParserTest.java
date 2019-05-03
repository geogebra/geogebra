package org.geogebra.common.kernel.commands;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Locale;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class ParserTest {
	static AppDNoGui app;
	static AlgebraProcessor ap;

	@BeforeClass
	public static void setupCas() {
		app = new AppDNoGui(new LocalizationD(3), false);
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
		shouldReparseAs(Unicode.PI_STRING + "8", Unicode.PI_STRING + "8");
		shouldReparseAs("2" + Unicode.PI_STRING + "8",
				"2" + Unicode.PI_STRING + "8");
		// APPS-804
		// shouldReparseAs(Unicode.PI_STRING + "8.1",
		// Unicode.PI_STRING + "8.1");
		// shouldReparseAs("2" + Unicode.PI_STRING + "8.1",
		// "2" + Unicode.PI_STRING + "8.1");

	}

	private static void checkSameStructure(String string, String string2) {
		Assert.assertEquals(reparse(string, StringTemplate.maxPrecision),
				reparse(string2, StringTemplate.maxPrecision));
	}

	private static String reparse(String string, StringTemplate tpl) {
		String reparse1 = "";
		try {
			ValidExpression v1 = parseExpression(string);
			FunctionVariable xVar = new FunctionVariable(app.getKernel(), "x"),
					yVar = new FunctionVariable(app.getKernel(), "y"),
					zVar = new FunctionVariable(app.getKernel(), "z");
			v1.wrap().replaceXYZnodes(xVar, yVar, zVar,
					new ArrayList<ExpressionNode>());
			reparse1 = v1.toString(tpl);
		} catch (Throwable e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		return reparse1;
	}

	private static void shouldBeException(String string,
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
		shouldReparseAs("log(x)", "ln(x)");
		shouldReparseAs("log_" + Unicode.EULER_STRING + "(x)",
				"log(" + Unicode.EULER_STRING + ", x)");
		shouldReparseAs("log_{" + Unicode.EULER_STRING + "}(x)",
				"log(" + Unicode.EULER_STRING + ", x)");
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
	public void commaParseTest() {
		shouldReparseAs("3,5", "3.5");
		shouldReparseAs("3,5>x", "If(5 > x, 3)");
		shouldReparseAs("1,2 + 1,4", "1.2 + 1.4");
		shouldReparseAs("(1,2) + 1,4", "(1, 2) + 1.4");
	}

	private static void shouldReparseAs(String string, String expected) {
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

	private static void checkStable(ExpressionNode left) {
		String str = null;
		try {
			str = left.toString(StringTemplate.editTemplate);
			// Log.debug(str);
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
			Assert.fail(str);
		}
	}

	private static ValidExpression parseExpression(String string)
			throws ParseException {
		return app.getKernel().getParser().parseGeoGebraExpression(string);
	}

}