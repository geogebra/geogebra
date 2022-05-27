package org.geogebra.common.kernel.interval;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.plugin.Operation;

public class ConditionalSamplerList implements IntervalEvaluation {
	private List<ConditionalSampler> samplers = new ArrayList<>();
	private GeoFunction function;
	private DiscreteSpace space;
	private Operation operation;

	public ConditionalSamplerList(GeoFunction function) {
		this.function = function;
	}

	public ConditionalSamplerList(GeoFunction function, Interval x, int width) {
		this(function);
		update(x, width);
	}

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
		}
	}

	private void addSingleIfSampler(ExpressionNode node) {
		samplers.add(new ConditionalSampler(node.getLeftTree(), node.getRightTree(),
				space));
	}

	private void addIfElseSamplers(ExpressionNode node) {
		MyNumberPair pair = (MyNumberPair) node.getLeft();
		ExpressionNode conditional = pair.getX().wrap();
		ConditionalSampler ifSampler = new ConditionalSampler(conditional,
				pair.getY().wrap(), space);
		ConditionalSampler elseSampler =
				ConditionalSampler.createNegated(function, conditional, node.getRightTree(), space);
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
				conditionBodies.getItem(i).wrap(), space));
	}

	public void forEach(Consumer<? super ConditionalSampler> action) {
		samplers.forEach(action);
	}

	public int size() {
		return samplers.size();
	}

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
	public IntervalTupleList evaluateBetween(double low, double high) {
		return evaluate(new Interval(low, high));
	}

	@Override
	public IntervalTupleList evaluate(Interval x) {
		IntervalTupleList tuples = new IntervalTupleList();
		for (int i = 0; i < samplers.size(); i++) {
			ConditionalSampler sampler = samplers.get(i);
			IntervalTupleList newPoints =
					sampler.isAccepted(x) ? sampler.evaluateOn(x) : IntervalTupleList.emptyList();
			if (isIfList()) {
				return tuples;
			}
			newPoints.setPiece(i);
			tuples.append(newPoints);
		}
		return tuples;
	}

	private boolean isIfList() {
		return Operation.IF_LIST.equals(operation);
	}

	@Override
	public IntervalTupleList evaluateOnSpace(DiscreteSpace space) {
		IntervalTupleList tuples = new IntervalTupleList();
		for (int i = 0; i < samplers.size(); i++) {
			ConditionalSampler sampler = samplers.get(i);
			IntervalTupleList newPoints = sampler.evaluateOnSpace(space);
			if (!newPoints.isEmpty()) {
				if (isIfList()) {
					return tuples;
				}
				newPoints.setPiece(i);
				tuples.append(newPoints);
			}
		}

		return tuples;
	}
}
