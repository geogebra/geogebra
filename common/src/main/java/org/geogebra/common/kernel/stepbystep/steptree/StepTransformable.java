package org.geogebra.common.kernel.stepbystep.steptree;

import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.solution.SolutionUtils;
import org.geogebra.common.kernel.stepbystep.steps.RegroupTracker;
import org.geogebra.common.kernel.stepbystep.steps.SimplificationStepGenerator;
import org.geogebra.common.kernel.stepbystep.steps.StepStrategies;
import org.geogebra.common.plugin.Operation;

public abstract class StepTransformable extends StepNode {

	protected int color;

	public abstract boolean contains(Operation op);

	public abstract StepTransformable iterateThrough(SimplificationStepGenerator step,
			SolutionBuilder sb, RegroupTracker tracker);

	public abstract StepSolvable toSolvable();

	public abstract boolean isOperation(Operation op);

	/**
	 * @return whether the current node is a trigonometric function
	 */
	public boolean isTrigonometric() {
		return isOperation(Operation.SIN) || isOperation(Operation.COS)
				|| isOperation(Operation.TAN);
	}

	/**
	 * @return whether the current node is an inverse trigonometric function
	 */
	public boolean isInverseTrigonometric() {
		return isOperation(Operation.ARCSIN) || isOperation(Operation.ARCCOS)
				|| isOperation(Operation.ARCTAN);
	}

	StepTransformable regroup() {
		return regroup(null);
	}

	/**
	 * This is the default regroup. Assumes every nonSpecialConstant is an integer.
	 *
	 * @param sb SolutionBuilder for the regroup steps
	 * @return the expression, regrouped
	 */
	public StepTransformable regroup(SolutionBuilder sb) {
		return StepStrategies.defaultRegroup(this, sb);
	}

	public StepTransformable weakRegroup() {
		return StepStrategies.weakRegroup(this, null);
	}

	/**
	 * Numeric regroup. Evaluates expressions like 1/3 and sqrt(2)..
	 *
	 * @param sb SolutionBuilder for the regroup steps
	 * @return the expression, regrouped
	 */
	public StepTransformable numericRegroup(SolutionBuilder sb) {
		return StepStrategies.decimalRegroup(this, sb);
	}

	public StepTransformable convertToFractions(SolutionBuilder sb) {
		return StepStrategies.convertToFraction(this, sb);
	}

	public StepTransformable adaptiveRegroup() {
		return adaptiveRegroup(null);
	}

	public StepTransformable adaptiveRegroup(SolutionBuilder sb) {
		if (0 < maxDecimal() && maxDecimal() < 5 && containsFractions()) {
			StepTransformable temp = convertToFractions(sb);
			return temp.regroup(sb);
		}

		if (maxDecimal() > 0) {
			return numericRegroup(sb);
		}

		return regroup(sb);
	}

	public StepTransformable regroupOutput(SolutionBuilder sb) {
		SolutionBuilder temp = new SolutionBuilder();
		StepTransformable result = adaptiveRegroup(temp);

		sb.addGroup(SolutionStepType.SIMPLIFY, temp, result, this);
		return result;
	}

	/**
	 * @return the expression, regrouped and expanded
	 */
	public StepTransformable expand() {
		return expand(new SolutionBuilder());
	}

	/**
	 * @param sb SolutionBuilder for the expansion steps
	 * @return the expression, regrouped and expanded
	 */
	public StepTransformable expand(SolutionBuilder sb) {
		return StepStrategies.defaultExpand(this, sb);
	}

	public StepTransformable expandOutput(SolutionBuilder sb) {
		SolutionBuilder temp = new SolutionBuilder();
		StepTransformable result = expand(temp);

		sb.addGroup(SolutionStepType.EXPAND, temp, result, this);
		return result;
	}

	/**
	 * @return the expression, factored
	 */
	public StepTransformable factor() {
		return factor(null);
	}

	/**
	 * @param sb SolutionBuilder for the factoring steps
	 * @return the expression, factored
	 */
	public StepTransformable factor(SolutionBuilder sb) {
		return StepStrategies.defaultFactor(this, sb);
	}

	public StepTransformable weakFactor(SolutionBuilder sb) {
		return StepStrategies.weakFactor(this, sb);
	}

	public StepTransformable factorOutput(SolutionBuilder sb) {
		SolutionBuilder temp = new SolutionBuilder();
		StepTransformable result = factor(temp);

		sb.addGroup(SolutionStepType.FACTOR, temp, result, this);
		return result;
	}

	public StepTransformable differentiate() {
		return differentiate(null);
	}

	public StepTransformable differentiate(SolutionBuilder sb) {
		return StepStrategies.defaultDifferentiate(this, sb);
	}

	public StepTransformable differentiateOutput(SolutionBuilder sb) {
		SolutionBuilder temp = new SolutionBuilder();
		StepTransformable result = differentiate(temp);

		sb.addGroup(SolutionStepType.DIFFERENTIATE, temp, result, this);
		return result;
	}

	public abstract int maxDecimal();

	public abstract boolean containsFractions();

	protected String getColorHex() {
		return SolutionUtils.getColorHex(color);
	}

	/**
	 * Recursively sets a color for the tree (i.e. for the root and all of the nodes
	 * under it)
	 *
	 * @param color the color to set
	 */
	public abstract void setColor(int color);

	/**
	 * Sets 0 as the color of the tree
	 */
	public void cleanColors() {
		setColor(0);
	}
}
