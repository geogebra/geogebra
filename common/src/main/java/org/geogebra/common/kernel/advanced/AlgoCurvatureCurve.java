package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * @author Victor Franco Espino, Markus Hohenwarter
 * @version 11-02-2007
 * 
 *          Calculate Curvature for curve: k(t) = (a'(t)b''(t)-a''(t)b'(t))/T^3,
 *          T = sqrt(a'(t)^2+b'(t)^2)
 */
public class AlgoCurvatureCurve extends AlgoElement {

	private GeoPointND A; // input
	private GeoCurveCartesianND f;
	private GeoNumeric K; // output
	private GeoConicND gc = null;
	private double kVal;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param A
	 *            point on curve
	 * @param f
	 *            curve
	 */
	public AlgoCurvatureCurve(Construction cons, String label, GeoPointND A,
			GeoCurveCartesianND f) {
		this(cons, A, f);

		if (label != null) {
			K.setLabel(label);
		} else {
			// if we don't have a label we could try k
			K.setLabel("k");
		}
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param A
	 *            point on curve
	 * @param f
	 *            conic
	 */
	public AlgoCurvatureCurve(Construction cons, String label, GeoPointND A,
			GeoConicND f) {
		this(cons, A, f);

		if (label != null) {
			K.setLabel(label);
		} else {
			// if we don't have a label we could try k
			K.setLabel("k");
		}
	}

	/**
	 * @param cons
	 *            construction
	 * @param A
	 *            point on curve
	 * @param f
	 *            curve
	 */
	public AlgoCurvatureCurve(Construction cons, GeoPointND A,
			GeoCurveCartesianND f) {
		super(cons);
		this.f = f;
		this.A = A;
		K = new GeoNumeric(cons);

		setInputOutput();
		compute();
	}

	/**
	 * @param cons
	 *            construction
	 * @param A
	 *            point on curve
	 * @param gc
	 *            conic
	 */
	public AlgoCurvatureCurve(Construction cons, GeoPointND A, GeoConicND gc) {
		super(cons);
		this.gc = gc;
		this.A = A;
		K = new GeoNumeric(cons);

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Curvature;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = A.toGeoElement();
		if (gc != null) {

			f = kernel.getGeoFactory().newCurve(gc instanceof GeoConic ? 2 : 3,
					cons);

			gc.toGeoCurveCartesian(f);
			input[1] = gc;
		} else {
			input[1] = f;
		}

		super.setOutputLength(1);
		super.setOutput(0, K);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return curvature
	 */
	public GeoNumeric getResult() {
		return K;
	}

	@Override
	public final void compute() {

		if (gc != null) {
			if (gc.getType() == GeoConicNDConstants.CONIC_PARABOLA) {
				double t = gc.getClosestParameterForParabola(A);
				K.setValue(gc.evaluateCurvatureForParabola(t));
				return;
			}

			gc.toGeoCurveCartesian(f);
			f.updateDistanceFunction();
		}

		if (f.isDefined()) {
			try {
				double t = f.getClosestParameterForCurvature(A,
						f.getMinParameter());
				kVal = gc == null ? f.evaluateCurvature(t)
						: Math.abs(f.evaluateCurvature(t));
				K.setValue(kVal);
			} catch (Exception ex) {
				ex.printStackTrace();
				K.setUndefined();
			}
		} else {
			K.setUndefined();
		}
	}

}