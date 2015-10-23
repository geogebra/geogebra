package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.LocusEquation;
import org.geogebra.common.kernel.arithmetic.Command;
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
import org.geogebra.common.main.App;

/**
 * class to split off some CmdXXX classes into another jar (for faster applet
 * loading)
 *
 */
public class CommandDispatcherCAS implements CommandDispatcherInterface {
	public CommandProcessor dispatch(Commands c, Kernel kernel,
			Command command) {
		App.printStacktrace("");
		App app = kernel.getApplication();

		switch (c) {
		case Integral:
		case IntegralBetween:
		case NIntegral:

			if (command.getArgumentNumber() < 3 && app.isExam()
					&& !app.getExam().isCASAllowed()) {
				return null;
			}

			return new CmdIntegral(kernel, c);

		}

		if (app.isExam() && !app.getExam().isCASAllowed()) {
			return null;
		}

		// syntaxes disallowed in non-CAS Exam Mode
		switch (c) {

		case LocusEquation:
			return LocusEquation.newCmdLocusEquation(kernel);
		case Envelope:
			return LocusEquation.newCmdEnvelope(kernel);

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
		case Derivative:
			return new CmdDerivative(kernel);
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
		default:
			break;
		}
		return null;
	}
}
