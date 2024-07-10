package org.geogebra.common.exam.restrictions;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.filter.BaseCommandArgumentFilter;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.syntax.suggestionfilter.LineSelector;

final class CvteCommandArgumentFilter implements CommandArgumentFilter {

	@Override
	public void checkAllowed(Command command, CommandProcessor commandProcessor)
			throws MyError {
		String internalCommandName = command.getName();
		if (Commands.Circle.name().equals(internalCommandName)) {
			checkCircle(command, commandProcessor);
		} else if (Commands.Extremum.name().equals(internalCommandName)) {
//		only Extremum(<Function>, <Start x-Value>, <End x-Value>) allowed - remove other syntaxes
		} else if (Commands.Root.name().equals(internalCommandName)) {
//		only Root( <Function>, <Start x-Value>, <End x-Value> ) allowed - remove other syntaxes
		}
	}

	private void checkCircle(Command command, CommandProcessor commandProcessor) throws MyError {
		GeoElement[] arguments = commandProcessor.resArgs(command);
		// only Circle(<Center>, <Radius>) allowed
		if (arguments.length != 2) {
			throw commandProcessor.argNumErr(command, arguments.length);
		}
		GeoElement firstArgument = arguments[0];
		GeoElement secondArgument = arguments[1];
		if (!firstArgument.isGeoPoint()) {
			throw commandProcessor.argErr(command, firstArgument);
		}
		if (!secondArgument.isNumberValue()) {
			throw commandProcessor.argErr(command, secondArgument);
		}
	}
}