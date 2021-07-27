package org.geogebra.common.kernel.stepbystep;

import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;

@SuppressWarnings("serial")
public class SolveFailedException extends RuntimeException {

	private SolutionStep steps;
	private String message;

	public SolveFailedException(String message) {
		this.message = message;
	}

	public SolveFailedException(SolutionStep steps) {
		this.steps = steps;
	}

	public SolutionStep getSteps() {
		return steps;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
