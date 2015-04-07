package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.main.App;

/**
 * @author Victor Franco Espino
 * @version 11-02-2007
 * 
 *          Osculating Circle of a curve f in point A: center = A + (radius)^2 *
 *          v radius = 1/abs(k(x)), k(x)=curvature of f v = curvature vector of
 *          f in point A
 */

public class AlgoOsculatingCircleCurve extends AlgoElement {

	private GeoPoint A, R;// input A
	private GeoCurveCartesian f;// input
	private GeoVector v;// curvature vector of f in point A
	private GeoNumeric curv;// curvature of f in point A
	private GeoConic circle; // output
	private GeoConic gc = null;
	AlgoCurvatureCurve algo;
	AlgoCurvatureVectorCurve cv;

	public AlgoOsculatingCircleCurve(Construction cons, String label,
			GeoPoint A, GeoCurveCartesian f) {
		super(cons);
		this.A = A;
		this.f = f;

		R = new GeoPoint(cons);// R is the center of the circle
		circle = new GeoConic(cons);

		// Catch curvature and curvature vector
		algo = new AlgoCurvatureCurve(cons, A, f);
		cv = new AlgoCurvatureVectorCurve(cons, A, f);
		curv = algo.getResult();
		v = cv.getVector();

		cons.removeFromConstructionList(algo);
		cons.removeFromConstructionList(cv);
		setInputOutput();
		compute();
		circle.setLabel(label);
	}

	public AlgoOsculatingCircleCurve(Construction cons, String label,
			GeoPoint A, GeoConic geoConic) {
		super(cons);
		this.A = A;
		f = new GeoCurveCartesian(cons);
		gc = geoConic;
		gc.toGeoCurveCartesian(f);
		R = new GeoPoint(cons);// R is the center of the circle
		circle = new GeoConic(cons);

		// Catch curvature and curvature vector
		algo = new AlgoCurvatureCurve(cons, A, f);
		cv = new AlgoCurvatureVectorCurve(cons, A, f);
		curv = algo.getResult();
		v = cv.getVector();

		cons.removeFromConstructionList(algo);
		cons.removeFromConstructionList(cv);
		setInputOutput();
		compute();
		circle.setLabel(label);

	}

	@Override
	public Commands getClassName() {
		return Commands.OsculatingCircle;
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
		super.setOutput(0, circle);
		setDependencies(); // done by AlgoElement
	}

	// Return the resultant circle
	public GeoConic getCircle() {
		return circle;
	}

	@Override
	public final void compute() {
		// undefined unless A is a point on f
		if (gc == null && !f.isOnPath(A, Kernel.MIN_PRECISION)) {
			circle.setUndefined();
			return;
		}
		if (gc != null) {
			if (!gc.isOnPath(A, Kernel.MIN_PRECISION)) {
				circle.setUndefined();
				return;
			}
			f = new GeoCurveCartesian(cons);
			gc.toGeoCurveCartesian(f);
			// Catch curvature and curvature vector
			algo.compute();
			cv.compute();
			curv = algo.getResult();
			v = cv.getVector();
			App.debug(v.toValueString(StringTemplate.defaultTemplate));
			App.debug(curv.toValueString(StringTemplate.defaultTemplate));
		}
		// bugfix Michael Borcherds

		double radius = 1 / Math.abs(curv.getValue());
		double r2 = radius * radius;
		double x = r2 * v.x;
		double y = r2 * v.y;

		R.setCoords(A.inhomX + x, A.inhomY + y, 1.0);
		App.debug(R.toValueString(StringTemplate.defaultTemplate));
		App.debug(A.toValueString(StringTemplate.defaultTemplate));
		circle.setCircle(R, A);
	}

	@Override
	public void remove() {
		if (removed)
			return;
		super.remove();
		f.removeAlgorithm(algo);
		f.removeAlgorithm(cv);
		A.removeAlgorithm(algo);
		A.removeAlgorithm(cv);

		// make sure all AlgoCASDerivatives get removed
		cv.remove();
	}

	// TODO Consider locusequability

}