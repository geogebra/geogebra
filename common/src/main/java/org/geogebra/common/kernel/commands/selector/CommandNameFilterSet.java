package org.geogebra.common.kernel.commands.selector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.util.debug.Log;

/**
 * CommandFilter interface implemented using HashSet&lt;Commands&gt;
 */
class CommandNameFilterSet implements CommandFilter {

    private Set<Commands> allowedCommands;
	private boolean inverse;

	/**
	 * New command filter
	 * 
	 * @param inverse
	 *            whether to invert selection
	 */
	CommandNameFilterSet(boolean inverse) {
        allowedCommands = new HashSet<>();
		this.inverse = inverse;
    }

	/**
	 * @param commands
	 *            allowed commands
	 */
    void addCommands(Commands... commands) {
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
