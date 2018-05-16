package org.geogebra.common.kernel.stepbystep.steps;

import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.steptree.StepSolution;
import org.geogebra.common.kernel.stepbystep.steptree.StepSolvable;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;

import java.util.List;

public interface SolveStepGenerator {

	List<StepSolution> apply(StepSolvable se, StepVariable sv, SolutionBuilder sb,
			SolveTracker tracker);
}
