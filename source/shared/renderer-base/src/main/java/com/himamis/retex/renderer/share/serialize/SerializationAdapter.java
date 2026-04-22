package com.himamis.retex.renderer.share.serialize;

public interface SerializationAdapter {

	String subscriptContent(String base, String sub, String sup);

	/**
	 * @param left
	 *            left bracket (LaTeX)
	 * @param base
	 *            content
	 * @param right
	 *            right bracket (LaTeX)
	 * @return content wrapped in brackets
	 */
	String transformBrackets(String left, String base, String right);

	String sqrt(String base);

	String convertCharacter(char character);

	String fraction(String numerator, String denominator);

	String nroot(String base, String root);

	String parenthesis(String paren);

	String getLigature(String toString);

	String convertToReadable(String s);

	/**
	 * Screen reader may need to handle symbols wrapped in ResizeAtom differently
	 * @param baseString serialized content of the wrapper atom
	 * @return baseString with optional prefix/suffix
	 */
	default String transformWrapper(String baseString) {
		return baseString;
	}

	TableAdapter getTableAdapter();

	String segment(String base);

	String vector(String content);

	String circled(String serialize);

	String under(String decoration, String base);

	String over(String decoration, String base);

	String blank();

	String operatorFromTo(String operator, String from, String to);

	String hyperbolic(String baseName);
}
