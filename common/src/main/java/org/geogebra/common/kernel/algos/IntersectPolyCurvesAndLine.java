package org.geogebra.common.kernel.algos;

import java.util.ArrayList;
import java.util.List;

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

	public List<Double> compute() {
		ArrayList<Double> roots = new ArrayList<>();
		for (int i = 0; i < spline.size(); i++) {
			ExpressionNode enx = spline.multiply(i, coeffs);
			GeoFunction functionX = enx.buildFunction(spline.getFunctionVariable());
			Solution solution = solve(functionX);
			if (solution.curRoots != null) {
				filterRoots(solution, i, roots);
			}
		}
		return roots;
	}

	private void filterRoots(Solution solution, int i, ArrayList<Double> roots) {
		for (int j = 0; j < solution.curRealRoots; j++) {
			double root = solution.curRoots[j];
			if (spline.isInInterval(root, i) && !roots.contains(root)) {
				roots.add(root);
			}
		}
	}

	private Solution solve(GeoFunction fX) {
		Solution solution = new Solution();
		AlgoRootsPolynomial.calcRootsMultiple(fX.getFunction(),
				0, solution, kernel.getEquationSolver());

		solution.sortAndMakeUnique();
		return solution;
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

