package org.geogebra.common.kernel.commands.selector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.geogebra.common.kernel.commands.Commands;

/**
 * Filters commands by their table name.
 */
public class CommandTableFilter implements CommandFilter {

	private Set<Integer> filteredTables;

	/**
	 * Creates a Command Table filter instance.
	 *
	 * @param filteredTables the tables that have to be filtered
	 */
	public CommandTableFilter(Integer... filteredTables) {
		this.filteredTables = new HashSet<>(Arrays.asList(filteredTables));
	}

	@Override
	public boolean isCommandAllowed(Commands command) {
		return !filteredTables.contains(command.getTable());
	}
}
