package org.geogebra.common.kernel.stepbystep;

import java.util.ArrayList;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.kernel.stepbystep.steptree.StepArbitraryConstant;
import org.geogebra.common.kernel.stepbystep.steptree.StepConstant;
import org.geogebra.common.kernel.stepbystep.steptree.StepInterval;
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
							long denominatorValue = (a * b) / StepNode.gcd(a, b);
							denominator = new StepConstant(denominatorValue);
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
	public static StepNode getAll(StepNode sn, Operation op) {
		if (sn != null && sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(op)) {
				return so;
			} else if (so.isOperation(Operation.MULTIPLY) || so.isOperation(Operation.DIVIDE) || so.isOperation(Operation.MINUS)) {
				if (countOperation(so, op) > 0) {
					return so;
				}
			} else if (so.isOperation(Operation.PLUS)) {
				StepNode roots = null;
				for (int i = 0; i < so.noOfOperands(); i++) {
					roots = StepNode.add(roots, getAll(so.getSubTree(i), op));
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
	public static StepNode getNon(StepNode sn, Operation op) {
		return StepNode.subtract(sn, getAll(sn, op)).regroup();
	}

	/**
	 * @param sn expression tree to traverse
	 * @return first subexpression containing square roots
	 */
	public static StepNode getOne(StepNode sn, Operation op) {
		if (sn != null && sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(op)) {
				return so;
			} else if (so.isOperation(Operation.MULTIPLY) || so.isOperation(Operation.DIVIDE)) {
				if (countOperation(so, Operation.NROOT) > 0) {
					return so;
				}
			} else if (so.isOperation(Operation.MINUS)) {
				return StepNode.minus(getOne(so, op));
			} else if (so.isOperation(Operation.PLUS)) {
				StepNode root = null;
				for (int i = 0; i < so.noOfOperands(); i++) {
					root = getOne(so.getSubTree(i), op);
					if (root != null) {
						return root;
					}
				}
				return root;
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

			if (so.isOperation(Operation.MULTIPLY) || so.isOperation(Operation.DIVIDE)) {
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
			} else if (isZero(constants) && so.noOfOperands() == 2 && getPower(so.getSubTree(0)) != 0
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

		double constant = getValue(findConstant(sn));
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

	public static boolean containsTrigonometric(StepNode sn) {
		if (sn != null && sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isTrigonometric()) {
				return true;
			}

			for (int i = 0; i < so.noOfOperands(); i++) {
				if (containsTrigonometric(so.getSubTree(i))) {
					return true;
				}
			}
			return false;
		}

		return false;
	}

	public static StepOperation findTrigonometricVariable(StepNode sn) {
		if (sn != null && sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isTrigonometric()) {
				return (StepOperation) sn;
			}

			for (int i = 0; i < so.noOfOperands(); i++) {
				StepOperation trigo = findTrigonometricVariable(so.getSubTree(i));
				if (trigo != null) {
					return trigo;
				}
			}
			return null;
		}

		return null;
	}

	public static StepOperation linearInTrigonometric(StepNode sn) {
		StepOperation trigoVar = findTrigonometricVariable(sn);
		int degree = degree(sn.deepCopy().replace(trigoVar, new StepVariable("x")));

		if (degree == 1) {
			return trigoVar;
		}

		return null;
	}

	public static StepOperation quadraticInTrigonometric(StepNode sn) {
		StepOperation trigoVar = findTrigonometricVariable(sn);
		int degree = degree(sn.deepCopy().replace(trigoVar, new StepVariable("x")));

		if (degree == 2) {
			return trigoVar;
		}

		return null;
	}

	public static StepNode swapAbsInTree(StepNode sn, StepInterval si, StepNode variable) {
		if (sn != null && sn.isOperation()) {
			StepOperation so = (StepOperation) sn;
			if (so.isOperation(Operation.ABS)) {
				if (isNegative(so.getSubTree(0), si.getLeftBound(), si.getRightBound(), variable)) {
					return StepNode.minus(so.getSubTree(0));
				}
				return so.getSubTree(0);
			}

			StepOperation newSo = new StepOperation(so.getOperation());
			for (int i = 0; i < so.noOfOperands(); i++) {
				newSo.addSubTree(swapAbsInTree(so.getSubTree(i), si, variable));
			}
			return newSo;
		}

		return sn;
	}

	private static boolean isNegative(StepNode x, StepNode a, StepNode b, StepNode variable) {
		StepNode evaluateAt;

		if (Double.isInfinite(a.getValue()) && a.getValue() < 0) {
			evaluateAt = StepNode.subtract(b, 10);
		} else if (Double.isInfinite(b.getValue()) && b.getValue() > 0) {
			evaluateAt = StepNode.add(a, 10);
		} else {
			evaluateAt = StepNode.divide(StepNode.add(a, b), 2);
		}

		return x.getValueAt(variable, evaluateAt.getValue()) < 0;
	}

	public static boolean isValidSolution(StepNode LHS, StepNode RHS, StepNode solution, StepNode variable, Kernel kernel) {
		StepNode denominators = getDenominator(StepNode.add(LHS, RHS), kernel);

		StepArbitraryConstant sa = new StepArbitraryConstant("k", 0, StepArbitraryConstant.ConstantType.INTEGER);

		if (denominators != null && !denominators.isConstant()) {
			if (isEqual(denominators.getValueAt(variable, solution.getValueAt(sa, 0)), 0)) {
				return false;
			}
		}

		double evaluatedLHS = LHS.getValueAt(variable, solution.getValueAt(sa, 0));
		double evaluatedRHS = RHS.getValueAt(variable, solution.getValueAt(sa, 0));

		if (!isEqual(evaluatedLHS, evaluatedRHS)) {
			return false;
		}

		return true;
	}

	public static double getCoefficientValue(StepNode sn, StepNode s) {
		StepNode coeff = findCoefficient(sn, s);
		return coeff == null ? 0 : coeff.getValue();
	}

	private static double getValue(StepNode sn) {
		return sn == null ? 0 : sn.getValue();
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

	public static StepNode[] getCASSolutions(String LHS, String RHS, String variable, Kernel kernel) {
		try {
			String s = kernel.evaluateCachedGeoGebraCAS("Solutions(" + LHS + " = " + RHS + ", " + variable + ")", null);
			MyList solutionList = (MyList) kernel.getParser().parseGeoGebraExpression(s).unwrap();

			StepNode[] sn = new StepNode[solutionList.getLength()];

			for (int i = 0; i < solutionList.getLength(); i++) {
				sn[i] = StepNode.convertExpression(solutionList.getListElement(i));
			}

			return sn;
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return null;
	}

	public static int degree(StepNode sn) {
		if (sn instanceof StepVariable) {
			return 1;
		} else if (sn instanceof StepConstant || sn instanceof StepArbitraryConstant) {
			return 0;
		} else if (sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isTrigonometric() || so.isInverseTrigonometric()) {
				if(so.isConstant()) {
					return 0;
				}
				return -1;
			}
			
			switch (so.getOperation()) {
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
		try {
			return StepNode.getStepTree(
					kernel.evaluateCachedGeoGebraCAS(
							"Factor(LCM(" + (a == null ? "1" : a.toString()) + ", " + (b == null ? "1" : b.toString()) + "))", null),
					kernel.getParser());
		} catch (Throwable e) {
			return null;
		}
	}

	public static StepNode factor(StepNode sn, Kernel kernel) {
		try {
			return StepNode.getStepTree(kernel.evaluateCachedGeoGebraCAS("Factor(" + sn.toString() + ")", null), kernel.getParser());
		} catch (Throwable e) {
			return null;
		}
	}
}
