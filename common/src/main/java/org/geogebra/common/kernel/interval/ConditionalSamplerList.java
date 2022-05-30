package org.geogebra.common.kernel.interval;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

public class ConditionalSamplerList implements IntervalEvaluatable {
	private List<ConditionalSampler> samplers = new ArrayList<>();
	private GeoFunction function;
	private Operation operation;

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
	 * Process a function with If commands to be ready to sample.
	 * @param function to process.
	 */
	public void process(GeoFunction function) {
		ExpressionNode node = function.getFunctionExpression();
		operation = node.getOperation();
		switch (operation) {
		case IF:
			addSingleIfSampler(node);
			break;
		case IF_ELSE:
			addIfElseSamplers(node);
			break;
		case IF_LIST:
			addIfListSamplers(node);
		default:
			Log.debug("Not supported conditional operation: " + operation);
		}
	}

	private void addSingleIfSampler(ExpressionNode node) {
		samplers.add(new ConditionalSampler(node.getLeftTree(), node.getRightTree()));
	}

	private void addIfElseSamplers(ExpressionNode node) {
		MyNumberPair pair = (MyNumberPair) node.getLeft();
		ExpressionNode conditional = pair.getX().wrap();
		ConditionalSampler ifSampler = new ConditionalSampler(conditional,
				pair.getY().wrap());
		ConditionalSampler elseSampler =
				ConditionalSampler.createNegated(function, conditional, node.getRightTree());
		samplers.add(ifSampler);
		samplers.add(elseSampler);
	}

	private void addIfListSamplers(ExpressionNode node) {
		MyList conditions = (MyList) node.getLeft();
		MyList conditionBodies = (MyList) node.getRight();
		for (int i = 0; i < conditions.size(); i++) {
			addListItemSampler(conditions, conditionBodies, i);
		}
	}

	private void addListItemSampler(MyList conditions, MyList conditionBodies, int i) {
		samplers.add(new ConditionalSampler(conditions.getItem(i).wrap(),
				conditionBodies.getItem(i).wrap()));
	}

	public void forEach(Consumer<? super ConditionalSampler> action) {
		samplers.forEach(action);
	}

	public int size() {
		return samplers.size();
	}

	/**
	 * Refresh all the sampled data for the potentially new function, x and width.
	 * @param x interval.
	 * @param width in pixels.
	 */
	public void update(Interval x, int width) {
		samplers.clear();
		process(function);
		DiscreteSpaceImp aSpace = new DiscreteSpaceImp(x, width);
		updateSpace(aSpace);
	}

	private void updateSpace(DiscreteSpace space) {
		samplers.forEach(sampler -> sampler.setSpace(space));
	}

	@Override
	public IntervalTupleList evaluate(double low, double high) {
		return evaluate(new Interval(low, high));
	}

	@Override
	public IntervalTupleList evaluate(Interval x) {
		IntervalTupleList tuples = new IntervalTupleList();
		for (int i = 0; i < samplers.size(); i++) {
			ConditionalSampler sampler = samplers.get(i);
			IntervalTupleList newPoints = new IntervalTupleList();
			if (sampler.isAccepted(x)) {
				newPoints = sampler.evaluate(x);
				newPoints.setPiece(i);
				tuples.append(newPoints);
			}
		}
		return tuples;
	}

	private boolean isIfList() {
		return Operation.IF_LIST.equals(operation);
	}

	@Override
	public IntervalTupleList evaluate(DiscreteSpace space) {
		IntervalTupleList tuples = new IntervalTupleList();
		for (int i = 0; i < samplers.size(); i++) {
			ConditionalSampler sampler = samplers.get(i);
			IntervalTupleList newPoints = sampler.evaluate(space);
			if (!newPoints.isEmpty()) {
				newPoints.setPiece(i);
				tuples.append(newPoints);
			}
		}

		return tuples;
	}
}
