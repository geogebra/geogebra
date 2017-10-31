package org.geogebra.common.kernel.stepbystep;

import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;
import org.geogebra.common.kernel.stepbystep.steptree.StepSet;

@SuppressWarnings("serial")
public class CASConflictException extends RuntimeException {

	private SolutionStep steps;
	private StepSet solutions;
	private StepSet CASSolutions;

	public CASConflictException(SolutionStep steps, StepSet solutions, StepSet CASSolutions) {
		this.steps = steps;
		this.solutions = solutions;
		this.CASSolutions = CASSolutions;
	}

	public SolutionStep getSteps() {
		return steps;
	}

	public StepSet getSolutions() {
		return solutions;
	}

	public StepSet getCASSolutions() {
		return CASSolutions;
	}
}
