package org.geogebra.common.main.syntax.suggestionfilter;

import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.filter.GraphingCommandArgumentFilter;

public class GraphingSyntaxFilter extends AbstractSyntaxFilter {

	@Override
	public String getFilteredSyntax(String internalCommandName, String syntax) {
		if (Commands.Line.name().equals(internalCommandName)) {
			return select(syntax, 0, 2);
		} else if (Commands.Length.name().equals(internalCommandName)) {
			return select(syntax, 0);
		} else if (Commands.Invert.name().equals(internalCommandName)) {
			return select(syntax, 0);
		}
		return syntax;
	}
}
