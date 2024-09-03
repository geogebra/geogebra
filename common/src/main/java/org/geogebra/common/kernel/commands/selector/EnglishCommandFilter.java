package org.geogebra.common.kernel.commands.selector;

import org.geogebra.common.kernel.commands.Commands;

/**
 * Wraps a CommandFilter and filters both the internal versions and 'english' version
 * of commands that are filtered by the wrapped filter.
 * @see Commands#englishToInternal(Commands)
 */
public class EnglishCommandFilter implements CommandFilter {

	private final CommandFilter wrappedFilter;

	/**
	 * Creates a new EnglishCommandFilter.
	 * @param wrappedFilter filter to wrap
	 */
	public EnglishCommandFilter(CommandFilter wrappedFilter) {
		this.wrappedFilter = wrappedFilter;
	}

	@Override
	public boolean isCommandAllowed(Commands command) {
		return wrappedFilter.isCommandAllowed(command)
				&& wrappedFilter.isCommandAllowed(Commands.englishToInternal(command));
	}
}
