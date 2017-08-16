package org.geogebra.common.kernel.stepbystep.steptree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

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
			StepNode constant = null;
			for (int i = 0; i < noOfOperands(); i++) {
				constant = StepNode.multiply(constant, getSubTree(i).getCoefficient());
			}
			return constant;
		} else if (isOperation(Operation.MINUS)) {
			StepNode coeff = getSubTree(0).getCoefficient();
			return coeff == null ? new StepConstant(-1) : StepNode.minus(coeff);
		}

		return null;
	}

	@Override
	public StepNode getVariable() {
		if (isConstant()) {
			return null;
		}

		if (isOperation(Operation.MULTIPLY)) {
			StepNode variable = null;
			for (int i = 0; i < noOfOperands(); i++) {
				variable = StepNode.multiply(variable, getSubTree(i).getVariable());
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
		boolean changed = true;
		StepNode sn = this;

		sort();

		while (changed) {
			changed = false;

			Boolean[] temp = new Boolean[] { false };

			sn = doubleMinus(sn, temp);
			if (temp[0]) {
				Log.error("DoubleMinus");
				Log.error(sn.toString());
				temp[0] = false;
				changed = true;
			}

			sn = trivialPowers(sn, temp);
			if (temp[0]) {
				Log.error("TrivialPowers");
				Log.error(sn.toString());
				temp[0] = false;
				changed = true;
			}

			sn = distributeMinus(sn, temp);
			if (temp[0]) {
				Log.error("MinusDistributed");
				Log.error(sn.toString());
				temp[0] = false;
				changed = true;
			}

			sn = minusConstant(sn, temp);
			if (temp[0]) {
				Log.error("MinusConstant");
				Log.error(sn.toString());
				temp[0] = false;
				changed = true;
			}

			sn = sumOfConstants(sn, temp);
			if (temp[0]) {
				Log.error("SumOfConstants");
				Log.error(sn.toString());
				temp[0] = false;
				changed = true;
			}

			sn = regroupProducts(sn, temp);
			if (temp[0]) {
				Log.error("ProductsRegrouped");
				Log.error(sn.toString());
				temp[0] = false;
				changed = true;
			}

			sn = regroupSums(sn, temp);
			if (temp[0]) {
				Log.error("RegroupedSums");
				Log.error(sn.toString());
				temp[0] = false;
				changed = true;
			}

			sn = squaringMinuses(sn, temp);
			if (temp[0]) {
				Log.error("MinusesSquared");
				Log.error(sn.toString());
				temp[0] = false;
				changed = true;
			}

			sn = sameRootAsPower(sn, temp);
			if (temp[0]) {
				Log.error("SameRootAsPower");
				Log.error(sn.toString());
				temp[0] = false;
				changed = true;
			}

		}
		Log.error("Done: ");
		Log.error(sn.toString());

		return sn;
	}

	private static StepNode doubleMinus(StepNode sn, Boolean[] changed) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.MINUS)) {
				if (so.getSubTree(0).isOperation(Operation.MINUS)) {
					changed[0] = true;
					return doubleMinus(((StepOperation) so.getSubTree(0)).getSubTree(0), changed);
				}
			}

			StepOperation toReturn = new StepOperation(so.getOperation());
			for (int i = 0; i < so.noOfOperands(); i++) {
				toReturn.addSubTree(doubleMinus(so.getSubTree(i), changed));
			}
			return toReturn;
		}

		return sn;
	}

	private static StepNode distributeMinus(StepNode sn, Boolean[] changed) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.MINUS)) {
				if (so.getSubTree(0).isOperation(Operation.PLUS)) {
					changed[0] = true;
					StepOperation innerPlus = (StepOperation) so.getSubTree(0);
					for (int i = 0; i < innerPlus.noOfOperands(); i++) {
						innerPlus.subtrees.set(i, minus(innerPlus.getSubTree(i)));
					}
					return distributeMinus(innerPlus, changed);
				}
			}

			StepOperation toReturn = new StepOperation(so.getOperation());
			for (int i = 0; i < so.noOfOperands(); i++) {
				toReturn.addSubTree(distributeMinus(so.getSubTree(i), changed));
			}
			return toReturn;
		}

		return sn;
	}

	private static StepNode minusConstant(StepNode sn, Boolean[] changed) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.MINUS)) {
				if (so.getSubTree(0) instanceof StepConstant) {
					changed[0] = true;
					return new StepConstant(-so.getSubTree(0).getValue());
				}
			}

			for (int i = 0; i < so.noOfOperands(); i++) {
				so.subtrees.set(i, distributeMinus(so.getSubTree(i), changed));
			}
		}

		return sn;
	}

	private static StepNode sumOfConstants(StepNode sn, Boolean[] changed) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			for (int i = 0; i < so.noOfOperands(); i++) {
				so.subtrees.set(i, sumOfConstants(so.getSubTree(i), changed));
			}

			if (so.isOperation(Operation.PLUS)) {
				double constants = 0;

				for (int i = 0; i < so.noOfOperands(); i++) {
					if (so.subtrees.get(i) instanceof StepConstant) {
						constants += so.subtrees.get(i).getValue();
						so.subtrees.remove(i);
						i--;
					}
				}

				if (so.noOfOperands() == 0) {
					return new StepConstant(constants);
				}

				if (constants != 0) {
					so.addSubTree(new StepConstant(constants));
				}
			}

			return so;
		}

		return sn;
	}

	private static StepNode regroupSums(StepNode sn, Boolean[] changed) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			for (int i = 0; i < so.noOfOperands(); i++) {
				so.subtrees.set(i, regroupSums(so.getSubTree(i), changed));
			}

			if (so.isOperation(Operation.PLUS)) {
				StepNode[] coefficients = new StepNode[so.noOfOperands()];
				StepNode[] variables = new StepNode[so.noOfOperands()];
				for (int i = 0; i < so.noOfOperands(); i++) {
					coefficients[i] = so.getSubTree(i).getCoefficient();
					if (coefficients[i] == null) {
						coefficients[i] = new StepConstant(1);
					}
					variables[i] = so.getSubTree(i).getVariable();
					if (variables[i] == null) {
						variables[i] = new StepConstant(1);
					}
				}

				for (int i = 0; i < so.noOfOperands(); i++) {
					if (coefficients[i].getValue() != 0) {
						for (int j = i + 1; j < so.noOfOperands(); j++) {
							if (variables[i].equals(variables[j])) {
								coefficients[i] = StepNode.add(coefficients[i], coefficients[j]);
								coefficients[j] = new StepConstant(0);
							}
						}
					}
				}

				StepOperation newSum = new StepOperation(Operation.PLUS);
				for (int i = 0; i < so.noOfOperands(); i++) {
					coefficients[i] = coefficients[i].constantRegroup();
					if (coefficients[i].getValue() != 0) {
						if (coefficients[i].getValue() == 1) {
							newSum.addSubTree(variables[i]);
						} else if (variables[i].getValue() == 1) {
							newSum.addSubTree(coefficients[i]);
						} else if (coefficients[i].getValue() == -1) {
							newSum.addSubTree(StepNode.minus(variables[i]));
						} else {
							newSum.addSubTree(StepNode.multiply(coefficients[i], variables[i]));
						}
					}
				}

				if (newSum.noOfOperands() == 0) {
					return new StepConstant(0);
				} else if (newSum.noOfOperands() == 1) {
					return newSum.getSubTree(0);
				}
				return newSum;
			}

			return so;
		}

		return sn;
	}

	private static StepNode regroupProducts(StepNode sn, Boolean[] changed) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			for (int i = 0; i < so.noOfOperands(); i++) {
				so.subtrees.set(i, regroupProducts(so.getSubTree(i), changed));
			}

			if (so.isOperation(Operation.MULTIPLY) || so.isOperation(Operation.DIVIDE)) {
				List<StepNode> bases = new ArrayList<StepNode>();
				List<StepNode> exponents = new ArrayList<StepNode>();

				getBasesAndExponents(so, null, bases, exponents);

				double nominator = 1;
				double denominator = 1;
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

				StepNode soNominator = null;
				StepNode soDenominator = null;
				for (int i = 0; i < bases.size(); i++) {
					exponents.set(i, exponents.get(i).constantRegroup());
					if (exponents.get(i).getValue() != 0 && bases.get(i).getValue() != 1) {
						if (exponents.get(i).getValue() == 1) {
							soNominator = StepNode.multiply(soNominator, bases.get(i));
						} else if (exponents.get(i).getValue() == -1) {
							soDenominator = StepNode.multiply(soDenominator, bases.get(i));
						} else if (exponents.get(i).getValue() == 0.5) {
							soNominator = StepNode.multiply(soNominator, StepNode.root(bases.get(i), 2));
						} else {
							soNominator = StepNode.multiply(soNominator, StepNode.power(bases.get(i), exponents.get(i)));
						}
					}
				}

				if (soNominator != null && soDenominator != null) {
					StepNode divided = StepNode.polynomialDivision(soNominator.deepCopy(), soDenominator.deepCopy(), new StepVariable("x"));

					if (divided != null) {
						soNominator = divided;
						soDenominator = null;
					}
				}

				if (nominator == 0) {
					return new StepConstant(0);
				}

				if (closeToAnInteger(nominator) && closeToAnInteger(denominator)) {
					long gcd = StepNode.gcd(Math.round(nominator), Math.round(denominator));
					nominator = Math.round(nominator / gcd);
					denominator = Math.round(denominator / gcd);
				} else {
					nominator /= denominator;
					denominator = 1;
				}

				if (!isEqual(nominator, 1) || soNominator == null) {
					soNominator = StepNode.multiply(nominator, soNominator);
				}
				if (!isEqual(denominator, 1)) {
					soDenominator = StepNode.multiply(denominator, soDenominator);
				}

				return StepNode.divide(soNominator, soDenominator);
			}

			return so;
		}

		return sn;
	}

	private static void getBasesAndExponents(StepNode sn, StepNode currentExp, List<StepNode> bases, List<StepNode> exponents) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			switch (so.getOperation()) {
			case MULTIPLY:
				for (int i = 0; i < so.noOfOperands(); i++) {
					getBasesAndExponents(so.getSubTree(i), currentExp, bases, exponents);
				}
				return;
			case MINUS:
				bases.add(new StepConstant(-1));
				exponents.add(new StepConstant(1));
				getBasesAndExponents(so.getSubTree(0), currentExp, bases, exponents);
				return;
			case DIVIDE:
				getBasesAndExponents(so.getSubTree(0), currentExp, bases, exponents);
				getBasesAndExponents(so.getSubTree(1), StepNode.multiply(-1, currentExp), bases, exponents);
				return;
			case POWER:
				getBasesAndExponents(so.getSubTree(0), StepNode.multiply(so.getSubTree(1), currentExp), bases, exponents);
				return;
			case NROOT:
				getBasesAndExponents(so.getSubTree(0),
						StepNode.divide(currentExp == null ? new StepConstant(1) : currentExp, so.getSubTree(1)), bases, exponents);
				return;
			}
		}

		bases.add(sn);
		exponents.add(currentExp == null ? new StepConstant(1) : currentExp);
	}

	private static StepNode squaringMinuses(StepNode sn, Boolean[] changed) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.POWER)) {
				if (so.getSubTree(1).getValue() == 2) {
					if (so.getSubTree(0).isOperation(Operation.MINUS)) {
						changed[0] = true;
						return squaringMinuses(((StepOperation) so.getSubTree(0)).getSubTree(0), changed);
					}
				}
			}

			for (int i = 0; i < so.noOfOperands(); i++) {
				so.subtrees.set(i, squaringMinuses(so.getSubTree(i), changed));
			}
		}

		return sn;
	}

	private static StepNode sameRootAsPower(StepNode sn, Boolean[] changed) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.POWER)) {
				if (so.getSubTree(0).isOperation(Operation.NROOT)) {
					if (so.getSubTree(1).equals(((StepOperation) so.getSubTree(0)).getSubTree(1))) {
						changed[0] = true;
						return sameRootAsPower(((StepOperation) so.getSubTree(0)).getSubTree(0), changed);
					}
				}
			} else if (so.isOperation(Operation.NROOT)) {
				if (so.getSubTree(0).isOperation(Operation.POWER)) {
					if (so.getSubTree(1).equals(((StepOperation) so.getSubTree(0)).getSubTree(1))) {
						changed[0] = true;
						return sameRootAsPower(((StepOperation) so.getSubTree(0)).getSubTree(0), changed);
					}
				}
			}

			for (int i = 0; i < so.noOfOperands(); i++) {
				so.subtrees.set(i, sameRootAsPower(so.getSubTree(i), changed));
			}
		}

		return sn;
	}

	private static StepNode trivialPowers(StepNode sn, Boolean[] changed) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.POWER)) {
				if (closeToAnInteger(so.getSubTree(0).getValue()) && closeToAnInteger(so.getSubTree(1).getValue())) {
					changed[0] = true;
					return new StepConstant(Math.pow(so.getSubTree(0).getValue(), so.getSubTree(1).getValue()));
				}
				if (so.getSubTree(1).getValue() == 0) {
					changed[0] = true;
					return new StepConstant(1);
				}
				if (so.getSubTree(1).getValue() == 1) {
					changed[0] = true;
					return trivialPowers(so.getSubTree(0), changed);
				}
			} else if (so.isOperation(Operation.NROOT)) {
				if (so.getSubTree(1).getValue() == 1) {
					changed[0] = true;
					return trivialPowers(so.getSubTree(0), changed);
				}
			}

			for (int i = 0; i < so.noOfOperands(); i++) {
				so.subtrees.set(i, trivialPowers(so.getSubTree(i), changed));
			}
		}

		return sn;
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
			return getSubTree(0).divideAndSimplify(-x);
		} else if (isOperation(Operation.MULTIPLY)) {
			StepNode sn = null;
			StepNode subtree = null;
			for (int i = 0; i < noOfOperands(); i++) {
				double coeff = getSubTree(i).getConstantCoefficient().getValue();
				subtree = getSubTree(i).divideAndSimplify(coeff);

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
			remainder[i] = getSubTree(i).divideAndSimplify(coefficients[i]);
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
