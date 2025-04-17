package org.geogebra.common.util;

public interface LaTeXCache {
	/**
	 * Remove cached formula
	 */
	void remove();

	/**
	 * @param content formula text
	 * @param fontStyle font style
	 * @param fontSize font size
	 * @return key for LaTeX cache
	 */
	Object getCachedLaTeXKey(String content, int fontSize, int fontStyle, Object color);
}
