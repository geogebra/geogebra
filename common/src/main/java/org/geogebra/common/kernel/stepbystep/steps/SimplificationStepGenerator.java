package org.geogebra.common.kernel.stepbystep.steps;

import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;

public interface SimplificationStepGenerator {

	StepNode apply(StepNode sn, SolutionBuilder sb, RegroupTracker tracker);

	// 1 -- group type, 2 -- substep type, 3 -- strategy type
	int type();
}
