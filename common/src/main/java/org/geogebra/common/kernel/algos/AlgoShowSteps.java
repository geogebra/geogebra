package org.geogebra.common.kernel.algos;

import org.geogebra.common.gui.view.algebra.StepGuiBuilder;
import org.geogebra.common.gui.view.algebra.StepGuiBuilderCmd;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepTransformable;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;

public class AlgoShowSteps extends AlgoElement implements TableAlgo {

	private Commands name;
	private StepTransformable expression;
	private int maxRows;

	private GeoText text;

	public AlgoShowSteps(Construction c, GeoElement[] input, Commands name,
			StepTransformable expression, int maxRows) {
		super(c);
		this.input = input;
		this.name = name;
		this.expression = expression;
		this.maxRows = maxRows;

		setInputOutput();
		compute();
	}

	@Override
	protected void setInputOutput() {
		text = new GeoText(cons);
		text.setAbsoluteScreenLoc(0, 0);
		text.setAbsoluteScreenLocActive(true);

		text.setLaTeX(true, false);

		// set sans-serif LaTeX default
		text.setSerifFont(false);

		text.setIsTextCommand(true); // stop editing as text
		setOnlyOutput(text);

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
					StepTransformable.differentiate((StepExpression) expression, variable)
							.differentiate(sb);
			}

			StepGuiBuilder builder = new StepGuiBuilderCmd(kernel.getLocalization(),
					maxRows, variable);
			builder.buildStepGui(sb.getSteps());

			text.setTextString(builder.toString());
		} catch (Exception e) {
			text.setUndefined();
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
