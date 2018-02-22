package org.geogebra.common.kernel.stepbystep;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.CASException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steptree.*;
import org.geogebra.common.plugin.Operation;

import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.*;

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

	public static StepOperation findExpressionInVariable(StepExpression se, StepVariable var) {
		if (se instanceof StepOperation) {
			StepOperation so = (StepOperation) se;

			if ((so.isOperation(Operation.ABS) || so.isOperation(Operation.NROOT)
					|| so.isOperation(Operation.DIVIDE) && so.getOperand(0).isConstantIn(var)
					|| so.isTrigonometric())
					&& !so.isConstantIn(var)) {
				return so;
			}

			for (StepExpression operand : so) {
				StepOperation expression = findExpressionInVariable(operand, var);
				if (expression != null) {
					return expression;
				}
			}
			return null;
		}

		return null;
	}

	public static StepOperation nthDegreeInExpression(StepExpression se, StepVariable originalVar, StepOperation expr,
													  int n) {
		if (expr == null) {
			return null;
		}

		StepVariable replacementVar = new StepVariable("a");
		if (originalVar.equals(replacementVar)) {
			replacementVar = new StepVariable("x");
		}

		StepExpression withVariable = se.deepCopy().replace(expr, replacementVar);
		StepExpression withConstant = se.deepCopy().replace(expr, StepConstant.create(1));

		if (withVariable.degree(new StepVariable("a")) == n && withConstant.degree(originalVar) == 0) {
			return expr;
		}

		return null;
	}

	public static StepOperation linearInExpression(StepSolvable ss, StepVariable var) {
		StepExpression diff = StepNode.subtract(ss.getLHS(), ss.getRHS()).regroup();
		return nthDegreeInExpression(diff, var, findExpressionInVariable(diff, var), 1);
	}

	public static StepOperation quadraticInExpression(StepSolvable ss, StepVariable var) {
		StepExpression diff = StepNode.subtract(ss.getLHS(), ss.getRHS()).regroup();
		return nthDegreeInExpression(diff, var, findExpressionInVariable(diff, var), 2);
	}

	public static StepExpression swapAbsInTree(StepExpression se, StepLogical sl, StepVariable variable,
											   SolutionBuilder steps, int colorTracker[]) {
		if (se instanceof StepOperation && sl instanceof StepInterval) {
			StepOperation so = (StepOperation) se;
			StepInterval si = (StepInterval) sl;

			if (so.isOperation(Operation.ABS)) {
				StepExpression underAbs = so.getOperand(0);
				underAbs.setColor(++colorTracker[0]);

				if (isNegative(underAbs, si.getLeftBound(), si.getRightBound(), variable)) {
					StepExpression result = StepNode.minus(so.getOperand(0));
					result.setColor(colorTracker[0]);
					steps.add(SolutionStepType.IS_NEGATIVE_IN, underAbs, sl);
					return result;
				}

				steps.add(SolutionStepType.IS_POSITIVE_IN, underAbs, sl);
				return underAbs.deepCopy();
			}

			StepOperation newSo = new StepOperation(so.getOperation());
			for (StepExpression operand : so) {
				newSo.addOperand(swapAbsInTree(operand, si, variable, steps, colorTracker));
			}
			return newSo;
		}

		return se;
	}

	public static boolean isNegative(StepExpression x, StepExpression a, StepExpression b, StepVariable variable) {
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

		StepExpression constant;
		if (!isZero(integerA) && !isZero(integerB)) {
			constant = StepConstant.create(StepNode.lcm(integerA, integerB));
		} else {
			constant = StepNode.multiply(integerA, integerB);
		}

		StepExpression GCD = weakGCD(aFactored, bFactored);

		if (aFactored == null || bFactored == null || isOne(GCD)) {
			return multiply(constant, multiply(aFactored, bFactored));
		}

		return multiply(constant, multiply(aFactored, bFactored).quotient(GCD));
	}

	public static StepExpression weakGCD(StepExpression a, StepExpression b) {
		if (isZero(a)) {
			return b;
		}

		if (isZero(b)) {
			return a;
		}

		StepExpression integerA = a.getIntegerCoefficient();
		StepExpression integerB = b.getIntegerCoefficient();

		StepExpression nonIntegerA = a.getNonInteger();
		StepExpression nonIntegerB = b.getNonInteger();

		StepExpression result = null;
		if (integerA != null && integerB != null) {
			result = StepConstant.create(StepNode.gcd(integerA, integerB));
		}

		if (nonIntegerA == null || nonIntegerB == null) {
			return result == null ? StepConstant.create(1) : result;
		}

		List<StepExpression> aBases = new ArrayList<>();
		List<StepExpression> aExponents = new ArrayList<>();
		List<StepExpression> bBases = new ArrayList<>();
		List<StepExpression> bExponents = new ArrayList<>();

		nonIntegerA.getBasesAndExponents(aBases, aExponents);
		nonIntegerB.getBasesAndExponents(bBases, bExponents);

		boolean[] found = new boolean[aBases.size()];

		for (int i = 0; i < aBases.size(); i++) {
			for (int j = 0; j < bBases.size(); j++) {
				if (aBases.get(i).equals(bBases.get(j))) {
					StepExpression common = aExponents.get(i).getCommon(bExponents.get(j));

					if (!isZero(common)) {
						aExponents.set(i, common);
						found[i] = true;
					}
				}
			}
		}

		for (int i = 0; i < aBases.size(); i++) {
			if (found[i]) {
				result = multiply(result, nonTrivialPower(aBases.get(i), aExponents.get(i)));
			}
		}

		return result == null ? StepConstant.create(1) : result;
	}

	public static StepExpression GCD(StepExpression a, StepExpression b) {
		if (isZero(a)) {
			return b;
		}

		if (isZero(b)) {
			return a;
		}

		return weakGCD(a.factor(), b.factor());
	}
}
