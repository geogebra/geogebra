package org.geogebra.common.main.syntax.suggestionfilter;

import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.filter.ScientificCommandArgumentFilter;

public class ScientificSyntaxFilter extends AbstractSyntaxFilter {

	@Override
	public String getFilteredSyntax(String internalCommandName, String syntax) {
		if (Commands.Normal.name().equals(internalCommandName)) {
			return select(syntax, 0, 1);
		} else if (Commands.BinomialDist.name().equals(internalCommandName)) {
			return select(syntax, 1, 2, 3);
		}
		return syntax;
	}
}