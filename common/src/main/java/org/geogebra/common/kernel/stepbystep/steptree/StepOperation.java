package org.geogebra.common.kernel.stepbystep.steptree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.steps.RegroupTracker;
import org.geogebra.common.kernel.stepbystep.steps.SimplificationStepGenerator;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Operation;

public class StepOperation extends StepExpression implements Iterable<StepExpression> {

	private final Operation operation;
	private final StepExpression[] operands;
	private StepExpression[] sortedOperandList;

	private StepOperation(Operation operation, List<StepExpression> operands) {
		this.operation = operation;
		this.operands = operands.toArray(new StepExpression[0]);
	}

	/**
	 * Use it when you are sure you do not require collapsing of the operands
	 * @param operation Operation of the node
	 * @param operands operands of the node
	 */
	public StepOperation(Operation operation, StepExpression... operands) {
		this.operation = operation;
		this.operands = operands;
	}

	/**
	 * Use this, when you are doing an operation-agnostic change in the
	 * structure, that might or might not require collapsing, such as
	 * replacing
	 * @param operation Operation of the node
	 * @param operands of the node (nullable)
	 * @return StepExpression after collapsing
	 */
	public static StepExpression create(Operation operation, StepExpression... operands) {
		if (operation == Operation.PLUS || operation == Operation.MULTIPLY) {
			List<StepExpression> operandsList = new ArrayList<>();
			for (StepExpression operand : operands) {
				if (operand != null) {
					if (operand.isOperation(operation)) {
						Collections.addAll(operandsList, ((StepOperation) operand).operands);
					} else {
						operandsList.add(operand);
					}
				}
			}

			if (operandsList.size() == 0) {
				return null;
			}

			if (operandsList.size() == 1) {
				return operandsList.get(0);
			}

			return new StepOperation(operation, operandsList);
		} else {
			return new StepOperation(operation, operands);
		}
	}

	public static StepExpression add(List<StepExpression> terms) {
		return create(Operation.PLUS, terms.toArray(new StepExpression[0]));
	}

	/**
	 * The safe way to add up multiple terms
	 * @param terms StepExpressions to add, nullable, can contain other sums
	 * @return collapsed result
	 */
	public static StepExpression add(StepExpression... terms) {
		return create(Operation.PLUS, terms);
	}

	public static StepExpression multiply(List<StepExpression> multiplicands) {
		return create(Operation.MULTIPLY, multiplicands.toArray(new StepExpression[0]));
	}

	public static StepExpression multiply(StepExpression... multiplicands) {
		return create(Operation.MULTIPLY, multiplicands);
	}

	private static boolean requiresPlus(StepExpression a) {
		return !a.isOperation(Operation.MINUS) && !a.isOperation(Operation.PLUSMINUS);
	}

	private static boolean requiresDot(StepExpression a, StepExpression b) {
		return b.nonSpecialConstant() || (a instanceof StepVariable && a.equals(b))
				|| a.isInteger() && b.isFraction()
				|| (b.isOperation(Operation.POWER)
						&& requiresDot(a, ((StepOperation) b).getOperand(0)));
	}

	public int noOfOperands() {
		return operands.length;
	}

	public StepExpression getOperand(int index) {
		return operands[index];
	}

	public Operation getOperation() {
		return operation;
	}

