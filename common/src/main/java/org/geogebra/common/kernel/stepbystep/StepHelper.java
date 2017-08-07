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

	/**
	 * @param sn expression tree to parse
	 * @return lowest common denominator as a string
	 */
	public static StepNode getDenominator(StepNode sn, Kernel kernel) {
		if (sn != null && sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.DIVIDE)) {
				return so.getSubTree(1);
			} else if (so.isOperation(Operation.MINUS)) {
				return StepNode.minus(getDenominator(so.getSubTree(0), kernel));
			} else if (so.isOperation(Operation.PLUS) || so.isOperation(Operation.MULTIPLY)) {
				StepNode denominator = new StepConstant(1);
				for (int i = 0; i < so.noOfOperands(); i++) {
					denominator = LCM(denominator, getDenominator(so.getSubTree(i), kernel), kernel);
				}
				return denominator;
			}
		}
		return null;
	}

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

	public static StepNode getNonIrrational(StepNode sn) {
		return StepNode.subtract(sn, getSQRoots(sn)).regroup();
	}

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

	public static StepNode findVariable(StepNode sn, StepNode variable) {
		if (sn != null && sn.equals(variable)) {
			return sn;
		}
		if (sn != null && sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.equals(variable)) {
				return so;
			} else if (so.isOperation(Operation.MULTIPLY) || so.isOperation(Operation.DIVIDE)) {
				for (int i = 0; i < so.noOfOperands(); i++) {
					if (so.getSubTree(i).equals(variable)) {
						return so;
					}
				}
			} else if (so.isOperation(Operation.MINUS)) {
				return StepNode.minus(findVariable(so.getSubTree(0), variable));
			} else if (so.isOperation(Operation.PLUS)) {
				StepNode found = null;
				for (int i = 0; i < so.noOfOperands(); i++) {
					found = StepNode.add(found, findVariable(so.getSubTree(i), variable));
				}
				return found;
			}
		}
		return null;
	}

	public static StepNode findCoefficient(StepNode sn, StepNode variable) {
		if (sn != null && sn.equals(variable)) {
			return new StepConstant(1);
		}
		if (sn != null && sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if(!containsExpression(sn, variable)) {
				return null;
			}
			
			if (so.isOperation(Operation.DIVIDE)) {
				StepNode coeff;
				if (so.getSubTree(0).isConstant()) {
					coeff = so.getSubTree(0);
				} else {
					coeff = findCoefficient(so.getSubTree(0), variable);
				}
				if (so.getSubTree(1).isConstant()) {
					coeff = StepNode.divide(coeff, so.getSubTree(0));
				} else {
					coeff = StepNode.divide(coeff, findCoefficient(so.getSubTree(0), variable));
				}
				return coeff.regroup();
			} else if (so.isOperation(Operation.MULTIPLY)) {
				StepNode coeff = new StepConstant(1);
				for (int i = 0; i < so.noOfOperands(); i++) {
					if (so.getSubTree(i).isConstant()) {
						coeff = StepNode.multiply(coeff, so.getSubTree(i));
					} else {
						coeff = StepNode.multiply(coeff, findCoefficient(so.getSubTree(i), variable));
					}
				}
				return coeff.regroup();
			} else if (so.isOperation(Operation.MINUS)) {
				return StepNode.minus(findCoefficient(so.getSubTree(0), variable));
			} else if (so.isOperation(Operation.PLUS)) {
				StepNode found = null;
				for (int i = 0; i < so.noOfOperands(); i++) {
					found = StepNode.add(found, findCoefficient(so.getSubTree(i), variable));
				}
				return found == null ? null : found.regroup();
			}
		}
		return null;
	}

	private static boolean containsExpression(StepNode sn, StepNode expr) {
		if (sn != null && sn.equals(expr)) {
			return true;
		}
		if (sn != null && sn.isOperation()) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.POWER) || so.isOperation(Operation.NROOT)) {
				return expr.equals(so);
			}

			for(int i = 0; i < so.noOfOperands(); i++) {
				if(containsExpression(so.getSubTree(i), expr)) {
					return true;
				}
			}
		}
		return false;
	}

	public static StepNode findCoefficient(StepNode sn) {
		if (sn != null && sn.isOperation() && !sn.isConstant()) {
			StepOperation so = (StepOperation) sn;

			StepNode coeff = new StepConstant(1);
			if (so.isOperation(Operation.DIVIDE)) {
				if (so.getSubTree(0).isConstant()) {
					coeff = StepNode.multiply(coeff, so.getSubTree(0));
				}
				if (so.getSubTree(0).isConstant()) {
					coeff = StepNode.divide(coeff, so.getSubTree(1));
				}
			} else if (so.isOperation(Operation.MULTIPLY)) {
				for (int i = 0; i < so.noOfOperands(); i++) {
					if (so.getSubTree(i).isConstant()) {
						coeff = StepNode.multiply(coeff, so.getSubTree(i));
					}
				}
			}
			return coeff;
		}
		return null;
	}

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
			} else if (so.isOperation(Operation.MULTIPLY) || so.isOperation(Operation.DIVIDE)) {
				for(int i = 0; i < so.noOfOperands(); i++) {
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

			if (so.isOperation(Operation.POWER) && !so.getSubTree(0).isConstant()) {
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

	public static boolean canCompleteCube(StepNode sn, StepNode variable, Kernel kernel) {
		if (degree(sn, kernel) != 3) {
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

	public static boolean canBeReducedToQuadratic(StepNode sn, StepNode variable, Kernel kernel) {
		int degree = degree(sn, kernel);
		
		if(degree / 2 * 2 != degree) { 		// if degree is odd
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

	public static boolean integerCoefficients(StepNode sn, StepNode variable, Kernel kernel) {
		int degree = degree(sn, kernel);

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

		if (!denominators.isConstant()) {
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
		return sn == null || "".equals(sn.toString()) || "1".equals(sn.toString());
	}

	private static boolean isEqual(double a, double b) {
		if (Math.abs(a - b) < 0.00000001) {
			return true;
		}
		return false;
	}

	public static String callCAS(String s, String cmd, Kernel kernel) {
		return kernel.getGeoGebraCAS().evaluateGeoGebraCAS(cmd + "[" + s + "]", null, StringTemplate.defaultTemplate, kernel);
	}

	public static StepNode[] getCASSolutions(String LHS, String RHS, String variable, Kernel kernel) {
		String s = callCAS(LHS + " = " + RHS + ", " + variable, "Solutions", kernel);

		MyList solutionList = null;
		try {
			solutionList = (MyList) kernel.getParser().parseGeoGebraExpression(s).unwrap();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		StepNode[] sn = new StepNode[solutionList.getLength()];

		for (int i = 0; i < solutionList.getLength(); i++) {
			sn[i] = StepNode.convertExpression(solutionList.getListElement(i));
		}

		return sn;
	}

	public static int degree(StepNode sn, Kernel kernel) {
		String d = callCAS(sn.toString(), "Degree", kernel);
		if ("?".equals(d)) {
			return -1;
		}
		return Integer.parseInt(d);
	}

	public static StepNode LCM(StepNode a, StepNode b, Kernel kernel) {
		return StepNode.getStepTree(
				callCAS("(" + (a == null ? "1" : a.toString()) + "), (" + (b == null ? "1" : b.toString()) + ")", "LCM", kernel),
				kernel.getParser());
	}

	public static StepNode factor(StepNode sn, Kernel kernel) {
		return StepNode.getStepTree(callCAS(sn.toString(), "Factor", kernel), kernel.getParser());
	}
}
