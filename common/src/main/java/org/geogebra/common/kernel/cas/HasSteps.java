package org.geogebra.common.kernel.cas;

import org.geogebra.common.gui.view.algebra.StepGuiBuilder;

/**
 * Interface for algos that can provide steps for StepGuiBuilder
 *
 */
public interface HasSteps {

	/**
	 * Show steps of this computation in given step builder
	 * 
	 * @param builder
	 *            step builder
	 */
	void getSteps(StepGuiBuilder builder);

	/**
	 * @return whether some steps are showable
	 */
	boolean canShowSteps();

}
