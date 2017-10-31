package org.geogebra.common.kernel.stepbystep;

import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;

@SuppressWarnings("serial")
public class SolveFailedException extends RuntimeException {

	private SolutionStep steps;

	public SolveFailedException(SolutionStep steps) {
		this.steps = steps;
	}
	
	public SolutionStep getSteps() {
		return steps;
	}

}
