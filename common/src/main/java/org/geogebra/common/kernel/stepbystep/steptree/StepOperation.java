package org.geogebra.common.kernel.stepbystep.steptree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.geogebra.common.kernel.stepbystep.StepHelper;
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
		}
		return Double.NaN;
	}

	@Override
	public StepNode getCoefficient() {
		if (isConstant()) {
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
			if (subtrees.size() == 0) {
				ss.append("0");
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
			if (isEqual(subtrees.get(1).getValue(), 2)) {
				return "\\sqrt{" + subtrees.get(0).toLaTeXString() + "}";
			}
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

	public void sort() {
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

	@Override
	public StepNode regroup() {
		sort();
		if (isOperation(Operation.MINUS)) {
			StepOperation so = new StepOperation(Operation.MINUS);
			so.addSubTree(getSubTree(0).regroup());
			if (so.getSubTree(0).isOperation(Operation.MINUS)) {
				return ((StepOperation) so.getSubTree(0)).getSubTree(0).regroup();
			} else if (so.getSubTree(0).isOperation(Operation.PLUS)) {
				StepOperation sn = (StepOperation) so.getSubTree(0);
				for (int i = 0; i < sn.noOfOperands(); i++) {
					sn.subtrees.set(i, minus(sn.getSubTree(i)));
				}
				return sn.regroup();
			} else if (so.getSubTree(0).isOperation(Operation.MULTIPLY)) {
				StepOperation sn = (StepOperation) so.getSubTree(0);
				sn.addSubTree(new StepConstant(-1));
				return sn.regroup();
			} else if (so.getSubTree(0).isOperation(Operation.DIVIDE)) {
				StepOperation sn = new StepOperation(Operation.DIVIDE);
				sn.addSubTree(StepNode.minus(((StepOperation) so.getSubTree(0)).getSubTree(0)));
				sn.addSubTree(((StepOperation) so.getSubTree(0)).getSubTree(1));
				return sn;
			} else if (so.getSubTree(0) instanceof StepConstant) {
				return new StepConstant(-so.getSubTree(0).getValue());
			}
			return so;
		} else if (isOperation(Operation.PLUS)) {
			StepOperation sn = new StepOperation(Operation.PLUS);
			while (noOfOperands() > 0) {
				sn.addSubTree(getSubTree(0).regroup());
				subtrees.remove(0);
			}
			addSubTree(sn);

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
							coefficients[i] = StepNode.add(coefficients[i], coefficients[j]);
							coefficients[j] = new StepConstant(0);
						}
					}
				}
			}

			StepOperation so = new StepOperation(Operation.PLUS);
			for (int i = 0; i < noOfOperands(); i++) {
				coefficients[i] = coefficients[i].constantRegroup();
				if (coefficients[i].getValue() != 0) {
					if (coefficients[i].getValue() == 1) {
						so.addSubTree(variables[i]);
					} else if (variables[i].getValue() == 1) {
						so.addSubTree(coefficients[i]);
					} else if (coefficients[i].getValue() == -1) {
						so.addSubTree(StepNode.minus(variables[i]));
					} else {
						so.addSubTree(StepNode.multiply(coefficients[i], variables[i]));
					}
				}
			}

			if (so.noOfOperands() == 0) {
				return new StepConstant(constants);
			}

			if (constants != 0) {
				so.addSubTree(new StepConstant(constants));
			}
			if (so.noOfOperands() == 1) {
				return so.getSubTree(0);
			}
			return so;
		} else if (isOperation(Operation.MULTIPLY)) {
			StepOperation sn = new StepOperation(Operation.MULTIPLY);
			while (noOfOperands() > 0) {
				sn.addSubTree(getSubTree(0).regroup());
				subtrees.remove(0);
			}
			addSubTree(sn);

			double nominator = 1;
			double denominator = 1;

			List<StepNode> bases = new ArrayList<StepNode>();
			List<StepNode> exponents = new ArrayList<StepNode>();
			for (int i = 0; i < noOfOperands(); i++) {
				if (getSubTree(i).isOperation(Operation.MINUS)) {
					nominator *= -1;
					subtrees.set(i, ((StepOperation) getSubTree(i)).getSubTree(0));
					i--;
				} else if (getSubTree(i).isOperation(Operation.DIVIDE)) {
					bases.add(((StepOperation) getSubTree(i)).getSubTree(0));
					exponents.add(new StepConstant(1));
					bases.add(((StepOperation) getSubTree(i)).getSubTree(1));
					exponents.add(new StepConstant(-1));
				} else if (getSubTree(i).isOperation(Operation.POWER)) {
					bases.add(((StepOperation) getSubTree(i)).getSubTree(0));
					exponents.add(((StepOperation) getSubTree(i)).getSubTree(1));
				} else if (getSubTree(i).isOperation(Operation.NROOT)) {
					bases.add(((StepOperation) getSubTree(i)).getSubTree(0));
					exponents.add(StepNode.divide(new StepConstant(1), ((StepOperation) getSubTree(i)).getSubTree(1)));
				} else {
					bases.add(getSubTree(i));
					exponents.add(new StepConstant(1));
				}
			}
			for (int i = 0; i < bases.size(); i++) {
				if (bases.get(i) instanceof StepConstant && exponents.get(i).getValue() == 1) {
					nominator *= bases.get(i).getValue();
					exponents.set(i, new StepConstant(0));
				} else if (bases.get(i) instanceof StepConstant && exponents.get(i).getValue() == -1) {
					denominator *= bases.get(i).getValue();
					exponents.set(i, new StepConstant(0));
				} else if (exponents.get(i).getValue() != 0) {
					for (int j = i + 1; j < bases.size(); j++) {
						if (bases.get(i).equals(bases.get(j))) {
							exponents.set(i, StepNode.add(exponents.get(i), exponents.get(j)));
							exponents.set(j, new StepConstant(0));
						}
					}
				}
			}

			for (int i = 0; i < bases.size(); i++) {
				if (exponents.get(i).getValue() != 0) {
					for (int j = i + 1; j < bases.size(); j++) {
						if (exponents.get(i).equals(exponents.get(j))) {
							bases.set(i, StepNode.multiply(bases.get(i), bases.get(j)));
							exponents.set(j, new StepConstant(0));
						}
					}
				}
			}

			StepOperation so = new StepOperation(Operation.MULTIPLY);
			StepOperation soDenominator = new StepOperation(Operation.MULTIPLY);
			for (int i = 0; i < bases.size(); i++) {
				exponents.set(i, exponents.get(i).constantRegroup());
				if (exponents.get(i).getValue() != 0 && bases.get(i).getValue() != 1) {
					if (exponents.get(i).getValue() == 1) {
						so.addSubTree(bases.get(i));
					} else if (exponents.get(i).getValue() == -1) {
						soDenominator.addSubTree(bases.get(i));
					} else if (exponents.get(i).getValue() == 0.5) {
						so.addSubTree(StepNode.root(bases.get(i), 2));
					} else {
						so.addSubTree(StepNode.power(bases.get(i), exponents.get(i)));
					}
				}
			}


			if (soDenominator.noOfOperands() > 0) {
				StepNode divided = StepNode.polynomialDivision(so.deepCopy(), soDenominator.deepCopy(), new StepVariable("x"));
				
				if(divided != null) {
					while (so.noOfOperands() > 0) {
						so.subtrees.remove(0);
					}
					so.addSubTree(divided);

				} else {
					so.addSubTree(StepNode.divide(new StepConstant(1), soDenominator));
				}
			}

			if (nominator == 0) {
				return new StepConstant(0);
			} else if (denominator != 1 && closeToAnInteger(nominator) && closeToAnInteger(denominator)) {
				long nom = Math.round(nominator);
				long denom = Math.round(denominator);
				long gcd = StepNode.gcd(nom, denom);
				nom /= gcd;
				denom /= gcd;

				if (nom != 1 || denom != 1 || so.noOfOperands() == 0) {
					if (denom == 1) {
						so.addSubTree(new StepConstant(nom));
					} else {
						so.addSubTree(StepNode.divide(new StepConstant(nom), denom));
					}
				}
			} else {
				double constants = nominator / denominator;

				if (constants < 0) {
					constants = -constants;

					if (constants != 1 || so.noOfOperands() == 0) {
						so.addSubTree(new StepConstant(constants));
					}

					if (so.noOfOperands() == 1) {
						return minus(so.getSubTree(0));
					}

					return StepNode.minus(so);
				}
				if (constants != 1 || so.noOfOperands() == 0) {
					so.addSubTree(new StepConstant(constants));
				}
			}

			if (so.noOfOperands() == 1) {
				return so.getSubTree(0);
			}

			return so;
		}

		StepOperation so = new StepOperation(getOperation());
		so.addSubTree(getSubTree(0).regroup());
		so.addSubTree(getSubTree(1).regroup());

		if (isOperation(Operation.DIVIDE)) {
			if (so.getSubTree(0) instanceof StepConstant && so.getSubTree(1) instanceof StepConstant) {
				if (closeToAnInteger(so.getSubTree(0).getValue()) && closeToAnInteger(so.getSubTree(1).getValue())) {
					if (Math.round(so.getSubTree(0).getValue()) % Math.round(so.getSubTree(1).getValue()) == 0) {
						return new StepConstant(so.getValue());
					}
				}
			}
			if (so.getSubTree(0).isOperation(Operation.MULTIPLY)) {
				((StepOperation) so.getSubTree(0)).addSubTree(StepNode.divide(new StepConstant(1), so.getSubTree(1)));
				return so.getSubTree(0).regroup();
			}
			if (!so.getSubTree(0).isConstant() && !so.getSubTree(1).isConstant()) {
				return StepNode.multiply(so.getSubTree(0), StepNode.power(so.getSubTree(1), -1)).regroup();
			}
		} else if (isOperation(Operation.POWER)) {
			if (so.getSubTree(1).getValue() == 2) {
				if (so.getSubTree(0).isOperation(Operation.MINUS)) {
					so.subtrees.set(0, ((StepOperation) so.getSubTree(0)).getSubTree(0));
				}
			}
			if (so.getSubTree(0).isOperation(Operation.NROOT)) {
				if (so.getSubTree(1).equals(((StepOperation) so.getSubTree(0)).getSubTree(1))) {
					return ((StepOperation) so.getSubTree(0)).getSubTree(0);
				}
			}
			if (closeToAnInteger(so.getSubTree(0).getValue()) && closeToAnInteger(so.getSubTree(1).getValue())) {
				return new StepConstant(Math.pow(so.getSubTree(0).getValue(), so.getSubTree(1).getValue()));
			}
			if (so.getSubTree(1).getValue() == 0) {
				return new StepConstant(1);
			}
			if (so.getSubTree(1).getValue() == 1) {
				return so.getSubTree(0);
			}
		} else if (isOperation(Operation.NROOT)) {
			if (so.getSubTree(0).isOperation(Operation.POWER)) {
				if (so.getSubTree(1).equals(((StepOperation) so.getSubTree(0)).getSubTree(1))) {
					return ((StepOperation) so.getSubTree(0)).getSubTree(0);
				}
			}
			if (so.getSubTree(1).getValue() == 1) {
				return so.getSubTree(0);
			}

			return so;
		}

		return so;
	}

	@Override
	public StepNode getConstantCoefficient() {
		if (isOperation(Operation.MINUS)) {
			return new StepConstant(-getSubTree(0).getConstantCoefficient().getValue());
		} else if (isOperation(Operation.MULTIPLY)) {
			double c = 1;
			for (int i = 0; i < noOfOperands(); i++) {
				c *= getSubTree(i).getConstantCoefficient().getValue();
			}
			return new StepConstant(c);
		} else if (isOperation(Operation.POWER)) {
			double a = getSubTree(0).getConstantCoefficient().getValue();
			double b = getSubTree(1).getConstantCoefficient().getValue();

			return new StepConstant(Math.pow(a, b));
		} else if (isOperation(Operation.NROOT)) {
			double a = getSubTree(0).getConstantCoefficient().getValue();
			double b = getSubTree(1).getConstantCoefficient().getValue();

			if (closeToAnInteger(a) && closeToAnInteger(b)) {
				long x = Math.round(a);
				long y = Math.round(b);

				int power = 1;
				int count = 0;

				while (x % 2 == 0) {
					count++;
					x /= 2;
				}

				count /= y;
				power *= Math.pow(2, count);

				for (int i = 3; i < x; i += 2) {
					count = 0;

					while (x % i == 0) {
						count++;
						x /= i;
					}

					count /= y;
					power *= Math.pow(i, count);
				}

				return new StepConstant(power);
			}
		}

		return new StepConstant(1);
	}

	@Override
	public StepNode divideAndSimplify(double x) {
		if (isOperation(Operation.MINUS)) {
			return getSubTree(0).divideAndSimplify(x);
		} else if (isOperation(Operation.MULTIPLY)) {
			StepNode sn = null;
			StepNode subtree = null;
			double dividedby = 1;
			for (int i = 0; i < noOfOperands(); i++) {
				double coeff = getSubTree(i).getConstantCoefficient().getValue();
				if (dividedby * coeff >= x) {
					subtree = getSubTree(i).divideAndSimplify(x / dividedby);
					dividedby = x;
				} else if (dividedby * coeff < x) {
					subtree = getSubTree(i).divideAndSimplify(coeff);
					dividedby *= coeff;
				}

				if (subtree.getValue() != 1) {
					sn = StepNode.multiply(sn, subtree);
				}
			}
			return sn;
		} else if (isOperation(Operation.POWER)) {
			double power = getSubTree(1).getValue();

			StepOperation sn = new StepOperation(Operation.POWER);
			sn.addSubTree(getSubTree(0).divideAndSimplify(Math.pow(x, 1 / power)));
			sn.addSubTree(getSubTree(1));

			return sn;
		} else if (isOperation(Operation.NROOT)) {
			double root = getSubTree(1).getValue();

			StepOperation sn = new StepOperation(Operation.NROOT);
			sn.addSubTree(getSubTree(0).divideAndSimplify(Math.pow(x, root)));
			sn.addSubTree(getSubTree(1));

			return sn;
		}

		return this;
	}

	@Override
	public StepNode constantRegroup() {
		if (!isOperation(Operation.PLUS)) {
			return this;
		}

		double constants = 0;

		for (int i = 0; i < noOfOperands(); i++) {
			if (subtrees.get(i) instanceof StepConstant) {
				constants += subtrees.get(i).getValue();
				subtrees.remove(i);
				i--;
			}
		}

		double[] coefficients = new double[noOfOperands()];
		StepNode[] remainder = new StepNode[noOfOperands()];
		for (int i = 0; i < noOfOperands(); i++) {
			coefficients[i] = getSubTree(i).getConstantCoefficient().getValue();
			remainder[i] = getSubTree(i).divideAndSimplify(Math.abs(coefficients[i]));
		}

		for (int i = 0; i < noOfOperands(); i++) {
			if (coefficients[i] != 0) {
				for (int j = i + 1; j < noOfOperands(); j++) {
					if (remainder[i].equals(remainder[j])) {
						coefficients[i] += coefficients[j];
						coefficients[j] = 0;
					}
				}
			}
		}

		StepOperation so = new StepOperation(Operation.PLUS);
		for (int i = 0; i < noOfOperands(); i++) {
			if (coefficients[i] != 0) {
				if (coefficients[i] == 1) {
					so.addSubTree(remainder[i]);
				} else if (coefficients[i] == -1) {
					so.addSubTree(StepNode.minus(remainder[i]));
				} else {
					so.addSubTree(StepNode.multiply(new StepConstant(coefficients[i]), remainder[i]));
				}
			}
		}

		if (so.noOfOperands() == 0) {
			return new StepConstant(constants);
		}

		if (constants != 0) {
			so.addSubTree(new StepConstant(constants));
		}
		if (so.noOfOperands() == 1) {
			return so.getSubTree(0);
		}

		return so;
	}

	@Override
	public StepNode expand() {
		for (int i = 0; i < noOfOperands(); i++) {
			subtrees.set(i, getSubTree(i).expand());
		}

		if (isOperation(Operation.MULTIPLY)) {
			StepOperation so = null;

			if (StepHelper.countOperation(this, Operation.DIVIDE) > 0) {
				for (int i = 0; i < noOfOperands(); i++) {
					if (getSubTree(i).isOperation(Operation.PLUS) && StepHelper.countOperation(getSubTree(i), Operation.DIVIDE) > 0) {
						so = (StepOperation) subtrees.remove(i);
						break;
					}
				}

				if (so == null) {
					return this;
				}

				for (int i = 0; i < noOfOperands(); i++) {
					for (int j = 0; j < so.noOfOperands(); j++) {
						so.subtrees.set(j, StepNode.multiply(so.getSubTree(j), getSubTree(i)));
					}
				}

				return so.regroup().expand();
			}

			for (int i = 0; i < noOfOperands(); i++) {
				if (getSubTree(i).isOperation(Operation.PLUS)) {
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
					for (int j = 0; j < so.noOfOperands(); j++) {
						so.subtrees.set(j, StepNode.multiply(so.getSubTree(j), getSubTree(i)));
					}
				}
			}

			return so.regroup();
		} else if (isOperation(Operation.POWER)) {
			if ((getSubTree(0).isOperation(Operation.PLUS) || getSubTree(0).isOperation(Operation.MULTIPLY))
					&& closeToAnInteger(getSubTree(1).getValue())) {
				long n = Math.round(getSubTree(1).getValue());

				if (n < 0) {
					return this;
				}

				StepNode sn = null;

				for (int i = 0; i < n; i++) {
					sn = StepNode.multiply(sn, getSubTree(0));
				}

				return sn.expand();
			}
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

	public static boolean closeToAnInteger(double d) {
		return Math.abs(Math.round(d) - d) < 0.0000001;
	}

	public static boolean isEqual(double a, double b) {
		return Math.abs(a - b) < 0.0000001;
	}

	@Override
	public int compareTo(StepNode sn) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;
			int eq = so.getOperation().compareTo(getOperation());
			if (eq != 0) {
				return eq;
			}
			eq = Integer.compare(noOfOperands(), so.noOfOperands());
			if (eq != 0) {
				return eq;
			}

			for (int i = 0; i < noOfOperands(); i++) {
				eq = getSubTree(i).compareTo(so.getSubTree(i));
				if (eq != 0) {
					return eq;
				}
			}

			return 0;
		}
		return -1;
	}
}
