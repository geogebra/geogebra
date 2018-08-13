package org.geogebra.common.kernel.commands.filter;

import org.geogebra.common.kernel.commands.Commands;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * CommandFilter interface implemented using HashSet<Commands>
 */
class CommandFilterSet implements CommandFilter {

    private Set<Commands> allowedCommands;

    CommandFilterSet() {
        allowedCommands = new HashSet<>();
    }

    void addAllowedCommands(Commands... commands) {
        allowedCommands.addAll(Arrays.asList(commands));
    }

    @Override
    public boolean isCommandAllowed(Commands command) {
        return allowedCommands.contains(command);
    }
}
