package org.geogebra.common.kernel.commands.filter;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;

public class GraphingCommandArgumentFilter implements CommandArgumentFilter {
    @Override
    public void checkAllowed(Command command,
                             CommandProcessor commandProcessor) {
        boolean line = isCommand(command, Commands.Line);
        if (!line || commandProcessor == null) {
            return;
        }
        GeoElement[] arguments = commandProcessor.resArgs(command);
        if (arguments.length < 2) {
            return;
        }
        GeoElement firstArgument = arguments[0];
        GeoElement secondArgument = arguments[1];
        if (firstArgument.isGeoPoint() && secondArgument.isGeoLine()) {
            throw commandProcessor.argErr(command, secondArgument);
        }
    }

    private static boolean isCommand(Command command, Commands cmdName) {
        return cmdName.name().equals(command.getName());
    }
}
