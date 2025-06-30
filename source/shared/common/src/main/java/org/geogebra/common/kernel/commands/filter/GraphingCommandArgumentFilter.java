package org.geogebra.common.kernel.commands.filter;

import static org.geogebra.common.kernel.commands.Commands.Invert;
import static org.geogebra.common.kernel.commands.Commands.Length;
import static org.geogebra.common.kernel.commands.Commands.Line;
import static org.geogebra.common.kernel.commands.Commands.PolyLine;
import static org.geogebra.common.kernel.commands.Commands.Polyline;

import java.util.Set;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandNotFoundError;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

public final class GraphingCommandArgumentFilter implements CommandArgumentFilter {
    private final Set<Commands> filteredCommands = Set.of(Line, Length, Polyline, PolyLine, Invert);

    private boolean isFilteredCommand(Command command) {
        return filteredCommands.stream().anyMatch(cmd -> cmd.name().equals(command.getName()));
    }

    private static boolean isCommand(Command command, Commands commandsValue) {
        return command.getName().equals(commandsValue.name());
    }

    @Override
    public void checkAllowed(Command command, CommandProcessor commandProcessor) {
        if (!isFilteredCommand(command)) {
            return;
        }
        GeoElement[] arguments = commandProcessor.resArgs(command);

        if (isCommand(command, Line)) {
            checkLine(command, arguments, commandProcessor);
        } else if (isCommand(command, Length)) {
            checkLength(command, arguments, commandProcessor);
        } else if (isCommand(command, PolyLine) || isCommand(command, Polyline)) {
            checkPolyline(command, arguments, commandProcessor);
        }
    }

    private void checkLine(Command command, GeoElement[] arguments,
            CommandProcessor commandProcessor) {
        if (arguments.length < 2) {
            return;
        }
        GeoElement firstArgument = arguments[0];
        GeoElement secondArgument = arguments[1];
        boolean secArgIsLineOrFunction =
                secondArgument.isGeoLine() || secondArgument.isGeoFunction();
        if (firstArgument.isGeoPoint() && secArgIsLineOrFunction) {
            throw commandProcessor.argErr(command, secondArgument);
        }
    }

    private void checkLength(Command command, GeoElement[] arguments,
                                            CommandProcessor commandProcessor) {
        if (arguments.length == 1) {
            GeoElement argument = arguments[0];
            boolean argIsListOrText = argument.isGeoList() || argument.isGeoText();

            if (!argIsListOrText) {
                throw commandProcessor.argErr(command, argument);
            }
        } else if (arguments.length > 1) {
            GeoElement secondArgument = arguments[1];
            throw commandProcessor.argErr(command, secondArgument);
        }
    }

    private void checkPolyline(
            Command command, GeoElement[] arguments, CommandProcessor commandProcessor) {
        if (arguments.length < 2) {
            throw new CommandNotFoundError(commandProcessor.getLocalization(), command);
        }

        GeoElement lastArgument = arguments[arguments.length - 1];
        if (!lastArgument.isGeoBoolean()) {
            throw new CommandNotFoundError(commandProcessor.getLocalization(), command);
        }

        if (arguments[0].isGeoList()) {
            if (arguments.length != 2) {
                throw new CommandNotFoundError(commandProcessor.getLocalization(), command);
            }
            GeoList points = (GeoList) arguments[0];
            for (int i = 0; i < points.size(); i++) {
                if (!points.get(i).isGeoPoint()) {
                    throw new CommandNotFoundError(commandProcessor.getLocalization(), command);
                }
            }
        } else {
            for (int i = 0; i < arguments.length - 1; i++) {
                if (!arguments[i].isGeoPoint()) {
                    throw new CommandNotFoundError(commandProcessor.getLocalization(), command);
                }
            }
        }
    }
}
