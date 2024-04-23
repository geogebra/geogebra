package org.geogebra.common.kernel.algos;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

public final class IntersectPolyCurvesAndLine {
	private final Kernel kernel;
	private final Coords coeffs;
	private final Spline spline;


	public IntersectPolyCurvesAndLine(GeoCurveCartesianND curve, Coords coeffs) {
		this.kernel = curve.kernel;
		this.coeffs = coeffs;
		spline = new Spline(curve);
	}

	public void compute(AlgoElement.OutputHandler<GeoPointND> outputPoints) {
		ArrayList<Double> roots = new ArrayList<>();
		for (int i = 0; i < spline.size(); i++) {
			GeoFunction fX = getGeoFunction(i);
			Solution solution = solveFx(fX);
			if (solution.curRoots != null) {
				for (int j = 0; j < solution.curRealRoots; j++) {
					double root = solution.curRoots[j];
					if (spline.isInInterval(root, i) && !roots.contains(root)) {
						roots.add(root);
					}
				}
			}
		}

		if (roots.isEmpty()) {
			outputPoints.adjustOutputSize(1);
			outputPoints.getElement(0).setCoords(0,0, 1);
		} else {
			updatePoints(outputPoints, roots);
		}
	}

	private Solution solveFx(GeoFunction fX) {
		Solution solution = new Solution();
		AlgoRootsPolynomial.calcRootsMultiple(fX.getFunction(),
				0, solution, kernel.getEquationSolver());

		solution.sortAndMakeUnique();
		return solution;
	}

	private GeoFunction getGeoFunction(int i) {
		ExpressionNode xFun = spline.getFuncX(i);
		ExpressionNode yFun = spline.getFuncY(i);
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

		GeoFunction fX = enx.buildFunction(spline.getFunctionVariable());
		return fX;
	}

	private void updatePoints(AlgoElement.OutputHandler<GeoPointND> outputPoints,
			ArrayList<Double> roots) {
		ArrayList<GPoint2D> points = new ArrayList<>();
		double x = Double.NaN;
		for (int i = 0; i < roots.size(); i++) {
			Double t = roots.get(i);
			if (!DoubleUtil.isEqual(t, x, 1E-4)) {
				GPoint2D p = spline.get(t);
				if (p != null) {
					points.add(p);
				}
			}
			x = t;
		}
		outputPoints.adjustOutputSize(points.size());
		for (int i = 0; i < points.size(); i++) {
			GPoint2D p = points.get(i);
			outputPoints.getElement(i)
					.setCoords(p.x, p.y, 1);
		}
	}
}

