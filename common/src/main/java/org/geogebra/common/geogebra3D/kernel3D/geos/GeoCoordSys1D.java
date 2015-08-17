package org.geogebra.common.geogebra3D.kernel3D.geos;

import java.util.ArrayList;

import org.geogebra.common.geogebra3D.kernel3D.transform.MirrorableAtPlane;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MatrixTransformable;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.Matrix.CoordMatrix;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.CoordMatrixUtil;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
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
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.RotateableND;

public abstract class GeoCoordSys1D extends GeoElement3D implements Path,
		GeoLineND, GeoCoordSys, GeoCoordSys1DInterface, Translateable,
		MatrixTransformable, Traceable, RotateableND, MirrorableAtPlane,
		Transformable, Dilateable {

	protected CoordSys coordsys;

	protected GeoPointND startPoint;

	protected GeoPointND endPoint;

	private boolean isIntersection;

	public GeoCoordSys1D(Construction c) {
		this(c, false);
	}

	public GeoCoordSys1D(Construction c, boolean isIntersection) {
		super(c);

		this.isIntersection = isIntersection;

		// moved from GeoElement's constructor
		// must be called from the subclass, see
		// http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

		coordsys = new CoordSys(1);
	}

	public GeoCoordSys1D(Construction c, Coords O, Coords V) {
		this(c);
		setCoord(O, V);
	}

	public GeoCoordSys1D(Construction c, GeoPointND O, GeoPointND I) {
		this(c, O, I, false);
	}

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

	/** set the matrix to [(I-O) O] */
	public void setCoordFromPoints(Coords a_O, Coords a_I) {
		setCoord(a_O, a_I.sub(a_O));
	}

	/** set the matrix to [V O] */
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

		if ((O == null) || (I == null))
			return true;

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

	public void setCoord(GeoCoordSys1D geo) {
		setCoord(geo.getCoordSys().getOrigin(), geo.getCoordSys().getVx());
	}

	@Override
	public void set(GeoElement geo) {
		if (geo instanceof GeoCoordSys1D) {
			if (!geo.isDefined())
				setUndefined();
			else
				setCoord((GeoCoordSys1D) geo);
		} else if (geo instanceof GeoLineND) {
			if (!geo.isDefined())
				setUndefined();
			else {
				setCoord(((GeoLineND) geo).getStartPoint(),
						((GeoLineND) geo).getEndPoint());
			}
		}

	}

	/**
	 * @param cons
	 * @return a new instance of the proper GeoCoordSys1D (GeoLine3D,
	 *         GeoSegment3D, ...)
	 */
	abstract protected GeoCoordSys1D create(Construction cons);

	@Override
	final public GeoCoordSys1D copy() {
		GeoCoordSys1D geo = create(cons);
		geo.setCoord(this);
		return geo;
	}

	/**
	 * returns matrix corresponding to segment joining l1 to l2, using
	 * getLineThickness()
	 */
	/*
	 * public GgbMatrix getSegmentMatrix(double l1, double l2){
	 * 
	 * 
	 * 
	 * return GgbMatrix4x4.subSegmentX(getMatrix4x4(), l1, l2); }
	 */

	/**
	 * returns the point at position lambda on the coord sys
	 * 
	 * @param lambda
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
	 * @param lambda
	 * @return the point at position lambda on the coord sys
	 * */
	public Coords getPointInD(int dimension, double lambda) {

		Coords v = getPoint(lambda);
		// Application.debug("v("+lambda+")=\n"+v+"\no=\n"+coordsys.getOrigin()+"\nVx=\n"+coordsys.getVx()+"\ncoordsys=\n"+coordsys.getMatrixOrthonormal());
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

	public Coords getDirectionForEquation() {
		return getCoordSys().getVx();
	}

	// Path3D interface
	@Override
	public boolean isPath() {
		return true;
	}

	public void pointChanged(GeoPointND P) {

		double t = getParamOnLine(P);

		if (t < getMinParameter())
			t = getMinParameter();
		else if (t > getMaxParameter())
			t = getMaxParameter();

		// set path parameter
		PathParameter pp = P.getPathParameter();

		pp.setT(t);

		// udpate point using pathChanged
		P.setCoords(getPoint(t), false);

	}

	// get the param of P's projection on line
	public double getParamOnLine(GeoPointND P) {

		boolean done = false;
		double t = 0;
		if (((GeoElement) P).isGeoElement3D()) {
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
					// Application.debug("ici\n getWillingCoords=\n"+P.getWillingCoords()+"\n matrix=\n"+getMatrix().toString());
					Coords preDirection = ((GeoPoint3D) P).getWillingCoords()
							.sub(coordsys.getOrigin())
							.crossProduct(coordsys.getVx());
					if (preDirection.equalsForKernel(0,
							Kernel.STANDARD_PRECISION))
						preDirection = coordsys.getVy();

					if (tmpCoords1 == null) {
						tmpCoords1 = Coords.createInhomCoorsInD3();
					}
					t = ((GeoPoint3D) P)
							.getWillingCoords()
							.projectedParameterOnLineWithDirection(
									coordsys.getOrigin(),
									coordsys.getVx(),
									preDirection.crossProduct4(coordsys.getVx()),
									tmpCoords1);

					done = true;
				}
			}
		}

		if (!done) {
			// project current point coordinates
			// Application.debug("project current point coordinates");
			Coords preDirection = P.getInhomCoordsInD3()
					.sub(coordsys.getOrigin()).crossProduct(coordsys.getVx());
			if (preDirection.equalsForKernel(0, Kernel.STANDARD_PRECISION))
				preDirection = coordsys.getVy();

			if (tmpCoords1 == null) {
				tmpCoords1 = Coords.createInhomCoorsInD3();
			}
			t = P.getInhomCoordsInD3().projectedParameterOnLineWithDirection(
					coordsys.getOrigin(), coordsys.getVx(),
					preDirection.crossProduct4(coordsys.getVx()), tmpCoords1);

		}
		return t;
	}

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

	public boolean isOnPath(GeoPointND PI, double eps) {
		if (PI.getPath() == this)
			return true;

		return isOnPath(PI.getCoordsInD3(), eps);
	}

	public boolean isOnPath(Coords coords, double eps) {
		return isOnFullLine(coords, eps);
	}

	public boolean isOnFullLine(Coords p, double eps) {
		Coords cross;

		if (Kernel.isZero(p.getW())) {// infinite point : check direction
			cross = p.crossProduct(getDirectionInD3());
			return cross.equalsForKernel(0, Kernel.MIN_PRECISION);
		}

		// standard case
		Coords d = getDirectionInD3().normalized();
		Coords v = p.sub(getStartInhomCoords());
		Coords n = v.sub(d.mul(v.dotproduct(d)));
		return n.dotproduct(n) < eps * eps;

	}

	public boolean respectLimitedPath(Coords coords, double eps) {
		return true;
	}

	// //////////////////////////////////
	//

	/**
	 * return true if x is a valid coordinate (eg 0<=x<=1 for a segment)
	 * 
	 * @param x
	 *            coordinate
	 * @return true if x is a valid coordinate (eg 0<=x<=1 for a segment)
	 */
	abstract public boolean isValidCoord(double x);

	// //////////////////////////////////
	// XML
	// //////////////////////////////////

	/**
	 * returns all class-specific xml tags for saveXML
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
		// line thickness and type
		getLineStyleXML(sb);

	}

	public CoordSys getCoordSys() {
		return coordsys;
	}

	@Override
	public Coords getLabelPosition() {
		return coordsys.getPoint(0.5);
	}

	public Coords getCartesianEquationVector(CoordMatrix m) {

		if (m == null) {
			return CoordMatrixUtil.lineEquationVector(
					getCoordSys().getOrigin(), getCoordSys().getVx());
		}

		return CoordMatrixUtil.lineEquationVector(getCoordSys().getOrigin(),
				getCoordSys().getVx(), m);
	}

	public Coords getStartInhomCoords() {
		return getCoordSys().getOrigin().getInhomCoordsInSameDimension();
	}

	/**
	 * @return inhom coords of the end point
	 */
	public Coords getEndInhomCoords() {
		return getCoordSys().getPoint(1).getInhomCoordsInSameDimension();
	}

	public Coords getDirectionInD3() {
		return getCoordSys().getVx();
	}

	/**
	 * 
	 * @return start point
	 */
	public GeoPointND getStartPoint() {
		return startPoint;
	}

	/**
	 * 
	 * @return "end" point
	 */
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

	final public void translate(Coords v) {

		Coords o = getCoordSys().getOrigin();
		o.addInside(v);
		setCoord(o, getCoordSys().getVx());

	}

	// ///////////////////////////////////
	// POINTS ON COORD SYS
	// ///////////////////////////////////

	/** list of points on this line */
	protected ArrayList<GeoPointND> pointsOnLine;

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

	public final void addPointOnLine(GeoPointND p) {
		if (pointsOnLine == null)
			pointsOnLine = new ArrayList<GeoPointND>();

		if (!pointsOnLine.contains(p))
			pointsOnLine.add(p);
	}

	/**
	 * Calculates the distance between this line and line g.
	 * 
	 * @param g
	 *            line
	 * @return distance between lines
	 */
	final public double distance(GeoLineND g) {

		double dist;
		Coords cVector = this.getDirectionInD3().crossProduct(
				g.getDirectionInD3());
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

	public void setToImplicit() {
		// TODO Auto-generated method stub

	}

	public void setToExplicit() {
		// TODO Auto-generated method stub

	}

	public void setToParametric(String parameter) {
		// TODO Auto-generated method stub

	}

	// ///////////////////////////
	// MATRIX TRANSFORMABLE
	// ///////////////////////////

	@Override
	public boolean isMatrixTransformable() {
		return true;
	}

	public void matrixTransform(double a00, double a01, double a10, double a11) {

		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = CoordMatrix4x4.Identity();
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

	private CoordMatrix4x4 tmpMatrix4x4;

	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {

		if (tmpMatrix4x4 == null) {
			tmpMatrix4x4 = CoordMatrix4x4.Identity();
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

	private boolean trace;

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

	// //////////////////
	// ROTATE
	// //////////////////

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

		Coords oRot = new Coords((x - qx) * cos + (qy - y) * sin + qx, (x - qx)
				* sin + (y - qy) * cos + qy, z, w);

		double vx = v.getX();
		double vy = v.getY();
		double vw = v.getW();

		Coords vRot = new Coords(vx * cos - vy * sin, vx * sin + vy * cos, vz,
				vw);

		setCoord(oRot, vRot);

	}

	private Coords tmpCoords1, tmpCoords2;

	final private void rotate(NumberValue phiValue, Coords o1, Coords vn) {

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
		Coords oRot = tmpCoords1.add(v1.mul(cos)).add(v2.mul(sin));

		// new line direction
		v2 = vn2.crossProduct4(v);
		v1 = v2.crossProduct4(vn2);
		Coords vRot = v1.mul(cos).add(v2.mul(sin))
				.add(vn2.mul(v.dotproduct(vn2)));

		setCoord(oRot, vRot);
	}

	public void rotate(NumberValue phiValue, GeoPointND S,
			GeoDirectionND orientation) {

		Coords o1 = S.getInhomCoordsInD3();

		Coords vn = orientation.getDirectionInD3();

		rotate(phiValue, o1, vn);
	}

	public void rotate(NumberValue phiValue, GeoLineND line) {

		Coords o1 = line.getStartInhomCoords();
		Coords vn = line.getDirectionInD3();

		rotate(phiValue, o1, vn);

	}

	// //////////////////////
	// MIRROR
	// //////////////////////

	public void mirror(Coords Q) {

		Coords o = getCoordSys().getOrigin().mul(-1);

		o.addInside(Q.mul(2));

		setCoord(o, getCoordSys().getVx().mul(-1));

	}

	public void mirror(GeoLineND line) {

		Coords o1 = line.getStartInhomCoords();
		Coords vn = line.getDirectionInD3();

		Coords point = getCoordSys().getOrigin();
		if (tmpCoords1 == null) {
			tmpCoords1 = Coords.createInhomCoorsInD3();
		}
		point.projectLine(o1, vn, tmpCoords1, null); // point projected on the
														// line
		point = point.mul(-1);
		point.addInside(tmpCoords1.mul(2));

		double l = vn.getNorm();
		Coords v = getCoordSys().getVx();
		setCoord(point, vn.mul(2 * v.dotproduct(vn) / (l * l)).add(v.mul(-1)));

	}

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
		setCoord(
				point,
				tmpCoords1.setAdd(v,
						tmpCoords2.setMul(vn, -2 * v.dotproduct(vn))));

	}

	// //////////////////////
	// DILATE
	// //////////////////////

	public void dilate(NumberValue rval, Coords S) {

		double r = rval.getDouble();

		Coords o = getCoordSys().getOrigin().mul(r);

		o.addInside(S.mul(1 - r));

		setCoord(o, getCoordSys().getVx().mul(r));

	}

}
