package org.geogebra.common.geogebra3D.kernel3D.geos;

import java.util.ArrayList;

import javax.annotation.CheckForNull;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoLinePoint;
import org.geogebra.common.geogebra3D.kernel3D.transform.MirrorableAtPlane;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MatrixTransformable;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.Dilateable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.geos.Transformable;
import org.geogebra.common.kernel.geos.Translateable;
import org.geogebra.common.kernel.kernelND.GeoCoordSys;
import org.geogebra.common.kernel.kernelND.GeoCoordSys1DInterface;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.kernelND.RotatableND;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.CoordMatrixUtil;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * 1D linear object in space (segment, line, ...)
 *
 */
public abstract class GeoCoordSys1D extends GeoElement3D
		implements Path, GeoLineND, GeoCoordSys, GeoCoordSys1DInterface,
		Translateable, MatrixTransformable, Traceable, RotatableND,
		MirrorableAtPlane, Transformable, Dilateable {
	/** coord system */
	protected CoordSys coordsys;
	/** start point */
	protected GeoPointND startPoint;
	/** end point */
	protected GeoPointND endPoint;

	private boolean isIntersection;
	/** list of points on this line */
	protected ArrayList<GeoPointND> pointsOnLine;

	private CoordMatrix4x4 tmpMatrix4x4;

	private Coords tmpCoords1;
	private Coords tmpCoords2;
	private boolean trace;

	/**
	 * @param c
	 *            construction
	 */
	public GeoCoordSys1D(Construction c) {
		this(c, false);
	}

	/**
	 * @param c
	 *            construction
	 * @param isIntersection
	 *            whetherthis is intersection line
	 */
	public GeoCoordSys1D(Construction c, boolean isIntersection) {
		super(c);

		this.isIntersection = isIntersection;

		// moved from GeoElement's constructor
		// must be called from the subclass, see
		// http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

		coordsys = new CoordSys(1);
	}

	/**
	 * @param c
	 *            construction
	 * @param O
	 *            start point
	 * @param V
	 *            direction
	 */
	public GeoCoordSys1D(Construction c, Coords O, Coords V) {
		this(c);
		setCoord(O, V);
	}

	/**
	 * @param c
	 *            construction
	 * @param O
	 *            start point
	 * @param I
	 *            end point
	 */
	public GeoCoordSys1D(Construction c, GeoPointND O, GeoPointND I) {
		this(c, O, I, false);
	}

	/**
	 * @param c
	 *            construction
	 * @param O
	 *            start point
	 * @param I
	 *            end point
	 * @param isIntersection
	 *            true for intersection lines
	 */
	public GeoCoordSys1D(Construction c, GeoPointND O, GeoPointND I,
			boolean isIntersection) {
		this(c, isIntersection);
		setCoord(O, I);
	}

	@Override
	public boolean isDefined() {
		return coordsys.isDefined();
	}

	@Override
	public void setUndefined() {
		coordsys.setUndefined();
	}

	/**
	 * set the matrix to [(I-O) O]
	 * 
	 * @param a_O
	 *            start point
	 * @param a_I
	 *            end point
	 */
	public void setCoordFromPoints(Coords a_O, Coords a_I) {
		setCoord(a_O, a_I.sub(a_O));
	}

	/**
	 * set the matrix to [V O]
	 * 
	 * @param o
	 *            start point
	 * @param v
	 *            direction
	 */
	public void setCoord(Coords o, Coords v) {
		coordsys.resetCoordSys();
		coordsys.addPoint(o);
		coordsys.addVector(v);
		coordsys.makeOrthoMatrix(false, false);
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
		setCoord(new Coords(pointX, pointY, 0, 1), getDirectionInD3());
	}

	/**
	 * set coords to origin O and vector (I-O). If I (or O) is infinite, I is
	 * used as direction vector.
	 * 
	 * @param O
	 *            origin point
	 * @param I
	 *            unit point
	 * @return true if one point is null or infinite
	 */
	public boolean setCoord(GeoPointND O, GeoPointND I) {
		startPoint = O;
		endPoint = I;

		if ((O == null) || (I == null)) {
			return true;
		}

		if (I.isInfinite()) {
			if (O.isInfinite()) {
				setUndefined(); // TODO infinite line
			} else {
				setCoord(O.getInhomCoordsInD3(), I.getCoordsInD3());
			}
			return true;
		} else if (O.isInfinite()) {
			setCoord(I.getInhomCoordsInD3(), O.getCoordsInD3());
			return true;
		} else {
			setCoord(O.getInhomCoordsInD3(),
					I.getInhomCoordsInD3().sub(O.getInhomCoordsInD3()));
			return false;
		}

	}

	/**
	 * @param geo
	 *            other system
	 */
	public void setCoord(GeoCoordSys1D geo) {
		setCoord(geo.getCoordSys().getOrigin(), geo.getCoordSys().getVx());
	}

	@Override
	public void set(GeoElementND geo) {
		if (geo instanceof GeoCoordSys1D) {
			if (!geo.isDefined()) {
				setUndefined();
			} else {
				setCoord((GeoCoordSys1D) geo);
			}
		} else if (geo instanceof GeoLineND) {
			if (!geo.isDefined()) {
				setUndefined();
			} else {
				setCoord(((GeoLineND) geo).getStartPoint(),
						((GeoLineND) geo).getEndPoint());
			}
		}

	}

	/**
	 * @param cons1
	 *            construction for the copy
	 * @return a new instance of the proper GeoCoordSys1D (GeoLine3D,
	 *         GeoSegment3D, ...)
	 */
	abstract protected GeoCoordSys1D create(Construction cons1);

	@Override
	final public GeoCoordSys1D copy() {
		GeoCoordSys1D geo = create(cons);
		geo.set(this);
		geo.setCoord(this);
		return geo;
	}

	/**
	 * returns the point at position lambda on the coord sys
	 * 
	 * @param lambda
	 *            path parameter (0 for stat point)
	 * @return the point at position lambda on the coord sys
	 */
	public Coords getPoint(double lambda) {
		return coordsys.getPoint(lambda);

	}

	/**
	 * returns the point at position lambda on the coord sys in the dimension
	 * given
	 * 
	 * @param dimension
	 *            dimension
	 * @param lambda
	 *            path parameter
	 * @return the point at position lambda on the coord sys
	 */
	@Override
	public Coords getPointInD(int dimension, double lambda) {
		Coords v = getPoint(lambda);
		switch (dimension) {
		case 3:
			return v;
		case 2:
			return new Coords(v.getX(), v.getY(), v.getW());
		default:
			return null;
		}
	}

	/** @return cs unit */
	public double getUnit() {
		return getCoordSys().getVx().norm();
	}

	@Override
	public Coords getMainDirection() {
		return getCoordSys().getMatrixOrthonormal().getVx();
	}

	@Override
	public Coords getDirectionForEquation() {
		return getCoordSys().getVx();
	}

	// Path3D interface
	@Override
	public boolean isPath() {
		return true;
	}

	@Override
	public void pointChanged(GeoPointND P) {
		double t = getParamOnLine(P);

		if (t < getMinParameter()) {
			t = getMinParameter();
		} else if (t > getMaxParameter()) {
			t = getMaxParameter();
		}

		// set path parameter
		PathParameter pp = P.getPathParameter();

		pp.setT(t);

		// update point using pathChanged
		P.setCoords(getPoint(t), false);

	}

	/**
	 * @param P
	 *            point in space
	 * @return path parameter of P's projection on line
	 */
	public double getParamOnLine(GeoPointND P) {
		boolean done = false;
		double t = 0;
		if (P.isGeoElement3D()) {
			if (((GeoPoint3D) P).hasWillingCoords()) {
				if (((GeoPoint3D) P).hasWillingDirection()) {
					// project willing location using willing direction
					// GgbVector[] project =
					// coordsys.getProjection(P.getWillingCoords(),
					// P.getWillingDirection());

					if (tmpCoords1 == null) {
						tmpCoords1 = Coords.createInhomCoorsInD3();
					}
					t = ((GeoPoint3D) P).getWillingCoords()
							.projectedParameterOnLineWithDirection(
									coordsys.getOrigin(), coordsys.getVx(),
									((GeoPoint3D) P).getWillingDirection(),
									tmpCoords1);

					done = true;
				} else {
					// project current point coordinates
					Coords preDirection = ((GeoPoint3D) P).getWillingCoords()
							.sub(coordsys.getOrigin())
							.crossProduct(coordsys.getVx());
					if (preDirection.equalsForKernel(0,
							Kernel.STANDARD_PRECISION)) {
						preDirection = coordsys.getVy();
					}

					if (tmpCoords1 == null) {
						tmpCoords1 = Coords.createInhomCoorsInD3();
					}
					t = ((GeoPoint3D) P).getWillingCoords()
							.projectedParameterOnLineWithDirection(
									coordsys.getOrigin(),
									coordsys.getVx(), preDirection
											.crossProduct4(coordsys.getVx()),
									tmpCoords1);

					done = true;
				}
			}
		}

		if (!done) {
			// project current point coordinates
			Coords preDirection = P.getInhomCoordsInD3()
					.sub(coordsys.getOrigin()).crossProduct(coordsys.getVx());
			if (preDirection.equalsForKernel(0, Kernel.STANDARD_PRECISION)) {
				preDirection = coordsys.getVy();
			}

			if (tmpCoords1 == null) {
				tmpCoords1 = Coords.createInhomCoorsInD3();
			}
			t = P.getInhomCoordsInD3().projectedParameterOnLineWithDirection(
					coordsys.getOrigin(), coordsys.getVx(),
					preDirection.crossProduct4(coordsys.getVx()), tmpCoords1);

		}
		return t;
	}

	@Override
	public void pathChanged(GeoPointND P) {
		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		if (!getKernel().usePathAndRegionParameters(P)) {
			pointChanged(P);
			return;
		}

		PathParameter pp = P.getPathParameter();
		P.setCoords(getPoint(pp.getT()), false);

	}

	@Override
	public boolean isOnPath(GeoPointND PI, double eps) {
		if (PI.getPath() == this) {
			return true;
		}

		return isOnPath(PI.getCoordsInD3(), eps);
	}

	@Override
	public boolean isOnPath(Coords coords, double eps) {
		return isOnFullLine(coords, eps);
	}

	@Override
	public boolean isOnFullLine(Coords p, double eps) {
		Coords cross;

		if (DoubleUtil.isZero(p.getW())) { // infinite point : check direction
			cross = p.crossProduct(getDirectionInD3());
			return cross.equalsForKernel(0, Kernel.MIN_PRECISION);
		}

		// standard case
		Coords d = getDirectionInD3().normalized();
		Coords v = p.sub(getStartInhomCoords());
		Coords n = v.sub(d.mul(v.dotproduct(d)));
		return n.dotproduct(n) < eps * eps;

	}

	@Override
	public boolean respectLimitedPath(Coords coords, double eps) {
		return true;
	}

	/**
	 * return true if x is a valid coordinate (eg 0&lt;=x&lt;=1 for a segment)
	 * 
	 * @param x
	 *            coordinate
	 * @return true if x is a valid coordinate (eg 0&lt;=x&lt;=1 for a segment)
	 */
	abstract public boolean isValidCoord(double x);

	// //////////////////////////////////
	// XML
	// //////////////////////////////////

	/**
	 * returns all class-specific xml tags for saveXML
	 */
	@Override
	protected void getStyleXML(StringBuilder sb) {
		super.getStyleXML(sb);
		// line thickness and type
		getLineStyleXML(sb);
	}

	@Override
	public CoordSys getCoordSys() {
		return coordsys;
	}

	@Override
	public Coords getLabelPosition() {
		return coordsys.getPoint(0.5);
	}

	@Override
	public Coords getCartesianEquationVector(CoordMatrix m) {
		if (m == null) {
			return CoordMatrixUtil.lineEquationVector(getCoordSys().getOrigin(),
					getCoordSys().getVx());
		}

		return CoordMatrixUtil.lineEquationVector(getCoordSys().getOrigin(),
				getCoordSys().getVx(), m);
	}

	@Override
	public Coords getStartInhomCoords() {
		return getCoordSys().getOrigin().getInhomCoordsInSameDimension();
	}

	/**
	 * @return inhom coords of the end point
	 */
	@Override
	public Coords getEndInhomCoords() {
		return getCoordSys().getPoint(1).getInhomCoordsInSameDimension();
	}

	@Override
	public Coords getDirectionInD3() {
		return getCoordSys().getVx();
	}

	/**
	 * 
	 * @return start point
	 */
	@Override
	public GeoPointND getStartPoint() {
		return startPoint;
	}

	/**
	 * 
	 * @return "end" point
	 */
	@Override
	public GeoPointND getEndPoint() {
		return endPoint;
	}

	/**
	 * 
	 * @return true if is an intersection curve
	 */
	public boolean isIntersection() {
		return isIntersection;
	}

	@Override
	final public boolean isTranslateable() {
		return true;
	}

	@Override
	final public void translate(Coords v) {
		Coords o = getCoordSys().getOrigin();
		o.addInside(v);
		setCoord(o, getCoordSys().getVx());

	}

	// ///////////////////////////////////
	// POINTS ON COORD SYS
	// ///////////////////////////////////

	/**
	 * Returns a list of points that this line passes through. May return null.
	 * 
	 * @return list of points that this line passes through.
	 */
	public final ArrayList<GeoPointND> getPointsOnLine() {
		return pointsOnLine;
	}

	/**
	 * Sets a list of points that this line passes through. This method should
	 * only be used by AlgoMacro.
	 * 
	 * @param points
	 *            list of points that this line passes through
	 */
	public final void setPointsOnLine(ArrayList<GeoPointND> points) {
		pointsOnLine = points;
	}

	@Override
	public final void addPointOnLine(GeoPointND p) {
		if (pointsOnLine == null) {
			pointsOnLine = new ArrayList<>();
		}

		if (!pointsOnLine.contains(p)) {
			pointsOnLine.add(p);
		}
	}

	/**
	 * Calculates the distance between this line and line g.
	 * 
	 * @param g
	 *            line
	 * @return distance between lines
	 */
	@Override
	final public double distance(GeoLineND g) {
		double dist;
		Coords cVector = this.getDirectionInD3()
				.crossProduct(g.getDirectionInD3());
		Coords diffPoints = this.getPointInD(3, 0)
				.getInhomCoordsInSameDimension()
				.sub(g.getPointInD(3, 0).getInhomCoordsInSameDimension());

		if (cVector.isZero()) { // two lines are parallel
			Coords n = diffPoints.crossProduct(this.getDirectionInD3())
					.crossProduct(this.getDirectionInD3());
			dist = Math.abs(diffPoints.dotproduct(n.normalize()));
		} else {
			dist = Math.abs(diffPoints.dotproduct(cVector.normalize()));
		}

		return dist;
	}

	// ///////////////////////////
	// MATRIX TRANSFORMABLE
	// ///////////////////////////

	@Override
	public boolean isMatrixTransformable() {
		return true;
	}

	@Override
	public void matrixTransform(double a00, double a01, double a10,
			double a11) {

		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = CoordMatrix4x4.identity();
		} else {
			tmpMatrix4x4.set(1, 3, 0);
			tmpMatrix4x4.set(1, 4, 0);

			tmpMatrix4x4.set(2, 3, 0);
			tmpMatrix4x4.set(2, 4, 0);

			tmpMatrix4x4.set(3, 1, 0);
			tmpMatrix4x4.set(3, 2, 0);
			tmpMatrix4x4.set(3, 3, 0);
			tmpMatrix4x4.set(3, 4, 0);

			tmpMatrix4x4.set(4, 1, 0);
			tmpMatrix4x4.set(4, 2, 0);
			tmpMatrix4x4.set(4, 3, 0);
			tmpMatrix4x4.set(4, 4, 1);
		}

		tmpMatrix4x4.set(1, 1, a00);
		tmpMatrix4x4.set(1, 2, a01);
		tmpMatrix4x4.set(2, 1, a10);
		tmpMatrix4x4.set(2, 2, a11);

		setCoord(tmpMatrix4x4.mul(getCoordSys().getOrigin()),
				tmpMatrix4x4.mul(getCoordSys().getVx()));
	}

	@Override
	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {

		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = CoordMatrix4x4.identity();
		} else {
			tmpMatrix4x4.set(1, 4, 0);
			tmpMatrix4x4.set(2, 4, 0);
			tmpMatrix4x4.set(3, 4, 0);

			tmpMatrix4x4.set(4, 1, 0);
			tmpMatrix4x4.set(4, 2, 0);
			tmpMatrix4x4.set(4, 3, 0);
			tmpMatrix4x4.set(4, 4, 1);
		}

		tmpMatrix4x4.set(1, 1, a00);
		tmpMatrix4x4.set(1, 2, a01);
		tmpMatrix4x4.set(1, 3, a02);

		tmpMatrix4x4.set(2, 1, a10);
		tmpMatrix4x4.set(2, 2, a11);
		tmpMatrix4x4.set(2, 3, a12);

		tmpMatrix4x4.set(3, 1, a20);
		tmpMatrix4x4.set(3, 2, a21);
		tmpMatrix4x4.set(3, 3, a22);

		setCoord(tmpMatrix4x4.mul(getCoordSys().getOrigin()),
				tmpMatrix4x4.mul(getCoordSys().getVx()));

	}

	// ////////////////
	// TRACE
	// ////////////////

	@Override
	public boolean isTraceable() {
		return true;
	}

	@Override
	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	@Override
	public boolean getTrace() {
		return trace;
	}

	// //////////////////
	// ROTATE
	// //////////////////

	@Override
	public void rotate(NumberValue phiValue) {
		Coords o = getCoordSys().getOrigin();

		double z = o.getZ();
		/*
		 * if (!Kernel.isZero(z)){ setUndefined(); return; }
		 */

		Coords v = getCoordSys().getVx();

		double vz = v.getZ();
		/*
		 * if (!Kernel.isZero(vz)){ setUndefined(); return; }
		 */

		double phi = phiValue.getDouble();
		double cos = Math.cos(phi);
		double sin = Math.sin(phi);

		double x = o.getX();
		double y = o.getY();
		double w = o.getW();

		Coords oRot = new Coords(x * cos - y * sin, x * sin + y * cos, z, w);

		double vx = v.getX();
		double vy = v.getY();
		double vw = v.getW();

		Coords vRot = new Coords(vx * cos - vy * sin, vx * sin + vy * cos, vz,
				vw);

		setCoord(oRot, vRot);

	}

	@Override
	final public void rotate(NumberValue phiValue, GeoPointND point) {
		Coords o = getCoordSys().getOrigin();

		double z = o.getZ();
		/*
		 * if (!Kernel.isZero(z)){ setUndefined(); return; }
		 */

		Coords v = getCoordSys().getVx();

		double vz = v.getZ();
		/*
		 * if (!Kernel.isZero(vz)){ setUndefined(); return; }
		 */

		double phi = phiValue.getDouble();
		double cos = Math.cos(phi);
		double sin = Math.sin(phi);

		double x = o.getX();
		double y = o.getY();
		double w = o.getW();

		Coords Q = point.getInhomCoords();
		double qx = w * Q.getX();
		double qy = w * Q.getY();

		Coords oRot = new Coords((x - qx) * cos + (qy - y) * sin + qx,
				(x - qx) * sin + (y - qy) * cos + qy, z, w);

		double vx = v.getX();
		double vy = v.getY();
		double vw = v.getW();

		Coords vRot = new Coords(vx * cos - vy * sin, vx * sin + vy * cos, vz,
				vw);

		setCoord(oRot, vRot);

	}

	private void rotate(NumberValue phiValue, Coords o1, Coords vn) {

		if (vn.isZero()) {
			setUndefined();
			return;
		}
		Coords vn2 = vn.normalized();

		Coords point = getCoordSys().getOrigin();
		if (tmpCoords1 == null) {
			tmpCoords1 = Coords.createInhomCoorsInD3();
		}
		point.projectLine(o1, vn, tmpCoords1, null); // point projected on the
														// axis

		Coords v1 = point.sub(tmpCoords1); // axis->point of the line

		Coords v = getCoordSys().getVx(); // direction of the line

		double phi = phiValue.getDouble();
		double cos = Math.cos(phi);
		double sin = Math.sin(phi);

		// new line origin
		Coords v2 = vn2.crossProduct4(v1);
		Coords oRot = tmpCoords1.addInsideMul(v1, cos).addInsideMul(v2, sin);

		// new line direction
		v2 = vn2.crossProduct4(v);
		v1 = v2.crossProduct4(vn2);
		Coords vRot = v1.mul(cos).addInsideMul(v2, sin).addInsideMul(vn2,
				v.dotproduct(vn2));

		setCoord(oRot, vRot);
	}

	@Override
	public void rotate(NumberValue phiValue, Coords S,
			GeoDirectionND orientation) {

		Coords vn = orientation.getDirectionInD3();

		rotate(phiValue, S, vn);
	}

	// //////////////////////
	// MIRROR
	// //////////////////////

	@Override
	public void mirror(Coords Q) {
		Coords o = getCoordSys().getOrigin().mul(-1);

		o.addInside(Q.mul(2));

		setCoord(o, getCoordSys().getVx().mul(-1));

	}

	@Override
	public void mirror(GeoLineND line) {
		Coords o1 = line.getStartInhomCoords();
		Coords vn = line.getDirectionInD3();

		Coords point = getCoordSys().getOrigin();
		if (tmpCoords1 == null) {
			tmpCoords1 = Coords.createInhomCoorsInD3();
		}
		point.projectLine(o1, vn, tmpCoords1, null); // point projected on the
														// line
		point.mulInside(-1);
		point.addInsideMul(tmpCoords1, 2);

		double l = vn.getNorm();
		Coords v = getCoordSys().getVx();
		setCoord(point, vn.copy().mulInside(2 * v.dotproduct(vn) / (l * l))
				.addInsideMul(v, -1));

	}

	@Override
	public void mirror(GeoCoordSys2D plane) {
		Coords point = getCoordSys().getOrigin();
		// point projected on the plane
		if (tmpCoords1 == null) {
			tmpCoords1 = Coords.createInhomCoorsInD3();
		}
		point.projectPlane(plane.getCoordSys().getMatrixOrthonormal(),
				tmpCoords1);
		point.mulInside(-1);
		point.addInside(tmpCoords1.mulInside(2));

		Coords vn = plane.getDirectionInD3().normalized();
		Coords v = getCoordSys().getVx();
		if (tmpCoords2 == null) {
			tmpCoords2 = new Coords(4);
		}
		setCoord(point, tmpCoords1.setAdd(v,
				tmpCoords2.setMul(vn, -2 * v.dotproduct(vn))));

	}

	// //////////////////////
	// DILATE
	// //////////////////////

	@Override
	public void dilate(NumberValue rval, Coords S) {
		double r = rval.getDouble();

		Coords o = getCoordSys().getOrigin().mul(r);

		o.addInside(S.mul(1 - r));

		setCoord(o, getCoordSys().getVx().mul(r));
	}

	@Override
	public ExpressionValue evaluateCurve(double t) {
		Coords O = coordsys.getOrigin(); // TODO inhom coords, also copied from
										// toString
		Coords V = coordsys.getVx();
		if (getParentAlgorithm() instanceof AlgoLinePoint) {
			AlgoLinePoint algoLP = (AlgoLinePoint) getParentAlgorithm();

			GeoElement[] geos = algoLP.getInput();

			if (geos[0].isGeoPoint() && geos[1].isGeoVector()) {

				// use original coordinates for displaying, not normalized form
				// for Line[ A, u ]

				GeoPointND pt = (GeoPointND) geos[0];
				O = pt.getInhomCoordsInD3();
				GeoVectorND vec = (GeoVectorND) geos[1];

				V = vec.getCoordsInD3();
			}
		}
		return new Geo3DVec(kernel, O.get(1) + t * V.get(1),
				O.get(2) + t * V.get(2), O.get(3) + t * V.get(3));
	}

	@Override // EquationLinear
	@CheckForNull
	public Form getEquationForm() {
		return Form.valueOf(toStringMode);
	}

	@Override // EquationLinear
	public void setEquationForm(int toStringMode) {
		Form equationForm = Form.valueOf(toStringMode);
		if (equationForm != null) {
			this.toStringMode = toStringMode;
		}
	}

	@Override // EquationLinear
	public void setToGeneral() {
		// no general line type in 3D
	}
}
