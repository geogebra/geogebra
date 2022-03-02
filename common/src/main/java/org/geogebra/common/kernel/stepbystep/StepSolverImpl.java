package org.geogebra.common.kernel.stepbystep;

import org.geogebra.common.gui.view.algebra.StepGuiBuilderJson;
import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepTransformable;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;

/**
 * Command dispatcher for step-by-step
 */
public class StepSolverImpl implements StepSolver {

	@Override
	public String getSteps(String input, String type, Parser parser) {
		SolutionBuilder sb = new SolutionBuilder();
		StepTransformable expression = StepNode.getStepTree(
				input, parser);
		StepVariable variable = null;
		if (expression == null) {
			return "[]";
		}
		if (expression.getListOfVariables().size() > 0) {
			variable = expression.getListOfVariables().get(0);
		}
		switch (type) {
		case "simplify":
			expression.regroup(sb);
			break;
		case "expand":
			expression.expand(sb);
			break;
		case "factor":
			expression.factor(sb);
			break;
		case "solve":
			expression.toSolvable().solve(variable, sb);
			break;
		case "derivative":
			StepNode.differentiate((StepExpression) expression, variable).differentiate(sb);
			break;
		default:
			throw new IllegalArgumentException("Unexpected step type " + type);
		}
		SolutionStep steps = sb.getSteps();
		StepGuiBuilderJson builder = new StepGuiBuilderJson(parser.getKernel().getLocalization());
		builder.buildStepGui(steps);
		return builder.toString();
	}
}
