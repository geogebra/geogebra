package org.geogebra.common.exam.restrictions.ib;

import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.main.syntax.suggestionfilter.LineSelectorSyntaxFilter;

/**
 * Additional syntax restrictions for the IB exam
 */
public final class IBSyntaxFilter extends LineSelectorSyntaxFilter {

	/**
	 * Create the filter.
	 */
	public IBSyntaxFilter() {
		addSelector(Commands.Integral, 2);
		addSelector(Commands.Invert, 0);
		addSelector(Commands.Tangent, 1, 3);
	}
}
