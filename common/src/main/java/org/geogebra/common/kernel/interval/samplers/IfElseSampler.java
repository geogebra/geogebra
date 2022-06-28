package org.geogebra.common.kernel.interval.samplers;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.interval.evaluators.DefaultConditionalEvaluator;
import org.geogebra.common.plugin.Operation;

public class IfElseSampler implements ExpressionSampler {

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return node != null && Operation.IF_ELSE.equals(node.getOperation());
	}

	@Override
	public MultiSampler create(ExpressionNode node) {
		MyNumberPair pair = (MyNumberPair) node.getLeft();
		ExpressionNode conditional = pair.getX().wrap();
		List<ConditionalSampler> samplers = Arrays.asList(
				new ConditionalSampler(ifExpression(conditional, pair.getY().wrap())),
				new ConditionalSampler(elseExpression(conditional, node.getRightTree())));
		return new MultiSampler(
				new DefaultConditionalEvaluator(samplers),
				samplers);
	}

	private IntervalConditionalExpression ifExpression(ExpressionNode conditional,
			ExpressionNode body) {
		return new IntervalConditionalExpression(conditional, body);
	}

	private IntervalConditionalExpression elseExpression(ExpressionNode conditional,
			ExpressionNode elseBody) {
		return ifExpression(conditional.negation(), elseBody);
	}
}
