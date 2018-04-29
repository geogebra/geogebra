package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;

/**
 * Interface for algos that can provide steps for StepGuiBuilder
 *
 */
public interface HasSteps {

	/**
	 * Show steps of this computation in given step builder
	 *
	 */
	SolutionStep getSteps();

	/**
	 * @return whether some steps are showable
	 */
	boolean canShowSteps();

}
