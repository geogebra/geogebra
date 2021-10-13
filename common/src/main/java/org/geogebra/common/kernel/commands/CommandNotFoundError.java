package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;

public class CommandNotFoundError extends MyError {

	/**
	 * @param loc localization
	 * @param command command
	 */
	public CommandNotFoundError(Localization loc, Command command) {
		super(loc, loc.getError("UnknownCommand") + " : "
						+ loc.getCommand(command.getName()), Errors.UnknownCommand);
		commandName = command.getName();
	}
}
