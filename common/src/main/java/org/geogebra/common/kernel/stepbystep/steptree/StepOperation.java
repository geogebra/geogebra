package org.geogebra.common.kernel.stepbystep.steptree;

import java.util.ArrayList;
import java.util.List;

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
		if (sn.isOperation(operation) && ((StepOperation) sn).noOfOperands() == noOfOperands()) {
			for (int i = 0; i < noOfOperands(); i++) {
				if(!((StepOperation) sn).getSubTree(i).equals(getSubTree(i))) {
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
			return Math.pow(subtrees.get(0).getValue(), 1 / subtrees.get(1).getValue());
		case ABS:
			return Math.abs(subtrees.get(0).getValue());
		}
		return Double.NaN;
	}

	@Override
	public double getValueAt(StepVariable variable, double replaceWith) {
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
			double p = 0;
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
		}
		return Double.NaN;
	}

	@Override
	public StepNode getCoefficient() {
		if(isConstant()) {
			return this;
		}
		
		if (isOperation(Operation.MULTIPLY)) {
			StepNode constant = new StepConstant(1);
			for (int i = 0; i < noOfOperands(); i++) {
				constant = StepNode.multiply(constant, getSubTree(i).getCoefficient()).regroup();
			}
			return constant.regroup();
		} else if (isOperation(Operation.MINUS)) {
			return minus(getSubTree(0).getCoefficient()).regroup();
		}

		return new StepConstant(1);
	}

	@Override
	public StepNode getVariable() {
		if (isConstant()) {
			return new StepConstant(1);
		}

		if (isOperation(Operation.MULTIPLY)) {
			StepNode variable = new StepConstant(1);
			for (int i = 0; i < noOfOperands(); i++) {
				variable = StepNode.multiply(variable, getSubTree(i).getVariable()).regroup();
			}
			return variable;
		} else if (isOperation(Operation.MINUS)) {
			return getSubTree(0).getVariable();
		}

		return this;
	}

	@Override
	public String toString() {
		switch (operation) {
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
			ss.append(")");
			return ss.toString();
		case MINUS:
			if (subtrees.get(0).getPriority() == 1) {
				return "-(" + subtrees.get(0).toString() + ")";
			}
			return "-" + subtrees.get(0).toString();
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
		}
		return "";
	}

	@Override
	public String toLaTeXString() {
		switch (operation) {
		case PLUS:
			StringBuilder ss = new StringBuilder();
			for (int i = 0; i < subtrees.size(); i++) {
				String temp = subtrees.get(i).toLaTeXString();
				if (i != 0 && temp.charAt(0) != '-') {
					ss.append(" + ");
				}
				ss.append(temp);
			}
			return ss.toString();
		case MINUS:
			if (subtrees.get(0).getPriority() == 1) {
				return "-\\left(" + subtrees.get(0).toLaTeXString() + "\\right)";
			}
			return "-" + subtrees.get(0).toLaTeXString();
		case MULTIPLY:
			StringBuilder sp = new StringBuilder();
			for (int i = 0; i < subtrees.size(); i++) {
				if (i != 0) {
					sp.append(" \\cdot ");
				}
				if (subtrees.get(i).getPriority() < getPriority()) {
					sp.append("\\left(");
				}
				sp.append(subtrees.get(i).toLaTeXString());
				if (subtrees.get(i).getPriority() < getPriority()) {
					sp.append("\\right)");
				}
			}
			return sp.toString();
		case DIVIDE:
			return "\\frac{" + subtrees.get(0).toLaTeXString() + "}{" + subtrees.get(1).toLaTeXString() + "}";
		case POWER:
			if (subtrees.get(0).getPriority() <= 3) {
				return "\\left(" + subtrees.get(0).toLaTeXString() + "\\right)^{" + subtrees.get(1).toLaTeXString() + "}";
			}
			return subtrees.get(0).toLaTeXString() + "^{" + subtrees.get(1).toLaTeXString() + "}";
		case NROOT:
			return "\\sqrt[" + subtrees.get(1).toLaTeXString() + "]{" + subtrees.get(0).toLaTeXString() + "}";
		case ABS:
			return "\\left|" + subtrees.get(0).toLaTeXString() + "\\right|";
		}
		return "";
	}

	@Override
	public StepNode deepCopy() {
		StepOperation so = new StepOperation(operation);
		for (int i = 0; i < noOfOperands(); i++) {
			so.addSubTree(getSubTree(i).deepCopy());
		}
		return so;
	}

	@Override
	public StepNode regroup() {
		if (isOperation(Operation.MINUS)) {
			if (getSubTree(0).isOperation(Operation.PLUS)) {
				StepOperation sn = (StepOperation) getSubTree(0);
				for (int i = 0; i < sn.noOfOperands(); i++) {
					sn.subtrees.set(i, minus(sn.getSubTree(i)));
				}
				return sn.regroup();
			} else if (getSubTree(0).isOperation(Operation.MULTIPLY)) {
				StepOperation sn = (StepOperation) getSubTree(0);
				sn.addSubTree(new StepConstant(-1));
				return sn.regroup();
			}
		}
		
		if(isOperation(Operation.NROOT)) {
			if(getSubTree(0).isOperation(Operation.POWER)) {
				StepOperation so = (StepOperation) getSubTree(0);
				if(getSubTree(1).equals(so.getSubTree(1))) {
					return so.getSubTree(0).regroup();
				}
 			}
		}

		if (isOperation(Operation.PLUS)) {
			StepOperation sn = new StepOperation(Operation.PLUS);
			while (noOfOperands() > 0) {
				sn.addSubTree(getSubTree(0).regroup());
				subtrees.remove(0);
			}
			addSubTree(sn);
		} else {
			for (int i = 0; i < noOfOperands(); i++) {
				subtrees.set(i, subtrees.get(i).regroup());
			}
		}

		if (isOperation(Operation.PLUS)) {
			double constants = 0;

			for (int i = 0; i < noOfOperands(); i++) {
				if (subtrees.get(i) instanceof StepConstant) {
					constants += subtrees.get(i).getValue();
					subtrees.remove(i);
					i--;
				}
			}

			StepNode[] coefficients = new StepNode[noOfOperands()];
			StepNode[] variables = new StepNode[noOfOperands()];
			for (int i = 0; i < noOfOperands(); i++) {
				coefficients[i] = getSubTree(i).getCoefficient();
				variables[i] = getSubTree(i).getVariable();
			}

			for (int i = 0; i < noOfOperands(); i++) {
				if (coefficients[i].getValue() != 0) {
					for (int j = i + 1; j < noOfOperands(); j++) {
						if (variables[i].equals(variables[j])) {
							coefficients[i] = StepNode.add(coefficients[i], coefficients[j]).regroup();
							coefficients[j] = new StepConstant(0);
						}
					}
				}
			}

			StepOperation so = new StepOperation(Operation.PLUS);
			for (int i = 0; i < noOfOperands(); i++) {
				if (coefficients[i].getValue() != 0) {
					if (coefficients[i].getValue() == 1) {
						so.addSubTree(variables[i]);
					} else if (coefficients[i].getValue() == -1) {
						so.addSubTree(StepNode.minus(variables[i]));
					} else {
						so.addSubTree(StepNode.multiply(coefficients[i], variables[i]));
					}
				}
			}

			if (constants != 0) {
				so.addSubTree(new StepConstant(constants));
			}

			if (so.noOfOperands() == 1) {
				return so.getSubTree(0);
			}
			return so;
		} else if (isOperation(Operation.MULTIPLY)) {
			double constants = 1;

			for (int i = 0; i < noOfOperands(); i++) {
				if (getSubTree(i) instanceof StepConstant) {
					constants *= getSubTree(i).getValue();
					subtrees.remove(i);
					i--;
				}
			}

			if (constants != 1 || noOfOperands() == 0) {
				addSubTree(new StepConstant(constants));
			}

			if (noOfOperands() == 1) {
				return getSubTree(0);
			}
		} else if (isOperation(Operation.MINUS)) {
			if (getSubTree(0).isOperation(Operation.MINUS)) {
				return ((StepOperation) getSubTree(0)).getSubTree(0);
			} else if (getSubTree(0).isOperation(Operation.MULTIPLY)) {
				StepNode coefficient = StepNode.minus(getSubTree(0).getCoefficient().regroup());
				StepNode variable = getSubTree(0).getVariable();
				return StepNode.multiply(coefficient, variable);
			}
		}

		return this;
	}

	@Override
	public StepNode expand() {
		for (int i = 0; i < noOfOperands(); i++) {
			subtrees.set(i, getSubTree(i).regroup());
		}

		if (isOperation(Operation.MULTIPLY)) {
			StepOperation so = null;

			for (int i = 0; i < noOfOperands(); i++) {
				if (isOperation(Operation.PLUS)) {
					so = (StepOperation) subtrees.remove(i);
					break;
				}
			}

			if (so == null) {
				return this;
			}

			for (int i = 0; i < noOfOperands(); i++) {
				if (getSubTree(i).isOperation(Operation.PLUS)) {
					StepOperation so2 = (StepOperation) getSubTree(i);
					StepOperation newSo = new StepOperation(Operation.PLUS);
					for (int j = 0; j < so.noOfOperands(); j++) {
						for (int k = 0; k < so2.noOfOperands(); k++) {
							newSo.addSubTree(StepNode.multiply(so.getSubTree(j), so2.getSubTree(k)));
						}
					}
					so = newSo;
				} else if (getSubTree(i).isOperation(Operation.MINUS)) {
					StepOperation so2 = (StepOperation) getSubTree(i);
					for (int j = 0; j < so.noOfOperands(); j++) {
						if (so.getSubTree(j).isOperation(Operation.MINUS)) {
							so.subtrees.set(j, StepNode.multiply(((StepOperation) so.getSubTree(j)).getSubTree(0), so2.getSubTree(0)));
						} else {
							so.subtrees.set(j, StepNode.minus(StepNode.multiply(so.getSubTree(j), so2.getSubTree(0))));
						}
					}
				} else {
					for(int j = 0; j < so.noOfOperands(); j++) {
						so.subtrees.set(j, StepNode.multiply(so.getSubTree(j), getSubTree(i)));
					}
				}
			}

			return so;
		}
		return this;
	}

	@Override
	public StepNode simplify() {
		return regroup().expand().regroup();
	}

	public void addSubTree(StepNode sn) {
		if (sn != null) {
			if (isOperation(Operation.PLUS) && sn.isOperation(Operation.PLUS)) {
				for (int i = 0; i < ((StepOperation) sn).noOfOperands(); i++) {
					addSubTree(((StepOperation) sn).getSubTree(i).deepCopy());
				}
			} else if (isOperation(Operation.MULTIPLY) && sn.isOperation(Operation.MULTIPLY)) {
				for (int i = 0; i < ((StepOperation) sn).noOfOperands(); i++) {
					addSubTree(((StepOperation) sn).getSubTree(i).deepCopy());
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
