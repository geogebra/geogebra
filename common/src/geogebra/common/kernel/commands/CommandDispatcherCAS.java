package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.LocusEquation;
import geogebra.common.kernel.cas.CmdCoefficients;
import geogebra.common.kernel.cas.CmdDegree;
import geogebra.common.kernel.cas.CmdExpand;
import geogebra.common.kernel.cas.CmdFactor;
import geogebra.common.kernel.cas.CmdImplicitDerivative;
import geogebra.common.kernel.cas.CmdIntegral;
import geogebra.common.kernel.cas.CmdLength;
import geogebra.common.kernel.cas.CmdLimit;
import geogebra.common.kernel.cas.CmdLimitAbove;
import geogebra.common.kernel.cas.CmdLimitBelow;
import geogebra.common.kernel.cas.CmdNextPreviousPrime;
import geogebra.common.kernel.cas.CmdParametricDerivative;
import geogebra.common.kernel.cas.CmdPartialFractions;
import geogebra.common.kernel.cas.CmdSimplify;
import geogebra.common.kernel.cas.CmdSolveODE;
import geogebra.common.kernel.cas.CmdSurdText;
import geogebra.common.kernel.cas.CmdTangent;
import geogebra.common.kernel.cas.CmdTrigCombine;
import geogebra.common.kernel.cas.CmdTrigExpand;
import geogebra.common.kernel.cas.CmdTrigSimplify;

/**
 * class to split off some CmdXXX classes into another jar (for faster applet loading)
 *
 */
public class CommandDispatcherCAS {
	public CommandProcessor dispatch(Commands c, Kernel kernel){
		switch(c){

		case LocusEquation:
			return LocusEquation.newCmdLocusEquation(kernel);
		case Expand:
			return new CmdExpand(kernel);
		case Factor:
			return new CmdFactor(kernel);
		case Simplify:
			return new CmdSimplify(kernel);
		case SurdText:
			return new CmdSurdText(kernel);
		case Tangent:
			return new CmdTangent(kernel);
		case ParametricDerivative:
			return new CmdParametricDerivative(kernel);
		case Derivative:
			return new CmdDerivative(kernel);
		case Integral:
			return new CmdIntegral(kernel, Commands.Integral);
		case IntegralBetween:
			return new CmdIntegral(kernel, Commands.IntegralBetween);
		case NIntegral:
			return new CmdIntegral(kernel, Commands.NIntegral);
		case TrigExpand:
			return new CmdTrigExpand(kernel);
		case TrigSimplify:
			return new CmdTrigSimplify(kernel);
		case TrigCombine:
			return new CmdTrigCombine(kernel);
		case Length:
			return new CmdLength(kernel);
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
			return new CmdNextPreviousPrime(kernel,true);
		case PreviousPrime:
			return new CmdNextPreviousPrime(kernel,false);
		}
		return null;
	}
}
