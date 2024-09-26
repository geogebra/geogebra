package org.geogebra.common.kernel.commands.filter;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;

public abstract class BaseCommandArgumentFilter implements CommandArgumentFilter {

    private Commands[] commands;

    public BaseCommandArgumentFilter(Commands... commands) {
        this.commands = commands;
    }

    protected boolean check(Command command, CommandProcessor commandProcessor) {
        if (commandProcessor == null) {
            return false;
        }
        return isFilteredCommand(command.getName());
    }

    @Override
    public boolean isFilteredCommand(String commandName) {
        for (Commands cmd: commands) {
            if (cmd.name().equals(commandName)) {
                return true;
            }
        }
        return false;
    }

    protected static boolean isCommand(Command command, Commands cmdName) {
        return cmdName.name().equals(command.getName());
    }
}
