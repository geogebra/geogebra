package org.geogebra.common.kernel.stepbystep.solution;

import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.steps.SolveTracker;
import org.geogebra.common.kernel.stepbystep.steptree.*;
import org.geogebra.common.main.Localization;

import java.util.ArrayList;
import java.util.List;

import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.divide;

public class SolutionTable extends SolutionStep {

	private StepExpression[] header;
	private List<List<TableElement>> rows;

	public SolutionTable(StepExpression... header) {
		this.header = header;
		rows = new ArrayList<>();
	}

	public static SolutionTable createSignTable(StepVariable variable, List<StepExpression> roots,
			List<StepExpression> expressions) {
		StepExpression[] header = new StepExpression[1 + roots.size()];
		header[0] = variable;
		for (int i = 0; i < roots.size(); i++) {
			header[i + 1] = roots.get(i);
		}

		SolutionTable table = new SolutionTable(header);

		for (StepExpression expression : expressions) {
			List<TableElement> row = new ArrayList<>();
			row.add(expression);
			for (int i = 0; i < roots.size(); i++) {
				double value = expression.getValueAt(variable, roots.get(i).getValue());
				if (StepNode.isEqual(value, 0)) {
					row.add(TableElementType.ZERO);
				} else if (value < 0) {
					row.add(TableElementType.NEGATIVE);
				} else {
					row.add(TableElementType.POSITIVE);
				}

				if (i == roots.size() - 1) {
					break;
				}

				if (StepHelper.isNegative(expression, roots.get(i), roots.get(i + 1), variable)) {
					row.add(TableElementType.NEGATIVE);
				} else {
					row.add(TableElementType.POSITIVE);
				}
			}
			table.addRow(row);
		}

		return table;
	}

	public void addRow(List<TableElement> row) {
		rows.add(row);
	}

	public void addInequalityRow(StepExpression numerator, StepExpression denominator) {
		List<TableElement> newRow = new ArrayList<>();

		StepExpression expression = divide(numerator, denominator);
		newRow.add(expression);

		for (int j = 1; j < rows.get(0).size(); j++) {
			boolean isInvalid = false;
			boolean isZero = false;
			boolean isNegative = false;

			for (List<TableElement> row : rows) {
				if (row.get(j) == TableElementType.ZERO) {
					if (denominator != null &&
							denominator.containsExpression((StepExpression) row.get(0))) {
						isInvalid = true;
					}
					isZero = true;
				} else if (row.get(j) == TableElementType.NEGATIVE) {
					isNegative = !isNegative;
				}
			}

			StepNode value;
			if (j % 2 == 0) {
				value = new StepInterval(header[j / 2], header[j / 2 + 1], false, false);
			} else {
				value = header[j / 2 + 1];
			}

			SolutionStepType type;
			if (isInvalid) {
				newRow.add(TableElementType.INVALID);
				type = SolutionStepType.IS_INVALID_IN;
			} else if (isZero) {
				newRow.add(TableElementType.ZERO);
				type = SolutionStepType.IS_ZERO_IN;
			} else if (isNegative) {
				newRow.add(TableElementType.NEGATIVE);
				type = SolutionStepType.IS_NEGATIVE_IN_INEQUALITY;
			} else {
				newRow.add(TableElementType.POSITIVE);
				type = SolutionStepType.IS_POSITIVE_IN_INEQUALITY;
			}

			if (!value.equals(StepConstant.POS_INF) && !value.equals(StepConstant.NEG_INF)) {
				addSubStep(new SolutionLine(type, expression, value));
			}
		}

		addRow(newRow);
	}

	public List<StepSolution> readSolution(StepInequality si, StepVariable variable,
			SolveTracker tracker) {
		List<StepInterval> intervals = new ArrayList<>();

		List<TableElement> row = rows.get(rows.size() - 1);
		for (int i = 2; i < row.size(); i += 2) {
			if (si.isLessThan() == (row.get(i) == TableElementType.NEGATIVE)) {
				StepExpression left = header[i / 2];
				StepExpression right = header[i / 2 + 1];

				intervals.add(new StepInterval(left, right,
						!si.isStrong() && row.get(i - 1) == TableElementType.ZERO,
						!si.isStrong() && row.get(i + 1) == TableElementType.ZERO));
			}
		}

		for (int i = 0; i < intervals.size() - 1; i++) {
			if (intervals.get(i).getRightBound().equals(intervals.get(i + 1).getLeftBound()) &&
					intervals.get(i).isClosedRight()) {
				intervals.set(i, new StepInterval(intervals.get(i).getLeftBound(),
						intervals.get(i + 1).getRightBound(), intervals.get(i).isClosedLeft(),
						intervals.get(i + 1).isClosedRight()));
				intervals.remove(i + 1);
				i--;
			}
		}

		List<StepSolution> solutions = new ArrayList<>();

		for (StepInterval interval : intervals) {
			solutions.add(StepSolution.simpleSolution(variable, interval, tracker));
		}

		return solutions;
	}

	@Override
	public List<TextElement> getDefault(Localization loc) {
		StringBuilder sb = new StringBuilder();

		sb.append("$\\begin{tabular}{r | *{");
		sb.append(header.length * 2 - 3);
		sb.append("}{c}}");

		for (int i = 0; i < header.length; i++) {
			if (i > 0) {
				sb.append(" & ");
			}
			if (i > 1) {
				sb.append(" \\; & ");
			}
			sb.append(header[i].toLaTeXString(loc));
		}
		sb.append(" \\\\ ");
		sb.append(" \\hline ");

		for (List<TableElement> row : rows) {
			for (int i = 0; i < row.size(); i++) {
				if (i != 0) {
					sb.append(" & ");
				}
				sb.append(row.get(i).toLaTeXString(loc));
			}
			sb.append(" \\\\ ");
		}

		sb.append("\\end{tabular}$");

		List<TextElement> result = new ArrayList<>();
		result.add(new TextElement(sb.toString(), sb.toString()));
		return result;
	}

	@Override
	public List<TextElement> getDetailed(Localization loc) {
		return getDefault(loc);
	}
}
