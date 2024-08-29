package org.geogebra.common.kernel.commands.filter;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.main.MyError;

/**
 * Filters out commands that are not allowed in certain contexts (apps).
 */
public interface CommandArgumentFilter {

    /**
	 * @param command
	 *            the command that should be allowed or not
	 * @param commandProcessor
	 *            makes it possible to check the argument list of the command
	 * @throws MyError
	 *             if the command (with the given arguments) is not allowed
	 */
	void checkAllowed(Command command, CommandProcessor commandProcessor)
			throws MyError;
}
