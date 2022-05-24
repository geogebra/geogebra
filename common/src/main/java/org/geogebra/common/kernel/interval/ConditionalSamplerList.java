package org.geogebra.common.kernel.interval;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.plugin.Operation;

public class ConditionalSamplerList {
	private List<ConditionalSampler> samplers = new ArrayList<>();
	private GeoFunction function;
	private DiscreteSpace space;

	public ConditionalSamplerList(GeoFunction function) {
		this.function = function;
	}

	public void process(GeoFunction function) {
		ExpressionNode node = function.getFunctionExpression();
		Operation operation = node.getOperation();
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
		samplers.add(new ConditionalSampler(function, node.getLeftTree(), node.getRightTree(),
				space));
	}

	private void addIfElseSamplers(ExpressionNode node) {
		MyNumberPair pair = (MyNumberPair) node.getLeft();
		ExpressionNode conditional = pair.getX().wrap();
		ConditionalSampler ifSampler = new ConditionalSampler(function, conditional,
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
			samplers.add(new ConditionalSampler(function, conditions.getItem(i).wrap(),
					conditionBodies.getItem(i).wrap(), space));
		}
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

	public IntervalTupleList evaluateOn(double low, double high) {
		Interval x = new Interval(low, high);
		IntervalTupleList tuples = new IntervalTupleList();
		for (int i = 0; i < samplers.size(); i++) {
			IntervalTupleList newPoints = sampleIfAccepted(samplers.get(i), x);
			newPoints.setPiece(i);
			tuples.append(newPoints);
		}
		return tuples;
	}

	private IntervalTupleList sampleIfAccepted(ConditionalSampler sampler, Interval x) {
		return sampler.isAccepted(x) ? sampler.evaluateOn(x) : IntervalTupleList.emptyList();
	}

	public IntervalTupleList evaluateOnSpace(DiscreteSpace space) {
		IntervalTupleList tuples = new IntervalTupleList();
		for (int i = 0; i < samplers.size(); i++) {
			ConditionalSampler sampler = samplers.get(i);
			IntervalTupleList newPoints = sampler.evaluateOnSpace(space);
			if (!newPoints.isEmpty()) {
				newPoints.setPiece(i);
				tuples.append(newPoints);
			}

		}
		return tuples;
	}
}
