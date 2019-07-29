package org.geogebra.common.kernel.commands.selector;

import org.geogebra.common.kernel.commands.Commands;

/**
 * Creates CommandNameFilters for various apps.
 * 
 * @author laszlo
 *
 */
public final class CommandNameFilterFactory {
	/**
	 *
	 * @return Returns the CommandNameFilter that allows only the Scientific
	 *         Calculator commands
	 */
	public static CommandNameFilter createSciCalcCommandNameFilter() {
		CommandNameFilterSet commandNameFilter = new CommandNameFilterSet(
				false);
		commandNameFilter.addCommands(Commands.Mean, Commands.mean, Commands.SD,
				Commands.stdev, Commands.SampleSD, Commands.stdevp,
				Commands.nPr, Commands.nCr, Commands.Binomial, Commands.MAD,
				Commands.mad);
		return commandNameFilter;
	}

	/**
	 * @return name filter for apps with no CAS
	 */
	public static CommandNameFilter createNoCasCommandNameFilter() {
		CommandNameFilterSet commandNameFilter = new CommandNameFilterSet(true);
		commandNameFilter.addCommands(Commands.LocusEquation, Commands.Envelope,
				Commands.Expand, Commands.Factor, Commands.Factors,
				Commands.IFactor, Commands.CFactor, Commands.Simplify,
				Commands.SurdText, Commands.ParametricDerivative,
				Commands.TrigExpand, Commands.TrigCombine,
				Commands.TrigSimplify, Commands.Limit, Commands.LimitBelow,
				Commands.LimitAbove, Commands.Degree, Commands.Coefficients,
				Commands.CompleteSquare, Commands.PartialFractions,
				Commands.SolveODE, Commands.ImplicitDerivative,
				Commands.NextPrime, Commands.PreviousPrime, Commands.Solve,
				Commands.Solutions, Commands.NSolutions, Commands.NSolve);
		return commandNameFilter;
	}

}
