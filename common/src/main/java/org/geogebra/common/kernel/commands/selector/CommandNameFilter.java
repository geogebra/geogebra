package org.geogebra.common.kernel.commands.selector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.util.debug.Log;

/**
 * Filters commands by their name.
 */
public class CommandNameFilter implements CommandFilter {

    private Set<Commands> allowedCommands;
	private boolean inverse;

	/**
	 * New command filter
	 * 
	 * @param inverse
	 *            whether to invert selection
	 */
	public CommandNameFilter(boolean inverse) {
        allowedCommands = new HashSet<>();
		this.inverse = inverse;
    }

	/**
	 * Create a new command filter.
	 *
	 * @param inverse Pass true to invert the selection.
	 * @param commands The list of allowed commands.
	 */
	public CommandNameFilter(boolean inverse, Commands... commands) {
		this.inverse = inverse;
		allowedCommands = Set.of(commands);
	}

	/**
	 * @param commands
	 *            allowed commands
	 */
	public void addCommands(Commands... commands) {
        allowedCommands.addAll(Arrays.asList(commands));
    }

    @Override
    public boolean isCommandAllowed(Commands command) {
		return inverse ^ allowedCommands.contains(command);
    }

	/**
	 *
	 * @param commands names that allowed
	 */
	public void addCommandsByName(String[] commands) {
		for (String commandName: commands) {
			try {
				Commands command = Commands.valueOf(commandName);
				allowedCommands.add(command);
			} catch (Exception e) {
				Log.debug("No such command: " + commandName);
			}
		}
	}
}
