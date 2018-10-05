package org.geogebra.common.kernel.commands.filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.geogebra.common.kernel.commands.Commands;

/**
 * CommandFilter interface implemented using HashSet&lt;Commands&gt;
 */
class CommandFilterSet implements CommandFilter {

    private Set<Commands> allowedCommands;

	/**
	 * New command filter
	 */
    CommandFilterSet() {
        allowedCommands = new HashSet<>();
    }

	/**
	 * @param commands
	 *            allowed commands
	 */
    void addAllowedCommands(Commands... commands) {
        allowedCommands.addAll(Arrays.asList(commands));
    }

    @Override
    public boolean isCommandAllowed(Commands command) {
        return allowedCommands.contains(command);
    }
}
