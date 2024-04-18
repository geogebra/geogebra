package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.ParametricCurve;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

class PolyCurveParams {
	private Coords coeffs;
	private ExpressionNode enx;
	private ExpressionNode eny;
	ExpressionNode xFun;
	ExpressionNode yFun;
	FunctionVariable functionVariable;
	private double xNorm;
	private double yNorm;

	public PolyCurveParams(ExpressionNode xFun,
			ExpressionNode yFun, FunctionVariable functionVariable, Coords coeffs) {
		this.xFun = xFun;
		this.yFun = yFun;
		this.functionVariable = functionVariable;
		this.coeffs = coeffs;
		if (!DoubleUtil.isZero(coeffs.getZ())) {
			xNorm = coeffs.getX() / coeffs.getZ();
			yNorm = coeffs.getY() / coeffs.getZ();
		}

	}

	public PolyCurveParams(ParametricCurve curve, Coords coeffs) {
		this(curve.getFun(0).getExpression(),
				curve.getFun(1).getExpression(),
				curve.getFun(0).getFunctionVariable(), coeffs);
	}


	public void multiplyWithLine() {
		if (DoubleUtil.isZero(coeffs.getZ())) {
			enx = xFun.multiply(coeffs.getX());
			eny = yFun.multiply(coeffs.getY());
			enx = enx.plus(eny);
		} else {
			// Normalizing to (a/c)x + (b/c)y + 1 seems to work better
			enx = xFun.multiply(xNorm);
			eny = yFun.multiply(yNorm);
			enx = enx.plus(eny).plus(1);
		}


	}

	public ExpressionNode getEnX() {
		return enx;
	}

	public GeoFunction buildFunctionX(FunctionVariable fv) {
		return enx.buildFunction(fv);
	}
}
