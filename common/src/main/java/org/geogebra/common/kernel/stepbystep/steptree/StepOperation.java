package org.geogebra.common.kernel.stepbystep.steptree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Operation;

public class StepOperation extends StepExpression implements Iterable<StepExpression> {

	private Operation operation;
	private List<StepExpression> operands;

	public StepOperation(Operation op) {
		operation = op;
		operands = new ArrayList<>();
	}

	public int noOfOperands() {
		return operands.size();
	}

	public StepExpression getOperand(int index) {
		return operands.get(index);
	}

	public Operation getOperation() {
		return operation;
	}

	public void addOperand(StepExpression sn) {
		if (sn != null) {
			if (isOperation(Operation.PLUS) && sn.isOperation(Operation.PLUS)
					|| isOperation(Operation.MULTIPLY) && sn.isOperation(Operation.MULTIPLY)) {
				for (StepExpression operand : (StepOperation) sn) {
					addOperand(operand);
				}
			} else {
				operands.add(sn);
			}
		}
	}

	@Override
	public Iterator<StepExpression> iterator() {
		return operands.iterator();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((operation == null) ? 0 : operation.hashCode());
		result = prime * result + ((operands == null) ? 0 : operands.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StepOperation) {
			StepOperation so = (StepOperation) obj;

			if (so.operation != operation || so.operands.size() != operands.size()) {
				return false;
			}

			StepOperation copyOfThis = deepCopy().sort();
			StepOperation copyOfThat = so.deepCopy().sort();

			return copyOfThis.operands.equals(copyOfThat.operands);
		}

		return false;
	}

	/**
	 * Sorts the operands, recursively, so things like 3*4+5 and 5+4*3 will be equal.
	 * The actual order is not important - only consistency. That is why hashCode is okay for this
	 */
	public StepOperation sort() {
		for (StepExpression operand : this) {
			if (operand instanceof StepOperation) {
				((StepOperation) operand).sort();
			}
		}

		if (isOperation(Operation.PLUS) || isOperation(Operation.MULTIPLY)) {
			operands.sort(new Comparator<StepExpression>() {
				@Override
				public int compare(StepExpression o1, StepExpression o2) {
					return o1.hashCode() - o2.hashCode();
				}
			});
		}

		return this;
	}

	@Override
	public boolean isConstantIn(StepVariable sv) {
		for (StepExpression operand : operands) {
			if (!operand.isConstantIn(sv)) {
				return false;
			}
		}

		return true;
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
				if (!getOperand(1).isConstant()) {
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
			return -operands.get(0).getValue();
		case MULTIPLY:
			double p = 1;
			for (StepExpression operand : operands) {
				p *= operand.getValue();
			}
			return p;
		case DIVIDE:
			return operands.get(0).getValue() / operands.get(1).getValue();
		case POWER:
			return Math.pow(operands.get(0).getValue(), operands.get(1).getValue());
		case NROOT:
			double base = operands.get(0).getValue();
			double exponent = operands.get(1).getValue();

			if (base < 0) {
				if (isOdd(exponent)) {
					return -Math.pow(-base, 1 / exponent);
				}
			}

			return Math.pow(base, 1 / exponent);
		case ABS:
			return Math.abs(operands.get(0).getValue());
		case SIN:
			return Math.sin(operands.get(0).getValue());
		case COS:
			return Math.cos(operands.get(0).getValue());
		case TAN:
			return Math.tan(operands.get(0).getValue());
		case ARCSIN:
			return Math.asin(operands.get(0).getValue());
		case ARCCOS:
			return Math.acos(operands.get(0).getValue());
		case ARCTAN:
			return Math.atan(operands.get(0).getValue());
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
			return -operands.get(0).getValueAt(variable, replaceWith);
		case MULTIPLY:
			double p = 1;
			for (StepExpression operand : operands) {
				p *= operand.getValueAt(variable, replaceWith);
			}
			return p;
		case DIVIDE:
			return operands.get(0).getValueAt(variable, replaceWith)
					/ operands.get(1).getValueAt(variable, replaceWith);
		case POWER:
			return Math.pow(operands.get(0).getValueAt(variable, replaceWith),
					operands.get(1).getValueAt(variable, replaceWith));
		case NROOT:
			return Math.pow(operands.get(0).getValueAt(variable, replaceWith),
					1 / operands.get(1).getValueAt(variable, replaceWith));
		case ABS:
			return Math.abs(operands.get(0).getValueAt(variable, replaceWith));
		case SIN:
			return Math.sin(operands.get(0).getValueAt(variable, replaceWith));
		case COS:
			return Math.cos(operands.get(0).getValueAt(variable, replaceWith));
		case TAN:
			return Math.tan(operands.get(0).getValueAt(variable, replaceWith));
		case ARCSIN:
			return Math.asin(operands.get(0).getValueAt(variable, replaceWith));
		case ARCCOS:
			return Math.acos(operands.get(0).getValueAt(variable, replaceWith));
		case ARCTAN:
			return Math.atan(operands.get(0).getValueAt(variable, replaceWith));
		}
		return Double.NaN;
	}

