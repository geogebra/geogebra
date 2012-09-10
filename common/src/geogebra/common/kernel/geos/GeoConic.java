/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * GeoConic.java
 *
 * Created on 10. September 2001, 08:52
 */

package geogebra.common.kernel.geos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra.common.kernel.prover.NoSymbolicParametersException;
import geogebra.common.kernel.prover.Polynomial;
import geogebra.common.kernel.prover.Variable;
import geogebra.common.main.App;
import geogebra.common.plugin.GeoClass;
import geogebra.common.util.MyMath;

import java.util.ArrayList;

/**
 * Conics in 2D
 */
public class GeoConic extends GeoConicND implements Traceable,
		ConicMirrorable, Transformable, Mirrorable,
		Dilateable, SymbolicParametersBotanaAlgo {

	/*
	 * ( A[0] A[3] A[4] ) matrix = ( A[3] A[1] A[5] ) ( A[4] A[5] A[2] )
	 */

	/**
	 * Creates a conic
	 * 
	 * @param c
	 *            construction
	 */
	public GeoConic(Construction c) {
		super(c, 2);
	}

	/**
	 * Creates new GeoConic with Coordinate System for 3D
	 * 
	 * @param c
	 *            construction
	 * @param label
	 *            label
	 * @param coeffs
	 *            coefficients
	 */
	public GeoConic(Construction c, String label, double[] coeffs) {

		this(c);
		setCoeffs(coeffs);
		setLabel(label);
	}

	/**
	 * Creates copy of conic in construction of conic
	 * 
	 * @param conic
	 *            conic to be copied
	 */
	public GeoConic(GeoConic conic) {
		this(conic.cons);
		set(conic);
	}
	
	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.CONIC;
	}

	@Override
	public GeoElement copy() {
		return new GeoConic(this);
	}

	@Override
	public boolean isTraceable() {
		return true;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	public boolean getTrace() {
		return trace;
	}

	/**
	 * makes this conic a circle with midpoint M through Point P
	 */
	@Override
	final public void setCircle(GeoPoint M, GeoPoint P) {
		defined = M.isDefined() && P.isDefined() && !P.isInfinite();
		if (!defined) {
			return;
		}

		if (M.isInfinite()) {
			// midpoint at infinity -> parallelLines
			// one through P, the other through infinite point M
			/*
			 * b.x = P.inhomX; b.y = P.inhomY;
			 */
			double[] coords = new double[3];
			P.getCoords(coords);
			setMidpoint(coords);
			// M is normalvector of double line
			eigenvecX = -M.y;
			eigenvecY = M.x;
			setEigenvectors();
			halfAxes[0] = Double.POSITIVE_INFINITY;
			halfAxes[1] = Double.POSITIVE_INFINITY;
			mu[0] = 0.0; // line at infinity is not drawn
			parallelLines(mu);
			// set line at infinity 0 = 1
			lines[1].x = Double.NaN;
			lines[1].y = Double.NaN;
			lines[1].z = Double.NaN;
			// set degenerate matrix
			matrix[0] = 0.0d;
			matrix[1] = 0.0d;
			matrix[2] = lines[0].z;
			matrix[3] = 0.0d;
			matrix[4] = lines[0].x / 2.0;
			matrix[5] = lines[0].y / 2.0;
		} else {
			setCircleMatrix(M, M.distance(P));
		}
		setAffineTransform();
	}

	/**
	 * Return angle of rotation from x-axis to the major axis of ellipse
	 * 
	 * @return angle between x-axis and major axis of ellipse
	 */
	double getPhi() {
		if (matrix[3] == 0) {
			if (matrix[0] < matrix[1]) {
				return 0.0;
			}
			return 0.5 * Math.PI;
		}
		if (matrix[0] <= matrix[1]) {
			return 0.25 * Math.PI - 0.5
					* Math.atan((matrix[0] - matrix[1]) / (2 * matrix[3]));
		}
		return 0.75 * Math.PI - 0.5
				* Math.atan((matrix[0] - matrix[1]) / (2 * matrix[3]));
	}

	/**
	 * Return n points on conic
	 * 
	 * @param n
	 *            number of points
	 * @return Array list of points
	 */
	public ArrayList<GeoPoint> getPointsOnConic(int n) {
		GeoCurveCartesian curve = new GeoCurveCartesian(cons);
		this.toGeoCurveCartesian(curve);

		double startInterval = -Math.PI, endInterval = Math.PI;

		if (this.type == CONIC_HYPERBOLA) {
			startInterval = -Math.PI / 2;
			endInterval = Math.PI / 2;
		}
		if (this.type == CONIC_PARABOLA) {
			startInterval = -1;
			endInterval = 1;
		}

		return curve.getPointsOnCurve(n, startInterval, endInterval);
	}

	/**
	 * Invert circle or line in circle
	 * 
	 * @version 2010-01-21
	 * @author Michael Borcherds
	 * @param mirror
	 *            Circle used as mirror
	 */
	final public void mirror(GeoConic mirror) {
		
		if (mirror.getType() == CONIC_SINGLE_POINT) {
			setUndefined();
			return;
		}
		
		if (mirror.isCircle() && (type == CONIC_SINGLE_POINT || type == CONIC_CIRCLE)) { // Mirror point in circle
			double r1 = mirror.getHalfAxes()[0];
			GeoVec2D midpoint1 = mirror.getTranslationVector();
			double x1 = midpoint1.getX();
			double y1 = midpoint1.getY();

			double r2 = getHalfAxes()[0];
			GeoVec2D midpoint2 = getTranslationVector();
			double x2 = midpoint2.getX();
			double y2 = midpoint2.getY();

			// distance between centers
			double dist = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

			//App.debug("dist ="+dist);
			//App.debug("r1="+r1+" x1="+x1+"y1 ="+y1);
			//App.debug("r2="+r2+" x2="+x2+"y2 ="+y2);

			// circle being reflected has zero radius
			// and it's at center of mirror
			if (Kernel.isZero(r2) && Kernel.isZero(dist)) {
				
				setUndefined();
				update();
				return;
				
			}
			
			
			// does circle being inverted pass through center of the other?
			if (Kernel.isEqual(dist, r2)) {
				double dx = x2 - x1;
				double dy = y2 - y1;
				// (x3,y3) is reflection of reflection of (x1+2dx,x1+2dy) an
				// thus lies on the line
				double k = r1 * r1 / 2 / r2 / r2;
				double x3 = x1 + k * dx;
				double y3 = y1 + k * dy;

				matrix[4] = dx * 0.5;
				matrix[5] = dy * 0.5;
				matrix[2] = -dx * x3 - dy * y3;
				matrix[0] = 0;
				matrix[1] = 0;
				matrix[3] = 0;
				// we update the eigenvectors etc.
				this.setMatrix(matrix);
				// classification yields CONIC_DOUBLE_LINE, we want a single
				// line
				type = GeoConicNDConstants.CONIC_LINE;
				return;
			}

			double x = r1 * r1 / (dist - r2);
			double y = r1 * r1 / (dist + r2);

			// radius of new circle
			double r3 = Math.abs(y - x) / 2.0;
			// center of new circle
			double centerX, centerY;
			
			if (Kernel.isZero(dist)) {
				// circle being mirrored has same centre as mirror -> centre doesn't change
				centerX = x1;
				centerY = y1;
			} else {
				centerX = x1 + (x2 - x1) * (Math.min(x, y) + r3) / dist;
				centerY = y1 + (y2 - y1) * (Math.min(x, y) + r3) / dist;
			}
			
			//App.debug("r3="+r3+" centerX="+centerX+"centerY ="+centerY);
			
			// double sf=r1*r1/((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
			// setCoords( x1+sf*(x2-x1), y1+sf*(y2-y1) ,1.0);
			GeoPoint tmp = new GeoPoint(cons, null, centerX, centerY, 1.0);
			setCircleMatrix(tmp, r3);
			tmp.removeOrSetUndefinedIfHasFixedDescendent();
		} else if (mirror.isCircle()
				&& (this.getType() == GeoConicNDConstants.CONIC_LINE || this
						.getType() == GeoConicNDConstants.CONIC_PARALLEL_LINES)) { // Mirror
																					// point
																					// in
																					// circle

			if (mirror.getType() == GeoConicNDConstants.CONIC_CIRCLE) { // Mirror
																	// point in
																	// circle
				double r = mirror.getHalfAxes()[0];
				GeoVec2D midPoint = mirror.getTranslationVector();
				double mx = midPoint.getX();
				double my = midPoint.getY();
				double lx = (getLines()[0]).x;
				double ly = (getLines()[0]).y;
				double lz = (getLines()[0]).z;
				double perpY, perpX;

				if (lx == 0) {
					perpX = mx;
					perpY = -lz / ly;
				} else {
					perpY = -(lx * ly * mx - lx * lx * my + ly * lz)
							/ (lx * lx + ly * ly);
					perpX = (-lz - ly * perpY) / lx;

				}
				double dist2 = ((perpX - mx) * (perpX - mx) + (perpY - my)
						* (perpY - my));
				// if line goes through center, we keep it
				if (!Kernel.isZero(dist2)) {
					double sf = r * r / dist2;
					// GeoPoint p =new GeoPoint(cons,null,a+sf*(perpX-a),
					// b+sf*(perpY-b) ,1.0);
					GeoPoint m = new GeoPoint(cons);
					m.setCoords(mx + sf * (perpX - mx) / 2, my + sf * (perpY - my)
							/ 2, 1.0);
					setSphereND(
							m,
							sf
									/ 2
									* Math.sqrt(((perpX - mx) * (perpX - mx) + (perpY - my)
											* (perpY - my))));
				} else
					type = GeoConicNDConstants.CONIC_LINE;
			} else {
				setUndefined();
			}
		}

		setAffineTransform();
		// updateDegenerates(); // for degenerate conics
	}

	/**
	 * mirror this conic at point Q
	 */
	final public void mirror(GeoPoint Q) {
		double qx = Q.getInhomX();
		double qy = Q.getInhomY();

		matrix[2] = 4.0
				* (qy * qy * matrix[1] + qx
						* (qx * matrix[0] + 2.0 * qy * matrix[3] + matrix[4]) + qy
						* matrix[5]) + matrix[2];
		matrix[4] = -2.0 * (qx * matrix[0] + qy * matrix[3]) - matrix[4];
		matrix[5] = -2.0 * (qx * matrix[3] + qy * matrix[1]) - matrix[5];

		// change eigenvectors' orientation
		eigenvec[0].mult(-1.0);
		eigenvec[1].mult(-1.0);

		// mirror translation vector b
		b.mirror(Q);
		setMidpoint(new double[] { b.getX(), b.getY() });

		setAffineTransform();
		updateDegenerates(); // for degenerate conics
	}

	/**
	 * mirror this point at line g
	 */
	final public void mirror(GeoLine g) {
		// Y = S(phi).(X - Q) + Q
		// where Q is a point on g, S(phi) is the mirror transform
		// and phi/2 is the line's slope angle

		// get arbitrary point of line
		double qx, qy;
		if (Math.abs(g.getX()) > Math.abs(g.getY())) {
			qx = -g.getZ() / g.getX();
			qy = 0.0d;
		} else {
			qx = 0.0d;
			qy = -g.getZ() / g.getY();
		}

		// translate -Q
		doTranslate(-qx, -qy);
		// do mirror transform
		mirror(2.0 * Math.atan2(-g.getX(), g.getY()));
		// translate +Q
		doTranslate(qx, qy);

		setAffineTransform();
		updateDegenerates(); // for degenerate conics
	}

	/**
	 * mirror transform with angle phi [ cos sin 0 ] [ sin -cos 0 ] [ 0 0 1 ]
	 */
	final private void mirror(double phi) {
		// set rotated matrix
		double sum = matrix[0] + matrix[1];
		double diff = matrix[0] - matrix[1];
		double cos = Math.cos(phi);
		double sin = Math.sin(phi);
		// cos(2 phi) = cos(phi)\u00b2 - sin(phi)\u00b2 = (cos + sin)*(cos -
		// sin)
		double cos2 = (cos + sin) * (cos - sin);
		// cos(2 phi) = 2 cos sin
		double sin2 = 2.0 * cos * sin;

		double tmp = diff * cos2 + 2.0 * matrix[3] * sin2;
		double A0 = (sum + tmp) / 2.0;
		double A1 = (sum - tmp) / 2.0;
		double A3 = -matrix[3] * cos2 + diff * cos * sin;
		double A4 = matrix[4] * cos + matrix[5] * sin;
		matrix[5] = -matrix[5] * cos + matrix[4] * sin;
		matrix[0] = A0;
		matrix[1] = A1;
		matrix[3] = A3;
		matrix[4] = A4;

		// avoid classification: make changes by hand
		eigenvec[0].mirror(phi);
		eigenvec[1].mirror(phi);

		b.mirror(phi);
		setMidpoint(new double[] { b.getX(), b.getY() });
	}

	// //////////////////////////////////////
	// FOR DRAWING IN 3D
	// //////////////////////////////////////

	@Override
	public Coords getEigenvec3D(int i) {
		Coords ret = new Coords(4);
		ret.set(getEigenvec(i));
		return ret;
	}

	@Override
	public boolean hasDrawable3D() {
		return true;
	}

	@Override
	public Coords getLabelPosition() {
		return new Coords(0, 0, 0, 1);
	}

	@Override
	public Coords getDirection3D(int i) {
		return new Coords(lines[i].y, -lines[i].x, 0, 0);
	}

	@Override
	public Coords getOrigin3D(int i) {
		return new Coords(startPoints[i].x, startPoints[i].y, 0, 1);
	}

	@Override
	final public boolean isCasEvaluableObject() {
		return true;
	}

	@Override
	protected char getLabelDelimiter() {
		return ':';
	}

	private CoordSys coordSys = CoordSys.Identity3D();

	@Override
	public CoordSys getCoordSys() {
		return coordSys;
	}

	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {
		// TODO Auto-generated method stub

		double[][] adj = MyMath.adjoint(a00, a01, a02, a10, a11, a12, a20, a21,
				a22);
		/*
		 * ( A[0] A[3] A[4] ) matrix = ( A[3] A[1] A[5] ) ( A[4] A[5] A[2] )
		 * P=matrix*B
		 */
		double p00 = matrix[0] * adj[0][0] + matrix[3] * adj[0][1] + matrix[4]
				* adj[0][2];
		double p01 = matrix[0] * adj[1][0] + matrix[3] * adj[1][1] + matrix[4]
				* adj[1][2];
		double p02 = matrix[0] * adj[2][0] + matrix[3] * adj[2][1] + matrix[4]
				* adj[2][2];
		double p10 = matrix[3] * adj[0][0] + matrix[1] * adj[0][1] + matrix[5]
				* adj[0][2];
		double p11 = matrix[3] * adj[1][0] + matrix[1] * adj[1][1] + matrix[5]
				* adj[1][2];
		double p12 = matrix[3] * adj[2][0] + matrix[1] * adj[2][1] + matrix[5]
				* adj[2][2];
		double p20 = matrix[4] * adj[0][0] + matrix[5] * adj[0][1] + matrix[2]
				* adj[0][2];
		double p21 = matrix[4] * adj[1][0] + matrix[5] * adj[1][1] + matrix[2]
				* adj[1][2];
		double p22 = matrix[4] * adj[2][0] + matrix[5] * adj[2][1] + matrix[2]
				* adj[2][2];

		matrix[0] = adj[0][0] * p00 + adj[0][1] * p10 + adj[0][2] * p20;
		matrix[3] = adj[0][0] * p01 + adj[0][1] * p11 + adj[0][2] * p21;
		matrix[4] = adj[0][0] * p02 + adj[0][1] * p12 + adj[0][2] * p22;
		matrix[1] = adj[1][0] * p01 + adj[1][1] * p11 + adj[1][2] * p21;
		matrix[5] = adj[1][0] * p02 + adj[1][1] * p12 + adj[1][2] * p22;
		matrix[2] = adj[2][0] * p02 + adj[2][1] * p12 + adj[2][2] * p22;

		this.classifyConic(false);
	}

	@Override
	public boolean isFillable() {
		return type != GeoConicNDConstants.CONIC_LINE;
	}

	@Override
	public boolean isInverseFillable() {
		return isFillable();
	}

	/*
	 * ( A[0] A[3] A[4] ) matrix = ( A[3] A[1] A[5] ) ( A[4] A[5] A[2] )
	 */

	/**
	 * @param coeff matrix of coefficients
	 */
	public void setCoeffs(ExpressionValue[][] coeff) {

		matrix[0] = evalCoeff(coeff, 2, 0);
		matrix[1] = evalCoeff(coeff, 0, 2);
		matrix[2] = evalCoeff(coeff, 0, 0);
		matrix[3] = evalCoeff(coeff, 1, 1) / 2;
		matrix[4] = evalCoeff(coeff, 1, 0) / 2;
		matrix[5] = evalCoeff(coeff, 0, 1) / 2;

		classifyConic(false);
		if (coeff.length <= 2 && coeff[0].length <= 2
				&& Kernel.isZero(evalCoeff(coeff, 1, 1))) {
			type = CONIC_LINE;
			App.debug(matrix[4] + "," + matrix[5] + ","
					+ matrix[2]);
		}
	}

	/**
	 * @param ev two-fold array of expressions	
	 * @param i row
	 * @param j column
	 * @return evaluated ev[i][j] or 0 if the array does not contain [i][j]
	 */
	public static double evalCoeff(ExpressionValue[][] ev, int i, int j) {
		if (ev.length > i && ev[i].length > j && ev[i][j] != null) {
			return ev[i][j].evaluateNum().getDouble();
		}
		return 0;
	}
	/**
	 * @return parameter of parabola
	 */
	public double getP() {
		return p;
	}
	/**
	 * Set this conic from line (type will be CONIC_LINE)
	 * @param line line
	 */
	public void fromLine(GeoLine line) {
		lines = new GeoLine[2];
		lines[0] = line;
		lines[1] = line;
		type = GeoConicNDConstants.CONIC_LINE;

	}

	public Variable[] getBotanaVars(GeoElement geo) {
		if (algoParent != null
				&& algoParent instanceof SymbolicParametersBotanaAlgo) {
			return ((SymbolicParametersBotanaAlgo) algoParent).getBotanaVars(this);
		}
		return null;
	}
	
	public Polynomial[] getBotanaPolynomials(GeoElement geo) throws NoSymbolicParametersException  {
		if (algoParent != null && algoParent instanceof SymbolicParametersBotanaAlgo) {
		return ((SymbolicParametersBotanaAlgo) algoParent).getBotanaPolynomials(this);
		}
	throw new NoSymbolicParametersException();
	}

	
}
