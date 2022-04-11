package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.MyMath;

/**
 * Osculating Circle of a curve f in point A: center = A + (radius)^2 * v radius
 * = 1/abs(k(x)), k(x)=curvature of f v = curvature vector of f in point A
 * 
 * @author Victor Franco Espino
 * @version 11-02-2007
 */

public class AlgoOsculatingCircleCurve extends AlgoElement {

	private GeoPoint A; // input
	private GeoPoint R;
	private GeoCurveCartesian f; // input
	private GeoVector v; // curvature vector of f in point A
	private GeoConic circle; // output
	private GeoConic gc = null;
	private AlgoCurvatureVectorCurve cv;

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
	public AlgoOsculatingCircleCurve(Construction cons, String label,
			GeoPoint A, GeoCurveCartesian f) {
		super(cons);
		this.A = A;
		this.f = f;

		R = new GeoPoint(cons); // R is the center of the circle
		circle = new GeoConic(cons);

		// Catch curvature and curvature vector
		cv = new AlgoCurvatureVectorCurve(cons, A, f);
		v = cv.getVector();

		cons.removeFromConstructionList(cv);
		setInputOutput();
		compute();
		circle.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param A
	 *            point on conic
	 * @param geoConic
	 *            conic
	 */
	public AlgoOsculatingCircleCurve(Construction cons, String label,
			GeoPoint A, GeoConic geoConic) {
		super(cons);
		this.A = A;
		gc = geoConic;
		R = new GeoPoint(cons); // R is the center of the circle
		circle = new GeoConic(cons);

		// Catch curvature and curvature vector
		cv = new AlgoCurvatureVectorCurve(cons, A, gc);
		v = cv.getVector();

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

	/** @return the resultant circle */
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
			// Catch curvature and curvature vector
			cv.compute();
			v = cv.getVector();
		}
		// bugfix Michael Borcherds

		double radius = 1 / MyMath.length(v.x, v.y);
		double r2 = radius * radius;
		double x = r2 * v.x;
		double y = r2 * v.y;

		Coords coords = A.getCoordsInD2();
		double ax = coords.getX() / coords.getZ();
		double ay = coords.getY() / coords.getZ();
		R.setCoords(ax + x, ay + y, 1.0);
		circle.setCircle(R, A);
	}

	@Override
	public void remove() {
		if (removed) {
			return;
		}
		super.remove();

		if (f != null) {
			f.removeAlgorithm(cv);
		}

		if (A != null) {
			A.removeAlgorithm(cv);
		}

		// make sure all AlgoCASDerivatives get removed
		if (cv != null) {
			cv.remove();
		}
	}

}