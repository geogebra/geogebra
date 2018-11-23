package org.geogebra.common.kernel.commands.filter;

import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;

public interface CommandFilter {
    boolean isAllowed(Command command, CommandProcessor commandProcessor);
}
