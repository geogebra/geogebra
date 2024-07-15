package org.geogebra.common.exam.restrictions.cvte;

import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.main.syntax.suggestionfilter.LineSelector;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;

/**
 * Additional syntax restrictions for CvTE exams.
 */
public final class CvteSyntaxFilter implements SyntaxFilter {
	@Override
	public String getFilteredSyntax(String internalCommandName, String syntax) {
		if (Commands.Circle.name().equals(internalCommandName)) {
			// allow only Circle(<Center>, <Radius>)
			return LineSelector.select(syntax, 0);
		} else if (Commands.Extremum.name().equals(internalCommandName)) {
			// allow only Extremum(<Function>, <Start x-Value>, <End x-Value>)
			return LineSelector.select(syntax, 1);
		} else if (Commands.Root.name().equals(internalCommandName)) {
			// allow only Root( <Function>, <Start x-Value>, <End x-Value> )
			return LineSelector.select(syntax, 2);
		}
		return syntax;
	}
}
