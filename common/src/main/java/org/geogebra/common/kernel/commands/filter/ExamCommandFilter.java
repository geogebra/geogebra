package org.geogebra.common.kernel.commands.filter;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Filters out the commands that are not enabled in the exam mode
 */
public class ExamCommandFilter extends BaseCommandArgumentFilter {

	public ExamCommandFilter() {
		super(Commands.SetFixed, Commands.CopyFreeObject);
	}

    @Override
	public void checkAllowed(Command command,
			CommandProcessor commandProcessor) {
		if (!check(command, commandProcessor)) {
			return;
		}
		boolean setFixed = isCommand(command, Commands.SetFixed);
		boolean copyFree = isCommand(command, Commands.CopyFreeObject);

		GeoElement[] arguments = commandProcessor.resArgs(command);
		if (arguments.length < 1) {
			return;
		}
		GeoElement firstArgument = arguments[0];
		if (setFixed && firstArgument.isFunctionOrEquationFromUser()) {
			throw commandProcessor.argErr(command, firstArgument);
		}
		if (copyFree && (firstArgument instanceof EquationValue)) {
			throw commandProcessor.argErr(command, firstArgument);
		}
    }
}
