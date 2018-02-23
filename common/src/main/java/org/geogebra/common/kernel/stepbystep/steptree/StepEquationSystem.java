package org.geogebra.common.kernel.stepbystep.steptree;

import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.steps.SystemSteps;
import org.geogebra.common.main.Localization;

import java.util.Arrays;
import java.util.List;

public class StepEquationSystem extends StepNode {

    private StepEquation[] equations;

    public StepEquationSystem(StepEquation... equations) {
        this.equations = new StepEquation[equations.length];
        for (int i = 0; i < equations.length; i++) {
            this.equations[i] = equations[i].deepCopy();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof StepEquationSystem) {
            StepEquationSystem ses = (StepEquationSystem) o;
            return Arrays.equals(equations, ses.equations);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(equations);
    }

    public List<StepSolution> solve(SolutionBuilder steps) {
        return SystemSteps.solveBySubstitution(this, steps);
    }

    public StepEquation getEquation(int i) {
        return equations[i];
    }

    public StepEquation[] getEquations() {
        return equations;
    }

    @Override
    public StepEquationSystem deepCopy() {
        StepEquation[] copy = new StepEquation[equations.length];

        for (int i = 0; i < equations.length; i++) {
            copy[i] = equations[i].deepCopy();
        }

        return new StepEquationSystem(copy);
    }

    @Override
    public String toLaTeXString(Localization loc, boolean colored) {
        StringBuilder sb = new StringBuilder();

        sb.append("\\begin{cases}");
        for (StepEquation equation : equations) {
            sb.append(equation.toLaTeXString(loc, colored));
            sb.append("\\\\");
        }
        sb.append("\\end{cases}");

        return sb.toString();
    }
}
