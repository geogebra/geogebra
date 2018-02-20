package org.geogebra.common.kernel.stepbystep.solution;

import org.geogebra.common.gui.view.algebra.StepGuiBuilder;
import org.geogebra.common.main.Localization;

import java.util.ArrayList;
import java.util.List;

public abstract class SolutionStep {

    protected List<SolutionStep> substeps;

    public abstract String getDefault(Localization loc);

    public abstract String getDetailed(Localization loc);

    public void addSubStep(SolutionStep substep) {
        if (substeps == null) {
            substeps = new ArrayList<>();
        }

        substeps.add(substep);
    }


    public List<SolutionStep> getSubsteps() {
        return substeps;
    }

    public void getListOfSteps(StepGuiBuilder builder, Localization loc) {
        getListOfSteps(builder, loc, true);
    }

    public void getListOfSteps(StepGuiBuilder builder, Localization loc, boolean detailed) {
        if (detailed) {
            builder.addLatexRow(getDetailed(loc));
        } else {
            builder.addLatexRow(getDefault(loc));
        }

        if (substeps != null) {
            builder.startGroup();
            for (int i = 0; i < substeps.size(); i++) {
                (substeps.get(i)).getListOfSteps(builder, loc, true);
                if (i != substeps.size() - 1) {
                    builder.linebreak();
                }
            }
            builder.endGroup();
        }
    }

    /**
     * the complexity of a solution is simply the total number of steps
     * @return complexity of solution
     */
    public int getComplexity() {
        int complexity = 1;

        if (substeps != null) {
            for(SolutionStep step : substeps) {
                complexity += step.getComplexity();
            }
        }

        return complexity;
    }
}
