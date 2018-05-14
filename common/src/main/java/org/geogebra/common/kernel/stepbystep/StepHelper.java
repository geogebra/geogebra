package org.geogebra.common.kernel.stepbystep;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

	public static abstract class Condition {
		public abstract StepNode isTrueFor(StepNode sn);

		public static final Condition underAbs = new Condition() {
			@Override
			public StepNode isTrueFor(StepNode sn) {
				if(sn.isOperation(Operation.ABS)) {
					return ((StepOperation) sn).getOperand(0);
				}
				return null;
			}
		};

		public static final Condition underEvenRoot = new Condition() {
			@Override
			public StepNode isTrueFor(StepNode sn) {
				if (sn.isOperation(Operation.NROOT)
						&& ((StepOperation) sn).getOperand(1).isEven()) {
					return ((StepOperation) sn).getOperand(0);
				}
				return null;
			}
		};

		public static final Condition plusminusToPlus = new Condition() {
			@Override
			public StepNode isTrueFor(StepNode sn) {
				if (sn.isOperation(Operation.PLUSMINUS)) {
					return ((StepOperation) sn).getOperand(0);
				}
				return null;
			}
		};

		public static final Condition plusminusToMinus = new Condition() {
			@Override
			public StepNode isTrueFor(StepNode sn) {
				if (sn.isOperation(Operation.PLUSMINUS)) {
					return minus(((StepOperation) sn).getOperand(0));
				}
				return null;
			}
		};

		public static final Condition isDenominator = new Condition() {
			@Override
			public StepNode isTrueFor(StepNode sn) {
				if(sn.isOperation(Operation.DIVIDE)) {
					return ((StepOperation) sn).getOperand(1);
				}
				return null;
			}
		};
	}

	/**
	 * Finds the first expression which adheres to the condition
	 * @param sn expression to search
	 * @param c condition
	 * @return the first expression which adheres to the condition
	 */
	public static StepExpression findExpression(StepExpression sn, Condition c) {
		StepNode value = c.isTrueFor(sn);
		if (value != null) {
			return (StepExpression) value;
		}

		if (sn instanceof StepOperation) {
			for (StepExpression operand : (StepOperation) sn) {
				StepExpression expression = findExpression(operand, c);
				if (expression != null) {
					return expression;
				}
			}
		}

		return null;
	}

	public static void getAll(StepNode sn, Set<StepExpression> values, Condition c) {
		StepNode value = c.isTrueFor(sn);
		if (value != null) {
			values.add((StepExpression) value);
		}

		if (sn instanceof StepOperation) {
			for (StepExpression operand : (StepOperation) sn) {
				getAll(operand, values, c);
			}
		}

		if (sn instanceof StepSolvable) {
			getAll(((StepSolvable) sn).getLHS(), values, c);
			getAll(((StepSolvable) sn).getRHS(), values, c);
		}
	}

	public static StepNode replaceFirst(StepNode sn, Condition c) {
		StepNode value = c.isTrueFor(sn);
		if (value != null) {
			return value.deepCopy();
		}

		if (sn instanceof StepOperation) {
			StepOperation so = (StepOperation) sn;
			StepExpression[] result = new StepExpression[so.noOfOperands()];

			boolean found = false;
			for (int i = 0; i < so.noOfOperands(); i++) {
				if (!found) {
					StepExpression replaced = (StepExpression) replaceFirst(so.getOperand(i), c);
					if (replaced != null) {
						result[i] = replaced;
						found = true;
						continue;
					}
				}
				result[i] = so.getOperand(i);
			}

			return found ? new StepOperation(so.getOperation(), result) : null;
		}

		if (sn instanceof StepSolvable) {
			StepSolvable replaced = (StepSolvable) sn.deepCopy();

			StepExpression lhs = (StepExpression) replaceFirst(replaced.getLHS(), c);
			StepExpression rhs = (StepExpression) replaceFirst(replaced.getRHS(), c);

			if (lhs != null) {
				replaced.modify(lhs, replaced.getRHS());
			} else if (rhs != null) {
				replaced.modify(replaced.getLHS(), rhs);
			}

			return replaced;
		}

		return null;
	}

	public static void getAbsoluteValues(StepNode sn, Set<StepExpression> absoluteValues) {
		getAll(sn, absoluteValues, Condition.underAbs);
	}

	public static void getDenominators(StepNode sn, Set<StepExpression> denominators) {
		getAll(sn, denominators, Condition.isDenominator);
	}

	public static void getRoots(StepNode sn, Set<StepExpression> roots) {
		getAll(sn, roots, Condition.underEvenRoot);
	}

	public static StepOperation findTrigonometricExpression(StepExpression se, final StepVariable sv) {
		return (StepOperation) findExpression(se, new Condition() {
			@Override
			public StepNode isTrueFor(StepNode sn) {
				if (sn instanceof StepOperation) {
					StepOperation so = (StepOperation) sn;
					if (so.isTrigonometric() && !so.isConstantIn(sv)) {
						return so;
					}
				}
				return null;
			}
		});
	}

	public static StepExpression nthDegreeInExpression(StepSolvable ss, final StepVariable var, int n) {
		StepExpression diff = StepNode.subtract(ss.getLHS(), ss.getRHS()).regroup();

		StepExpression expr = findExpression(diff, new Condition() {
			@Override
			public StepNode isTrueFor(StepNode sn) {
				if (sn instanceof StepOperation) {
					StepOperation so = (StepOperation) sn;

					if (so.isOperation(Operation.ABS) || so.isOperation(Operation.NROOT)
							|| so.isOperation(Operation.DIVIDE) && so.getOperand(0).isConstantIn(var)
							|| so.isTrigonometric() && !so.isConstantIn(var)) {
						return so;
					}
				}
				return null;
			}
		});

		if (expr == null) {
			return null;
		}

		StepVariable replacementVar = new StepVariable("a");
		if (var.equals(replacementVar)) {
			replacementVar = new StepVariable("x");
		}

		StepExpression withVariable = diff.replace(expr, replacementVar);
		StepExpression withConstant = diff.replace(expr, StepConstant.create(1));

		if (withVariable.degree(new StepVariable("a")) == n && withConstant.degree(var) == 0) {
			return expr;
		}

		return null;
	}

	public static StepOperation linearInExpression(StepSolvable ss, StepVariable var) {
		return (StepOperation) nthDegreeInExpression(ss, var, 1);
	}

	public static StepOperation quadraticInExpression(StepSolvable ss, StepVariable var) {
		return (StepOperation) nthDegreeInExpression(ss, var, 2);
	}

	public static StepSolvable replaceWithPlus(StepSolvable ss) {
		return (StepSolvable) replaceFirst(ss, Condition.plusminusToPlus);
	}

	public static StepSolvable replaceWithMinus(StepSolvable ss) {
		return (StepSolvable) replaceFirst(ss, Condition.plusminusToMinus);
	}

	/**
	 * @param sn
	 *            expression tree to traverse
	 * @return sum of all the subexpressions containing op
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

	public static boolean shouldReciprocate(StepExpression sn) {
		if (sn.isOperation(Operation.DIVIDE)) {
			StepOperation so = (StepOperation) sn;

			return so.getOperand(0).isConstant();
		}

		return sn.isConstant();
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

			StepExpression[] result = new StepExpression[so.noOfOperands()];
			for (int i = 0; i < so.noOfOperands(); i++) {
				result[i] = swapAbsInTree(so.getOperand(i), si, variable, steps, colorTracker);
			}
			return new StepOperation(so.getOperation(), result);
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

	public static List<StepNode> getCASSolutions(StepEquation se, StepVariable variable, Kernel kernel)
			throws CASException {
		try {
			String s = kernel.evaluateCachedGeoGebraCAS("Solutions(" + se + ", " + variable + ")", null);
			MyList solutionList = (MyList) kernel.getParser().parseGeoGebraExpression(s).unwrap();

			List<StepNode> solutions = new ArrayList<>();
			for (int i = 0; i < solutionList.getLength(); i++) {
				solutions.add(StepNode.convertExpression(solutionList.getListElement(i)));
			}

			return solutions;
		} catch (ParseException e) {
			e.printStackTrace();
			return new ArrayList<>();
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

		StepExpression GCD = simpleGCD(aFactored, bFactored);

		if (aFactored == null || bFactored == null || isOne(GCD)) {
			return multiply(constant, multiply(aFactored, bFactored));
		}

		return multiply(constant, multiply(aFactored, bFactored).quotient(GCD));
	}

	public static StepExpression simpleGCD(StepExpression a, StepExpression b) {
		if (isZero(a)) {
			return b;
		}

		if (isZero(b)) {
			return a;
		}

		List<StepExpression> aBases = new ArrayList<>();
		List<StepExpression> aExponents = new ArrayList<>();
		List<StepExpression> bBases = new ArrayList<>();
		List<StepExpression> bExponents = new ArrayList<>();

		a.getBasesAndExponents(aBases, aExponents);
		b.getBasesAndExponents(bBases, bExponents);

		boolean[] found = new boolean[aBases.size()];

		for (int i = 0; i < aBases.size(); i++) {
			for (int j = 0; j < bBases.size(); j++) {
				if (aBases.get(i).equals(bBases.get(j))) {
					StepExpression common = aExponents.get(i).getCommon(bExponents.get(j));

					if (!isZero(common)) {
						aExponents.set(i, common);
						bExponents.set(j, null);
						found[i] = true;
					}
				}
			}
		}

		StepExpression result = null;
		for (int i = 0; i < aBases.size(); i++) {
			if (found[i]) {
				result = nonTrivialProduct(result, nonTrivialPower(aBases.get(i), aExponents.get(i)));
			}
		}

		return result == null ? StepConstant.create(1) : result;
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
			if (!(integerA.isNegative() && integerB.isNegative()) && result.isNegative()) {
				result = result.negate();
			}
		}

		if (nonIntegerA == null || nonIntegerB == null) {
			return result == null ? StepConstant.create(1) : result;
		}

		return nonTrivialProduct(result, simpleGCD(nonIntegerA, nonIntegerB));
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
