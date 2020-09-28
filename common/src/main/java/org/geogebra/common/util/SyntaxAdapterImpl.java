package org.geogebra.common.util;

import org.geogebra.common.io.MathMLParser;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.plugin.Operation;

import com.himamis.retex.editor.share.editor.SyntaxAdapter;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.serialize.TeXAtomSerializer;

/**
 * Convert expressions from Presentation MathML / LaTeX to simple ggb syntax
 * when pasting into the editor eg \sqrt{\frac{x}{2}} -> sqrt(x/2)
 * 
 * <mrow><mi> x</mi><mo> +</mo><mrow><mi> 1</mi><mo>/</mo>
 * <mi> 2</mi></mrow></mrow> -> x+1/2
 * 
 * @author michael
 *
 */
public class SyntaxAdapterImpl implements SyntaxAdapter {

	private Kernel kernel;

	/**
	 * @param kernel
	 *            Kernel
	 */
	public SyntaxAdapterImpl(Kernel kernel) {
		this.kernel = kernel;
	}

	private boolean mightBeLaTeXSyntax(String expression) {
		try {
			kernel.getAlgebraProcessor()
					.getValidExpressionNoExceptionHandling(expression);
			String[] parts = expression.split("\\\\");
			// a\b is set difference: allow it
			for (int i = 1; i < parts.length; i++) {
				String command = parts[i].contains(" ")
						? parts[i].substring(0, parts[i].indexOf(' ')) : parts[i];
				if (kernel.lookupLabel(command) == null) {
					return true;
				}
			}
			// parses OK as GGB, not LaTeX
			return false;
		} catch (Throwable e) {
			// fall through
		}

		return StringUtil.containsLaTeX(expression);
	}

	private String convertLaTeXtoGGB(String latexExpression) {
		kernel.getApplication().getDrawEquation()
				.checkFirstCall(kernel.getApplication());
		TeXFormula tf = new TeXFormula(latexExpression);
		// TeXParser tp = new TeXParser(latexExpression, tf);
		// tp.parse();
		return new TeXAtomSerializer(null).serialize(tf.root);
	}

	private String convertMathMLoGGB(String mathmlExpression) {
		MathMLParser mathmlParserGGB = new MathMLParser(true);
		return mathmlParserGGB.parse(mathmlExpression, false, true);
	}

	@Override
	public String convert(String exp) {
		// might start <math> or <mrow> etc
		if (exp.startsWith("<")) {
			return convertMathMLoGGB(exp);
		} else if (mightBeLaTeXSyntax(exp)) {
			return convertLaTeXtoGGB(exp);
		}
		return exp;
	}

	@Override
	public boolean isFunction(String casName) {
		Operation operation = kernel.getApplication().getParserFunctions(true).get(casName, 1);
		return operation != null && casName.length() > 1;
	}

}
