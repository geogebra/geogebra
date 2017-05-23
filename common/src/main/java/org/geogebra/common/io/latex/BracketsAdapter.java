package org.geogebra.common.io.latex;

/**
 * Transforms brackets in expression
 *
 */
public class BracketsAdapter {

	/**
	 * @param left
	 *            left bracket (LaTeX)
	 * @param base
	 *            content
	 * @param right
	 *            right bracket (LaTeX)
	 * @return content wrapped in brackets
	 */
	public String transformBrackets(String left, String base, String right) {
		if ("[".equals(left) && base.contains("...")) {
			String[] parts = base.split(",");
			if (parts.length == 1) {
				parts = base.split("...");
			}
			return "(" + parts[0] + "..." + parts[parts.length - 1] + ")";
		}
		return left + base + right;
	}

}
