package org.geogebra.common.kernel.interval;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.plugin.Operation;

public class IfFunctionSampler implements IntervalFunctionSampler {

	private final ExpressionNode node;
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
		node = function.getFunctionExpression();
		extractConditions(function);
	}

	private void extractConditions(GeoFunction function) {
		Operation operation = node.getOperation();
		switch (operation) {
		case IF:
			addSingleIfSampler();
			break;
		case IF_ELSE:
			addIfElseSamplers();
			break;
		case IF_LIST:
			addIfListSamplers();
		}
	}

	private void addIfListSamplers() {
		MyList conditions = (MyList) node.getLeft();
		MyList conditionBodies = (MyList) node.getRight();
		for (int i = 0; i < conditions.size(); i++) {
			samplers.add(new ConditionalSampler(function, conditions.getItem(i).wrap(),
					conditionBodies.getItem(i).wrap(), space));
		}
	}

	private void addIfElseSamplers() {
		MyNumberPair pair = (MyNumberPair) node.getLeft();
		ExpressionNode conditional = pair.getX().wrap();
		ConditionalSampler ifSampler = new ConditionalSampler(function, conditional,
				pair.getY().wrap(), space);
		ConditionalSampler elseSampler =
				ConditionalSampler.createNegated(function, conditional, node.getRightTree(), space);
		samplers.add(ifSampler);
		samplers.add(elseSampler);
	}

	private void addSingleIfSampler() {
		samplers.add(new ConditionalSampler(function, node.getLeftTree(), node.getRightTree(),
				space));
	}

	@Override
	public IntervalTupleList result() {
		return results.isEmpty() ? IntervalTupleList.emptyList(): results().get(0);
	}

	@Override
	public List<IntervalTupleList> results() {
		results.clear();
		samplers.forEach(sampler -> results.add(sampler.result()));
		return results;
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
		DiscreteSpaceImp aSpace = new DiscreteSpaceImp(range.x(), evBounds.getWidth());
		samplers.forEach(sampler -> sampler.setSpace(aSpace));
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
