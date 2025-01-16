package org.geogebra.common.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.plugin.Operation;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.editor.share.io.latex.ParseException;
import com.himamis.retex.editor.share.io.latex.Parser;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.serializer.GeoGebraSerializer;
import com.himamis.retex.editor.share.serializer.TeXSerializer;
import com.himamis.retex.editor.share.util.Unicode;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class EditorParserTest {

	/**
	 * Reset LaTeX factory
	 */
	@BeforeClass
	public static void prepare() {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderCommon());
		}
	}

	private static MathFormula parseForEditor(String input) {
		try {
			Parser parser = new Parser(new MetaModel());
			return parser.parse(input);
		} catch (ParseException e) {
			throw new IllegalStateException(e);
		}
	}

	private static void assertParsesAs(String input, String serialized) {
		MathFormula formula = parseForEditor(input);
		GeoGebraSerializer serializer = new GeoGebraSerializer();
		String result = serializer.serialize(formula);
		assertEquals(serialized, result);
	}

	@Test
	public void trigonometricFunctionPowerTest() {
		String cosOverExponential = "((cos(x))/(e^(3t)))";
		assertParsesAs("cos(x)/e^(3t)", cosOverExponential);

		String oneOverCosPower = "((1)/(cos^(4)(x)))";
		assertParsesAs("1/cos^(4)(x)", oneOverCosPower);
		assertParsesAs("1/cos" + Unicode.SUPERSCRIPT_4 + "(x)", oneOverCosPower);

		String cosSquaredTimesFraction = "cos^(2)(x)((sin(x))/(x))";
		assertParsesAs("cos^(2)(x)sin(x)/x", cosSquaredTimesFraction);
		assertParsesAs("cos" + Unicode.SUPERSCRIPT_2 + "(x)sin(x)/x", cosSquaredTimesFraction);

		String expArctan = "e^(tan^(-1)(1))";
		assertParsesAs("e^(tan^(-1)(1))", expArctan);
		assertParsesAs("e^(tan" + Unicode.SUPERSCRIPT_MINUS_ONE_STRING + "(1))", expArctan);

		String xToArcsin = "x^(sin^(-1)(x))";
		assertParsesAs("x^(sin^(-1)(x))", xToArcsin);
		assertParsesAs("x^(sin" + Unicode.SUPERSCRIPT_MINUS_ONE_STRING + "(x))", xToArcsin);
	}

	@Test
	public void subscriptTest() {
		assertParsesAs("a_{1,2}", "a_{1,2}");
	}

	@Test
	public void absoluteValuesUsingVerticalBarsTest() {
		assertParsesAs("|x|", "abs(x)");
		assertParsesAs("|1|+|2|", "abs(1)+abs(2)");
		assertParsesAs("|x-|3||", "abs(x-abs(3))");
		assertParsesAs("||x-3| - 4|", "abs(abs(x-3) - 4)");
		assertParsesAs("|1-|2-|3|||", "abs(1-abs(2-abs(3)))");
		assertParsesAs("|||1| - 2| - 3|", "abs(abs(abs(1) - 2) - 3)");
	}

	@Test
	public void unicodeSqrtParseTest() {
		assertParsesAs("r(2) / r(x) = 4".replace('r', Unicode.SQUARE_ROOT),
				"((sqrt(2))/(sqrt(x))) = 4");
		assertParsesAs("r2 / rx = 4".replace('r', Unicode.SQUARE_ROOT),
				"((r2)/(rx)) = 4".replace('r', Unicode.SQUARE_ROOT));
	}

	@Test
	public void mixedNumber() {
		assertParsesAs("3" + Unicode.INVISIBLE_PLUS + "(1)/(2)",
				"(3" + Unicode.INVISIBLE_PLUS + "(1)/(2))");
		assertParsesAs("3 1/2", "(3 " + Unicode.INVISIBLE_PLUS + "(1)/(2))");
		assertParsesAs("-4 1/3", "-(4 " + Unicode.INVISIBLE_PLUS + "(1)/(3))");
		assertParsesAs("-7" + Unicode.INVISIBLE_PLUS + "(2)/(3)",
				"-(7" + Unicode.INVISIBLE_PLUS + "(2)/(3))");
		assertParsesAs("sqrt(3 1/2)", "sqrt((3 " + Unicode.INVISIBLE_PLUS + "(1)/(2)))");
	}

	@Test
	public void recurringDecimal() {
		assertParsesAs("1.23\u03054\u0305", "1.23\u03054\u0305");
		assertParsesAs("4.34\u03055\u03056\u0305", "4.34\u03055\u03056\u0305");
		assertParsesAs("7.9\u0305 / 2", "((7.9\u0305)/(2))");
		assertParsesAs("1.23\u0305 + 2", "1.23\u0305 + 2");
		assertParsesAs("1.23\u0305 * 1/2", "1.23\u0305 * ((1)/(2))");
		assertParsesAs("1.87\u0305 - 0.5", "1.87\u0305 - 0.5");
	}

	@Test
	public void pointTemplate() {
		assertParsesAs("$point(1,2)", "(1,2)");
		assertParsesAs("$pointAt(1,3)", "(1,3)");
	}

	@Test
	public void symbolLaTeXShouldParse() {
		AppCommon appCommon = AppCommonFactory.create();
		Kernel kernel = appCommon.getKernel();
		FunctionVariable varX = new FunctionVariable(kernel, "x");
		for (Operation op : Operation.values()) {
			ExpressionNode node = new ExpressionNode(kernel, varX, op, varX);
			if (isSpecialOperation(op)) {
				continue;
			}
			MathFormula editable = parseForEditor(node.toString(StringTemplate.editorTemplate));
			TeXFormula formula = new TeXFormula(
					TeXSerializer.serialize(editable.getRootComponent()));
			assertNotNull(formula.root);
		}
	}

	/**
	 * @return whether operation requires special argument types
	 */
	private boolean isSpecialOperation(Operation op) {
		return op == Operation.IF_LIST || op == Operation.DOLLAR_VAR_COL
				|| op == Operation.DOLLAR_VAR_ROW || op == Operation.DOLLAR_VAR_ROW_COL
				|| op == Operation.INVISIBLE_PLUS;
	}
}
