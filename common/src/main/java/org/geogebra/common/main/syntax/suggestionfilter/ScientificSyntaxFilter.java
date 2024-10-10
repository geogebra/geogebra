package org.geogebra.common.main.syntax.suggestionfilter;

import org.geogebra.common.kernel.commands.Commands;

public final class ScientificSyntaxFilter extends LineSelectorSyntaxFilter {

	/***/
	public ScientificSyntaxFilter() {
		addSelector(Commands.Normal, 0, 1);
		addSelector(Commands.BinomialDist, 1, 2, 3);
	}
}