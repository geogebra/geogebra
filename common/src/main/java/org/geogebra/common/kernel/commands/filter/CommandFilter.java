package org.geogebra.common.kernel.commands.filter;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;

/**
 * Filters out commands that are not allowed
 */
public interface CommandFilter {

    /**
     * @param command
     *          the command that should be allowed or not
     * @param commandProcessor
     *          makes it possible to check the argument list of the command
     * @return
     *          true if the command is allowed otherwise false
     */
    boolean isAllowed(Command command, CommandProcessor commandProcessor);
}
