package com.himamis.retex.renderer.share.serialize;

public interface BracketsAdapterI {

	String subscriptContent(String sub);

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

}
