package org.geogebra.common.main.syntax.suggestionfilter;

import org.geogebra.common.kernel.commands.Commands;

public class ScientificSyntaxFilter implements SyntaxFilter {

	@Override
	public String getFilteredSyntax(String internalCommandName, String syntax) {
		if (Commands.Normal.name().equals(internalCommandName)) {
			return LineSelector.select(syntax, 0, 1);
		} else if (Commands.BinomialDist.name().equals(internalCommandName)) {
			return LineSelector.select(syntax, 1, 2, 3);
		}
		return syntax;
	}
}