	@Override
	public Iterator<StepExpression> iterator() {
		return new Iterator<StepExpression>() {
			private int it = 0;

			@Override
			public boolean hasNext() {
				return it < operands.length;
			}

			@Override
			public StepExpression next() throws NoSuchElementException {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}

				return operands[it++];
			}

			@Override
			public void remove() {
				// not possible
			}
		};
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + operation.hashCode();
		result = prime * result + Arrays.hashCode(operands);
		return result;
	}

	@Override
	public void setColor(int color) {
		this.color = color;
		for (StepExpression operand : operands) {
			operand.setColor(color);
		}
	}

	public StepExpression[] getSortedOperandList() {
		if (sortedOperandList == null) {
			StepOperation simpleCopy = simpleCopy();
			simpleCopy.sort();
			sortedOperandList = simpleCopy.operands;
		}
		return sortedOperandList;
	}

	private StepOperation simpleCopy() {
		StepExpression[] newOperands = new StepExpression[operands.length];
		for (int i = 0; i < operands.length; i++) {
			if (operands[i] instanceof StepOperation) {
				newOperands[i] = ((StepOperation) operands[i]).simpleCopy();
			} else {
				newOperands[i] = operands[i];
			}
		}

		return new StepOperation(operation, newOperands);
	}

	private void sort() {
		for (StepExpression operand : operands) {
			if (operand instanceof StepOperation) {
				((StepOperation) operand).sort();
			}
		}

		if (operation == Operation.PLUS || operation == Operation.MULTIPLY) {
			Arrays.sort(operands);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StepOperation) {
			StepOperation so = (StepOperation) obj;

			return so.operation == operation && so.operands.length == operands.length
					&& Arrays.equals(getSortedOperandList(), so.getSortedOperandList());
		}

		return false;
	}

	@Override
	public boolean isOperation(Operation operation) {
		return this.operation == operation;
	}

	@Override
	public boolean nonSpecialConstant() {
		return operation == Operation.MINUS && operands[0].nonSpecialConstant();
	}

	@Override
	public boolean specialConstant() {
		return false;
	}

	@Override
	public boolean isInteger() {
		return operation == Operation.MINUS && operands[0].isInteger();
	}

	@Override
	public boolean proveInteger() {
		switch (operation) {
		case PLUS:
		case MINUS:
		case MULTIPLY:
		case ABS:
			for (StepExpression operand : operands) {
				if (!operand.proveInteger()) {
					return false;
				}
			}
			return true;

		case POWER:
			return operands[0].proveInteger()
					&& operands[1].proveInteger()
					&& operands[1].sign() >= 0;

		default:
			return false;
		}
	}

	@Override
	public boolean isConstantIn(StepVariable sv) {
		for (StepExpression operand : operands) {
			if (!operand.isConstantIn(sv)) {
				return false;
			}
		}

		return operation != Operation.PLUSMINUS;
	}

	@Override
	public int degree(StepVariable var) {
		if (isConstantIn(var)) {
			return 0;
		}

		switch (operation) {
			case MINUS:
				if (countNonConstOperation(Operation.PLUS, var) > 0) {
					return -1;
				}

				return getOperand(0).degree(var);
			case PLUS:
				int max = 0;

				for (StepExpression operand : this) {
					int temp = operand.degree(var);
					if (temp == -1) {
						return -1;
					} else if (temp > max) {
						max = temp;
					}
				}

				return max;
			case POWER:
				int temp = getOperand(0).degree(var);

				if (temp != -1 && getOperand(1).isInteger()) {
					return (int) (temp * getOperand(1).getValue());
				}

				return -1;
			case MULTIPLY:
				if (countNonConstOperation(Operation.PLUS, var) > 0) {
					return -1;
				}

				int p = 0;

				for (StepExpression operand : this) {
					int tmp = operand.degree(var);
					if (tmp == -1) {
						return -1;
					}
					p += tmp;
				}

				return p;
			case DIVIDE:
				if (!getOperand(1).isConstantIn(var)) {
					return -1;
				}
				return getOperand(0).degree(var);
		}

		return -1;
	}

	@Override
	public boolean canBeEvaluated() {
		for (StepExpression operand : operands) {
			if (!operand.canBeEvaluated()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public double getValue() {
		switch (operation) {
			case PLUS:
				double s = 0;
				for (StepExpression operand : operands) {
					s += operand.getValue();
				}
				return s;
			case MINUS:
				return -operands[0].getValue();
			case MULTIPLY:
				double p = 1;
				for (StepExpression operand : operands) {
					p *= operand.getValue();
				}
				return p;
			case DIVIDE:
				return operands[0].getValue() / operands[1].getValue();
			case POWER:
				return Math.pow(operands[0].getValue(), operands[1].getValue());
			case NROOT:
				double base = operands[0].getValue();
				double exponent = operands[1].getValue();

				if (base < 0) {
					if (isOdd(exponent)) {
						return -Math.pow(-base, 1 / exponent);
					}
				}

				return Math.pow(base, 1 / exponent);
			case ABS:
				return Math.abs(operands[0].getValue());
			case SIN:
				return Math.sin(operands[0].getValue());
			case COS:
				return Math.cos(operands[0].getValue());
			case TAN:
				return Math.tan(operands[0].getValue());
			case ARCSIN:
				return Math.asin(operands[0].getValue());
			case ARCCOS:
				return Math.acos(operands[0].getValue());
			case ARCTAN:
				return Math.atan(operands[0].getValue());
		}
		return Double.NaN;
	}

	@Override
	public double getValueAt(StepVariable variable, double replaceWith) {
		switch (operation) {
			case PLUS:
				double s = 0;
				for (StepExpression operand : operands) {
					s += operand.getValueAt(variable, replaceWith);
				}
				return s;
			case MINUS:
				return -operands[0].getValueAt(variable, replaceWith);
			case MULTIPLY:
				double p = 1;
				for (StepExpression operand : operands) {
					p *= operand.getValueAt(variable, replaceWith);
				}
				return p;
			case DIVIDE:
				return operands[0].getValueAt(variable, replaceWith)
						/ operands[1].getValueAt(variable, replaceWith);
			case POWER:
				return Math.pow(operands[0].getValueAt(variable, replaceWith),
						operands[1].getValueAt(variable, replaceWith));
			case NROOT:
				return Math.pow(operands[0].getValueAt(variable, replaceWith),
						1 / operands[1].getValueAt(variable, replaceWith));
			case ABS:
				return Math.abs(operands[0].getValueAt(variable, replaceWith));
			case SIN:
				return Math.sin(operands[0].getValueAt(variable, replaceWith));
			case COS:
				return Math.cos(operands[0].getValueAt(variable, replaceWith));
			case TAN:
				return Math.tan(operands[0].getValueAt(variable, replaceWith));
			case ARCSIN:
				return Math.asin(operands[0].getValueAt(variable, replaceWith));
			case ARCCOS:
				return Math.acos(operands[0].getValueAt(variable, replaceWith));
			case ARCTAN:
				return Math.atan(operands[0].getValueAt(variable, replaceWith));
		}
		return Double.NaN;
	}

	@Override
	public String toString() {
		switch (operation) {
			case IS_ELEMENT_OF:
				return operands[0] + " in " + operands[1];
			case PLUS:
				StringBuilder ss = new StringBuilder();
				ss.append("(");
				for (int i = 0; i < operands.length; i++) {
					if (i != 0 && !operands[i].isOperation(Operation.MINUS)) {
						ss.append(" + ");
					}
					ss.append(operands[i].toString());
				}
				ss.append(")");
				return ss.toString();
			case MINUS:
				if (operands[0].isOperation(Operation.PLUS)
						|| operands[0].isOperation(Operation.MINUS)) {
					return "-(" + operands[0].toString() + ")";
				}
				return "-" + operands[0].toString();
			case PLUSMINUS:
				return "pm(" + operands[0].toString() + ")";
			case MULTIPLY:
				StringBuilder sp = new StringBuilder();
				for (StepExpression operand : operands) {
					sp.append("(");
					sp.append(operand.toString());
					sp.append(")");
				}
				return sp.toString();
			case DIVIDE:
				return "(" + operands[0].toString() + ")/(" + operands[1].toString() + ")";
			case POWER:
				return "(" + operands[0].toString() + ")^(" + operands[1].toString() + ")";
			case NROOT:
				return "nroot(" + operands[0].toString() + ", " + operands[1].toString() + ")";
			case ABS:
				return "|" + operands[0].toString() + "|";
			case SIN:
			case COS:
			case TAN:
			case CSC:
			case SEC:
			case COT:
			case ARCSIN:
			case ARCCOS:
			case ARCTAN:
				return operation.toString().toLowerCase() + "(" + operands[0].toString() + ")";
			case DIFF:
				return "d/d" + operands[1].toString() + "(" + operands[0].toString() + ")";
			case LOG:
				return "log_(" + operands[0].toString() + ")(" + operands[1].toString() + ")";
		}
		return "";
	}

	@Override
	public String toLaTeXString(Localization loc) {
		return toLaTeXString(loc, false);
	}

	@Override
	public String toLaTeXString(Localization loc, boolean colored) {
		if (colored && color != 0) {
			return "\\fgcolor{" + getColorHex() + "}{" + convertToString(loc, false) + "}";
		}
		return convertToString(loc, colored);
	}

	private String convertToString(Localization loc, boolean colored) {
		switch (operation) {
			case IS_ELEMENT_OF:
				return operands[0].toLaTeXString(loc, colored) + " \\in "
						+ operands[1].toLaTeXString(loc, colored);
			case PLUS:
				StringBuilder ss = new StringBuilder();
				for (int i = 0; i < operands.length; i++) {
					if (i != 0 && requiresPlus(operands[i])) {
						ss.append(" + ");
					}
					ss.append(operands[i].toLaTeXString(loc, colored));
				}
				if (operands.length == 0) {
					ss.append("0");
				}
				return ss.toString();
			case MINUS:
				if (operands[0].isOperation(Operation.PLUS) 
						|| operands[0].isOperation(Operation.MINUS)) {
					return "-\\left(" + operands[0].toLaTeXString(loc, colored) + "\\right)";
				}
				return "-" + operands[0].toLaTeXString(loc, colored);
			case PLUSMINUS:
				if (operands[0].isOperation(Operation.PLUS) 
						|| operands[0].isOperation(Operation.MINUS)) {
					return "\\pm\\left(" + operands[0].toLaTeXString(loc, colored) + "\\right)";
				}
				return "\\pm " + operands[0].toLaTeXString(loc, colored);
			case MULTIPLY:
				StringBuilder sp = new StringBuilder();
				for (int i = 0; i < operands.length; i++) {
					StepExpression current = operands[i];
					if (i != 0 && requiresDot(operands[i - 1], current)) {
						sp.append(" \\cdot ");
					} else if (i != 0) {
						sp.append(" ");
					}
					boolean last = i == operands.length - 1;
					boolean parentheses =
							current.isOperation(Operation.PLUS) || current.isNegative()
									|| (current.isOperation(Operation.DIFF)	&& !last);

					if (parentheses) {
						sp.append("\\left(");
					}
					sp.append(current.toLaTeXString(loc, colored));
					if (parentheses) {
						sp.append("\\right)");
					}
				}
				return sp.toString();
			case DIVIDE:
				return "\\frac{" + operands[0].toLaTeXString(loc, colored) + "}{"
						+ operands[1].toLaTeXString(loc, colored) + "}";
			case POWER:
				if (operands[0].isNegative() || (operands[0] instanceof StepOperation
						&& !operands[0].isOperation(Operation.NROOT))) {
					return "\\left(" + operands[0].toLaTeXString(loc, colored) + "\\right)^{"
							+ operands[1].toLaTeXString(loc, colored) + "}";
				}
				return operands[0].toLaTeXString(loc, colored) + "^{"
						+ operands[1].toLaTeXString(loc, colored) + "}";
			case NROOT:
				if (isSquareRoot()) {
					return "\\sqrt{" + operands[0].toLaTeXString(loc, colored) + "}";
				}
				return "\\sqrt[" + operands[1].toLaTeXString(loc, colored) + "]{"
						+ operands[0].toLaTeXString(loc, colored) + "}";
			case ABS:
				return "\\left|" + operands[0].toLaTeXString(loc, colored) + "\\right|";
			case SIN:
			case COS:
			case TAN:
			case CSC:
			case SEC:
			case COT:
			case ARCSIN:
			case ARCCOS:
			case ARCTAN:
				return "\\" + loc.getFunction(operation.toString().toLowerCase()) + "\\left("
						+ operands[0].toLaTeXString(loc, colored) + "\\right)";
			case LOG:
				if (isNaturalLog()) {
					return "\\ln \\left(" + operands[1].toLaTeXString(loc, colored) + "\\right)";
				}
				return "\\log_{" + operands[0].toLaTeXString(loc, colored) + "} \\left("
						+ operands[1].toLaTeXString(loc, colored) + "\\right)";
			case DIFF:
				StringBuilder sb = new StringBuilder();

				if (loc.primeNotation()) {
					sb.append("\\left(");
					sb.append(operands[0].toLaTeXString(loc, colored));
					sb.append("\\right)");
					sb.append("'");
					return sb.toString();
				}

				sb.append("\\frac{d}{d");
				sb.append(operands[1].toLaTeXString(loc, colored));
				sb.append("}");
				boolean composite = operands[0].isNegative()
						|| operands[0].isOperation(Operation.PLUS)
						|| operands[0].isOperation(Operation.MULTIPLY);
				if (composite) {
					sb.append("\\left(");
				}
				sb.append(operands[0].toLaTeXString(loc, colored));
				if (composite) {
					sb.append("\\right)");
				}
				return sb.toString();

		}
		return "";
	}

	@Override
	public StepOperation deepCopy() {
		StepExpression[] newOperands = new StepExpression[operands.length];
		for (int i = 0; i < operands.length; i++) {
			newOperands[i] = operands[i].deepCopy();
		}

		StepOperation so = new StepOperation(operation, newOperands);
		so.color = color;

		return so;
	}

	@Override
	public StepExpression getCoefficientIn(StepVariable sv) {
		if (isConstantIn(sv)) {
			return this;
		}

		if (isOperation(Operation.MULTIPLY)) {
			StepExpression[] coefficient = new StepExpression[noOfOperands()];
			for (int i = 0; i < noOfOperands(); i++) {
				coefficient[i] = operands[i].getCoefficientIn(sv);
			}
			return multiply(coefficient);
		} else if (isOperation(Operation.MINUS)) {
			StepExpression coefficient = getOperand(0).getCoefficientIn(sv);
			if (coefficient == null) {
				return StepConstant.create(-1);
			}
			return new StepOperation(Operation.MINUS, coefficient);
		}

		return null;
	}

	@Override
	public StepExpression getVariableIn(StepVariable sv) {
		if (isConstantIn(sv)) {
			return null;
		}

		if (isOperation(Operation.MULTIPLY)) {
			StepExpression[] variable = new StepExpression[noOfOperands()];
			for (int i = 0; i < noOfOperands(); i++) {
				variable[i] = operands[i].getVariableIn(sv);
			}
			return multiply(variable);
		} else if (isOperation(Operation.MINUS)) {
			return getOperand(0).getVariableIn(sv);
		}

		return this;
	}

	@Override
	public StepExpression getIntegerCoefficient() {
		switch (operation) {
			case MINUS:
				StepExpression sm = getOperand(0).getIntegerCoefficient();
				if (sm == null) {
					return StepConstant.create(-1);
				}
				return new StepOperation(Operation.MINUS, sm);
			case PLUSMINUS:
				return getOperand(0).getIntegerCoefficient();
			case MULTIPLY:
				StepExpression[] coefficient = new StepExpression[noOfOperands()];
				for (int i = 0; i < noOfOperands(); i++) {
					coefficient[i] = operands[i].getIntegerCoefficient();
				}
				return multiply(coefficient);
		}
		return null;
	}

	@Override
	public StepExpression getNonInteger() {
		switch (operation) {
			case MINUS:
				return getOperand(0).getNonInteger();
			case PLUSMINUS:
				StepExpression sm = getOperand(0).getNonInteger();
				if (sm == null) {
					sm = StepConstant.create(1);
				}
				return new StepOperation(Operation.PLUSMINUS, sm);
			case MULTIPLY:
				StepExpression[] nonInteger = new StepExpression[noOfOperands()];
				for (int i = 0; i < noOfOperands(); i++) {
					nonInteger[i] = operands[i].getNonInteger();
				}
				return multiply(nonInteger);
		}
		return this;
	}

	@Override
	public StepTransformable iterateThrough(SimplificationStepGenerator step, SolutionBuilder sb,
			RegroupTracker tracker) {
		int colorsAtStart = tracker.getColorTracker();

		StepExpression[] toReturn = null;
		for (int i = 0; i < operands.length; i++) {
			StepExpression a = (StepExpression) step.apply(operands[i], sb, tracker);
			if (a.isUndefined()) {
				return a;
			}

			if (toReturn == null && tracker.getColorTracker() > colorsAtStart) {
				toReturn = new StepExpression[operands.length];

				System.arraycopy(operands, 0, toReturn, 0, i);
			}
			if (toReturn != null) {
				toReturn[i] = a;
			}
		}

		if (toReturn == null) {
			return this;
		}

		return create(operation, toReturn);
	}
}
