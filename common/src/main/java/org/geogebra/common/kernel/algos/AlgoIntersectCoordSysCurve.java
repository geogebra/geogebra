package org.geogebra.common.kernel.algos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Intersect plane and curve
 *
 */
public abstract class AlgoIntersectCoordSysCurve extends AlgoIntersectAbstract {
	/** curve */
	protected GeoCurveCartesianND curve;

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

		ArrayList<Double> polyRoots = new ArrayList<>();

		if (geoFun.isPolynomialFunction(true)) {
			// AbstractApplication.debug("trying polynomial");

			LinkedList<PolyFunction> factorList = geoFun.getFunction()
					.getPolynomialFactors(false, false);

			if (factorList != null) {
				// compute the roots of every single factor
				Iterator<PolyFunction> it = factorList.iterator();
				while (it.hasNext()) {
					PolyFunction polyFun = it.next();

					if (polyFun.updateCoeffValues()) {
						// now let's compute the roots of this factor
						// compute all roots of polynomial polyFun
						roots = polyFun.getCoeffsCopy();
						int n = cons.getKernel().getEquationSolver()
								.polynomialRoots(roots, true);

						for (int i = 0; i < n; i++) {
							polyRoots.add(roots[i]);
						}
					} else {
						outputSize = -1;
						break;
					}

				}
			}

		}

		if (polyRoots.size() > 0) {

			outputSize = polyRoots.size();

			roots = new double[outputSize];

			for (int i = 0; i < outputSize; i++) {
				roots[i] = polyRoots.get(i);
			}
		} else {
			// polynomial method hasn't worked
			// AbstractApplication.debug("trying non-polynomial");

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
					updatePoint(point, paramVal, fv);

					// test the intersection point
					// this is needed for the intersection of Segments, Rays
					if (!inCoordSys(point)) {
						point.setUndefined();
					}
				}

				// AbstractApplication.debug(xFun.evaluateDouble()+","+
				// yFun.evaluateDouble());
			}
		}

		// other points are undefined
		for (; index < getOutputPoints().size(); index++) {
			// AbstractApplication.debug("setting undefined "+index);
			getOutputPoints().getElement(index).setUndefined();
		}

	}

	/**
	 * @return output handler
	 */
	protected abstract OutputHandler<GeoElement> getOutputPoints();

	/**
	 * 
	 * @param point
	 *            output point
	 * @param param
	 *            curve parameter
	 * @param fv
	 *            function variable
	 */
	protected abstract void updatePoint(GeoPointND point, double param,
			FunctionVariable fv);

	/**
	 * @param point
	 *            point
	 * @return check it's really on the coord sys element
	 */
	protected abstract boolean inCoordSys(GeoPointND point);

}
