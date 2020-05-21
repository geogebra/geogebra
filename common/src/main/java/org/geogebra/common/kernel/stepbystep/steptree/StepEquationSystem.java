package org.geogebra.common.kernel.stepbystep.steptree;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.steps.SystemSteps;
import org.geogebra.common.main.Localization;

public class StepEquationSystem extends StepNode {

	private StepEquation[] equations;

	public StepEquationSystem(StepEquation... equations) {
		this.equations = equations;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof StepEquationSystem) {
			StepEquationSystem ses = (StepEquationSystem) o;
			return Arrays.equals(equations, ses.equations);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(equations);
	}

	public List<StepSolution> solve(SolutionBuilder steps) {
		return solve(steps, getListOfVariables());
	}

	public List<StepSolution> solve(SolutionBuilder steps, List<StepVariable> variables) {
		return SystemSteps.solveBySubstitution(this, variables, steps);
	}

	public StepEquation getEquation(int i) {
		return equations[i];
	}

	public StepEquation[] getEquations() {
		return equations;
	}

	public int size() {
		return equations.length;
	}

	@Override
	public StepEquationSystem deepCopy() {
		StepEquation[] copy = new StepEquation[equations.length];

		for (int i = 0; i < equations.length; i++) {
			copy[i] = equations[i].deepCopy();
		}

		return new StepEquationSystem(copy);
	}

	@Override
	public String toLaTeXString(Localization loc, boolean colored) {
		StringBuilder sb = new StringBuilder();

		sb.append("\\begin{cases}");
		for (StepEquation equation : equations) {
			sb.append(equation.toLaTeXString(loc, colored));
			sb.append("\\\\");
		}
		sb.append("\\end{cases}");

		return sb.toString();
	}
}
