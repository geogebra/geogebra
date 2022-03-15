package org.geogebra.common.kernel.stepbystep;

import org.geogebra.common.kernel.parser.Parser;

public interface StepSolver {

	/**
	 * @param type operation type
	 * @return steps as an object
	 */
	String getSteps(String text, String type, Parser parser);
}
