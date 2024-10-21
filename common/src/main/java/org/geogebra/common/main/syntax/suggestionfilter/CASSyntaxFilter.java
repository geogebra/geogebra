package org.geogebra.common.main.syntax.suggestionfilter;

import org.geogebra.common.kernel.commands.Commands;

public final class CASSyntaxFilter extends LineSelectorSyntaxFilter {

	public CASSyntaxFilter() {
		addSelector(Commands.Distance, 0, 1);
	}
}
