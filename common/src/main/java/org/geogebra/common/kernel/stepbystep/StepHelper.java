package org.geogebra.common.kernel.stepbystep;

import java.util.ArrayList;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.kernel.stepbystep.steptree.StepArbitraryConstant;
import org.geogebra.common.kernel.stepbystep.steptree.StepConstant;
import org.geogebra.common.kernel.stepbystep.steptree.StepEquation;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepInterval;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepOperation;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;
import org.geogebra.common.plugin.Operation;

public class StepHelper {

	public static StepExpression getCommon(StepExpression a, StepExpression b) {
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

	public static boolean containsExactExpression(StepExpression sn, StepExpression expr) {
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
	 * @param sn
	 *            expression tree to traverse
	 * @param kernel
	 *            GeoGebra kernel (used for CAS)
	 * @return lowest common denominator of the expression
	 */
	public static StepExpression getCommonDenominator(StepExpression sn) {
		if (sn instanceof StepOperation) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.DIVIDE)) {
				return so.getSubTree(1);
			} else if (so.isOperation(Operation.MINUS)) {
				return getCommonDenominator(so.getSubTree(0));
			} else if (so.isOperation(Operation.PLUS) || so.isOperation(Operation.MULTIPLY)) {
				StepExpression denominator = null;
				for (int i = 0; i < so.noOfOperands(); i++) {
					StepExpression newDenominator = getCommonDenominator(so.getSubTree(i));
					if (newDenominator != null) {
						if (denominator == null) {
							denominator = newDenominator;
						} else if (StepNode.closeToAnInteger(denominator.getValue())
								&& StepNode.closeToAnInteger(newDenominator.getValue())) {
							long a = (long) denominator.getValue();
							long b = (long) newDenominator.getValue();
							long denominatorValue = (a * b) / StepNode.gcd(a, b);
							denominator = new StepConstant(denominatorValue);
						} else {
							denominator = LCM(denominator, newDenominator);
						}
					}
				}
				return denominator;
			}
		}
		return null;
	}

	public static StepExpression factorDenominators(StepExpression sn, Boolean[] changed) {
		if (sn instanceof StepOperation) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.DIVIDE)) {
				StepExpression newDenominator = so.getSubTree(1).factor();
				if (!isZero(StepNode.subtract(so.getSubTree(1), newDenominator).regroup())) {
					changed[0] = true;
					return StepNode.divide(so.getSubTree(0), newDenominator);
				}
				return so;
			}

			if (so.isOperation(Operation.MINUS) || so.isOperation(Operation.PLUS)
					|| so.isOperation(Operation.MULTIPLY)) {
				StepOperation newSo = new StepOperation(so.getOperation());

				for (int i = 0; i < so.noOfOperands(); i++) {
					newSo.addSubTree(factorDenominators(so.getSubTree(i), changed));
				}

				return newSo;
			}

			return so;
		}

		return sn;
	}

	public static StepExpression expandFractions(StepExpression sn, StepExpression commonDenominator,
			Boolean[] changed) {
		if (sn instanceof StepOperation) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.DIVIDE)) {
				StepExpression ratio = StepNode.divide(commonDenominator, so.getSubTree(1)).regroup();
				if (!isOne(ratio)) {
					changed[0] = true;
					return StepNode.divide(StepNode.multiply(ratio, so.getSubTree(0)).regroup(),
							commonDenominator);
				}
				return so;
			}

			if (so.isOperation(Operation.PLUS)) {
				StepOperation newSo = new StepOperation(so.getOperation());

				for (int i = 0; i < so.noOfOperands(); i++) {
					if (StepExpression.getNumerator(so.getSubTree(i)).equals(so.getSubTree(i))) {
						newSo.addSubTree(
								StepNode.divide(StepNode.multiply(commonDenominator, so.getSubTree(i)).regroup(),
								commonDenominator));
					} else {
						newSo.addSubTree(
								expandFractions(so.getSubTree(i), commonDenominator, changed));
					}
				}

				return newSo;
			}

			if (so.isOperation(Operation.MINUS) || so.isOperation(Operation.MULTIPLY)) {
				StepOperation newSo = new StepOperation(so.getOperation());

				newSo.addSubTree(expandFractions(so.getSubTree(0), commonDenominator, changed));

				for (int i = 1; i < so.noOfOperands(); i++) {
					newSo.addSubTree(so.getSubTree(i));
				}

				return newSo;
			}

			return so;
		}

		return sn;
	}

	public static StepExpression addFractions(StepExpression sn, StepExpression commonDenominator, Boolean[] changed) {
		if (sn instanceof StepOperation) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.PLUS)) {
				changed[0] = true;

				StepExpression newSo = null;
				for (int i = 0; i < so.noOfOperands(); i++) {
					newSo = StepNode.add(newSo, StepExpression.getNumerator(so.getSubTree(i)));
				}

				return StepNode.divide(newSo, commonDenominator);
			} else if (so.isOperation(Operation.MINUS)) {
				return StepNode.minus(addFractions(so.getSubTree(0), commonDenominator, changed));
			}

			return so;
		}

		return sn;
	}

	/**
	 * @param sn
	 *            expression tree to traverse
	 * @return sum of all the subexpressions containing square roots
	 */
	public static StepExpression getAll(StepExpression sn, Operation op) {
		if (sn instanceof StepOperation) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(op)) {
				return so;
			} else if (so.isOperation(Operation.MULTIPLY) || so.isOperation(Operation.DIVIDE)
					|| so.isOperation(Operation.MINUS)) {
				if (countOperation(so, op) > 0) {
					return so;
				}
			} else if (so.isOperation(Operation.PLUS)) {
				StepExpression roots = null;
				for (int i = 0; i < so.noOfOperands(); i++) {
					roots = StepNode.add(roots, getAll(so.getSubTree(i), op));
				}
				return roots;
			}
		}
		return null;
	}

	/**
	 * @param sn
	 *            expression tree to traverse
	 * @return part of the expression tree, which doesn't contain roots
	 */
	public static StepExpression getNon(StepExpression sn, Operation op) {
		return StepNode.subtract(sn, getAll(sn, op)).regroup();
	}

	/**
	 * @param sn
	 *            expression tree to traverse
	 * @return first subexpression containing square roots
	 */
	public static StepExpression getOne(StepExpression sn, Operation op) {
		if (sn instanceof StepOperation) {
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
				StepExpression root = null;
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
	 * @param sn
	 *            expression tree to traverse
	 * @param expr
	 *            expression to find
	 * @return all subexpression which contain expr
	 */
	public static StepExpression findVariable(StepNode sn, StepExpression expr) {
		if (sn != null && sn.equals(expr)) {
			return (StepExpression) sn;
		}
		if (sn instanceof StepOperation) {
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
				StepExpression found = null;
				for (int i = 0; i < so.noOfOperands(); i++) {
					found = StepNode.add(found, findVariable(so.getSubTree(i), expr));
				}
				return found;
			}
		}
		return null;
	}

	/**
	 * @param sn
	 *            expression tree to traverse
	 * @param expr
	 *            expression to find
	 * @return the coefficient of expr in the tree
	 */
	public static StepExpression findCoefficient(StepExpression sn, StepExpression expr) {
		if (sn != null && sn.equals(expr)) {
			return new StepConstant(1);
		}
		if (sn instanceof StepOperation) {
			StepOperation so = (StepOperation) sn;

			if (!containsExpression(sn, expr)) {
				return null;
			}

			if (so.isOperation(Operation.DIVIDE)) {
				StepExpression coeff;
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
				StepExpression coeff = new StepConstant(1);
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
				StepExpression found = null;
				for (int i = 0; i < so.noOfOperands(); i++) {
					found = StepNode.add(found, findCoefficient(so.getSubTree(i), expr));
				}
				return found == null ? null : found.regroup();
			}
		}
		return null;
	}

	/**
	 * @param sn
	 *            expression tree to traverse
	 * @param expr
	 *            expression to find
	 * @return whether sn contains expr
	 */
	private static boolean containsExpression(StepExpression sn, StepExpression expr) {
		if (sn != null && sn.equals(expr)) {
			return true;
		}
		if (sn instanceof StepOperation) {
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
	 * @param sn
	 *            expression tree to traverse
	 * @return subexpression which do not contain any variable part
	 */
	public static StepExpression findConstant(StepExpression sn) {
		if (sn != null && sn.isConstant()) {
			return sn;
		}
		if (sn instanceof StepOperation) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.MINUS)) {
				return StepNode.minus(findConstant(so.getSubTree(0)));
			} else if (so.isOperation(Operation.PLUS)) {
				StepExpression found = null;
				for (int i = 0; i < so.noOfOperands(); i++) {
					found = StepNode.add(found, findConstant(so.getSubTree(i)));
				}
				return found;
			}
		}

		return null;
	}

	public static int getPower(StepExpression sn) {
		if (sn instanceof StepOperation) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.POWER) && !so.getSubTree(0).isConstant()) {
				return (int) so.getSubTree(1).getValue();
			} else if (so.isOperation(Operation.MULTIPLY) || so.isOperation(Operation.DIVIDE)
					|| so.isOperation(Operation.MINUS)) {
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

	public static boolean isPower(StepExpression sn) {
		if (sn instanceof StepOperation) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.POWER)) {
				return true;
			} else if (so.isOperation(Operation.MINUS) || so.isOperation(Operation.MULTIPLY)
					|| so.isOperation(Operation.DIVIDE)) {
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

	public static void getAbsoluteValues(ArrayList<StepExpression> absoluteValues, StepNode sn) {
		if (sn instanceof StepOperation) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.ABS)) {
				absoluteValues.add(so.getSubTree(0));
			} else {
				for (int i = 0; i < so.noOfOperands(); i++) {
					getAbsoluteValues(absoluteValues, so.getSubTree(i));
				}
			}
		} else if (sn instanceof StepEquation) {
			StepEquation se = (StepEquation) sn;
			
			getAbsoluteValues(absoluteValues, se.getLHS());
			getAbsoluteValues(absoluteValues, se.getRHS());
		}
	}

	public static boolean shouldTakeRoot(StepExpression snRHS, StepExpression snLHS) {
		StepExpression sn = StepNode.subtract(snRHS, snLHS).regroup();

		StepExpression constants = findConstant(sn);
		sn = StepNode.subtract(sn, constants).regroup();

		if (sn instanceof StepOperation) {
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

	public static boolean canCompleteCube(StepExpression sn, StepExpression variable) {
		if (degree(sn) != 3) {
			return false;
		}

		StepExpression cubic = findCoefficient(sn, StepNode.power(variable, 3));
		StepExpression quadratic = findCoefficient(sn, StepNode.power(variable, 2));
		StepExpression linear = findCoefficient(sn, variable);

		if (!isOne(cubic)) {
			return false;
		}

		if (isEqual(StepNode.power(quadratic, 2).getValue(), StepNode.multiply(3, linear).getValue())) {
			return true;
		}

		return false;
	}

	public static boolean canBeReducedToQuadratic(StepExpression sn, StepExpression variable) {
		int degree = degree(sn);

		if (degree % 2 != 0) {
			return false;
		}

		for (int i = 1; i < degree; i++) {
			if (i != degree / 2) {
				StepExpression coeff = findCoefficient(sn, StepNode.power(variable, i));
				if (!isZero(coeff)) {
					return false;
				}
			}
		}

		return true;
	}

	public static boolean shouldReciprocate(StepExpression sn) {
		if (sn.isOperation(Operation.DIVIDE)) {
			StepOperation so = (StepOperation) sn;

			return so.getSubTree(0).isConstant();
		}

		return sn.isConstant();
	}

	public static boolean shouldMultiply(StepEquation se) {
		return countOperation(se, Operation.DIVIDE) > 1
				|| countNonConstOperation(se, Operation.DIVIDE) == 1 && linearInInverse(se) == null;
	}

	public static StepOperation findInverse(StepNode sn) {
		if (sn instanceof StepOperation) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.DIVIDE) && so.getSubTree(0).isConstant() && !so.getSubTree(1).isConstant()) {
				return (StepOperation) sn;
			}

			for (int i = 0; i < so.noOfOperands(); i++) {
				StepOperation inverse = findInverse(so.getSubTree(i));
				if (inverse != null) {
					return inverse;
				}
			}
			return null;
		} else if (sn instanceof StepEquation) {
			StepEquation se = (StepEquation) sn;

			StepOperation temp = findInverse(se.getLHS());
			if (temp != null) {
				return temp;
			}

			return findInverse(se.getRHS());
		}

		return null;
	}

	public static StepOperation linearInInverse(StepEquation se) {
		StepOperation inverse = findInverse(se);

		StepEquation withVariable = se.deepCopy();
		withVariable.replace(inverse, new StepVariable("x"));
		StepEquation withConstant = se.deepCopy();
		withConstant.replace(inverse, new StepConstant(1));

		if (inverse != null && degree(withVariable) == 1 && degree(withConstant) == 0) {
			return inverse;
		}

		return null;
	}

	public static StepExpression multiplyByConstant(StepExpression a, StepExpression b) {
		if (b.isOperation(Operation.PLUS)) {
			StepOperation bo = (StepOperation) b;

			StepOperation product = new StepOperation(Operation.PLUS);
			for (int i = 0; i < bo.noOfOperands(); i++) {
				if (bo.getSubTree(i).isNegative()) {
					product.addSubTree(StepNode.multiply(a, bo.getSubTree(i).negate()).negate());
				} else {
					product.addSubTree(StepNode.multiply(a, bo.getSubTree(i)));
				}
			}
			return product;
		}

		return StepNode.multiply(a, b);
	}

	public static int countNonConstOperation(StepNode sn, Operation operation) {
		if (sn instanceof StepOperation) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(operation) && !so.isConstant()) {
				return 1;
			}

			int operations = 0;
			for (int i = 0; i < so.noOfOperands(); i++) {
				operations += countNonConstOperation(so.getSubTree(i), operation);
			}
			return operations;
		} else if (sn instanceof StepEquation) {
			StepEquation se = (StepEquation) sn;

			return countNonConstOperation(se.getLHS(), operation) + countNonConstOperation(se.getRHS(), operation);
		}

		return 0;
	}

	public static int countOperation(StepNode sn, Operation operation) {
		if (sn instanceof StepOperation) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(operation)) {
				return 1;
			}

			int operations = 0;
			for (int i = 0; i < so.noOfOperands(); i++) {
				operations += countOperation(so.getSubTree(i), operation);
			}
			return operations;
		} else if (sn instanceof StepEquation) {
			StepEquation se = (StepEquation) sn;
			
			return countOperation(se.getLHS(), operation) + countOperation(se.getRHS(), operation);
		}

		return 0;
	}

	public static boolean containsTrigonometric(StepNode sn) {
		if (sn instanceof StepOperation) {
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
		} else if (sn instanceof StepEquation) {
			StepEquation se = (StepEquation) sn;

			return containsTrigonometric(se.getLHS()) || containsTrigonometric(se.getRHS());
		}

		return false;
	}

	public static StepOperation findTrigonometricVariable(StepExpression sn) {
		if (sn instanceof StepOperation) {
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

	public static StepOperation linearInTrigonometric(StepExpression sn) {
		StepOperation trigoVar = findTrigonometricVariable(sn);
		int degree = degree(sn.deepCopy().replace(trigoVar, new StepVariable("x")));

		if (degree == 1) {
			return trigoVar;
		}

		return null;
	}

	public static StepOperation quadraticInTrigonometric(StepExpression sn) {
		StepOperation trigoVar = findTrigonometricVariable(sn);
		int degree = degree(sn.deepCopy().replace(trigoVar, new StepVariable("x")));

		if (degree == 2) {
			return trigoVar;
		}

		return null;
	}

	public static boolean isProduct(StepExpression sn) {
		if (sn.isOperation(Operation.MULTIPLY)) {
			StepOperation so = (StepOperation) sn;

			int nonConstMultiplicands = 0;
			for (int i = 0; i < so.noOfOperands() && nonConstMultiplicands < 2; i++) {
				if (!so.getSubTree(i).isConstant()) {
					nonConstMultiplicands++;
				}
			}

			return nonConstMultiplicands > 1;
		}

		return false;
	}

	public static StepExpression swapAbsInTree(StepExpression sn, StepInterval si, StepVariable variable) {
		if (sn instanceof StepOperation) {
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

	private static boolean isNegative(StepExpression x, StepExpression a, StepExpression b, StepVariable variable) {
		StepExpression evaluateAt;

		if (Double.isInfinite(a.getValue()) && a.getValue() < 0) {
			evaluateAt = StepNode.subtract(b, 10);
		} else if (Double.isInfinite(b.getValue()) && b.getValue() > 0) {
			evaluateAt = StepNode.add(a, 10);
		} else {
			evaluateAt = StepNode.divide(StepNode.add(a, b), 2);
		}

		return x.getValueAt(variable, evaluateAt.getValue()) < 0;
	}

	public static boolean isValidSolution(StepExpression LHS, StepExpression RHS, StepExpression solution,
			StepVariable variable, Kernel kernel) {
		StepExpression denominators = getCommonDenominator(StepNode.add(LHS, RHS));

		if (denominators != null && !denominators.isConstant()) {
			if (isEqual(denominators.getValueAt(variable, solution.getValue()), 0)) {
				return false;
			}
		}

		String casCommand = "CorrectSolution(" + LHS + ", " + RHS + ", " + variable + " = " + solution + ")";
		String withAssumptions = getAssumptions(StepNode.add(LHS, StepNode.add(RHS, solution)), casCommand);

		try {
			String result = kernel.evaluateCachedGeoGebraCAS(withAssumptions, null);
			return "true".equals(result);
		} catch (Throwable e) {
			return false;
		}
	}

	private static String getAssumptions(StepExpression sn, String s) {
		if (sn instanceof StepArbitraryConstant) {
			return "AssumeInteger(" + sn.toString() + ", " + s + ")";
		} else if (sn instanceof StepOperation) {
			String temp = s;
			for (int i = 0; i < ((StepOperation) sn).noOfOperands(); i++) {
				temp = getAssumptions(((StepOperation) sn).getSubTree(i), temp);
			}
			return temp;
		}

		return s;
	}

	public static double getCoefficientValue(StepExpression sn, StepExpression s) {
		StepExpression coeff = findCoefficient(sn, s);
		return coeff == null ? 0 : coeff.getValue();
	}

	private static boolean isZero(StepExpression sn) {
		return sn == null || isEqual(sn.getValue(), 0);
	}

	private static boolean isOne(StepExpression sn) {
		return sn == null || isEqual(sn.getValue(), 1);
	}

	private static boolean isEqual(double a, double b) {
		return Math.abs(a - b) < 0.00000001;
	}

	public static StepExpression[] getCASSolutions(String LHS, String RHS, String variable, Kernel kernel) {
		try {
			String s = kernel.evaluateCachedGeoGebraCAS("Solutions(" + LHS + " = " + RHS + ", " + variable + ")", null);
			MyList solutionList = (MyList) kernel.getParser().parseGeoGebraExpression(s).unwrap();

			StepExpression[] sn = new StepExpression[solutionList.getLength()];

			for (int i = 0; i < solutionList.getLength(); i++) {
				sn[i] = (StepExpression) StepNode.convertExpression(solutionList.getListElement(i));
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
		} else if (sn instanceof StepEquation) {
			int degreeLHS = degree(((StepEquation) sn).getLHS());
			int degreeRHS = degree(((StepEquation) sn).getRHS());
			
			if (degreeLHS == -1 || degreeRHS == -1) {
				return -1;
			}

			return Math.max(degreeLHS, degreeRHS);
		} else if (sn instanceof StepOperation) {
			StepOperation so = (StepOperation) sn;

			if (so.isTrigonometric() || so.isInverseTrigonometric()) {
				if (so.isConstant()) {
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
				if (StepNode.closeToAnInteger(so.getSubTree(1).getValue())) {
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
			case PLUSMINUS:
				return -1;
			}
		}

		return -1;
	}

	public static StepExpression LCM(StepExpression a, StepExpression b) {
		return StepNode.multiply(a, b);
	}
}
