package org.geogebra.common.kernel.stepbystep;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.plugin.Operation;

public class TreeOperations {
	private Kernel kernel;
	
	public TreeOperations(Kernel kernel) {
		this.kernel = kernel;
	}

	public ExpressionValue add(ExpressionValue a, ExpressionValue b) {
		if (a == null) {
			return b;
		}
		if (b == null) {
			return a;
		}

		return new ExpressionNode(kernel, a, Operation.PLUS, b);
	}

	public ExpressionValue subtract(ExpressionValue a, ExpressionValue b) {
		if (b == null) {
			return a;
		}
		if (a == null) {
			return minus(b);
		}

		return new ExpressionNode(kernel, a, Operation.MINUS, b);
	}

	public ExpressionValue minus(ExpressionValue a) {
		if (a == null) {
			return null;
		}

		return multiply(new MyDouble(kernel, -1), a);
	}

	public ExpressionValue multiply(ExpressionValue a, ExpressionValue b) {
		if (a == null) {
			return b;
		}
		if (b == null) {
			return a;
		}

		return new ExpressionNode(kernel, a, Operation.MULTIPLY, b);
	}

	public ExpressionValue multiply(ExpressionValue a, double b) {
		return multiply(a, new MyDouble(kernel, b));
	}

	public ExpressionValue divide(ExpressionValue a, ExpressionValue b) {
		if (b == null) {
			return a;
		}
		if (a == null) {
			return b;
		}

		return new ExpressionNode(kernel, a, Operation.DIVIDE, b);
	}

	public ExpressionValue divide(ExpressionValue a, double b) {
		return divide(a, new MyDouble(kernel, b));
	}

	public ExpressionValue power(ExpressionValue a, String power) {
		if (a == null) {
			return null;
		}

		return new ExpressionNode(kernel, a, Operation.POWER, new MyDouble(kernel, Double.parseDouble(power)));
	}

	public ExpressionValue root(ExpressionValue a, String root) {
		if (a == null) {
			return null;
		}

		return new ExpressionNode(kernel, a, Operation.NROOT, new MyDouble(kernel, Double.parseDouble(root)));
	}
}
