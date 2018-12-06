package org.geogebra.common.kernel.commands.selector;

import org.geogebra.common.kernel.commands.Commands;

public class NoCASCommandSelectorFactory {

	/**
	 * @return selector for apps with no CAS
	 */
	public CommandSelector createCommandSelector() {
		CommandSelectorSet commandSelector = new CommandSelectorSet(true);
		commandSelector.addCommands(Commands.LocusEquation,
				Commands.Envelope, Commands.Expand, Commands.Factor,
				Commands.Factors, Commands.IFactor, Commands.CFactor,
				Commands.Simplify, Commands.SurdText,
				Commands.ParametricDerivative, Commands.Derivative,
				Commands.TrigExpand, Commands.TrigCombine,
				Commands.TrigSimplify, Commands.Limit, Commands.LimitBelow,
				Commands.LimitAbove, Commands.Degree, Commands.Coefficients,
				Commands.CompleteSquare, Commands.PartialFractions,
				Commands.SolveODE, Commands.ImplicitDerivative,
				Commands.NextPrime, Commands.PreviousPrime, Commands.Solve,
				Commands.Solutions, Commands.NSolutions, Commands.NSolve);
		return commandSelector;
	}
}
