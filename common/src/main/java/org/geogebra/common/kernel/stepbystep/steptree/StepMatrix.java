package org.geogebra.common.kernel.stepbystep.steptree;

import org.geogebra.common.kernel.stepbystep.SolveFailedException;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.main.Localization;

public class StepMatrix extends StepNode {

    private StepExpression[][] data;
    private Determinant determinant = new Determinant();

    public class Determinant extends StepNode {

        @Override
        public StepNode deepCopy() {
            return StepMatrix.this.deepCopy().getDeterminant();
        }

        @Override
        public String toLaTeXString(Localization loc, boolean colored) {
            if (colored && color != 0) {
                return "\\fgcolor{" + getColorHex() + "}{\\begin{vmatrix}"
                        + convertToString(loc, false) + "\\end{vmatrix}}";
            }

            return "\\begin{vmatrix}" + convertToString(loc, colored) + "\\end{vmatrix}";
        }

        public StepExpression calculateDeterminant(SolutionBuilder steps) {
            SolutionBuilder tempSteps = new SolutionBuilder();

            int color = 1;
            for (StepExpression[] row : data) {
                for (StepExpression element : row) {
                    element.setColor(color++);
                }
            }

            StepExpression sum = null;

            if (data.length == 2) {
                sum = multiply(data[0][0], data[1][1]);
                sum = subtract(sum, multiply(data[0][1], data[1][0]));
            } else if (data.length == 3) {
                sum = multiply(data[0][0], data[1][1], data[2][2]);
                sum = add(sum, multiply(data[0][1], data[1][2], data[2][0]));
                sum = add(sum, multiply(data[0][2], data[1][0], data[2][1]));
                sum = subtract(sum, multiply(data[0][2], data[1][1], data[2][0]));
                sum = subtract(sum, multiply(data[0][1], data[1][0], data[2][2]));
                sum = subtract(sum, multiply(data[0][0], data[1][2], data[2][1]));
            } else {
                throw new SolveFailedException("determinant size not supported");
            }

            tempSteps.addSubstep(this, sum, SolutionStepType.USE_LEIBNIZ_FORMULA);
            sum = sum.regroup(tempSteps);

            for (StepExpression[] row : data) {
                for (StepExpression element : row) {
                    element.cleanColors();
                }
            }

            steps.addGroup(SolutionStepType.CALCULATE_DETERINANT, tempSteps, sum, this);
            return sum;
        }
    }

    public Determinant getDeterminant() {
        if (data.length != data[0].length) {
            return null;
        }

        return determinant;
    }

    public StepMatrix(StepExpression[][] data) {
        this.data = new StepExpression[data.length][data[0].length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                this.data[i][j] = data[i][j].deepCopy();
            }
        }
    }

    public StepMatrix deepCopy() {
        StepMatrix sm = new StepMatrix(data);
        sm.setColor(color);
        return sm;
    }

    @Override
    public String toLaTeXString(Localization loc, boolean colored) {
        if (colored && color != 0) {
            return "\\fgcolor{" + getColorHex() + "}{\\begin{pmatrix}"
                    + convertToString(loc, false) + "\\end{pmatrix}}";
        }

        return "\\begin{pmatrix}" + convertToString(loc, colored) + "\\end{pmatrix}";
    }

    private String convertToString(Localization loc, boolean colored) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            if (i != 0) {
                sb.append(" \\\\ ");
            }
            for (int j = 0; j < data[i].length; j++) {
                if (j != 0) {
                    sb.append(" & ");
                }
                sb.append(data[i][j].toLaTeXString(loc, colored));
            }
        }

        return sb.toString();
    }
}