	@Override
	public String toString() {
		switch (operation) {
		case IS_ELEMENT_OF:
			return operands.get(0) + " in " + operands.get(1);
		case PLUS:
			StringBuilder ss = new StringBuilder();
			ss.append("(");
			for (int i = 0; i < operands.size(); i++) {
				if (i != 0 && !operands.get(i).isOperation(Operation.MINUS)) {
					ss.append(" + ");
				}
				ss.append(operands.get(i).toString());
			}
			if (operands.size() == 0) {
				ss.append("0");
			}
			ss.append(")");
			return ss.toString();
		case MINUS:
			if (operands.get(0).isOperation(Operation.PLUS) || operands.get(0).isOperation(Operation.MINUS)) {
				return "-(" + operands.get(0).toString() + ")";
			}
			return "-" + operands.get(0).toString();
		case PLUSMINUS:
			return "pm(" + operands.get(0).toString() + ")";
		case MULTIPLY:
			StringBuilder sp = new StringBuilder();
			for (StepExpression operand : operands) {
				sp.append("(");
				sp.append(operand.toString());
				sp.append(")");
			}
			return sp.toString();
		case DIVIDE:
			return "(" + operands.get(0).toString() + ")/(" + operands.get(1).toString() + ")";
		case POWER:
			return "(" + operands.get(0).toString() + ")^(" + operands.get(1).toString() + ")";
		case NROOT:
			return "nroot(" + operands.get(0).toString() + ", " + operands.get(1).toString() + ")";
		case ABS:
			return "|" + operands.get(0).toString() + "|";
		case SIN:
		case COS:
		case TAN:
		case CSC:
		case SEC:
		case COT:
		case ARCSIN:
		case ARCCOS:
		case ARCTAN:
			return operation.toString().toLowerCase() + "(" + operands.get(0).toString() + ")";
		case DIFF:
			return "d/d" + operands.get(1).toString() + "(" + operands.get(0).toString() + ")";
		case LOG:
			return "log_(" + operands.get(0).toString() + ")(" + operands.get(1).toString() + ")";
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
			return operands.get(0).toLaTeXString(loc, colored) + " \\in " + operands.get(1).toLaTeXString(loc, colored);
		case PLUS:
			StringBuilder ss = new StringBuilder();
			for (int i = 0; i < operands.size(); i++) {
				if (i != 0 && requiresPlus(operands.get(i))) {
					ss.append(" + ");
				}
				ss.append(operands.get(i).toLaTeXString(loc, colored));
			}
			if (operands.size() == 0) {
				ss.append("0");
			}
			return ss.toString();
		case MINUS:
			if (operands.get(0).isOperation(Operation.PLUS) || operands.get(0).isOperation(Operation.MINUS)) {
				return "-\\left(" + operands.get(0).toLaTeXString(loc, colored) + "\\right)";
			}
			return "-" + operands.get(0).toLaTeXString(loc, colored);
		case PLUSMINUS:
			if (operands.get(0).isOperation(Operation.PLUS) || operands.get(0).isOperation(Operation.MINUS)) {
				return "\\pm\\left(" + operands.get(0).toLaTeXString(loc, colored) + "\\right)";
			}
			return "\\pm " + operands.get(0).toLaTeXString(loc, colored);
		case MULTIPLY:
			StringBuilder sp = new StringBuilder();
			for (int i = 0; i < operands.size(); i++) {
				if (i != 0 && requiresDot(operands.get(i - 1), operands.get(i))) {
					sp.append(" \\cdot ");
				} else if (i != 0) {
					sp.append(" ");
				}

				boolean parentheses = operands.get(i).isOperation(Operation.PLUS) || operands.get(i).isNegative();

				if (parentheses) {
					sp.append("\\left(");
				}
				sp.append(operands.get(i).toLaTeXString(loc, colored));
				if (parentheses) {
					sp.append("\\right)");
				}
			}
			return sp.toString();
		case DIVIDE:
			return "\\frac{" + operands.get(0).toLaTeXString(loc, colored) + "}{"
					+ operands.get(1).toLaTeXString(loc, colored) + "}";
		case POWER:
			if (operands.get(0).isNegative()
					|| (operands.get(0) instanceof StepOperation && !operands.get(0).isOperation(Operation.NROOT))) {
				return "\\left(" + operands.get(0).toLaTeXString(loc, colored) + "\\right)^{"
						+ operands.get(1).toLaTeXString(loc, colored) + "}";
			}
			return operands.get(0).toLaTeXString(loc, colored) + "^{" + operands.get(1).toLaTeXString(loc, colored)
					+ "}";
		case NROOT:
			if (isSquareRoot()) {
				return "\\sqrt{" + operands.get(0).toLaTeXString(loc, colored) + "}";
			}
			return "\\sqrt[" + operands.get(1).toLaTeXString(loc, colored) + "]{"
					+ operands.get(0).toLaTeXString(loc, colored) + "}";
		case ABS:
			return "\\left|" + operands.get(0).toLaTeXString(loc, colored) + "\\right|";
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
					+ operands.get(0).toLaTeXString(loc, colored) + "\\right)";
		case LOG:
			if (isNaturalLog()) {
				return "\\ln \\left(" + operands.get(1).toLaTeXString(loc, colored) + "\\right)";
			}
			return "\\log_{" + operands.get(0).toLaTeXString(loc, colored) + "} \\left("
					+ operands.get(1).toLaTeXString(loc, colored) + "\\right)";
		case DIFF:
			StringBuilder sb = new StringBuilder();

			if (loc.primeNotation()) {
				sb.append("\\left(");
				sb.append(operands.get(0).toLaTeXString(loc, colored));
				sb.append("\\right)");
				sb.append("'");
				return sb.toString();
			}

			sb.append("\\frac{d}{d");
			sb.append(operands.get(1).toLaTeXString(loc, colored));
			sb.append("}");
			if (operands.get(0).isOperation(Operation.PLUS)) {
				sb.append("\\left(");
			}
			sb.append(operands.get(0).toLaTeXString(loc, colored));
			if (operands.get(0).isOperation(Operation.PLUS)) {
				sb.append("\\right)");
			}
			return sb.toString();
			
		}
		return "";
	}

