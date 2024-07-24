/*
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * GeoLine.java
 *
 * Created on 30. August 2001, 17:39
 */

package org.geogebra.common.kernel.geos;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MatrixTransformable;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.PathMover;
import org.geogebra.common.kernel.PathMoverGeneric;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.algos.SymbolicParameters;
import org.geogebra.common.kernel.algos.SymbolicParametersAlgo;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import org.geogebra.common.kernel.algos.TangentAlgo;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Functional;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.properties.TableProperties;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.kernel.matrix.CoordMatrixUtil;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.kernel.prover.AbstractProverReciosMethod;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.ExtendedBoolean;
import org.geogebra.common.util.MyMath;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Geometrical representation of line
 * 
 * @author Markus
 * 
 */
public class GeoLine extends GeoVec3D implements Path, Translateable,
		Mirrorable, Dilateable, GeoLineND, MatrixTransformable,
		GeoFunctionable, Transformable, GeoEvaluatable, SymbolicParametersAlgo,
		SymbolicParametersBotanaAlgo, EquationValue, Lineable2D, Functional {

	// modes
	/** implicit equation */
	public static final int EQUATION_IMPLICIT = 0; // a x + b y = c
	/** explicit equation */
	public static final int EQUATION_EXPLICIT = 1; // y = m x + b
	/** parametric equation */
	public static final int PARAMETRIC = 2;
	/** non-canonical implicit equation */
	public static final int EQUATION_IMPLICIT_NON_CANONICAL = 3;
	/** general form **/
	public static final int EQUATION_GENERAL = 4;
	/** user input **/
	public static final int EQUATION_USER = 5;
	private boolean showUndefinedInAlgebraView = false;

	private String parameter = Unicode.lambda + "";
	/** start point */
	public GeoPoint startPoint;
	/** end point */
	public GeoPoint endPoint;

	private static final String[] vars = { "x", "y" };

	private PVariable[] botanaVars; // only for an axis or a fixed slope line

	private StringBuilder sbToString;

	private static StringBuilder sbToStringLHS = new StringBuilder("\u221E");

	/** list of points on this line */
	protected ArrayList<GeoPoint> pointsOnLine;

	private GeoFunction asFunction;
	private int tableColumn = -1;
	private boolean pointsVisible = true;

	/**
	 * Creates new line
	 * 
	 * @param c
	 *            construction
	 */
	public GeoLine(Construction c) {
		super(c);
		setConstructionDefaults();
		int lineStyle = getConstruction().getApplication().getConfig()
				.getLineDisplayStyle();
		if (lineStyle > -1) {
			setMode(lineStyle);
		}
	}

	/**
	 * Creates new line
	 * 
	 * @param c
	 *            construction
	 * @param mode
	 *            string mode (GeoLine.EQUATION_*)
	 */
	public GeoLine(Construction c, int mode) {
		this(c);
		setMode(mode);
	}

	/**
	 * Creates new GeoLine
	 * 
	 * @param cons
	 *            construction
	 * @param a
	 *            x-coefficient
	 * @param b
	 *            y-coefficient
	 * @param c
	 *            z-coefficient
	 */
	public GeoLine(Construction cons, double a, double b, double c) {
		super(cons, a, b, c); // GeoVec3D constructor
		setConstructionDefaults();
	}

	/**
	 * Copy constructor
	 * 
	 * @param line
	 *            line to copy
	 */
	public GeoLine(GeoLine line) {
		this(line.cons);
		set(line);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.LINE;
	}

	@Override
	public GeoLine copy() {
		return new GeoLine(this);
	}

	@Override
	final public void setCoords(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		setDefinition(null);
	}

	/**
	 * set the line to pass through (pointX, pointY)
	 * 
	 * @param pointX
	 *            x coord
	 * @param pointY
	 *            y coord
	 */
	@Override
	final public void setLineThrough(double pointX, double pointY) {
		setCoords(x, y, -((x * pointX) + (y * pointY)));
		setStandardStartPoint();
	}

	@Override
	final public void setCoords(GeoVec3D v) {
		setCoords(v.x, v.y, v.z);
	}

	/**
	 * returns true if P lies on this line
	 * 
	 * @param p
	 *            point
	 * @param eps
	 *            precision
	 * @return true if P lies on this line
	 */
	public boolean isIntersectionPointIncident(GeoPoint p, double eps) {
		return isOnFullLine(p, eps);
	}

	/**
	 * States wheter P lies on this line or not.
	 * 
	 * @return true iff P lies on this line
	 * @param P
	 *            point
	 * @param eps
	 *            precision (ratio of allowed error and |x|+|y|)
	 */
	public final boolean isOnFullLine(GeoPoint P, double eps) {
		if (!P.isDefined()) {
			return false;
		}

		double simplelength = Math.abs(x) + Math.abs(y);
		if (P.isInfinite()) {
			return Math.abs(x * P.x + y * P.y) < eps * simplelength;
		}
		// STANDARD CASE: finite point
		return Math.abs(x * P.inhomX + y * P.inhomY + z) < eps * simplelength;
	}

	@Override
	public final boolean isOnFullLine(Coords Pnd, double eps) {
		Coords P = Pnd.getCoordsIn2DView();

		return isOnFullLine2D(P, eps);
	}

	/**
	 * @param P
	 *            2D coords of the point
	 * @param eps
	 *            precision
	 * @return whether point lies on this line within given precision
	 */
	public final boolean isOnFullLine2D(Coords P, double eps) {
		double simplelength = Math.abs(x) + Math.abs(y);
		if (DoubleUtil.isZero(P.getZ())) { // infinite point
			return Math.abs(x * P.getX() + y * P.getY()) < eps * simplelength;
		}
		// STANDARD CASE: finite point
		return Math.abs(
				x * P.getX() / P.getZ() + y * P.getY() / P.getZ() + z) < eps
						* simplelength;
	}

	/**
	 * Returns whether this point lies on this line, segment or ray.
	 */
	@Override
	final public boolean isOnPath(GeoPointND PI, double eps) {
		GeoPoint P = (GeoPoint) PI;

		if (P.getPath() == this) {
			return true;
		}

		// check if P lies on line first
		if (!isOnFullLine(P, eps)) {
			return false;
		}

		// for a line we are done here: the point is on the line
		// for rays and segments we need to continue
		GeoClass classType = getGeoClassType();
		if (classType.equals(GeoClass.LINE)) {
			return true;
		}

		// idea: calculate path parameter and check
		// if it is in [0, 1] for a segment or greater than 0 for a ray

		// make sure we use point changed for a line to get parameters on
		// the entire line when this is a segment or ray
		Coords coords = P.getCoordsInD2();
		double t = projectCoordsAndComputePathParam(coords);

		boolean result;
		switch (classType) {
		case SEGMENT:
			// segment: parameter in [0,1]
			result = t >= -eps && t <= 1 + eps;
			break;

		case RAY:
			// ray: parameter > 0
			result = t >= -eps;
			break;

		default:
			// line: any parameter
			result = true;
		}

		return result;
	}

	@Override
	public boolean isOnPath(Coords Pnd, double eps) {
		Coords P2d = Pnd.getCoordsIn2DView();
		return isOnFullLine2D(P2d, eps);
	}

	/**
	 * Returns whether three lines are concurrent
	 * 
	 * @param inputLine1
	 *            first input
	 * @param inputLine2
	 *            second input
	 * @param inputLine3
	 *            third input
	 * @return if the inputs are concurrent
	 */
	final public static boolean concurrent(GeoLine inputLine1,
			GeoLine inputLine2, GeoLine inputLine3) {
		double det = inputLine1.getX() * inputLine2.getY() * inputLine3.getZ()
				+ inputLine2.getX() * inputLine3.getY() * inputLine1.getZ()
				+ inputLine3.getX() * inputLine1.getY() * inputLine2.getZ()
				- inputLine3.getX() * inputLine2.getY() * inputLine1.getZ()
				- inputLine2.getX() * inputLine1.getY() * inputLine3.getZ()
				- inputLine1.getX() * inputLine3.getY() * inputLine2.getZ();
		return DoubleUtil.isZero(det);
	}

	@Override
	public boolean respectLimitedPath(Coords coords, double eps) {
		return true;
	}

	/**
	 * return a possible parameter for the point P (return the parameter for the
	 * projection of P on the path)
	 * 
	 * @param coords
	 *            point whose possible parameter we need
	 * @return a possible parameter for the point P
	 */
	public double getPossibleParameter(Coords coords) {
		// get parameters on the entire line when this is a segment or ray
		return projectCoordsAndComputePathParam(coords);
	}

	/**
	 * @param g
	 *            line
	 * @return true if this line and g are parallel
	 */
	final public boolean isParallel(GeoLine g) {
		return DoubleUtil.isEqual(g.x * y, g.y * x);
	}

	/**
	 * @param g
	 *            line
	 * @return true if this line and g are parallel (signed)
	 */
	final public boolean isSameDirection(GeoLine g) {
		// check x and g.x have the same sign
		// also y and g.y
		return (g.x * x >= 0) && (g.y * y >= 0) && isParallel(g);
	}

	/**
	 * @param g
	 *            line
	 * @return true if this line and g are perpendicular
	 */
	final public boolean isPerpendicular(GeoLine g) {
		return DoubleUtil.isEqual(g.x * x, -g.y * y);
	}

	/**
	 * Calculates the euclidian distance between this GeoLine and (px, py).
	 */
	@Override
	public double distance(GeoPoint p) {
		return distance(p.inhomX, p.inhomY);
	}

	/**
	 * Calculates the euclidian distance between this GeoLine and (x0, y0).
	 * 
	 * @param x0
	 *            x coord
	 * @param y0
	 *            y coord
	 * @return distance
	 */
	public double distance(double x0, double y0) {
		return Math.abs((x * x0 + y * y0 + z) / MyMath.length(x, y));
	}

	/**
	 * Calculates the euclidian distance between this GeoLine and GeoPoint P.
	 * Here the inhomogenouse coords of p are calculated and p.inhomX, p.inhomY
	 * are not used.
	 * 
	 * @param p
	 *            point
	 * @return distance between this line and a point
	 */
	final public double distanceHom(GeoPoint p) {
		return Math
				.abs((x * p.x / p.z + y * p.y / p.z + z) / MyMath.length(x, y));
	}

	/**
	 * 
	 * @param p
	 *            coords to which we compute the distance
	 * @return the euclidian distance between this GeoLine and 2D point p.
	 */
	final public double distanceHom(Coords p) {
		return Math.abs((x * p.getX() / p.getZ() + y * p.getY() / p.getZ() + z)
				/ MyMath.length(x, y));
	}

	/**
	 * Calculates the distance between this GeoLine and GeoLine g.
	 * 
	 * @param g
	 *            line
	 * @return distance between lines
	 */
	final public double distance(GeoLine g) {
		// parallel
		if (DoubleUtil.isZero(g.x * y - g.y * x)) {
			// get a point (px, py) of g and calc distance
			double px, py;
			if (Math.abs(g.x) > Math.abs(g.y)) {
				px = -g.z / g.x;
				py = 0.0d;
			} else {
				px = 0.0d;
				py = -g.z / g.y;
			}
			return Math.abs((x * px + y * py + z) / MyMath.length(x, y));
		}
		return 0.0;
	}

	/**
	 * @param out
	 *            vector to store direction
	 */
	final public void getDirection(GeoVec3D out) {
		out.setCoords(y, -x, 0.0d);
	}

	/**
	 * Writes coords of direction vector to array dir.
	 * 
	 * @param dir
	 *            array of length 2
	 */
	final public void getDirection(double[] dir) {
		dir[0] = y;
		dir[1] = -x;
	}

	/**
	 * Set array p to (x,y) coords of a point on this line
	 * 
	 * @param p
	 *            array for pint coordinates
	 */
	final public void getInhomPointOnLine(double[] p) {
		// point defined by parent algorithm
		if (startPoint != null && startPoint.isFinite()) {
			p[0] = startPoint.inhomX;
			p[1] = startPoint.inhomY;
		}
		// point on axis
		else {
			if (Math.abs(x) > Math.abs(y)) {
				p[0] = -z / x;
				p[1] = 0.0d;
			} else {
				p[0] = 0.0d;
				p[1] = -z / y;
			}
		}
	}

	/**
	 * Sets point p p to coords of some point on this line
	 * 
	 * @param p
	 *            point to be moved to this path
	 */
	final public void getPointOnLine(GeoPoint p) {
		// point defined by parent algorithm
		if (startPoint != null && startPoint.isFinite()) {
			p.setCoords(startPoint);
		}
		// point on axis
		else {
			if (Math.abs(x) > Math.abs(y)) {
				p.setCoords(-z / x, 0.0, 1.0);
			} else {
				p.setCoords(0.0, -z / y, 1.0);
			}
		}
	}

	/**
	 * Set standard start point (closest to (0,0)). Needed for path parameter to
	 * work correctly.
	 * 
	 * @return start point
	 */
	@Override
	public final GeoPointND setStandardStartPoint() {
		if (startPoint == null) {
			startPoint = new GeoPoint(cons);
			startPoint.addIncidence(this, true);
		}

		// this way the behaviour of pathChanged and pointChanged remain
		// the same as if there weren't a startPoint
		// so the dependent path parameters (probably) needn't be changed
		if (x != 0 || y != 0) {
			startPoint.setCoords(-z * x / (x * x + y * y),
					-z * y / (x * x + y * y), 1.0);
		} else {
			// this case probably won't happen, just for completeness
			startPoint.setCoords(0.0, 0.0, 1.0);
		}

		// alternative method
		// if (Math.abs(x) > Math.abs(y)) {
		// startPoint.setCoords(-z / x, 0.0, 1.0);
		// } else {
		// startPoint.setCoords(0.0, -z / y, 1.0);
		// }

		return startPoint;
	}

	/**
	 * @param P
	 *            start point
	 */
	public final void setStartPoint(GeoPoint P) {
		if (startPoint == P) {
			return;
		}

		startPoint = P;
		if (P != null) {
			P.addIncidence(this, true);
		}
	}

	@Override
	public final void setStartPoint(GeoPointND P) {
		if (P instanceof GeoPoint) {
			setStartPoint((GeoPoint) P);
		}
		if (startPoint == null) {
			startPoint = new GeoPoint(cons);
		}
		startPoint.set(P);
	}

	/**
	 * @param Q
	 *            end point
	 */
	public final void setEndPoint(GeoPoint Q) {
		if (endPoint == Q) {
			return;
		}

		endPoint = Q;
		if (Q != null) {
			Q.addIncidence(this, true);
		}
	}

	/**
	 * Retuns first defining point of this line or null.
	 */
	@Override
	final public GeoPoint getStartPoint() {
		return startPoint;
	}

	/**
	 * Retuns second point of this line or null.
	 */
	@Override
	final public GeoPoint getEndPoint() {
		return endPoint;
	}

	@Override
	public boolean isDefined() {
		return coefficientsDefined() && !(DoubleUtil.isZero(x) && DoubleUtil.isZero(y));
	}

	private boolean coefficientsDefined() {
		return !Double.isNaN(x) && !Double.isNaN(y) && !Double.isNaN(z);
	}

	@Override
	public boolean isDefinitionValid() {
		return coefficientsDefined();
	}

	@Override
	protected boolean showInEuclidianView() {
		// defined
		return isDefined();
	}

	@Override
	public boolean showInAlgebraView() {
		return showUndefinedInAlgebraView || isDefined();
	}

	/**
	 * Set whether this line should be visible in AV when undefined
	 *
	 * @param flag
	 *            true to show undefined
	 */
	public void showUndefinedInAlgebraView(boolean flag) {
		showUndefinedInAlgebraView = flag;
	}

	@Override
	public void set(GeoElementND geo) {
		super.set(geo);

		GeoLine l = (GeoLine) geo;
		parameter = l.parameter;
		toStringMode = l.toStringMode;
		reuseDefinition(geo);
	}

	/**
	 * Yields true if the coefficients of this line are linear dependent on
	 * those of line g.
	 */
	// Michael Borcherds 2008-04-30
	@Override
	public ExtendedBoolean isEqualExtended(GeoElementND geo) {
		if (!geo.isDefined() || !isDefined()) {
			return ExtendedBoolean.newExtendedBoolean(isDegenerate() && geo instanceof GeoLine
					&& ((GeoLine) geo).isDegenerate());
		}

		// support c==f for Line, Function
		if (geo.isGeoFunction()) {
			PolyFunction poly = ((GeoFunction) geo).getFunction()
					.expandToPolyFunction(
							((GeoFunction) geo).getFunctionExpression(), false,
							true);

			if (poly == null) {
				// (probably) not a polynomial
				return ExtendedBoolean.FALSE;
			}

			int degree = poly.getDegree();

			if (degree > 1) {
				// not linear
				return ExtendedBoolean.FALSE;
			}

			double[] coeffs = poly.getCoeffs();

			if (degree == 0) {
				if (DoubleUtil.isEqual(x, 0) && DoubleUtil.isEqual(-z / y, coeffs[0])) {
					return ExtendedBoolean.TRUE;
				}

			} else {
				// f(x_var) = -x/y x_var - z/y
				if (DoubleUtil.isEqual(-x / y, coeffs[1])
						&& DoubleUtil.isEqual(-z / y, coeffs[0])) {
					return ExtendedBoolean.TRUE;
				}
			}

			return ExtendedBoolean.FALSE;
		}

		// return false if it's a different type, otherwise use equals() method
		if (geo.isGeoRay() || geo.isGeoSegment()) {
			return ExtendedBoolean.FALSE;
		}
		if (geo.isGeoLine()) {
			return ExtendedBoolean.newExtendedBoolean(linDep((GeoLine) geo));
		}
		return ExtendedBoolean.FALSE;
	}

	private boolean isDegenerate() {
		return x == 0 && y == 0 && Double.isFinite(z);
	}

	/**
	 * yields true if this line is defined as a tangent of conic c
	 * 
	 * @param c
	 *            conic
	 * @return true iff defined as tangent of given conic
	 */
	final public boolean isDefinedTangent(GeoConic c) {
		AlgoElement ob = getParentAlgorithm();
		if (ob instanceof TangentAlgo) {
			for (GeoElement geo : ob.getInput()) {
				if (geo == c) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * yields true if this line is defined as a asymptote of conic c
	 * 
	 * @param c
	 *            conic
	 * @return true iff defined as a asymptote of conic c
	 */
	final public boolean isDefinedAsymptote(GeoConic c) {
		if (Algos.isUsedFor(Commands.Asymptote, this)) {
			for (GeoElement geo : getParentAlgorithm().getInput()) {
				if (geo == c) {
					return true;
				}
			}
		}

		return false;
	}

	/***********************************************************
	 * MOVEMENTS
	 ***********************************************************/

	/**
	 * translate by vector v
	 */
	@Override
	public void translate(Coords v) {
		z -= x * v.getX() + y * v.getY();
	}

	@Override
	final public boolean isTranslateable() {
		return true;
	}

	/**
	 * dilate from S by r
	 */
	@Override
	public void dilate(NumberValue rval, Coords S) {
		double r = rval.getDouble();
		double temp = r - 1;
		z = temp * (x * S.getX() + y * S.getY()) + r * z;

		x *= r;
		y *= r;
		z *= r;
	}

	/**
	 * rotate this line by angle phi around (0,0)
	 */
	@Override
	public void rotate(NumberValue phiVal) {
		rotateXY(phiVal);
	}

	/**
	 * rotate this line by angle phi around Q
	 */
	@Override
	public void rotate(NumberValue phiVal, GeoPointND point) {
		Coords Q = point.getInhomCoords();
		double phi = phiVal.getDouble();
		double cos = Math.cos(phi);
		double sin = Math.sin(phi);
		double qx = Q.getX();
		double qy = Q.getY();

		double x0 = x * cos - y * sin;
		double y0 = x * sin + y * cos;
		z = z + (x * qx + y * qy) * (1.0 - cos) + (y * qx - x * qy) * sin;
		x = x0;
		y = y0;
	}

	/**
	 * mirror this line at point Q
	 */
	@Override
	public void mirror(Coords Q) {
		double qx = x * Q.getX();
		double qy = y * Q.getY();

		z = z + 2.0 * (qx + qy);
		x = -x;
		y = -y;
	}

	/**
	 * mirror this point at line g
	 */
	@Override
	public void mirror(GeoLineND g1) {
		GeoLine g = (GeoLine) g1;

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

		double phi = 2.0 * Math.atan2(-g.getX(), g.getY());
		double cos = Math.cos(phi);
		double sin = Math.sin(phi);

		double x0 = x * cos + y * sin;
		double y0 = x * sin - y * cos;
		double xqx = x * qx;
		double yqy = y * qy;
		z += (xqx + yqy) + (yqy - xqx) * cos - (x * qy + y * qx) * sin;
		x = x0;
		y = y0;

		// change orientation
		x = -x;
		y = -y;
		z = -z;
	}

	/***********************************************************/

	/**
	 * Switch to parametric mode and set parameter name
	 * 
	 * @param parameter
	 *            name
	 */
	@Override
	final public void setToParametric(String parameter) {
		setMode(GeoLine.PARAMETRIC);
		if (parameter != null && parameter.length() > 0) {
			this.parameter = parameter;
		}
	}

	/** change equation mode to explicit */
	@Override
	final public void setToExplicit() {
		setMode(EQUATION_EXPLICIT);
	}

	private void setToExplicit(boolean force) {
		setMode(EQUATION_EXPLICIT, force);
	}

	/** set equation mode to implicit */
	@Override
	final public void setToImplicit() {
		setMode(EQUATION_IMPLICIT);
	}

	@Override
	final public void setToUser() {
		setMode(EQUATION_USER);
	}

	@Override
	final public void setMode(int mode) {
		setMode(mode, false);
	}

	/**
	 * Sets the coord style
	 *
	 * @param mode
	 *            new coord style
	 *
	 * @param force
	 *            mode is forced
	 */
	public void setMode(int mode, boolean force) {
		if (!force && isEquationFormEnforced()) {
			toStringMode = cons.getApplication().getConfig().getEnforcedLineEquationForm();
		} else {
			setModeWithImplicitEquationAsDefault(mode);
		}
	}

	/** output depends on mode: PARAMETRIC or EQUATION */
	@Override
	public String toString(StringTemplate tpl) {
		return label + ": " + toValueString(tpl);
	}

	private StringBuilder getSbToString() {
		if (sbToString == null) {
			sbToString = new StringBuilder(50);
		} else {
			sbToString.setLength(0);
		}
		return sbToString;
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		if (tpl.hasCASType()) {
			if (getDefinition() != null) {
				return getDefinition().toValueString(tpl);
			}

			double[] numbers = new double[3];
			numbers[0] = x;
			numbers[1] = y;
			numbers[2] = z;
			double gcd = Kernel.gcd(numbers);
			StringBuilder sb = getSbToString();
			sb.append("(");
			if (gcd != 1 && !DoubleUtil.isZero(gcd)) {
				sb.append(kernel.format(x / gcd, tpl));
			} else {
				sb.append(kernel.format(x, tpl));
			}
			sb.append(")*");
			sb.append(tpl.printVariableName("x"));
			sb.append("+(");
			if (gcd != 1 && !DoubleUtil.isZero(gcd)) {
				sb.append(kernel.format(y / gcd, tpl));
			} else {
				sb.append(kernel.format(y, tpl));
			}
			sb.append(")*");
			sb.append(tpl.printVariableName("y"));
			sb.append('=');
			if (gcd != 1 && !DoubleUtil.isZero(gcd)) {
				sb.append(kernel.format(-z / gcd, tpl));
			} else {
				sb.append(kernel.format(-z, tpl));
			}
			return sb.toString();
		}

		double[] P = new double[2];
		double[] g = new double[3];

		if (!coefficientsDefined() || (DoubleUtil.isZero(x) && DoubleUtil.isZero(y)
				&& getToStringMode() != EQUATION_USER)) {
			if (getToStringMode() == PARAMETRIC) {
				return "X" + tpl.getEqualsWithSpace() + "(?, ?)";
			} else {
				// eg list = {x = ?}
				return  "?";
			}
		}

		switch (getToStringMode()) {
		case EQUATION_EXPLICIT: // /EQUATION
			g[0] = x;
			g[1] = y;
			g[2] = z;
			return kernel.buildExplicitEquation(g, vars, tpl, true).toString();

		case PARAMETRIC:
			getInhomPointOnLine(P); // point
			StringBuilder sbBuildValueStr = getSbToString();
			GeoCasCell casCell = getCorrespondingCasCell();
			if (casCell == null || !casCell.isAssignmentVariableDefined()) {
				sbBuildValueStr.append("X");
				sbBuildValueStr.append(tpl.getEqualsWithSpace());
			}
			sbBuildValueStr.append("(");
			sbBuildValueStr.append(kernel.format(P[0], tpl));
			sbBuildValueStr.append(", ");
			sbBuildValueStr.append(kernel.format(P[1], tpl));
			sbBuildValueStr.append(") + ");
			sbBuildValueStr.append(parameter);
			sbBuildValueStr.append(" (");
			sbBuildValueStr.append(kernel.format(y, tpl));
			sbBuildValueStr.append(", ");
			sbBuildValueStr.append(kernel.format(-x, tpl));
			sbBuildValueStr.append(")");
			return sbBuildValueStr.toString();

		case EQUATION_IMPLICIT_NON_CANONICAL:
		case EQUATION_GENERAL:
			g[0] = x;
			g[1] = y;
			g[2] = z;
			if (DoubleUtil.isZero(x) || DoubleUtil.isZero(y)) {
				return kernel.buildExplicitEquation(g, vars, tpl,
						EQUATION_IMPLICIT_NON_CANONICAL == getToStringMode()).toString();
			}
			return kernel.buildImplicitEquation(g, vars,
					false, false, tpl,
					EQUATION_IMPLICIT_NON_CANONICAL == getToStringMode()).toString();
		case EQUATION_USER:
			if (getDefinition() != null) {
				return getDefinition().toValueString(tpl);
			}
			return buildImplicitEquation(g, tpl);
		default: // EQUATION_IMPLICIT
			return buildImplicitEquation(g, tpl);
		}
	}

	private String buildImplicitEquation(double[] g, StringTemplate tpl) {
		g[0] = x;
		g[1] = y;
		g[2] = z;
		if (DoubleUtil.isZero(x) || DoubleUtil.isZero(y)) {
			return kernel.buildExplicitEquation(g, vars, tpl, true).toString();
		}
		boolean useGCD = true;
		return kernel.buildImplicitEquation(g, vars, useGCD, false, tpl, true)
				.toString();
	}

	/**
	 * left hand side as String : ax + by + c
	 * 
	 * @param sb
	 *            string builder
	 * @param tpl
	 *            string template
	 */
	final public void toStringLHS(StringBuilder sb, StringTemplate tpl) {
		double[] g = new double[3];

		if (isDefined()) {
			g[0] = x;
			g[1] = y;
			g[2] = z;
			sb.append(kernel.buildLHS(g, vars, true, false, tpl));
			return;
		}
		sb.append(sbToStringLHS);
	}

	@Override
	protected void getStyleXML(StringBuilder sb) {
		super.getStyleXML(sb);
		// line thickness and type
		getLineStyleXML(sb);
		XMLBuilder.appendEquationTypeLine(sb, getToStringMode(), parameter);
	}

	/*
	 * Path interface
	 */

	@Override
	public boolean isClosedPath() {
		return false;
	}

	@Override
	public void pointChanged(GeoPointND P) {
		doPointChanged(P);
	}

	private void doPointChanged(GeoPointND P) {
		Coords coords = P.getCoordsInD2();
		PathParameter pp = P.getPathParameter();

		doPointChanged(coords, pp);

		P.setCoords2D(coords.getX(), coords.getY(), coords.getZ());
		P.updateCoordsFrom2D(false, null);
	}

	/**
	 * @param coords
	 *            changed point
	 * @param pp
	 *            path parameter of P
	 */
	public void doPointChanged(Coords coords, PathParameter pp) {
		pp.t = projectCoordsAndComputePathParam(coords);
	}

	protected double projectCoordsAndComputePathParam(Coords coords) {
		// project P on line
		double px = coords.getX() / coords.getZ();
		double py = coords.getY() / coords.getZ();

		// param of projection point on perpendicular line
		double t = -(z + x * px + y * py) / (x * x + y * y);
		// calculate projection point using perpendicular line

		if (x == 0 && Double.isInfinite(px) && Double.isFinite(py)) {
			py = -z / y;
		} else if (y == 0 && Double.isInfinite(py) && Double.isFinite(px)) {
			px = -z / x;
		} else {
			px += t * x;
			py += t * y;
		}
		coords.setX(px);
		coords.setY(py);
		coords.setZ(1);

		// set path parameter
		double spx = 0;
		double spy = 0;
		double spz = 1;
		if (startPoint != null) {
			spx = startPoint.x;
			spy = startPoint.y;
			spz = startPoint.z;
		} else {
			if (x != 0 && y != 0) {
				spx = -z * x / (x * x + y * y);
				spy = -z * y / (x * x + y * y);
			} else if (x != 0) {
				spx = -z / x;
			} else if (y != 0) {
				spy = -z / y;
			}
		}
		if (Math.abs(x) <= Math.abs(y)) {
			return (spz * px - spx) / (y * spz);
		} else {
			return (spy - spz * py) / (x * spz);
		}
	}

	@Override
	public void pathChanged(GeoPointND P) {
		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		if (!getKernel().usePathAndRegionParameters(P)) {
			pointChanged(P);
			return;
		}

		Coords coords = P.getCoordsInD2();
		PathParameter pp = P.getPathParameter();

		pathChanged(coords, pp);

		P.setCoords2D(coords.getX(), coords.getY(), coords.getZ());
		P.updateCoordsFrom2D(false, null);
	}

	/**
	 * This path changed =&gt; change P to lie on this path
	 * 
	 * @param P
	 *            coords of point on path
	 * @param pp
	 *            path parameter of that point
	 */
	public void pathChanged(Coords P, PathParameter pp) {
		// calc point for given parameter
		if (startPoint != null) {
			P.setX(startPoint.inhomX + pp.t * y);
			P.setY(startPoint.inhomY - pp.t * x);
			P.setZ(1.0);
		} else {
			double inhomX = 0;
			double inhomY = 0;
			if (x != 0 && y != 0) {
				inhomX = -z * x / (x * x + y * y);
				inhomY = -z * y / (x * x + y * y);
			} else if (x != 0) {
				inhomX = -z / x;
			} else if (y != 0) {
				inhomY = -z / y;
			}
			P.setX(inhomX + pp.t * y);
			P.setY(inhomY - pp.t * x);
			P.setZ(1.0);
		}
	}

	@Override
	public boolean isPath() {
		return true;
	}

	@Override
	public boolean isGeoLine() {
		return true;
	}

	/**
	 * Returns the smallest possible parameter value for this path (may be
	 * Double.NEGATIVE_INFINITY)
	 * 
	 * @return smallest possible parameter value for this path
	 */
	@Override
	public double getMinParameter() {
		return Double.NEGATIVE_INFINITY;
	}

	/**
	 * Returns the largest possible parameter value for this path (may be
	 * Double.POSITIVE_INFINITY)
	 * 
	 * @return largest possible parameter value for this path
	 */
	@Override
	public double getMaxParameter() {
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public PathMover createPathMover() {
		return new PathMoverLine();
	}

	/**
	 * @return slope of the line, NaN if vertical or undefined
	 */
	public double getSlope() {
		if (isDefined() && !DoubleUtil.isZero(y)) {
			return -x / y;
		} else {
			return Double.NaN;
		}
	}

	private class PathMoverLine extends PathMoverGeneric {

		public PathMoverLine() {
			super(GeoLine.this);
		}

		@Override
		public void init(GeoPointND p, int minSteps) {
			// we need a start point for pathChanged() to work correctly
			// with our path parameters
			if (startPoint == null) {
				setStandardStartPoint();
			}
			super.init(p, minSteps);
		}

	}

	/**
	 * TODO never used ?
	 * 
	 * @return ":"
	 */
	@Override
	public String getAssignmentOperator() {
		return ": ";
	}

	@Override
	public void matrixTransform(double p, double q, double r, double s) {
		double x1, y1;

		if (DoubleUtil.isZero(y)) {
			x1 = s;
			y1 = -q;
			setCoords(x1 * x, y1 * x, -q * r * z + s * p * z);
		} else {
			x1 = r * y - s * x;
			y1 = q * x - p * y;
			setCoords(x1 * y, y1 * y, q * z * x1 + s * z * y1);
		}
	}

	/**
	 * Creates a GeoFunction of the form f(x) = thisNumber needed for
	 * SumSquaredErrors[FitLine[]]
	 * 
	 * @return constant function
	 * @deprecated see parent
	 */
	@Deprecated
	@Override
	public GeoFunction getGeoFunction() {
		if (asFunction != null) {
			return asFunction;
		}
		GeoFunction ret = kernel.getGeoFactory().newFunction(this);
		if (!ret.isIndependent()) {
			asFunction = ret;
		}

		return ret;
	}

	@Override
	public boolean isRealValuedFunction() {
		return true;
	}

	@Override
	public boolean isMatrixTransformable() {
		return true;
	}

	/**
	 * @param con
	 *            conic to store result
	 */
	public void toGeoConic(GeoConic con) {
		con.fromLine(this);
	}

	@Override
	public double value(double x_var) {
		if (DoubleUtil.isZero(y)) {
			return Double.NaN;
		}
		return (-x * x_var - z) / y;
	}

	// ////////////////////////////////////
	// 3D stuff
	// ////////////////////////////////////

	@Override
	public boolean hasDrawable3D() {
		return true;
	}

	@Override
	public Coords getLabelPosition() {
		return getPointInD(3, 0.5).getInhomCoordsInSameDimension();
	}

	@Override
	public Coords getPointInD(int dimension, double lambda) {
		return getStartCoordsInD(dimension).copy()
				.addInsideMul(getDirectionInD(dimension), lambda);
	}

	/**
	 * @return inhom coords in dimension
	 * 
	 * @param dimension
	 *            2 or 3
	 */
	private Coords getStartCoordsInD(int dimension) {

		Coords startCoords;
		// TODO merge with getPointOnLine
		// point defined by parent algorithm
		if (startPoint != null && startPoint.isFinite()) {
			// startCoords=startPoint.getInhomCoordsInD(dimension);
			startCoords = startPoint.getCoordsInD(dimension);
		}
		// point on axis
		else {
			startCoords = new Coords(dimension + 1);
			if (Math.abs(x) > Math.abs(y)) {
				startCoords.setX(-z / x);
			} else {
				startCoords.setY(-z / y);
			}
			startCoords.set(dimension + 1, 1); // last homogeneous coord
		}

		return startCoords;
	}

	private Coords getDirectionInD(int dimension) {

		Coords direction = new Coords(dimension + 1);
		direction.setX(y);
		direction.setY(-x);

		return direction;
	}

	@Override
	public Coords getMainDirection() {
		return getDirectionInD(3);
	}

	@Override
	public Coords getDirectionForEquation() {
		return getDirectionInD(3);
	}

	@Override
	public Coords getCartesianEquationVector(CoordMatrix m) {
		if (m == null) {
			return new Coords(x, y, z);
		}
		Coords o = getStartInhomCoords();
		Coords d = getEndInhomCoords().sub(o);
		return CoordMatrixUtil.lineEquationVector(o, d, m);
	}

	@Override
	public Coords getStartInhomCoords() {
		if (startPoint != null && startPoint.isFinite()) {
			return startPoint.getInhomCoordsInD3();
		}
		return getStartCoordsInD(3);
	}

	@Override
	public Coords getEndInhomCoords() {
		if (getEndPoint() != null) {
			return getEndPoint().getInhomCoordsInD3();
		}
		return getPointInD(3, 1).getInhomCoordsInSameDimension();
	}

	@Override
	final public boolean isCasEvaluableObject() {
		return true;
	}

	@Override
	public Coords getDirectionInD3() {
		if (getEndPoint() == null) {
			return new Coords(-y, x, 0, 0);
		}

		return getEndInhomCoords().sub(getStartInhomCoords());
	}

	@Override
	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {
		double[][] b = MyMath.adjoint(a00, a01, a02, a10, a11, a12, a20, a21,
				a22);

		double x1 = b[0][0] * x + b[0][1] * y + b[0][2] * z;
		double y1 = b[1][0] * x + b[1][1] * y + b[1][2] * z;
		double z1 = b[2][0] * x + b[2][1] * y + b[2][2] * z;
		setCoords(x1, y1, z1);

	}

	// ///////
	// / for incidence checking
	// ///////

	// //////////////////////////////////
	// FROM GEOCONIC
	// //////////////////////////////////

	/**
	 * Returns a list of points that this line passes through. May return null.
	 * 
	 * @return list of points that this line passes through.
	 */
	public final ArrayList<GeoPoint> getPointsOnLine() {
		return pointsOnLine;
	}

	/**
	 * Adds a point to the list of points that this line passes through.
	 * 
	 * @param p
	 *            point tobe added
	 */
	@Override
	public final void addPointOnLine(GeoPointND p) {
		if (pointsOnLine == null) {
			pointsOnLine = new ArrayList<>();
		}

		if (!pointsOnLine.contains(p)) {
			pointsOnLine.add((GeoPoint) p);
		}
	}

	/**
	 * Removes a point from the list of points that this line passes through.
	 * 
	 * @param p
	 *            Point to be removed
	 */
	@Override
	public final void removePointOnLine(GeoPointND p) {
		if (pointsOnLine != null) {
			pointsOnLine.remove(p);
		}
	}

	@Override
	public void doRemove() {
		if (getStartPoint() != null) {
			getStartPoint().removeIncidence(this);
		}
		if (getEndPoint() != null) {
			getEndPoint().removeIncidence(this);
		}
		if (pointsOnLine != null) {
			for (int i = 0; i < pointsOnLine.size(); ++i) {
				GeoPoint p = pointsOnLine.get(i);
				p.removeIncidence(this);
			}
		}

		super.doRemove();
	}

	@Override
	public Function getFunctionForRoot() {
		return createLinearFunction(x, z);
	}

	@Override
	public GeoFunction getGeoDerivative(int order, boolean fast) {
		return getGeoFunction().getGeoDerivative(order, fast);
	}

	@Override
	public SymbolicParameters getSymbolicParameters() {
		if (algoParent != null
				&& (algoParent instanceof SymbolicParametersAlgo)) {
			return new SymbolicParameters((SymbolicParametersAlgo) algoParent);
		}
		return null;
	}

	@Override
	public void getFreeVariables(HashSet<PVariable> variables)
			throws NoSymbolicParametersException {
		if (algoParent instanceof SymbolicParametersAlgo) {
			((SymbolicParametersAlgo) algoParent).getFreeVariables(variables);
			return;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public int[] getDegrees(AbstractProverReciosMethod a)
			throws NoSymbolicParametersException {
		if (algoParent instanceof SymbolicParametersAlgo) {
			return ((SymbolicParametersAlgo) algoParent).getDegrees(a);
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public BigInteger[] getExactCoordinates(
			final HashMap<PVariable, BigInteger> values)
			throws NoSymbolicParametersException {
		if (algoParent instanceof SymbolicParametersAlgo) {
			return ((SymbolicParametersAlgo) algoParent)
					.getExactCoordinates(values);
		}
		return null;
	}

	@Override
	public PPolynomial[] getPolynomials() throws NoSymbolicParametersException {
		if (algoParent instanceof SymbolicParametersAlgo) {
			return ((SymbolicParametersAlgo) algoParent).getPolynomials();
		}
		throw new NoSymbolicParametersException();
	}

	/**
	 * Botana's theorem proving subsystem can handle axes and fixed slope lines
	 * if a locus equation is requested. Otherwise (in strict/general theorem
	 * proving) they are unsupported (because they are not synthetic).
	 * 
	 * @return the line has fixed slope (e.g. an axis or defined by an equation)
	 */
	public boolean hasFixedSlope() {
		return (this instanceof GeoAxis) || this.getParentAlgorithm() == null;
	}

	@Override
	public PVariable[] getBotanaVars(GeoElementND geo)
			throws NoSymbolicParametersException {
		if (algoParent instanceof SymbolicParametersBotanaAlgo) {
			return ((SymbolicParametersBotanaAlgo) algoParent)
					.getBotanaVars(this);
		}
		if (hasFixedSlope()) {
			if (botanaVars == null) {
				botanaVars = new PVariable[4];
				botanaVars[0] = new PVariable(kernel); // ,true
				botanaVars[1] = new PVariable(kernel); // ,true
				botanaVars[2] = new PVariable(kernel); // ,true
				botanaVars[3] = new PVariable(kernel); // ,true
				// we substitute them in AlgoLocusEquation, not here
			}
			return botanaVars;
		}
		return null;
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {
		if (algoParent instanceof SymbolicParametersBotanaAlgo) {
			return ((SymbolicParametersBotanaAlgo) algoParent)
					.getBotanaPolynomials(this);
		}
		if (hasFixedSlope()) {
			// we construct the polynomials in AlgoLocusEquation, not here
			return null;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public double distance(GeoLineND g) {
		return distance((GeoLine) g);
	}

	/**
	 * Whether the parameter is within acceptable range
	 * 
	 * @param param
	 *            path parameter
	 */
	@Override
	public boolean respectLimitedPath(double param) {
		return true;
	}

	/**
	 * normalize coeffients so that Intersect[ (x - 1.62010081566832)^2 + (y +
	 * 31.674457260881873)^2 = 0.028900000000021 , 0.000158120368003x +
	 * 0.000144840828995y = -0.004331583710062 ] works
	 * 
	 * Also needed for repeated use of PerpendicularLine()
	 * 
	 * @param ret output array
	 * @param max at least one coefficient has to be lower or equal to this bound
	 * @param min at least one coefficient has to be greater or equal to this bound
	 * 
	 * @return normalized coefficients x,y,z
	 */
	public double[] getnormalizedCoefficients(double[] ret, double max, double min) {
		ret[0] = x;
		ret[1] = y;
		ret[2] = z;

		if (DoubleUtil.isZero(x) && DoubleUtil.isZero(y) && DoubleUtil.isZero(z)) {
			return ret;
		}

		if (!Double.isFinite(x) || !Double.isFinite(y)
				|| !Double.isFinite(z)) {
			return ret;
		}

		while (Math.abs(ret[0]) < min
				&& Math.abs(ret[1]) < min
				&& Math.abs(ret[2]) < min) {
			ret[0] *= 2;
			ret[1] *= 2;
			ret[2] *= 2;
		}

		while (Math.abs(ret[0]) > max
				&& Math.abs(ret[1]) > max
				&& Math.abs(ret[2]) > max) {
			ret[0] /= 2;
			ret[1] /= 2;
			ret[2] /= 2;
		}

		return ret;
	}

	@Override
	public boolean hasLineOpacity() {
		return true;
	}

	@Override
	public boolean isParametric() {
		return getToStringMode() == GeoLine.PARAMETRIC;
	}

	@Override
	public ValueType getValueType() {
		return getToStringMode() == GeoLine.PARAMETRIC ? ValueType.PARAMETRIC2D
				: ValueType.EQUATION;
	}

	@Override
	public ExtendedBoolean isCongruent(GeoElement geo) {
		return ExtendedBoolean.newExtendedBoolean(geo.isGeoLine());
	}

	@Override
	public char getLabelDelimiter() {
		return ':';
	}

	@Override
	public Coords getOrigin() {
		return getStartPoint().getCoordsInD3();
	}

	@Override
	public Equation getEquation() {
		FunctionVariable fvx = new FunctionVariable(kernel, "x");
		FunctionVariable fvy = new FunctionVariable(kernel, "y");

		ExpressionNode lhs = new ExpressionNode(kernel, this.x).multiply(fvx);

		lhs = lhs.plus(new ExpressionNode(kernel, this.y).multiply(fvy));

		return new Equation(kernel, lhs,
				new ExpressionNode(kernel, this.z).multiply(-1));
	}

	/**
	 * used by GeoSegment/Ray/3D to set start/end points
	 * 
	 * @param cons
	 *            cons
	 * @param my
	 *            my point
	 * @param other
	 *            point from other geo
	 * @return what my start/end point should be
	 */
	public static GeoPointND updatePoint(Construction cons, GeoPointND my,
			GeoPointND other) {
		if (my == null) {
			if (other == null) {
				return null;
			}
			return (GeoPointND) other.copyInternal(cons);
		}
		if (other != null) {
			ExpressionNode oldDef = my.getDefinition();
			my.set(other);
			if (!my.isIndependent()) {
				my.setDefinition(oldDef);
			}
		}
		return my;
	}

	@Override
	public void setToGeneral() {
		setModeIfEquationFormIsNotForced(EQUATION_GENERAL);
	}

	@Override
	public boolean isLaTeXDrawableGeo() {
		return getToStringMode() == GeoLine.EQUATION_USER
				&& getDefinition() != null;
	}

	@Override
	public String[] getEquationVariables() {
		ArrayList<String> usedVars = new ArrayList<>();
		if (!MyDouble.exactEqual(x, 0)) {
			usedVars.add("x");
		}
		if (!MyDouble.exactEqual(y, 0)) {
			usedVars.add("y");
		}
		addUsedVars(usedVars, getDefinition());
		return usedVars.toArray(new String[0]);
	}

	/**
	 * @param usedVars
	 *            output list of vars
	 * @param definition
	 *            (wrapped) equation
	 */
	public static void addUsedVars(ArrayList<String> usedVars,
			ExpressionNode definition) {
		if (usedVars.isEmpty() && definition != null
				&& definition.unwrap() instanceof Equation) {
			if (((Equation) definition.unwrap())
					.containsFreeFunctionVariable("x")) {
				usedVars.add("x");
			}
			if (((Equation) definition.unwrap())
					.containsFreeFunctionVariable("y")) {
				usedVars.add("y");
			}
			if (((Equation) definition.unwrap())
					.containsFreeFunctionVariable("z")) {
				usedVars.add("z");
			}
		}

	}

	@Override
	public ExpressionValue evaluateCurve(double t) {
		double[] P = new double[2];
		getInhomPointOnLine(P);
		return new GeoVec2D(kernel, P[0] + t * y, P[1] - t * x);
	}

	@Override
	public DescriptionMode getDescriptionMode() {
		if (toStringMode == GeoLine.EQUATION_USER
				&& (isIndependent() || (getParentAlgorithm().getClassName() == Algos.Expression
				&& isAllowedToShowValue()))) {
			return DescriptionMode.VALUE;
		}
		return super.getDescriptionMode();
	}

	@Override
	public boolean setTypeFromXML(String style, String parameter, boolean force) {
		if (isEquationFormEnforced()) {
			ignoreLineModeFromXML(style);
			return true;
		}

		if ("implicit".equals(style)) {
			setToImplicit();
		} else if ("explicit".equals(style)) {
			setToExplicit(force);
		} else if ("parametric".equals(style)) {
			setToParametric(parameter);
		} else if ("user".equals(style)) {
			setToUser();
		} else if ("general".equals(style)) {
			setToGeneral();
		} else {
			return false;
		}
		return true;
	}

	private void ignoreLineModeFromXML(String style) {
		if ("user".equals(style)) {
			setToUser();
		} else {
			setToExplicit(true);
		}
	}

	@Override
	protected boolean canHaveSpecialPoints() {
		return true;
	}

	@Override
	public Function getFunction() {
		Function definitionFn = definitionAsFunction(getDefinition());
		if (definitionFn != null) {
			return definitionFn;
		}
		// f(x_var) = -x/y x_var - z/y
		return createLinearFunction(-x / y, -z / y);
	}

	/**
	 * @param definition definition
	 * @return definition converted to function
	 */
	public static Function definitionAsFunction(ExpressionNode definition) {
		if (definition != null && definition.unwrap() instanceof Equation) {
			return ((Equation) definition.unwrap()).asFunction();
		}
		return null;
	}

	private Function createLinearFunction(double slope, double constant) {
		FunctionVariable xVariable = new FunctionVariable(kernel);
		ExpressionNode linear = new ExpressionNode(kernel,
				new MyDouble(kernel, slope), Operation.MULTIPLY, xVariable);
		return new Function(linear.plus(constant), xVariable);
	}

	@Override
	public boolean hasTableOfValues() {
		return getDefinition() != null && !DoubleUtil.isZero(getY()) && super.hasTableOfValues();
	}

	@Override
	public int getTableColumn() {
		return this.tableColumn;
	}

	@Override
	public void setTableColumn(int col) {
		tableColumn = col;
	}

	@Override
	public boolean isPointsVisible() {
		return pointsVisible;
	}

	@Override
	public void setPointsVisible(boolean pointsVisible) {
		this.pointsVisible = pointsVisible;
	}

	@Override
	public void setAllVisualPropertiesExceptEuclidianVisible(GeoElement geo,
			boolean keepAdvanced, boolean setAuxiliaryProperty) {
		super.setAllVisualPropertiesExceptEuclidianVisible(geo, keepAdvanced,
				setAuxiliaryProperty);
		if (geo instanceof GeoEvaluatable) {
			TableProperties.transfer(geo, this);
		}
	}

	@Override
	public boolean isPolynomialFunction(boolean forRoot) {
		return true;
	}

	protected void setModeIfEquationFormIsNotForced(int mode) {
		if (isEquationFormEnforced()) {
			toStringMode = cons.getApplication().getConfig().getEnforcedLineEquationForm();
		} else {
			toStringMode = mode;
		}
	}

	private boolean isEquationFormEnforced() {
		return cons.getApplication().getConfig().getEnforcedLineEquationForm() != -1;
	}

	private void setModeWithImplicitEquationAsDefault(int mode) {
		switch (mode) {
			case PARAMETRIC:
			case EQUATION_EXPLICIT:
			case EQUATION_IMPLICIT_NON_CANONICAL:
			case EQUATION_GENERAL:
			case EQUATION_USER:
				toStringMode = mode;
				break;

			default:
				toStringMode = EQUATION_IMPLICIT;
		}
	}

	@Override
	public boolean hasSpecialEditor() {
		return false;
	}
}
