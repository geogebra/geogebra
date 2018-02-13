package org.geogebra.common.kernel.stepbystep.steps;

import org.geogebra.common.kernel.stepbystep.steptree.StepArbitraryConstant;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepInterval;
import org.geogebra.common.kernel.stepbystep.steptree.StepSet;

public class SolveTracker {

    private StepInterval restriction;
    private StepSet undefinedPoints;

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

    public void setRestriction(StepInterval restriction) {
        this.restriction = restriction;
    }

    public void addUndefinedPoint(StepExpression point) {
        if (undefinedPoints == null) {
            undefinedPoints = new StepSet(point);
        } else {
            undefinedPoints.addElement(point);
        }
    }

    public StepInterval getRestriction() {
        if (restriction == null) {
            return StepInterval.R;
        }

        return restriction;
    }

    public StepSet getUndefinedPoints() {
        return undefinedPoints;
    }

    public boolean shouldCheck() {
        return shouldCheckSolutions;
    }

    public StepArbitraryConstant getNextArbInt() {
        return new StepArbitraryConstant("k", ++arbConstTracker, StepArbitraryConstant.ConstantType.INTEGER);
    }
}
