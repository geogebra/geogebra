package org.geogebra.common.kernel.arithmetic.traversing;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.Traversing;

public enum ConstantSimplifier implements Traversing, Inspecting {

	INSTANCE;

	@Override
	public boolean check(ExpressionValue ev) {
		return ev.isExpressionNode()
				&& !((ExpressionNode) ev).containsFreeFunctionVariable(null);
	}

	@Override
	public ExpressionValue process(ExpressionValue ev) {
		if (check(ev)) {
			return ev.evaluate(StringTemplate.defaultTemplate);
		}
		return ev;
	}
}
