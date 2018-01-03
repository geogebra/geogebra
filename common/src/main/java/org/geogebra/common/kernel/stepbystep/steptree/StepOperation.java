package org.geogebra.common.kernel.stepbystep.steptree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Operation;

public class StepOperation extends StepExpression implements Iterable<StepExpression> {
	private List<StepExpression> subtrees;
	private Operation operation;

	public StepOperation(Operation op) {
		operation = op;
		subtrees = new ArrayList<>();
	}

	public int noOfOperands() {
		return subtrees.size();
	}

	public StepExpression getSubTree(int index) {
		return subtrees.get(index);
	}

	public Operation getOperation() {
		return operation;
	}

	public void addSubTree(StepExpression sn) {
		if (sn != null) {
			if (isOperation(Operation.PLUS) && sn.isOperation(Operation.PLUS)) {
				for (int i = 0; i < ((StepOperation) sn).noOfOperands(); i++) {
					addSubTree(((StepOperation) sn).getSubTree(i));
				}
			} else if (isOperation(Operation.MULTIPLY) && sn.isOperation(Operation.MULTIPLY)) {
				for (int i = 0; i < ((StepOperation) sn).noOfOperands(); i++) {
					addSubTree(((StepOperation) sn).getSubTree(i));
				}
			} else {
				subtrees.add(sn);
			}
		}
	}

	@Override
	public Iterator<StepExpression> iterator() {
		return subtrees.iterator();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((operation == null) ? 0 : operation.hashCode());
		result = prime * result + ((subtrees == null) ? 0 : subtrees.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StepOperation) {
			StepOperation so = (StepOperation) obj;

			if (so.operation != operation) {
				return false;
			}

			StepOperation copyOfThis = this.deepCopy();
			copyOfThis.sort();
			StepOperation copyOfThat = so.deepCopy();
			copyOfThat.sort();

			return copyOfThis.exactEquals(copyOfThat);
		}

		return false;
	}

	private void sort() {
		for (int i = 0; i < noOfOperands(); i++) {
			if (getSubTree(i) instanceof StepOperation) {
				((StepOperation) getSubTree(i)).sort();
			}
		}

		if (isOperation(Operation.PLUS) || isOperation(Operation.MULTIPLY)) {
			subtrees.sort(new Comparator<StepExpression>() {
				@Override
				public int compare(StepExpression arg0, StepExpression arg1) {
					return arg0.compareTo(arg1);
				}
			});
		}
	}

