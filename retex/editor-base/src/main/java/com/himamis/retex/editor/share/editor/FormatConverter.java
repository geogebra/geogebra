package com.himamis.retex.editor.share.editor;

/**
 * @author michael
 *
 */
public interface FormatConverter {

	/**
	 * @param exp
	 *            expression in ggb, LaTeX or Presentation MathML format
	 * @return expression converted into simple ggb syntax if possible
	 */
	public String convert(String exp);

}
