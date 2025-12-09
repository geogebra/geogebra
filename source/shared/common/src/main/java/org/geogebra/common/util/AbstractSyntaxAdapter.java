/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
