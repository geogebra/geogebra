package org.geogebra.common.kernel.interval;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.plugin.Operation;

public class IfFunctionSampler implements IntervalFunctionSampler {

	private List<ConditionalSampler> samplers = new ArrayList<>();
	private GeoFunction function;
	private IntervalTuple range;
	private EuclidianViewBounds evBounds;
	private final DiscreteSpace space;

	private ConditionalSampler acceptedSampler = null;

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
		if (Operation.IF_ELSE.equals(node.getOperation())) {
			addIfElseCondition(node);
		}
	}

	private void addIfElseCondition(ExpressionNode node) {
		MyNumberPair pair = (MyNumberPair) node.getLeft();
		samplers.add(new ConditionalSampler(function, pair, space));
	}

	@Override
	public IntervalTupleList result() {
		return getAcceptedSampler().result();
	}

	private IntervalFunctionSampler getAcceptedSampler() {
		return acceptedSampler == null ? samplers.get(0) : acceptedSampler;
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
				return acceptedSampler.result();
			}
		}
		return IntervalTupleList.emptyList();
	}

	@Override
	public void evaluate() {

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

	private boolean evaluateBoolean(Interval x, ExpressionNode condition) {
		Interval left = IntervalFunction.evaluate(x, condition.getLeft());
		Operation operation = condition.getOperation();
		ExpressionValue value = condition.getRight();
		switch (operation) {
		case LESS:
			return left.isLessThan(IntervalFunction.evaluate(x, value));
		case GREATER:
			return left.isGreaterThan(IntervalFunction.evaluate(x, value));
		}
		return true;
	}

}
