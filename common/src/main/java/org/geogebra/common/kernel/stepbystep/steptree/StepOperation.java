package org.geogebra.common.kernel.stepbystep.steptree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Operation;

public class StepOperation extends StepNode {
	private List<StepNode> subtrees;
	private Operation operation;

	public StepOperation(Operation op) {
		operation = op;
		subtrees = new ArrayList<StepNode>();
	}

	@Override
	public boolean equals(StepNode sn) {
		if (sn != null && sn.isOperation()) {
			StepOperation copyOfThis = (StepOperation) this.deepCopy();
			copyOfThis.sort();
			StepOperation copyOfThat = (StepOperation) sn.deepCopy();
			copyOfThat.sort();

			return copyOfThis.exactEquals(copyOfThat);
		}

		return exactEquals(sn);
	}

	private void sort() {
		for (int i = 0; i < noOfOperands(); i++) {
			if (getSubTree(i).isOperation()) {
				((StepOperation) getSubTree(i)).sort();
			}
		}

		if (isOperation(Operation.PLUS) || isOperation(Operation.MULTIPLY)) {
			subtrees.sort(new Comparator<StepNode>() {
				public int compare(StepNode arg0, StepNode arg1) {
					return arg0.compareTo(arg1);
				}
			});
		}
	}

	private boolean exactEquals(StepNode sn) {
		if (sn != null && sn.isOperation(operation) && ((StepOperation) sn).noOfOperands() == noOfOperands()) {
			for (int i = 0; i < noOfOperands(); i++) {
				if (!((StepOperation) sn).getSubTree(i).equals(getSubTree(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isOperation() {
		return true;
	}

	@Override
	public boolean isOperation(Operation op) {
		return operation == op;
	}

	@Override
	public boolean isConstant() {
		for (int i = 0; i < subtrees.size(); i++) {
			if (!subtrees.get(i).isConstant()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean canBeEvaluated() {
		for (int i = 0; i < subtrees.size(); i++) {
			if (!subtrees.get(i).canBeEvaluated()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int getPriority() {
		switch (operation) {
		case PLUS:
		case MINUS:
			return 1;
		case MULTIPLY:
		case DIVIDE:
			return 2;
		case POWER:
		case NROOT:
		case ABS:
			return 3;
		case SIN:
		case COS:
		case TAN:
		case CSC:
		case SEC:
		case COT:
		case ARCSIN:
		case ARCCOS:
		case ARCTAN:
			return 4;
		}
		return 0;
	}

	@Override
	public double getValue() {
		switch (operation) {
		case PLUS:
			double s = 0;
			for (int i = 0; i < subtrees.size(); i++) {
				s += subtrees.get(i).getValue();
			}
			return s;
		case MINUS:
			return -subtrees.get(0).getValue();
		case MULTIPLY:
			double p = 1;
			for (int i = 0; i < subtrees.size(); i++) {
				p *= subtrees.get(i).getValue();
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
	public double getValueAt(StepNode variable, double replaceWith) {
		switch (operation) {
		case PLUS:
			double s = 0;
			for (int i = 0; i < subtrees.size(); i++) {
				s += subtrees.get(i).getValueAt(variable, replaceWith);
			}
			return s;
		case MINUS:
			return -subtrees.get(0).getValueAt(variable, replaceWith);
		case MULTIPLY:
			double p = 1;
			for (int i = 0; i < subtrees.size(); i++) {
				p *= subtrees.get(i).getValueAt(variable, replaceWith);
			}
			return p;
		case DIVIDE:
			return subtrees.get(0).getValueAt(variable, replaceWith) / subtrees.get(1).getValueAt(variable, replaceWith);
		case POWER:
			return Math.pow(subtrees.get(0).getValueAt(variable, replaceWith), subtrees.get(1).getValueAt(variable, replaceWith));
		case NROOT:
			return Math.pow(subtrees.get(0).getValueAt(variable, replaceWith), 1 / subtrees.get(1).getValueAt(variable, replaceWith));
		case ABS:
			return Math.abs(subtrees.get(0).getValueAt(variable, replaceWith));
		case SIN:
			return Math.sin(subtrees.get(0).getValueAt(variable, replaceWith));
		case COS:
			return Math.cos(subtrees.get(0).getValueAt(variable, replaceWith));
		case TAN:
			return Math.tan(subtrees.get(0).getValueAt(variable, replaceWith));
		}
		return Double.NaN;
	}

	@Override
	public String toString() {
		switch (operation) {
		case EQUAL_BOOLEAN:
			return subtrees.get(0).toString() + " = " + subtrees.get(1).toString();
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
			if (subtrees.get(0).getPriority() == 1) {
				return "-(" + subtrees.get(0).toString() + ")";
			}
			return "-" + subtrees.get(0).toString();
		case PLUSMINUS:
			return "pm(" + subtrees.get(0).toString() + ")";
		case MULTIPLY:
			StringBuilder sp = new StringBuilder();
			for (int i = 0; i < subtrees.size(); i++) {
				sp.append("(");
				sp.append(subtrees.get(i).toString());
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
		case EQUAL_BOOLEAN:
			return subtrees.get(0).toLaTeXString(loc, colored) + " = " + subtrees.get(1).toLaTeXString(loc, colored);
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
			if (subtrees.get(0).getPriority() == 1) {
				return "-\\left(" + subtrees.get(0).toLaTeXString(loc, colored) + "\\right)";
			}
			return "-" + subtrees.get(0).toLaTeXString(loc, colored);
		case PLUSMINUS:
			if (subtrees.get(0).getPriority() == 1) {
				return "\\pm\\left(" + subtrees.get(0).toLaTeXString(loc, colored) + "\\right)";
			}
			return "\\pm" + subtrees.get(0).toLaTeXString(loc, colored);
		case MULTIPLY:
			StringBuilder sp = new StringBuilder();
			for (int i = 0; i < subtrees.size(); i++) {
				if (i != 0 && requiresDot(subtrees.get(i - 1), subtrees.get(i))) {
					sp.append(" \\cdot ");
				} else if (i != 0) {
					sp.append(" ");
				}

				boolean parantheses = subtrees.get(i).getPriority() < getPriority() && !subtrees.get(i).isOperation(Operation.MINUS)
						|| (i != 0 && isNegative(subtrees.get(i)));

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
			return "\\frac{" + subtrees.get(0).toLaTeXString(loc, colored) + "}{" + subtrees.get(1).toLaTeXString(loc, colored) + "}";
		case POWER:
			if (subtrees.get(0).getPriority() <= 3) {
				return "\\left(" + subtrees.get(0).toLaTeXString(loc, colored) + "\\right)^{" + subtrees.get(1).toLaTeXString(loc, colored)
						+ "}";
			}
			return subtrees.get(0).toLaTeXString(loc, colored) + "^{" + subtrees.get(1).toLaTeXString(loc, colored) + "}";
		case NROOT:
			if (isSquareRoot()) {
				return "\\sqrt{" + subtrees.get(0).toLaTeXString(loc, colored) + "}";
			}
			return "\\sqrt[" + subtrees.get(1).toLaTeXString(loc, colored) + "]{" + subtrees.get(0).toLaTeXString(loc, colored) + "}";
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
			return "\\" + loc.getFunction(operation.toString().toLowerCase()) + "\\left(" + subtrees.get(0).toLaTeXString(loc, colored)
					+ "\\right)";
		}
		return "";
	}

	private static boolean requiresPlus(StepNode a) {
		return !(a instanceof StepConstant && a.getValue() < 0) 
				&& !a.isOperation(Operation.MINUS) 
				&& !a.isOperation(Operation.PLUSMINUS)
				&& (!a.isOperation(Operation.MULTIPLY) || requiresPlus(((StepOperation) a).getSubTree(0)));
	}

	private static boolean requiresDot(StepNode a, StepNode b) {
		return (a.nonSpecialConstant() && b.nonSpecialConstant()) 
				|| (a instanceof StepVariable && b.nonSpecialConstant())
				|| (a instanceof StepVariable && a.equals(b))
				|| (b.isOperation(Operation.POWER) && requiresDot(a, ((StepOperation) b).getSubTree(0)));
	}

	@Override
	public StepNode deepCopy() {
		StepOperation so = new StepOperation(operation);
		so.color = color;
		for (int i = 0; i < noOfOperands(); i++) {
			so.addSubTree(getSubTree(i).deepCopy());
		}
		return so;
	}

	@Override
	public StepNode regroup() {
		return regroup(null);
	}

	@Override
	public StepNode regroup(SolutionBuilder sb) {
		cleanColors();
		return SimplificationSteps.DEFAULT_REGROUP.apply(this, sb, new int[] { 1 });
	}

	@Override
	public StepNode expand(SolutionBuilder sb) {
		cleanColors();
		return SimplificationSteps.DEFAULT_EXPAND.apply(this, sb, new int[] { 1 });
	}

	@Override
	public StepNode getCoefficient() {
		if (isConstant()) {
			return this;
		}

		if (isOperation(Operation.MULTIPLY)) {
			StepOperation coefficient = new StepOperation(Operation.MULTIPLY);
			for (int i = 0; i < noOfOperands(); i++) {
				coefficient.addSubTree(getSubTree(i).getCoefficient());
			}
			if (coefficient.noOfOperands() == 0) {
				return null;
			}
			if (coefficient.noOfOperands() == 1) {
				return coefficient.getSubTree(0);
			}
			return coefficient;
		} else if (isOperation(Operation.MINUS)) {
			StepNode coefficient = getSubTree(0).getCoefficient();
			if (coefficient == null) {
				return new StepConstant(-1);
			}
			StepOperation result = new StepOperation(Operation.MINUS);
			result.addSubTree(coefficient);
			return result;
		}

		return null;
	}

	@Override
	public StepNode getVariable() {
		if (isConstant()) {
			return null;
		}

		if (isOperation(Operation.MULTIPLY)) {
			StepOperation variable = new StepOperation(Operation.MULTIPLY);
			for (int i = 0; i < noOfOperands(); i++) {
				variable.addSubTree(getSubTree(i).getVariable());
			}
			if (variable.noOfOperands() == 0) {
				return null;
			}
			if (variable.noOfOperands() == 1) {
				return variable.getSubTree(0);
			}
			return variable;
		} else if (isOperation(Operation.MINUS)) {
			return getSubTree(0).getVariable();
		}

		return this;
	}

	@Override
	public StepNode getIntegerCoefficient() {
		switch (operation) {
		case MINUS:
			StepNode sm = getSubTree(0).getIntegerCoefficient();
			if (sm == null) {
				return new StepConstant(-1);
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
	public StepNode getNonInteger() {
		switch (operation) {
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

	public void addSubTree(StepNode sn) {
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

	public int noOfOperands() {
		return subtrees.size();
	}

	public StepNode getSubTree(int index) {
		return subtrees.get(index);
	}

	public Operation getOperation() {
		return operation;
	}
}
