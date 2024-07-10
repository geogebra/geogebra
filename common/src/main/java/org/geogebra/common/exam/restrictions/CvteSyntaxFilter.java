package org.geogebra.common.exam.restrictions;

import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.main.syntax.suggestionfilter.LineSelector;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;

/**
 * Additional syntax restrictions for CvTE exams.
 */
final class CvteSyntaxFilter implements SyntaxFilter {
	@Override
	public String getFilteredSyntax(String commandName, String syntax) {
		if (Commands.Circle.name().equals(commandName)) {
			return LineSelector.select(syntax.split("\n"), 0);
		}
		// TODO
//		only Extremum(<Function>, <Start x-Value>, <End x-Value>) allowed - remove other syntaxes
//		only Root( <Function>, <Start x-Value>, <End x-Value> ) allowed - remove other syntaxes
		return syntax;
	}
}
