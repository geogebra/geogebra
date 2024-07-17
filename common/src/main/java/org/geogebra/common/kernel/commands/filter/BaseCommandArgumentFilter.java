package org.geogebra.common.kernel.commands.filter;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.Commands;

public abstract class BaseCommandArgumentFilter implements CommandArgumentFilter {

    private final Commands[] filteredCommands;

    /**
     * @param commands A list of commands that are not allowed.
     */
    public BaseCommandArgumentFilter(Commands... commands) {
        this.filteredCommands = commands;
    }

    protected boolean isFilteredCommand(Command command) {
        return isFilteredCommand(command.getName());
    }

    protected boolean isFilteredCommand(String internalCommandName) {
        for (Commands cmd: filteredCommands) {
            if (cmd.name().equals(internalCommandName)) {
                return true;
            }
        }
        return false;
    }
}
