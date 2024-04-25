package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Intersect plane and curve
 *
 */
public abstract class AlgoIntersectCoordSysCurve extends AlgoIntersectAbstract {
	/** curve */
	protected GeoCurveCartesianND curve;
	private Solution soln;
	protected OutputHandler<GeoPointND> outputPoints; // output

	/**
	 * 
	 * @param c
	 *            construction
	 */
	public AlgoIntersectCoordSysCurve(Construction c) {
		super(c);
	}

	/**
	 * @param enx
	 *            eqution of the corrd sys with x,y,z replaced by corresponding
	 *            curve expressions
	 * @param fv
	 *            function variable
	 */
	protected void findIntersections(ExpressionNode enx, FunctionVariable fv) {
		// wrap in a function
		GeoFunction geoFun = enx.buildFunction(fv);

		double[] roots = null;
		int outputSize = -1;

		if (geoFun.isPolynomialFunction(true)) {
			if (soln == null) {
				soln = new Solution();
			}
			AlgoRootsPolynomial.calcRootsMultiple(geoFun.getFunction(),
					0, soln, kernel.getEquationSolver());
			soln.sortAndMakeUnique();
			roots = soln.curRoots;
			outputSize = soln.curRealRoots;
		}

		if (roots == null || outputSize == 0) {
			// polynomial method hasn't worked
			// solve a x(t) + b y(t) + c = 0 (for t)
			roots = AlgoRoots.findRoots(geoFun.getFunction(),
					curve.getMinParameter(),
					curve.getMaxParameter(), 100);

			outputSize = roots == null || roots.length == 0 ? 1 : roots.length;
		}

		// update and/or create points
		getOutputPoints().adjustOutputSize(outputSize);

		// affect new computed points
		int index = 0;
		if (roots != null && roots.length > 0) {
			for (index = 0; index < outputSize; index++) {
				double paramVal = roots[index];
				GeoPointND point = (GeoPointND) getOutputPoints()
						.getElement(index);

				if (paramVal < curve.getMinParameter()
						|| paramVal > curve.getMaxParameter()) {
					// intersection is not on the curve
					point.setUndefined();
				} else {

					// substitute parameter back into curve to get cartesian
					// coords

					getCoordsBySubstitution(fv, paramVal, point, curve);
				}
			}
		}

		// other points are undefined
		for (; index < getOutputPoints().size(); index++) {
			getOutputPoints().getElement(index).setUndefined();
		}

	}

	void getCoordsBySubstitution(FunctionVariable fv, double paramVal, GeoPointND point,
			GeoCurveCartesianND curve1) {
		ExpressionNode xFun = curve1.getFun(0).getExpression();
		ExpressionNode yFun = curve1.getFun(1).getExpression();
		double z = 0;
		fv.set(paramVal);
		if (curve1.getDimension() > 2) {
			z = curve1.getFun(2).getExpression().evaluateDouble();
		}
		point.setCoords(xFun.evaluateDouble(), yFun.evaluateDouble(), z, 1.0);

		// test the intersection point
		// this is needed for the intersection of Segments, Rays
		if (!inCoordSys(point)) {
			point.setUndefined();
		}
	}

	/**
	 * @return output handler
	 */
	protected final OutputHandler<GeoPointND> getOutputPoints() {
		return outputPoints;
	}

	/**
	 * 
	 * @param point
	 *            output point
	 * @param param
	 *            curve parameter
	 * @param fv
	 *            function variable
	 */
	protected final void updatePoint(GeoPointND point, double param,
			FunctionVariable fv) {

		ExpressionNode xFun = curve.getFun(0).getExpression();
		ExpressionNode yFun = curve.getFun(1).getExpression();
		double z = 0;
		fv.set(param);
		if (curve.getDimension() > 2) {
			z = curve.getFun(2).getExpression().evaluateDouble();
		}
		point.setCoords(xFun.evaluateDouble(), yFun.evaluateDouble(), z, 1.0);
	}

	/**
	 * @param point
	 *            point
	 * @return check it's really on the coord sys element
	 */
	protected boolean inCoordSys(GeoPointND point) {
		return true;
	}

	/**
	 *
	 * @return handler for output points
	 */
	protected OutputHandler<GeoPointND> createOutputPoints(boolean is3d) {
		if (is3d) {
			return new OutputHandler<>(() -> {
				GeoPointND pt = kernel.getManager3D().point3D(0, 0, 0, false);
				pt.setParentAlgorithm(this);
				return pt;
			});
		} else {
			return new OutputHandler<>(() -> {
				GeoPoint p = new GeoPoint(cons);
				p.setCoords(0, 0, 1);
				p.setParentAlgorithm(this);
				return p;
			});
		}
	}

}
