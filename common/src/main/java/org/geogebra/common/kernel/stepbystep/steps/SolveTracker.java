package org.geogebra.common.kernel.stepbystep.steps;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.stepbystep.steptree.StepArbitraryInteger;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepInterval;
import org.geogebra.common.kernel.stepbystep.steptree.StepLogical;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepSet;
import org.geogebra.common.kernel.stepbystep.steptree.StepSolvable;

public class SolveTracker {

	private StepLogical restriction;
	private StepSet undefinedPoints;
	private List<StepSolvable> conditions;

	private int arbConstTracker;

	private boolean shouldCheckSolutions;
	private Boolean approximate;

	public Boolean isApproximate() {
		return approximate;
	}

	public void setApproximate(Boolean approximate) {
		this.approximate = approximate;
	}

	public void setShouldCheckSolutions() {
		shouldCheckSolutions = true;
	}

	public boolean shouldCheckSolutions() {
		return shouldCheckSolutions;
	}

	public void addRestriction(StepLogical restriction) {
		this.restriction = StepNode.intersect(this.restriction, restriction);
	}

	public void addCondition(StepSolvable condition) {
		if (conditions == null) {
			conditions = new ArrayList<>();
		}

		conditions.add(condition);
	}

	public List<StepSolvable> getConditions() {
		return conditions;
	}

	public void addUndefinedPoint(StepExpression point) {
		if (undefinedPoints == null) {
			undefinedPoints = new StepSet(point);
		} else {
			undefinedPoints.addElement(point);
		}
	}

	public void addUndefinedPoints(StepSet points) {
		if (undefinedPoints == null) {
			undefinedPoints = points.deepCopy();
		} else {
			undefinedPoints.addAll(points);
		}
	}

	public StepLogical getRestriction() {
		if (restriction == null) {
			return StepInterval.R;
		}

		return restriction;
	}

	public StepSet getUndefinedPoints() {
		if (undefinedPoints == null) {
			return new StepSet();
		}

		return undefinedPoints;
	}

	public boolean shouldCheck() {
		return shouldCheckSolutions;
	}

	public StepArbitraryInteger getNextArbInt() {
		return new StepArbitraryInteger("k", ++arbConstTracker);
	}
}