	private boolean exactEquals(StepOperation so) {
		if (so.noOfOperands() == noOfOperands()) {
			for (int i = 0; i < noOfOperands(); i++) {
				if (!so.getSubTree(i).equals(getSubTree(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isConstantIn(StepVariable sv) {
		for (StepExpression subtree : subtrees) {
			if (!subtree.isConstantIn(sv)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean canBeEvaluated() {
		for (StepExpression subtree : subtrees) {
			if (!subtree.canBeEvaluated()) {
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
			for (StepExpression subtree : subtrees) {
				s += subtree.getValue();
			}
			return s;
		case MINUS:
			return -subtrees.get(0).getValue();
		case MULTIPLY:
			double p = 1;
			for (StepExpression subtree : subtrees) {
				p *= subtree.getValue();
			}
			return p;
		case DIVIDE:
			return subtrees.get(0).getValue() / subtrees.get(1).getValue();
		case POWER:
			return Math.pow(subtrees.get(0).getValue(), subtrees.get(1).getValue());
		case NROOT:
			double base = subtrees.get(0).getValue();
			double exponent = subtrees.get(1).getValue();

			if (base < 0) {
				if (closeToAnInteger(exponent) && Math.round(exponent) % 2 == 1) {
					return -Math.pow(-base, 1 / exponent);
				}
			}

			return Math.pow(base, 1 / exponent);
		case ABS:
			return Math.abs(subtrees.get(0).getValue());
		case SIN:
			return Math.sin(subtrees.get(0).getValue());
		case COS:
			return Math.cos(subtrees.get(0).getValue());
		case TAN:
			return Math.tan(subtrees.get(0).getValue());
		case ARCSIN:
			return Math.asin(subtrees.get(0).getValue());
		case ARCCOS:
			return Math.acos(subtrees.get(0).getValue());
		case ARCTAN:
			return Math.atan(subtrees.get(0).getValue());
		}
		return Double.NaN;
	}

	@Override
	public double getValueAt(StepVariable variable, double replaceWith) {
		switch (operation) {
		case PLUS:
			double s = 0;
			for (StepExpression subtree : subtrees) {
				s += subtree.getValueAt(variable, replaceWith);
			}
			return s;
		case MINUS:
			return -subtrees.get(0).getValueAt(variable, replaceWith);
		case MULTIPLY:
			double p = 1;
			for (StepExpression subtree : subtrees) {
				p *= subtree.getValueAt(variable, replaceWith);
			}
			return p;
		case DIVIDE:
			return subtrees.get(0).getValueAt(variable, replaceWith)
					/ subtrees.get(1).getValueAt(variable, replaceWith);
		case POWER:
			return Math.pow(subtrees.get(0).getValueAt(variable, replaceWith),
					subtrees.get(1).getValueAt(variable, replaceWith));
		case NROOT:
			return Math.pow(subtrees.get(0).getValueAt(variable, replaceWith),
					1 / subtrees.get(1).getValueAt(variable, replaceWith));
		case ABS:
			return Math.abs(subtrees.get(0).getValueAt(variable, replaceWith));
		case SIN:
			return Math.sin(subtrees.get(0).getValueAt(variable, replaceWith));
		case COS:
			return Math.cos(subtrees.get(0).getValueAt(variable, replaceWith));
		case TAN:
			return Math.tan(subtrees.get(0).getValueAt(variable, replaceWith));
		case ARCSIN:
			return Math.asin(subtrees.get(0).getValueAt(variable, replaceWith));
		case ARCCOS:
			return Math.acos(subtrees.get(0).getValueAt(variable, replaceWith));
		case ARCTAN:
			return Math.atan(subtrees.get(0).getValueAt(variable, replaceWith));
		}
		return Double.NaN;
	}

	@Override
	public String toString() {
		switch (operation) {
		case IS_ELEMENT_OF:
			return subtrees.get(0) + " in " + subtrees.get(1);
		case PLUS:
			StringBuilder ss = new StringBuilder();
			ss.append("(");
			for (int i = 0; i < subtrees.size(); i++) {
				String temp = subtrees.get(i).toString();
				if (i != 0 && temp.charAt(0) != '-') {
					ss.append(" + ");
				}
				ss.append(temp);
			}
			if (subtrees.size() == 0) {
				ss.append("0");
			}
			ss.append(")");
			return ss.toString();
		case MINUS:
			if (subtrees.get(0).isOperation(Operation.PLUS) || subtrees.get(0).isOperation(Operation.MINUS)) {
				return "-(" + subtrees.get(0).toString() + ")";
			}
			return "-" + subtrees.get(0).toString();
		case PLUSMINUS:
			return "pm(" + subtrees.get(0).toString() + ")";
		case MULTIPLY:
			StringBuilder sp = new StringBuilder();
			for (StepExpression subtree : subtrees) {
				sp.append("(");
				sp.append(subtree.toString());
				sp.append(")");
			}
			return sp.toString();
		case DIVIDE:
			return "(" + subtrees.get(0).toString() + ")/(" + subtrees.get(1).toString() + ")";
		case POWER:
			return "(" + subtrees.get(0).toString() + ")^(" + subtrees.get(1).toString() + ")";
		case NROOT:
			return "nroot(" + subtrees.get(0).toString() + ", " + subtrees.get(1).toString() + ")";
		case ABS:
			return "|" + subtrees.get(0).toString() + "|";
		case SIN:
		case COS:
		case TAN:
		case CSC:
		case SEC:
		case COT:
		case ARCSIN:
		case ARCCOS:
		case ARCTAN:
			return operation.toString().toLowerCase() + "(" + subtrees.get(0).toString() + ")";
		case DIFF:
			return "d/d" + subtrees.get(1).toString() + "(" + subtrees.get(0).toString() + ")";
		case LOG:
			return "log_(" + subtrees.get(0).toString() + ")(" + subtrees.get(1).toString() + ")";
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
			return subtrees.get(0).toLaTeXString(loc, colored) + " \\in " + subtrees.get(1).toLaTeXString(loc, colored);
		case PLUS:
			StringBuilder ss = new StringBuilder();
			for (int i = 0; i < subtrees.size(); i++) {
				if (i != 0 && requiresPlus(subtrees.get(i))) {
					ss.append(" + ");
				}
				ss.append(subtrees.get(i).toLaTeXString(loc, colored));
			}
			if (subtrees.size() == 0) {
				ss.append("0");
			}
			return ss.toString();
		case MINUS:
			if (subtrees.get(0).isOperation(Operation.PLUS) || subtrees.get(0).isOperation(Operation.MINUS)) {
				return "-\\left(" + subtrees.get(0).toLaTeXString(loc, colored) + "\\right)";
			}
			return "-" + subtrees.get(0).toLaTeXString(loc, colored);
		case PLUSMINUS:
			if (subtrees.get(0).isOperation(Operation.PLUS) || subtrees.get(0).isOperation(Operation.MINUS)) {
				return "\\pm\\left(" + subtrees.get(0).toLaTeXString(loc, colored) + "\\right)";
			}
			return "\\pm " + subtrees.get(0).toLaTeXString(loc, colored);
		case MULTIPLY:
			StringBuilder sp = new StringBuilder();
			for (int i = 0; i < subtrees.size(); i++) {
				if (i != 0 && requiresDot(subtrees.get(i - 1), subtrees.get(i))) {
					sp.append(" \\cdot ");
				} else if (i != 0) {
					sp.append(" ");
				}

				boolean parantheses = subtrees.get(i).isOperation(Operation.PLUS)
						&& !subtrees.get(i).isOperation(Operation.MINUS) || (i != 0 && subtrees.get(i).isNegative());

				if (parantheses) {
					sp.append("\\left(");
				}
				sp.append(subtrees.get(i).toLaTeXString(loc, colored));
				if (parantheses) {
					sp.append("\\right)");
				}
			}
			return sp.toString();
		case DIVIDE:
			return "\\frac{" + subtrees.get(0).toLaTeXString(loc, colored) + "}{"
					+ subtrees.get(1).toLaTeXString(loc, colored) + "}";
		case POWER:
			if (subtrees.get(0).isNegative()
					|| (subtrees.get(0) instanceof StepOperation && !subtrees.get(0).isOperation(Operation.NROOT))) {
				return "\\left(" + subtrees.get(0).toLaTeXString(loc, colored) + "\\right)^{"
						+ subtrees.get(1).toLaTeXString(loc, colored) + "}";
			}
			return subtrees.get(0).toLaTeXString(loc, colored) + "^{" + subtrees.get(1).toLaTeXString(loc, colored)
					+ "}";
		case NROOT:
			if (isSquareRoot()) {
				return "\\sqrt{" + subtrees.get(0).toLaTeXString(loc, colored) + "}";
			}
			return "\\sqrt[" + subtrees.get(1).toLaTeXString(loc, colored) + "]{"
					+ subtrees.get(0).toLaTeXString(loc, colored) + "}";
		case ABS:
			return "\\left|" + subtrees.get(0).toLaTeXString(loc, colored) + "\\right|";
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
					+ subtrees.get(0).toLaTeXString(loc, colored) + "\\right)";
		case LOG:
			if (isNaturalLog()) {
				return "\\ln \\left(" + subtrees.get(1).toLaTeXString(loc, colored) + "\\right)";
			}
			return "\\log_{" + subtrees.get(0).toLaTeXString(loc, colored) + "} \\left("
					+ subtrees.get(1).toLaTeXString(loc, colored) + "\\right)";
		case DIFF:
			StringBuilder sb = new StringBuilder();

			if (loc.primeNotation()) {
				sb.append("\\left(");
				sb.append(subtrees.get(0).toLaTeXString(loc, colored));
				sb.append("\\right)");
				sb.append("'");
				return sb.toString();
			}

			sb.append("\\frac{d}{d");
			sb.append(subtrees.get(1).toLaTeXString(loc, colored));
			sb.append("}");
			if (subtrees.get(0).isOperation(Operation.PLUS)) {
				sb.append("\\left(");
			}
			sb.append(subtrees.get(0).toLaTeXString(loc, colored));
			if (subtrees.get(0).isOperation(Operation.PLUS)) {
				sb.append("\\right)");
			}
			return sb.toString();
			
		}
		return "";
	}

	private static boolean requiresPlus(StepExpression a) {
		return !a.isNegative() && !a.isOperation(Operation.PLUSMINUS)
				&& (!a.isOperation(Operation.MULTIPLY) || requiresPlus(((StepOperation) a).getSubTree(0)));
	}

	private static boolean requiresDot(StepExpression a, StepExpression b) {
		return (a.nonSpecialConstant() && b.nonSpecialConstant())
				|| (a instanceof StepVariable && b.nonSpecialConstant()) || (a instanceof StepVariable && a.equals(b))
				|| (b.isOperation(Operation.POWER) && requiresDot(a, ((StepOperation) b).getSubTree(0)));
	}

	@Override
	public StepOperation deepCopy() {
		StepOperation so = new StepOperation(operation);
		so.color = color;
		for (int i = 0; i < noOfOperands(); i++) {
			so.addSubTree(getSubTree(i).deepCopy());
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
			for (int i = 0; i < noOfOperands(); i++) {
				coefficient.addSubTree(getSubTree(i).getCoefficientIn(sv));
			}
			if (coefficient.noOfOperands() == 0) {
				return null;
			}
			if (coefficient.noOfOperands() == 1) {
				return coefficient.getSubTree(0);
			}
			return coefficient;
		} else if (isOperation(Operation.MINUS)) {
			StepExpression coefficient = getSubTree(0).getCoefficientIn(sv);
			if (coefficient == null) {
				return StepConstant.create(-1);
			}
			StepOperation result = new StepOperation(Operation.MINUS);
			result.addSubTree(coefficient);
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
			for (int i = 0; i < noOfOperands(); i++) {
				variable.addSubTree(getSubTree(i).getVariableIn(sv));
			}
			if (variable.noOfOperands() == 0) {
				return null;
			}
			if (variable.noOfOperands() == 1) {
				return variable.getSubTree(0);
			}
			return variable;
		} else if (isOperation(Operation.MINUS)) {
			return getSubTree(0).getVariableIn(sv);
		}

		return this;
	}

	@Override
	public StepExpression getIntegerCoefficient() {
		switch (operation) {
		case PLUSMINUS:
			return getSubTree(0).getIntegerCoefficient();
		case MINUS:
			StepExpression sm = getSubTree(0).getIntegerCoefficient();
			if (sm == null) {
				return StepConstant.create(-1);
			}
			StepOperation result = new StepOperation(Operation.MINUS);
			result.addSubTree(sm);
			return result;
		case MULTIPLY:
			StepOperation coefficient = new StepOperation(Operation.MULTIPLY);
			for (int i = 0; i < noOfOperands(); i++) {
				coefficient.addSubTree(getSubTree(i).getIntegerCoefficient());
			}
			if (coefficient.noOfOperands() == 0) {
				return null;
			}
			if (coefficient.noOfOperands() == 1) {
				return coefficient.getSubTree(0);
			}
			return coefficient;
		case DIVIDE:
			return divide(getSubTree(0).getIntegerCoefficient(), getSubTree(1).getIntegerCoefficient());
		}
		return null;
	}

	@Override
	public StepExpression getNonInteger() {
		switch (operation) {
		case PLUSMINUS:
			return apply(getSubTree(0).getNonInteger(), Operation.PLUSMINUS);
		case MINUS:
			return getSubTree(0).getNonInteger();
		case MULTIPLY:
			StepOperation variable = new StepOperation(Operation.MULTIPLY);
			for (int i = 0; i < noOfOperands(); i++) {
				variable.addSubTree(getSubTree(i).getNonInteger());
			}
			if (variable.noOfOperands() == 0) {
				return null;
			}
			if (variable.noOfOperands() == 1) {
				return variable.getSubTree(0);
			}
			return variable;
		case DIVIDE:
			return divide(getSubTree(0).getNonInteger(), getSubTree(1).getNonInteger());
		}
		return this;
	}

}
