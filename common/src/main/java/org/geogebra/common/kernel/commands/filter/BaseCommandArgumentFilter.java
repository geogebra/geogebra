package org.geogebra.common.kernel.commands.filter;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;

public abstract class BaseCommandArgumentFilter implements CommandArgumentFilter {

    private Commands[] filteredCommands;

    /**
     * @param commands A list of commands that are not allowed.
     */
    public BaseCommandArgumentFilter(Commands... commands) {
        this.filteredCommands = commands;
    }

    protected boolean isFilteredCommand(Command command) {
        return isFilteredCommand(command.getName());
    }

    @Override
    public boolean isFilteredCommand(String commandName) {
        for (Commands cmd: filteredCommands) {
            if (cmd.name().equals(commandName)) {
                return true;
            }
        }
        return false;
    }
}
