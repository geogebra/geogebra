package org.geogebra.common.exam.restrictions.ib;

import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.main.syntax.suggestionfilter.LineSelector;
import org.geogebra.common.main.syntax.suggestionfilter.SyntaxFilter;

/**
 * Additional syntax restrictions for the IB exam
 */
public final class IBSyntaxFilter implements SyntaxFilter {

	@Override
	public String getFilteredSyntax(String internalCommandName, String syntax) {
		if (Commands.Integral.name().equals(internalCommandName)) {
			return LineSelector.select(syntax, 2);
		} else if (Commands.Invert.name().equals(internalCommandName)) {
			return LineSelector.select(syntax, 0);
		} else if (Commands.Tangent.name().equals(internalCommandName)) {
			return LineSelector.select(syntax, 1, 3);
		}
		return syntax;
	}
}
