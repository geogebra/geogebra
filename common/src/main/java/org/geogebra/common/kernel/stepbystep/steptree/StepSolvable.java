package org.geogebra.common.kernel.stepbystep.steptree;

import org.geogebra.common.kernel.CASException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steps.RegroupTracker;
import org.geogebra.common.kernel.stepbystep.steps.SolveTracker;
import org.geogebra.common.kernel.stepbystep.steps.StepStrategies;
import org.geogebra.common.plugin.Operation;

public abstract class StepSolvable extends StepNode {

	protected StepExpression LHS;
	protected StepExpression RHS;
	protected boolean swapped;

	public void swapSides() {
		swapped = !swapped;

		StepExpression temp = RHS;
		RHS = LHS;
		LHS = temp;
	}

	public int countNonConstOperation(Operation operation, StepVariable variable) {
		return LHS.countNonConstOperation(operation, variable) +
				RHS.countNonConstOperation(operation, variable);
	}

	public int countOperation(Operation operation) {
		return LHS.countOperation(operation) + RHS.countOperation(operation);
	}

	public StepExpression findCoefficient(StepExpression expr) {
		return subtract(LHS.findCoefficient(expr), RHS.findCoefficient(expr));
	}

	/**
	 * Solves "variable ? constant" and "constant ? constant" type equations and inequalities
	 * @return the solution - a set containing either a constant StepExpression or a StepInterval
	 */
	public abstract StepSet trivialSolution(StepVariable variable, SolveTracker tracker);

	public StepSet solve(StepVariable sv, SolutionBuilder sb) {
		return solve(sv, sb, new SolveTracker());
	}

	public abstract StepSet solve(StepVariable sv, SolutionBuilder sb, SolveTracker tracker);

	public abstract StepSet solveAndCompareToCAS(Kernel kernel, StepVariable sv, SolutionBuilder sb)
			throws CASException;

	public abstract boolean checkSolution(StepSolution solution, SolutionBuilder sb, SolveTracker tracker);

	public abstract StepSolvable deepCopy();

	public int degree(StepVariable var) {
		int degreeLHS = LHS.degree(var);
		int degreeRHS = RHS.degree(var);

		if (degreeLHS == -1 || degreeRHS == -1) {
			return -1;
		}

		return Math.max(degreeLHS, degreeRHS);
	}

	public StepExpression getLHS() {
		return LHS;
	}

	public StepExpression getRHS() {
		return RHS;
	}

	public StepSolvable regroup() {
		return regroup(null, null);
	}

	private int maxDecimal() {
		return Math.max(LHS.maxDecimal(), RHS.maxDecimal());
	}

	private boolean containsFractions() {
		return LHS.containsFractions() || RHS.containsFractions();
	}

	public StepSolvable regroup(SolutionBuilder sb, SolveTracker tracker) {
		StepSolvable temp = this;

		if (tracker != null && tracker.isApproximate() == null) {
			tracker.setApproximate(maxDecimal() >= 5 || maxDecimal() > 0 && !containsFractions());
			if (!tracker.isApproximate()) {
				temp = (StepSolvable) StepStrategies.convertToFraction(this, sb);
			}
		}

		if (tracker != null && tracker.isApproximate()) {
			temp = (StepSolvable) StepStrategies.decimalRegroup(temp, sb);
		} else {
			temp = (StepSolvable) StepStrategies.defaultRegroup(temp, sb);
		}

		LHS = temp.LHS;
		RHS = temp.RHS;

		return temp;
	}

	public StepSolvable expand() {
		return expand(null, null);
	}

	public StepSolvable expand(SolutionBuilder sb, SolveTracker tracker) {
		StepSolvable temp = this;

		if (tracker != null && tracker.isApproximate() == null) {
			tracker.setApproximate(maxDecimal() >= 5 || maxDecimal() > 0 && !containsFractions());
			if (!tracker.isApproximate()) {
				temp = (StepSolvable) StepStrategies.convertToFraction(this, sb);
			}
		}

		if (tracker != null && tracker.isApproximate()) {
			temp = (StepSolvable) StepStrategies.decimalExpand(temp, sb);
		} else {
			temp = (StepSolvable) StepStrategies.defaultExpand(temp, sb);
		}

		LHS = temp.LHS;
		RHS = temp.RHS;

		return temp;
	}

	public StepSolvable factor() {
		return factor(null);
	}

	public StepSolvable factor(SolutionBuilder sb) {
		cleanColors();
		StepSolvable temp = (StepSolvable) StepStrategies.defaultFactor(this, sb, new RegroupTracker());

		LHS = temp.getLHS();
		RHS = temp.getRHS();

		return temp;
	}

	public void modify(StepExpression newLHS, StepExpression newRHS) {
		LHS = newLHS;
		RHS = newRHS;
	}

	public void add(StepExpression toAdd, SolutionBuilder steps, SolveTracker tracker) {
		if (!isZero(toAdd)) {
			toAdd.setColor(1);

			LHS = add(LHS, toAdd);
			RHS = add(RHS, toAdd);

			steps.add(SolutionStepType.GROUP_WRAPPER);
			steps.levelDown();
			steps.add(SolutionStepType.ADD_TO_BOTH_SIDES, toAdd);
			steps.levelDown();
			steps.add(this);

			regroup(steps, tracker);
			steps.levelUp();
			steps.add(this);
			steps.levelUp();

			toAdd.cleanColors();
		}
	}

