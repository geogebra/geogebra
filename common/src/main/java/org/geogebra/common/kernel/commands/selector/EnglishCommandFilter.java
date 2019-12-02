package org.geogebra.common.kernel.commands.selector;

import org.geogebra.common.kernel.commands.Commands;

/**
 * Wraps a CommandFilter and filters the internal versions
 * of commands that are filtered by the wrapped filter.
 */
public class EnglishCommandFilter implements CommandFilter {

	private CommandFilter wrappedFilter;

	/**
	 * Creates a new EnglishCommandFilter.
	 *
	 * @param wrappedFilter filter to wrap
	 */
	EnglishCommandFilter(CommandFilter wrappedFilter) {
		this.wrappedFilter = wrappedFilter;
	}

	@Override
	public boolean isCommandAllowed(Commands command) {
		boolean allowed = wrappedFilter.isCommandAllowed(command);

		Commands internal = Commands.englishToInternal(command);
		boolean internalAllowed = wrappedFilter.isCommandAllowed(internal);
		return allowed && internalAllowed;
	}
}
