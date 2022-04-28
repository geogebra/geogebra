package org.geogebra.common.kernel.stepbystep;

import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.nonTrivialPower;
import static org.geogebra.common.kernel.stepbystep.steptree.StepExpression.nonTrivialProduct;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.divide;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isOne;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.isZero;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.minus;
import static org.geogebra.common.kernel.stepbystep.steptree.StepNode.multiply;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.geogebra.common.kernel.CASException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.kernel.stepbystep.solution.HasLaTeX;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionLine;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.solution.SolutionTable;
import org.geogebra.common.kernel.stepbystep.solution.TableElement;
import org.geogebra.common.kernel.stepbystep.steps.SolveTracker;
import org.geogebra.common.kernel.stepbystep.steptree.StepArbitraryInteger;
import org.geogebra.common.kernel.stepbystep.steptree.StepConstant;
import org.geogebra.common.kernel.stepbystep.steptree.StepEquation;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepInequality;
import org.geogebra.common.kernel.stepbystep.steptree.StepInterval;
import org.geogebra.common.kernel.stepbystep.steptree.StepLogical;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepOperation;
import org.geogebra.common.kernel.stepbystep.steptree.StepSolution;
import org.geogebra.common.kernel.stepbystep.steptree.StepSolvable;
import org.geogebra.common.kernel.stepbystep.steptree.StepTransformable;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

public class StepHelper {

	public interface Condition {
		StepTransformable isTrueFor(StepTransformable sn);
	}

	public interface BooleanCondition {
		boolean isTrueFor(StepTransformable sn);
	}

	public static final BooleanCondition squareRoot = new BooleanCondition() {
		@Override
		public boolean isTrueFor(StepTransformable sn) {
			if (sn instanceof StepExpression) {
				return ((StepExpression) sn).containsSquareRoot();
			}

			return false;
		}
	};

	public static final BooleanCondition abs = new BooleanCondition() {
		@Override
		public boolean isTrueFor(StepTransformable sn) {
			return sn.isOperation(Operation.ABS);
		}
	};

	private static final Condition underAbs = new Condition() {
		@Override
		public StepTransformable isTrueFor(StepTransformable sn) {
			if (sn.isOperation(Operation.ABS)) {
				return ((StepOperation) sn).getOperand(0);
			}
			return null;
		}
	};

	private static final Condition underEvenRoot = new Condition() {
		@Override
		public StepTransformable isTrueFor(StepTransformable sn) {
			if (sn.isOperation(Operation.NROOT) && ((StepOperation) sn).getOperand(1)
					.isEven()) {
				return ((StepOperation) sn).getOperand(0);
			}
			return null;
		}
	};

	private static final Condition plusminusToPlus = new Condition() {
		@Override
		public StepTransformable isTrueFor(StepTransformable sn) {
			if (sn.isOperation(Operation.PLUSMINUS)) {
				return ((StepOperation) sn).getOperand(0);
			}
			return null;
		}
	};

	private static final Condition plusminusToMinus = new Condition() {
		@Override
		public StepTransformable isTrueFor(StepTransformable sn) {
			if (sn.isOperation(Operation.PLUSMINUS)) {
				return minus(((StepOperation) sn).getOperand(0));
			}
			return null;
		}
	};

	private static final Condition isDenominator = new Condition() {
		@Override
		public StepTransformable isTrueFor(StepTransformable sn) {
			if (sn.isOperation(Operation.DIVIDE)) {
				return ((StepOperation) sn).getOperand(1);
			}
			return null;
		}
	};

