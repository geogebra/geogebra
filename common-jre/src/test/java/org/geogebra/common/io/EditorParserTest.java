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

	private static void parsesAs(String input, String serialized) {
		MathFormula formula = parseForEditor(input);
		GeoGebraSerializer serializer = new GeoGebraSerializer();
		String result = serializer.serialize(formula);
		assertEquals(serialized, result);
	}

	@Test
	public void trigonometricFunctionPowerTest() {
		String cosOverExponential = "(cos(x))/(e^(3t))";
		parsesAs("cos(x)/e^(3t)", cosOverExponential);

		String oneOverCosPower = "(1)/(cos^(4)(x))";
		parsesAs("1/cos^(4)(x)", oneOverCosPower);
		parsesAs("1/cos" + Unicode.SUPERSCRIPT_4 + "(x)", oneOverCosPower);

		String cosSquaredTimesFraction = "cos^(2)(x)(sin(x))/(x)";
		parsesAs("cos^(2)(x)sin(x)/x", cosSquaredTimesFraction);
		parsesAs("cos" + Unicode.SUPERSCRIPT_2 + "(x)sin(x)/x", cosSquaredTimesFraction);

		String expArctan = "e^(tan^(-1)(1))";
		parsesAs("e^(tan^(-1)(1))", expArctan);
		parsesAs("e^(tan" + Unicode.SUPERSCRIPT_MINUS_ONE_STRING + "(1))", expArctan);

		String xToArcsin = "x^(sin^(-1)(x))";
		parsesAs("x^(sin^(-1)(x))", xToArcsin);
		parsesAs("x^(sin" + Unicode.SUPERSCRIPT_MINUS_ONE_STRING + "(x))", xToArcsin);
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
				|| op == Operation.DOLLAR_VAR_ROW || op == Operation.DOLLAR_VAR_ROW_COL;
	}
}
