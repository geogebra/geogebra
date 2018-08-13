package org.geogebra.common.kernel.commands.filter;

import org.geogebra.common.kernel.commands.Commands;

/**
 * Singleton factory that creates command filters for the Scientific Calculator
 */
public class SciCalcCommandFilterFactory {

    private static SciCalcCommandFilterFactory instance;

    private SciCalcCommandFilterFactory() {
    }

    /**
     *
     * @return Returns the instance of the singleton class
     */
    public static SciCalcCommandFilterFactory getInstance() {
        if (instance == null) {
            instance = new SciCalcCommandFilterFactory();
        }
        return instance;
    }

    /**
     *
     * @return Returns the CommandFilter that allows only the Scientific Calculator commands
     */
    public CommandFilter createCommandFilter() {
        CommandFilterSet commandFilter = new CommandFilterSet();
        commandFilter.addAllowedCommands(
                Commands.Mean,
                Commands.stdev,
                Commands.SampleSD,
                Commands.nPr,
                Commands.Binomial
        );
        return commandFilter;
    }
}
