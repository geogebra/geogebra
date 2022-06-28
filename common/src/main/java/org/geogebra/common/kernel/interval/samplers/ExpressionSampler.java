package org.geogebra.common.kernel.interval.samplers;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;

public interface ExpressionSampler {
	boolean isAccepted(ExpressionNode node);

	MultiSampler create(ExpressionNode node);
}
