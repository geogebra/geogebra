package org.geogebra.common.kernel.commands.filter;

import org.geogebra.common.kernel.commands.Commands;

/**
 * Factory that creates command filters for the Scientific Calculator
 */
public final class SciCalcCommandFilterFactory {

    /**
     *
     * @return Returns the CommandFilter that allows only the Scientific Calculator commands
     */
    public CommandFilter createCommandFilter() {
        CommandFilterSet commandFilter = new CommandFilterSet();
        commandFilter.addAllowedCommands(
                Commands.Mean,
                Commands.mean,
                Commands.stdev,
                Commands.SampleSD,
                Commands.nPr,
                Commands.nCr,
                Commands.Binomial,
                Commands.MAD,
                Commands.mad
        );
        return commandFilter;
    }
}
