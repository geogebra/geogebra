package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

public final class IntersectPolyCurvesAndLine {
	private final Kernel kernel;
	private final Coords coefficients;
	private final Spline spline;


//	private void updatePoint(FunctionVariable functionVariable,
//				AlgoElement.OutputHandler<GeoPointND> outputPoints) {
//			outputPoints.adjustOutputSize(roots.size());
//			for (int index = 0; index < roots.size(); index++) {
//				GeoPointND point = outputPoints.getElement(index);
//				functionVariable.set(roots.get(index));
//				ExpressionNode xFun1 = params.get(index).xFun;
//				ExpressionNode yFun1 = params.get(index).yFun;
//				point.setCoords(xFun1.evaluateDouble(), yFun1.evaluateDouble(), 0, 1.0);
//			}
//		}
//	}

	public IntersectPolyCurvesAndLine(GeoCurveCartesianND curve, Coords coefficients) {
		this.kernel = curve.kernel;
		this.coefficients = coefficients;
		spline = new Spline(curve);
	}

	public void compute(AlgoElement.OutputHandler<GeoPointND> outputPoints) {
	}

}

