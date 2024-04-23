package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyList;

public class Spline {
	private final MyList conditions;
	private final MyList funcXs;
	private final MyList funcYs;
	private final FunctionVariable functionVariable;

	public Spline(ExpressionNode fun1, ExpressionNode fun2, FunctionVariable functionVariable) {
		conditions = (MyList) fun1.getLeft();
		funcXs = (MyList) fun1.getRight();
		funcYs = (MyList) fun2.getRight();
		this.functionVariable = functionVariable;
	}

	public int size() {
		return funcYs.size();
	}

	public ExpressionNode getFuncX(int idx) {
		return funcXs.getItem(idx).wrap();
	}

	public ExpressionNode getFuncY(int idx) {
		return funcYs.getItem(idx).wrap();
	}

	public FunctionVariable getFunctionVariable() {
		return functionVariable;
	}

	public boolean isInInterval(double root, int i) {
		return satisfiesCondition(root, i) && !satisfiesCondition(root, i - 1);
	}

	private boolean satisfiesCondition(double root, int i) {
		if (i < 0) {
			return root < 0;
		}
		if (i >= conditions.size()) {
			return root <= 1;
		}
		functionVariable.set(root);
		ExpressionValue condition = conditions.getItem(i);
		return condition.wrap().evaluateBoolean();
	}

}
