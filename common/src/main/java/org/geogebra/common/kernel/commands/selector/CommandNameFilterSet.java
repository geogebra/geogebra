package org.geogebra.common.kernel.commands.selector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.geogebra.common.kernel.commands.Commands;

/**
 * CommandNameFilter interface implemented using HashSet&lt;Commands&gt;
 */
class CommandNameFilterSet implements CommandNameFilter {

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
}
