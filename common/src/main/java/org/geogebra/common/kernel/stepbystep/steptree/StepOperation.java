package org.geogebra.common.kernel.stepbystep.steptree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.main.Localization;
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
		if(sn.isOperation()) {
			StepOperation copyOfThis = (StepOperation) this.deepCopy();
			copyOfThis.sort();
			StepOperation copyOfThat = (StepOperation) sn.deepCopy();
			copyOfThat.sort();
			
			return copyOfThis.exactEquals(copyOfThat);
		}
		
		return exactEquals(sn);
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
				constant = multiply(constant, getSubTree(i).getCoefficient());
			}
			return constant;
		} else if (isOperation(Operation.MINUS)) {
			StepNode coeff = getSubTree(0).getCoefficient();
			return coeff == null ? new StepConstant(-1) : minus(coeff);
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
				variable = multiply(variable, getSubTree(i).getVariable());
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
		case EQUAL_BOOLEAN:
			return subtrees.get(0).toString() + " = " + subtrees.get(1).toString();
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
			if (subtrees.get(0).getPriority() == 1) {
				return "pm(" + subtrees.get(0).toString() + ")";
			}
			return "pm" + subtrees.get(0).toString();
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
			return "\\fgcolor{" + getColorHex() + "}{" + toLaTeXString(loc, false) + "}";
		}

		switch (operation) {
		case EQUAL_BOOLEAN:
			return subtrees.get(0).toLaTeXString(loc, colored) + " = " + subtrees.get(1).toLaTeXString(loc, colored);
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

				boolean parantheses = subtrees.get(i).getPriority() < getPriority()
						|| (subtrees.get(i).nonSpecialConstant() && subtrees.get(i).getValue() < 0 && i != 0);

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
			if (isEqual(subtrees.get(1).getValue(), 2)) {
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
			return "\\" + operation.toString().toLowerCase() + "\\left(" + subtrees.get(0).toLaTeXString(loc, colored) + "\\right)";
		}
		return "";
	}

	private static boolean requiresPlus(StepNode a) {
		return !(a instanceof StepConstant && a.getValue() < 0) && !a.isOperation(Operation.MINUS)
				&& !a.isOperation(Operation.PLUSMINUS)
				&& (!a.isOperation(Operation.MULTIPLY) || requiresPlus(((StepOperation) a).getSubTree(0)));
	}

	private static boolean requiresDot(StepNode a, StepNode b) {
		if (a.nonSpecialConstant() && b.nonSpecialConstant()) {
			return true;
		}

		if (a instanceof StepVariable && b.nonSpecialConstant()) {
			return true;
		}

		if (a instanceof StepVariable && a.equals(b)) {
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
		so.setColor(color);
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
		return regroup(null);
	}

	@Override
	public StepNode regroup(SolutionBuilder sb) {
		StepNode origSn = this, newSn;

		Localization loc = sb == null ? null : sb.getLocalization();

		SolutionBuilder changes = new SolutionBuilder(loc);
		int[] colorTracker = new int[] { 1 };
		
		boolean changedInCycle = true;
		while (changedInCycle) {
			changedInCycle = false;

			newSn = calculateInverseTrigo(origSn, changes, colorTracker);
			changedInCycle |= regroupStep(origSn, newSn, changes, sb, colorTracker);
			origSn = newSn;

			newSn = trivialPowers(origSn, changes, colorTracker);
			changedInCycle |= regroupStep(origSn, newSn, changes, sb, colorTracker);
			origSn = newSn;

			newSn = distributeMinus(origSn, changes, colorTracker);
			changedInCycle |= regroupStep(origSn, newSn, changes, sb, colorTracker);
			origSn = newSn;

			newSn = doubleMinus(origSn, changes, colorTracker);
			changedInCycle |= regroupStep(origSn, newSn, changes, sb, colorTracker);
			origSn = newSn;

			newSn = eliminateOpposites(origSn, changes, colorTracker);
			changedInCycle |= regroupStep(origSn, newSn, changes, sb, colorTracker);
			origSn = newSn;

			newSn = factorSquare(origSn, changes, colorTracker);
			changedInCycle |= regroupStep(origSn, newSn, changes, sb, colorTracker);
			origSn = newSn;

			newSn = simplifyFractions(origSn, changes, colorTracker);
			changedInCycle |= regroupStep(origSn, newSn, changes, sb, colorTracker);
			origSn = newSn;

			newSn = expandFractions(origSn, changes, colorTracker);
			changedInCycle |= regroupStep(origSn, newSn, changes, sb, colorTracker);
			origSn = newSn;

			newSn = commonFraction(origSn, changes, colorTracker);
			changedInCycle |= regroupStep(origSn, newSn, changes, sb, colorTracker);
			origSn = newSn;

			newSn = regroupProducts(origSn, changes, colorTracker);
			changedInCycle |= regroupStep(origSn, newSn, changes, sb, colorTracker);
			origSn = newSn;

			newSn = addFractions(origSn, changes, colorTracker);
			changedInCycle |= regroupStep(origSn, newSn, changes, sb, colorTracker);
			origSn = newSn;

			newSn = regroupSums(origSn, changes, colorTracker, false);
			changedInCycle |= regroupStep(origSn, newSn, changes, sb, colorTracker);
			origSn = newSn;

			newSn = regroupSums(origSn, changes, colorTracker, true);
			changedInCycle |= regroupStep(origSn, newSn, changes, sb, colorTracker);
			origSn = newSn;

			newSn = squaringMinuses(origSn, changes, colorTracker);
			changedInCycle |= regroupStep(origSn, newSn, changes, sb, colorTracker);
			origSn = newSn;

			newSn = sameRootAsPower(origSn, changes, colorTracker);
			changedInCycle |= regroupStep(origSn, newSn, changes, sb, colorTracker);
			origSn = newSn;

			newSn = nicerFractions(origSn, changes, colorTracker);
			changedInCycle |= regroupStep(origSn, newSn, changes, sb, colorTracker);
			origSn = newSn;
		}

		return origSn;
	}

	@Override
	public StepNode expand(SolutionBuilder sb) {
		StepNode origSn = this, newSn;

		Localization loc = sb == null ? null : sb.getLocalization();

		SolutionBuilder changes = new SolutionBuilder(loc);
		int[] colorTracker = new int[] { 1 };
		
		Log.error("New expand started");

		boolean changedInCycle = true;
		while (changedInCycle) {
			changedInCycle = false;

			Log.error("New expand cycle started" + origSn);
			origSn = origSn.regroup(sb);

			newSn = expandPowers(origSn, changes, colorTracker);
			changedInCycle |= regroupStep(origSn, newSn, changes, sb, colorTracker);
			origSn = newSn;

			newSn = expandProducts(origSn, changes, colorTracker);
			changedInCycle |= regroupStep(origSn, newSn, changes, sb, colorTracker);
			origSn = newSn;
		}

		return origSn;
	}

	private static boolean regroupStep(StepNode origSn, StepNode newSn, SolutionBuilder changes, SolutionBuilder sb, int[] colorTracker) {
		final boolean printDebug = false;

		if (printDebug) {
			Log.error(": " + origSn);
		}

		if (colorTracker[0] > 1) {
			if (sb != null) {
				sb.add(SolutionStepType.SUBSTEP_WRAPPER);
				sb.levelDown();
				sb.add(SolutionStepType.EQUATION, origSn.deepCopy());
				sb.addAll(changes.getSteps());
				sb.add(SolutionStepType.EQUATION, newSn.deepCopy());
				sb.levelUp();
			}

			newSn.cleanColors();
			changes.reset();
			colorTracker[0] = 1;
			return true;
		}

		return false;
	}

	private static StepNode expandProducts(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.MULTIPLY)) {
				StepNode firstMultiplicand = null;
				StepOperation secondMultiplicand = null; // must be a sum
				StepNode remaining = null;

				for (int i = 0; i < so.noOfOperands(); i++) {
					if (firstMultiplicand == null && (secondMultiplicand != null || !so.getSubTree(i).isOperation(Operation.PLUS))) {
						firstMultiplicand = so.getSubTree(i);
					} else if (secondMultiplicand == null && so.getSubTree(i).isOperation(Operation.PLUS)) {
						secondMultiplicand = (StepOperation) so.getSubTree(i);
					} else {
						remaining = multiply(remaining, so.getSubTree(i));
					}
				}

				if (firstMultiplicand != null && secondMultiplicand != null) {
					StepOperation product = new StepOperation(Operation.PLUS);

					if (firstMultiplicand.isOperation(Operation.PLUS)
							&& StepHelper.countOperation(secondMultiplicand, Operation.DIVIDE) == 0) {
						StepOperation firstMultiplicandS = (StepOperation) firstMultiplicand;

						for (int i = 0; i < firstMultiplicandS.noOfOperands(); i++) {
							firstMultiplicandS.getSubTree(i).setColor(colorTracker[0]++);
						}
						for (int i = 0; i < secondMultiplicand.noOfOperands(); i++) {
							secondMultiplicand.getSubTree(i).setColor(colorTracker[0]++);
						}

						for (int i = 0; i < firstMultiplicandS.noOfOperands(); i++) {
							for (int j = 0; j < secondMultiplicand.noOfOperands(); j++) {
								product.addSubTree(multiply(firstMultiplicandS.getSubTree(i), secondMultiplicand.getSubTree(j)));
							}
						}

						sb.add(SolutionStepType.EXPAND_SUM_TIMES_SUM);
					} else {
						firstMultiplicand.setColor(colorTracker[0]++);
						for (int i = 0; i < secondMultiplicand.noOfOperands(); i++) {
							secondMultiplicand.getSubTree(i).setColor(colorTracker[0]++);
						}
						
						for (int i = 0; i < secondMultiplicand.noOfOperands(); i++) {
							product.addSubTree(multiply(firstMultiplicand, secondMultiplicand.getSubTree(i)));
						}
						sb.add(SolutionStepType.EXPAND_SIMPLE_TIMES_SUM, firstMultiplicand);
					}

					return multiply(product, remaining);
				}
			}

			StepOperation toReturn = new StepOperation(so.getOperation());
			for (int i = 0; i < so.noOfOperands(); i++) {
				if (so.getSubTree(i).isOperation(Operation.ABS)) {
					toReturn.addSubTree(so.getSubTree(i));
				} else {
					toReturn.addSubTree(expandProducts(so.getSubTree(i), sb, colorTracker));
				}
			}
			return toReturn;
		}

		return sn;
	}

	private static StepNode expandPowers(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.POWER) && so.getSubTree(0).isOperation(Operation.PLUS)) {
				StepOperation sum = (StepOperation) so.getSubTree(0);

				if (so.getSubTree(1).getValue() > 0 && closeToAnInteger(so.getSubTree(1))) {
					if (so.getSubTree(1).getValue() + sum.noOfOperands() < 6) {
						for (int i = 0; i < sum.noOfOperands(); i++) {
							sum.getSubTree(i).setColor(colorTracker[0]++);
						}

						StepOperation newSum = new StepOperation(Operation.PLUS);

						if (isEqual(so.getSubTree(1).getValue(), 2)) {
							if (sum.noOfOperands() == 2) {
								newSum.addSubTree(power(sum.getSubTree(0), 2));
								newSum.addSubTree(multiply(2, multiply(sum.getSubTree(0), sum.getSubTree(1))));
								newSum.addSubTree(power(sum.getSubTree(1), 2));

								sb.add(SolutionStepType.BINOM_SQUARED);
							} else if (sum.noOfOperands() == 3) {
								newSum.addSubTree(power(sum.getSubTree(0), 2));
								newSum.addSubTree(power(sum.getSubTree(1), 2));
								newSum.addSubTree(power(sum.getSubTree(2), 2));
								newSum.addSubTree(multiply(2, multiply(sum.getSubTree(0), sum.getSubTree(1))));
								newSum.addSubTree(multiply(2, multiply(sum.getSubTree(1), sum.getSubTree(2))));
								newSum.addSubTree(multiply(2, multiply(sum.getSubTree(0), sum.getSubTree(2))));

								sb.add(SolutionStepType.TRINOM_SQUARED);
							}
						} else if (isEqual(so.getSubTree(1).getValue(), 3)) {
							if (sum.noOfOperands() == 2) {
								newSum.addSubTree(power(sum.getSubTree(0), 3));
								newSum.addSubTree(multiply(3, multiply(power(sum.getSubTree(0), 2), sum.getSubTree(1))));
								newSum.addSubTree(multiply(3, multiply(sum.getSubTree(0), power(sum.getSubTree(1), 2))));
								newSum.addSubTree(power(sum.getSubTree(1), 3));

								sb.add(SolutionStepType.BINOM_CUBED);
							}
						}

						return newSum;
					}

					StepOperation asMultiplication = new StepOperation(Operation.MULTIPLY);
					for (int i = 0; i < Math.round(so.getSubTree(1).getValue()); i++) {
						asMultiplication.addSubTree(sum.deepCopy());
					}
					return asMultiplication;
				}
			}

			StepOperation toReturn = new StepOperation(so.getOperation());
			for (int i = 0; i < so.noOfOperands(); i++) {
				if (so.getSubTree(i).isOperation(Operation.ABS)) {
					toReturn.addSubTree(so.getSubTree(i));
				} else {
					toReturn.addSubTree(expandPowers(so.getSubTree(i), sb, colorTracker));
				}
			}
			return toReturn;
		}

		return sn;
	}

	private static StepNode eliminateOpposites(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
		if (sn.isOperation()) {
			StepOperation so = new StepOperation(((StepOperation) sn).getOperation());

			Integer colorsAtStart = colorTracker[0];
			for (int i = 0; i < ((StepOperation) sn).noOfOperands(); i++) {
				so.addSubTree(eliminateOpposites(((StepOperation) sn).getSubTree(i), sb, colorTracker));
			}

			if (colorsAtStart < colorTracker[0]) {
				return so;
			}

			so = (StepOperation) sn;

			if (so.isOperation(Operation.PLUS)) {
				StepNode[] coefficients = new StepNode[so.noOfOperands()];
				StepNode[] variables = new StepNode[so.noOfOperands()];
				for (int i = 0; i < so.noOfOperands(); i++) {
					coefficients[i] = so.getSubTree(i).getIntegerCoefficient();
					variables[i] = so.getSubTree(i).getNonInteger();

					if (coefficients[i] == null) {
						coefficients[i] = new StepConstant(1);
					}
					if (variables[i] == null) {
						variables[i] = new StepConstant(1);
					}
				}

				for (int i = 0; i < so.noOfOperands(); i++) {
					if (!isEqual(variables[i].getValue(), 0)) {
						for (int j = i + 1; j < so.noOfOperands(); j++) {
							if (!isEqual(variables[j].getValue(), 0)
									&& isEqual(-coefficients[i].getValue(), coefficients[j].getValue())
									&& variables[i].equals(variables[j])) {
								so.getSubTree(i).setColor(colorTracker[0]);
								so.getSubTree(j).setColor(colorTracker[0]);

								variables[i] = new StepConstant(0);
								variables[j] = new StepConstant(0);

								sb.add(SolutionStepType.ELIMINATE_OPPOSITES, colorTracker[0]++);
								break;
							}
						}
					} 

					// if (isEqual(coefficients[i].getValue(), 0)) {
					// so.getSubTree(i).setColor(colorTracker[0]);
					// sb.add(SolutionStepType.ZERO_IN_ADDITION, colorTracker[0]++);
					// }
				}

				StepOperation newSum = new StepOperation(Operation.PLUS);

				for (int i = 0; i < so.noOfOperands(); i++) {
					if (coefficients[i].getValue() != 0 && variables[i].getValue() != 0) {
						if (coefficients[i].nonSpecialConstant() && isEqual(coefficients[i].getValue(), 1)) {
							newSum.addSubTree(variables[i]);
						} else if (variables[i].nonSpecialConstant() && isEqual(variables[i].getValue(), 1)) {
							newSum.addSubTree(coefficients[i]);
						} else if (coefficients[i].nonSpecialConstant() && isEqual(coefficients[i].getValue(), -1)) {
							newSum.addSubTree(minus(variables[i]));
						} else {
							newSum.addSubTree(multiply(coefficients[i], variables[i]));
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

	private static StepNode doubleMinus(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.MINUS)) {
				if (so.getSubTree(0).isOperation(Operation.MINUS)) {
					StepNode result = ((StepOperation) so.getSubTree(0)).getSubTree(0);
					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.DOUBLE_MINUS, colorTracker[0]++);

					return result;
				}
			}

			StepOperation toReturn = new StepOperation(so.getOperation());
			for (int i = 0; i < so.noOfOperands(); i++) {
				toReturn.addSubTree(doubleMinus(so.getSubTree(i), sb, colorTracker));
			}
			return toReturn;
		}

		return sn;
	}

	private static StepNode nicerFractions(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.NROOT)) {
				if (so.getSubTree(0).isOperation(Operation.DIVIDE)) {
					StepNode nominator = root(((StepOperation) so.getSubTree(0)).getSubTree(0), so.getSubTree(1));
					StepNode denominator = root(((StepOperation) so.getSubTree(0)).getSubTree(1), so.getSubTree(1));

					StepNode result = divide(nominator, denominator);
					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.DISTRIBUTE_ROOT_FRAC, colorTracker[0]++);
							
					return result;
				}
			} else if (so.isOperation(Operation.DIVIDE)) {
				if (so.getSubTree(1).isOperation(Operation.NROOT)) {
					double root = ((StepOperation) so.getSubTree(1)).getSubTree(1).getValue();
					
					if (closeToAnInteger(root)) {
						StepNode nominator = multiply(so.getSubTree(0),
								root(nonTrivialPower(((StepOperation) so.getSubTree(1)).getSubTree(0), root - 1), root));
						StepNode denominator = ((StepOperation) so.getSubTree(1)).getSubTree(0);

						StepNode result = divide(nominator, denominator);
						so.setColor(colorTracker[0]);
						result.setColor(colorTracker[0]);
						sb.add(SolutionStepType.RATIONALIZE_DENOMINATOR, colorTracker[0]++);
						
						return result;
					}
				}
			}

			StepOperation toReturn = new StepOperation(so.getOperation());
			for (int i = 0; i < so.noOfOperands(); i++) {
				toReturn.addSubTree(nicerFractions(so.getSubTree(i), sb, colorTracker));
			}
			return toReturn;
		}

		return sn;
	}

	private static StepNode distributeMinus(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;
			
			if (so.isOperation(Operation.MULTIPLY) && isEqual(so.getSubTree(0).getValue(), -1)) {
				if (so.noOfOperands() == 2 && so.getSubTree(1).isOperation(Operation.PLUS)) {
					so = (StepOperation) minus(so.getSubTree(1));
				}
			}
			
			if (so.isOperation(Operation.MINUS)) {
				if (so.getSubTree(0).isOperation(Operation.PLUS)) {
					StepOperation result = new StepOperation(Operation.PLUS);
					for (int i = 0; i < ((StepOperation) so.getSubTree(0)).noOfOperands(); i++) {
						result.addSubTree(negate(((StepOperation) so.getSubTree(0)).getSubTree(i)));
					}
					
					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.DISTRIBUTE_MINUS, colorTracker[0]++);
					
					return result;
				}
			}

			StepOperation toReturn = new StepOperation(so.getOperation());
			for (int i = 0; i < so.noOfOperands(); i++) {
				toReturn.addSubTree(distributeMinus(so.getSubTree(i), sb, colorTracker));
			}
			return toReturn;
		}

		return sn;
	}

	private static StepNode factorSquare(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.NROOT)) {
				StepNode coefficient = so.getSubTree(0).getIntegerCoefficient();
				StepNode remainder = so.getSubTree(0).getNonInteger();

				if (coefficient != null && coefficient.nonSpecialConstant() && closeToAnInteger(coefficient.getValue())) {
					double root = so.getSubTree(1).getValue();
					double newCoefficient = highestNthPower(coefficient.getValue(), so.getSubTree(1).getValue());

					if (!isEqual(newCoefficient, 1)) {
						StepNode result = multiply(newCoefficient,
								root(multiply(coefficient.getValue() / Math.pow(newCoefficient, root), remainder), so.getSubTree(1)));

						so.setColor(colorTracker[0]);
						result.setColor(colorTracker[0]);

						sb.add(SolutionStepType.FACTOR_SQUARE, colorTracker[0]++);

						return result;
					}
				}
			}

			StepOperation toReturn = new StepOperation(so.getOperation());
			for (int i = 0; i < so.noOfOperands(); i++) {
				toReturn.addSubTree(factorSquare(so.getSubTree(i), sb, colorTracker));
			}

			return toReturn;
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

	private static StepNode regroupSums(StepNode sn, SolutionBuilder sb, int[] colorTracker, boolean integer) {
		if (sn.isOperation()) {
			StepOperation so = new StepOperation(((StepOperation) sn).getOperation());

			Integer colorsAtStart = colorTracker[0];
			for (int i = 0; i < ((StepOperation) sn).noOfOperands(); i++) {
				so.addSubTree(regroupSums(((StepOperation) sn).getSubTree(i), sb, colorTracker, integer));
			}

			if (colorsAtStart < colorTracker[0]) {
				return so;
			}

			so = (StepOperation) sn;

			if (so.isOperation(Operation.PLUS)) {
				StepNode[] coefficients = new StepNode[so.noOfOperands()];
				StepNode[] variables = new StepNode[so.noOfOperands()];
				for (int i = 0; i < so.noOfOperands(); i++) {
					if (integer) {
						coefficients[i] = so.getSubTree(i).getIntegerCoefficient();
						variables[i] = so.getSubTree(i).getNonInteger();
					} else {
						coefficients[i] = so.getSubTree(i).getCoefficient();
						variables[i] = so.getSubTree(i).getVariable();
					}

					if (coefficients[i] == null) {
						coefficients[i] = new StepConstant(1);
					}
					if (variables[i] == null) {
						variables[i] = new StepConstant(1);
					}
				}

				List<StepNode> constantList = new ArrayList<StepNode>();
				double constantSum = 0;
				for (int i = 0; i < so.noOfOperands(); i++) {
					if (coefficients[i].nonSpecialConstant() && isEqual(variables[i].getValue(), 1)) {
						constantList.add(coefficients[i]);
						constantSum += coefficients[i].getValue();
						coefficients[i] = new StepConstant(0);
					}
				}

				for (int i = 0; i < so.noOfOperands(); i++) {
					if ((integer || !variables[i].isConstant()) && !isEqual(coefficients[i].getValue(), 0)) {
						boolean foundCommon = false;
						for (int j = i + 1; j < so.noOfOperands(); j++) {
							if (!isEqual(coefficients[j].getValue(), 0) && !isEqual(variables[i].getValue(), 1)
									&& variables[i].equals(variables[j])) {
								foundCommon = true;
								so.getSubTree(j).setColor(colorTracker[0]);
								coefficients[i] = add(coefficients[i], coefficients[j]);
								coefficients[j] = new StepConstant(0);
							}
						}
						if (foundCommon) {
							so.getSubTree(i).setColor(colorTracker[0]);
							coefficients[i].setColor(colorTracker[0]);
							variables[i].setColor(colorTracker[0]);
							sb.add(SolutionStepType.COLLECT_LIKE_TERMS, variables[i]);
							colorTracker[0]++;
						}
					}
				}

				StepOperation newSum = new StepOperation(Operation.PLUS);

				for (int i = 0; i < so.noOfOperands(); i++) {
					if (!isEqual(coefficients[i].getValue(), 0) && !isEqual(variables[i].getValue(), 0)) {
						if (coefficients[i].nonSpecialConstant() && isEqual(coefficients[i].getValue(), 1)) {
							newSum.addSubTree(variables[i]);
						} else if (variables[i].nonSpecialConstant() && isEqual(variables[i].getValue(), 1)) {
							newSum.addSubTree(coefficients[i]);
						} else if (coefficients[i].nonSpecialConstant() && isEqual(coefficients[i].getValue(), -1)) {
							newSum.addSubTree(minus(variables[i]));
						} else {
							newSum.addSubTree(multiply(coefficients[i], variables[i]));
						}
					}
				}

				StepNode newConstants = new StepConstant(constantSum);
				if (constantList.size() > 1) {
					for (int i = 0; i < constantList.size(); i++) {
						constantList.get(i).setColor(colorTracker[0]);
					}
					sb.add(SolutionStepType.ADD_CONSTANTS, colorTracker[0]);
					newConstants.setColor(colorTracker[0]);
					colorTracker[0]++;
				}

				if (isEqual(constantSum, 0) && constantList.size() == 1) {
					constantList.get(0).setColor(colorTracker[0]);
					sb.add(SolutionStepType.ZERO_IN_ADDITION, colorTracker[0]++);
				}

				if (!isEqual(constantSum, 0)) {
					newSum.addSubTree(newConstants);
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

	private static StepNode expandFractions(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.PLUS)) {
				StepOperation newSum = new StepOperation(Operation.PLUS);

				long newDenominator = 1;

				for (int i = 0; i < so.noOfOperands(); i++) {
					long currentDenominator = getDenominator(so.getSubTree(i));
					if (currentDenominator != 0) {
						newDenominator = lcm(newDenominator, currentDenominator);
					}
				}

				if (newDenominator != 1) {
					boolean wasChanged = false;

					for (int i = 0; i < so.noOfOperands(); i++) {
						long currentDenominator = getDenominator(so.getSubTree(i));
						if (currentDenominator != 0 && currentDenominator != newDenominator) {
							wasChanged = true;

							StepNode newFraction = divide(
									nonTrivialProduct(new StepConstant(((double) newDenominator) / currentDenominator),
											getNominator(so.getSubTree(i))), newDenominator);

							newFraction.setColor(colorTracker[0]);
							so.getSubTree(i).setColor(colorTracker[0]);

							newSum.addSubTree(newFraction);
						} else {
							newSum.addSubTree(so.getSubTree(i));
						}
					}

					if (wasChanged) {
						StepConstant denominatorNode = new StepConstant(newDenominator);
						denominatorNode.setColor(colorTracker[0]++);
						sb.add(SolutionStepType.EXPAND_FRACTIONS, denominatorNode);

						return newSum;
					}
				}
			}

			StepOperation toReturn = new StepOperation(so.getOperation());
			for (int i = 0; i < so.noOfOperands(); i++) {
				toReturn.addSubTree(expandFractions(so.getSubTree(i), sb, colorTracker));
			}

			return toReturn;
		}

		return sn;
	}

	private static StepNode addFractions(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.PLUS)) {
				StepNode remainder = null;
				StepNode newNominator = null;
				long newDenominator = 0;

				List<StepNode> fractions = new ArrayList<StepNode>();
				for (int i = 0; i < so.noOfOperands(); i++) {
					StepNode currentNominator = getNominator(so.getSubTree(i));
					long currentDenominator = getDenominator(so.getSubTree(i));

					if (newDenominator == 0 && currentDenominator != 0 && currentDenominator != 1) {
						newDenominator = currentDenominator;
					}

					if (currentDenominator != 0 && currentDenominator == newDenominator) {
						newNominator = add(newNominator, currentNominator);
						fractions.add(so.getSubTree(i));
					} else {
						remainder = add(remainder, so.getSubTree(i));
					}
				}

				if (fractions.size() > 1) {
					for (int i = 0; i < fractions.size(); i++) {
						fractions.get(i).setColor(colorTracker[0]);
					}

					StepNode result = divide(newNominator, newDenominator);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.ADD_FRACTIONS, colorTracker[0]++);
					return add(remainder, result);
				}
			}

			StepOperation toReturn = new StepOperation(so.getOperation());
			for (int i = 0; i < so.noOfOperands(); i++) {
				toReturn.addSubTree(addFractions(so.getSubTree(i), sb, colorTracker));
			}

			return toReturn;
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
			return minus(getNominator(((StepOperation) sn).getSubTree(0)));
		} else if (sn.isOperation(Operation.DIVIDE)) {
			return ((StepOperation) sn).getSubTree(0);
		}
		return null;
	}

	private static StepNode simplifyFractions(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
		if(sn.isOperation()) {
			StepOperation so = (StepOperation) sn;
			
			if (so.isOperation(Operation.MULTIPLY) || so.isOperation(Operation.DIVIDE)) {

				List<StepNode> bases = new ArrayList<StepNode>();
				List<StepNode> exponents = new ArrayList<StepNode>();

				getBasesAndExponents(so, null, bases, exponents);

				for (int i = 0; i < bases.size(); i++) {
					for (int j = i + 1; j < bases.size(); j++) {
						if ((exponents.get(i).getValue() * exponents.get(j).getValue()) < 0 && bases.get(i).equals(bases.get(j))
								&& !isEqual(bases.get(i).getValue(), 1)) {
							bases.get(i).setColor(colorTracker[0]);
							bases.get(j).setColor(colorTracker[0]);

							double min = Math.min(Math.abs(exponents.get(i).getValue()), Math.abs(exponents.get(j).getValue()));

							exponents.get(i).setColor(colorTracker[0]);
							exponents.get(j).setColor(colorTracker[0]);

							double newExponent1 = exponents.get(i).getValue() > 0 ? exponents.get(i).getValue() - min
									: exponents.get(i).getValue() + min;
							double newExponent2 = exponents.get(j).getValue() > 0 ? exponents.get(j).getValue() - min
									: exponents.get(j).getValue() + min;

							exponents.set(i, new StepConstant(newExponent1));
							exponents.set(j, new StepConstant(newExponent2));

							exponents.get(i).setColor(colorTracker[0]);
							exponents.get(j).setColor(colorTracker[0]);

							StepNode toCancel = nonTrivialPower(bases.get(i), min);
							toCancel.setColor(colorTracker[0]++);
							sb.add(SolutionStepType.CANCEL_FRACTION, toCancel);

							break;
						}
						if (isEqual(exponents.get(i).getValue(), 1) && isEqual(exponents.get(j).getValue(), -1)
								&& closeToAnInteger(bases.get(i)) && closeToAnInteger(bases.get(j))) {
							long gcd = gcd(bases.get(i), bases.get(j));
							if (gcd > 1) {
								bases.get(i).setColor(colorTracker[0]);
								bases.get(j).setColor(colorTracker[0]);

								bases.set(i, new StepConstant(bases.get(i).getValue() / gcd));
								bases.set(j, new StepConstant(bases.get(j).getValue() / gcd));

								bases.get(i).setColor(colorTracker[0]);
								bases.get(j).setColor(colorTracker[0]);

								StepNode toCancel = new StepConstant(gcd);
								toCancel.setColor(colorTracker[0]++);
								sb.add(SolutionStepType.CANCEL_FRACTION, toCancel);

								break;
							}
						}
					}
				}

				StepNode newFraction = null;
				for (int i = 0; i < bases.size(); i++) {
					if (!isEqual(exponents.get(i).getValue(), 0) && !isEqual(bases.get(i).getValue(), 1)) {
						newFraction = makeFraction(newFraction, bases.get(i), exponents.get(i));
					}
				}

				return newFraction == null ? new StepConstant(1) : newFraction;
			}

			StepOperation toReturn = new StepOperation(so.getOperation());
			for (int i = 0; i < so.noOfOperands(); i++) {
				toReturn.addSubTree(simplifyFractions(so.getSubTree(i), sb, colorTracker));
			}
			return toReturn;
		}
		
		return sn;
	}

	private static StepNode commonFraction(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
		if (sn.isOperation()) {
			StepOperation so = new StepOperation(((StepOperation) sn).getOperation());

			Integer colorsAtStart = colorTracker[0];
			for (int i = 0; i < ((StepOperation) sn).noOfOperands(); i++) {
				so.addSubTree(commonFraction(((StepOperation) sn).getSubTree(i), sb, colorTracker));
			}

			if (colorsAtStart < colorTracker[0]) {
				return so;
			}

			so = (StepOperation) sn;

			if (so.isOperation(Operation.MULTIPLY)) {
				List<StepNode> bases = new ArrayList<StepNode>();
				List<StepNode> exponents = new ArrayList<StepNode>();

				getBasesAndExponents(so, null, bases, exponents);

				StepNode newFraction = null;

				for (int i = 0; i < bases.size(); i++) {
					newFraction = makeFraction(newFraction, bases.get(i), exponents.get(i));
				}

				if (newFraction.isOperation(Operation.DIVIDE)) {
					so.setColor(colorTracker[0]);
					newFraction.setColor(colorTracker[0]);
					sb.add(SolutionStepType.COMMON_FRACTION, colorTracker[0]++);

					return newFraction;
				}
			}

			return so;
		}

		return sn;
	}

	private static StepNode regroupProducts(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
		if (sn.isOperation()) {
			StepOperation so = new StepOperation(((StepOperation) sn).getOperation());

			Integer colorsAtStart = colorTracker[0];
			for (int i = 0; i < ((StepOperation) sn).noOfOperands(); i++) {
				so.addSubTree(regroupProducts(((StepOperation) sn).getSubTree(i), sb, colorTracker));
			}

			if (colorsAtStart < colorTracker[0]) {
				return so;
			}

			so = (StepOperation) sn;

			if (so.isOperation(Operation.MULTIPLY)) {
				List<StepNode> bases = new ArrayList<StepNode>();
				List<StepNode> exponents = new ArrayList<StepNode>();

				getBasesAndExponents(so, null, bases, exponents);

				List<StepNode> constantList = new ArrayList<StepNode>();
				double constantValue = 1;
				for (int i = 0; i < bases.size(); i++) {
					if (bases.get(i).nonSpecialConstant() && isEqual(exponents.get(i).getValue(), 1)) {
						constantList.add(bases.get(i));
						constantValue *= bases.get(i).getValue();

						exponents.set(i, new StepConstant(0));
					}
				}
				
				if (isEqual(constantValue, 0)) {
					so.setColor(colorTracker[0]);
					StepNode result = new StepConstant(0);
					result.setColor(colorTracker[0]);
					
					sb.add(SolutionStepType.MULTIPLIED_BY_ZERO, colorTracker[0]++);
					return result;
				}

				for (int i = 0; i < bases.size(); i++) {
					if (!isEqual(exponents.get(i).getValue(), 0)) {
						boolean foundCommon = false;
						for (int j = i + 1; j < bases.size(); j++) {
							if (!isEqual(exponents.get(j).getValue(), 0) && bases.get(i).equals(bases.get(j))) {
								foundCommon = true;
								bases.get(j).setColor(colorTracker[0]);

								exponents.set(i, add(exponents.get(i), exponents.get(j)));
								exponents.set(j, new StepConstant(0));
							}
						}
						if (foundCommon) {
							bases.get(i).setColor(colorTracker[0]);
							exponents.get(i).setColor(colorTracker[0]++);

							sb.add(SolutionStepType.REGROUP_PRODUCTS, bases.get(i));
						}
					}
				}

				StepNode newProduct = null;
				
				for (int i = 0; i < bases.size(); i++) {
					if (!isEqual(exponents.get(i).getValue(), 0) && !isEqual(bases.get(i).getValue(), 1)) {
						newProduct = makeFraction(newProduct, bases.get(i), exponents.get(i));
					}
				}

				StepNode newConstant;
				newConstant = new StepConstant(Math.abs(constantValue));
				if (constantList.size() > 1) {
					for (int i = 0; i < constantList.size(); i++) {
						constantList.get(i).setColor(colorTracker[0]);
					}
					sb.add(SolutionStepType.MULTIPLY_CONSTANTS, colorTracker[0]);
					if (newProduct == null) {
						newConstant = new StepConstant(constantValue);
						newConstant.setColor(colorTracker[0]++);
						return newConstant;
					}
					newConstant.setColor(colorTracker[0]++);
				}

				newProduct = makeFraction(newConstant, newProduct, new StepConstant(1));

				if (constantValue < 0) {
					return minus(newProduct);
				}
				return newProduct;
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
				getBasesAndExponents(so.getSubTree(1), multiply(-1, currentExp), bases, exponents);
				return;
			case POWER:
				bases.add(so.getSubTree(0));
				exponents.add(multiply(currentExp, so.getSubTree(1)));
				return;
			}
		}

		bases.add(sn);
		exponents.add(currentExp == null ? new StepConstant(1) : currentExp);
	}

	private static StepNode squaringMinuses(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.POWER)) {
				if (so.getSubTree(1).getValue() == 2) {
					if (so.getSubTree(0).isOperation(Operation.MINUS)) {
						StepNode result = power(((StepOperation) so.getSubTree(0)).getSubTree(0), 2);
						
						so.setColor(colorTracker[0]);
						result.setColor(colorTracker[0]);
						sb.add(SolutionStepType.SQUARE_MINUS, colorTracker[0]++);
						
						return result;
					}
				}
			}

			StepOperation toReturn = new StepOperation(so.getOperation());
			for (int i = 0; i < so.noOfOperands(); i++) {
				toReturn.addSubTree(squaringMinuses(so.getSubTree(i), sb, colorTracker));
			}
			return toReturn;
		}

		return sn;
	}

	private static StepNode sameRootAsPower(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if ((so.isOperation(Operation.POWER) && so.getSubTree(0).isOperation(Operation.NROOT)) || 
					(so.isOperation(Operation.NROOT) && so.getSubTree(0).isOperation(Operation.POWER))) {
				StepNode exponent1 = so.getSubTree(1);
				StepNode exponent2 = ((StepOperation) so.getSubTree(0)).getSubTree(1);

				if (closeToAnInteger(exponent1) && closeToAnInteger(exponent2)) {
					long gcd = gcd(Math.round(exponent1.getValue()), Math.round(exponent2.getValue()));
					
					if(gcd > 1) {
						exponent1 = isEqual(exponent1.getValue(), gcd) ? null : new StepConstant(exponent1.getValue() / gcd);
						exponent2 = isEqual(exponent2.getValue(), gcd) ? null : new StepConstant(exponent2.getValue() / gcd);
						
						StepConstant gcdConstant = new StepConstant(gcd);
						gcdConstant.setColor(colorTracker[0]);

						StepNode argument = ((StepOperation) so.getSubTree(0)).getSubTree(0);

						StepNode result;
						if (so.isOperation(Operation.NROOT) && so.getSubTree(0).isOperation(Operation.POWER)) {
							if (isEven(gcd) && (exponent2 == null || !isEven(exponent2.getValue()))) {
								if(argument.nonSpecialConstant()) {
									result = root(power(new StepConstant(Math.abs(argument.getValue())), exponent2), exponent1);
								} else {
									result = root(power(abs(argument), exponent2), exponent1);
								}
								sb.add(SolutionStepType.DIVIDE_ROOT_AND_POWER_EVEN, gcdConstant);
							} else {
								result = root(power(argument, exponent2), exponent1);
								sb.add(SolutionStepType.DIVIDE_ROOT_AND_POWER, gcdConstant);
							}
						} else {
							result = power(root(argument, exponent2), exponent1);
							sb.add(SolutionStepType.DIVIDE_ROOT_AND_POWER, gcdConstant);
						}

						so.setColor(colorTracker[0]);
						result.setColor(colorTracker[0]++);
						return result;						
					}

				}
			}

			StepOperation toReturn = new StepOperation(so.getOperation());
			for (int i = 0; i < so.noOfOperands(); i++) {
				toReturn.addSubTree(sameRootAsPower(so.getSubTree(i), sb, colorTracker));
			}
			return toReturn;
		}

		return sn;
	}

	private static StepNode trivialPowers(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.POWER)) {
				if (closeToAnInteger(so.getSubTree(0).getValue()) && closeToAnInteger(so.getSubTree(1).getValue())) {
					StepNode result = new StepConstant(Math.pow(so.getSubTree(0).getValue(), so.getSubTree(1).getValue()));

					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.EVALUATE_POWER, colorTracker[0]++);

					return result;
				}
				if (so.getSubTree(1).getValue() == 0) {
					StepNode result = new StepConstant(1);

					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.ZEROTH_POWER, colorTracker[0]++);

					return result;
				}
				if (so.getSubTree(1).getValue() == 1) {
					StepNode result = so.getSubTree(0);

					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.FIRST_POWER, colorTracker[0]++);

					return result;
				}
			} else if (so.isOperation(Operation.NROOT)) {
				if (so.getSubTree(1).getValue() == 1) {
					StepNode result = so.getSubTree(0);

					so.setColor(colorTracker[0]);
					result.setColor(colorTracker[0]);
					sb.add(SolutionStepType.FIRST_ROOT, colorTracker[0]++);

					return result;
				}

				if(closeToAnInteger(so.getSubTree(0).getValue()) && closeToAnInteger(so.getSubTree(1).getValue())) {
					long root = Math.round(so.getSubTree(1).getValue());
					long power = getIntegerPower(Math.round(so.getSubTree(0).getValue()));
					long gcd = gcd(root, power);
					
					if (gcd > 1) {
						StepNode newValue = power(new StepConstant(Math.pow(so.getSubTree(0).getValue(), ((double) 1) / gcd)), gcd);

						so.getSubTree(0).setColor(colorTracker[0]);
						newValue.setColor(colorTracker[0]++);

						StepNode result = root(newValue, root);
						sb.add(SolutionStepType.REWRITE_AS_POWER, so.getSubTree(0), newValue);
						
						return result;
					}
				}
			}

			StepOperation toReturn = new StepOperation(so.getOperation());
			for (int i = 0; i < so.noOfOperands(); i++) {
				toReturn.addSubTree(trivialPowers(so.getSubTree(i), sb, colorTracker));
			}
			return toReturn;
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

	private static StepNode calculateInverseTrigo(StepNode sn, SolutionBuilder sb, int[] colorTracker) {
		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isInverseTrigonometric()) {
				StepNode value = inverseTrigoLookup(so);
				if (value != null) {
					so.setColor(colorTracker[0]);
					value.setColor(colorTracker[0]);
					sb.add(SolutionStepType.EVALUATE_INVERSE_TRIGO, colorTracker[0]++);
					return value;
				}
			}

			StepOperation toReturn = new StepOperation(so.getOperation());
			for (int i = 0; i < so.noOfOperands(); i++) {
				toReturn.addSubTree(calculateInverseTrigo(so.getSubTree(i), sb, colorTracker));
			}
			return toReturn;
		}

		return sn;
	}

	private static StepNode inverseTrigoLookup(StepOperation so) {
		String[] arguments = new String[] { "-1", "-(nroot(3, 2))/(2)", "-(nroot(2, 2))/(2)", "-(1)/(2)", "0", "(1)/(2)",
				"(nroot(2, 2))/(2)", "(nroot(3, 2))/(2)", "1" };
		String[] argumentsTan = new String[] { "", "-nroot(3, 2)", "-1", "-nroot(3, 2)/3", "0", "nroot(3, 2)/3", "1", "nroot(3, 2)", "" };

		StepNode pi = new StepConstant(Math.PI);
		StepNode[] valuesSinTan = new StepNode[] { minus(divide(pi, 2)), minus(divide(pi, 3)), minus(divide(pi, 4)), minus(divide(pi, 6)),
				new StepConstant(0), divide(pi, 6), divide(pi, 4), divide(pi, 3), divide(pi, 2) };
		StepNode[] valuesCos = new StepNode[] { pi, divide(multiply(5, pi), 6), divide(multiply(3, pi), 4), divide(multiply(2, pi), 3),
				divide(pi, 2), divide(pi, 3), divide(pi, 4), divide(pi, 6), new StepConstant(0) };

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
			return minus(sm);
		case MULTIPLY:
			StepNode sp = null;
			for (int i = 0; i < noOfOperands(); i++) {
				sp = multiply(sp, getSubTree(i).getIntegerCoefficient());
			}
			return sp;
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
			StepNode sp = null;
			for (int i = 0; i < noOfOperands(); i++) {
				sp = multiply(sp, getSubTree(i).getNonInteger());
			}
			return sp;
		case DIVIDE:
			return divide(getSubTree(0).getNonInteger(), getSubTree(1).getNonInteger());
		}
		return this;
	}

	public void addSubTree(StepNode sn) {
		if (sn != null) {
			if (isOperation(Operation.PLUS) && sn.isOperation(Operation.PLUS)) {
				for (int i = 0; i < ((StepOperation) sn).noOfOperands(); i++) {
					if (((StepOperation) sn).getSubTree(i).color == 0) {
						((StepOperation) sn).getSubTree(i).setColor(sn.color);
					}
					addSubTree(((StepOperation) sn).getSubTree(i));
				}
			} else if (isOperation(Operation.MULTIPLY) && sn.isOperation(Operation.MULTIPLY)) {
				for (int i = 0; i < ((StepOperation) sn).noOfOperands(); i++) {
					if (((StepOperation) sn).getSubTree(i).color == 0) {
						((StepOperation) sn).getSubTree(i).setColor(sn.color);
					}
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
}
