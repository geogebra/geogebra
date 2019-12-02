package org.geogebra.common.kernel.commands.selector;

import org.geogebra.common.kernel.commands.Commands;

/**
 * If the CommandDispatcher has a CommandFilter then only those commands are
 * accepted in the CommandDispatcher which are allowed by the filter
 */
public interface CommandFilter {

    /**
	 * @param command
	 *            command
	 * @return Returns true if the command is allowed, otherwise false
	 */
    boolean isCommandAllowed(Commands command);
}
