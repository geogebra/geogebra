package org.geogebra.common.kernel.commands;

import java.util.HashSet;
import java.util.Set;

public class CommandFilter {

    private Set<Commands> allowedCommands;

    public CommandFilter() {
        allowedCommands = new HashSet<>();
    }

    public void addAllowedCommands(Set<Commands> commands) {
        allowedCommands.addAll(commands);
    }

    public boolean isCommandAllowed(Commands command) {
        return allowedCommands.contains(command);
    }
}
