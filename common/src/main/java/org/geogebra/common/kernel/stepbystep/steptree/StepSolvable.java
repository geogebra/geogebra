package org.geogebra.common.kernel.stepbystep.steptree;

import java.util.List;

import org.geogebra.common.kernel.CASException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steps.RegroupTracker;
import org.geogebra.common.kernel.stepbystep.steps.SimplificationStepGenerator;
import org.geogebra.common.kernel.stepbystep.steps.SolveTracker;
import org.geogebra.common.kernel.stepbystep.steps.StepStrategies;
import org.geogebra.common.plugin.Operation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public abstract class StepSolvable extends StepTransformable {
	@SuppressFBWarnings("PA_PUBLIC_MUTABLE_OBJECT_ATTRIBUTE")
	public final StepExpression LHS;
	@SuppressFBWarnings("PA_PUBLIC_MUTABLE_OBJECT_ATTRIBUTE")
	public final StepExpression RHS;

	protected final boolean swapped;

	protected StepSolvable(StepExpression LHS, StepExpression RHS, boolean swapped) {
		this.LHS = LHS;
		this.RHS = RHS;
		this.swapped = swapped;
	}

	protected StepSolvable(StepExpression LHS, StepExpression RHS) {
		this(LHS, RHS, false);
	}

	@Override
	public abstract StepSolvable deepCopy();

	public abstract StepSolvable cloneWith(StepExpression newLHS, StepExpression newRHS);

	public abstract StepSolvable swapSides();

	public int countNonConstOperation(Operation operation, StepVariable variable) {
		return LHS.countNonConstOperation(operation, variable)
				+ RHS.countNonConstOperation(operation, variable);
	}

	public int countOperation(Operation operation) {
		return LHS.countOperation(operation) + RHS.countOperation(operation);
	}

	public StepExpression findCoefficient(StepExpression expr) {
		return subtract(LHS.findCoefficient(expr), RHS.findCoefficient(expr));
	}

	public List<StepSolution> solve(StepVariable sv) {
		return solve(sv, new SolutionBuilder());
	}

	public List<StepSolution> solve(StepVariable sv, SolutionBuilder sb) {
		return solve(sv, sb, new SolveTracker());
	}

	@Override
	public void setColor(int color) {
		this.color = color;
		LHS.setColor(color);
		RHS.setColor(color);
	}

	@Override
	public boolean isOperation(Operation operation) {
		return false;
	}

	@Override
	public boolean contains(Operation op) {
		return LHS.contains(op) || RHS.contains(op);
	}

	public abstract List<StepSolution> solve(StepVariable sv, SolutionBuilder sb,
			SolveTracker tracker);

	public abstract List<StepSolution> solveAndCompareToCAS(Kernel kernel, StepVariable sv,
			SolutionBuilder sb) throws CASException;

	public abstract boolean checkSolution(StepVariable variable, StepExpression value,
			SolutionBuilder steps, SolveTracker tracker);

	public int degree(StepVariable var) {
		int degreeLHS = LHS.degree(var);
		int degreeRHS = RHS.degree(var);

		if (degreeLHS == -1 || degreeRHS == -1) {
			return -1;
		}

		return Math.max(degreeLHS, degreeRHS);
	}

	@Override
	public StepSolvable toSolvable() {
		return this;
	}

	@Override
	public int maxDecimal() {
		return Math.max(LHS.maxDecimal(), RHS.maxDecimal());
	}

	@Override
	public boolean containsFractions() {
		return LHS.containsFractions() || RHS.containsFractions();
	}

	@Override
	public StepSolvable regroup() {
		return (StepSolvable) super.regroup();
	}

	@Override
	public StepSolvable regroup(SolutionBuilder sb) {
		return (StepSolvable) super.regroup(sb);
	}

	@Override
	public StepSolvable adaptiveRegroup(SolutionBuilder sb) {
		return (StepSolvable) super.adaptiveRegroup(sb);
	}

	@Override
	public StepSolvable expand() {
		return (StepSolvable) super.expand();
	}

	@Override
	public StepSolvable expand(SolutionBuilder sb) {
		return (StepSolvable) super.expand(sb);
	}

	@Override
	public StepSolvable factor() {
		return (StepSolvable) super.factor();
	}

	@Override
	public StepSolvable factor(SolutionBuilder sb) {
		return (StepSolvable) super.factor(sb);
	}

	@Override
	public StepSolvable weakFactor(SolutionBuilder sb) {
		return (StepSolvable) super.weakFactor(sb);
	}

	private StepSolvable solverRegroup(SolutionBuilder sb) {
		return StepStrategies.solverRegroup(this, sb);
	}

	public StepSolvable reorganize(SolutionBuilder steps, int eqNum) {
		//move constants to the right
		StepSolvable result = addOrSubtract(LHS.findConstant(), steps, eqNum);

		//move variables to the left
		return result.addOrSubtract(result.RHS.findVariable(), steps, eqNum);
	}

	public StepSolvable add(StepExpression toAdd, SolutionBuilder steps) {
		return add(toAdd, steps, -1);
	}

	public StepSolvable add(StepExpression toAdd, SolutionBuilder steps, int eqNum) {
		if (!isZero(toAdd)) {
			toAdd.setColor(1);
			StepSolvable result = cloneWith(add(LHS, toAdd), add(RHS, toAdd));

			steps.add(SolutionStepType.GROUP_WRAPPER);
			steps.levelDown();
			if (eqNum == -1) {
				steps.add(SolutionStepType.ADD_TO_BOTH_SIDES, toAdd);
			} else {
				steps.add(SolutionStepType.ADD_TO_BOTH_SIDES_NUM, toAdd,
						StepConstant.create(eqNum + 1));
			}
			steps.levelDown();
			steps.add(result);

			toAdd.cleanColors();
			result.cleanColors();

			result = result.solverRegroup(steps);
			steps.levelUp();
			steps.add(result);
			steps.levelUp();

			return result;
		}

		return this;
	}

	public StepSolvable subtract(StepExpression toSubtract, SolutionBuilder steps) {
		return subtract(toSubtract, steps, -1);
	}

	public StepSolvable subtract(StepExpression toSubtract, SolutionBuilder steps, int eqNum) {
		if (!isZero(toSubtract)) {
			toSubtract.setColor(1);
			StepSolvable result = cloneWith(subtract(LHS, toSubtract),
					subtract(RHS, toSubtract));

			steps.add(SolutionStepType.GROUP_WRAPPER);
			steps.levelDown();
			if (eqNum == -1) {
				steps.add(SolutionStepType.SUBTRACT_FROM_BOTH_SIDES, toSubtract);
			} else {
				steps.add(SolutionStepType.SUBTRACT_FROM_BOTH_SIDES_NUM, toSubtract,
						StepConstant.create(eqNum + 1));
			}
			steps.levelDown();
			steps.add(result);

			toSubtract.cleanColors();
			result.cleanColors();

			result = result.solverRegroup(steps);
			steps.levelUp();
			steps.add(result);
			steps.levelUp();

			return result;
		}

		return this;
	}

	public StepSolvable addOrSubtract(StepExpression se, SolutionBuilder steps) {
		return addOrSubtract(se, steps, -1);
	}

	public StepSolvable addOrSubtract(StepExpression se, SolutionBuilder steps, int eqNum) {
		if (se == null) {
			return this;
		}

		if (se.isNegative()) {
			return add(se.negate(), steps, eqNum);
		} else if (se.isSum()) {
			StepSolvable temp = this;
			for (StepExpression operand : (StepOperation) se) {
				if (operand.isNegative()) {
					temp = temp.add(operand.negate(), steps, eqNum);
				} else {
					temp = temp.subtract(operand, steps, eqNum);
				}
			}
			return temp;
		} else {
			return subtract(se, steps, eqNum);
		}
	}

	public StepSolvable multiply(StepExpression toMultiply, SolutionBuilder steps) {
		return multiply(toMultiply, steps, -1);
	}

	public StepSolvable multiply(StepExpression toMultiply, SolutionBuilder steps, int eqNum) {
		if (!isOne(toMultiply) && !isZero(toMultiply)) {
			toMultiply.setColor(1);

			StepSolvable result;
			if (toMultiply.isConstant()) {
				result = cloneWith(multiply(toMultiply, LHS), multiply(toMultiply, RHS));
			} else {
				result = cloneWith(multiply(LHS, toMultiply), multiply(RHS, toMultiply));
			}

			steps.add(SolutionStepType.GROUP_WRAPPER);
			steps.levelDown();
			if (eqNum == -1) {
				steps.add(SolutionStepType.MULTIPLY_BOTH_SIDES, toMultiply);
			} else {
				steps.add(SolutionStepType.MULTIPLY_BOTH_SIDES_NUM, toMultiply,
						StepConstant.create(eqNum + 1));
			}
			steps.levelDown();
			steps.add(result);

			toMultiply.cleanColors();
			result.cleanColors();

			result = result.solverRegroup(steps);
			steps.levelUp();
			steps.add(result);
			steps.levelUp();

			return result;
		}

		return this;
	}

	public StepSolvable divide(StepExpression toDivide, SolutionBuilder steps) {
		return divide(toDivide, steps, -1);
	}

	public StepSolvable divide(StepExpression toDivide, SolutionBuilder steps, int eqNum) {
		if (!isOne(toDivide) && !isZero(toDivide)) {
			toDivide.setColor(1);
			StepSolvable result  = cloneWith(divide(LHS, toDivide), divide(RHS, toDivide));

			steps.add(SolutionStepType.GROUP_WRAPPER);
			steps.levelDown();
			if (eqNum == -1) {
				steps.add(SolutionStepType.DIVIDE_BOTH_SIDES, toDivide);
			} else {
				steps.add(SolutionStepType.DIVIDE_BOTH_SIDES_NUM, toDivide,
						StepConstant.create(eqNum + 1));
			}
			steps.levelDown();
			steps.add(result);

			toDivide.cleanColors();
			result.cleanColors();

			result = result.solverRegroup(steps);
			steps.levelUp();
			steps.add(result);
			steps.levelUp();

			return result;
		}

		return this;
	}

	public StepSolvable multiplyOrDivide(StepExpression se, SolutionBuilder steps) {
		return multiplyOrDivide(se, steps, -1);
	}

	public StepSolvable multiplyOrDivide(StepExpression se, SolutionBuilder steps, int eqNum) {
		if (se == null) {
			return this;
		}

		StepSolvable temp = this;
		if (this instanceof StepInequality && se.canBeEvaluated() && se.getValue() < 0) {
			temp = ((StepInequality) this).flip();
		}

		if (se.canBeEvaluated() && isEqual(se.getValue(), -1)) {
			return temp.multiply(se, steps, eqNum);
		} else if (se.isOperation(Operation.DIVIDE)) {
			return temp.multiply(se.reciprocate(), steps, eqNum);
		} else {
			return temp.divide(se, steps, eqNum);
		}
	}

	public StepSolvable reciprocate(SolutionBuilder steps) {
		StepSolvable result = cloneWith(LHS.reciprocate(), RHS.reciprocate());

		steps.add(SolutionStepType.GROUP_WRAPPER);
		steps.levelDown();
		steps.add(SolutionStepType.RECIPROCATE_BOTH_SIDES);
		steps.add(result);
		steps.levelUp();

		return result;
	}

	public StepSolvable power(long power, SolutionBuilder steps, SolveTracker tracker) {
		StepSolvable result = cloneWith(power(LHS, power), power(RHS, power));

		steps.add(SolutionStepType.GROUP_WRAPPER);
		steps.levelDown();

		if (power == 2) {
			steps.add(SolutionStepType.SQUARE_BOTH_SIDES);
		} else {
			steps.add(SolutionStepType.RAISE_TO_POWER, StepConstant.create(power));
		}

		steps.levelDown();
		steps.add(result);

		result = result.expand(steps);
		steps.levelUp();
		steps.add(result);
		steps.levelUp();

		if (power % 2 == 0) {
			tracker.setShouldCheckSolutions();
		}

		return result;
	}

	public StepSolvable nthroot(long root, SolutionBuilder steps) {
		if (root == 0 || root == 1) {
			return this;
		} else if (root == 2) {
			steps.add(SolutionStepType.SQUARE_ROOT);
		} else if (root == 3) {
			steps.add(SolutionStepType.CUBE_ROOT);
		} else {
			steps.add(SolutionStepType.NTH_ROOT, StepConstant.create(root));
		}

		if (!isZero(RHS)) {
			return cloneWith(root(LHS, root), root(RHS, root));
		} else {
			return cloneWith(root(LHS, root), RHS);
		}
	}

	public StepSolvable replace(StepExpression from, StepExpression to) {
		return cloneWith(LHS.replace(from, to), RHS.replace(from, to));
	}

	public StepSolvable replace(StepExpression from, StepExpression to, SolutionBuilder steps) {
		from.setColor(1);
		to.setColor(1);

		// to color
		StepSolvable original = replace(from, from);
		StepSolvable result = replace(from, to);

		steps.addSubstep(original, result, SolutionStepType.REPLACE_WITH, from, to);

		from.cleanColors();
		to.cleanColors();

		result.cleanColors();
		return result;
	}

	@Override
	public StepTransformable iterateThrough(SimplificationStepGenerator step, SolutionBuilder sb,
			RegroupTracker tracker) {
		StepExpression newLHS = (StepExpression) step.apply(LHS, sb, tracker);
		StepExpression newRHS = (StepExpression) step.apply(RHS, sb, tracker);

		return cloneWith(newLHS, newRHS);
	}
}
