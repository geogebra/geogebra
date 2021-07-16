package org.geogebra.common.kernel.stepbystep.steptree;

import java.util.Arrays;

import org.geogebra.common.kernel.stepbystep.SolveFailedException;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steps.RegroupTracker;
import org.geogebra.common.kernel.stepbystep.steps.SimplificationStepGenerator;
import org.geogebra.common.kernel.stepbystep.steps.StepStrategies;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Operation;

public class StepMatrix extends StepTransformable {

	private StepExpression[][] data;
	private Determinant determinant = new Determinant();

	private boolean isAugmented;

	public StepMatrix(StepExpression[][] data) {
		this.data = new StepExpression[data.length][data[0].length];
		for (int i = 0; i < data.length; i++) {
			System.arraycopy(data[i], 0, this.data[i], 0, data[i].length);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StepMatrix) {
			StepMatrix sm = (StepMatrix) obj;

			return isAugmented == sm.isAugmented && Arrays.deepEquals(data, sm.data);
		}

		return false;
	}

	@Override
	public int hashCode() {
		int result = Arrays.deepHashCode(data);
		result = 31 * result + (isAugmented ? 1 : 0);
		return result;
	}

	@Override
	public void setColor(int color) {
		this.color = color;
		for (StepExpression[] row : data) {
			for (StepExpression elem : row) {
				elem.setColor(color);
			}
		}
	}

	@Override
	public boolean isOperation(Operation operation) {
		return false;
	}

	@Override
	public boolean contains(Operation op) {
		for (StepExpression[] row : data) {
			for (StepExpression elem : row) {
				if (elem.contains(op)) {
					return true;
				}
			}
		}

		return false;
	}

	public int getHeight() {
		return data.length;
	}

	public int getWidth() {
		return data[0].length;
	}

	public StepExpression get(int i, int j) {
		return data[i][j];
	}

	public void set(int i, int j, StepExpression value) {
		data[i][j] = value;
	}

	public Determinant getDeterminant() {
		if (getWidth() != getHeight()) {
			return null;
		}

		return determinant;
	}

	public void setAugmented() {
		isAugmented = true;
	}

	@Override
	public StepMatrix regroup() {
		return regroup(null);
	}

	@Override
	public StepMatrix regroup(SolutionBuilder steps) {
		return (StepMatrix) StepStrategies.defaultRegroup(this, steps);
	}

	@Override
	public StepSolvable toSolvable() {
		return null;
	}

	@Override
	public int maxDecimal() {
		return 0;
	}

	@Override
	public boolean containsFractions() {
		return false;
	}

	public StepMatrix addRow(int i, int j, StepExpression coefficient, SolutionBuilder steps) {
		SolutionBuilder tempSteps = new SolutionBuilder();

		StepMatrix result = deepCopy();

		coefficient.setColor(1);
		for (int k = 0; k < getWidth(); k++) {
			data[i][k].setColor(2);
			result.data[i][k].setColor(2);
			result.data[j][k] = add(data[j][k], multiply(coefficient, data[i][k]));
		}

		tempSteps.addSubstep(this, result, SolutionStepType.MULTIPLY_EACH_ELEMENT_AND_ADD,
				StepConstant.create(i + 1), coefficient, StepConstant.create(j + 1));

		result = result.regroup(tempSteps);

		steps.addGroup(SolutionStepType.MULTIPLY_ROW_AND_ADD, tempSteps, result,
				StepConstant.create(i + 1), coefficient, StepConstant.create(j + 1));

		cleanColors();
		return result;
	}

	public StepMatrix divideRow(int i, StepExpression coefficient, SolutionBuilder steps) {
		SolutionBuilder tempSteps = new SolutionBuilder();

		StepMatrix result = deepCopy();

		coefficient.setColor(1);
		for (int j = 0; j < getWidth(); j++) {
			result.data[i][j] = divide(data[i][j], coefficient);
		}

		tempSteps.addSubstep(this, result, SolutionStepType.DIVIDE_EACH_ELEMENT,
				StepConstant.create(i + 1), coefficient);

		result = result.regroup(tempSteps);

		steps.addGroup(SolutionStepType.DIVIDE_ROW, tempSteps, result, StepConstant.create(i + 1),
				coefficient);

		return result;
	}

	@Override
	public StepMatrix deepCopy() {
		StepMatrix sm = new StepMatrix(data);
		sm.color = color;
		if (isAugmented) {
			sm.setAugmented();
		}
		return sm;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (StepExpression[] row : data) {
			sb.append("\n");
			for (StepExpression element : row) {
				sb.append(element);
				sb.append(" ");
			}
		}

		return sb.toString();
	}

	@Override
	public String toLaTeXString(Localization loc, boolean colored) {
		String output = "";

		if (colored && color != 0) {
			output += "\\fgcolor{" + getColorHex() + "}";
		}

		output += "\\left(";
		output += "\\begin{array}";

		if (isAugmented) {
			output += "{*{" + (data[0].length - 1) + "}{c} | {c}}";
		}

		if (colored && color != 0) {
			output += convertToString(loc, false);
		} else {
			output += convertToString(loc, colored);
		}

		output += "\\end{array}";
		output += "\\right)";

		return output;
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

	public class Determinant extends StepNode {

		@Override
		public StepNode deepCopy() {
			return StepMatrix.this.deepCopy().getDeterminant();
		}

		@Override
		public String toLaTeXString(Localization loc, boolean colored) {
			if (colored && color != 0) {
				return "\\fgcolor{" + StepMatrix.this.getColorHex() + "}{\\begin{vmatrix}"
						+ convertToString(loc, false) + "\\end{vmatrix}}";
			}

			return "\\begin{vmatrix}" + convertToString(loc, colored) + "\\end{vmatrix}";
		}

		public StepExpression calculateDeterminant(SolutionBuilder steps) {
			int color = 1;
			for (StepExpression[] row : data) {
				for (StepExpression element : row) {
					element.setColor(color++);
				}
			}

			StepExpression sum;

			if (data.length == 2) {
				sum = multiply(data[0][0], data[1][1]);
				sum = subtract(sum, multiply(data[0][1], data[1][0]));
			} else if (data.length == 3) {
				sum = StepOperation.multiply(data[0][0], data[1][1], data[2][2]);
				sum = add(sum, StepOperation.multiply(data[0][1], data[1][2], data[2][0]));
				sum = add(sum, StepOperation.multiply(data[0][2], data[1][0], data[2][1]));
				sum = subtract(sum, StepOperation.multiply(data[0][2], data[1][1], data[2][0]));
				sum = subtract(sum, StepOperation.multiply(data[0][1], data[1][0], data[2][2]));
				sum = subtract(sum, StepOperation.multiply(data[0][0], data[1][2], data[2][1]));
			} else {
				throw new SolveFailedException("determinant size not supported");
			}

			steps.addSubstep(this, sum, SolutionStepType.USE_LEIBNIZ_FORMULA);
			sum = sum.regroup(steps);

			for (StepExpression[] row : data) {
				for (StepExpression element : row) {
					element.cleanColors();
				}
			}

			return sum;
		}
	}

	@Override
	public StepTransformable iterateThrough(SimplificationStepGenerator step, SolutionBuilder sb,
			RegroupTracker tracker) {
		StepMatrix result = deepCopy();
		for (int i = 0; i < getHeight(); i++) {
			for (int j = 0; j < getWidth(); j++) {
				StepExpression elem = (StepExpression) step.apply(data[i][j], sb, tracker);
				result.data[i][j] = elem;
			}
		}

		return result;
	}
}
