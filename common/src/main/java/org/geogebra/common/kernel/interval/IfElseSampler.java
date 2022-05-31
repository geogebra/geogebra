package org.geogebra.common.kernel.interval;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.plugin.Operation;

public class IfElseSampler implements ExpressionSampler {
	@Override
	public boolean isAccepted(ExpressionNode node) {
		return node != null && Operation.IF_ELSE.equals(node.getOperation());
	}

	@Override
	public List<ConditionalSampler> create(ExpressionNode node) {
		MyNumberPair pair = (MyNumberPair) node.getLeft();
		ExpressionNode conditional = pair.getX().wrap();
		ConditionalSampler ifSampler = new ConditionalSampler(conditional,
				pair.getY().wrap());
		ConditionalSampler elseSampler =
				ConditionalSampler.createNegated(conditional, node.getRightTree());
		return Arrays.asList(ifSampler, elseSampler);
	}
}
