package org.geogebra.common.kernel.commands.selector;

import org.geogebra.common.kernel.commands.Commands;

/**
 * Factory that creates command filters for the Scientific Calculator
 */
public final class SciCalcCommandSelectorFactory {

    /**
     *
     * @return Returns the CommandSelector that allows only the Scientific Calculator commands
     */
    public CommandSelector createCommandSelector() {
        CommandSelectorSet commandSelector = new CommandSelectorSet();
        commandSelector.addAllowedCommands(
                Commands.Mean,
                Commands.mean,
                Commands.SD,
                Commands.stdev,
                Commands.SampleSD,
                Commands.stdevp,
                Commands.nPr,
                Commands.nCr,
                Commands.Binomial,
                Commands.MAD,
                Commands.mad
        );
        return commandSelector;
    }
}
