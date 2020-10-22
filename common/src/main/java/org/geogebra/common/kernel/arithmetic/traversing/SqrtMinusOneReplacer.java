package org.geogebra.common.kernel.arithmetic.traversing;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.plugin.Operation;

import com.google.j2objc.annotations.Weak;

public 	class SqrtMinusOneReplacer implements Traversing {
	@Weak
	private final Kernel kernel;

	public SqrtMinusOneReplacer(Kernel kernel) {
		this.kernel = kernel;
	}

	@Override
	public ExpressionValue process(ExpressionValue ev) {
		if (ev.isExpressionNode()) {
			ExpressionNode node = ev.wrap();
			ExpressionValue left = node.getLeft();
			if (node.getOperation() == Operation.SQRT
					&& left.isNumberValue() && left.isConstant()) {
				if (left.evaluateDouble() == -1) {
					return kernel.getImaginaryUnit();
				}
			}
		}
		if (ev instanceof Variable
				&& "i".equals(ev.toString(StringTemplate.xmlTemplate))) {
			return kernel.getImaginaryUnit();
		}
		return ev;
	}
}
