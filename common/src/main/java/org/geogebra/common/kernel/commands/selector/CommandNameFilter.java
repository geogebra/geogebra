package org.geogebra.common.kernel.commands.selector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.geogebra.common.kernel.commands.Commands;

/**
 * Filters commands by their internal name.
 */
public final class CommandNameFilter implements CommandFilter {

	private Set<Commands> allowedCommands;
	private boolean inverse;

	/**
	 * New command filter
	 * @param inverse whether to invert selection
	 */
	public CommandNameFilter(boolean inverse) {
		allowedCommands = new HashSet<>();
		this.inverse = inverse;
	}

	/**
	 * Create a new command filter.
	 * @param inverse Pass true to invert the selection.
	 * @param commands The list of allowed commands.
	 */
	public CommandNameFilter(boolean inverse, Commands... commands) {
		this.inverse = inverse;
		allowedCommands = new HashSet(convertToInternal(commands));
	}

	/**
	 * @param commands allowed commands
	 */
	public void addCommands(Commands... commands) {
		allowedCommands.addAll(convertToInternal(commands));
	}

	private List<Commands> convertToInternal(Commands[] commands) {
		return Arrays.stream(commands).map(Commands::englishToInternal)
				.collect(Collectors.toList());
	}

	@Override
	public boolean isCommandAllowed(Commands command) {
		boolean containsCommand = allowedCommands.contains(Commands.englishToInternal(command));
		return inverse ^ containsCommand;
	}
}