	public void subtract(StepExpression toSubtract, SolutionBuilder steps, SolveTracker tracker) {
		if (!isZero(toSubtract)) {
			toSubtract.setColor(1);

			LHS = subtract(LHS, toSubtract);
			RHS = subtract(RHS, toSubtract);

			steps.add(SolutionStepType.GROUP_WRAPPER);
			steps.levelDown();
			steps.add(SolutionStepType.SUBTRACT_FROM_BOTH_SIDES, toSubtract);
			steps.levelDown();
			steps.add(this);

			regroup(steps, tracker);
			steps.levelUp();
			steps.add(this);
			steps.levelUp();

			toSubtract.cleanColors();
		}
	}

	public void addOrSubtract(StepExpression se, SolutionBuilder steps, SolveTracker tracker) {
		if (se == null) {
			return;
		}

		if (se.isNegative()) {
			add(se.negate().deepCopy(), steps, tracker);
		} else {
			subtract(se.deepCopy(), steps, tracker);
		}
	}

	public void multiply(StepExpression toMultiply, SolutionBuilder steps, SolveTracker tracker) {
		if (!isOne(toMultiply) && !isZero(toMultiply)) {
			toMultiply.setColor(1);

			if (toMultiply.isConstant()) {
				LHS = multiply(toMultiply, LHS);
				RHS = multiply(toMultiply, RHS);
			} else {
				LHS = multiply(LHS, toMultiply);
				RHS = multiply(RHS, toMultiply);
			}

			steps.add(SolutionStepType.GROUP_WRAPPER);
			steps.levelDown();
			steps.add(SolutionStepType.MULTIPLY_BOTH_SIDES, toMultiply);
			steps.levelDown();
			steps.add(this);

			regroup(steps, tracker);
			steps.levelUp();
			steps.add(this);
			steps.levelUp();

			toMultiply.cleanColors();
		}
	}

	public void divide(StepExpression toDivide, SolutionBuilder steps, SolveTracker tracker) {
		if (!isOne(toDivide) && !isZero(toDivide)) {
			toDivide.setColor(1);

			LHS = divide(LHS, toDivide);
			RHS = divide(RHS, toDivide);

			steps.add(SolutionStepType.GROUP_WRAPPER);
			steps.levelDown();
			steps.add(SolutionStepType.DIVIDE_BOTH_SIDES, toDivide);
			steps.levelDown();
			steps.add(this);

			regroup(steps, tracker);
			steps.levelUp();
			steps.add(this);
			steps.levelUp();

			toDivide.cleanColors();
		}
	}

	public void multiplyOrDivide(StepExpression se, SolutionBuilder steps, SolveTracker tracker) {
		if (se == null) {
			return;
		}

		if (this instanceof StepInequality && se.canBeEvaluated() && se.getValue() < 0) {
			((StepInequality) this).flip();
		}

		if (se.canBeEvaluated() && isEqual(se.getValue(), -1)) {
			multiply(se.deepCopy(), steps, tracker);
		} else if (se.isOperation(Operation.DIVIDE)) {
			StepOperation so = (StepOperation) se;
			multiply(StepNode.divide(so.getOperand(1), so.getOperand(0)), steps, tracker);
		} else {
			divide(se.deepCopy(), steps, tracker);
		}
	}

	public void reciprocate(SolutionBuilder steps) {
		LHS = LHS.reciprocate();
		RHS = RHS.reciprocate();

		steps.add(SolutionStepType.GROUP_WRAPPER);
		steps.levelDown();
		steps.add(SolutionStepType.RECIPROCATE_BOTH_SIDES);
		steps.add(this);
		steps.levelUp();
	}

	public void square(SolutionBuilder steps, SolveTracker tracker) {
		LHS = power(LHS, 2);
		RHS = power(RHS, 2);

		steps.add(SolutionStepType.GROUP_WRAPPER);
		steps.levelDown();
		steps.add(SolutionStepType.SQUARE_BOTH_SIDES);
		steps.levelDown();
		steps.add(this);

		expand(steps, tracker);
		steps.levelUp();
		steps.add(this);
		steps.levelUp();

		tracker.setShouldCheckSolutions();
	}

	public void nthroot(long root, SolutionBuilder steps) {
		if (root == 0 || root == 1) {
			return;
		} else if (root == 2) {
			steps.add(SolutionStepType.SQUARE_ROOT);
		} else if (root == 3) {
			steps.add(SolutionStepType.CUBE_ROOT);
		} else {
			steps.add(SolutionStepType.NTH_ROOT, StepConstant.create(root));
		}

		LHS = StepNode.root(LHS, root);
		if (!isZero(RHS)) {
			RHS = StepNode.root(RHS, root);
		}
	}

	public StepSolvable replace(StepExpression from, StepExpression to) {
		LHS = LHS.replace(from, to);
		RHS = RHS.replace(from, to);

		return this;
	}

	public void replace(StepExpression from, StepExpression to, SolutionBuilder steps) {
		LHS = LHS.replace(from, to);
		RHS = RHS.replace(from, to);

		steps.add(SolutionStepType.GROUP_WRAPPER);
		steps.levelDown();
		steps.add(SolutionStepType.REPLACE_WITH, from, to);
		steps.add(this);
		steps.levelUp();
	}
}
