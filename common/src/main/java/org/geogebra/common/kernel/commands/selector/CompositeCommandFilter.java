package org.geogebra.common.kernel.commands.selector;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.commands.Commands;

/**
 * Makes a composition of CommandFilters. If any of the instances
 * filters a command, the composition will filter it as well.
 */
final class CompositeCommandFilter implements CommandFilter {

	private List<CommandFilter> filters;

	/**
	 * Create a CompositeCommandFilter.
	 *
	 * @param filters filters to combine
	 */
	CompositeCommandFilter(CommandFilter... filters) {
		this.filters = Arrays.asList(filters);
	}

	@Override
	public boolean isCommandAllowed(Commands command) {
		for (CommandFilter filter: filters) {
			if (!filter.isCommandAllowed(command)) {
				return false;
			}
		}
		return true;
	}
}
