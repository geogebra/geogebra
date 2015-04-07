package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.cas.AlgoDerivative;
import org.geogebra.common.kernel.commands.Commands;
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
	private GeoCurveCartesian f, f1, f2; // f = f(x), f1 is f'(x), f2 is f''(x)
	private GeoVector v; // output

	private double f1eval[] = new double[2];
	private double f2eval[] = new double[2];

	AlgoDerivative algoCAS, algoCAS2;
	private GeoConic gc;

	public AlgoCurvatureVectorCurve(Construction cons, String label,
			GeoPoint A, GeoCurveCartesian f) {
		this(cons, A, f);

		if (label != null) {
			v.setLabel(label);
		} else {
			// if we don't have a label we could try c
			v.setLabel("cv");
		}
	}

	AlgoCurvatureVectorCurve(Construction cons, GeoPoint A, GeoCurveCartesian f) {
		super(cons);
		this.A = A;
		this.f = f;

		// create new vector
		v = new GeoVector(cons);
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
		this.f1 = (GeoCurveCartesian) algoCAS.getResult();

		// Second derivative of curve f
		algoCAS2 = new AlgoDerivative(cons, f1, true);
		cons.removeFromConstructionList(algoCAS2);
		this.f2 = (GeoCurveCartesian) algoCAS2.getResult();
	}

	AlgoCurvatureVectorCurve(Construction cons, String label, GeoPoint a2,
			GeoConic geoConic) {
		this(cons, a2, geoConic);
		if (label != null) {
			v.setLabel(label);
		} else {
			v.setLabel("cv");
		}
	}

	public AlgoCurvatureVectorCurve(Construction cons, GeoPoint A,
			GeoConic geoConic) {
		super(cons);
		this.A = A;
		this.gc = geoConic;
		f = new GeoCurveCartesian(cons);
		gc.toGeoCurveCartesian(f);
		// create new vector
		v = new GeoVector(cons);
		try {
			v.setStartPoint(A);
		} catch (CircularDefinitionException e) {
		}

		cas();

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

	// Return the resultant vector
	public GeoVector getVector() {
		return v;
	}

	@Override
	public final void compute() {
		try {
			double t2, t4, x, y, evals, tvalue;
			if (gc != null) {
				f = new GeoCurveCartesian(cons);
				gc.toGeoCurveCartesian(f);
				cas();
			}
			tvalue = f.getClosestParameter(A, f.getMinParameter());
			f1.evaluateCurve(tvalue, f1eval);
			f2.evaluateCurve(tvalue, f2eval);
			t2 = f1eval[0] * f1eval[0] + f1eval[1] * f1eval[1];
			t4 = t2 * t2;
			evals = f1eval[0] * f2eval[1] - f2eval[0] * f1eval[1];

			x = A.inhomX + ((evals / t4) * (-f1eval[1]));
			y = A.inhomY + ((evals / t4) * f1eval[0]);

			v.x = x - A.inhomX;
			v.y = y - A.inhomY;
			v.z = 0.0;
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
		A.removeAlgorithm(algoCAS);
		f.removeAlgorithm(algoCAS);
		A.removeAlgorithm(algoCAS2);
		f.removeAlgorithm(algoCAS2);
	}

	// TODO Consider locusequability

}