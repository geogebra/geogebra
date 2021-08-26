package org.geogebra.common.kernel.stepbystep.steps;

import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.steptree.StepTransformable;

public interface SimplificationStepGenerator {

	StepTransformable apply(StepTransformable sn, SolutionBuilder sb, RegroupTracker tracker);

	boolean isGroupType();
}
