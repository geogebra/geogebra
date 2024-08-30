package org.geogebra.common.kernel.commands.filter;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Filters out the commands that are not enabled in the exam mode
 */
public class ExamCommandArgumentFilter extends BaseCommandArgumentFilter {

	public ExamCommandArgumentFilter() {
		super(Commands.SetFixed, Commands.CopyFreeObject);
	}

	@Override
	public void checkAllowed(Command command, CommandProcessor commandProcessor) {
		if (!isFilteredCommand(command)) {
			return;
		}
		GeoElement[] arguments = commandProcessor.resArgs(command);
		if (arguments.length < 1) {
			return;
		}
		GeoElement firstArgument = arguments[0];
		boolean isSetFixed = isCommand(command, Commands.SetFixed);
		if (isSetFixed && firstArgument.isFunctionOrEquationFromUser()) {
			throw commandProcessor.argErr(command, firstArgument);
		}
		boolean isCopyFree = isCommand(command, Commands.CopyFreeObject);
		if (isCopyFree && (firstArgument instanceof EquationValue)) {
			throw commandProcessor.argErr(command, firstArgument);
		}
	}
}
