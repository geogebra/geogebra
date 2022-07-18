package org.geogebra.common.kernel.interval.samplers;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.evaluators.DiscreteSpace;
import org.geogebra.common.kernel.interval.evaluators.DiscreteSpaceImp;
import org.geogebra.common.kernel.interval.evaluators.IntervalEvaluatable;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;

public class ConditionalSamplerList implements IntervalEvaluatable {
	private MultiSampler multiSampler;
	private GeoFunction function;
	private List<ExpressionSampler> expressionSamplers = Arrays.asList(new IfSampler(),
			new IfElseSampler(),
			new IfListSampler());

	public ConditionalSamplerList(GeoFunction function) {
		this.function = function;
	}

	/**
	 *
	 * @param function the conditional GeoFunction
	 * @param x the x interval to evaluate.
	 * @param width the width of interval in pixels
	 */
	public ConditionalSamplerList(GeoFunction function, Interval x, int width) {
		this(function);
		update(x, width);
	}

	/**
	 * Refresh all the sampled data for the potentially new function, x and width.
	 * @param x interval.
	 * @param width in pixels.
	 */
	public void update(Interval x, int width) {
		DiscreteSpaceImp aSpace = new DiscreteSpaceImp(x, width);
		multiSampler = createSamplers(function.getFunctionExpression());
		updateSpace(aSpace);
	}

	private MultiSampler createSamplers(ExpressionNode node) {
		for (ExpressionSampler sampler: expressionSamplers) {
			if (sampler.isAccepted(node)) {
				return sampler.create(node);
			}
		}
		return null;
	}

	private void updateSpace(DiscreteSpace space) {
		multiSampler.updateSpace(space);
	}

	@Override
	public IntervalTupleList evaluate(double low, double high) {
		return multiSampler.evaluate(new Interval(low, high));
	}

	@Override
	public IntervalTupleList evaluate(Interval x) {
		return multiSampler.evaluate(x);
	}

	@Override
	public IntervalTupleList evaluate(DiscreteSpace space) {
		return multiSampler.evaluate(space);
	}
}
