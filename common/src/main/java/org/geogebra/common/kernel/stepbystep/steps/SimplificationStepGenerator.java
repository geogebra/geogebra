package org.geogebra.common.kernel.stepbystep.steps;

import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;

public interface SimplificationStepGenerator {

	public StepNode apply(StepNode sn, SolutionBuilder sb, int[] colorTracker);
}
