/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.cas;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoFunctionFreehand;
import org.geogebra.common.kernel.algos.AlgoPointOnPath;
import org.geogebra.common.kernel.algos.TangentAlgo;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.statistics.AlgoFitPoly;

/**
 * Algorithm for tangent of function
 */
public class AlgoTangentFunctionPoint extends AlgoElement
		implements TangentAlgo {

	private GeoPointND P; // input
	private GeoLine tangent; // output
	private GeoFunctionable f;
	private GeoPoint T;
	private boolean pointOnFunction;
	private boolean freehand;
	private AlgoFunctionFreehand freehandAlgo;
	private GeoList freehandList;
	private AlgoFitPoly algoFitPoly;
	private GeoPoint[] points;
	private GeoList geoList;
	private final NoCASDerivativeCache cache;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param P
	 *            point on function
	 * @param f
	 *            function
	 */
	public AlgoTangentFunctionPoint(Construction cons, String label,
			GeoPointND P, GeoFunctionable f) {
		this(cons, P, f);
		tangent.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param P
	 *            point on function
	 * @param f
	 *            function
	 */
	public AlgoTangentFunctionPoint(Construction cons, GeoPointND P,
			GeoFunctionable f) {
		super(cons);
		this.P = P;
		this.f = f;
		cache = new NoCASDerivativeCache(f);
		tangent = new GeoLine(cons);

		// check if P is defined as a point of the function's graph
		pointOnFunction = false;
		if (P.getParentAlgorithm() instanceof AlgoPointOnPath) {
			AlgoPointOnPath algoPoint = (AlgoPointOnPath) P
					.getParentAlgorithm();
			pointOnFunction = algoPoint.getPath() == f;
		}

		if (pointOnFunction) {
			T = (GeoPoint) P;
		} else {
			T = new GeoPoint(cons);
		}
		tangent.setStartPoint(T);

		if (f.getParentAlgorithm() instanceof AlgoFunctionFreehand) {
			// APPROXIMATE tangent for Freehand Functions
			freehand = true;

			freehandAlgo = (AlgoFunctionFreehand) f.getParentAlgorithm();

			freehandList = freehandAlgo.getList();

			geoList = new GeoList(cons);

			// # of points to sample (must be even)
			int steps = 10;
			points = new GeoPoint[steps];

			// order 5, 10 steps, 100 slices seems to work nicely
			// mockup https://www.geogebra.org/m/zTJ7KeE6
			algoFitPoly = new AlgoFitPoly(cons, geoList,
					new GeoNumeric(cons, 5));
			cons.removeFromConstructionList(algoFitPoly);

		}

		setInputOutput(); // for AlgoElement
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Tangent;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_TANGENTS;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) P;
		input[1] = f.toGeoElement();

		setOutputLength(1);
		setOutput(0, tangent);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting tangent
	 */
	public GeoLine getTangent() {
		return tangent;
	}

	/**
	 * @return function
	 */
	GeoFunctionable getFunction() {
		return f;
	}

	/**
	 * @return point on function
	 */
	GeoPointND getPoint() {
		return P;
	}

	/**
	 * @return point on function
	 */
	GeoPoint getTangentPoint() {
		return T;
	}

	@Override
	public GeoPoint getTangentPoint(GeoElement geo, GeoLine line) {
		if (geo != f) {
			return null;
		}
		if (line != tangent) {
			return null;
		}
		return T;
	}

	// calc tangent at x=a
	@Override
	public final void compute() {
		if (!(f.isDefined() && P.isDefined())) {
			tangent.setUndefined();
			return;
		}

		double slope;
		double a = P.getInhomX();
		double fa = f.value(a);

		if (freehand) {

			int steps = points.length;

			double min = freehandList.get(0).evaluateDouble();
			double max = freehandList.get(1).evaluateDouble();

			double step = (max - min) / 100;

			double offset = 0;

			// adjust if the sample goes outside the domain of the function
			if (a - step * steps / 2 < min) {
				offset = min - (a - step * steps / 2);
			} else if (a + step * (steps / 2d - 1) > max) {
				offset = max - (a + step * (steps / 2d - 1));
			}

			geoList.clear();
			for (int i = 0; i < points.length; i++) {
				points[i] = newPoint(a + step * (i - steps / 2d) + offset);
				geoList.add(points[i]);
			}

			algoFitPoly.compute();

			GeoFunction fun = algoFitPoly.getFitPoly();

			if (fun.getFunction() != null) {
				FunctionVariable fv = fun.getFunction().getFunctionVariable();
				ExpressionValue derivFit = fun.getFunction().derivative(fv,
						kernel);

				fv.set(a);

				slope = derivFit.evaluateDouble();
			} else {
				slope = Double.NaN;
			}

		} else {
			// calc the tangent;
			slope = cache.evaluateDerivative(a);
		}

		tangent.setCoords(-slope, 1.0, a * slope - fa);

		if (!pointOnFunction) {
			T.setCoords(a, fa, 1.0);
		}
	}

	private GeoPoint newPoint(double a) {
		return new GeoPoint(cons, a, f.value(a), 1);
	}

	@Override
	public final String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("TangentToAatB", "Tangent to %0 at %1",
				f.getLabel(tpl),
				"x = x(" + P.getLabel(tpl) + ")");

	}

}
