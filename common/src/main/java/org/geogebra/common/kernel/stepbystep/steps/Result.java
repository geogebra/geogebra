package org.geogebra.common.kernel.stepbystep.steps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.stepbystep.steptree.StepSolution;
import org.geogebra.common.kernel.stepbystep.steptree.StepSolvable;

/**
 * Class used as the result of a SolveStepGenerator. It contains eiter
 * the solutions, or the (un)changed equation/inequality.
 */
class Result {

	private List<StepSolution> solutions;
	private StepSolvable solvable;

	public Result() {
		this.solutions = new ArrayList<>();
	}

	public Result(StepSolution... solutions) {
		this.solutions = new ArrayList<>(Arrays.asList(solutions));
	}

	public Result(List<StepSolution> solutions) {
		this.solutions = solutions;
	}

	public Result(StepSolvable solvable) {
		this.solvable = solvable;
	}

	@Override
	public String toString() {
		if (solutions != null) {
			return solutions.toString();
		} else {
			return solvable.toString();
		}
	}

	public List<StepSolution> getSolutions() {
		return solutions;
	}

	public StepSolvable getSolvable() {
		return solvable;
	}
}
