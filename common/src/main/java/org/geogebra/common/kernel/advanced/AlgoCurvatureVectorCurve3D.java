package org.geogebra.common.kernel.advanced;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.cas.AlgoDerivative;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * @author Michael
 * 
 *         http://en.wikipedia.org/w/index.php?title=Curvature&section=8#
 *         Local_expressions_2 Calculate Curvature Vector for curve: c(t) =
 *         ((a'(t)b''(t)-a''(t)b'(t))/T^4) * (-b'(t),a'(t)) T =
 *         sqrt(a'(t)^2+b'(t)^2)
 */

public class AlgoCurvatureVectorCurve3D extends AlgoElement {

	private GeoPointND A; // input
	private GeoCurveCartesian3D f; // input
	private GeoCurveCartesian3D f1; // f1 = f'
	private GeoCurveCartesian3D f2; // f2 = f''
	private GeoVector3D v; // output

	private double[] f1eval = new double[3];
	private double[] f2eval = new double[3];

	private AlgoDerivative algoCAS;
	private AlgoDerivative algoCAS2;
	private GeoConic3D gc;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param pt
	 *            point
	 * @param f
	 *            curve
	 */
	public AlgoCurvatureVectorCurve3D(Construction cons, String label,
			GeoPointND pt, GeoCurveCartesian3D f) {
		this(cons, pt, f);

		if (label != null) {
			v.setLabel(label);
		} else {
			// if we don't have a label we could try c
			v.setLabel("cv");
		}
	}

	AlgoCurvatureVectorCurve3D(Construction cons, GeoPointND A,
			GeoCurveCartesian3D f) {
		super(cons);
		this.A = A;
		this.f = f;

		// create new vector
		v = new GeoVector3D(cons);
		try {
			v.setStartPoint(A);
		} catch (CircularDefinitionException e) {
			// can't happen with new vector
		}

		cas();

		setInputOutput();
		compute();
	}

	/**
	 * @param cons
	 *            construction
	 * @param arg
	 *            point
	 * @param geoConic3D
	 *            conic
	 */
	public AlgoCurvatureVectorCurve3D(Construction cons,
			GeoPoint3D arg, GeoConic3D geoConic3D) {
		super(cons);
		this.gc = geoConic3D;
		this.A = arg;
		// create new vector
		v = new GeoVector3D(cons);
		try {
			v.setStartPoint(A);
		} catch (CircularDefinitionException e) {
			// can't happen with new vectors
		}

		setInputOutput();
		compute();
	}

	private void cas() {
		EvalInfo info = new EvalInfo(false);
		// First derivative of curve f
		algoCAS = new AlgoDerivative(cons, f, true, info);
		cons.removeFromConstructionList(algoCAS);
		this.f1 = (GeoCurveCartesian3D) algoCAS.getResult();

		// Second derivative of curve f
		algoCAS2 = new AlgoDerivative(cons, f1, true, info);
		cons.removeFromConstructionList(algoCAS2);
		this.f2 = (GeoCurveCartesian3D) algoCAS2.getResult();
	}

	@Override
	public Commands getClassName() {
		return Commands.CurvatureVector;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) A;
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
	public GeoVector3D getVector() {
		return v;
	}

	@Override
	public final void compute() {
		try {
			double curvature;

			double[] w2 = new double[3];
			if (gc != null) {
				gc.evaluateFirstDerivative(A, f1eval);

				curvature = gc.evaluateCurvatureFromDerivative(f1eval);
				Coords rw = gc.getCoordSys().getVector(f1eval[0], f1eval[1]);
				w2[0] = rw.getX();
				w2[1] = rw.getY();
				w2[2] = rw.getZ();
			} else {
				double tvalue = f.getClosestParameterForCurvature(A,
						f.getMinParameter());
				f1.evaluateCurve(tvalue, f1eval);
				f2.evaluateCurve(tvalue, f2eval);
				curvature = f.evaluateCurvature(tvalue);
				// CurvatureVector = curvature.((f'xf'')xf')/|(f'xf'')xf'|
				double[] w = new double[3];
				GeoVec3D.cross(f1eval, f2eval, w);
				GeoVec3D.cross(w, f1eval, w2);
			}

			// normalize
			double d = Math.sqrt(w2[0] * w2[0] + w2[1] * w2[1] + w2[2] * w2[2]);
			w2[0] /= d;
			w2[1] /= d;
			w2[2] /= d;

			double[] w3 = new double[4];
			w3[0] = w2[0] * curvature;
			w3[1] = w2[1] * curvature;
			w3[2] = w2[2] * curvature;
			w3[3] = 0;

			v.setCoords(w3);

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
		((GeoElement) A).removeAlgorithm(algoCAS);
		f.removeAlgorithm(algoCAS);
		((GeoElement) A).removeAlgorithm(algoCAS2);
		f.removeAlgorithm(algoCAS2);
	}

}