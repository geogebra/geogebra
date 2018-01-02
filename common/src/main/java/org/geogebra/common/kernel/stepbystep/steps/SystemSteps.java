package org.geogebra.common.kernel.stepbystep.steps;

import org.geogebra.common.kernel.stepbystep.SolveFailedException;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.*;

import java.util.HashSet;
import java.util.Set;

public class SystemSteps {

    // for very simple systems.
    public static StepSet solveBySubstitution(StepEquationSystem ses, SolutionBuilder steps) {

        int n = ses.getEquations().length;

        StepEquationSystem temp = ses.deepCopy();
        for (int i = 0; i < n; i++) {
            steps.add(SolutionStepType.EQUATION, temp);

            Set<StepVariable> variableSet = new HashSet<>();
            temp.getEquation(i).getListOfVariables(variableSet);
            StepVariable[] variableList =  variableSet.toArray(new StepVariable[0]);

            StepNode[] solutions = temp.getEquation(i).solve(variableList[0], steps).getElements();

            if (solutions.length == 0) {
                return new StepSet();
            } else if (solutions.length > 1) {
                throw new SolveFailedException(steps.getSteps());
            }

            StepExpression solution = (StepExpression) solutions[0];
            solution.cleanColors();

            steps.add(SolutionStepType.REPLACE_WITH, variableList[0], solution);

            StepEquation[] newEquations = new StepEquation[n];
            for (int j = 0; j < n; j++) {
                if (j == i) {
                    newEquations[j] = new StepEquation(variableList[0], solution);
                } else {
                    newEquations[j] = temp.getEquation(j).deepCopy();
                    newEquations[j].replace(variableList[0], solution);
                }
            }

            temp = new StepEquationSystem(newEquations);
        }

        steps.add(SolutionStepType.EQUATION, temp);
        return new StepSet(temp);
    }

}
