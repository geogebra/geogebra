package org.geogebra.common.kernel.stepbystep.steps;

import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.steptree.StepSolvable;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;

interface SolveStepGenerator<T extends StepSolvable> {

	Result apply(T se, StepVariable sv, SolutionBuilder sb, SolveTracker
			tracker);
}
