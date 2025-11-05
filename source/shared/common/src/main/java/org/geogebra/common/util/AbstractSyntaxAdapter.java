package org.geogebra.common.util;

import org.geogebra.common.io.MathMLParser;
import org.geogebra.editor.share.editor.SyntaxAdapter;

import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.serialize.TeXAtomSerializer;

/**
 * Convert expressions from Presentation MathML / LaTeX to simple ggb syntax
 * when pasting into the editor eg
 * <ul>
 * <li>\sqrt{\frac{x}{2}} -&gt; sqrt(x/2)
 * <li>
 * &lt;mrow&gt;&lt;mi&gt; x&lt;/mi&gt;&lt;mo&gt;
 * +&lt;/mo&gt;&lt;mrow&gt;&lt;mi&gt; 1&lt;/mi&gt;&lt;mo&gt;/&lt;/mo&gt;
 * &lt;mi&gt; 2&lt;/mi&gt;&lt;/mrow&gt;&lt;/mrow&gt; -&gt; x+1/2
 * </ul>
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

	/**
	 * @param latexExpression expression in LaTeX format
	 * @return expression in AsciiMath / GeoGebra input format
	 */
	public String convertLaTeXtoGGB(String latexExpression) {
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
