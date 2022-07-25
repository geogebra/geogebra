package org.geogebra.common.kernel.interval.evaluators;

import java.util.List;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.function.IntervalFunction;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;
import org.geogebra.common.kernel.interval.samplers.ConditionalSampler;

public class IfListElseEvaluator implements IntervalEvaluatable {

	private final IfListEvaluator evaluator;
	private final ExpressionNode elseNode;
	private int piece;

	/**
	 *
	 * @param samplers the ConditionSampler list.
	 * @param elseNode the else body block.
	 * @param piece that the tuples evaluated as else case will belong to.
	 */
	public IfListElseEvaluator(List<ConditionalSampler> samplers, ExpressionNode elseNode,
			int piece) {
		evaluator = new IfListEvaluator(samplers);
		this.elseNode = elseNode;
		this.piece = piece;
	}

	@Override
	public IntervalTupleList evaluate(Interval x) {
		IntervalTupleList tuples = evaluator.evaluate(x);
		if (tuples.isEmpty()) {
			return evaluateElse(x);
		}
		return tuples;
	}

	private IntervalTupleList evaluateElse(Interval x) {
		return new IntervalTupleList(
				new IntervalTuple(x, IntervalFunction.evaluate(x, elseNode), piece));
	}

	@Override
	public IntervalTupleList evaluate(double low, double high) {
		return evaluate(new Interval(low, high));
	}

	@Override
	public IntervalTupleList evaluate(DiscreteSpace space) {
		IntervalTupleList result = new IntervalTupleList();
		space.values().forEach(x -> result.append(evaluate(x)));
		return result;
	}
}
