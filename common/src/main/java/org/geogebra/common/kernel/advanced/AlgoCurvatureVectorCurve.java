package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.cas.AlgoDerivative;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVector;

/**
 * @author Victor Franco Espino
 * @version 11-02-2007
 * 
 *          Calculate Curvature Vector for curve: c(t) =
 *          ((a'(t)b''(t)-a''(t)b'(t))/T^4) * (-b'(t),a'(t)) T =
 *          sqrt(a'(t)^2+b'(t)^2)
 */

public class AlgoCurvatureVectorCurve extends AlgoElement {

	private GeoPoint A; // input
	private GeoCurveCartesian f; // input
	private GeoCurveCartesian f1; // f1 = f'
	private GeoCurveCartesian f2; // f2 = f''
	private GeoVector v; // output

	private double[] f1eval = new double[2];
	private double[] f2eval = new double[2];

	AlgoDerivative algoCAS;
	AlgoDerivative algoCAS2;
	private GeoConic gc;

	/**
	 * @param cons
	 *            construction
	 * @param A
	 *            point
	 * @param f
	 *            curve
	 */
	public AlgoCurvatureVectorCurve(Construction cons, GeoPoint A,
			GeoCurveCartesian f) {
		super(cons);
		this.A = A;
		this.f = f;

		// create new vector
		v = new GeoVector(cons);
		try {
			v.setStartPoint(A);
		} catch (CircularDefinitionException e) {
			// can't happen with new vectors
		}

		cas();

		setInputOutput();
		compute();
	}

	private void cas() {
		EvalInfo info = new EvalInfo(false);
		// First derivative of curve f
		algoCAS = new AlgoDerivative(cons, f, true, info);
		cons.removeFromConstructionList(algoCAS);
		this.f1 = (GeoCurveCartesian) algoCAS.getResult();

		// Second derivative of curve f
		algoCAS2 = new AlgoDerivative(cons, f1, true, info);
		cons.removeFromConstructionList(algoCAS2);
		this.f2 = (GeoCurveCartesian) algoCAS2.getResult();
	}

	/**
	 * @param cons
	 *            construction
	 * @param A
	 *            point
	 * @param geoConic
	 *            conic
	 */
	public AlgoCurvatureVectorCurve(Construction cons, GeoPoint A,
			GeoConic geoConic) {
		super(cons);
		this.A = A;
		this.gc = geoConic;
		// create new vector
		v = new GeoVector(cons);
		try {
			v.setStartPoint(A);
		} catch (CircularDefinitionException e) {
			// can't happen with new vectors
		}

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.CurvatureVector;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = A;
		if (gc != null) {
			input[1] = gc;
		} else {
			input[1] = f;
		}

		super.setOutputLength(1);
		super.setOutput(0, v);
		setDependencies(); // done by AlgoElement
	}

	/** @return the resultant vector */
	public GeoVector getVector() {
		return v;
	}

	@Override
	public final void compute() {
		try {
			double t2, t4, evals;
			if (gc != null) {
				gc.evaluateFirstDerivative(A, f1eval);
				double curvature = gc.evaluateCurvatureFromDerivative(f1eval);
				double norm = Math.hypot(f1eval[0], f1eval[1]);
				v.x = f1eval[0] * curvature / norm;
				v.y = f1eval[1] * curvature / norm;
			} else {
				double tvalue = f.getClosestParameterForCurvature(A,
						f.getMinParameter());
				f1.evaluateCurve(tvalue, f1eval);
				f2.evaluateCurve(tvalue, f2eval);
				t2 = f1eval[0] * f1eval[0] + f1eval[1] * f1eval[1];
				t4 = t2 * t2;
				evals = f1eval[0] * f2eval[1] - f2eval[0] * f1eval[1];

				v.x = ((evals / t4) * (-f1eval[1]));
				v.y = ((evals / t4) * f1eval[0]);
			}
			v.z = 0.0;
		} catch (Exception e) {
			// in case something went wrong, e.g. derivatives not defined
			v.setUndefined();
		}
	}

	@Override
	public void remove() {
		if (removed) {
			return;
		}
		super.remove();
		A.removeAlgorithm(algoCAS);
		f.removeAlgorithm(algoCAS);
		A.removeAlgorithm(algoCAS2);
		f.removeAlgorithm(algoCAS2);
	}

}