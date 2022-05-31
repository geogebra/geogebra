package org.geogebra.common.kernel.interval;

import java.util.Collections;
import java.util.List;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.plugin.Operation;

public class IfSampler implements ExpressionSampler {

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return node != null && Operation.IF.equals(node.getOperation());
	}

	@Override
	public List<ConditionalSampler> create(ExpressionNode node) {
		return Collections.singletonList(
				new ConditionalSampler(node.getLeftTree(), node.getRightTree()));
	}
}
