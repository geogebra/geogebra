package org.geogebra.common.kernel.interval;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.plugin.Operation;

public class IfFunctionSampler implements IntervalFunctionSampler {

	private List<ConditionalSampler> samplers = new ArrayList<>();
	private GeoFunction function;
	private IntervalTuple range;
	private EuclidianViewBounds evBounds;
	private final DiscreteSpace space;
	private List<IntervalTupleList> results = new ArrayList<>();

	public IfFunctionSampler(GeoFunction function, IntervalTuple range,
			EuclidianViewBounds evBounds) {
		this.function = function;
		this.range = range;
		this.evBounds = evBounds;
		space = new DiscreteSpaceImp(range.x(), evBounds.getWidth());
		extractConditions(function);
	}

	private void extractConditions(GeoFunction function) {
		ExpressionNode node = function.getFunctionExpression();
		Operation operation = node.getOperation();
		switch (operation) {
		case IF:
			addSingleIfSampler(node);
			break;
		case IF_ELSE:
			addIfElseSamplers(node);
			break;
		}
	}

	private void addIfElseSamplers(ExpressionNode node) {

	}

	private void addSingleIfSampler(ExpressionNode node) {
		samplers.add(new ConditionalSampler(function, node.getLeftTree(), node.getRightTree(),
				space));
	}

	@Override
	public IntervalTupleList result() {
		if (samplers.isEmpty()) {
			return IntervalTupleList.emptyList();
		}
		results.clear();
		samplers.forEach(sampler -> results.add(sampler.result()));
		return evaluateOn(range.x());
	}

	@Override
	public IntervalTupleList evaluateOn(Interval x) {
		return evaluateOn(x.getLow(), x.getHigh());
	}

	@Override
	public IntervalTupleList evaluateOn(double low, double high) {
		for (ConditionalSampler sampler: samplers) {
			Interval x = new Interval(low, high);
			if (sampler.isAccepted(x)) {
				return sampler.result();
			}
		}
		return IntervalTupleList.emptyList();
	}

	@Override
	public void update(IntervalTuple range) {

	}

	@Override
	public Interval evaluatedValue(Interval x) {
		return null;
	}

	@Override
	public IntervalTupleList extendDomain(double min, double max) {
		return null;
	}

	@Override
	public void setInterval(double low, double high) {

	}

	@Override
	public GeoFunction getGeoFunction() {
		return function;
	}
}
