package org.geogebra.common.kernel.commands.filter;

import org.geogebra.common.kernel.commands.Commands;

public class ExamCommandFilter implements CommandFilter {

    @Override
    public boolean isCommandAllowed(Commands command) {
        return command != Commands.SetFixed;
    }
}
