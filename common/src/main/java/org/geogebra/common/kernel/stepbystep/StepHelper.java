package org.geogebra.common.kernel.stepbystep;

import java.util.ArrayList;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.kernel.stepbystep.steptree.StepConstant;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepOperation;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;
import org.geogebra.common.plugin.Operation;

public class StepHelper {

	public static StepNode getCommon(StepNode a, StepNode b) {
		if (a.isOperation(Operation.PLUS)) {
			StepOperation op = new StepOperation(Operation.PLUS);
			for (int i = 0; i < ((StepOperation) a).noOfOperands(); i++) {
				if (containsExactExpression(b, ((StepOperation) a).getSubTree(i))) {
					op.addSubTree(((StepOperation) a).getSubTree(i));
				}
			}
			if (op.noOfOperands() == 1) {
				return op.getSubTree(0);
			} else if (op.noOfOperands() > 1) {
				return op;
			}
		} else if (containsExactExpression(b, a)) {
			return a;
		}

		return null;
	}

	public static boolean containsExactExpression(StepNode sn, StepNode expr) {
		if (sn != null && sn.equals(expr)) {
			return true;
		}
		if (sn != null && sn.isOperation(Operation.PLUS)) {
			StepOperation so = (StepOperation) sn;
			for (int i = 0; i < so.noOfOperands(); i++) {
				if (containsExactExpression(so.getSubTree(i), expr)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param sn expression tree to traverse
	 * @param kernel GeoGebra kernel (used for CAS)
	 * @return lowest common denominator of the expression
	 */
	public static StepNode getDenominator(StepNode sn, Kernel kernel) {
		if (sn != null && sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.DIVIDE)) {
				return so.getSubTree(1);
			} else if (so.isOperation(Operation.MINUS)) {
				return getDenominator(so.getSubTree(0), kernel);
			} else if (so.isOperation(Operation.PLUS) || so.isOperation(Operation.MULTIPLY)) {
				StepNode denominator = null;
				for (int i = 0; i < so.noOfOperands(); i++) {
					StepNode newDenominator = getDenominator(so.getSubTree(i), kernel);
					if (newDenominator != null) {
						if (denominator == null) {
							denominator = newDenominator;
						} else if (StepOperation.closeToAnInteger(denominator.getValue())
								&& StepOperation.closeToAnInteger(newDenominator.getValue())) {
							long a = (long) denominator.getValue();
							long b = (long) newDenominator.getValue();
							denominator = new StepConstant((a * b) / StepNode.gcd(a, b));
						} else {
							denominator = LCM(denominator, newDenominator, kernel);
						}
					}
				}
				return denominator;
			}
		}
		return null;
	}

	/**
	 * @param sn expression tree to traverse
	 * @return sum of all the subexpressions containing square roots
	 */
	public static StepNode getSQRoots(StepNode sn) {
		if (sn != null && sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.NROOT)) {
				return so;
			} else if (so.isOperation(Operation.MULTIPLY) || so.isOperation(Operation.DIVIDE) || so.isOperation(Operation.MINUS)) {
				if (countOperation(so, Operation.NROOT) > 0) {
					return so;
				}
			} else if (so.isOperation(Operation.PLUS)) {
				StepNode roots = null;
				for (int i = 0; i < so.noOfOperands(); i++) {
					roots = StepNode.add(roots, getSQRoots(so.getSubTree(i)));
				}
				return roots;
			}
		}
		return null;
	}

	/**
	 * @param sn expression tree to traverse
	 * @return part of the expression tree, which doesn't contain roots
	 */
	public static StepNode getNonIrrational(StepNode sn) {
		return StepNode.subtract(sn, getSQRoots(sn)).regroup();
	}

