package org.geogebra.common.kernel.commands.filter;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;

public class ScientificCommandArgumentFilter extends BaseCommandArgumentFilter {

	public ScientificCommandArgumentFilter() {
		super(Commands.BinomialDist, Commands.Normal);
	}

	@Override
	public void checkAllowed(Command command,
			CommandProcessor commandProcessor) {
		if (!isFilteredCommand(command)) {
			return;
		}
		GeoElement[] arguments = commandProcessor.resArgs(command);

		if (isCommand(command, Commands.BinomialDist)) {
			checkBinomial(command, arguments, commandProcessor);
		} else if (isCommand(command, Commands.Normal)) {
			checkNormal(command, arguments, commandProcessor);
		}
	}

	private boolean isCommand(Command command, Commands commandsValue) {
		return command.getName().equals(commandsValue.name());
	}

	private void checkBinomial(Command command, GeoElement[] arguments,
			CommandProcessor commandProcessor) {

		if (arguments.length == 3 || arguments.length == 4) {
				return;
		} else {
			throw commandProcessor.argNumErr(command, arguments.length);
		}
	}

	private void checkNormal(Command command, GeoElement[] arguments,
			CommandProcessor commandProcessor) {

		if (arguments.length == 3) {
			return;
		} else if (arguments.length == 4) {
			GeoElement thirdArgument = arguments[2];
			if (!(thirdArgument instanceof GeoNumberValue)) {
				throw commandProcessor.argErr(command, thirdArgument);
			} else {
				return;
			}
		}

		throw commandProcessor.argNumErr(command, arguments.length);
	}
}
