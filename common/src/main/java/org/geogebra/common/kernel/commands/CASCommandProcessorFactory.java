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
 * Factory for CAS commands.
 * @see CommandProcessorFactory
 */
public class CASCommandProcessorFactory implements CommandProcessorFactory {
	@Override
	public CommandProcessor getProcessor(Commands command, Kernel kernel) {
		switch (command) {

		// note: Integral(f,a,b) is allowed but not Integral(f)
		case Integral:

		case IntegralBetween:
		case NIntegral:
			return new CmdIntegral(kernel, command);
		case Derivative:
		case NDerivative:
			return new CmdDerivative(kernel, command);
		case TrigSimplify:
		case Expand:
		case Factor:
		case IFactor:
			return new CmdCASCommand1Arg(kernel, command);
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
			return new CmdSolve(kernel, command);
		case CASLoaded:
			return new CmdCASLoaded(kernel);
		default:
			break;
		}
		return null;
	}
}
