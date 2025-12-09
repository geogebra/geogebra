/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoPointOnPath;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.cas.AlgoDerivative;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.Operation;

/**
 * @author Victor Franco Espino
 * @version 11-02-2007
 * 
 *          tangent to Curve f in point P: (b'(t), -a'(t),
 *          a'(t)*b(t)-a(t)*b'(t))
 */

public class AlgoTangentCurve3D extends AlgoLinePoint {

	private GeoPointND P; // input
	private GeoCurveCartesian3D f; // input f
	private GeoCurveCartesian3D df;
	private GeoLine3D tangent; // output
	private GeoPointND T;
	private boolean pointOnCurve;
	private boolean pointOnCurveSpecial;
	private ExpressionValue pointOnCurveSpecialParam;
	private AlgoDerivative algo;

	private double[] feval = new double[3];
	private double[] dfeval = new double[3];
	private Coords direction = new Coords(0, 0, 0, 0);

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
	public AlgoTangentCurve3D(Construction cons, String label, GeoPointND P,
			GeoCurveCartesian3D f) {
		super(cons);
		tangent = new GeoLine3D(cons);
		this.P = P;
		initialize(f);
		setInputOutput(); // for AlgoElement
		compute();
		tangent.setLabel(label);

		update();
	}

	/**
	 * @param f1
	 *            cartesian curve (input)
	 */
	public void initialize(GeoCurveCartesian3D f1) {

		this.f = f1;

		// check if P is defined as a point of the curve's graph
		pointOnCurve = false;
		if (P.getParentAlgorithm() instanceof AlgoPointOnPath) {
			AlgoPointOnPath algoPOP = (AlgoPointOnPath) P.getParentAlgorithm();
			pointOnCurve = algoPOP.getPath() == f;
		} else if (P.getParentAlgorithm() instanceof AlgoDependentPoint3D) {
			// special code for curve(t)

			AlgoDependentPoint3D algoDP = (AlgoDependentPoint3D) P
					.getParentAlgorithm();

			ExpressionNode en = algoDP.getExpression();

			if (en.getOperation() == Operation.VEC_FUNCTION
					&& en.getLeft().unwrap() == f) {
				pointOnCurveSpecial = true;
				pointOnCurveSpecialParam = en.getRight().unwrap();
			}

		}

		if (pointOnCurve || pointOnCurveSpecial) {
			T = P;
		} else {
			T = new GeoPoint3D(cons);
		}
		tangent.setStartPoint(T);
		EvalInfo info = new EvalInfo(false);
		// First derivative of curve f
		algo = new AlgoDerivative(cons, f, true, info);
		this.df = (GeoCurveCartesian3D) algo.getResult();
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

		setOnlyOutput(tangent);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting tangent
	 */
	public GeoLine3D getTangent() {
		return tangent;
	}

	/**
	 * @return input curve
	 */
	GeoCurveCartesian3D getCurve() {
		return f;
	}

	@Override
	public GeoLine3D getLine() {
		return tangent;
	}

	@Override
	protected GeoPointND getPoint() {
		return T;
	}

	@Override
	protected Coords getDirection() {
		if (!(f.isDefined() && P.isDefined())) {
			direction.setX(Double.NaN);
			direction.setY(Double.NaN);
			direction.setZ(Double.NaN);
			return direction;
		}

		// first derivative
		if (df == null || !df.isDefined()) {
			direction.setX(Double.NaN);
			direction.setY(Double.NaN);
			direction.setZ(Double.NaN);
			return direction;
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

		df.evaluateCurve(tvalue, dfeval);

		direction.setX(dfeval[0]);
		direction.setY(dfeval[1]);
		direction.setZ(dfeval[2]);

		if (!pointOnCurve && !pointOnCurveSpecial) {
			f.evaluateCurve(tvalue, feval);
			T.setCoords(feval[0], feval[1], feval[2], 1.0);
		}

		return direction;
	}

	@Override
	public String toString(StringTemplate tpl) {
		return getLoc().getPlain("TangentToAatB", f.getLabel(tpl),
				P.getLabel(tpl));
	}

}