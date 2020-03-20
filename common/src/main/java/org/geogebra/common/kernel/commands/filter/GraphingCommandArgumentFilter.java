package org.geogebra.common.kernel.commands.filter;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;

public class GraphingCommandArgumentFilter extends DefaultCommandArgumentFilter {

    public GraphingCommandArgumentFilter() {
        super(Commands.Line);
    }

    @Override
    public void checkAllowed(Command command,
                             CommandProcessor commandProcessor) {
        if (!check(command, commandProcessor)) {
            return;
        }
        GeoElement[] arguments = commandProcessor.resArgs(command);
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
}
