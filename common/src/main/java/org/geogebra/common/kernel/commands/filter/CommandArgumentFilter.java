package org.geogebra.common.kernel.commands.filter;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.main.MyError;

/**
 * Filters out commands that are not allowed
 */
public interface CommandArgumentFilter {

    /**
	 * @param command
	 *            the command that should be allowed or not
	 * @param commandProcessor
	 *            makes it possible to check the argument list of the command
	 * @throws MyError
	 *             if the command is allowed otherwise false
	 */
	void checkAllowed(Command command, CommandProcessor commandProcessor)
			throws MyError;

	/**
	 * @param commandName command name
	 * @return True if the command with the name commandName is one of the filtered commands
	 */
	boolean isFilteredCommand(String commandName);
}
