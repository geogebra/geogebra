package org.geogebra.common.kernel.commands.filter;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.main.MyError;

public class CASCommandArgumentFilter extends BaseCommandArgumentFilter {

	public CASCommandArgumentFilter() {
		super(Commands.If);
	}

	@Override
	public void checkAllowed(Command command, CommandProcessor commandProcessor) throws MyError {
		if (!isFilteredCommand(command)) {
			return;
		}
		if (command.getArgumentNumber() < 4) {
			return;
		}
		throw commandProcessor.argNumErr(command, 4);
	}
}
