package org.geogebra.common.kernel.commands.filter;

import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Filters out the commands that are not enabled in the exam mode
 */
public class ExamCommandFilter implements CommandFilter {

    @Override
    public boolean isAllowed(Command command, CommandProcessor commandProcessor) {
        if (!isSetFixed(command)) {
            return true;
        }
        if (commandProcessor == null) {
            return false;
        }
        GeoElement[] arguments = commandProcessor.resArgs(command);
        GeoElement firstArgument = arguments[0];
        return !firstArgument.isGeoFunction() && !AlgebraItem.isEquationFromUser(firstArgument);
    }

    private boolean isSetFixed(Command command) {
        return Commands.valueOf(command.getName()) == Commands.SetFixed;
    }
}