	/**
	 * Finds the first expression which adheres to the condition
	 *
	 * @param sn expression to search
	 * @param c  condition
	 * @return the first expression which adheres to the condition
	 */
	private static StepExpression findExpression(StepExpression sn, Condition c) {
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

	private static void getAll(StepTransformable sn, Set<StepExpression> values, Condition c) {
		StepTransformable value = c.isTrueFor(sn);
		if (value != null) {
			values.add((StepExpression) value);
		}

		if (sn instanceof StepOperation) {
			for (StepExpression operand : (StepOperation) sn) {
				getAll(operand, values, c);
			}
		}

		if (sn instanceof StepSolvable) {
			getAll(((StepSolvable) sn).LHS, values, c);
			getAll(((StepSolvable) sn).RHS, values, c);
		}
	}

	private static StepNode replaceFirst(StepTransformable sn, Condition c) {
		StepTransformable value = c.isTrueFor(sn);
		if (value != null) {
			return value;
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

			return found ? StepOperation.create(so.getOperation(), result) : null;
		}

		if (sn instanceof StepSolvable) {
			StepSolvable ss = (StepSolvable) sn;

			StepExpression lhs = (StepExpression) replaceFirst(ss.LHS, c);
			if (lhs != null) {
				return ss.cloneWith(lhs, ss.RHS);
			}

			StepExpression rhs = (StepExpression) replaceFirst(ss.RHS, c);
			if (rhs != null) {
				return ss.cloneWith(ss.LHS, rhs);
			}
		}

		return null;
	}

	public static void getAbsoluteValues(StepTransformable sn, Set<StepExpression> absoluteValues) {
		getAll(sn, absoluteValues, underAbs);
	}

	public static void getDenominators(StepTransformable sn, Set<StepExpression> denominators) {
		getAll(sn, denominators, isDenominator);
	}

	public static void getRoots(StepTransformable sn, Set<StepExpression> roots) {
		getAll(sn, roots, underEvenRoot);
	}

	public static StepOperation findTrigonometricExpression(StepExpression se,
			final StepVariable sv) {
		return (StepOperation) findExpression(se, new Condition() {
			@Override
			public StepTransformable isTrueFor(StepTransformable sn) {
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

	private static StepExpression nthDegreeInExpression(StepSolvable ss, final StepVariable var,
			int n) {
		StepExpression diff = StepNode.subtract(ss.LHS, ss.RHS).regroup();

		StepExpression expr = findExpression(diff, new Condition() {
			@Override
			public StepTransformable isTrueFor(StepTransformable sn) {
				if (sn instanceof StepOperation) {
					StepOperation so = (StepOperation) sn;

					if (so.isOperation(Operation.ABS) || so.isOperation(Operation.NROOT)
							|| so.isOperation(Operation.DIVIDE)
									&& so.getOperand(0).isConstantIn(var)
							|| (so.isTrigonometric() || so.isInverseTrigonometric())
									&& !so.isConstantIn(var)) {
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
		return (StepSolvable) replaceFirst(ss, plusminusToPlus);
	}

	public static StepSolvable replaceWithMinus(StepSolvable ss) {
		return (StepSolvable) replaceFirst(ss, plusminusToMinus);
	}

	/**
	 * @param sn expression tree to traverse
	 * @return sum of all the subexpressions containing op
	 */
	public static StepExpression getAll(StepExpression sn, BooleanCondition c) {
		if (sn.isOperation(Operation.PLUS)) {
			StepOperation so = (StepOperation) sn;

			StepExpression roots = null;
			for (StepExpression operand : so) {
				roots = StepNode.add(roots, getAll(operand, c));
			}
			return roots;
		}

		if (c.isTrueFor(sn)) {
			return sn;
		}

		return null;
	}

	/**
	 * @param sn expression tree to traverse
	 * @return part of the expression tree, which doesn't contain roots
	 */
	public static StepExpression getNon(StepExpression sn, BooleanCondition c) {
		return StepNode.subtract(sn, getAll(sn, c)).regroup();
	}

	/**
	 * @param sn expression tree to traverse
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
				return minus(getOne(so, op));
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

		return sn.isConstant() && !isZero(sn);
	}

	public static StepExpression swapAbsInTree(StepExpression se, StepLogical sl,
			StepVariable variable, SolutionBuilder steps, int[] colorTracker) {
		if (se instanceof StepOperation && sl instanceof StepInterval) {
			StepOperation so = (StepOperation) se;
			StepInterval si = (StepInterval) sl;

			if (so.isOperation(Operation.ABS)) {
				StepExpression underAbs = so.getOperand(0);
				underAbs.setColor(++colorTracker[0]);

				if (isNegative(underAbs, si.getLeftBound(), si.getRightBound(), variable)) {
					StepExpression result = minus(so.getOperand(0));
					result.setColor(colorTracker[0]);
					steps.add(SolutionStepType.IS_NEGATIVE_IN, underAbs, sl);
					return result;
				}

				steps.add(SolutionStepType.IS_POSITIVE_IN, underAbs, sl);
				return underAbs;
			}

			StepExpression[] result = new StepExpression[so.noOfOperands()];
			for (int i = 0; i < so.noOfOperands(); i++) {
				result[i] = swapAbsInTree(so.getOperand(i), si, variable, steps, colorTracker);
			}
			return StepOperation.create(so.getOperation(), result);
		}

		return se;
	}

	public static boolean isNegative(StepExpression x, StepExpression a, StepExpression b,
			StepVariable variable) {
		StepExpression evaluateAt;

		if (Double.isInfinite(a.getValue()) && a.getValue() < 0) {
			evaluateAt = StepNode.subtract(b, 10);
		} else if (Double.isInfinite(b.getValue()) && b.getValue() > 0) {
			evaluateAt = StepNode.add(a, 10);
		} else {
			evaluateAt = divide(StepNode.add(a, b), 2);
		}

		return x.getValueAt(variable, evaluateAt.getValue()) < 0;
	}

	public static String getAssumptions(StepExpression sn, String s) {
		if (sn instanceof StepArbitraryInteger) {
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

	public static List<StepNode> getCASSolutions(StepEquation se, StepVariable variable,
			Kernel kernel) throws CASException {
		try {
			String s = kernel.evaluateCachedGeoGebraCAS("Solutions(" + se + ", " + variable + ")",
					null);
			MyList solutionList = (MyList) kernel.getParser().parseGeoGebraExpression(s).unwrap();

			List<StepNode> solutions = new ArrayList<>();
			for (int i = 0; i < solutionList.getLength(); i++) {
				solutions.add(StepNode.convertExpression(solutionList.getListElement(i)));
			}

			return solutions;
		} catch (ParseException e) {
			Log.debug(e);
			return new ArrayList<>();
		}
	}

	public static StepExpression lcm(StepExpression a, StepExpression b) {
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
			constant = multiply(integerA, integerB);
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
				result = nonTrivialProduct(result,
						nonTrivialPower(aBases.get(i), aExponents.get(i)));
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

	public static StepExpression gcd(StepExpression a, StepExpression b) {
		if (isZero(a)) {
			return b;
		}

		if (isZero(b)) {
			return a;
		}

		return weakGCD(a.factor(), b.factor());
	}

	public static SolutionTable createSignTable(StepVariable variable, List<StepExpression> roots,
			List<StepExpression> expressions) {
		StepExpression[] header = new StepExpression[1 + roots.size()];
		header[0] = variable;
		for (int i = 0; i < roots.size(); i++) {
			header[i + 1] = roots.get(i);
		}

		SolutionTable table = new SolutionTable(header);

		for (StepExpression expression : expressions) {
			List<HasLaTeX> row = new ArrayList<>();
			row.add(expression);
			for (int i = 0; i < roots.size(); i++) {
				double value = expression.getValueAt(variable, roots.get(i).getValue());
				if (StepNode.isEqual(value, 0)) {
					row.add(TableElement.ZERO);
				} else if (value < 0) {
					row.add(TableElement.NEGATIVE);
				} else {
					row.add(TableElement.POSITIVE);
				}

				if (i == roots.size() - 1) {
					break;
				}

				if (StepHelper.isNegative(expression, roots.get(i), roots.get(i + 1), variable)) {
					row.add(TableElement.NEGATIVE);
				} else {
					row.add(TableElement.POSITIVE);
				}
			}
			table.rows.add(row);
		}

		return table;
	}

	public static void addInequalityRow(SolutionTable table, StepExpression numerator,
			StepExpression denominator) {
		List<HasLaTeX> newRow = new ArrayList<>();

		StepExpression expression = divide(numerator, denominator);
		newRow.add(expression);

		for (int j = 1; j < table.rows.get(0).size(); j++) {
			boolean isInvalid = false;
			boolean isZero = false;
			boolean isNegative = false;

			for (List<HasLaTeX> row : table.rows) {
				if (row.get(j) == TableElement.ZERO) {
					if (denominator != null
							&& denominator.containsExpression((StepExpression) row.get(0))) {
						isInvalid = true;
					}
					isZero = true;
				} else if (row.get(j) == TableElement.NEGATIVE) {
					isNegative = !isNegative;
				}
			}

			StepNode value;
			if (j % 2 == 0) {
				value = new StepInterval((StepExpression) table.header[j / 2],
						(StepExpression) table.header[j / 2 + 1], false, false);
			} else {
				value = (StepNode) table.header[j / 2 + 1];
			}

			SolutionStepType type;
			if (isInvalid) {
				newRow.add(TableElement.INVALID);
				type = SolutionStepType.IS_INVALID_IN;
			} else if (isZero) {
				newRow.add(TableElement.ZERO);
				type = SolutionStepType.IS_ZERO_IN;
			} else if (isNegative) {
				newRow.add(TableElement.NEGATIVE);
				type = SolutionStepType.IS_NEGATIVE_IN_INEQUALITY;
			} else {
				newRow.add(TableElement.POSITIVE);
				type = SolutionStepType.IS_POSITIVE_IN_INEQUALITY;
			}

			if (!value.equals(StepConstant.POS_INF) && !value.equals(StepConstant.NEG_INF)) {
				table.addSubStep(new SolutionLine(type, expression, value));
			}
		}

		table.rows.add(newRow);
	}

	public static List<StepSolution> readSolution(SolutionTable table, StepInequality si,
			StepVariable variable, SolveTracker tracker) {
		List<StepInterval> intervals = new ArrayList<>();

		List<HasLaTeX> row = table.rows.get(table.rows.size() - 1);
		for (int i = 2; i < row.size(); i += 2) {
			if (si.isLessThan() == (row.get(i) == TableElement.NEGATIVE)) {
				StepExpression left = (StepExpression) table.header[i / 2];
				StepExpression right = (StepExpression) table.header[i / 2 + 1];

				intervals.add(new StepInterval(left, right,
						!si.isStrong() && row.get(i - 1) == TableElement.ZERO,
						!si.isStrong() && row.get(i + 1) == TableElement.ZERO));
			}
		}

		for (int i = 0; i < intervals.size() - 1; i++) {
			if (intervals.get(i).getRightBound().equals(intervals.get(i + 1).getLeftBound())
					&& intervals.get(i).isClosedRight()) {
				intervals.set(i, new StepInterval(intervals.get(i).getLeftBound(),
						intervals.get(i + 1).getRightBound(), intervals.get(i).isClosedLeft(),
						intervals.get(i + 1).isClosedRight()));
				intervals.remove(i + 1);
				i--;
			}
		}

		List<StepSolution> solutions = new ArrayList<>();

		for (StepInterval interval : intervals) {
			solutions.add(StepSolution.simpleSolution(variable, interval, tracker));
		}

		return solutions;
	}
}
