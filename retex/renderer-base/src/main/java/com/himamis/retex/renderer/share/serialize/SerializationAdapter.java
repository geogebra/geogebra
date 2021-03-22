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
}
