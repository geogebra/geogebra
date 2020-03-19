package org.geogebra.common.kernel.commands.filter;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;

public abstract class DefaultCommandArgumentFilter implements CommandArgumentFilter {

    private Commands[] commands;

    DefaultCommandArgumentFilter(Commands... commands) {
        this.commands = commands;
    }

    protected boolean check(Command command, CommandProcessor commandProcessor) {
        if (commandProcessor == null) {
            return false;
        }
        for (Commands cmd: commands) {
            if (isCommand(command, cmd)) {
                return true;
            }
        }
        return false;
    }

    static boolean isCommand(Command command, Commands cmdName) {
        return cmdName.name().equals(command.getName());
    }
}
