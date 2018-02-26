package org.geogebra.common.kernel.stepbystep.steptree;

import org.geogebra.common.kernel.stepbystep.steps.SolveTracker;
import org.geogebra.common.main.Localization;

import java.util.*;

public class StepSolution extends StepNode {

    private Map<StepVariable, StepNode> values;
    private List<StepSolvable> conditions;

    public StepSolution() {
        values = new HashMap<>();
        conditions = new ArrayList<>();
    }

    public static StepSolution simpleSolution(StepVariable variable, StepNode value, SolveTracker tracker) {
        StepSolution ss = new StepSolution();
        ss.addVariableSolutionPair(variable, value);
        ss.addConditions(tracker.getConditions());
        return ss;
    }

    public void addVariableSolutionPair(StepVariable variable, StepNode value) {
        values.put(variable, value);
    }

    public Set<Map.Entry<StepVariable, StepNode>> getVariableSolutionPairs() {
        return values.entrySet();
    }

    public void addCondition(StepSolvable condition) {
        conditions.add(condition);
    }

    public void addConditions(List<StepSolvable> conditions) {
        if (conditions != null) {
            this.conditions.addAll(conditions);
        }
    }

    /**
     * For simple solutions (only one variable)
     * @return variable of solution
     */
    public StepVariable getVariable() {
        if (values.size() == 1) {
            return (StepVariable) values.keySet().toArray()[0];
        }

        return null;
    }

    /**
     * For simple solutions (only one variable)
     * @return value of solution
     */
    public StepNode getValue() {
        if (values.size() == 1) {
            return getValue(getVariable());
        }

        return null;
    }

    public StepNode getValue(StepVariable variable) {
        return values.get(variable);
    }

    @Override
    public StepSolution deepCopy() {
        StepSolution copy = new StepSolution();

        for (Map.Entry<StepVariable, StepNode> entry : values.entrySet()) {
            copy.values.put(entry.getKey().deepCopy(), entry.getValue().deepCopy());
        }

        for (StepSolvable condition : conditions) {
            copy.conditions.add(condition.deepCopy());
        }

        return copy;
    }

    @Override
    public String toString() {
        StringBuilder solutionString = new StringBuilder();
        for (Map.Entry<StepVariable, StepNode> entry : values.entrySet()) {
            if (!"".equals(solutionString.toString())) {
                solutionString.append(", ");
            }
            solutionString.append(entry.getKey().toString());
            if (entry.getValue() instanceof StepExpression) {
                solutionString.append(" = ");
            } else {
                solutionString.append(" in ");
            }
            solutionString.append(entry.getValue().toString());
        }

        if (conditions.isEmpty()) {
            return solutionString.toString();
        }

        StringBuilder conditionString = new StringBuilder();
        for (StepSolvable condition : conditions) {
            if (!"".equals(conditionString.toString())) {
                conditionString.append(" and ");
            }
            conditionString.append(condition.toString());
        }

        return solutionString.toString() + "if " + conditionString.toString();
    }

    @Override
    public String toLaTeXString(Localization loc, boolean colored) {
        if (colored && color != 0) {
            return "\\fgcolor{" + getColorHex() + "}{" + convertToString(loc, false) + "}";
        }
        return convertToString(loc, colored);
    }

    public String convertToString(Localization loc, boolean colored) {
        StringBuilder solutionString = new StringBuilder();
        for (Map.Entry<StepVariable, StepNode> entry : values.entrySet()) {
            if (!"".equals(solutionString.toString())) {
                solutionString.append(", ");
            }
            solutionString.append(entry.getKey().toLaTeXString(loc, colored));
            if (entry.getValue() instanceof StepExpression) {
                solutionString.append(" = ");
            } else {
                solutionString.append(" \\in ");
            }
            solutionString.append(entry.getValue().toLaTeXString(loc, colored));
        }

        if (conditions.isEmpty()) {
            return solutionString.toString();
        }

        StringBuilder conditionString = new StringBuilder();
        for (StepSolvable condition : conditions) {
            if (!"".equals(conditionString.toString())) {
                conditionString.append(" and ");
            }
            conditionString.append(condition.toLaTeXString(loc, colored));
        }

        return solutionString.toString() + " if " + conditionString.toString();
    }
}
