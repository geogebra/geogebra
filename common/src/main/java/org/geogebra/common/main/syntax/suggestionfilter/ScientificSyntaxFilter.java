package org.geogebra.common.main.syntax.suggestionfilter;

import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.filter.ScientificCommandArgumentFilter;

public class ScientificSyntaxFilter implements SyntaxFilter {

	private CommandArgumentFilter argumentFilter = new ScientificCommandArgumentFilter();
	private LineSelector lineSelector = new LineSelector();

	@Override
	public String getFilteredSyntax(String commandName, String syntax) {
		if (!argumentFilter.isFilteredCommand(commandName)) {
			return syntax;
		}
		String[] syntaxArray = syntax.split("\n");
		if (Commands.Normal.name().equals(commandName)) {
			return lineSelector.select(syntaxArray, 0, 1);
		} else if (Commands.BinomialDist.name().equals(commandName)) {
			return lineSelector.select(syntaxArray, 1, 2, 3);
		}
		return syntax;
	}
}