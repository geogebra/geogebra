package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoShowSteps;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepTransformable;
import org.geogebra.common.util.DoubleUtil;

public class CmdShowSteps extends CommandProcessor {

	/**
	 * Creates new command processor
	 *
	 * @param kernel kernel
	 */
	public CmdShowSteps(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) {
		if (c.getArgumentNumber() != 1 && c.getArgumentNumber() != 2) {
			throw argNumErr(c);
		}

		if (!(c.getArgument(0).unwrap() instanceof Command)) {
			throw argErr(c, c.getArgument(0));
		}

		if (c.getArgumentNumber() == 2
				&& (!c.getArgument(1).evaluatesToNumber(false)
				|| c.getArgument(1).evaluateDouble() <= 0
				|| !DoubleUtil.isInteger(c.getArgument(1).evaluateDouble()))) {
			throw argErr(c, c.getArgument(1));
		}

		Command internalCommand = (Command) c.getArgument(0).unwrap();
		Commands name = Commands.valueOf(internalCommand.getName());
		ExpressionNode expressionNode = internalCommand.getArgument(0);
		expressionNode.resolveVariables(new EvalInfo(false));

		StepTransformable expression = StepNode.getStepTree(
				expressionNode.toOutputValueString(StringTemplate.defaultTemplate), kernel.getParser());
		int maxRows = c.getArgumentNumber() == 2 ? (int) c.getArgument(1).evaluateDouble() : -1;

		return new AlgoShowSteps(cons, resArgs(c), name, expression, maxRows).getOutput();
	}
}
