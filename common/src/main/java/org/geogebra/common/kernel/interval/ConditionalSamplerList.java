package org.geogebra.common.kernel.interval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.plugin.Operation;

public class ConditionalSamplerList implements IntervalEvaluatable {
	private List<ConditionalSampler> samplers = new ArrayList<>();
	private GeoFunction function;
	private Operation operation;
	private List<ExpressionSampler> expressionSamplers = Arrays.asList(new IfSampler(),
			new IfElseSampler(), new IfListSampler());

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
		samplers = createSamplers(function.getFunctionExpression());
		updateSpace(aSpace);
	}

	private List<ConditionalSampler> createSamplers(ExpressionNode node) {
		for (ExpressionSampler sampler: expressionSamplers) {
			if (sampler.isAccepted(node)) {
				return sampler.create(node);
			}
		}
		return Collections.emptyList();
	}

	private void updateSpace(DiscreteSpace space) {
		samplers.forEach(sampler -> sampler.setSpace(space));
	}

	public void forEach(Consumer<? super ConditionalSampler> action) {
		samplers.forEach(action);
	}

	public int size() {
		return samplers.size();
	}

	@Override
	public IntervalTupleList evaluate(double low, double high) {
		return evaluate(new Interval(low, high));
	}

	@Override
	public IntervalTupleList evaluate(Interval x) {
		for (int i = 0; i < samplers.size(); i++) {
			ConditionalSampler sampler = samplers.get(i);
			IntervalTupleList newPoints = sampler.evaluate(x);
			if (!newPoints.isEmpty()) {
				newPoints.setPiece(i);
				return newPoints;
			}
		}
		return IntervalTupleList.emptyList();
	}

	@Override
	public IntervalTupleList evaluate(DiscreteSpace space) {
		for (int i = 0; i < samplers.size(); i++) {
			ConditionalSampler sampler = samplers.get(i);
			IntervalTupleList newPoints = sampler.evaluate(space);
			if (!newPoints.isEmpty()) {
				newPoints.setPiece(i);
				return newPoints;
			}
		}

		return IntervalTupleList.emptyList();
	}
}
