package org.geogebra.common.kernel.advanced;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.cas.AlgoDerivative;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.kernelND.GeoPointND;

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
	private GeoCurveCartesian3D f, f1, f2; // f = f(x), f1 is f'(x), f2 is
											// f''(x)
	private GeoVector3D v; // output

	private double f1eval[] = new double[3];
	private double f2eval[] = new double[3];

	private AlgoDerivative algoCAS, algoCAS2;
	private GeoConic gc;

	public AlgoCurvatureVectorCurve3D(Construction cons, String label,
			GeoPoint3D arg, GeoCurveCartesian3D arg2) {
		this(cons, arg, arg2);

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
		}

		cas();

		setInputOutput();
		compute();
	}

	private void cas() {
		// First derivative of curve f
		algoCAS = new AlgoDerivative(cons, f, true);
		cons.removeFromConstructionList(algoCAS);
		this.f1 = (GeoCurveCartesian3D) algoCAS.getResult();

		// Second derivative of curve f
		algoCAS2 = new AlgoDerivative(cons, f1, true);
		cons.removeFromConstructionList(algoCAS2);
		this.f2 = (GeoCurveCartesian3D) algoCAS2.getResult();
	}

	/*
	 * AlgoCurvatureVectorCurve3D(Construction cons, String label, GeoPoint a2,
	 * GeoConic geoConic) { this(cons, a2, geoConic); if (label != null) {
	 * v.setLabel(label); } else { v.setLabel("cv"); } }
	 */

	/*
	 * public AlgoCurvatureVectorCurve3D(Construction cons, GeoPoint A, GeoConic
	 * geoConic) { super(cons); this.A = A; this.gc = geoConic; f = new
	 * GeoCurveCartesian3D(cons); gc.toGeoCurveCartesian(f); // create new
	 * vector v = new GeoVector3D(cons); try { v.setStartPoint(A); } catch
	 * (CircularDefinitionException e) { }
	 * 
	 * cas();
	 * 
	 * setInputOutput(); compute(); }
	 */

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

	// Return the resultant vector
	public GeoVector3D getVector() {
		return v;
	}

	@Override
	public final void compute() {
		try {
			double tvalue;
			/*
			 * if (gc!=null){ f=new GeoCurveCartesian2D(cons);
			 * gc.toGeoCurveCartesian(f); cas(); }
			 */

			tvalue = f.getClosestParameter(A, f.getMinParameter());
			f1.evaluateCurve(tvalue, f1eval);
			f2.evaluateCurve(tvalue, f2eval);

			double[] w = new double[3];
			double[] w2 = new double[3];
			double[] w3 = new double[4];

			// CurvatureVector = curvature.((f'xf'')xf')/|(f'xf'')xf'|
			GeoVec3D.cross(f1eval, f2eval, w);
			GeoVec3D.cross(w, f1eval, w2);

			// normalize
			double d = Math.sqrt(w2[0] * w2[0] + w2[1] * w2[1] + w2[2] * w2[2]);
			w2[0] /= d;
			w2[1] /= d;
			w2[2] /= d;

			double curvature = f.evaluateCurvature(tvalue);
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
		if (removed)
			return;
		super.remove();
		((GeoElement) A).removeAlgorithm(algoCAS);
		f.removeAlgorithm(algoCAS);
		((GeoElement) A).removeAlgorithm(algoCAS2);
		f.removeAlgorithm(algoCAS2);
	}

	// TODO Consider locusequability

}