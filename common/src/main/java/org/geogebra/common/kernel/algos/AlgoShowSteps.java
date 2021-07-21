package org.geogebra.common.kernel.algos;

import org.geogebra.common.gui.view.algebra.StepGuiBuilderCmd;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepTransformable;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;

public class AlgoShowSteps extends AlgoElement implements TableAlgo {

	private Commands name;
	private StepTransformable expression;

	private GeoList list;

	/**
	 * @param c
	 *            construction
	 * @param input
	 *            inputs
	 * @param name
	 *            command name
	 * @param expression
	 *            expression
	 */
	public AlgoShowSteps(Construction c, GeoElement[] input, Commands name,
			StepTransformable expression) {
		super(c);
		this.input = input;
		this.name = name;
		this.expression = expression;

		setInputOutput();
		compute();
	}

	@Override
	protected void setInputOutput() {
		list = new GeoList(cons);

		setOnlyOutput(list);

		setDependencies();
	}

	@Override
	public void compute() {
		SolutionBuilder sb = new SolutionBuilder();

		try {
			StepVariable variable = null;
			if (expression.getListOfVariables().size() > 0) {
				variable = expression.getListOfVariables().get(0);
			}

			switch (name) {
			case Simplify:
				expression.regroup(sb);
				break;
			case Expand:
				expression.expand(sb);
				break;
			case Factor:
				expression.factor(sb);
				break;
			case Solve:
				expression.toSolvable().solve(variable, sb);
				break;
			case Derivative:
				StepNode.differentiate((StepExpression) expression, variable).differentiate(sb);
			default:
				// checked in Cmd, just in case
				list.setUndefined();
				return;
			}

			StepGuiBuilderCmd builder = new StepGuiBuilderCmd(kernel.getLocalization(), list,
					variable);

			list.clear();
			builder.buildList(sb.getSteps());
		} catch (Exception e) {
			list.setUndefined();
		}
	}

	@Override
	public GetCommand getClassName() {
		return Commands.ShowSteps;
	}

	@Override
	public boolean isLaTeXTextCommand() {
		return true;
	}
}
