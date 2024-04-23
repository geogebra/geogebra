package org.geogebra.common.kernel.algos;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

public class Spline {
	private final MyList conditions;
	private final MyList funcXs;
	private final MyList funcYs;
	private final FunctionVariable functionVariable;

	public Spline(GeoCurveCartesianND curve) {
		Function fun1 = curve.getFun(0);
		Function fun2 = curve.getFun(1);
		conditions = (MyList) fun1.getExpression().getLeft();
		funcXs = (MyList) fun1.getExpression().getRight();
		funcYs = (MyList) fun2.getExpression().getRight();
		functionVariable = fun1.getFunctionVariable();
	}

	public int size() {
		return funcYs.size();
	}

	public GPoint2D get(double t) {
		functionVariable.set(t);
		GPoint2D p = new GPoint2D();
		for (int i = 0; i < conditions.size() - 1; i++) {
			ExpressionValue condition = conditions.getItem(i);
			if (condition.wrap().evaluateBoolean()) {
				ExpressionValue xExp = funcXs.getItem(i);
				ExpressionValue yExp = funcYs.getItem(i);
				p.setLocation(xExp.evaluateDouble(), yExp.evaluateDouble());
				return p;
			}
		}
		ExpressionValue xExp = funcXs.getItem(conditions.size() - 1);
		ExpressionValue yExp = funcYs.getItem(conditions.size() - 1);
		p.setLocation(xExp.evaluateDouble(), yExp.evaluateDouble());
		return p;

//		return null;
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

	public ExpressionNode multiply(int i, Coords coeffs) {
		ExpressionNode xFun = getFuncX(i);
		ExpressionNode yFun = getFuncY(i);
		ExpressionNode enx, eny;
		if (DoubleUtil.isZero(coeffs.getZ())) {
			enx = xFun.multiply(coeffs.getX());
			eny = yFun.multiply(coeffs.getY());
			enx = enx.plus(eny);
		} else {
			// Normalizing to (a/c)x + (b/c)y + 1 seems to work better
			enx = xFun.multiply(coeffs.getX() / coeffs.getZ());
			eny = yFun.multiply(coeffs.getY() / coeffs.getZ());
			enx = enx.plus(eny).plus(1);
		}

		return enx;
	}
}
