package geogebra3D.kernel3D;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.PathParameter;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoPolygon3DInterface;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.kernelND.RotateableND;
import geogebra.common.kernel.kernelND.ViewCreator;
import geogebra.common.plugin.GeoClass;
import geogebra3D.App3D;
import geogebra3D.euclidian3D.Drawable3D;
import geogebra3D.euclidianForPlane.EuclidianViewForPlane;

/**
 * Class extending {@link GeoPolygon} in 3D world.
 * 
 * @author ggb3D
 * 
 */
public class GeoPolygon3D extends GeoPolygon implements GeoElement3DInterface,
		GeoPolygon3DInterface, ViewCreator, RotateableND {

	/** 2D coord sys where the polygon exists */
	private CoordSys coordSys;

	/** link with drawable3D */
	private Drawable3D drawable3D = null;

	/** image of the 3D points in the coord sys */
	private GeoPoint[] points2D;

	/** says if this is a part of a closed surface (e.g. a polyhedron) */
	private boolean isPartOfClosedSurface = false;

	private boolean createSegments = true;

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

	public GeoPolygon3D(Construction cons) {
		super(cons);
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

	// ///////////////////////////////////////
	// Overwrite GeoPolygon

	/**
	 * remove an old segment
	 * 
	 * @param oldSegment
	 *            the old segment
	 */

	@Override
	public void removeSegment(GeoSegmentND oldSegment) {
		((GeoSegment3D) oldSegment).getParentAlgorithm().remove();
	}

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
	public GeoSegmentND createSegment(GeoPointND startPoint,
			GeoPointND endPoint, boolean euclidianVisible) {

		// if start and end points are both 2D, then use super method
		if (!((GeoElement) startPoint).isGeoElement3D()
				&& !((GeoElement) endPoint).isGeoElement3D())
			return super.createSegment(startPoint, endPoint, euclidianVisible);

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
		
		if (isCopyInternal){
			return getPointND(i).getInhomCoordsInD(3);
		}
		
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

		if (reverseNormal)
			return coordSys.getNormal().mul(-1);
		
		return coordSys.getNormal();

	}



	private boolean reverseNormal = false;

	public void setReverseNormal() {
		reverseNormal = true;
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

		if (points == null)
			return;

		setDefined();

		coordSys = cs;

		points2D = new GeoPoint[points.length];
		for (int i = 0; i < points.length; i++) {
			points2D[i] = new GeoPoint(getConstruction());
		}

		// if there's no coord sys, create it with points
		if (coordSys == null) {
			coordSys = new CoordSys(2);
			updateCoordSys();
		}
	}

	/**
	 * check that all points are on coord sys, and calc their 2D coords
	 * 
	 * @return true if all points lie on coord sys
	 */
	public boolean checkPointsAreOnCoordSys() {
		for (int i = 0; i < points.length; i++) {

			// check if the vertex is defined and finite
			if (!points[i].isDefined() || !points[i].isFinite()) {
				coordSys.setUndefined();
				return false;
			}

			// project the point on the coord sys
			Coords[] project = points[i].getInhomCoordsInD(3).projectPlane(
					coordSys.getMatrixOrthonormal());

			// Application.debug("project["+i+"]="+project[1]);

			// check if the vertex lies on the coord sys
			if (!Kernel
					.isEqual(project[1].getZ(), 0, Kernel.STANDARD_PRECISION)) {
				coordSys.setUndefined();
				return false;
			}

			// set the 2D points
			points2D[i].setCoords(project[1].getX(), project[1].getY(),
					project[1].getW());
		}

		return true;
	}

	public boolean updateCoordSys() {

		coordSys.resetCoordSys();
		for (int i = 0; (!coordSys.isMadeCoordSys()) && (i < points.length); i++) {
			// Application.debug(points[i].getLabel()+"=\n"+points[i].getCoordsInD(3));

			// check if the vertex is defined and finite
			if (!points[i].isDefined() || !points[i].isFinite()) {
				coordSys.setUndefined();
				return false;
			}

			coordSys.addPoint(points[i].getInhomCoordsInD(3));
		}
		
		if(coordSys.getMadeCoordSys()!=2){
			coordSys.completeCoordSys2D();
		}

		if (coordSys.makeOrthoMatrix(false, false)) {
			checkPointsAreOnCoordSys();
		} else
			return false;

		return true;

	}
	
	/**
	 * set cs for region as simplest orthonormal coord sys
	 */
	final public void setOrthoNormalRegionCS(){
		updateRegionCS(new GeoPoint(cons, 0, 0, 1), new GeoPoint(cons, 1, 0, 1), new GeoPoint(cons, 0, 1, 1));
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
		if (coordSys == null)
			return false;
		else
			return super.isDefined() && coordSys.isDefined();
		// return coordSys.isDefined();
	}

	// ///////////////////////////////////////
	// link with Drawable3D

	/**
	 * set the 3D drawable linked to
	 * 
	 * @param d
	 *            the 3D drawable
	 */
	public void setDrawable3D(Drawable3D d) {
		drawable3D = d;
	}

	/**
	 * return the 3D drawable linked to
	 * 
	 * @return the 3D drawable linked to
	 */
	public Drawable3D getDrawable3D() {
		return drawable3D;
	}


	/**
	 * set the alpha value to alpha for openGL
	 * 
	 * @param alpha
	 *            alpha value
	 */
	/*
	 * public void setAlphaValue(float alpha) {
	 * 
	 * alphaValue = alpha;
	 * 
	 * }
	 */

	/**
	 * set if this is a part of a closed surface
	 * 
	 * @param v
	 */
	public void setIsPartOfClosedSurface(boolean v) {
		isPartOfClosedSurface = v;
	}

	@Override
	public boolean isPartOfClosedSurface() {
		return isPartOfClosedSurface;
	}


	
	
	public GeoElement getGeoElement2D() {
		return null;
	}

	public boolean hasGeoElement2D() {
		return false;
	}

	public void setGeoElement2D(GeoElement geo) {

	}

	// /////////////////////////////////
	// Path interface

	// TODO merge with GeoPolygon
	@Override
	public void pathChanged(GeoPointND PI) {
		
		//if kernel doesn't use path/region parameters, do as if point changed its coords
		if(!getKernel().usePathAndRegionParameters(PI)){
			pointChanged(PI);
			return;
		}

		// TODO remove that
		if (!(PI instanceof GeoPoint3D))
			return;

		GeoPoint3D P = (GeoPoint3D) PI;

		PathParameter pp = P.getPathParameter();

		// remember old parameter
		double oldT = pp.getT();

		// find the segment where the point lies
		int index = (int) pp.getT();
		GeoSegmentND seg = segments[index];

		// sets the path parameter for the segment, calc the new position of the
		// point
		pp.setT(pp.getT() - index);
		seg.pathChanged(P);

		// recall the old parameter
		pp.setT(oldT);
	}

	// TODO merge with GeoPolygon
	@Override
	public void pointChanged(GeoPointND PI) {

		// TODO remove that
		if (!(PI instanceof GeoPoint3D))
			return;

		GeoPoint3D P = (GeoPoint3D) PI;

		Coords coordsOld = P.getInhomCoords();

		// prevent from region bad coords calculations
		Region region = P.getRegion();
		P.setRegion(null);

		double minDist = Double.POSITIVE_INFINITY;
		Coords res = null;
		double param = 0;

		// find closest point on each segment
		PathParameter pp = P.getPathParameter();
		for (int i = 0; i < segments.length; i++) {

			P.setCoords(coordsOld, false); // prevent circular path.pointChanged

			segments[i].pointChanged(P);

			double dist;// = P.getInhomCoords().sub(coordsOld).squareNorm();
			// double dist = 0;
			if (P.getWillingCoords() != null && P.getWillingDirection() != null) {
				dist = P.getInhomCoords().distLine(P.getWillingCoords(),
						P.getWillingDirection());
			} else {
				dist = P.getInhomCoords().sub(coordsOld).squareNorm();
			}

			if (dist < minDist) {
				minDist = dist;
				// remember closest point
				res = P.getInhomCoords();
				param = i + pp.getT();
				// Application.debug(i);
			}
		}

		P.setCoords(res, false);
		pp.setT(param);

		P.setRegion(region);
	}

	// /////////////////////////////////
	// REGION3D INTERFACE

	@Override
	public void setRegionChanged(GeoPointND PI, double x, double y) {

		PI.setCoords2D(x, y, 1);
		PI.updateCoordsFrom2D(false, null);

	}

	@Override
	public boolean isInRegion(GeoPointND PI, boolean update) {

		GeoPoint3D P = (GeoPoint3D) PI;
		P.updateCoords2D(this, false);

		return isInRegion(P.getX2D(), P.getY2D());

	}

	@Override
	protected GeoPolygon newGeoPolygon(Construction cons) {
		return new GeoPolygon3D(cons, null);
	}
	

	@Override
	protected GeoPointND[] copyPoints(Construction cons) {
		return GeoElement.copyPointsND(cons, points);
	}

	@Override
	protected GeoPointND newGeoPoint() {
		return new GeoPoint3D(cons);
	}

	@Override
	public Coords getDirectionInD3() {
		return getCoordSys().getNormal();
	}

	/*
	 * public void calcArea() {
	 * 
	 * //TODO: non-simple case area = Math.abs(calcAreaWithSign(points2D)); }
	 */

	@Override
	public void translate(Coords v) {
		super.translate(v);
		getCoordSys().translate(v);
	}
	

	// ////////////////////////////////
	// 2D VIEW

	private EuclidianViewForPlane euclidianViewForPlane;

	public void createView2D() {
		euclidianViewForPlane = ((App3D) app).createEuclidianViewForPlane(this,true);
		euclidianViewForPlane.setTransformRegardingView();
	}
	
	public void removeView2D(){
		euclidianViewForPlane.doRemove();
	}
	
	public void setEuclidianViewForPlane(EuclidianView view){
		euclidianViewForPlane = (EuclidianViewForPlane) view;
	}
	
	public boolean hasView2DVisible(){
		return euclidianViewForPlane!=null && app.getGuiManager().showView(euclidianViewForPlane.getId());
	}
	

	public void setView2DVisible(boolean flag){
		
		if (euclidianViewForPlane==null){
			if (flag)
				createView2D();
			return;
		}
		
		app.getGuiManager().setShowView(flag, euclidianViewForPlane.getId());
		
		
	}
	
	
	

	@Override
	public void update() {
		super.update();
		if (euclidianViewForPlane != null) {
			euclidianViewForPlane.updateForPlane();
		}
	}
	
	

	
	@Override
	public void doRemove() {
		if (euclidianViewForPlane != null){
			removeView2D();
		}
		super.doRemove();
	}
	
	
	@Override
	public void matrixTransform(double a00, double a01, double a10, double a11) {
		super.matrixTransform(a00, a01, a10, a11);
		updateCoordSys();
	}

	@Override
	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {

		super.matrixTransform(a00, a01, a02, a10, a11, a12, a20, a21, a22);
		updateCoordSys();
	}
	
	/**
	 * set that init label has been called (or not)
	 * @param flag flag
	 */
	public void setInitLabelsCalled(boolean flag){
		initLabelsCalled = flag;
	}
	
	

	//////////////////////////////////////////////
	// ROTATION
	//////////////////////////////////////////////
	
	
	@Override
	public void rotate(NumberValue r) {
		getCoordSys().rotate(r.getDouble(), Coords.O);
	}
	
	@Override
	public void rotate(NumberValue r, GeoPoint S) {
		getCoordSys().rotate(r.getDouble(), S.getInhomCoordsInD(3));
	}

	public void rotate(NumberValue phiVal, GeoPointND Q, GeoDirectionND orientation) {
		
		rotate(phiVal, Q.getInhomCoordsInD(3), orientation.getDirectionInD3());
		
	}

	public void rotate(NumberValue phiVal, GeoLineND line) {
		
		rotate(phiVal, line.getStartInhomCoords(), line.getDirectionInD3());
		
	}
	
	final private void rotate(NumberValue phiVal, Coords center, Coords direction) {
		getCoordSys().rotate(phiVal.getDouble(), center, direction.normalized());
	}

}
