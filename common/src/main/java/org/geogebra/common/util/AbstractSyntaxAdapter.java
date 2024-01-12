package org.geogebra.common.util;

import org.geogebra.common.io.MathMLParser;

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
public abstract class AbstractSyntaxAdapter implements SyntaxAdapter {

	@Override
	public String convert(String exp) {
		// might start <math> or <mrow> etc
		if (exp.startsWith("<")) {
			return convertMathMLtoGGB(exp);
		} else if (mightBeLaTeXSyntax(exp)) {
			return convertLaTeXtoGGB(exp);
		} else if (checkClipboardFormat(exp)) {
			return "";
		}
		return exp;
	}

	/**
	 * Checks if text is in a special clipboard format, pastes objects from clipboard as side effect
	 * @param text inserted text
	 * @return whether text was in a special clipboard format
	 */
	protected boolean checkClipboardFormat(String text) {
		return false;
	}

	/**
	 * Like convert, but assumes the text is one of the math formats
	 * @param exp expression in MathML or LaTeX syntax
	 * @return expression in GGB syntax
	 */
	public String convertMath(String exp) {
		// might start <math> or <mrow> etc
		if (exp.startsWith("<")) {
			return convertMathMLtoGGB(exp);
		} else  {
			return convertLaTeXtoGGB(exp);
		}
	}

	protected String convertLaTeXtoGGB(String latexExpression) {
		TeXFormula tf = new TeXFormula(latexExpression);
		return new TeXAtomSerializer(null).serialize(tf.root);
	}

	private String convertMathMLtoGGB(String mathmlExpression) {
		MathMLParser mathmlParserGGB = new MathMLParser(true);
		return mathmlParserGGB.parse(mathmlExpression, false, true);
	}

	protected boolean mightBeLaTeXSyntax(String expression) {
		return StringUtil.containsLaTeX(expression);
	}
}
