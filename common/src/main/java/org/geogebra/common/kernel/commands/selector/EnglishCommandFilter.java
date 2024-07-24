package org.geogebra.common.kernel.commands.selector;

import org.geogebra.common.kernel.commands.Commands;

/**
 * Wraps a CommandFilter and filters the internal versions
 * of commands that are filtered by the wrapped filter.
 */
public class EnglishCommandFilter implements CommandFilter {

	private final CommandFilter wrappedFilter;

	/**
	 * Creates a new EnglishCommandFilter.
	 *
	 * @param wrappedFilter filter to wrap
	 */
	public EnglishCommandFilter(CommandFilter wrappedFilter) {
		this.wrappedFilter = wrappedFilter;
	}

	@Override
	public boolean isCommandAllowed(Commands command) {
		Commands internal = Commands.englishToInternal(command);
		return wrappedFilter.isCommandAllowed(internal);
	}
}
