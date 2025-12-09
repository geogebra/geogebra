/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
