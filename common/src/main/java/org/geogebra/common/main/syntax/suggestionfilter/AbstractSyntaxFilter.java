package org.geogebra.common.main.syntax.suggestionfilter;

public abstract class AbstractSyntaxFilter implements SyntaxFilter {

	protected String select(String syntax, Integer... indices) {
		String[] syntaxArray = syntax.split("\n");
		return LineSelector.select(syntaxArray, indices);
	}
}
