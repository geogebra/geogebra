package org.geogebra.common.kernel.stepbystep;

import java.util.List;

import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepSolution;

@SuppressWarnings("serial")
public class CASConflictException extends RuntimeException {

	private SolutionStep steps;
	private List<StepSolution> solutions;
	private List<StepNode> CASSolutions;

	public CASConflictException(SolutionStep steps, List<StepSolution> solutions,
			List<StepNode> CASSolutions) {
		this.steps = steps;
		this.solutions = solutions;
		this.CASSolutions = CASSolutions;
	}

	public SolutionStep getSteps() {
		return steps;
	}

	public List<StepSolution> getSolutions() {
		return solutions;
	}

	public List<StepNode> getCASSolutions() {
		return CASSolutions;
	}
}
