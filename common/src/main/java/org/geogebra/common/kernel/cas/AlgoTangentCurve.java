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
import org.geogebra.common.kernel.algos.AlgoDependentPoint;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoPointOnPath;
import org.geogebra.common.kernel.algos.TangentAlgo;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.plugin.Operation;

/**
 * @author Victor Franco Espino
 * @version 11-02-2007
 * 
 *          tangent to Curve f in point P: (b'(t), -a'(t),
 *          a'(t)*b(t)-a(t)*b'(t))
 */

public class AlgoTangentCurve extends AlgoElement implements TangentAlgo {

	private GeoPointND P; // input
	private GeoCurveCartesian f, df; // input f
	private GeoLine tangent; // output
	private GeoPoint T;
	private boolean pointOnCurve;
	private boolean pointOnCurveSpecial;
	private ExpressionValue pointOnCurveSpecialParam;
	private AlgoDerivative algo;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param P
	 *            point on function
	 * @param f
	 *            curve
	 */
	public AlgoTangentCurve(Construction cons, String label, GeoPointND P,
			GeoCurveCartesian f) {
		super(cons);
		tangent = new GeoLine(cons);
		this.P = P;
		initialize(f);
		setInputOutput(); // for AlgoElement
		compute();
		tangent.setLabel(label);
	}

	/**
	 * @param f1
	 *            cartesian curve (input)
	 */
	public void initialize(GeoCurveCartesian f1) {

		this.f = f1;

		// check if P is defined as a point of the curve's graph
		pointOnCurve = false;
		if (P.getParentAlgorithm() instanceof AlgoPointOnPath) {
			AlgoPointOnPath algoPOP = (AlgoPointOnPath) P.getParentAlgorithm();
			pointOnCurve = algoPOP.getPath() == f;
		} else if (P.getParentAlgorithm() instanceof AlgoDependentPoint) {

			// special code for curve(t)

			AlgoDependentPoint algoDP = (AlgoDependentPoint) P
					.getParentAlgorithm();

			ExpressionNode en = algoDP.getExpressionNode();

			if (en.getOperation() == Operation.VEC_FUNCTION
					&& en.getLeft().unwrap() == f) {
				pointOnCurveSpecial = true;
				pointOnCurveSpecialParam = en.getRight().unwrap();
			}

		}

		if (pointOnCurve || pointOnCurveSpecial) {
			T = (GeoPoint) P;
		} else {
			T = new GeoPoint(cons);
		}
		tangent.setStartPoint(T);

		// First derivative of curve f
		algo = new AlgoDerivative(cons, f, true);
		this.df = (GeoCurveCartesian) algo.getResult();
		cons.removeFromConstructionList(algo);
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
		input[1] = f;

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
	 * @return input curve
	 */
	GeoCurveCartesian getCurve() {
		return f;
	}

	/**
	 * @return input point on curve
	 */
	GeoPointND getPoint() {
		return P;
	}

	/**
	 * @return tangent point
	 */
	GeoPoint getTangentPoint() {
		return T;
	}

	private double feval[] = new double[2];
	private double dfeval[] = new double[2];

	@Override
	public final void compute() {
		if (!(f.isDefined() && P.isDefined())) {
			tangent.setUndefined();
			return;
		}

		// first derivative
		if (df == null || !df.isDefined()) {
			tangent.setUndefined();
			return;
		}

		// calc the tangent;
		double tvalue;

		if (pointOnCurve) {
			tvalue = P.getPathParameter().t;
		} else if (pointOnCurveSpecialParam != null) {
			tvalue = pointOnCurveSpecialParam.evaluateDouble();
		} else {
			tvalue = f.getClosestParameter(P, f.getMinParameter());
		}

		f.evaluateCurve(tvalue, feval);
		df.evaluateCurve(tvalue, dfeval);
		tangent.setCoords(-dfeval[1], dfeval[0], feval[0] * dfeval[1]
				- dfeval[0] * feval[1]);

		if (!pointOnCurve && !pointOnCurveSpecial) {
			T.setCoords(feval[0], feval[1], 1.0);
		}
	}

	public GeoPoint getTangentPoint(GeoElement geo, GeoLine line) {
		if (geo == f && line == tangent) {
			return getTangentPoint();
		}
		return null;
	}

	// TODO Consider locusequability
}