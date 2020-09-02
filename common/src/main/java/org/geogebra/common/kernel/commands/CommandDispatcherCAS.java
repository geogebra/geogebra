package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.advanced.CmdCompleteSquare;
import org.geogebra.common.kernel.cas.CmdCASCommand1Arg;
import org.geogebra.common.kernel.cas.CmdCoefficients;
import org.geogebra.common.kernel.cas.CmdDegree;
import org.geogebra.common.kernel.cas.CmdImplicitDerivative;
import org.geogebra.common.kernel.cas.CmdIntegral;
import org.geogebra.common.kernel.cas.CmdLimit;
import org.geogebra.common.kernel.cas.CmdLimitAbove;
import org.geogebra.common.kernel.cas.CmdLimitBelow;
import org.geogebra.common.kernel.cas.CmdNextPreviousPrime;
import org.geogebra.common.kernel.cas.CmdParametricDerivative;
import org.geogebra.common.kernel.cas.CmdPartialFractions;
import org.geogebra.common.kernel.cas.CmdSimplify;
import org.geogebra.common.kernel.cas.CmdSolveODE;
import org.geogebra.common.kernel.cas.CmdSurdText;
import org.geogebra.common.kernel.cas.CmdTrigCombine;
import org.geogebra.common.kernel.cas.CmdTrigExpand;

/**
 * class to split off some CmdXXX classes into another jar (for faster applet
 * loading)
 *
 */
public class CommandDispatcherCAS implements CommandDispatcherInterface {
	@Override
	public CommandProcessor dispatch(Commands c, Kernel kernel) {
		switch (c) {

		// note: Integral(f,a,b) is allowed but not Integral(f)
		case Integral:

		case IntegralBetween:
		case NIntegral:
			return new CmdIntegral(kernel, c);
		case Derivative:
		case NDerivative:
			return new CmdDerivative(kernel, c);
		case TrigSimplify:
		case Expand:
		case Factor:
		case IFactor:
			return new CmdCASCommand1Arg(kernel, c);
		case Simplify:
			return new CmdSimplify(kernel);
		case SurdText:
			return new CmdSurdText(kernel);
		case ParametricDerivative:
			return new CmdParametricDerivative(kernel);
		case TrigExpand:
			return new CmdTrigExpand(kernel);
		case TrigCombine:
			return new CmdTrigCombine(kernel);
		case Limit:
			return new CmdLimit(kernel);
		case LimitBelow:
			return new CmdLimitBelow(kernel);
		case LimitAbove:
			return new CmdLimitAbove(kernel);
		case Degree:
			return new CmdDegree(kernel);
		case Coefficients:
			return new CmdCoefficients(kernel);
		case PartialFractions:
			return new CmdPartialFractions(kernel);
		case SolveODE:
			return new CmdSolveODE(kernel);
		case ImplicitDerivative:
			return new CmdImplicitDerivative(kernel);
		case NextPrime:
			return new CmdNextPreviousPrime(kernel, true);
		case PreviousPrime:
			return new CmdNextPreviousPrime(kernel, false);
		case CompleteSquare:
			return new CmdCompleteSquare(kernel);
		case NSolve:
		case Solve:
		case NSolutions:
		case Solutions:
		case PlotSolve:
			return new CmdSolve(kernel, c);
		case CASLoaded:
			return new CmdCASLoaded(kernel);
		default:
			break;
		}
		return null;
	}
}
