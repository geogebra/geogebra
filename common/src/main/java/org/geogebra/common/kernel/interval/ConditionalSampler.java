package org.geogebra.common.kernel.interval;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.plugin.Operation;

public class ConditionalSampler implements IntervalFunctionSampler {
	private final GeoFunction function;
	private final ExpressionNode condition;
	private final ExpressionNode conditionBody;
	private Interval x = IntervalConstants.undefined();
	private DiscreteSpace space;
	private IntervalTupleList samples;

	public ConditionalSampler(GeoFunction function, MyNumberPair pair, DiscreteSpace space) {
		this.function = function;
		this.condition = pair.getX().wrap();
		this.conditionBody = pair.getY().wrap();
		this.space = space;
	}

	public boolean isAccepted(Interval x) {
		if (isConditionTrue(x)) {
			this.x = x;
			return true;
		}

		return false;
	}

	private boolean isConditionTrue(Interval x) {
		Interval left = IntervalFunction.evaluate(x, condition.getLeft());
		Operation operation = condition.getOperation();
		ExpressionValue value = condition.getRight();
		switch (operation) {
		case LESS:
			return left.isLessThan(IntervalFunction.evaluate(x, value));
		case GREATER:
			return left.isGreaterThan(IntervalFunction.evaluate(x, value));
		}
		return false;
	}

	@Override
	public IntervalTupleList result() {
		if (x.isUndefined()) {
			return IntervalTupleList.emptyList();
		}
		return samples;
	}

	@Override
	public void evaluate() {
		samples = new IntervalTupleList();
		space.values().filter(x -> isConditionTrue(x)).forEach(x -> {
			IntervalTuple tuple = new IntervalTuple(x, evaluatedValue(x));
			samples.add(tuple);
		});
	}

	@Override
	public IntervalTupleList evaluateOn(Interval x) {
		return evaluateOn(x.getLow(), x.getHigh());
	}

	@Override
	public Interval evaluatedValue(Interval x) {
		return IntervalFunction.evaluate(x, conditionBody);
	}

	@Override
	public IntervalTupleList evaluateOn(double low, double high) {
		return null;
	}

	@Override
	public void update(IntervalTuple range) {

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
