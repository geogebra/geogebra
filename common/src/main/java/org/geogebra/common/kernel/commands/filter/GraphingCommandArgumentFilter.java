package org.geogebra.common.kernel.commands.filter;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;

public class GraphingCommandArgumentFilter extends DefaultCommandArgumentFilter {

    public GraphingCommandArgumentFilter() {
        super(Commands.Line, Commands.Length);
    }

    @Override
    public void checkAllowed(Command command,
                             CommandProcessor commandProcessor) {
        if (!check(command, commandProcessor)) {
            return;
        }
        GeoElement[] arguments = commandProcessor.resArgs(command);

        checkAllowedLineCommands(command, arguments, commandProcessor);
        checkAllowedLengthCommands(command, arguments, commandProcessor);
    }

    private void checkAllowedLineCommands(Command command, GeoElement[] arguments,
                                          CommandProcessor commandProcessor) {
        if (!command.getName().equals(Commands.Line.name())) {
            return;
        }
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

    private void checkAllowedLengthCommands(Command command, GeoElement[] arguments,
                                            CommandProcessor commandProcessor) {
        if (!command.getName().equals(Commands.Length.name())) {
            return;
        }
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
}
