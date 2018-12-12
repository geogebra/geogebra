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
	public void checkAllowed(Command command,
			CommandProcessor commandProcessor) {
		if (!isSetFixed(command) || commandProcessor == null) {
			return;
		}
		GeoElement[] arguments = commandProcessor.resArgs(command);
		GeoElement firstArgument = arguments[0];
		if (firstArgument.isGeoFunction()
				|| AlgebraItem.isEquationFromUser(firstArgument)) {
			throw commandProcessor.argErr(command, firstArgument);
		}
    }

	private static boolean isSetFixed(Command command) {
		return Commands.SetFixed.name().equals(command.getName());
    }
}
