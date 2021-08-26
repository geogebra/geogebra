package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoShowSteps;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepTransformable;

/**
 * ShowSteps(Solve/Simplify/Derivative//Expand/Factor(...))
 */
public class CmdShowSteps extends CommandProcessor {

	/**
	 * Creates new command processor
	 *
	 * @param kernel kernel
	 */
	CmdShowSteps(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) {
		if (c.getArgumentNumber() != 1) {
			throw argNumErr(c);
		}

		if (!(c.getArgument(0).unwrap() instanceof Command)) {
			throw argErr(c, c.getArgument(0));
		}

		Command internalCommand = (Command) c.getArgument(0).unwrap();
		Commands name = Commands.valueOf(internalCommand.getName());

		if (name != Commands.Simplify && name != Commands.Factor
				&& name != Commands.Expand && name != Commands.Solve
				&& name != Commands.Derivative
				|| internalCommand.getArgumentNumber() != 1) {
			throw argErr(c, c.getArgument(0));
		}

		ExpressionNode expressionNode = internalCommand.getArgument(0);
		expressionNode.resolveVariables(new EvalInfo(false));
		if (expressionNode.unwrap().isGeoElement()
				&& ((GeoElement) expressionNode.unwrap()).getDefinition() != null) {
			expressionNode = ((GeoElement) expressionNode.unwrap()).getDefinition();
		}

		StepTransformable expression = StepNode.getStepTree(
				expressionNode.toOutputValueString(StringTemplate.defaultTemplate),
				kernel.getParser());

		return new AlgoShowSteps(cons, resArgs(c), name, expression).getOutput();
	}
}
