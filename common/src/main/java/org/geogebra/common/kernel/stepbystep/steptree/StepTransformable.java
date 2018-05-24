package org.geogebra.common.kernel.stepbystep.steptree;

import org.geogebra.common.kernel.stepbystep.StepsCache;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steps.StepStrategies;

public abstract class StepTransformable extends StepNode {

	public abstract StepSolvable toSolvable();

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
		return StepsCache.getInstance().regroup(this, sb);
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
		sb.add(SolutionStepType.SIMPLIFY, this);
		return adaptiveRegroup(sb);
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
		return StepsCache.getInstance().expand(this, sb);
	}

	public StepTransformable expandOutput(SolutionBuilder sb) {
		sb.add(SolutionStepType.EXPAND, this);
		return expand(sb);
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
		return StepsCache.getInstance().factor(this, sb);
	}

	public StepTransformable weakFactor(SolutionBuilder sb) {
		return StepStrategies.weakFactor(this, sb);
	}

	public StepTransformable factorOutput(SolutionBuilder sb) {
		sb.add(SolutionStepType.FACTOR, this);
		return factor(sb);
	}

	public StepTransformable differentiate() {
		return differentiate(null);
	}

	public StepTransformable differentiate(SolutionBuilder sb) {
		return StepStrategies.defaultDifferentiate(this, sb);
	}

	public StepTransformable differentiateOutput(SolutionBuilder sb) {
		sb.add(SolutionStepType.DIFFERENTIATE, this);
		return differentiate(sb);
	}

	public abstract int maxDecimal();

	public abstract boolean containsFractions();

}
