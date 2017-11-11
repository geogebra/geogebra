package org.geogebra.common.kernel.stepbystep.steptree;

import org.geogebra.common.kernel.CASException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.common.kernel.stepbystep.CASConflictException;
import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steps.StepStrategies;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.Operation;

public class StepEquation extends StepNode {

	private StepExpression LHS;
	private StepExpression RHS;

	private StepInterval restriction;
	private StepSet undefinedPoints;

	private int arbConstTracker;

	private boolean flipped;
	private boolean shouldCheckSolutions;

	public StepEquation(StepExpression LHS, StepExpression RHS) {
		this.LHS = LHS;
		this.RHS = RHS;
	}

	public StepEquation(String str, Parser parser) {
		String[] sides = str.split("=");

		this.LHS = (StepExpression) getStepTree(sides[0], parser);
		this.RHS = (StepExpression) getStepTree(sides[1], parser);
	}

	public StepExpression getLHS() {
		return LHS;
	}

	public StepExpression getRHS() {
		return RHS;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((LHS == null) ? 0 : LHS.hashCode());
		result = prime * result + ((RHS == null) ? 0 : RHS.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object sn) {
		if (sn instanceof StepEquation) {
			StepEquation se = (StepEquation) sn;
			return LHS.equals(se.LHS) && RHS.equals(se.RHS);
		}

		return false;
	}

	@Override
	public StepEquation deepCopy() {
		StepEquation newEq = new StepEquation(LHS.deepCopy(), RHS.deepCopy());
		newEq.restriction = restriction == null ? null : restriction.deepCopy();
		newEq.undefinedPoints = undefinedPoints == null ? null : undefinedPoints.deepCopy();
		newEq.arbConstTracker = arbConstTracker;
		newEq.flipped = flipped;

		return newEq;
	}

	@Override
	public String toString() {
		return LHS + " = " + RHS;
	}

	@Override
	public String toLaTeXString(Localization loc, boolean colored) {
		if (colored && color != 0) {
			return "\\fgcolor{" + getColorHex() + "}{" + toLaTeXString(loc, false) + "}";
		}

		if (flipped) {
			return LHS.toLaTeXString(loc, colored) + " = " + RHS.toLaTeXString(loc, colored);
		}
		return RHS.toLaTeXString(loc, colored) + " = " + LHS.toLaTeXString(loc, colored);
	}

	public boolean isValid(StepVariable var, double val) {
		return isEqual(LHS.getValueAt(var, val), RHS.getValueAt(var, val));
	}

	public void setRestriction(StepInterval restriction) {
		this.restriction = restriction;
	}

	public void addUndefinedPoint(StepExpression point) {
		if (undefinedPoints == null) {
			undefinedPoints = new StepSet(point);
		} else {
			undefinedPoints.addElement(point);
		}
	}

	public StepInterval getRestriction() {
		return restriction;
	}

	public StepSet getUndefinedPoints() {
		return undefinedPoints;
	}

	public boolean shouldCheck() {
		return shouldCheckSolutions;
	}

	public void swapSides() {
		flipped = !flipped;

		StepExpression temp = RHS;
		RHS = LHS;
		LHS = temp;
	}

	public StepArbitraryConstant getNextArbInt() {
		return new StepArbitraryConstant("k", ++arbConstTracker, StepArbitraryConstant.ConstantType.INTEGER);
	}

	public StepSet solve(StepVariable sv, SolutionBuilder sb) {
		return (StepSet) StepStrategies.defaultSolve(this, sv, sb);
	}

	public StepSet solveAndCompareToCAS(Kernel kernel, StepVariable sv, SolutionBuilder sb) throws CASException {
		StepSet solutions = solve(sv, sb);

		for (StepNode solution : solutions) {
			if (solution instanceof StepExpression) {
				String casCommand = "CorrectSolution(" + LHS + ", " + RHS + ", " + sv + " = " + solution + ")";
				String withAssumptions = StepHelper.getAssumptions(add((StepExpression) solution, add(LHS, RHS)),
						casCommand);

				String result = kernel.evaluateCachedGeoGebraCAS(withAssumptions, null);

				if (!"true".equals(result)) {
					StepSet CASSolutions = StepHelper.getCASSolutions(this, sv, kernel);
					throw new CASConflictException(sb.getSteps(), solutions, CASSolutions);
				}
			}
		}

		return solutions;
	}

	public StepEquation regroup() {
		return regroup(null);
	}

	public StepEquation regroup(SolutionBuilder sb) {
		cleanColors();
		StepEquation temp = (StepEquation) StepStrategies.defaultRegroup(this, sb);

		LHS = temp.getLHS();
		RHS = temp.getRHS();

		return temp;
	}

	public StepEquation expand() {
		return expand(null);
	}

	public StepEquation expand(SolutionBuilder sb) {
		cleanColors();
		StepEquation temp = (StepEquation) StepStrategies.defaultExpand(this, sb);

		LHS = temp.getLHS();
		RHS = temp.getRHS();

		return temp;
	}

	public StepEquation factor() {
		return factor(null);
	}

	public StepEquation factor(SolutionBuilder sb) {
		cleanColors();
		StepEquation temp = (StepEquation) StepStrategies.defaultFactor(this, sb);

		LHS = temp.getLHS();
		RHS = temp.getRHS();

		return temp;
	}

	public void addStep(SolutionBuilder steps) {
		steps.add(SolutionStepType.EQUATION, this);
	}

	public void modify(StepExpression newLHS, StepExpression newRHS) {
		LHS = newLHS;
		RHS = newRHS;
	}

	public void add(StepExpression toAdd, SolutionBuilder steps) {
		if (!isZero(toAdd)) {
			toAdd.setColor(1);

			LHS = add(LHS, toAdd);
			RHS = add(RHS, toAdd);

			steps.add(SolutionStepType.WRAPPER);
			steps.levelDown();
			steps.add(SolutionStepType.ADD_TO_BOTH_SIDES, toAdd);
			steps.levelDown();
			addStep(steps);

			regroup(steps);
			steps.levelUp();
			addStep(steps);
			steps.levelUp();
		}
	}

	public void subtract(StepExpression toSubtract, SolutionBuilder steps) {
		if (!isZero(toSubtract)) {
			toSubtract.setColor(1);

			LHS = subtract(LHS, toSubtract);
			RHS = subtract(RHS, toSubtract);

			steps.add(SolutionStepType.WRAPPER);
			steps.levelDown();
			steps.add(SolutionStepType.SUBTRACT_FROM_BOTH_SIDES, toSubtract);
			steps.levelDown();
			addStep(steps);

			regroup(steps);
			steps.levelUp();
			addStep(steps);
			steps.levelUp();
		}
	}

	public void addOrSubtract(StepExpression se, SolutionBuilder steps) {
		if (se == null) {
			return;
		}

		if (se.isNegative()) {
			add(se.negate(), steps);
		} else {
			subtract(se, steps);
		}
	}

	public void multiply(StepExpression toMultiply, SolutionBuilder steps) {
		if (!isOne(toMultiply) && !isZero(toMultiply)) {
			toMultiply.setColor(1);

			if (toMultiply.isConstant()) {
				LHS = StepHelper.multiplyByConstant(toMultiply, LHS);
				RHS = StepHelper.multiplyByConstant(toMultiply, RHS);
			} else {
				LHS = multiply(LHS, toMultiply);
				RHS = multiply(RHS, toMultiply);
			}

			steps.add(SolutionStepType.WRAPPER);
			steps.levelDown();
			steps.add(SolutionStepType.MULTIPLY_BOTH_SIDES, toMultiply);
			steps.levelDown();
			addStep(steps);

			expand(steps);
			steps.levelUp();
			addStep(steps);
			steps.levelUp();
		}
	}

	public void divide(StepExpression toDivide, SolutionBuilder steps) {
		if (!isOne(toDivide) && !isZero(toDivide)) {
			toDivide.setColor(1);

			LHS = divide(LHS, toDivide);
			RHS = divide(RHS, toDivide);

			steps.add(SolutionStepType.WRAPPER);
			steps.levelDown();
			steps.add(SolutionStepType.DIVIDE_BOTH_SIDES, toDivide);
			steps.levelDown();
			addStep(steps);

			expand(steps);
			steps.levelUp();
			addStep(steps);
			steps.levelUp();
		}
	}
	
	public void multiplyOrDivide(StepExpression se, SolutionBuilder steps) {
		if (se == null) {
			return;
		}

		if (se.canBeEvaluated() && isEqual(se.getValue(), -1)) {
			multiply(se, steps);
		} else if (se.isOperation(Operation.DIVIDE)) {
			StepOperation so = (StepOperation) se;
			multiply(StepNode.divide(so.getSubTree(1), so.getSubTree(0)), steps);
		} else {
			divide(se, steps);
		}
	}

	public void reciprocate(SolutionBuilder steps) {
		LHS = StepExpression.reciprocate(LHS);
		RHS = StepExpression.reciprocate(RHS);

		steps.add(SolutionStepType.WRAPPER);
		steps.levelDown();
		steps.add(SolutionStepType.RECIPROCATE_BOTH_SIDES);
		addStep(steps);
		steps.levelUp();
	}

	public void square(SolutionBuilder steps) {
		LHS = power(LHS, 2);
		RHS = power(RHS, 2);

		steps.add(SolutionStepType.WRAPPER);
		steps.levelDown();
		steps.add(SolutionStepType.SQUARE_BOTH_SIDES);
		steps.levelDown();
		addStep(steps);

		expand(steps);
		steps.levelUp();
		addStep(steps);
		steps.levelUp();

		shouldCheckSolutions = true;
	}

	public void nthroot(int root, SolutionBuilder steps) {
		if (root == 0 || root == 1) {
			return;
		} else if (root == 2) {
			steps.add(SolutionStepType.SQUARE_ROOT);
		} else if (root == 3) {
			steps.add(SolutionStepType.CUBE_ROOT);
		} else {
			steps.add(SolutionStepType.NTH_ROOT, new StepConstant(root));
		}

		LHS = StepNode.root(LHS, root);
		if (!isZero(RHS)) {
			RHS = StepNode.root(RHS, root);
		}
	}

	public void replace(StepExpression from, StepExpression to) {
		LHS = LHS.replace(from, to);
		RHS = RHS.replace(from, to);
	}

	public void replace(StepExpression from, StepExpression to, SolutionBuilder steps) {
		LHS = LHS.replace(from, to);
		RHS = RHS.replace(from, to);

		steps.add(SolutionStepType.WRAPPER);
		steps.levelDown();
		steps.add(SolutionStepType.REPLACE_WITH, from, to);
		addStep(steps);
		steps.levelUp();
	}

	public boolean checkSolution(StepExpression solution, StepVariable variable, SolutionBuilder steps) {
		if (restriction != null) {
			if (restriction.contains(solution)) {
				steps.add(SolutionStepType.VALID_SOLUTION_ABS, new StepEquation(variable, solution),
						restriction);
			} else {
				steps.add(SolutionStepType.INVALID_SOLUTION_ABS, new StepEquation(variable, solution),
						restriction);
				return false;
			}
		} else {
			if (isValid(variable, solution.getValue())) {
				steps.add(SolutionStepType.VALID_SOLUTION, new StepEquation(variable, solution));
			} else {
				steps.add(SolutionStepType.INVALID_SOLUTION, new StepEquation(variable, solution));
				return false;
			}
		}

		return true;
	}
}