	/**
	 * @param sn expression tree to traverse
	 * @return first subexpression containing square roots
	 */
	public static StepNode getOneSquareRoot(StepNode sn) {
		if (sn != null && sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.NROOT)) {
				return so;
			} else if (so.isOperation(Operation.MULTIPLY) || so.isOperation(Operation.DIVIDE)) {
				if (countOperation(so, Operation.NROOT) > 0) {
					return so;
				}
			} else if (so.isOperation(Operation.MINUS)) {
				if (countOperation(so, Operation.NROOT) > 0) {
					return StepNode.minus(so);
				}
			} else if (so.isOperation(Operation.PLUS)) {
				StepNode roots = null;
				for (int i = 0; i < so.noOfOperands(); i++) {
					roots = StepNode.add(roots, getSQRoots(so.getSubTree(i)));
					if (countOperation(so.getSubTree(i), Operation.NROOT) > 0) {
						return roots;
					}
				}
				return roots;
			}
		}
		return null;
	}

	/**
	 * @param sn expression tree to traverse
	 * @param expr expression to find
	 * @return all subexpression which contain expr
	 */
	public static StepNode findVariable(StepNode sn, StepNode expr) {
		if (sn != null && sn.equals(expr)) {
			return sn;
		}
		if (sn != null && sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.equals(expr)) {
				return so;
			} else if (so.isOperation(Operation.MULTIPLY) || so.isOperation(Operation.DIVIDE)) {
				for (int i = 0; i < so.noOfOperands(); i++) {
					if (so.getSubTree(i).equals(expr)) {
						return so;
					}
				}
			} else if (so.isOperation(Operation.MINUS)) {
				return StepNode.minus(findVariable(so.getSubTree(0), expr));
			} else if (so.isOperation(Operation.PLUS)) {
				StepNode found = null;
				for (int i = 0; i < so.noOfOperands(); i++) {
					found = StepNode.add(found, findVariable(so.getSubTree(i), expr));
				}
				return found;
			}
		}
		return null;
	}

	/**
	 * @param sn expression tree to traverse
	 * @param expr expression to find
	 * @return the coefficient of expr in the tree
	 */
	public static StepNode findCoefficient(StepNode sn, StepNode expr) {
		if (sn != null && sn.equals(expr)) {
			return new StepConstant(1);
		}
		if (sn != null && sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (!containsExpression(sn, expr)) {
				return null;
			}

			if (so.isOperation(Operation.DIVIDE)) {
				StepNode coeff;
				if (so.getSubTree(0).isConstant()) {
					coeff = so.getSubTree(0);
				} else {
					coeff = findCoefficient(so.getSubTree(0), expr);
				}
				if (so.getSubTree(1).isConstant()) {
					coeff = StepNode.divide(coeff, so.getSubTree(0));
				} else {
					coeff = StepNode.divide(coeff, findCoefficient(so.getSubTree(0), expr));
				}
				return coeff.regroup();
			} else if (so.isOperation(Operation.MULTIPLY)) {
				StepNode coeff = new StepConstant(1);
				for (int i = 0; i < so.noOfOperands(); i++) {
					if (so.getSubTree(i).isConstant()) {
						coeff = StepNode.multiply(coeff, so.getSubTree(i));
					} else {
						coeff = StepNode.multiply(coeff, findCoefficient(so.getSubTree(i), expr));
					}
				}
				return coeff.regroup();
			} else if (so.isOperation(Operation.MINUS)) {
				return StepNode.minus(findCoefficient(so.getSubTree(0), expr));
			} else if (so.isOperation(Operation.PLUS)) {
				StepNode found = null;
				for (int i = 0; i < so.noOfOperands(); i++) {
					found = StepNode.add(found, findCoefficient(so.getSubTree(i), expr));
				}
				return found == null ? null : found.regroup();
			}
		}
		return null;
	}

	/**
	 * @param sn expression tree to traverse
	 * @param expr expression to find
	 * @return whether sn contains expr
	 */
	private static boolean containsExpression(StepNode sn, StepNode expr) {
		if (sn != null && sn.equals(expr)) {
			return true;
		}
		if (sn != null && sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.POWER) || so.isOperation(Operation.NROOT)) {
				return expr.equals(so);
			}

			for (int i = 0; i < so.noOfOperands(); i++) {
				if (containsExpression(so.getSubTree(i), expr)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param sn expression tree to traverse
	 * @return subexpression which do not contain any variable part
	 */
	public static StepNode findConstant(StepNode sn) {
		if (sn != null && sn.isConstant()) {
			return sn;
		}
		if (sn != null && sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.MINUS)) {
				return StepNode.minus(findConstant(so.getSubTree(0)));
			} else if (so.isOperation(Operation.PLUS)) {
				StepNode found = null;
				for (int i = 0; i < so.noOfOperands(); i++) {
					found = StepNode.add(found, findConstant(so.getSubTree(i)));
				}
				return found;
			}
		}

		return null;
	}

	public static int getPower(StepNode sn) {
		if (sn != null && sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.POWER) && !so.getSubTree(0).isConstant()) {
				return (int) so.getSubTree(1).getValue();
			} else if (so.isOperation(Operation.MULTIPLY) || so.isOperation(Operation.DIVIDE) || so.isOperation(Operation.MINUS)) {
				for (int i = 0; i < so.noOfOperands(); i++) {
					int power = getPower(so.getSubTree(i));
					if (power > 0) {
						return power;
					}
				}
			}
		}

		return 0;
	}

	public static boolean isPower(StepNode sn) {
		if (sn != null && sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.POWER)) {
				return true;
			} else if (so.isOperation(Operation.MINUS) || so.isOperation(Operation.MULTIPLY) || so.isOperation(Operation.DIVIDE)) {
				for (int i = 0; i < so.noOfOperands(); i++) {
					if (!isPower(so.getSubTree(i))) {
						return false;
					}
				}
				return true;
			}
		}

		return sn != null && sn.isConstant();
	}

	public static void getAbsoluteValues(ArrayList<String> absoluteValues, StepNode sn) {
		if (sn != null && sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.ABS)) {
				absoluteValues.add(so.getSubTree(0).toString());
			} else {
				for (int i = 0; i < so.noOfOperands(); i++) {
					getAbsoluteValues(absoluteValues, so.getSubTree(i));
				}
			}
		}
	}

	public static boolean shouldTakeRoot(StepNode snRHS, StepNode snLHS) {
		StepNode sn = StepNode.subtract(snRHS, snLHS).regroup();

		StepNode constants = findConstant(sn);
		sn = StepNode.subtract(sn, constants).regroup();

		if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (isPower(so)) {
				return true;
			} else if (so.noOfOperands() == 2 && getPower(so.getSubTree(0)) != 0
					&& getPower(so.getSubTree(0)) == getPower(so.getSubTree(1))) {
				return true;
			}
		}

		return false;
	}

	public static boolean canCompleteCube(StepNode sn, StepNode variable) {
		if (degree(sn) != 3) {
			return false;
		}

		StepNode cubic = findCoefficient(sn, StepNode.power(variable, 3));
		StepNode quadratic = findCoefficient(sn, StepNode.power(variable, 2));
		StepNode linear = findCoefficient(sn, variable);

		if (!isOne(cubic)) {
			return false;
		}

		if (isEqual(StepNode.power(quadratic, 2).getValue(), StepNode.multiply(3, linear).getValue())) {
			return true;
		}

		return false;
	}

	public static boolean canBeReducedToQuadratic(StepNode sn, StepNode variable) {
		int degree = degree(sn);

		if (degree / 2 * 2 != degree) { // if degree is odd
			return false;
		}

		for (int i = 1; i < degree; i++) {
			if (i != degree / 2) {
				StepNode coeff = findCoefficient(sn, StepNode.power(variable, i));
				if (!isZero(coeff)) {
					return false;
				}
			}
		}

		return true;
	}

	public static boolean integerCoefficients(StepNode sn, StepNode variable) {
		int degree = degree(sn);

		double constant = findConstant(sn).getValue();
		if (Math.floor(constant) != constant) {
			return false;
		}

		for (int i = 1; i <= degree; i++) {
			double coeff = getCoefficientValue(sn, StepNode.power(variable, i));
			if (Math.floor(coeff) != coeff) {
				return false;
			}
		}

		return true;
	}

	public static boolean shouldMultiply(StepNode sn) {
		return countOperation(sn, Operation.DIVIDE) > 1 || countNonConstOperation(sn, Operation.DIVIDE) > 0;
	}

	public static int countNonConstOperation(StepNode sn, Operation operation) {
		if (sn != null && sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(operation) && !so.isConstant()) {
				return 1;
			}

			int operations = 0;
			for (int i = 0; i < so.noOfOperands(); i++) {
				operations += countNonConstOperation(so.getSubTree(i), operation);
			}
			return operations;
		}

		return 0;
	}

	public static int countOperation(StepNode sn, Operation operation) {
		if (sn != null && sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(operation)) {
				return 1;
			}

			int operations = 0;
			for (int i = 0; i < so.noOfOperands(); i++) {
				operations += countOperation(so.getSubTree(i), operation);
			}
			return operations;
		}

		return 0;
	}

	public static StepNode swapAbsInTree(StepNode sn, StepNode a, StepNode b, StepVariable variable) {
		if (sn != null && sn.isOperation()) {
			StepOperation so = (StepOperation) sn;
			if (so.isOperation(Operation.ABS)) {
				if (isNegative(so.getSubTree(0), a, b, variable)) {
					return StepNode.minus(so.getSubTree(0));
				}
				return so.getSubTree(0);
			}

			StepOperation newSo = new StepOperation(so.getOperation());
			for (int i = 0; i < so.noOfOperands(); i++) {
				newSo.addSubTree(swapAbsInTree(so.getSubTree(i), a, b, variable));
			}
			return newSo;
		}

		return sn;
	}

	private static boolean isNegative(StepNode x, StepNode a, StepNode b, StepVariable variable) {
		StepNode evaluateAt;

		if (Double.isInfinite(a.getValue()) && a.getValue() < 0) {
			evaluateAt = StepNode.subtract(b, 10).simplify();
		} else if (Double.isInfinite(b.getValue()) && b.getValue() > 0) {
			evaluateAt = StepNode.add(a, 10).simplify();
		} else {
			evaluateAt = StepNode.divide(StepNode.add(a, b), 2).simplify();
		}

		return x.getValueAt(variable, evaluateAt.getValue()) < 0;
	}

	public static boolean isValidSolution(StepNode LHS, StepNode RHS, StepNode solution, StepVariable variable, Kernel kernel) {
		StepNode denominators = getDenominator(StepNode.add(LHS, RHS), kernel);

		if (denominators != null && !denominators.isConstant()) {
			if (isEqual(denominators.getValueAt(variable, solution.getValue()), 0)) {
				return false;
			}
		}

		double evaluatedLHS = LHS.getValueAt(variable, solution.getValue());
		double evaluatedRHS = RHS.getValueAt(variable, solution.getValue());

		if (!isEqual(evaluatedLHS, evaluatedRHS)) {
			return false;
		}

		return true;
	}

	public static double getCoefficientValue(StepNode sn, StepNode s) {
		StepNode coeff = findCoefficient(sn, s);
		return coeff == null ? 0 : coeff.getValue();
	}

	private static boolean isZero(StepNode sn) {
		return sn == null || isEqual(sn.getValue(), 0);
	}

	private static boolean isOne(StepNode sn) {
		return sn == null || isEqual(sn.getValue(), 1);
	}

	private static boolean isEqual(double a, double b) {
		return Math.abs(a - b) < 0.00000001;
	}

	public static String callCAS(String s, String cmd, Kernel kernel) {
		return kernel.getGeoGebraCAS().evaluateGeoGebraCAS(cmd + "[" + s + "]", null, StringTemplate.defaultTemplate, kernel);
	}

	public static StepNode[] getCASSolutions(String LHS, String RHS, String variable, Kernel kernel) {
		String s = callCAS(LHS + " = " + RHS + ", " + variable, "Solutions", kernel);

		try {
			MyList solutionList = (MyList) kernel.getParser().parseGeoGebraExpression(s).unwrap();

			StepNode[] sn = new StepNode[solutionList.getLength()];

			for (int i = 0; i < solutionList.getLength(); i++) {
				sn[i] = StepNode.convertExpression(solutionList.getListElement(i));
			}

			return sn;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static int degree(StepNode sn) {
		StepNode newSn = sn.deepCopy().regroup();

		if (newSn instanceof StepVariable) {
			return 1;
		} else if (newSn instanceof StepConstant) {
			return 0;
		} else if (newSn.isOperation()) {
			StepOperation so = (StepOperation) newSn;
			
			switch(so.getOperation()) {
			case MINUS:
				return degree(so.getSubTree(0));
			case PLUS:
				int max = 0;

				for (int i = 0; i < so.noOfOperands(); i++) {
					int temp = degree(so.getSubTree(i));
					if (temp == -1) {
						return -1;
					} else if (temp > max) {
						max = temp;
					}
				}

				return max;
			case POWER:
				int temp = degree(so.getSubTree(0));
				if (temp == -1) {
					return -1;
				}
				if (StepOperation.closeToAnInteger(so.getSubTree(1).getValue())) {
					return (int) (temp * so.getSubTree(1).getValue());
				}
				return -1;
			case MULTIPLY:
				int p = 0;

				for (int i = 0; i < so.noOfOperands(); i++) {
					int tmp = degree(so.getSubTree(i));
					if (tmp == -1) {
						return -1;
					}
					p += tmp;
				}

				return p;
			case DIVIDE:
				if (!so.getSubTree(1).isConstant()) {
					return -1;
				}
				return degree(so.getSubTree(0));
			case NROOT:
				if (so.getSubTree(0).isConstant()) {
					return 0;
				}
				return -1;
			}
		}

		return -1;
	}

	public static StepNode LCM(StepNode a, StepNode b, Kernel kernel) {
		return StepNode.getStepTree(kernel.getGeoGebraCAS().evaluateGeoGebraCAS(
				"Factor(LCM(" + (a == null ? "1" : a.toString()) + ", " + (b == null ? "1" : b.toString()) + "))", null,
				StringTemplate.defaultTemplate, kernel), kernel.getParser());
	}

	public static StepNode factor(StepNode sn, Kernel kernel) {
		return StepNode.getStepTree(callCAS(sn.toString(), "Factor", kernel), kernel.getParser());
	}
}