	private static boolean requiresPlus(StepExpression a) {
		return !a.isOperation(Operation.MINUS) && !a.isOperation(Operation.PLUSMINUS);
	}

	private static boolean requiresDot(StepExpression a, StepExpression b) {
		return b.nonSpecialConstant()
				|| (a instanceof StepVariable && a.equals(b))
				|| (b.isOperation(Operation.POWER) && requiresDot(a, ((StepOperation) b).getOperand(0)));
	}

	@Override
	public StepOperation deepCopy() {
		StepOperation so = new StepOperation(operation);
		so.color = color;
		for (StepExpression operand : operands) {
			so.addOperand(operand.deepCopy());
		}
		return so;
	}

	@Override
	public StepExpression getCoefficientIn(StepVariable sv) {
		if (isConstantIn(sv)) {
			return this;
		}

		if (isOperation(Operation.MULTIPLY)) {
			StepOperation coefficient = new StepOperation(Operation.MULTIPLY);
			for (StepExpression operand : operands) {
				coefficient.addOperand(operand.getCoefficientIn(sv));
			}
			if (coefficient.noOfOperands() == 0) {
				return null;
			}
			if (coefficient.noOfOperands() == 1) {
				return coefficient.getOperand(0);
			}
			return coefficient;
		} else if (isOperation(Operation.MINUS)) {
			StepExpression coefficient = getOperand(0).getCoefficientIn(sv);
			if (coefficient == null) {
				return StepConstant.create(-1);
			}
			StepOperation result = new StepOperation(Operation.MINUS);
			result.addOperand(coefficient);
			return result;
		}

		return null;
	}

	@Override
	public StepExpression getVariableIn(StepVariable sv) {
		if (isConstantIn(sv)) {
			return null;
		}

		if (isOperation(Operation.MULTIPLY)) {
			StepOperation variable = new StepOperation(Operation.MULTIPLY);
			for (StepExpression operand : operands) {
				variable.addOperand(operand.getVariableIn(sv));
			}
			if (variable.noOfOperands() == 0) {
				return null;
			}
			if (variable.noOfOperands() == 1) {
				return variable.getOperand(0);
			}
			return variable;
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
			StepOperation result = new StepOperation(Operation.MINUS);
			result.addOperand(sm);
			return result;
		case MULTIPLY:
			StepOperation coefficient = new StepOperation(Operation.MULTIPLY);
			for (StepExpression operand : operands) {
				coefficient.addOperand(operand.getIntegerCoefficient());
			}
			if (coefficient.noOfOperands() == 0) {
				return null;
			}
			if (coefficient.noOfOperands() == 1) {
				return coefficient.getOperand(0);
			}
			return coefficient;
		case DIVIDE:
			return divide(getOperand(0).getIntegerCoefficient(), getOperand(1).getIntegerCoefficient());
		}
		return null;
	}

	@Override
	public StepExpression getNonInteger() {
		switch (operation) {
		case MINUS:
			return getOperand(0).getNonInteger();
		case MULTIPLY:
			StepOperation variable = new StepOperation(Operation.MULTIPLY);
			for (StepExpression operand : operands) {
				variable.addOperand(operand.getNonInteger());
			}
			if (variable.noOfOperands() == 0) {
				return null;
			}
			if (variable.noOfOperands() == 1) {
				return variable.getOperand(0);
			}
			return variable;
		case DIVIDE:
			return divide(getOperand(0).getNonInteger(), getOperand(1).getNonInteger());
		}
		return this;
	}

}
