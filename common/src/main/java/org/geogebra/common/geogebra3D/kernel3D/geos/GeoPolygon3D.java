package org.geogebra.common.geogebra3D.kernel3D.geos;

import java.util.ArrayList;

import org.geogebra.common.euclidianForPlane.EuclidianViewForPlaneCompanionInterface;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoJoinPoints3D;
import org.geogebra.common.geogebra3D.kernel3D.transform.MirrorableAtPlane;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.algos.AlgoPolygon;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoPolygon3DInterface;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.RotateableND;
import org.geogebra.common.kernel.kernelND.ViewCreator;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.DoubleUtil;

/**
 * Class extending {@link GeoPolygon} in 3D world.
 * 
 * This is based on adding a coordsys where to plot 2D points. 2D points are
 * always defined and are used for all computations. 3D points are only used as
 * a link to created vertices.
 * 
 * @author ggb3D
 * 
 */
public class GeoPolygon3D extends GeoPolygon implements GeoPolygon3DInterface,
		ViewCreator, RotateableND, MirrorableAtPlane {

	/** 2D coord sys where the polygon exists */
	private CoordSys coordSys = new CoordSys(2);

	/** image of the 3D points in the coord sys */
	private GeoPoint[] points2D;

	/** says if this is a part of a closed surface (e.g. a polyhedron) */
	private boolean isPartOfClosedSurface = false;

	private boolean createSegments = true;
	private ArrayList<GeoPoint3D> points3DArray;

	private EuclidianViewForPlaneCompanionInterface euclidianViewForPlane;

	private CoordMatrix4x4 tmpMatrix4x4;

	private boolean reverseNormal = false;
	private double[] tmp3;

	/**
	 * default constructor
	 * 
	 * @param c
	 *            construction
	 * @param points
	 *            2D points
	 * @param cs2D
	 *            2D coord sys where the polygon is drawn
	 * @param createSegments
	 *            says if the polygon has to creates its edges
	 */
	public GeoPolygon3D(Construction c, GeoPointND[] points, CoordSys cs2D,
			boolean createSegments) {
		super(c, points, cs2D, createSegments);

		this.createSegments = createSegments;

	}

	/**
	 * common constructor for 3D.
	 * 
	 * @param c
	 *            the construction
	 * @param points
	 *            vertices
	 */
	public GeoPolygon3D(Construction c, GeoPointND[] points) {
		this(c, points, null, true);
	}

	/**
	 * @param cons
	 *            construction
	 */
	public GeoPolygon3D(Construction cons) {
		this(cons, false);
	}

	/**
	 * For intersection algos
	 * 
	 * @param cons
	 *            construction
	 * @param isIntersection
	 *            whether this is intersection
	 */
	public GeoPolygon3D(Construction cons, boolean isIntersection) {
		super(cons, isIntersection);
	}

	/**
	 * 
	 * @return true if is an intersection curve
	 */
	public boolean isIntersection() {
		return isIntersection;
	}

	// ///////////////////////////////////////
	// GeoPolygon3D
	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.POLYGON3D;
	}

	/**
	 * it's a 3D GeoElement.
	 * 
	 * @return true
	 */
	@Override
	public boolean isGeoElement3D() {
		return true;
	}

	@Override
	public boolean hasFillType() {
		return false;
	}

	// ///////////////////////////////////////
	// Overwrite GeoPolygon

	/**
	 * return a segment joining startPoint and endPoint
	 * 
	 * @param startPoint
	 *            the start point
	 * @param endPoint
	 *            the end point
	 * @return the segment
	 */
	@Override
	public GeoSegmentND createSegment(Construction cons1, GeoPointND startPoint,
			GeoPointND endPoint, boolean euclidianVisible) {

		// if start and end points are both 2D, then use super method
		if (!startPoint.isGeoElement3D() && !endPoint.isGeoElement3D()) {
			return super.createSegmentOwnDimension(cons1, startPoint, endPoint,
					euclidianVisible);
		}
		return createSegmentOwnDimension(cons1, startPoint, endPoint, euclidianVisible);
	}

	@Override
	public GeoSegmentND createSegmentOwnDimension(Construction cons1, GeoPointND startPoint,
			GeoPointND endPoint, boolean euclidianVisible) {
		AlgoJoinPoints3D algoSegment = new AlgoJoinPoints3D(cons, startPoint,
				endPoint, this, GeoClass.SEGMENT3D);
		cons.removeFromConstructionList(algoSegment);

		return createSegment((GeoSegmentND) algoSegment.getCS(),
				euclidianVisible);
	}

	/**
	 * Returns the i-th 2D point of this polygon.
	 * 
	 * @param i
	 *            number of point
	 * @return the i-th point
	 */
	@Override
	public GeoPoint getPoint(int i) {
		return points2D[i];
	}

	/**
	 * Returns the i-th 3D point of this polygon.
	 * 
	 * @param i
	 *            number of point
	 * @return the i-th point
	 */
	@Override
	public Coords getPoint3D(int i) {
		Coords v = super.getPoint3D(i);
		return coordSys.getPoint(v.getX(), v.getY());
	}

	/**
	 * return the normal of the polygon's plane
	 * 
	 * @return the normal of the polygon's plane
	 */
	@Override
	public Coords getMainDirection() {
		if (reverseNormal) {
			return coordSys.getNormal().mul(-1);
		}

		return coordSys.getNormal();

	}

	/**
	 * Returns the 2D points of this polygon. Note that this array may change
	 * dynamically.
	 */
	@Override
	public GeoPointND[] getPoints() {
		return points2D;
	}

	@Override
	public void setPoints2D(GeoPoint[] points) {
		points2D = points;
	}

	@Override
	public void setEuclidianVisible(boolean visible) {
		setEuclidianVisible(visible, createSegments);

	}

	// ///////////////////////////////////////
	// link with the 2D coord sys

	/**
	 * set the 2D coordinate system
	 * 
	 * @param cs
	 *            the 2D coordinate system
	 */
	@Override
	public void setCoordSys(CoordSys cs) {
		if (points == null) {
			return;
		}

		setDefined();

		coordSys = cs;

		points2D = new GeoPoint[points.length];
		for (int i = 0; i < points.length; i++) {
			points2D[i] = new GeoPoint(getConstruction(), true);
		}

		// if there's no coord sys, create it with points
		if (coordSys == null) {
			coordSys = new CoordSys(2);
			updateCoordSys();
		}
	}

	@Override
	public void setCoordSys(GeoPolygon poly) {
		// set coord sys
		coordSys.set(poly.getCoordSys());
	}

	@Override
	public void setCoordSysAndPoints3D(GeoPolygon poly) {
		// set coord sys
		setCoordSys(poly);

		// set 3D points
		setPoints3DLength();

		// set values
		for (int i = 0; i < points2D.length; i++) {
			Coords v = super.getPoint3D(i);
			GeoPoint3D point = points3DArray.get(i);
			point.setCoords(coordSys.getPoint(v.getX(), v.getY()));
		}

		// set last points undefined
		for (int i = points2D.length; i < points3DArray.size(); i++) {
			points3DArray.get(i).setUndefined();
		}

	}

	/**
	 * set 3D points length to match 2D points length
	 */
	public void setPoints3DLength() {
		if (points3DArray == null) {
			points3DArray = new ArrayList<>();
		}

		// adjust size
		for (int i = points3DArray.size(); i < points2D.length; i++) {
			GeoPoint3D point = new GeoPoint3D(cons);
			points3DArray.add(point);
			point.setCanBeRemovedAsInput(false);
		}

		// set array
		points = new GeoPointND[points2D.length];
		for (int i = 0; i < points2D.length; i++) {
			points[i] = points3DArray.get(i);
		}

	}

	@Override
	public void setPointsAndSegmentsLength(int polyLength) {
		setPointsLength(polyLength, null);
		setPoints3DLength();
		updateSegments(cons);
	}

	/**
	 * check that all points are on coord sys, and calc their 2D coords
	 * 
	 * @return true if all points lie on coord sys
	 */
	public boolean checkPointsAreOnCoordSys() {
		return checkPointsAreOnCoordSys(coordSys, points, points2D,
				new double[4]);
	}

	/**
	 * check that all points are on coord sys, and calc their 2D coords
	 * 
	 * @param coordSys
	 *            coordinate system
	 * @param points
	 *            points in 3D
	 * @param points2D
	 *            projections in 2D
	 * @param tmpCoords
	 *            temporary coords, must have length 4
	 * 
	 * @return true if all points lie on coord system
	 */
	static final public boolean checkPointsAreOnCoordSys(CoordSys coordSys,
			GeoPointND[] points, GeoPoint[] points2D, double[] tmpCoords) {

		Coords o = coordSys.getOrigin();
		Coords vn = coordSys.getVz();

		CoordMatrix4x4 matrix = coordSys.getMatrixOrthonormal();
		Coords d2 = new Coords(4);
		for (int i = 0; i < points.length; i++) {

			// check if the vertex is defined and finite
			if (!points[i].isDefined() || !points[i].isFinite()) {
				coordSys.setUndefined();
				return false;
			}

			Coords p = points[i].getInhomCoordsInD3();

			// origin-point vector
			d2.setSub(p, o);

			// check if the vertex lies on the coord sys
			if (!DoubleUtil.isZero(vn.dotproduct3(d2))) {
				coordSys.setUndefined();
				return false;
			}

			// set the 2D points
			points2D[i].setCoords(matrix.getVx().dotproduct3(d2),
					matrix.getVy().dotproduct3(d2), 1);

		}

		return true;
	}

	private void updatePointsND(GeoPointND[] geos) {
		int newLength = geos.length;
		int oldLength = getPointsLength();
		int ndLength = getPointsND().length;
		for (int i = newLength; i < ndLength && i < oldLength; i++) {
			getPointND(i).setUndefined();
		}
		setPointsLength(newLength, null);
		setPoints3DLength();
		for (int i = 0; i < getPointsND().length && i < newLength; i++) {
			ExpressionNode oldDef = getPointND(i).getDefinition();
			getPointND(i).set(geos[i].toGeoElement(), false);
			if (!getPointND(i).isIndependent()) {
				getPointND(i).setDefinition(oldDef);
			}
		}

	}

	@Override
	public void setPointsAndSegments(GeoPointND[] geos) {
		updatePointsND(geos);
		updateCoordSys();
		updateSegments(cons);

	}

	/**
	 * @return true if it has worked
	 */
	public boolean updateCoordSys() {
		return updateCoordSys(coordSys, points, points2D, new double[4]);

	}

	/**
	 * 
	 * @param coordSys
	 *            coord sys to update
	 * @param points
	 *            points that create the coord sys
	 * @param points2D
	 *            2D coords of the points in coord sys (if possible)
	 * @param tmpCoords
	 *            temporary coordinates, must have length 4
	 * @return true if it has worked
	 */
	static final public boolean updateCoordSys(CoordSys coordSys,
			GeoPointND[] points, GeoPoint[] points2D, double[] tmpCoords) {
		coordSys.resetCoordSys();
		for (int i = 0; (!coordSys.isMadeCoordSys())
				&& (i < points.length); i++) {
			// Application.debug(points[i].getLabel()+"=\n"+points[i].getCoordsInD3());

			// check if the vertex is defined and finite
			if (!points[i].isDefined() || !points[i].isFinite()) {
				coordSys.setUndefined();
				return false;
			}

			coordSys.addPoint(points[i].getInhomCoordsInD3());
		}

		if (coordSys.getMadeCoordSys() != 2) {
			coordSys.completeCoordSys2D();
		}

		if (coordSys.makeOrthoMatrix(false, false)) {
			return checkPointsAreOnCoordSys(coordSys, points, points2D,
					tmpCoords);
		}

		return true;
	}

	/**
	 * set cs for region as simplest orthonormal coord sys
	 */
	final public void setOrthoNormalRegionCS() {
		updateRegionCS(new GeoPoint(cons, 0, 0, 1), new GeoPoint(cons, 1, 0, 1),
				new GeoPoint(cons, 0, 1, 1));
	}

	/**
	 * update the coord system and 2D points
	 */
	/*
	 * public void updateCoordSysAndPoints2D(){
	 * 
	 * getCoordSys().getParentAlgorithm().update();
	 * 
	 * for(int i=0; i<points2D.length; i++)
	 * points2D[i].getParentAlgorithm().update(); }
	 */

	/**
	 * return the 2D coordinate system
	 * 
	 * @return the 2D coordinate system
	 */
	@Override
	public CoordSys getCoordSys() {
		return coordSys;
	}

	/** return true if there's a polygon AND a 2D coord sys */
	@Override
	public boolean isDefined() {
		if (coordSys == null) {
			return false;
		}
		return super.isDefined() && coordSys.isDefined();
	}

	/**
	 * set if this is a part of a closed surface
	 * 
	 * @param v
	 *            flag value
	 */
	public void setIsPartOfClosedSurface(boolean v) {
		isPartOfClosedSurface = v;
	}

	@Override
	public boolean isPartOfClosedSurface() {
		return isPartOfClosedSurface;
	}

	// /////////////////////////////////
	// Path interface

	// TODO merge with GeoPolygon
	@Override
	public void pathChanged(GeoPointND PI) {
		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		if (!getKernel().usePathAndRegionParameters(PI)) {
			pointChanged(PI);
			return;
		}

		// TODO remove that
		if (!(PI instanceof GeoPoint3D)) {
			return;
		}

		GeoPoint3D P = (GeoPoint3D) PI;

		PathParameter pp = P.getPathParameter();

		// remember old parameter
		double oldT = pp.getT();

		// find the segment where the point lies
		int index = (int) pp.getT();
		GeoSegmentND seg;
		if (segments == null) {
			seg = new GeoSegment3D(cons);
			((GeoSegment3D) seg).setCoordFromPoints(getPoint3D(index),
					getPoint3D((index + 1) % getPointsLength()));
		} else {
			seg = segments[index % segments.length];
		}

		// sets the path parameter for the segment, calc the new position of the
		// point
		pp.setT(pp.getT() - index);
		seg.pathChanged(P);

		// recall the old parameter
		pp.setT(oldT);
	}

	// /////////////////////////////////
	// REGION3D INTERFACE

	@Override
	public void setRegionChanged(GeoPointND PI, double x, double y) {
		PI.setCoords2D(x, y, 1);
		PI.updateCoordsFrom2D(false, null);
	}

	@Override
	public boolean isInRegion(GeoPointND PI) {
		Coords coords = PI.getCoordsInD2IfInPlane(getCoordSys());

		if (coords == null) { // point is not in plane containing the polygon
			return false;
		}

		return isInRegion(coords.getX(), coords.getY());

	}

	@Override
	protected GeoPolygon newGeoPolygon(Construction cons1) {
		return new GeoPolygon3D(cons1, null);
	}

	@Override
	public Coords getDirectionInD3() {
		return getMainDirection();
	}

	// ////////////////////////////////
	// 2D VIEW

	@Override
	public int getViewID() {
		return euclidianViewForPlane.getId();
	}

	@Override
	public void createView2D() {
		euclidianViewForPlane = kernel
				.getApplication().getCompanion()
				.createEuclidianViewForPlane(this, true);
		euclidianViewForPlane.setTransformRegardingView();
	}

	@Override
	public void removeView2D() {
		euclidianViewForPlane.doRemove();
	}

	@Override
	public void setEuclidianViewForPlane(
			EuclidianViewForPlaneCompanionInterface view) {
		euclidianViewForPlane = view;
	}

	@Override
	public boolean hasView2DVisible() {
		return euclidianViewForPlane != null && kernel.getApplication()
				.getGuiManager().showView(euclidianViewForPlane.getId());
	}

	@Override
	public void setView2DVisible(boolean flag) {
		if (euclidianViewForPlane == null) {
			if (flag) {
				createView2D();
			}
			return;
		}

		kernel.getApplication().getGuiManager().setShowView(flag,
				euclidianViewForPlane.getId());

	}

	@Override
	public void update(boolean drag) {
		super.update(drag);
		if (euclidianViewForPlane != null) {
			euclidianViewForPlane.updateForPlane();
		}
	}

	@Override
	public void doRemove() {
		if (euclidianViewForPlane != null) {
			removeView2D();
		}
		super.doRemove();
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

		double[] ret = getCoordSys().matrixTransform(tmpMatrix4x4);

		super.matrixTransform(ret[0], ret[1], 0, ret[2]);
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

		double[] ret = getCoordSys().matrixTransform(tmpMatrix4x4);

		super.matrixTransform(ret[0], ret[1], 0, ret[2]);

	}

	// ////////////////////////////////////////////
	// ROTATION
	// ////////////////////////////////////////////

	@Override
	public void rotate(NumberValue r) {
		getCoordSys().rotate(r.getDouble(), Coords.O);

		// we need to update points and segments also
		for (int i = 0; i < getPointsLength(); i++) {
			((GeoPoint3D) super.getPointND(i)).rotate(r);
		}

		for (GeoSegmentND seg : getSegments()) {
			if (seg.isGeoElement3D()) {
				((GeoSegment3D) seg).rotate(r);
			}
		}
	}

	@Override
	public void rotate(NumberValue r, GeoPointND S) {
		getCoordSys().rotate(r.getDouble(), S.getInhomCoordsInD3());

		// we need to update points and segments also
		for (int i = 0; i < getPointsLength(); i++) {
			((GeoPoint3D) super.getPointND(i)).rotate(r, S);
		}

		for (GeoSegmentND seg : getSegments()) {
			if (seg.isGeoElement3D()) {
				((GeoSegment3D) seg).rotate(r, S);
			}
		}
	}

	@Override
	public void rotate(NumberValue phiVal, GeoPointND Q,
			GeoDirectionND orientation) {

		rotate(phiVal, Q.getInhomCoordsInD3(), orientation.getDirectionInD3());

		// we need to update points and segments also
		for (int i = 0; i < getPointsLength(); i++) {
			((GeoPoint3D) super.getPointND(i)).rotate(phiVal, Q, orientation);
		}

		for (GeoSegmentND seg : getSegments()) {
			if (seg.isGeoElement3D()) {
				((GeoSegment3D) seg).rotate(phiVal, Q, orientation);
			}
		}

	}

	@Override
	public void rotate(NumberValue phiVal, GeoLineND line) {
		rotate(phiVal, line.getStartInhomCoords(), line.getDirectionInD3());

		// we need to update points and segments also
		for (int i = 0; i < getPointsLength(); i++) {
			((GeoPoint3D) super.getPointND(i)).rotate(phiVal, line);
		}

		for (GeoSegmentND seg : getSegments()) {
			if (seg.isGeoElement3D()) {
				((GeoSegment3D) seg).rotate(phiVal, line);
			}
		}

	}

	final private void rotate(NumberValue phiVal, Coords center,
			Coords direction) {
		getCoordSys().rotate(phiVal.getDouble(), center,
				direction.normalized());
	}

	// ////////////////////////////////////////////
	// TRANSLATE
	// ////////////////////////////////////////////

	@Override
	public void translate(Coords v) {
		getCoordSys().translate(v);

		// we need to update points and segments also
		for (int i = 0; i < getPointsLength(); i++) {
			super.getPointND(i).translate(v);
		}

		for (GeoSegmentND seg : getSegments()) {
			if (seg.isGeoElement3D()) {
				((GeoSegment3D) seg).translate(v);
			}
		}

	}

	// //////////////////////
	// MIRROR
	// //////////////////////

	@Override
	public void mirror(Coords Q) {
		getCoordSys().mirror(Q);

		// we need to update points and segments also
		for (int i = 0; i < getPointsLength(); i++) {
			((GeoPoint3D) super.getPointND(i)).mirror(Q);
		}

		for (GeoSegmentND seg : getSegments()) {
			if (seg.isGeoElement3D()) {
				((GeoSegment3D) seg).mirror(Q);
			}
		}
	}

	@Override
	public void mirror(GeoLineND line) {
		Coords point = line.getStartInhomCoords();
		Coords direction = line.getDirectionInD3().normalized();

		getCoordSys().mirror(point, direction);

		// orientation is reversed
		reverseNormal = !reverseNormal;

		// we need to update points and segments also
		for (int i = 0; i < getPointsLength(); i++) {
			((GeoPoint3D) super.getPointND(i)).mirror(line);
		}

		for (GeoSegmentND seg : getSegments()) {
			if (seg.isGeoElement3D()) {
				((GeoSegment3D) seg).mirror(line);
			}
		}
	}

	@Override
	public void mirror(GeoCoordSys2D plane) {
		getCoordSys().mirror(plane.getCoordSys());

		// orientation is reversed
		reverseNormal = !reverseNormal;

		// we need to update points and segments also
		for (int i = 0; i < getPointsLength(); i++) {
			((GeoPoint3D) super.getPointND(i)).mirror(plane);
		}

		for (GeoSegmentND seg : getSegments()) {
			if (seg.isGeoElement3D()) {
				((GeoSegment3D) seg).mirror(plane);
			}
		}
	}

	// //////////////////////
	// DILATE
	// //////////////////////

	@Override
	public void dilate(NumberValue rval, Coords S) {
		double r = rval.getDouble();

		getCoordSys().dilate(r, S);

		if (r < 0) { // mirror was done in coord sys
			r = -r;
		}

		for (int i = 0; i < getPointsLength(); i++) {
			getPoint(i).dilate(r);
		}

		this.calcArea();

		// we need to update points and segments also
		for (int i = 0; i < getPointsLength(); i++) {
			((GeoPoint3D) super.getPointND(i)).dilate(rval, S);
		}

		for (GeoSegmentND seg : getSegments()) {
			if (seg.isGeoElement3D()) {
				((GeoSegment3D) seg).dilate(rval, S);
			}
		}
	}

	// /////////////////////////////////
	// REVERSE ORIENTATION
	// /////////////////////////////////

	/**
	 * set that normal should be reversed
	 * 
	 * @param flag
	 *            flag
	 */
	public void setReverseNormal(boolean flag) {
		reverseNormal = flag;
	}

	/**
	 * set that normal should be reversed
	 */
	public void setReverseNormal() {
		setReverseNormal(true);
	}

	@Override
	public boolean hasReverseNormal() {
		return reverseNormal;
	}

	@Override
	public void set(GeoElementND geo, Construction cons1) {
		if (geo.isGeoPolygon() && geo.isGeoElement3D()) {
			reverseNormal = ((GeoPolygon3D) geo).reverseNormal;
		}

		super.set(geo, cons1);
	}

	/**
	 * Yields true if the points of this polygon are equal to the points of
	 * polygon p.
	 */
	@Override
	final public boolean isEqual(GeoElementND geo) {
		// Log.debug("Entree 3D");
		// return false if it's a different type
		if (geo.isGeoPolygon()) {

			GeoPolygon g = (GeoPolygon) geo;

			int gLength = g.getPointsLength();
			if (gLength == this.getPointsLength()) {

				// Log.debug("Polygones de meme longueur");
				// search for a first common point
				Coords firstPoint = this.getPoint3D(0);
				boolean fPointFound = false;
				int iFirstPoint = 0;
				while ((!fPointFound) && (iFirstPoint < gLength)) {
					if (firstPoint.equalsForKernel(g.getPoint3D(iFirstPoint))) {
						fPointFound = true;
					} else {
						iFirstPoint++;
					}
				}

				// Log.debug("Premier point commun : "+iFirstPoint);
				// next point
				if (fPointFound) {
					boolean sPointFound = false;
					int step = 1;
					if (this.getPoint3D(1).equalsForKernel(
							g.getPoint3D((iFirstPoint + step) % gLength))) {
						sPointFound = true;
					} else {
						step = -1;
						int j = iFirstPoint + step;
						if (j < 0) {
							j = gLength - 1;
						}
						if (this.getPoint3D(1)
								.equalsForKernel(g.getPoint3D(j))) {
							sPointFound = true;
						}
					}

					// Log.debug("Second point commun : "+(iFirstPoint+step));
					// other points
					if (sPointFound) {
						int i = 2;
						int j = iFirstPoint + step + step;
						if (j < 0) {
							j = j + gLength;
						}
						j = j % gLength;
						boolean pointOK = true;
						while ((pointOK) && (i < gLength)) {
							// Log.debug("Recherche pour : "+i+"="+j);
							pointOK = (this.getPoint3D(i)
									.equalsForKernel(g.getPoint3D(j)));
							/*
							 * if (pointOK){ Log.debug("Point suivant : "+j);
							 * }else { Log.debug("Arret : "+j); }
							 */
							j = j + step;
							if (j < 0) {
								j = gLength - 1;
							}
							j = j % gLength;
							i++;
						}
						return pointOK;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean isConvexInverseDirection() {
		// face orientation is created by the points
		return reverseNormal;
	}

	@Override
	public void calcCentroid(GeoPointND p) {
		// just do long method
		// could improve by transforming original centroid, but not worth doing
		// test-case Centroid[Dilate[Polygon[(0,0),(1,1),(1,0)],4]]
		// test-case Centroid[Polygon[(0,0),(1,1),(1,0)]]
		if (tmp3 == null) {
			tmp3 = new double[3];
		}
		AlgoPolygon.calcCentroid(tmp3, area, getPoints());
		if (Double.isNaN(tmp3[0])) {
			p.setUndefined();
		} else {
			Coords c = getCoordSys().getPoint(tmp3[0], tmp3[1], tmp3[2]);
			p.setCoords(c, false);
		}
	}

	@Override
	public GeoPointND newGeoPoint(Construction cons1) {
		return new GeoPoint3D(cons1);
	}

	@Override
	public ValidExpression toValidExpression() {
		return getNumber();
	}

	@Override
	public boolean isRegion3D() {
		return true;
	}
}
