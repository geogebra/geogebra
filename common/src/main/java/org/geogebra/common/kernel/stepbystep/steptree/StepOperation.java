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
				if (i != 0 && requiresDot(subtrees.get(i - 1), subtrees.get(i))) {
					sp.append(" \\cdot ");
				} else if (i != 0) {
					sp.append(" ");
				}

				boolean parantheses = subtrees.get(i).getPriority() < getPriority()
						|| (subtrees.get(i).nonSpecialConstant() && subtrees.get(i).getValue() < 0 && i != 0);

				if (parantheses) {
					sp.append("\\left(");
				}
				sp.append(subtrees.get(i).toLaTeXString());
				if (parantheses) {
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
		case SIN:
		case COS:
		case TAN:
		case CSC:
		case SEC:
		case COT:
		case ARCSIN:
		case ARCCOS:
		case ARCTAN:
			return "\\" + operation.toString().toLowerCase() + "\\left(" + subtrees.get(0).toLaTeXString() + "\\right)";
		}
		return "";
	}

	private static boolean requiresDot(StepNode a, StepNode b) {
		if (a.nonSpecialConstant() && b.nonSpecialConstant()) {
			return true;
		}

		if ((a instanceof StepVariable || a.isOperation()) && (b instanceof StepVariable || b.isOperation())) {
			return true;
		}

		return false;
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

		if (isOperation(Operation.MULTIPLY)) {
			subtrees.sort(new Comparator<StepNode>() {
				public int compare(StepNode arg0, StepNode arg1) {
					return arg0.compareTo(arg1);
				}
			});
		} else if (isOperation(Operation.PLUS)) {
			subtrees.sort(new Comparator<StepNode>() {
				public int compare(StepNode arg0, StepNode arg1) {
					return arg1.compareTo(arg0);
				}
			});
		}
	}

	@Override
	public StepNode regroup() {
		return regroup(new Boolean[1]);
	}

	@Override
	public StepNode regroup(Boolean[] changed) {
		StepNode sn = this;

		if (sn.isOperation()) {
			((StepOperation) sn).sort();
		}

		Boolean[] changedInCycle = new Boolean[] { true };
		Boolean[] changedInSubstep = new Boolean[] { false };

		while (changedInCycle[0]) {
			changedInCycle[0] = false;
			changedInSubstep[0] = false;

			// Log.error("Regroup: ");
			// Log.error(sn.toString());

			sn = doubleMinus(sn, changedInSubstep);
			regroupStep(sn, "DoubleMinus", changedInSubstep, changedInCycle);

			sn = trivialPowers(sn, changedInSubstep);
			regroupStep(sn, "TrivialPowers", changedInSubstep, changedInCycle);

			sn = distributeMinus(sn, changedInSubstep);
			regroupStep(sn, "MinusDistributed", changedInSubstep, changedInCycle);

			sn = minusConstant(sn, changedInSubstep);
			regroupStep(sn, "MinusConstant", changedInSubstep, changedInCycle);

			sn = nicerFractions(sn, changedInSubstep);
			regroupStep(sn, "FractionsNicened", changedInSubstep, changedInCycle);

			sn = factorConstant(sn, changedInSubstep);
			regroupStep(sn, "FactorConstant", changedInSubstep, changedInCycle);

			sn = regroupProducts(sn, changedInSubstep);
			regroupStep(sn, "ProductsRegrouped", changedInSubstep, changedInCycle);

			sn = regroupSums(sn, changedInSubstep);
			regroupStep(sn, "RegroupedSums", changedInSubstep, changedInCycle);

			sn = addFractions(sn, changedInSubstep);
			regroupStep(sn, "FractionsAdded", changedInSubstep, changedInCycle);

			sn = squaringMinuses(sn, changedInSubstep);
			regroupStep(sn, "MinusesSquared", changedInSubstep, changedInCycle);

			sn = sameRootAsPower(sn, changedInSubstep);
			regroupStep(sn, "SameRootAsPower", changedInSubstep, changedInCycle);

			sn = calculateInverseTrigo(sn, changedInSubstep);
			regroupStep(sn, "InverseTrigoCalculated", changedInSubstep, changedInCycle);

			if (changedInCycle[0]) {
				changed[0] = true;
			}
		}
		// Log.error("Done: ");
		// Log.error(sn.toString());

		if (sn.isOperation()) {
			((StepOperation) sn).sort();
		}

		return sn;
	}

	private static void regroupStep(StepNode sn, String text, Boolean[] temp, Boolean[] changed) {
		final boolean printDebug = false;

		if (printDebug) {
			Log.error(text);
			Log.error(sn.toString());
		}
		if (temp[0]) {
			temp[0] = false;
			changed[0] = true;
		}
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

	private static StepNode nicerFractions(StepNode sn, Boolean[] changed) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.NROOT)) {
				if (so.getSubTree(0).isOperation(Operation.DIVIDE)) {
					changed[0] = true;

					StepNode nominator = StepNode.root(((StepOperation) so.getSubTree(0)).getSubTree(0), so.getSubTree(1));
					StepNode denominator = StepNode.root(((StepOperation) so.getSubTree(0)).getSubTree(1), so.getSubTree(1));

					return nicerFractions(StepNode.divide(nominator, denominator), changed);
				}
			} else if (so.isOperation(Operation.DIVIDE)) {
				if (so.getSubTree(1).isOperation(Operation.NROOT)
						&& isEqual(((StepOperation) so.getSubTree(1)).getSubTree(1).getValue(), 2)) {
					changed[0] = true;

					StepNode nominator = StepNode.multiply(so.getSubTree(0), so.getSubTree(1));
					StepNode denominator = ((StepOperation) so.getSubTree(1)).getSubTree(0);

					return nicerFractions(StepNode.divide(nominator, denominator), changed);
				}

				if (so.getSubTree(0).isOperation(Operation.NROOT) && so.getSubTree(1).isOperation(Operation.NROOT)) {
					double a = ((StepOperation) so.getSubTree(0)).getSubTree(0).getValue();
					double b = ((StepOperation) so.getSubTree(1)).getSubTree(0).getValue();

					if (closeToAnInteger(a) && closeToAnInteger(b)) {
						long roota = Math.round(((StepOperation) so.getSubTree(0)).getSubTree(1).getValue());
						long rootb = Math.round(((StepOperation) so.getSubTree(1)).getSubTree(1).getValue());
						
						long commonRoot = lcm(roota, rootb);
						
						long remainderA = commonRoot / roota;
						long remainderB = commonRoot / rootb;

						return StepNode
								.root(StepNode.divide(Math.pow(a, remainderA),
										Math.pow(b, remainderB)), commonRoot);
					}
				}
			}

			StepOperation toReturn = new StepOperation(so.getOperation());
			for (int i = 0; i < so.noOfOperands(); i++) {
				toReturn.addSubTree(nicerFractions(so.getSubTree(i), changed));
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
				if (so.getSubTree(0).nonSpecialConstant()) {
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

	private static StepNode factorConstant(StepNode sn, Boolean[] changed) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			for (int i = 0; i < so.noOfOperands(); i++) {
				so.subtrees.set(i, factorConstant(so.getSubTree(i), changed));
			}

			if (so.isOperation(Operation.NROOT)) {
				StepNode coefficient = so.getSubTree(0).getIntegerCoefficient();
				StepNode remainder = so.getSubTree(0).getNonInteger();

				if (coefficient == null) {
					return so;
				}

				double root = so.getSubTree(1).getValue();
				
				if(coefficient.isOperation(Operation.DIVIDE)) {	
					double oldNominator = ((StepOperation) coefficient).getSubTree(0).getValue();
					double oldDenominator = ((StepOperation) coefficient).getSubTree(1).getValue();
					
					double nominator = highestNthPower(oldNominator, root);
					double denominator = highestNthPower(oldDenominator, root);
					

					return StepNode.multiply(StepNode.divide(nominator, denominator),
							StepNode.root(
									StepNode.multiply(StepNode.divide(oldNominator / Math.pow(nominator, root),
											oldDenominator / Math.pow(denominator, root)), remainder),
									so.getSubTree(1)));
				}

				double newCoefficient = highestNthPower(coefficient.getValue(), so.getSubTree(1).getValue());

				return StepNode.multiply(newCoefficient, StepNode
						.root(StepNode.multiply(coefficient.getValue() / Math.pow(newCoefficient, root), remainder), so.getSubTree(1)));
			}

			return so;
		}

		return sn;
	}

	private static double highestNthPower(double a, double b) {
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

			return power;
		}

		return a;
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

				int counter = 0;
				double constants = 0;

				for (int i = 0; i < so.noOfOperands(); i++) {
					if (coefficients[i].nonSpecialConstant() && isEqual(variables[i].getValue(), 1)) {
						constants += coefficients[i].getValue();
						coefficients[i] = new StepConstant(0);
						counter++;
					}
				}

				if (counter > 1) {
					changed[0] = true;
				}

				for (int i = 0; i < so.noOfOperands(); i++) {
					if (!isEqual(coefficients[i].getValue(), 0)) {
						for (int j = i + 1; j < so.noOfOperands(); j++) {
							if (coefficients[j].getValue() != 0 && variables[i].equals(variables[j])) {
								coefficients[i] = regroupConstant(StepNode.add(coefficients[i], coefficients[j]), changed);
								coefficients[j] = new StepConstant(0);
							}
						}
					}
				}

				StepOperation newSum = new StepOperation(Operation.PLUS);

				if (!isEqual(constants, 0)) {
					newSum.addSubTree(new StepConstant(constants));
				}

				for (int i = 0; i < so.noOfOperands(); i++) {
					if (coefficients[i].getValue() != 0) {
						if (isEqual(coefficients[i].getValue(), 1)) {
							newSum.addSubTree(variables[i]);
						} else if (isEqual(variables[i].getValue(), 1)) {
							newSum.addSubTree(coefficients[i]);
						} else if (isEqual(coefficients[i].getValue(), -1)) {
							newSum.addSubTree(StepNode.minus(variables[i]));
						} else {
							newSum.addSubTree(regroupProducts(StepNode.multiply(coefficients[i], variables[i]), new Boolean[1]));
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

	private static StepNode regroupConstant(StepNode sn, Boolean[] changed) {
		if (!sn.isOperation(Operation.PLUS)) {
			return sn;
		}

		StepNode[] coefficients = new StepNode[((StepOperation) sn).noOfOperands()];
		StepNode[] remainder = new StepNode[((StepOperation) sn).noOfOperands()];
		for (int i = 0; i < ((StepOperation) sn).noOfOperands(); i++) {
			coefficients[i] = ((StepOperation) sn).getSubTree(i).getIntegerCoefficient();
			remainder[i] = ((StepOperation) sn).getSubTree(i).getNonInteger();

			if (coefficients[i] == null) {
				coefficients[i] = new StepConstant(1);
			}
			if (remainder[i] == null) {
				remainder[i] = new StepConstant(1);
			}
		}

		for (int i = 0; i < ((StepOperation) sn).noOfOperands(); i++) {
			if (!isEqual(coefficients[i].getValue(), 0)) {
				for (int j = i + 1; j < ((StepOperation) sn).noOfOperands(); j++) {
					if (remainder[i].equals(remainder[j])) {
						changed[0] = true;
						coefficients[i] = StepNode.add(coefficients[i], coefficients[j]);
						coefficients[j] = new StepConstant(0);
					}
				}
			}
		}

		StepOperation so = new StepOperation(Operation.PLUS);
		for (int i = 0; i < ((StepOperation) sn).noOfOperands(); i++) {
			if (!isEqual(coefficients[i].getValue(), 0)) {
				if (isEqual(coefficients[i].getValue(), 1)) {
					so.addSubTree(remainder[i]);
				} else if (isEqual(remainder[i].getValue(), 1)) {
					so.addSubTree(coefficients[i]);
				} else if (isEqual(coefficients[i].getValue(), -1)) {
					so.addSubTree(StepNode.minus(remainder[i]));
				} else {
					so.addSubTree(regroupProducts(StepNode.multiply(coefficients[i], remainder[i]), new Boolean[1]));
				}
			}
		}

		if (so.noOfOperands() == 0) {
			return new StepConstant(0);
		}

		if (so.noOfOperands() == 1) {
			return so.getSubTree(0);
		}

		return so;
	}

	private static StepNode addFractions(StepNode sn, Boolean[] changed) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			for (int i = 0; i < so.noOfOperands(); i++) {
				so.subtrees.set(i, addFractions(so.getSubTree(i), changed));
			}
			if (so.isOperation(Operation.PLUS)) {
				List<StepNode> nominators = new ArrayList<StepNode>();
				List<Long> denominators = new ArrayList<Long>();

				long newDenominator = 1;

				for (int i = 0; i < so.noOfOperands(); i++) {
					long currentDenominator = getDenominator(so.getSubTree(i));
					if (currentDenominator != 0) {
						nominators.add(getNominator(so.getSubTree(i)));
						denominators.add(currentDenominator);

						newDenominator = lcm(newDenominator, currentDenominator);

						so.subtrees.remove(i);
						i--;
					}
				}

				StepNode newNominator = null;

				for (int i = 0; i < nominators.size(); i++) {
					if (newDenominator != denominators.get(i)) {
						newNominator = StepNode.add(newNominator,
								StepNode.multiply(((double) newDenominator) / denominators.get(i), nominators.get(i)));
					} else {
						newNominator = StepNode.add(newNominator, nominators.get(i));
					}
				}

				if (newDenominator != 1) {
					if (nominators.size() > 1) {
						changed[0] = true;
					}
					so.addSubTree(StepNode.divide(newNominator, newDenominator));
				} else {
					so.addSubTree(newNominator);
				}
			}

			return so;
		}

		return sn;
	}

	private static long getDenominator(StepNode sn) {
		if (sn.nonSpecialConstant()) {
			return 1;
		} else if (sn.isOperation(Operation.MINUS)) {
			return getDenominator(((StepOperation) sn).getSubTree(0));
		} else if (sn.isOperation(Operation.DIVIDE)) {
			if (closeToAnInteger(((StepOperation) sn).getSubTree(0).getValue())
					&& closeToAnInteger(((StepOperation) sn).getSubTree(1).getValue())) {
				return Math.round(((StepOperation) sn).getSubTree(1).getValue());
			}
		}
		return 0;
	}

	private static StepNode getNominator(StepNode sn) {
		if (sn.nonSpecialConstant()) {
			return sn;
		} else if (sn.isOperation(Operation.MINUS)) {
			return StepNode.minus(getNominator(((StepOperation) sn).getSubTree(0)));
		} else if (sn.isOperation(Operation.DIVIDE)) {
			return ((StepOperation) sn).getSubTree(0);
		}
		return null;
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

				boolean containsArbitraryInteger = false;

				int nomCounter = 0;
				int denomCounter = 0;

				double nominator = 1;
				double denominator = 1;
				for (int i = 0; i < bases.size(); i++) {
					if (bases.get(i).nonSpecialConstant() && isEqual(exponents.get(i).getValue(), 1)) {
						nominator *= bases.get(i).getValue();
						exponents.set(i, new StepConstant(0));
						nomCounter++;
					} else if (bases.get(i).nonSpecialConstant() && isEqual(exponents.get(i).getValue(), -1)) {
						denominator *= bases.get(i).getValue();
						exponents.set(i, new StepConstant(0));
						denomCounter++;
					}
				}

				if (nomCounter > 1 || denomCounter > 1) {
					changed[0] = true;
				}

				for (int i = 0; i < bases.size(); i++) {
					if (bases.get(i) instanceof StepArbitraryConstant && isEqual(Math.abs(exponents.get(i).getValue()), 1)) {
						containsArbitraryInteger = true;
					}

					if (!isEqual(exponents.get(i).getValue(), 0)) {
						for (int j = i + 1; j < bases.size(); j++) {
							if (!isEqual(exponents.get(j).getValue(), 0) && bases.get(i).equals(bases.get(j))) {
								changed[0] = true;
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
					if (!isEqual(exponents.get(i).getValue(), 0) && !isEqual(bases.get(i).getValue(), 1)) {
						double exponentValue = exponents.get(i).getValue();
						
						if (isEqual(exponentValue, 1)) {
							soNominator = StepNode.multiply(bases.get(i), soNominator);
						} else if (isEqual(exponentValue, -1)) {
							soDenominator = StepNode.multiply(bases.get(i), soDenominator);
						} else if (exponentValue > 0 && closeToAnInteger(1 / exponentValue)) {
							soNominator = StepNode.multiply(StepNode.root(bases.get(i), 1 / exponentValue), soNominator);
						} else if (exponentValue < 0 && closeToAnInteger(1 / exponentValue)) {
							soDenominator = StepNode.multiply(StepNode.root(bases.get(i), -1 / exponentValue), soDenominator);
						} else {
							soNominator = StepNode.multiply(StepNode.power(bases.get(i), exponents.get(i)), soNominator);
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

				boolean negative = false;
				if (nominator * denominator < 0) {
					nominator = Math.abs(nominator);
					denominator = Math.abs(denominator);
					negative = true;
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

				if (negative && !containsArbitraryInteger) {
					return StepNode.minus(StepNode.divide(soNominator, soDenominator));
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
						so.subtrees.set(0, ((StepOperation) so.getSubTree(0)).getSubTree(0));
						return squaringMinuses(so, changed);
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
						if (isEven(so.getSubTree(1).getValue())) {
							return StepNode.abs(sameRootAsPower(((StepOperation) so.getSubTree(0)).getSubTree(0), changed));
						}
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

				if(closeToAnInteger(so.getSubTree(0).getValue()) && closeToAnInteger(so.getSubTree(1).getValue())) {
					long root = Math.round(so.getSubTree(1).getValue());
					long power = getIntegerPower(Math.round(so.getSubTree(0).getValue()));

					long gcd = gcd(root, power);

					if (gcd > 1) {
						changed[0] = true;
						return StepNode.root(new StepConstant(Math.pow(so.getSubTree(0).getValue(), ((double) 1) / gcd)),
								((double) root) / gcd);
					}
				}
			}

			for (int i = 0; i < so.noOfOperands(); i++) {
				so.subtrees.set(i, trivialPowers(so.getSubTree(i), changed));
			}
		}

		return sn;
	}

	private static long getIntegerPower(long x) {
		long temp = x;
		if (temp < 0) {
			temp = -temp;
		}

		if (temp == 1) {
			return 1;
		}

		long power = 0;
		long currentPower;
		for (int i = 2; i <= temp; i++) {
			currentPower = 0;
			while(temp % i == 0) {
				currentPower ++;
				temp /= i;
			}
			power = gcd(power, currentPower);
		}
		return power;
	}

	private static StepNode calculateInverseTrigo(StepNode sn, Boolean[] changed) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isInverseTrigonometric()) {
				StepNode value = inverseTrigoLookup(so);
				if (value != null) {
					changed[0] = true;
					return value;
				}
			}

			for (int i = 0; i < so.noOfOperands(); i++) {
				so.subtrees.set(i, calculateInverseTrigo(so.getSubTree(i), changed));
			}
		}

		return sn;
	}

	private static StepNode inverseTrigoLookup(StepOperation so) {
		String[] arguments = new String[] { "-1", "-(nroot(3, 2))/(2)", "-(nroot(2, 2))/(2)", "-(1)/(2)", "0", "(1)/(2)",
				"(nroot(2, 2))/(2)", "(nroot(3, 2))/(2)", "1" };
		String[] argumentsTan = new String[] { "", "-nroot(3, 2)", "-1", "-nroot(3, 2)/3", "0", "nroot(3, 2)/3", "1", "nroot(3, 2)", "" };

		StepNode pi = new StepConstant(Math.PI);
		StepNode[] valuesSinTan = new StepNode[] { StepNode.minus(StepNode.divide(pi, 2)), StepNode.minus(StepNode.divide(pi, 3)),
				StepNode.minus(StepNode.divide(pi, 4)), StepNode.minus(StepNode.divide(pi, 6)), new StepConstant(0), StepNode.divide(pi, 6),
				StepNode.divide(pi, 4), StepNode.divide(pi, 3), StepNode.divide(pi, 2) };
		StepNode[] valuesCos = new StepNode[] { pi, StepNode.divide(StepNode.multiply(5, pi), 6),
				StepNode.divide(StepNode.multiply(3, pi), 4), StepNode.divide(StepNode.multiply(2, pi), 3), StepNode.divide(pi, 2),
				StepNode.divide(pi, 3), StepNode.divide(pi, 4), StepNode.divide(pi, 6), new StepConstant(0) };

		String currentArgument = so.getSubTree(0).toString();
		for (int i = 0; i < arguments.length; i++) {
			if (currentArgument.equals(arguments[i])) {
				if (so.isOperation(Operation.ARCSIN)) {
					return valuesSinTan[i];
				} else if (so.isOperation(Operation.ARCCOS)) {
					return valuesCos[i];
				}
			} else if (currentArgument.equals(argumentsTan[i])) {
				if (so.isOperation(Operation.ARCTAN)) {
					return valuesSinTan[i];
				}
			}
		}

		return null;
	}

	@Override
	public StepNode getIntegerCoefficient() {
		switch (operation) {
		case MINUS:
			StepNode sm = getSubTree(0).getIntegerCoefficient();
			if (sm == null) {
				return new StepConstant(-1);
			}
			return StepNode.minus(sm);
		case MULTIPLY:
			StepNode sp = null;
			for (int i = 0; i < noOfOperands(); i++) {
				sp = StepNode.multiply(sp, getSubTree(i).getIntegerCoefficient());
			}
			return sp;
		case DIVIDE:
			return StepNode.divide(getSubTree(0).getIntegerCoefficient(), getSubTree(1).getIntegerCoefficient());
		}
		return null;
	}

	@Override
	public StepNode getNonInteger() {
		switch (operation) {
		case MINUS:
			return getSubTree(0).getNonInteger();
		case MULTIPLY:
			StepNode sp = null;
			for (int i = 0; i < noOfOperands(); i++) {
				sp = StepNode.multiply(sp, getSubTree(i).getNonInteger());
			}
			return sp;
		case DIVIDE:
			return StepNode.divide(getSubTree(0).getNonInteger(), getSubTree(1).getNonInteger());
		}
		return this;
	}

	@Override
	public StepNode expand(Boolean[] changed) {
		if (isOperation(Operation.ABS)) {
			return this;
		}

		for (int i = 0; i < noOfOperands(); i++) {
			subtrees.set(i, distributeMinus(getSubTree(i).expand(changed), changed));
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

				return so.regroup().expand(changed);
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

			changed[0] = true;

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

				if (n <= 1) {
					return this;
				}

				changed[0] = true;

				StepNode sn = null;

				for (int i = 0; i < n; i++) {
					sn = StepNode.multiply(sn, getSubTree(0));
				}

				return sn.expand(changed);
			}
		}
		return this;
	}

	@Override
	public StepNode simplify() {
		return regroup().expand(new Boolean[1]).regroup();
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

	public boolean isTrigonometric() {
		return operation == Operation.SIN || operation == Operation.COS || operation == Operation.TAN || operation == Operation.CSC
				|| operation == Operation.SEC || operation == Operation.CSC;
	}

	public boolean isInverseTrigonometric() {
		return operation == Operation.ARCSIN || operation == Operation.ARCCOS || operation == Operation.ARCTAN;
	}

	public static Operation getInverse(Operation op) {
		switch (op) {
		case SIN:
			return Operation.ARCSIN;
		case COS:
			return Operation.ARCCOS;
		case TAN:
			return Operation.ARCTAN;
		default:
			return Operation.NO_OPERATION;
		}
	}

	public static boolean closeToAnInteger(double d) {
		return Math.abs(Math.round(d) - d) < 0.0000001;
	}

	public static boolean isEqual(double a, double b) {
		return Math.abs(a - b) < 0.0000001;
	}

	private static boolean isEven(double d) {
		return isEqual(Math.floor(d / 2) * 2, d);
	}
}
