package org.geogebra.common.kernel.commands.selector;

import org.geogebra.common.kernel.commands.Commands;

/**
 * Creates Command Selectors for various apps.
 * 
 * @author laszlo
 *
 */
public final class CommandSelectorFactory {
	/**
	 *
	 * @return Returns the CommandSelector that allows only the Scientific
	 *         Calculator commands
	 */
	public static CommandSelector createSciCalcCommandSelector() {
		CommandSelectorSet commandSelector = new CommandSelectorSet(false);
		commandSelector.addCommands(Commands.Mean, Commands.mean, Commands.SD,
				Commands.stdev, Commands.SampleSD, Commands.stdevp,
				Commands.nPr, Commands.nCr, Commands.Binomial, Commands.MAD,
				Commands.mad);
		return commandSelector;
	}

	/**
	 * @return selector for apps with no CAS
	 */
	public static CommandSelector createNoCasCommandSelector() {
		CommandSelectorSet commandSelector = new CommandSelectorSet(true);
		commandSelector.addCommands(Commands.LocusEquation, Commands.Envelope,
				Commands.Expand, Commands.Factor, Commands.Factors,
				Commands.IFactor, Commands.CFactor, Commands.Simplify,
				Commands.SurdText, Commands.ParametricDerivative,
				Commands.Derivative, Commands.TrigExpand, Commands.TrigCombine,
				Commands.TrigSimplify, Commands.Limit, Commands.LimitBelow,
				Commands.LimitAbove, Commands.Degree, Commands.Coefficients,
				Commands.CompleteSquare, Commands.PartialFractions,
				Commands.SolveODE, Commands.ImplicitDerivative,
				Commands.NextPrime, Commands.PreviousPrime, Commands.Solve,
				Commands.Solutions, Commands.NSolutions, Commands.NSolve);
		return commandSelector;
	}

}
