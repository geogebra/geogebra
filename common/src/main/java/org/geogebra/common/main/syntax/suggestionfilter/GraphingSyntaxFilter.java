package org.geogebra.common.main.syntax.suggestionfilter;

import org.geogebra.common.kernel.commands.Commands;

public class GraphingSyntaxFilter implements SyntaxFilter {

	@Override
	public String getFilteredSyntax(String internalCommandName, String syntax) {
		if (Commands.Line.name().equals(internalCommandName)) {
			return LineSelector.select(syntax, 0, 2);
		} else if (Commands.Length.name().equals(internalCommandName)) {
			return LineSelector.select(syntax, 0);
		} else if (Commands.Invert.name().equals(internalCommandName)) {
			return LineSelector.select(syntax, 0);
		}
		return syntax;
	}
}
