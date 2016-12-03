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
import org.geogebra.common.kernel.LocusEquation;
import org.geogebra.common.kernel.MatrixTransformable;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.PathMover;
import org.geogebra.common.kernel.PathMoverGeneric;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.CoordMatrix;
import org.geogebra.common.kernel.Matrix.CoordMatrixUtil;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoDependentFunction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.EquationElementInterface;
import org.geogebra.common.kernel.algos.SymbolicParameters;
import org.geogebra.common.kernel.algos.SymbolicParametersAlgo;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import org.geogebra.common.kernel.algos.TangentAlgo;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Functional;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.locusequ.EquationScope;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.MyMath;

/**
 * Geometrical representation of line
 * 
 * @author Markus
 * 
 */
public class GeoLine extends GeoVec3D implements Path, Translateable,
		PointRotateable, Mirrorable, Dilateable, GeoLineND,
		MatrixTransformable, GeoFunctionable, Transformable, Functional,
		SymbolicParametersAlgo, SymbolicParametersBotanaAlgo, EquationValue {

	// modes
	/** implicit equation */
	public static final int EQUATION_IMPLICIT = 0;
	/** explicit equation */
	public static final int EQUATION_EXPLICIT = 1;
	/** parametric equation*/
	public static final int PARAMETRIC = 2;
	/** non-canonical implicit equation */
	public static final int EQUATION_IMPLICIT_NON_CANONICAL = 3;
	/** general form **/
	public static final int EQUATION_GENERAL = 4;
	/** user input **/
	public static final int EQUATION_USER = 5;
	private boolean showUndefinedInAlgebraView = false;

	private String parameter = "\u03bb";
	/** start point */
	public GeoPoint startPoint;
	/** end point*/
	public GeoPoint endPoint;

	// enable negative sign of first coefficient in implicit equations
	private static boolean KEEP_LEADING_SIGN = true;
	private static final String[] vars = { "x", "y" };

	private Variable[] botanaVars; // only for an axis or a fixed slope line

	/**
	 * Creates new line
	 * @param c construction
	 */
	public GeoLine(Construction c) {
		super(c);
		setConstructionDefaults();
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
	 * @param cons construction
	 * @param a x-coefficient
	 * @param b y-coefficient
	 * @param c z-coefficient
	 */
	public GeoLine(Construction cons, double a, double b, double c) {
		super(cons, a, b, c); // GeoVec3D constructor
		setConstructionDefaults();
	}
	/**
	 * Copy constructor
	 * @param line line to copy
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
		// Application.debug("x="+x+", y="+y+", z="+z);
	}
	
	
	/**
	 * set the line to pass through (pointX, pointY)
	 * @param pointX x coord
	 * @param pointY y coord
	 */
	final public void setLineThrough(double pointX, double pointY) {
		setCoords(x, y, -((x * pointX) + (y * pointY)));
	}

	@Override
	final public void setCoords(GeoVec3D v) {

		setCoords(v.x, v.y, v.z);
		/*
		 * x = v.x; y = v.y; z = v.z;
		 */
	}

	/**
	 * returns true if P lies on this line
	 * 
	 * @param p
	 *            point
	 * @param eps
	 *            precision
	 * @return true if P lies on this line
	 * */
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
		if (!P.isDefined())
			return false;

		double simplelength = Math.abs(x) + Math.abs(y);
		if (P.isInfinite()) {
			return Math.abs(x * P.x + y * P.y) < eps * simplelength;
		}
		// STANDARD CASE: finite point
		return Math.abs(x * P.inhomX + y * P.inhomY + z) < eps * simplelength;
	}

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
		if (Kernel.isZero(P.getZ())) { // infinite point
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

		// remember the old point coordinates
		double px = P.x, py = P.y, pz = P.z;
		PathParameter tempParam = getTempPathParameter();
		PathParameter pp = P.getPathParameter();
		tempParam.set(pp);

		// make sure we use point changed for a line to get parameters on
		// the entire line when this is a segment or ray
		doPointChanged(P);

		boolean result;
		switch (classType) {
		case SEGMENT:
			// segment: parameter in [0,1]
			result = pp.t >= -eps && pp.t <= 1 + eps;
			break;

		case RAY:
			// ray: parameter > 0
			result = pp.t >= -eps;
			break;

		default:
			// line: any parameter
			result = true;
		}

		// restore old values
		P.x = px;
		P.y = py;
		P.z = pz;
		pp.set(tempParam);

		return result;
	}

	public boolean isOnPath(Coords Pnd, double eps) {
		Coords P2d = Pnd.getCoordsIn2DView();
		return isOnFullLine2D(P2d, eps);
	}

	public boolean respectLimitedPath(Coords coords, double eps) {
		return true;
	}

	/**
	 * return a possible parameter for the point P (return the parameter for the
	 * projection of P on the path)
	 * 
	 * @param coords point whose possible parameter we need
	 * @return a possible parameter for the point P
	 */
	public double getPossibleParameter(Coords coords) {

		PathParameter tempParam = getTempPathParameter();

		// make sure we use point changed for a line to get parameters on
		// the entire line when this is a segment or ray
		doPointChanged(coords, tempParam);

		return tempParam.t;
	}

	private PathParameter tempPP;

	/**
	 * @return temporary path parameter
	 */
	protected PathParameter getTempPathParameter() {
		if (tempPP == null)
			tempPP = new PathParameter();
		return tempPP;
	}

	/**
	 * @param g
	 *            line
	 * @return true if this line and g are parallel
	 */
	final public boolean isParallel(GeoLine g) {
		return Kernel.isEqual(g.x * y, g.y * x);
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
		return Kernel.isEqual(g.x * x, -g.y * y);
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
	 * @param x0 x coord
	 * @param y0 y coord
	 * @return distance
	 */
	public double distance(double x0, double y0) {
		return Math
				.abs((x * x0 + y * y0 + z) / MyMath.length(x, y));
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
		return Math.abs((x * p.x / p.z + y * p.y / p.z + z)
				/ MyMath.length(x, y));
	}

	/**
	 * 
	 * @param p coords to which we compute the distance
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
		if (Kernel.isZero(g.x * y - g.y * x)) {
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
	 * @param out vector to store direction
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
	 * Set standard start point (closest to (0,0)).
	 * Needed for path parameter to work correctly.
	 */
	public final void setStandardStartPoint() {

		if (startPoint == null) {
			startPoint = new GeoPoint(cons);
			startPoint.addIncidence(this, true);
		}

		// this way the behaviour of pathChanged and pointChanged remain
		// the same as if there weren't a startPoint
		// so the dependent path parameters (probably) needn't be changed
		if (x != 0 || y != 0) {
			startPoint.setCoords(-z * x / (x * x + y * y), -z * y
					/ (x * x + y * y), 1.0);
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
	}

	/**
	 * @param P start point
	 */
	public final void setStartPoint(GeoPoint P) {
		if (startPoint == P)
			return;
		
		startPoint = P;
		if (P != null)
			P.addIncidence(this, true);
	}

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
	 * @param Q end point
	 */
	public final void setEndPoint(GeoPoint Q) {
		if (endPoint == Q)
			return;
		
		endPoint = Q;
		if (Q != null)
			Q.addIncidence(this, true);
	}

	/**
	 * Retuns first defining point of this line or null.
	 */
	final public GeoPoint getStartPoint() {
		return startPoint;
	}

	/**
	 * Retuns second point of this line or null.
	 */
	final public GeoPoint getEndPoint() {
		return endPoint;
	}

	@Override
	public boolean isDefined() {
		return (!(Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z)) && !(Kernel
				.isZero(x) && Kernel.isZero(y)));
	}

	@Override
	protected boolean showInEuclidianView() {
		// defined
		return isDefined();
	}

	@Override
	public boolean showInAlgebraView() {
		// independent or defined
		// return isIndependent() || isDefined();

		return isDefined() || showUndefinedInAlgebraView;
	}

	/**
	 * Set whether this line should be visible in AV when undefined 
	 * @param flag true to show undefined
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
	public boolean isEqual(GeoElement geo) {
		
		if (!geo.isDefined() || !isDefined()) {
			return false;
		}
		
		// support c==f for Line, Function
		if (geo.isGeoFunction()) {
			PolyFunction poly = ((GeoFunction) geo).getFunction().expandToPolyFunction(((GeoFunction) geo).getFunctionExpression(), false, true);
			
			if (poly == null) {
				// (probably) not a polynomial
				return false;
			}
			
			int degree = poly.getDegree();
			
			if (degree > 1) {
				// not linear
				return false;
			}
			
			double[] coeffs = poly.getCoeffs();
			
			if (degree == 0) {
				if (Kernel.isEqual(x, 0)
						&& Kernel.isEqual(-z/y, coeffs[0])) {
					return true;
				}
				
			} else {
				// f(x_var) = -x/y x_var - z/y
				if (Kernel.isEqual(-x/y, coeffs[1])
						&& Kernel.isEqual(-z/y, coeffs[0])) {
					return true;
				}
			}
			
			return false;
		}
		
		// return false if it's a different type, otherwise use equals() method
		if (geo.isGeoRay() || geo.isGeoSegment()) {
			return false;
		}
		if (geo.isGeoLine()) {
			return linDep((GeoLine) geo);
		}
		return false;
	}

	/**
	 * yields true if this line is defined as a tangent of conic c
	 * 
	 * @param c
	 *            conic
	 * @return true iff defined as tangent of given conic
	 */
	final public boolean isDefinedTangent(GeoConic c) {
		boolean isTangent = false;

		Object ob = getParentAlgorithm();
		if (ob instanceof TangentAlgo) {
			GeoElement[] input = ((AlgoElement) ob).getInput();
			for (int i = 0; i < input.length; i++) {
				if (input[i] == c) {
					isTangent = true;
					break;
				}
			}
		}
		return isTangent;
	}

	/**
	 * yields true if this line is defined as a asymptote of conic c
	 * 
	 * @param c
	 *            conic
	 * @return true iff defined as a asymptote of conic c
	 */
	final public boolean isDefinedAsymptote(GeoConic c) {
		boolean isAsymptote = false;

		AlgoElement ob = getParentAlgorithm();
		if (ob != null && ob.getClassName().equals(Commands.Asymptote)) {
			GeoElement[] input = ob.getInput();
			for (int i = 0; i < input.length; i++) {
				if (input[i] == c) {
					isAsymptote = true;
					break;
				}
			}
		}
		return isAsymptote;
	}

	/***********************************************************
	 * MOVEMENTS
	 ***********************************************************/

	/**
	 * translate by vector v
	 */
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
	public void dilate(NumberValue rval, Coords S) {
		double r = rval.getDouble();
		double temp = (r - 1);
		z = temp * (x * S.getX() + y * S.getY()) + r * z;

		x *= r;
		y *= r;
		z *= r;
	}

	/**
	 * rotate this line by angle phi around (0,0)
	 */
	public void rotate(NumberValue phiVal) {
		rotateXY(phiVal);
	}

	/**
	 * rotate this line by angle phi around Q
	 */
	public void rotate(NumberValue phiVal,  GeoPointND point) {
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
	final public void setToParametric(String parameter) {
		setMode(GeoLine.PARAMETRIC);
		if (parameter != null && parameter.length() > 0)
			this.parameter = parameter;
	}

	/** change equation mode to explicit */
	final public void setToExplicit() {
		setMode(EQUATION_EXPLICIT);
	}
	
	/** set equation mode to implicit */
	final public void setToImplicit() {
		setMode(EQUATION_IMPLICIT);
	}

	final public void setToUser() {
		setMode(EQUATION_USER);
	}

	@Override
	final public void setMode(int mode) {
		switch (mode) {
		case PARAMETRIC:
			toStringMode = PARAMETRIC;
			break;

		case EQUATION_EXPLICIT:
			toStringMode = EQUATION_EXPLICIT;
			break;

		case EQUATION_IMPLICIT_NON_CANONICAL:
			toStringMode = EQUATION_IMPLICIT_NON_CANONICAL;
			break;

		case EQUATION_GENERAL:
			toStringMode = EQUATION_GENERAL;
			break;
		case EQUATION_USER:
			toStringMode = EQUATION_USER;
			break;

		default:
			toStringMode = EQUATION_IMPLICIT;
		}
	}

	/** output depends on mode: PARAMETRIC or EQUATION */
	@Override
	public String toString(StringTemplate tpl) {
		StringBuilder sbToStr = getSbToString();
		sbToStr.setLength(0);
		sbToStr.append(label);
		sbToStr.append(": ");
		sbToStr.append(buildValueString(tpl).toString());
		return sbToStr.toString();
	}

	private StringBuilder sbToString;

	private StringBuilder getSbToString() {
		if (sbToString == null)
			sbToString = new StringBuilder(50);
		return sbToString;
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return buildValueString(tpl).toString();
	}

	@Override
	public String toStringMinimal(StringTemplate tpl) {
		StringBuilder sbToStr = getSbToString();
		sbToStr.setLength(0);
		getXMLtagsMinimal(sbToStr,tpl);
		return sbToStr.toString();
	}

	private StringBuilder buildValueString(StringTemplate tpl) {
		if (tpl.hasCASType()) {
			if (getDefinition() != null) {
				StringBuilder sb = getSbBuildValueString();
				sb.append(getDefinition().toValueString(tpl));
				return sb;
			}
			double[] numbers = new double[3];
			numbers[0] = x;
			numbers[1] = y;
			numbers[2] = z;
			double gcd = Kernel.gcd(numbers);
			StringBuilder sb = getSbBuildValueString();
			sb.append("(");
			if (gcd != 1 && !Kernel.isZero(gcd)) {
				sb.append(kernel.format(x / gcd, tpl));
			} else {
				sb.append(kernel.format(x, tpl));
			}
			sb.append(")*");
			sb.append(tpl.printVariableName("x"));
			sb.append("+(");
			if (gcd != 1 && !Kernel.isZero(gcd)) {
				sb.append(kernel.format(y / gcd, tpl));
			} else {
				sb.append(kernel.format(y, tpl));
			}
			sb.append(")*");
			sb.append(tpl.printVariableName("y"));
			sb.append('=');
			if (gcd != 1 && !Kernel.isZero(gcd)) {
				sb.append(kernel.format(-z / gcd, tpl));
			} else {
				sb.append(kernel.format(-z, tpl));
			}
			return sb;
		}
		if (!kernel.getApplication().getSettings().getCasSettings()
				.isEnabled()) {
			toStringMode = GeoLine.EQUATION_USER;
		}
		double[] P = new double[2];
		double[] g = new double[3];
		char op = '=';
		
		if (!isDefined()) {
			return new StringBuilder((toStringMode == PARAMETRIC) ? "X = (?, ?)" : "y = ?"); 
		}
		
		switch (toStringMode) {
		case EQUATION_EXPLICIT: // /EQUATION
			g[0] = x;
			g[1] = y;
			g[2] = z;
			return kernel.buildExplicitEquation(g, vars, op, tpl,
					true);

		case PARAMETRIC:
			getInhomPointOnLine(P); // point
			StringBuilder sbBuildValueStr = getSbBuildValueString();
			GeoCasCell casCell = getCorrespondingCasCell();
			if (casCell == null || !casCell.isAssignmentVariableDefined()) {
				sbBuildValueStr.append("X = ");
			}
			sbBuildValueStr.append("(");
			sbBuildValueStr.append(kernel.format(P[0],tpl));
			sbBuildValueStr.append(", ");
			sbBuildValueStr.append(kernel.format(P[1],tpl));
			sbBuildValueStr.append(") + ");
			sbBuildValueStr.append(parameter);
			sbBuildValueStr.append(" (");
			sbBuildValueStr.append(kernel.format(y,tpl));
			sbBuildValueStr.append(", ");
			sbBuildValueStr.append(kernel.format(-x,tpl));
			sbBuildValueStr.append(")");
			return sbBuildValueStr;

		case EQUATION_IMPLICIT_NON_CANONICAL:
		case EQUATION_GENERAL:
			g[0] = x;
			g[1] = y;
			g[2] = z;
			if (Kernel.isZero(x) || Kernel.isZero(y)) {
				return kernel.buildExplicitEquation(g, vars, op, tpl,
						EQUATION_IMPLICIT_NON_CANONICAL == toStringMode);
			}
			return kernel.buildImplicitEquation(g, vars, KEEP_LEADING_SIGN,
					false, false, op, tpl,
					EQUATION_IMPLICIT_NON_CANONICAL == toStringMode);
		case EQUATION_USER:
			if (getDefinition() != null) {
				return new StringBuilder(getDefinition().toValueString(tpl));
			}
			return buildImplicitEquation(g, tpl, op);
		default: // EQUATION_IMPLICIT
			return buildImplicitEquation(g, tpl, op);
		}
	}

	private StringBuilder buildImplicitEquation(double[] g, StringTemplate tpl,
			char op) {
		g[0] = x;
		g[1] = y;
		g[2] = z;
		if (Kernel.isZero(x) || Kernel.isZero(y)) {
			return kernel.buildExplicitEquation(g, vars, op, tpl, true);
		}
		if (kernel.getAlgebraProcessor().getDisableGcd()) {
			return kernel.buildImplicitEquation(g, vars, KEEP_LEADING_SIGN,
					false, false, op, tpl, true);
		}
		return kernel.buildImplicitEquation(g, vars, KEEP_LEADING_SIGN, true,
				false, op, tpl, true);

	}

	private StringBuilder sbBuildValueString = new StringBuilder(50);

	private StringBuilder getSbBuildValueString() {
		if (sbBuildValueString == null) {
			sbBuildValueString = new StringBuilder();
		} else {
			// needed for GGB-719
			sbBuildValueString.setLength(0);
		}
		return sbBuildValueString;
	}

	/**
	 * left hand side as String : ax + by + c
	 * @param sb string builder
	 * @param tpl string template
	 */
	final public void toStringLHS(StringBuilder sb,StringTemplate tpl) {
		double[] g = new double[3];

		if (isDefined()) {
			g[0] = x;
			g[1] = y;
			g[2] = z;
			sb.append(kernel.buildLHS(g, vars, KEEP_LEADING_SIGN, true, false, tpl));
			return;
		}
		sb.append(sbToStringLHS);
	}

	private static StringBuilder sbToStringLHS = new StringBuilder("\u221E");

	/**
	 * returns all class-specific xml tags for saveXML GeoGebra File Format
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
		// line thickness and type
		getLineStyleXML(sb);

		// prametric, explicit or implicit mode
		switch (toStringMode) {
		case GeoLine.PARAMETRIC:
			sb.append("\t<eqnStyle style=\"parametric\" parameter=\"");
			sb.append(parameter);
			sb.append("\"/>\n");
			break;

		case GeoLine.EQUATION_EXPLICIT:
			Equation.appendType(sb, "explicit");
			break;
		case GeoLine.EQUATION_GENERAL:
			Equation.appendType(sb, "general");
			break;
		case GeoLine.EQUATION_USER:
			Equation.appendType(sb, "user");
			break;

		case GeoLine.EQUATION_IMPLICIT_NON_CANONICAL:
			// don't want anything here
			break;

		default:
			Equation.appendType(sb, "implicit");
		}

	}

	/*
	 * Path interface
	 */

	public boolean isClosedPath() {
		return false;
	}

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
	 * @param coords changed point
	 * @param pp path parameter of P
	 */
	public void doPointChanged(Coords coords, PathParameter pp) {

		// project P on line
		double px = coords.getX() / coords.getZ();
		double py = coords.getY() / coords.getZ();
		// param of projection point on perpendicular line
		double t = -(z + x * px + y * py) / (x * x + y * y);
		// calculate projection point using perpendicular line
		px += t * x;
		py += t * y;

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
			pp.t = (spz * px - spx) / (y * spz);
		} else {
			pp.t = (spy - spz * py) / (x * spz);
		}

	}

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
	 * This path changed => change P to lie on this path
	 * @param P coords of point on path
	 * @param pp path parameter of that point
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
	public double getMinParameter() {
		return Double.NEGATIVE_INFINITY;
	}

	/**
	 * Returns the largest possible parameter value for this path (may be
	 * Double.POSITIVE_INFINITY)
	 * 
	 * @return largest possible parameter value for this path
	 */
	public double getMaxParameter() {
		return Double.POSITIVE_INFINITY;
	}

	public PathMover createPathMover() {
		return new PathMoverLine();
	}

	private class PathMoverLine extends PathMoverGeneric {

		// private GeoPoint moverStartPoint;

		public PathMoverLine() {
			super(GeoLine.this);
		}

		@Override
		public void init(GeoPointND p, int minSteps) {
			// we need a start point for pathChanged() to work correctly
			// with our path parameters
			if (startPoint == null) {
				// moverStartPoint = new GeoPoint(cons);
				setStandardStartPoint();
			}

			// if (moverStartPoint != null) {
			// moverStartPoint.setCoords(p);
			// point p is on the line and we use it's location
			// as the startpoint, thus p needs to get path parameter 0
			// PathParameter pp = p.getPathParameter();
			// pp.t = 0;
			// }

			super.init(p, minSteps);

			// // we need a point on the line:
			// // p is a point on the line ;-)
			// moverStartPoint.setCoords(p);
			// PathParameter pp = p.getPathParameter();
			// pp.t = 0;
			// start_param = 0;
			//
			// min_param = -1 + PathMover.OPEN_BORDER_OFFSET;
			// max_param = 1 - PathMover.OPEN_BORDER_OFFSET;
			//
			// param_extent = max_param - min_param;
			// max_step_width = param_extent / MIN_STEPS;
			// posOrientation = true;
			//
			// resetStartParameter();
		}

		// protected void calcPoint(GeoPoint p) {
		// PathParameter pp = p.getPathParameter();
		// pp.t = PathMoverGeneric.infFunction(curr_param);
		// p.x = moverStartPoint.inhomX + pp.t * y;
		// p.y = moverStartPoint.inhomY - pp.t * x;
		// p.z = 1.0;
		// p.updateCoords();
		// }
		//
		// public boolean hasNext() {
		// // check if we pass the start parameter 0:
		// // i.e. check if the sign will change from
		// // last_param to the next parameter curr_param
		// double next_param = curr_param + step_width;
		// if (posOrientation)
		// return !(curr_param < 0 && next_param >= 0);
		// else
		// return !(curr_param > 0 && next_param <= 0);
		// }
	}
/*
	public void add(GeoLine line) {
		x += line.x;
		y += line.y;
		z += line.z;
	}

	public void subtract(GeoLine line) {
		x -= line.x;
		y -= line.y;
		z -= line.z;
	}

	public void multiply(GeoLine line) {
		x *= line.x;
		y *= line.y;
		z *= line.z;
	}

	public void divide(GeoLine line) {
		x /= line.x;
		y /= line.y;
		z /= line.z;
	}
*/
	@Override
	public void setZero() {
		setCoords(0, 1, 0);
	}

	/**
	 * TODO never used ?
	 * @return ":"
	 */
	@Override
	public String getAssignmentOperator() {
		return ": ";

	}

	public void matrixTransform(double p, double q, double r, double s) {

		double x1, y1;

		if (Kernel.isZero(y)) {
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
	 */
	public GeoFunction getGeoFunction() {
		GeoFunction ret;

		FunctionVariable fv = new FunctionVariable(kernel);

		ExpressionNode xCoord = new ExpressionNode(kernel, this,
				Operation.XCOORD, null);

		ExpressionNode yCoord = new ExpressionNode(kernel, this,
				Operation.YCOORD, null);

		ExpressionNode zCoord = new ExpressionNode(kernel, this,
				Operation.ZCOORD, null);

		// f(x_var) = -x/y x_var - z/y

		ExpressionNode temp = new ExpressionNode(kernel, xCoord,
				Operation.DIVIDE, yCoord);

		temp = new ExpressionNode(kernel, new MyDouble(kernel, -1.0),
				Operation.MULTIPLY, temp);

		temp = new ExpressionNode(kernel, temp, Operation.MULTIPLY, fv);

		temp = new ExpressionNode(kernel, temp, Operation.MINUS,
				new ExpressionNode(kernel, zCoord, Operation.DIVIDE, yCoord));

		// f(x_var) = -x/y x_var - z/y
		/*
		 * ExpressionNode temp = new ExpressionNode(kernel, new MyDouble(kernel,
		 * -x / y), Operation.MULTIPLY, fv);
		 * 
		 * temp = new ExpressionNode(kernel, temp, Operation.PLUS, new
		 * MyDouble(kernel, -z / y) );
		 */

		Function fun = new Function(temp, fv);

		// we get a dependent function if this line has a label or is dependent

		if (isLabelSet() || !isIndependent()) {
			ret = new AlgoDependentFunction(cons, fun, false).getFunction();

		} else {
			ret = new GeoFunction(cons);
			ret.setFunction(fun);
		}

		return ret;
	}

	@Override
	public boolean isGeoFunctionable() {
		return true;
	}

	@Override
	public boolean isMatrixTransformable() {
		return true;
	}

	/**
	 * @param con conic to store result
	 */
	public void toGeoConic(GeoConic con) {
		con.fromLine(this);
	}

	public double evaluate(double x_var) {
		if (Kernel.isZero(y))
			return Double.NaN;
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

	public Coords getPointInD(int dimension, double lambda) {
		return getStartCoordsInD(dimension).copy().addInsideMul(
				getDirectionInD(dimension), lambda);
	}

	/**
	 * returns inhom coords in dimension
	 * 
	 * @param dimension
	 * @return
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
	
	public Coords getDirectionForEquation(){
		return getDirectionInD(3);
	}
	

	public Coords getCartesianEquationVector(CoordMatrix m) {
		if (m == null) {
			return new Coords(x, y, z);
		}
		Coords o = getStartInhomCoords();
		Coords d = getEndInhomCoords().sub(o);
		return CoordMatrixUtil.lineEquationVector(o, d, m);
	}

	public Coords getStartInhomCoords() {
		if (startPoint != null && startPoint.isFinite()) {
			return startPoint.getInhomCoordsInD3();
		}
		return getStartCoordsInD(3);
	}

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

	public Coords getDirectionInD3() {
		if (getEndPoint()==null){
			return new Coords(-y, x, 0, 0);
		}
		
		return getEndInhomCoords().sub(getStartInhomCoords());
	}

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
	
	/** list of points on this line*/
	protected ArrayList<GeoPoint> pointsOnLine;

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
	 * @param p point tobe added
	 */
	public final void addPointOnLine(GeoPointND p) {
		if (pointsOnLine == null){
			pointsOnLine = new ArrayList<GeoPoint>();
		}

		if (!pointsOnLine.contains(p))
			pointsOnLine.add((GeoPoint) p);
	}

	/**
	 * Removes a point from the list of points that this line passes through.
	 * 
	 * @param p
	 *            Point to be removed
	 */
	public final void removePointOnLine(GeoPointND p) {
		if (pointsOnLine != null)
			pointsOnLine.remove(p);
	}

	@Override
	public void doRemove() {
		if(getStartPoint()!=null)
			getStartPoint().removeIncidence(this);
		if(getEndPoint()!=null)
			getEndPoint().removeIncidence(this);
		if (pointsOnLine != null) {
			for (int i = 0; i < pointsOnLine.size(); ++i) {
				GeoPoint p = pointsOnLine.get(i);
				p.removeIncidence(this);
			}
		}

		super.doRemove();
	}


	public Function getFunction() {
		return getGeoFunction().getFunction();
	}

	public GeoFunction getGeoDerivative(int order, boolean fast) {
		return getGeoFunction().getGeoDerivative(order, fast);
	}

	public SymbolicParameters getSymbolicParameters() {
		if (algoParent != null
				&& (algoParent instanceof SymbolicParametersAlgo)) {
			return new SymbolicParameters((SymbolicParametersAlgo) algoParent);
		}
		return null;
	}

	public void getFreeVariables(HashSet<Variable> variables)
			throws NoSymbolicParametersException {
		if (algoParent != null
				&& (algoParent instanceof SymbolicParametersAlgo)) {
			((SymbolicParametersAlgo) algoParent).getFreeVariables(variables);
			return;
		}
		throw new NoSymbolicParametersException();
	}
	
	public int[] getDegrees() throws NoSymbolicParametersException {
		if (algoParent != null
				&& (algoParent instanceof SymbolicParametersAlgo)) {
			return ((SymbolicParametersAlgo) algoParent).getDegrees();
		}
		throw new NoSymbolicParametersException();
	}

	public BigInteger[] getExactCoordinates(final HashMap<Variable,BigInteger> values) throws NoSymbolicParametersException {
		if (algoParent != null
	&& (algoParent instanceof SymbolicParametersAlgo)) {
			return ((SymbolicParametersAlgo) algoParent).getExactCoordinates(values);
		}
		return null;
	}

	public Polynomial[] getPolynomials() throws NoSymbolicParametersException {
		if (algoParent != null && algoParent instanceof SymbolicParametersAlgo) {
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
		if (this instanceof GeoAxis) {
			return true;
		}
		if (this.getParentAlgorithm() == null) {
			return true;
		}
		return false;
	}

	public Variable[] getBotanaVars(GeoElementND geo) {
		if (algoParent instanceof SymbolicParametersBotanaAlgo) {
			return ((SymbolicParametersBotanaAlgo) algoParent).getBotanaVars(this);
		}
		if (hasFixedSlope()) {
			if (botanaVars == null) {
				botanaVars = new Variable[4];
				botanaVars[0] = new Variable(true);
				botanaVars[1] = new Variable(true);
				botanaVars[2] = new Variable(true);
				botanaVars[3] = new Variable(true);
				// we substitute them in AlgoLocusEquation, not here
			}
			return botanaVars;
		}
		return null;
	}
	
	public Polynomial[] getBotanaPolynomials(GeoElementND geo)
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

	public double distance(GeoLineND g) {
		return distance((GeoLine)g);
	}
	
	/**
	 * Whether the parameter is within acceptable range
	 * @param param path parameter
	 */
	public boolean respectLimitedPath(double param){
		return true;
	}

	/**
	 * normalize coeffients so that Intersect[ (x - 1.62010081566832)^2 + (y +
	 * 31.674457260881873)^2 = 0.028900000000021 , 0.000158120368003x +
	 * 0.000144840828995y = -0.004331583710062 ] works
	 * 
	 * @param ret
	 *            output array
	 * 
	 * @return normalized coefficients x,y,z
	 */
	public double[] getnormalizedCoefficients(double[] ret) {
		
		ret[0] = x;
		ret[1] = y;
		ret[2] = z;
		
		if (Kernel.isZero(x) && Kernel.isZero(y) && Kernel.isZero(z)) {
			return ret;
		}
		
		if (Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z)) {
			return ret;
		}
		
		if (Double.isInfinite(x) || Double.isInfinite(y) || Double.isInfinite(z)) {
			return ret;
		}
		
		while (Math.abs(ret[0]) < 0.5 && Math.abs(ret[1]) < 0.5 && Math.abs(ret[2]) < 0.5) {
			ret[0] *= 2;
			ret[1] *= 2;
			ret[2] *= 2;
		}
		
		while (Math.abs(ret[0]) > 1 && Math.abs(ret[1]) > 1 && Math.abs(ret[2]) > 1) {
			ret[0] /= 2;
			ret[1] /= 2;
			ret[2] /= 2;
		}
		
		return ret;
	}
	
	@Override
	public EquationElementInterface buildEquationElement(EquationScope scope) {
		return LocusEquation.eqnLine(this, scope);
	}

	@Override
	public boolean hasLineOpacity() {
		return true;
	}

	
	@Override
	final public HitType getLastHitType(){
		return HitType.ON_BOUNDARY;
	}

	@Override
	public boolean isParametric() {
		return getMode() == GeoLine.PARAMETRIC;
	}

	public ValueType getValueType() {
		return getMode() == GeoLine.PARAMETRIC ? ValueType.PARAMETRIC2D
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

	public Coords getOrigin() {
		return getStartPoint().getCoordsInD3();
	}

	public Equation getEquation() {
		FunctionVariable fvx = new FunctionVariable(kernel, "x");
		FunctionVariable fvy = new FunctionVariable(kernel, "y");

		ExpressionNode lhs = new ExpressionNode(kernel, this.x).multiply(fvx);

		lhs = lhs.plus(new ExpressionNode(kernel, this.y)
				.multiply(fvy));

		return new Equation(kernel, lhs,
				new ExpressionNode(kernel, this.z)
						.multiply(-1));
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

	public void setToGeneral() {
		this.toStringMode = GeoLine.EQUATION_GENERAL;
	}
}