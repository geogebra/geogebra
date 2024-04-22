package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;

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

	public GeoPoint set(GeoPoint p, double t) {
		functionVariable.set(t);
		for (int i = 0; i < conditions.size(); i++) {
			ExpressionValue condition = conditions.getItem(i);
			if (condition.wrap().evaluateBoolean()) {
				ExpressionValue xExp = funcXs.getItem(i);
				ExpressionValue yExp = funcYs.getItem(i);
				p.setCoords(xExp.evaluateDouble(), yExp.evaluateDouble(), 1);
				return p;
			}
		}
		return p;
	}

}
