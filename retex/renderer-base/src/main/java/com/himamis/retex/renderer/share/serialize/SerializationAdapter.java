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

	String nroot(String serialize, String serialize1);

	String parenthesis(String paren);

	String getLigature(String toString);

	String convertToReadable(String s);

	/**
	 * Serialize matrix in brackets to string
	 * @param left left bracket
	 * @param base matrix content
	 * @param right right bracket
	 * @return serialization
	 */
	default String transformMatrix(String left, String base, String right) {
		if ("|".equals(left) && "|".equals(right)) {
			return "Determinant(" + base + ")";
		}
		return base;
	}
}
