package org.geogebra.common.main.syntax.suggestionfilter;

import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.filter.GraphingCommandArgumentFilter;

public class GraphingSyntaxFilter implements SyntaxFilter {

	private CommandArgumentFilter argumentFilter = new GraphingCommandArgumentFilter();
	private LineSelector lineSelector = new LineSelector();

	@Override
	public String getFilteredSyntax(String commandName, String syntax) {
		if (!argumentFilter.isFilteredCommand(commandName)) {
			return syntax;
		}
		String[] syntaxArray = syntax.split("\n");
		if (Commands.Line.name().equals(commandName)) {
			return lineSelector.select(syntaxArray, 0, 2);
		} else if (Commands.Length.name().equals(commandName)) {
			return lineSelector.select(syntaxArray, 0);
		} else if (Commands.PolyLine.name().equals(commandName)) {
			return "";
		}
		return syntax;
	}
}
