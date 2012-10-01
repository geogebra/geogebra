/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package geogebra.common.kernel.cas;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoPointOnPath;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;

/**
 * @author Victor Franco Espino 
 * @version 11-02-2007
 * 
 *         tangent to Curve f in point P: (b'(t), -a'(t), a'(t)*b(t)-a(t)*b'(t))
 */

public class AlgoTangentCurve extends AlgoUsingTempCASalgo {

	private GeoPoint P; // input
	private GeoCurveCartesian f, df; // input f
	private GeoLine tangent; // output
	private GeoPoint T;
	private boolean pointOnCurve;

	/**
	 * @param cons construction
	 * @param label label for output
	 * @param P point on function
	 * @param f curve
	 */
	public AlgoTangentCurve(Construction cons, String label, GeoPoint P,
			GeoCurveCartesian f) {
		super(cons);
		this.P = P;
		this.f = f;
		tangent = new GeoLine(cons);

		// check if P is defined as a point of the curve's graph
		pointOnCurve = false;
		if (P.getParentAlgorithm() instanceof AlgoPointOnPath) {
			AlgoPointOnPath algo = (AlgoPointOnPath) P.getParentAlgorithm();
			pointOnCurve = algo.getPath() == f;
		}

		if (pointOnCurve)
			T = P;
		else
			T = new GeoPoint(cons);
		tangent.setStartPoint(T);

		// First derivative of curve f
		algoCAS = new AlgoDerivative(cons, f);
		this.df = (GeoCurveCartesian) ((AlgoDerivative) algoCAS).getResult();
		cons.removeFromConstructionList(algoCAS);

		setInputOutput(); // for AlgoElement
		compute();
		tangent.setLabel(label);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoTangentCurve;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_TANGENTS;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = P;
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
	GeoPoint getPoint() {
		return P;
	}

	/**
	 * @return tangent point
	 */
	GeoPoint getTangentPoint() {
		return T;
	}

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
		double feval[] = new double[2];
		double dfeval[] = new double[2];

		double tvalue = f.getClosestParameter(P, f.getMinParameter());
		f.evaluateCurve(tvalue, feval);
		df.evaluateCurve(tvalue, dfeval);
		tangent.setCoords(-dfeval[1], dfeval[0], feval[0] * dfeval[1]
				- dfeval[0] * feval[1]);

		if (!pointOnCurve)
			T.setCoords(feval[0], feval[1], 1.0);
	}

	// TODO Consider locusequability
}