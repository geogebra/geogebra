package org.geogebra.common.main.syntax.suggestionfilter;

import org.geogebra.common.kernel.commands.Commands;

public class GraphingSyntaxFilter extends LineSelectorSyntaxFilter {

	/***/
	public GraphingSyntaxFilter() {
		addSelector(Commands.Line, 0, 2);
		addSelector(Commands.Length, 0);
		addSelector(Commands.Invert, 0);
		addSelector(Commands.Function, 0);
	}
}
