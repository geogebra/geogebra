package org.geogebra.common.kernel;

/**
 * For full UI we use cons protocol for stepping, if only EV is available use
 * Kernel instead.
 */
public interface ConstructionStepper {

	/**
	 * Sets construction step to first step of construction protocol. Note:
	 * showOnlyBreakpoints() is important here
	 */
	void firstStep();

	/**
	 * Sets construction step to previous step of construction protocol Note:
	 * showOnlyBreakpoints() is important here
	 */
	void previousStep();

	/**
	 * Sets construction step to next step of construction protocol. Note:
	 * showOnlyBreakpoints() is important here
	 */
	void nextStep();

	/**
	 * Sets construction step to last step of construction protocol. Note:
	 * showOnlyBreakpoints() is important here
	 */
	void lastStep();

	/**
	 * Returns current construction step position.
	 * 
	 * @return current construction step position.
	 */
	int getCurrentStepNumber();

	/**
	 * Returns the total number of construction steps.
	 * 
	 * @return Total number of construction steps.
	 */
	int getLastStepNumber();

	/**
	 * @param step
	 *            new construction step
	 */
	void setConstructionStep(int step);

}
