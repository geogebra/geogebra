package org.geogebra.common.kernel.stepbystep;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.CASException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.kernel.stepbystep.steptree.*;
import org.geogebra.common.plugin.Operation;

public class StepHelper {

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
				if (so.countOperation(op) > 0) {
					return so;
				}
			} else if (so.isOperation(Operation.PLUS)) {
				StepExpression roots = null;
				for (StepExpression operand : so) {
					roots = StepNode.add(roots, getAll(operand, op));
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
				if (so.countOperation(Operation.NROOT) > 0) {
					return so;
				}
			} else if (so.isOperation(Operation.MINUS)) {
				return StepNode.minus(getOne(so, op));
			} else if (so.isOperation(Operation.PLUS)) {
				for (StepExpression operand : so) {
					StepExpression root = getOne(operand, op);
					if (root != null) {
						return root;
					}
				}
				return null;
			}
		}
		return null;
	}

	public static void getAbsoluteValues(ArrayList<StepExpression> absoluteValues, StepNode sn) {
		if (sn instanceof StepOperation) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.ABS)) {
				absoluteValues.add(so.getOperand(0));
			} else {
				for (StepExpression operand : so) {
					getAbsoluteValues(absoluteValues, operand);
				}
			}
		} else if (sn instanceof StepSolvable) {
			StepSolvable se = (StepSolvable) sn;
			
			getAbsoluteValues(absoluteValues, se.getLHS());
			getAbsoluteValues(absoluteValues, se.getRHS());
		}
	}

	public static boolean shouldReciprocate(StepExpression sn) {
		if (sn.isOperation(Operation.DIVIDE)) {
			StepOperation so = (StepOperation) sn;

			return so.getOperand(0).isConstant();
		}

		return sn.isConstant();
	}

	public static StepOperation findInverse(StepNode sn, StepVariable var) {
		if (sn instanceof StepOperation) {
			StepOperation so = (StepOperation) sn;

			if (so.isOperation(Operation.DIVIDE) && so.getOperand(0).isConstantIn(var) &&
					!so.getOperand(1).isConstantIn(var)) {
				return (StepOperation) sn;
			}

			for (StepExpression operand : so) {
				StepOperation inverse = findInverse(operand, var);
				if (inverse != null) {
					return inverse;
				}
			}
			return null;
		} else if (sn instanceof StepSolvable) {
			StepSolvable se = (StepSolvable) sn;

			StepOperation temp = findInverse(se.getLHS(), var);
			if (temp != null) {
				return temp;
			}

			return findInverse(se.getRHS(), var);
		}

		return null;
	}

	public static StepOperation linearInInverse(StepSolvable se, StepVariable var) {
		StepOperation inverse = findInverse(se, var);

		StepSolvable withVariable = se.deepCopy();
		withVariable.replace(inverse, new StepVariable("a"));
		StepSolvable withConstant = se.deepCopy();
		withConstant.replace(inverse, StepConstant.create(1));

		if (inverse != null && withVariable.degree(new StepVariable("a")) == 1 && withConstant.degree(var) == 0) {
			return inverse;
		}

		return null;
	}

	public static StepOperation findTrigonometricVariable(StepExpression sn) {
		if (sn instanceof StepOperation) {
			StepOperation so = (StepOperation) sn;

			if (so.isTrigonometric()) {
				return (StepOperation) sn;
			}

			for (StepExpression operand : so) {
				StepOperation trigo = findTrigonometricVariable(operand);
				if (trigo != null) {
					return trigo;
				}
			}
			return null;
		}

		return null;
	}

	public static StepOperation linearInTrigonometric(StepSolvable ss) {
		StepOperation trigoVar = findTrigonometricVariable(StepNode.subtract(ss.getLHS(), ss.getRHS()).regroup());
		StepVariable toReplace = new StepVariable("x");

		int degree = ss.deepCopy().replace(trigoVar, toReplace).degree(toReplace);

		if (degree == 1) {
			return trigoVar;
		}

		return null;
	}

	public static StepOperation quadraticInTrigonometric(StepSolvable ss) {
		StepOperation trigoVar = findTrigonometricVariable(StepNode.subtract(ss.getLHS(), ss.getRHS()).regroup());
		StepVariable toReplace = new StepVariable("x");

		int degree = ss.deepCopy().replace(trigoVar, toReplace).degree(toReplace);

		if (degree == 2) {
			return trigoVar;
		}

		return null;
	}

	public static StepExpression swapAbsInTree(StepExpression sn, StepInterval si, StepVariable variable) {
		if (sn instanceof StepOperation) {
			StepOperation so = (StepOperation) sn;
			if (so.isOperation(Operation.ABS)) {
				if (isNegative(so.getOperand(0), si.getLeftBound(), si.getRightBound(), variable)) {
					return StepNode.minus(so.getOperand(0));
				}
				return so.getOperand(0);
			}

			StepOperation newSo = new StepOperation(so.getOperation());
			for (StepExpression operand : so) {
				newSo.addOperand(swapAbsInTree(operand, si, variable));
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

	public static String getAssumptions(StepExpression sn, String s) {
		if (sn instanceof StepArbitraryConstant) {
			return "AssumeInteger(" + sn.toString() + ", " + s + ")";
		} else if (sn instanceof StepOperation) {
			String temp = s;
			for (StepExpression operand : (StepOperation) sn) {
				temp = getAssumptions(operand, temp);
			}
			return temp;
		}

		return s;
	}

	public static double getCoefficientValue(StepExpression sn, StepExpression s) {
		StepExpression coeff = sn.findCoefficient(s);
		return coeff == null ? 0 : coeff.getValue();
	}

	private static boolean isZero(StepExpression sn) {
		return sn == null || isEqual(sn.getValue(), 0);
	}

	private static boolean isEqual(double a, double b) {
		return Math.abs(a - b) < 0.00000001;
	}

	public static StepSet getCASSolutions(StepEquation se, StepVariable variable, Kernel kernel) throws CASException {
		try {
			String s = kernel.evaluateCachedGeoGebraCAS("Solutions(" + se + ", " + variable + ")", null);
			MyList solutionList = (MyList) kernel.getParser().parseGeoGebraExpression(s).unwrap();

			StepSet solutions = new StepSet();

			for (int i = 0; i < solutionList.getLength(); i++) {
				solutions.addElement(StepNode.convertExpression(solutionList.getListElement(i)));
			}

			return solutions;
		} catch (ParseException e) {
			e.printStackTrace();
			return new StepSet();
		}
	}

	public static StepExpression LCM(StepExpression a, StepExpression b) {
		if (isZero(a) || isZero(b)) {
			return null;
		}

		StepExpression aFactored = a.factor();
		StepExpression bFactored = b.factor();

		StepExpression integerA = aFactored.getIntegerCoefficient();
		StepExpression integerB = bFactored.getIntegerCoefficient();

		aFactored = aFactored.getNonInteger();
		bFactored = bFactored.getNonInteger();

		List<StepExpression> aBases = new ArrayList<>();
		List<StepExpression> aExponents = new ArrayList<>();
		List<StepExpression> bBases = new ArrayList<>();
		List<StepExpression> bExponents = new ArrayList<>();

		StepExpression.getBasesAndExponents(aFactored, null, aBases, aExponents);
		StepExpression.getBasesAndExponents(bFactored, null, bBases, bExponents);

		for (int i = 0; i < aBases.size(); i++) {
			for (int j = 0; j < bBases.size(); j++) {
				if (aBases.get(i).equals(bBases.get(j))) {
					boolean less = aExponents.get(i).getValue() < bExponents.get(j).getValue();

					if (less) {
						aExponents.set(i, StepConstant.create(0));
					} else {
						bExponents.set(j, StepConstant.create(0));
					}
				}
			}
		}

		StepExpression result = null;

		if (integerA != null && integerB != null) {
			result = StepConstant.create(StepNode.lcm(integerA, integerB));
		}

		for (int i = 0; i < aBases.size(); i++) {
			result = StepExpression.makeFraction(result, aBases.get(i), aExponents.get(i));
		}

		for (int i = 0; i < bBases.size(); i++) {
			result = StepExpression.makeFraction(result, bBases.get(i), bExponents.get(i));
		}

		return result;
	}

	public static StepExpression GCD(StepExpression a, StepExpression b) {
		if (isZero(a)) {
			return b;
		}

		if (isZero(b)) {
			return a;
		}

		StepExpression aFactored = a.factor();
		StepExpression bFactored = b.factor();

		StepExpression integerA = aFactored.getIntegerCoefficient();
		StepExpression integerB = bFactored.getIntegerCoefficient();

		aFactored = aFactored.getNonInteger();
		bFactored = bFactored.getNonInteger();

		List<StepExpression> aBases = new ArrayList<>();
		List<StepExpression> aExponents = new ArrayList<>();
		List<StepExpression> bBases = new ArrayList<>();
		List<StepExpression> bExponents = new ArrayList<>();

		StepExpression.getBasesAndExponents(aFactored, null, aBases, aExponents);
		StepExpression.getBasesAndExponents(bFactored, null, bBases, bExponents);

		boolean[] found = new boolean[aBases.size()];

		for (int i = 0; i < aBases.size(); i++) {
			for (int j = 0; j < bBases.size(); j++) {
				if (aBases.get(i).equals(bBases.get(j))) {
					StepExpression common = aExponents.get(i).getCommon(bExponents.get(j));

					if (!isZero(common)) {
						aExponents.set(j, common);
						found[i] = true;
					}
				}
			}
		}

		StepExpression result = null;

		if (integerA != null && integerB != null) {
			result = StepConstant.create(StepNode.gcd(integerA, integerB));
		}

		for (int i = 0; i < aBases.size(); i++) {
			if (found[i]) {
				result = StepExpression.makeFraction(result, aBases.get(i), aExponents.get(i));
			}
		}

		return result;
	}
}
