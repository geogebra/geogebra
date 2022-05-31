package org.geogebra.common.kernel.interval;

import java.util.List;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;

public interface ExpressionSampler {
	boolean isAccepted(ExpressionNode node);

	List<ConditionalSampler> create(ExpressionNode node);
}
