package org.geogebra.common.geogebra3D.euclidian3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianControllerCompanion;
import org.geogebra.common.euclidian.EuclidianCursor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.euclidian.draw.DrawList;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawConic3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawConicSection3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawExtrusionOrConify3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawIntersectionCurve3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawLine3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPoint3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPolyLine3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPolygon3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawPolyhedron3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawSegment3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import org.geogebra.common.geogebra3D.euclidianFor3D.EuclidianControllerFor3DCompanion;
import org.geogebra.common.geogebra3D.kernel3D.ConstructionDefaults3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoDependentVector3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoDispatcher3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoIntersectCS1D2D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoIntersectCS1D2D.ConfigLinePlane;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoIntersectPlanes;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoUnitVector3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConicSection;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCoordSys1D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DPart;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoDynamicCoordinatesInterface;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoTranslate;
import org.geogebra.common.kernel.algos.AlgoVectorPoint;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.FromMeta;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElement.HitType;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.Test;
import org.geogebra.common.kernel.geos.Transformable;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoCoordSys;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoImplicitSurfaceND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoPolyhedronInterface;
import org.geogebra.common.kernel.kernelND.GeoQuadric3DLimitedInterface;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.Unicode;
import org.geogebra.common.util.debug.Log;

/**
 * Controller for the 3D view
 * 
 * @author Mathieu
 * 
 */
public abstract class EuclidianController3D extends EuclidianController {

	/** 3D point that is currently moved */
	// protected GeoPoint3D movedGeoPoint3D = null;

	/** min/max values for moving a point */
	private double[] xMinMax, yMinMax;
	/** min/max values for moving a point along z */
	protected double[] zMinMax;

	/** current plane where the movedGeoPoint3D lies */
	protected CoordMatrix4x4 currentPlane = null;

	/** 3D view controlled by this */
	protected EuclidianView3D view3D; // TODO move to EuclidianViewInterface

	private GPoint mouseLocOld = new GPoint();
	private Coords positionOld = new Coords(4);

	/** picking point */
	protected Coords pickPoint;

	/** says that a free point has just been created (used for 3D cursor) */
	private boolean freePointJustCreated = false;

	/** says if a rotation of the view occurred (with right-button) */
	protected boolean viewRotationOccured = false;

	/**
	 * scale factor for changing angle of view : 2Pi <-> 360 pixels (so 1 pixel
	 * = 1degrees )
	 */
	static final public double ANGLE_TO_DEGREES = 2 * Math.PI / 360;
	/** maximum vertical angle */
	static final public int ANGLE_MAX = 90;

	/** for animated rotation */
	protected double animatedRotSpeed;
	/** used when time is needed */
	protected double timeOld;
	/** used to record x information */
	private int xOld;

	private Hits3D goodHits;

	/**
	 * Store infos for intersection curve
	 */
	public static class IntersectionCurve {
		protected GeoElement geo1, geo2, result;
		public Drawable3D drawable;
		protected boolean hitted;

		/**
		 * constructor
		 * 
		 * @param geo1
		 *            first geo for the intersection
		 * @param geo2
		 *            second geo for the intersection
		 * @param result
		 *            result intersection
		 * @param hitted
		 *            say if the intersection is hitted
		 * @param drawable
		 *            drawable for the intersection
		 */
		public IntersectionCurve(GeoElement geo1, GeoElement geo2,
				GeoElement result, boolean hitted, Drawable3D drawable) {
			this.geo1 = geo1;
			this.geo2 = geo2;
			this.result = result;
			this.hitted = hitted;
			this.drawable = drawable;
		}

	}

	/**
	 * array list for intersection curves
	 */
	private ArrayList<IntersectionCurve> intersectionCurveList = new ArrayList<IntersectionCurve>();
	// private ArrayList<Drawable3D> intersectionCurves = new
	// ArrayList<Drawable3D>();

	// SELECTED GEOS
	/** 2D coord sys (plane, polygon, ...) */


	/**
	 * common constructor
	 * 
	 * @param app
	 *            application
	 */
	public EuclidianController3D(App app) {
		super(app);

		// inits min max
		xMinMax = new double[2];
		yMinMax = new double[2];
		zMinMax = new double[2];

	}

	@Override
	protected EuclidianControllerCompanion newCompanion() {
		return new EuclidianController3DCompanion(this);
	}

	/**
	 * sets the view controlled by this
	 * 
	 * @param view
	 *            euclidian view (3D assumed, not checked)
	 */
	public void setView3D(EuclidianView view) {
		this.view3D = (EuclidianView3D) view;

	}

	// //////////////////////////////////////////
	// setters movedGeoElement -> movedGeoPoint, ...

	private static double[] getMinMax(double min, double val, double max) {
		double min1 = min, max1 = max;
		if (val < min)
			min1 = val;
		else if (val > max)
			max1 = val;

		return new double[] { min1, max1 };
	}

	@Override
	public void setMovedGeoPoint(GeoElement geo) {

		movedGeoPoint = (GeoPointND) geo;
		((EuclidianView3D) view).setPointDecorations(movedGeoPoint);

		AlgoElement algo = ((GeoElement) movedGeoPoint).getParentAlgorithm();
		if (algo instanceof AlgoDynamicCoordinatesInterface) {
			movedGeoPoint = ((AlgoDynamicCoordinatesInterface) algo)
					.getParentPoint();
		}

		Coords coords = movedGeoPoint.getInhomCoordsInD3();

		// sets the min/max values
		double pointSize = movedGeoPoint.getPointSize()
				* DrawPoint3D.DRAW_POINT_FACTOR;
		double size;
		size = pointSize / view3D.getXscale();
		xMinMax = getMinMax(view3D.getXmin() + size, coords.getX(),
				view3D.getXmax() - size);
		size = pointSize / view3D.getYscale();
		yMinMax = getMinMax(view3D.getYmin() + size, coords.getY(),
				view3D.getYmax() - size);
		size = pointSize / view3D.getZscale();
		zMinMax = getMinMax(view3D.getZmin() + size, coords.getZ(),
				view3D.getZmax() - size);

		updateMovedGeoPointStartValues(coords);

		view3D.setDragCursor();
	}

	/**
	 * update values needed to move a point
	 * 
	 * @param coords
	 *            start point coords
	 */
	protected void updateMovedGeoPointStartValues(Coords coords) {
		if (!movedGeoPoint.hasPath() && !movedGeoPoint.hasRegion()) {

			CoordMatrix4x4.Identity(getCurrentPlane());
			// update the moving plane altitude
			getCurrentPlane().set(coords, 4);

		}
	}

	// //////////////////////////////////////////:
	// moving points

	/**
	 * return the current plane for moving
	 * 
	 * @return the current plane
	 */
	protected CoordMatrix4x4 getCurrentPlane() {
		if (currentPlane == null) {
			currentPlane = CoordMatrix4x4.Identity();
		}
		return currentPlane;
	}

	/**
	 * set the current plane for moving
	 * 
	 * @param plane
	 *            a plane
	 */
	protected void setCurrentPlane(CoordMatrix4x4 plane) {
		currentPlane = plane;
	}

	/**
	 * set the current plane to the path's moving plane
	 * 
	 * @param path
	 *            a path
	 */
	/*
	 * private void setCurrentPlane(Path3D path){ Ggb3DMatrix4x4 plane =
	 * path.getMovingMatrix(view3D.getToScreenMatrix());
	 * view3D.toSceneCoords3D(plane); setCurrentPlane(plane); }
	 */

	/**
	 * moves the point according to the current moving plane and mouse location
	 * 
	 * @param point
	 *            the point to move
	 * @param useOldMouse
	 *            if true, shift the point according to old mouse location
	 */
	protected void movePointOnCurrentPlane(GeoPointND point,
			boolean useOldMouse) {

		// Michael Borcherds
		// move mouse fast, sometimes get mouseLoc = null
		if (mouseLoc == null)
			return;

		// getting current pick point and direction v
		Coords o;
		if (useOldMouse) {
			// if (movePointMode != MOVE_POINT_MODE_XY){
			mouseLocOld.setLocation(mouseLoc.x, mouseLoc.y);
			positionOld = point.getCoords().copyVector();
			// movePointMode = MOVE_POINT_MODE_XY;
			// }
			o = view3D.getPickFromScenePoint(positionOld, mouseLoc.x
					- mouseLocOld.x, mouseLoc.y - mouseLocOld.y);
		} else {
			o = view3D.getPickPoint(mouseLoc);
		}

		view3D.toSceneCoords3D(o);
		addOffsetForTranslation(o);

		// getting new position of the point
		if (Kernel.isEqual(view3D.getHittingDirection().dotproduct(getCurrentPlane().getVz()), 0.0,
				Kernel.STANDARD_PRECISION)) {
			// hitting direction is parallel to the plane
			// project on (mouse position, hitting direction) line
			point.getInhomCoordsInD3().projectLine(o,
					view3D.getHittingDirection(), tmpCoords2);
			// now project on plane
			tmpCoords2.projectPlane(getCurrentPlane(), tmpCoords);
		} else {
			o.projectPlaneThruV(getCurrentPlane(),
					view3D.getHittingDirection(), tmpCoords);
		}


		// min-max x and y values
		checkXYMinMax(tmpCoords);

		// capturing points
		((EuclidianController3DCompanion) companion)
				.checkPointCapturingXY(tmpCoords);

		// set point coords
		point.setCoords(tmpCoords, true);
	}

	private Coords tmpCoords = new Coords(4);
	private Coords tmpCoords2 = new Coords(4);

	protected boolean checkXYMinMax(Coords v) {
		
		if (getMoveMode() != EuclidianController.MOVE_POINT) {
			return false;
		}

		boolean changed = false;

		// min-max x value
		if (v.getX() > xMinMax[1]) {
			v.setX(xMinMax[1]);
			changed = true;
		} else if (v.getX() < xMinMax[0]) {
			v.setX(xMinMax[0]);
			changed = true;
		}

		// min-max y value
		if (v.getY() > yMinMax[1]) {
			v.setY(yMinMax[1]);
			changed = true;
		} else if (v.getY() < yMinMax[0]) {
			v.setY(yMinMax[0]);
			changed = true;
		}

		return changed;
	}

	/**
	 * set the mouse information (location and viewing direction in real world
	 * coordinates) to the point
	 * 
	 * @param point
	 *            a point
	 */
	final protected void setMouseInformation(GeoPoint3D point) {

		setMouseOrigin(point);

		point.setWillingDirection(view3D.getHittingDirection());
	}

	/**
	 * set mouse origin information
	 * 
	 * @param point
	 *            a point
	 */
	protected void setMouseOrigin(GeoPoint3D point) {
		// Michael Borcherds
		// move mouse fast, sometimes get mouseLoc = null
		if (mouseLoc == null)
			return;

		Coords o = view3D.getPickPoint(mouseLoc);
		view3D.toSceneCoords3D(o);

		addOffsetForTranslation(o);
		point.setWillingCoords(o);
	}

	/**
	 * add offset when needed
	 * 
	 * @param o
	 *            coords
	 */
	public void addOffsetForTranslation(Coords o) {
		if (moveMode == MOVE_POINT_WITH_OFFSET) {
			o.setAdd(o, translationVec3D);
		}
	}

	@Override
	protected void moveTextAbsoluteLocation() {
		Coords o = view3D.getPickPoint(mouseLoc);
		view3D.toSceneCoords3D(o);
		// o =
		// (o.sub(startPoint3D)).projectPlaneThruVIfPossible(CoordMatrix4x4.IDENTITY,
		// view3D.getViewDirection())[0];
		o.projectPlaneThruVIfPossible(CoordMatrix4x4.IDENTITY,
				view3D.getHittingDirection(), tmpCoords);
		// Application.debug(o);
		// ((GeoPoint2)
		// movedGeoText.getStartPoint()).setCoords(o.getX(),o.getY(), 1.0);
		((GeoPoint) movedGeoText.getStartPoint()).setCoords(tmpCoords.getX()
				- startPoint3DxOy.getX(),
				tmpCoords.getY() - startPoint3DxOy.getY(), 1.0);
	}

	// ////////////////////////////////////////////
	// creating a new point

	@Override
	protected Hits getRegionHits(Hits hits) {
		return hits.getHits(Test.REGION3D, tempRegionHitsArrayList);
	}

	/**
	 * 
	 * @param point
	 *            cursor
	 * @return free point from cursor coords
	 */
	private GeoPoint3D getNewPointFree(GeoPointND point) {
		GeoPoint3D point3D = (GeoPoint3D) kernel.getManager3D().Point3D(null,
				0, 0, 0, false);
		point3D.setCoords(point);
		point3D.updateCoords();
		view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_ALREADY);
		view3D.updateMatrixForCursor3D();
		GeoPoint3D cursor = view3D.getCursor3D();
		cursor.setRegion(null);
		cursor.setPath(null);
		cursor.setMoveMode(point3D.getMoveMode());
		if (isModeForCreatingPoint(mode)) {
			freePointJustCreated = true;
		}
		return point3D;
	}

	private boolean lastGetNewPointWasExistingPoint = false;

	/**
	 * return a copy of the preview point if one
	 */
	@Override
	protected GeoPointND getNewPoint(Hits hits, boolean onPathPossible,
			boolean inRegionPossible, boolean intersectPossible, boolean complex) {

		GeoPoint3D point = view3D.getCursor3D();

		GeoPoint3D point3D;
		GeoPointND ret;

		lastGetNewPointWasExistingPoint = false;

		// Application.debug("view3D.getCursor3DType()="+view3D.getCursor3DType());

		switch (view3D.getCursor3DType()) {
		case EuclidianView3D.PREVIEW_POINT_FREE:
			point3D = getNewPointFree(point);
			ret = point3D;
			pointCreated = point3D;
			break;

		case EuclidianView3D.PREVIEW_POINT_PATH:
			if (onPathPossible) {
				Path path = point.getPath();
				if (path.toGeoElement().isGeoElement3D()
						|| (path.toGeoElement().isGeoList() && ((GeoList) path
								.toGeoElement()).containsGeoElement3D())) {
					point3D = (GeoPoint3D) getKernel().getManager3D().Point3D(
							null, path, false);
					point3D.setWillingCoords(point.getCoords());
					point3D.doPath();
					point3D.setWillingCoordsUndefined();
					point3D.setWillingDirectionUndefined();
					ret = point3D;
					pointCreated = point3D;
				} else {
					Coords coords = point.getCoordsInD2();
					pointCreated = createNewPoint2D(null, false, path,
							coords.getX(), coords.getY(), false, false);
					return pointCreated;
				}

			} else {
				pointCreated = null;
				return null;
			}
			break;

		case EuclidianView3D.PREVIEW_POINT_REGION:
		case EuclidianView3D.PREVIEW_POINT_REGION_AS_PATH:
			if (inRegionPossible) {
				Region region = point.getRegion();
				if (region == getKernel().getXOYPlane()) {
					point3D = getNewPointFree(point);
					ret = point3D;
					pointCreated = point3D;
				} else if (region.isRegion3D()) {
					Coords coords = point.getCoords();
					point3D = (GeoPoint3D) getKernel().getManager3D()
							.Point3DIn(null, region, coords, true, false);
					point3D.doRegion();
					point3D.setWillingCoordsUndefined();
					point3D.setWillingDirectionUndefined();
					ret = point3D;
					pointCreated = point3D;
				} else {
					Coords coords = point.getCoordsInD2();
					pointCreated = createNewPoint2D(null, false, region,
							coords.getX(), coords.getY(), false, false);
					return pointCreated;
				}
			} else {
				pointCreated = null;
				return null;
			}
			break;

		case EuclidianView3D.PREVIEW_POINT_DEPENDENT:
			if (intersectPossible) {

				// get last intersection preview point
				GeoPointND intersectionPoint = view3D.getIntersectionPoint();
				// add it to construction
				intersectionPoint.getParentAlgorithm().addToConstructionList();
				intersectionPoint.setLabel(null);
				pointCreated = intersectionPoint;

				// check if it's a 3D point
				if (((GeoElement) intersectionPoint).isGeoElement3D()) {
					point3D = (GeoPoint3D) intersectionPoint;
				} else {
					return intersectionPoint;
				}

			} else {
				point3D = null;
				pointCreated = null;
			}
			return point3D;

		case EuclidianView3D.PREVIEW_POINT_ALREADY:
			// current mode is not MOVE
			// we return current moved point if first hitted
			GeoPointND firstPoint = (GeoPointND) hits
					.getFirstHit(Test.GEOPOINTND);
			if (firstPoint == getMovedGeoPoint()) {
				lastGetNewPointWasExistingPoint = true;
				return firstPoint;
			}
			resetMovedGeoPoint();
			return null;
		case EuclidianView3D.PREVIEW_POINT_NONE:
		default:
			pointCreated = super.getNewPoint(hits, onPathPossible,
					inRegionPossible, intersectPossible, false);
			return pointCreated;

		}

		((GeoElement) ret).update();

		// view3D.addToHits3D((GeoElement) ret);

		setMovedGeoPoint(point3D);

		view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_ALREADY);
		view3D.updateMatrixForCursor3D();

		return ret;

	}

	/** put sourcePoint coordinates in point */
	@Override
	protected void createNewPoint(GeoPointND sourcePoint) {
		GeoPoint3D point3D = view3D.getCursor3D();

		// set coords
		point3D.setCoords(sourcePoint.getCoordsInD3(), false);

		// set/remove path/region
		point3D.setPath(sourcePoint.getPath());
		point3D.setRegion(sourcePoint.getRegion());

		// update cursor 3D infos
		if (((GeoElement) sourcePoint).isIndependent()
				|| !((GeoElement) sourcePoint).isGeoElement3D())
			point3D.setMoveNormalDirection(Coords.VZ);
		else if (sourcePoint.hasRegion())
			point3D.setMoveNormalDirection(sourcePoint.getRegionParameters()
					.getNormal());
		view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_ALREADY);
		point3D.setMoveMode(sourcePoint.getMoveMode());
		point3D.setPointSize(sourcePoint.getPointSize());

		// Application.debug("sourcePoint:\n"+sourcePoint.getCoordsInD3()+"\ncursor:\n"+view3D.getCursor3D().getCoordsInD3());
	}

	/** put intersectionPoint coordinates in point */
	@Override
	protected void createNewPointIntersection(GeoPointND intersectionPoint) {
		GeoPoint3D point3D = view3D.getCursor3D();
		point3D.setCoords(point3D.getCoords()
				.setInhomCoords(intersectionPoint.getCoordsInD3()),
				false);
		view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_DEPENDENT);
		view3D.setIntersectionPoint(intersectionPoint);
		// Application.debug("\nintersectionPoint="+intersectionPoint);
	}

	@Override
	protected boolean createNewPointInRegionPossible(GeoConicND conic) {

		return conic.getLastHitType() == HitType.ON_FILLING;

	}

	/*
	 * protected void updateMovedGeoPoint(GeoPointND point){ //movedGeoPoint3D =
	 * (GeoPoint3D) point; setMovedGeoPoint((GeoPoint3D) point); }
	 */
	
	private GeoPointND singleIntersectionPoint;

	// tries to get a single intersection point for the given hits
	// i.e. hits has to include two intersectable objects.
	@Override
	protected GeoPointND getSingleIntersectionPoint(Hits hits) {
		// Log.debug(hits);

		if (hits.isEmpty() || hits.size() < 2)
			return null;

		if (mouseLoc == null)
			return null;

		GeoElement a = hits.get(0);

		// remove planes containing a (when a is line, conic, or polygon --
		// notice that a plane containing a line is ever after in hits order)

		if (a.isGeoLine()) { // remove planes containing line a
			while (hits.size() >= 2) {
				if (hits.get(1).isGeoPlane()
						&& AlgoIntersectCS1D2D.getConfigLinePlane(
								(GeoLineND) a, ((GeoCoordSys2D) hits.get(1))) == ConfigLinePlane.CONTAINED)
					hits.remove(1);
				else
					break;
			}
		} else if (a.isGeoConic()) { // remove planes containing conic a
			while (hits.size() >= 2) {
				if (hits.get(1).isGeoPlane()
						&& AlgoIntersectPlanes.getConfigPlanePlane(
								(((GeoConicND) a).getCoordSys()),
								(((GeoCoordSys2D) hits.get(1)).getCoordSys())) == AlgoIntersectPlanes.RESULTCATEGORY_CONTAINED)
					hits.remove(1);
				else
					break;
			}
		} else if (a.isGeoPolygon()) { // remove planes containing polygon a
			while (hits.size() >= 2) {
				if (hits.get(1) instanceof GeoCoordSys2D
						&& AlgoIntersectPlanes.getConfigPlanePlane(
								(((GeoPolygon) a).getCoordSys()),
								(((GeoCoordSys2D) hits.get(1)).getCoordSys())) == AlgoIntersectPlanes.RESULTCATEGORY_CONTAINED)
					hits.remove(1);
				else
					break;
			}
		}

		if (hits.size() < 2)
			return null;

		// Application.debug(hits.toString());
		GeoElement b = hits.get(1);
		singleIntersectionPoint = null;
		
		
		boolean oldSilentMode = getKernel().isSilentMode();
		kernel.setSilentMode(true);

		// check if a and b are two 2D geos
		if (!a.isGeoElement3D() && !b.isGeoElement3D()) {
			// get pick point coords in xOy plane
			view3D.getToSceneMatrix()
					.mul(view3D.getPickPoint(mouseLoc))
					.projectPlaneThruVIfPossible(CoordMatrix4x4.IDENTITY,
							view3D.getViewDirection(), tmpCoords);
			xRW = tmpCoords.getX();
			yRW = tmpCoords.getY();

			// apply 2D method
			singleIntersectionPoint = ((EuclidianControllerFor3DCompanion) companion)
					.getSingleIntersectionPointFrom2D(a, b, false);

			// Log.debug("\npoint="+point+"\nmouse=\n"+project);
		}

		// line/line, line/plane, line/conic, line/quadric
		else if (a.isGeoLine()) {
			if (b.isGeoLine()) {
				singleIntersectionPoint = (GeoPoint3D) getKernel()
						.getManager3D()
						.Intersect(null, (GeoLineND) a, (GeoLineND) b);
			} else if (b.isGeoConic()) {
				Coords picked = view3D.getPickPoint(mouseLoc);
				singleIntersectionPoint = getKernel().getManager3D()
						.IntersectLineConicSingle(null, (GeoLineND) a,
								(GeoConicND) b, picked.getX(), picked.getY(),
								view3D.getToScreenMatrix());
			} else if (b instanceof GeoCoordSys2D) {
				singleIntersectionPoint = (GeoPoint3D) getKernel()
						.getManager3D()
						.Intersect(null, (GeoLineND) a, (GeoCoordSys2D) b,
								false);
			} else if (b instanceof GeoQuadric3D) {
				Coords picked = view3D.getPickPoint(mouseLoc);
				singleIntersectionPoint = getKernel().getManager3D().IntersectLineQuadricSingle(
						null, (GeoLineND) a, (GeoQuadric3D) b, picked.getX(),
						picked.getY(), view3D.getToScreenMatrix());
			}
		}

		// plane/line, conic/line, quadric/line
		else if (b.isGeoLine()) {
			if (a.isGeoConic()) {
				Coords picked = view3D.getPickPoint(mouseLoc);
				singleIntersectionPoint = getKernel().getManager3D()
						.IntersectLineConicSingle(null, (GeoLineND) b,
								(GeoConicND) a, picked.getX(), picked.getY(),
								view3D.getToScreenMatrix());
			} else if (a instanceof GeoCoordSys2D) {
				singleIntersectionPoint = (GeoPoint3D) getKernel()
						.getManager3D()
						.Intersect(null, (GeoLineND) b, (GeoCoordSys2D) a,
								true);
			} else if (a instanceof GeoQuadric3D) {
				Coords picked = view3D.getPickPoint(mouseLoc);
				singleIntersectionPoint = getKernel().getManager3D().IntersectLineQuadricSingle(
						null, (GeoLineND) b, (GeoQuadric3D) a, picked.getX(),
						picked.getY(), view3D.getToScreenMatrix());
			}
		}

		// conic/conic
		else if (a.isGeoConic() && b.isGeoConic()) {

			Coords picked = view3D.getPickPoint(mouseLoc);
			singleIntersectionPoint = getKernel().getManager3D().IntersectConicsSingle(null,
					(GeoConicND) a, (GeoConicND) b, picked.getX(),
					picked.getY(), view3D.getToScreenMatrix());

		}

		// TODO: conic/plane, conic/quadric

		kernel.setSilentMode(oldSilentMode);

		// Application.debug("point is defined : "+point.isDefined());

		if (singleIntersectionPoint == null)
			return null;
		
		if (singleIntersectionPoint.isDefined()) {
			if (((GeoElement) singleIntersectionPoint).isGeoElement3D()) {
				// if the resulting point is defined, but is not around the
				// mouse, discard it. (2011/8/8 Tam)
				Coords picked = view3D.getPickPoint(mouseLoc);
				Coords toScreenCoords = view3D
						.projectOnScreen(((GeoPoint3D) singleIntersectionPoint).getCoords()
								.getCoordsLast1());

				// Log.debug("\nmouse="+mouseLoc.x+","+mouseLoc.y+"\npicked=\n"+picked+"\ncoords\n"+toScreenCoords);
				// Log.debug("X: "+Math.abs(picked.getX() -
				// toScreenCoords.getX()) + "\n" +
				// "Y: "+Math.abs(picked.getY() - toScreenCoords.getY()));

				if (Math.abs(picked.getX() - toScreenCoords.getX()) > 15
						|| Math.abs(picked.getY() - toScreenCoords.getY()) > 15) {
					return null;
				}
			}

			view3D.setIntersectionThickness(a, b);

			// Application.printStacktrace("\npoint="+point);

			singleIntersectionPoint.setCartesian3D();
			singleIntersectionPoint.update();

			return singleIntersectionPoint;
		}

		return null;

	}

	// /////////////////////////////////////
	// creating new objects

	/**
	 * return selected points as 3D points
	 * 
	 * @return selected points
	 */
	final protected GeoPoint3D[] getSelectedPoints3D() {

		GeoPoint3D[] ret = new GeoPoint3D[getSelectedPointList().size()];
		getSelectedPointsND(ret);

		// Application.printStacktrace("");

		return ret;
	}

	/**
	 * @return selected 3D lines
	 */
	final protected GeoCoordSys1D[] getSelectedLines3D() {
		GeoCoordSys1D[] lines = new GeoCoordSys1D[getSelectedLineList().size()];
		getSelectedLinesND(lines);

		return lines;
	}

	// build polygon
	/*
	 * protected void polygon(){ //check if there is a 3D point GeoPointND[]
	 * points = getSelectedPointsND();
	 * 
	 * boolean point3D = false; for (int i=0; i<points.length && !point3D; i++)
	 * point3D = point3D || ((GeoElement) points[i]).isGeoElement3D(); if
	 * (point3D) kernel.getManager3D().Polygon3D(null, points); else
	 * kernel.Polygon(null, getSelectedPointsND()); }
	 */

	protected void circleOrSphere(GeoNumberValue num) {
		GeoPointND[] points = getSelectedPointsND();

		getKernel().getManager3D().Sphere(null, points[0], num);
	}

	/**
	 * get center point and number
	 * 
	 * @param hits
	 * @return true if sphere created
	 */
	final protected boolean spherePointRadius(Hits hits, boolean selPreview) {
		if (hits.isEmpty())
			return false;

		addSelectedPoint(hits, 1, false, selPreview);

		// we got the center point
		if (selPoints() == 1) {
			getDialogManager().showNumberInputDialogSpherePointRadius(
					app.getLocalization().getMenu(
							EuclidianConstants.getModeText(mode)),
					getSelectedPointsND()[0], this);
			return true;
		}
		return false;
	}

	/**
	 * get center point and number
	 * 
	 * @param hits
	 * @return true if cone created
	 */
	final protected boolean coneTwoPointsRadius(Hits hits, boolean selPreview) {
		if (hits.isEmpty())
			return false;

		addSelectedPoint(hits, 2, false, selPreview);

		// we got the center point
		if (selPoints() == 2) {
			GeoPointND[] points = getSelectedPointsND();
			getDialogManager().showNumberInputDialogConeTwoPointsRadius(
					app.getLocalization().getMenu(
							EuclidianConstants.getModeText(mode)),
					points[0],
					points[1],
					this);
			return true;
		}
		return false;
	}

	/**
	 * get center point and number
	 * 
	 * @param hits
	 * @return true if cylinder created
	 */
	final protected boolean cylinderTwoPointsRadius(Hits hits,
			boolean selPreview) {
		if (hits.isEmpty())
			return false;

		addSelectedPoint(hits, 2, false, selPreview);

		// we got the center point
		if (selPoints() == 2) {
			GeoPointND[] points = getSelectedPointsND();
			getDialogManager().showNumberInputDialogCylinderTwoPointsRadius(
					app.getLocalization().getMenu(
							EuclidianConstants.getModeText(mode)),
					points[0],
					points[1],
					this);
			return true;
		}
		return false;
	}

	/**
	 * get two points and eventually direction
	 * 
	 * @param hits
	 *            hits
	 * @param name
	 *            name of the solid
	 * @return true if solid created
	 */
	final protected GeoElement[] archimedeanSolid(Hits hits, Commands name,
			boolean selPreview) {
		if (hits.isEmpty())
			return null;

		if (addSelectedPoint(hits, 2, false, selPreview) == 0
				&& selPoints() == 0
				&& selDirections() == 0) {
			// select a plane only if no point is selected
			addSelectedCS2D(hits, 1, false, selPreview);
		}

		// we got the center point
		if (selPoints() == 2) {
			GeoPointND[] points = getSelectedPointsND();
			GeoDirectionND direction;
			if (selCS2D() == 1) {
				direction = getSelectedCS2D()[0];
				Coords v = direction.getDirectionInD3();
				if (v.dotproduct(view3D.getViewDirection()) > 0) { // reverse
																	// direction
					MyDouble a = new MyDouble(kernel);
					a.set(-1);
					GeoVector3D orientation = (GeoVector3D) (new AlgoUnitVector3D(
							kernel.getConstruction(), direction, true))
							.getVector();
					ExpressionNode en = new ExpressionNode(kernel, a,
							Operation.MULTIPLY, orientation);
					direction = new AlgoDependentVector3D(
							kernel.getConstruction(), en).getVector3D();
				}

				return new GeoElement[] { kernel.getManager3D()
						.ArchimedeanSolid(null, points[0], points[1],
								direction, name)[0] };
			}

			return new GeoElement[] { kernel.getManager3D().ArchimedeanSolid(
					null, points[0], points[1], name)[0] };

		}
		return null;
	}

	/**
	 * 
	 * @param hits
	 *            geos hitted
	 * @return net of a polyhedron
	 */
	final protected GeoElement[] polyhedronNet(Hits hits, boolean selPreview) {
		if (hits.isEmpty())
			return null;

		addSelectedGeo(hits.getPolyhedronsIncludingMetaHits(), 1, false,
				selPreview);

		if (selGeos() == 1) {
			GeoElement polyhedron = getSelectedGeos()[0];
			GeoNumeric slider = GeoNumeric.setSliderFromDefault(new GeoNumeric(
					kernel.getConstruction()), false);
			slider.setIntervalMin(0);
			slider.setIntervalMax(1);
			slider.setAnimationStep(0.01);
			slider.setLabel(null);
			slider.setValue(1);
			// slider.setSliderLocation(x, y, true);
			slider.update();

			return new GeoElement[] { kernel.getManager3D().PolyhedronNet(null,
					polyhedron, slider, null, null)[0] // no bottom face, no
														// pivot segments
			};
		}

		return null;

	}

	/**
	 * get point and line or vector; // create plane through point orthogonal to
	 * line or vector
	 * 
	 * @param hits
	 * @return orthogonal plane
	 */
	final protected GeoElement[] orthogonalPlane(Hits hits,
			boolean selPreview) {
		if (hits.isEmpty())
			return null;

		boolean hitPoint = (addSelectedPoint(hits, 1, false, selPreview) != 0);
		if (!hitPoint) {
			if (selLines() == 0) {
				addSelectedVector(hits, 1, false, selPreview);
			}
			if (selVectors() == 0) {
				addSelectedLine(hits, 1, false, selPreview);
			}
		}

		if (selPoints() == 1) {
			if (selVectors() == 1) {
				// fetch selected point and vector
				GeoPointND[] points = getSelectedPointsND();
				GeoVectorND[] vectors = getSelectedVectorsND();
				// create new plane
				GeoElement[] ret = { null };
				ret[0] = (GeoPlane3D) getKernel().getManager3D()
						.OrthogonalPlane3D(null, points[0], vectors[0]);
				return ret;

			} else if (selLines() == 1) {
				// fetch selected point and line
				GeoPointND[] points = getSelectedPointsND();
				GeoLineND[] lines = getSelectedLinesND();
				// create new plane
				GeoElement[] ret = { null };
				ret[0] = (GeoPlane3D) getKernel().getManager3D()
						.OrthogonalPlane3D(null, points[0], lines[0]);
				return ret;
			}
		}
		return null;
	}

	/**
	 * get axis and point create circle with axis and through the point
	 * 
	 * @param hits
	 *            hits
	 * @param selPreview
	 *            whether this is just for preview
	 * @return circle created
	 * 
	 */
	final protected GeoElement[] circleAxisPoint(Hits hits,
			boolean selPreview) {
		if (hits.isEmpty())
			return null;

		if (addSelectedPoint(hits, 1, false, selPreview) == 0) { // add line
																	// only if
																	// no
														// point to avoid dummy
														// circle
			addSelectedLine(hits, 1, false, selPreview);
		}

		if (selPoints() == 1 && selLines() == 1) {
			return new GeoElement[] { getKernel().getManager3D().Circle3D(null,
					getSelectedLinesND()[0], getSelectedPointsND()[0]) };

		}

		return null;

	}

	/**
	 * get point, direction, enter radius create circle with center, radius,
	 * axis parallel to direction
	 * 
	 * @param hits
	 * @return true if circle created
	 * 
	 */
	final protected boolean circlePointRadiusDirection(Hits hits,
			boolean selPreview) {
		if (hits.isEmpty())
			return false;

		if (addSelectedPoint(hits, 1, false, selPreview) == 0)
			addSelectedDirection(hits, 1, false, selPreview);

		if (selPoints() == 1 && selDirections() == 1) {
			app.getDialogManager()
					.showNumberInputDialogCirclePointDirectionRadius(
							app.getLocalization().getMenu(
									EuclidianConstants.getModeText(mode)),
							getSelectedPointsND()[0],
							getSelectedDirections()[0],
							this);

			return true;

		}

		return false;

	}

	private TextDispatcher3D textDispatcher;

	@Override
	protected TextDispatcher3D getTextDispatcher() {
		if (textDispatcher == null) {
			textDispatcher = new TextDispatcher3D(kernel, view3D);
		}
		return textDispatcher;
	}

	/**
	 * 
	 * @param hits
	 *            geos hitted
	 * @return volume of a geo (from hits) that has a volume
	 */
	final protected boolean volume(Hits hits, boolean selPreview) {
		if (hits.isEmpty())
			return false;

		addSelectedGeo(hits.getFiniteVolumeIncludingMetaHits(), 1, false,
				selPreview);

		if (selGeos() == 1) {
			GeoElement hasVolume = getSelectedGeos()[0];
			getTextDispatcher().createVolumeText(hasVolume, mouseLoc);

			return true;

		}

		return false;

	}

	/**
	 * create plane containing polygon / 2 lines / line & point / 3 points
	 * 
	 * @param hits0
	 *            hits
	 * @return true if a plane has been created
	 */
	final protected GeoElement[] planeContaining(Hits hits0,
			boolean selPreview) {

		// keep only one type between points/lines/2D coord sys
		Hits hits = hits0.keepFirsts(Test.GEOPOINTND, Test.GEOLINEND,
				Test.GEOCOORDSYS2DNOTPLANE);

		// Log.debug("\n=================\n"+hits0+"\n====\n"+hits+"\n=================\n");

		if (hits.isEmpty())
			return null;

		// first try with polygon, conic, etc.
		if (selPoints() == 0 && selLines() == 0) {
			addSelectedCS2D(hits, 1, false, selPreview);
		}

		if (selCS2D() == 1) {
			GeoCoordSys[] cs = getSelectedCS2D();
			GeoElement[] ret = new GeoElement[] { (GeoElement) getKernel()
					.getManager3D().Plane3D(null, (GeoCoordSys2D) cs[0]) };
			return ret;
		}

		// then try with points
		addSelectedPoint(hits, 3, false, selPreview);

		if (selPoints() == 3) { // 3 points
			GeoPointND[] points = getSelectedPointsND();
			GeoElement[] ret = new GeoElement[] { getKernel().getManager3D()
					.Plane3D(null, points[0], points[1], points[2]) };
			return ret;

		} else if (selPoints() == 1) { // try point & line
			// only one line allowed
			addSelectedLine(hits, 1, false, selPreview);
			if (selLines() == 1) {
				// fetch selected point and line
				GeoPointND[] points = getSelectedPointsND();
				GeoLineND[] lines = getSelectedLinesND();
				// create new plane
				GeoElement[] ret = new GeoElement[] { (GeoElement) getKernel()
						.getManager3D().Plane3D(null, points[0], lines[0]) };
				return ret;
			}

		} else if (selPoints() == 0) { // maybe two lines
			addSelectedLine(hits, 2, false, selPreview);
			if (selLines() == 2) {
				// plane containing two lines
				GeoLineND[] lines = getSelectedLinesND();
				GeoElement[] ret = new GeoElement[] { getKernel()
						.getManager3D().Plane3D(null, lines[0], lines[1]) };
				return ret;
			}
		}

		return null;

	}

	/**
	 * process view in front of mode
	 * 
	 * @param hits
	 * @return false (kernel won't change)
	 */
	final protected boolean viewInFrontOf(Hits hits, boolean selPreview) {
		if (hits.isEmpty())
			return false;

		// Application.debug(hits);

		addSelectedGeo(hits.getTopHits(), 1, false, selPreview);// TODO
																// hits.getTopHits()
																// ?

		if (selGeos() == 1) { // clear selection
			GeoElement geo = getSelectedGeos()[0];

			// GeoElement geo = (GeoElement) hits.get(0);
			// Application.debug(view3D.hasMouse());
			Coords vn = geo.getMainDirection();
			if (vn != null) {
				if (view3D.hasMouse())
					view3D.setRotAnimation(view3D.getCursorNormal());
				else {// doesn't come from 3D view
					view3D.setClosestRotAnimation(vn, true);
				}
			}
		}

		return false;
	}

	/**
	 * get point and plane; create line through point parallel to plane
	 * 
	 * @param hits
	 * @return plane created
	 */
	final protected GeoElement[] parallelPlane(Hits hits, boolean selPreview) {

		// Application.debug(hits.toString());

		if (hits.isEmpty())
			return null;

		boolean hitPoint = (addSelectedPoint(hits, 1, false, selPreview) != 0);
		if (!hitPoint) {
			addSelectedCS2D(hits, 1, false, selPreview);
		}

		if (selPoints() == 1) {
			if (selCS2D() == 1) {
				// fetch selected point and vector
				GeoPointND[] points = getSelectedPointsND();
				GeoCoordSys[] cs = getSelectedCS2D();// TODO
				// create new plane
				return new GeoElement[] { (GeoElement) getKernel()
						.getManager3D().Plane3D(null, points[0],
								(GeoCoordSys2D) cs[0]) };
			}
		}

		return null;
	}

	private boolean dialogOccurred = false;

	@Override
	public void setDialogOccurred() {
		dialogOccurred = true;
	}

	/**
	 * get basis and height; create prism/cylinder
	 * 
	 * @param hits
	 * @return prism created
	 */
	final protected GeoElement[] extrusionOrConify(Hits hits,
			boolean selPreview) {

		if (dialogOccurred) {
			dialogOccurred = false;
			return null;
		}

		if (!hits.isEmpty()) { // hits may be empty at the end of using the tool

			// we don't need to replace or de-select a polygon, since
			// we'll open immediately a dialog
			int basisAdded = selPolygons() + selConics();

			if (basisAdded == 0) { // if no basis for now, try to add polygon
				basisAdded += addSelectedPolygon(hits, 1, false, selPreview);
				if (basisAdded == 0) { // try to add conic
					basisAdded += addSelectedConic(hits, 1, false, selPreview);
					if (basisAdded == 0) { // if polygon/conic has been added,
											// the height
						// will be entered through dialog manager
						addSelectedNumberValue(hits, 1, false, selPreview);
					}
				}
			}

		}

		if (selNumberValues() == 1) {
			if (selPolygons() == 1) {
				GeoPolygon[] basis = getSelectedPolygons();
				GeoNumberValue[] height = getSelectedNumberValues();
				if (mode == EuclidianConstants.MODE_EXTRUSION) {
					return new GeoElement[] { // return only the prism
					getKernel().getManager3D().Prism(null, basis[0], height[0])[0] };
				}

				return new GeoElement[] { // return only the pyramid
				getKernel().getManager3D().Pyramid(null, basis[0], height[0])[0] };

			} else if (selConics() == 1) {
				GeoConicND[] basis = getSelectedConicsND();
				GeoNumberValue[] height = getSelectedNumberValues();
				if (mode == EuclidianConstants.MODE_EXTRUSION) {
					return new GeoElement[] { // return only the cylinder
					getKernel().getManager3D().CylinderLimited(null, basis[0],
							height[0])[0] };
				}
				return new GeoElement[] { // return only the cone
				getKernel().getManager3D().ConeLimited(null, basis[0],
						height[0])[0] };
			}
		}
		return null;
	}


	@Override
	protected boolean draggingOccurredBeforeRelease(boolean notAlreadyStarted) {
		if (notAlreadyStarted && lastGetNewPointWasExistingPoint
				&& draggingOccurredBeforeRelease) {
			// don't select a first point on dragging
			return true;
		}
		return super.draggingOccurredBeforeRelease(notAlreadyStarted);
	}

	private GeoPointND[] pyramidBasis = null;

	private boolean polygonForPyramidBasis = false;


	/**
	 * get basis and top point; create pyramid
	 * 
	 * @param hits
	 *            hits
	 * @return pyramid/prism created
	 */
	final protected GeoElement[] pyramidOrPrism(Hits hits, boolean selPreview) {

		// if (pyramidBasis!=null) Application.debug(pyramidBasis.length);
		polygonForPyramidBasis = false;

		if (hits.isEmpty())
			return null;

		if (draggingOccurredBeforeRelease((pyramidBasis == null)
				&& selPoints() == 0 && selPolygons() == 0)) {
			// don't select a first point on dragging
			return null;
		}

		if (pyramidBasis == null) { // try to find/create a polygon

			if (selPolygons() == 0) { // try to create a polygon
				// if the first point is clicked again, we create a polygon
				if (selPoints() > 2) {
					// check if first point was clicked again
					boolean finished = !selPreview
							&& hits.contains(getSelectedPointList().get(0));
					if (finished) {
						// store basis
						((DrawPolyhedron3D) view3D.getPreviewDrawable())
								.previewBasisIsFinished();
						pyramidBasis = getSelectedPointsND();
						// cancel last switch of point move mode
						cancelSwitchPointMoveModeIfNeeded();
						return null;
					}
				}

				if (addSelectedPoint(hits, GeoPolygon.POLYGON_MAX_POINTS, false,
						selPreview) != 0
						|| (!selPreview && !getSelectedPointList().isEmpty()
								&& hits
								.contains(getSelectedPointList().get(0)))) {
					return null; // add/remove point : don't check polygon
				}
			}

			boolean selectionOccured = false;

			if (selPoints() < 2) { // already two points : not a polygon for
									// basis
				if (addSelectedPolygon(hits, 1, false, selPreview) == 1) {
					polygonForPyramidBasis = true;
					selectionOccured = true;
				}
			}

			// there is 1 polygon, look for top point
			if (!selectionOccured) {
				addSelectedPoint(hits, 1, false, selPreview);
			}

			if (selPoints() == 1 && selPolygons() == 1) {
				// fetch selected point and vector
				GeoPolygon[] basis = getSelectedPolygons();
				GeoPointND[] points = getSelectedPointsND();
				// create new pyramid or prism
				view3D.disposePreview();
				switch (mode) {
				case EuclidianConstants.MODE_PYRAMID:
					return new GeoElement[] { getKernel().getManager3D()
							.Pyramid(null, basis[0], points[0])[0] };
				case EuclidianConstants.MODE_PRISM:
					return new GeoElement[] { getKernel().getManager3D().Prism(
							null, basis[0], points[0])[0] };
				}
			}

		} else { // there are points for basis

			addSelectedPoint(hits, 1, false, selPreview);

			if (selPoints() == 1) {
				// fetch selected point and vector
				GeoPointND[] points = new GeoPointND[pyramidBasis.length + 1];
				for (int i = 0; i < pyramidBasis.length; i++)
					points[i] = pyramidBasis[i];
				points[pyramidBasis.length] = getSelectedPointsND()[0];
				// create new pyramid or prism
				view3D.disposePreview();
				switch (mode) {
				case EuclidianConstants.MODE_PYRAMID:
					pyramidBasis = null;
					return new GeoElement[] { getKernel().getManager3D()
							.Pyramid(null, points)[0] };
				case EuclidianConstants.MODE_PRISM:
					pyramidBasis = null;
					return new GeoElement[] { getKernel().getManager3D().Prism(
							null, points)[0] };
				}
			}

			return null;
		}

		return null;

	}

	// /////////////////////////////////////////
	// moved GeoElements

	@Override
	public GeoElement getMovedGeoPoint() {
		return (GeoElement) movedGeoPoint;
	}

	// /////////////////////////////////////////
	// mouse released

	@Override
	public void wrapMouseReleased(AbstractEvent e) {
		if (!draggingOccured && !app.isControlDown(e))
			view3D.switchMoveCursor();

		super.wrapMouseReleasedND(e, true);
	}

	@Override
	protected void processReleaseForMovedGeoPoint(boolean rightClick) {

		((EuclidianView3D) view).setPointDecorations(null);

		if (isModeForMovingPoint(mode)) {
			if (freePointJustCreated) {
				// avoid switch if the point is created by a click
				freePointJustCreated = false;
			} else {
				// switch the direction of move (xy or z) in case of left-click
				// if (!movedGeoPointDragged){
				if (!draggingOccured && !rightClick
						&& movedGeoPoint.isIndependent()) {
					if (mode == EuclidianConstants.MODE_MOVE
							&& !movedGeoPoint.isGeoElement3D()) {
						// 2D point will be replaced by 3D point (only for move
						// mode)
						GeoPoint replaceable = (GeoPoint) movedGeoPoint;

						// create new 3D point
						Construction cons = kernel.getConstruction();
						boolean oldMacroMode = cons.isSuppressLabelsActive();
						cons.setSuppressLabelCreation(true);
						GeoPoint3D newGeo = (GeoPoint3D) kernel.getManager3D()
								.Point3D(null, replaceable.getInhomX(),
										replaceable.getInhomY(), 0, false);
						cons.setSuppressLabelCreation(oldMacroMode);

						try {
							cons.replace(replaceable, newGeo);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							// update geo selected
							String newLabel = newGeo.isLabelSet() ? newGeo
									.getLabelSimple() : replaceable
									.getLabelSimple();
							GeoElement geo = kernel.lookupLabel(newLabel);
							setMovedGeoPoint(geo);

							// update hits
							Hits3D hits = view3D.getHits3D();
							hits.init();
							hits.add(geo);

							// update selection
							app.getSelectionManager().clearSelectedGeos(false,
									false);
							app.getSelectionManager().addSelectedGeo(geo, true,
									true);

						}

					}
					if (mode == EuclidianConstants.MODE_MOVE
							|| mode == EuclidianConstants.MODE_POINT) {
						switchPointMoveMode();
					} else {
						Hits hits = view3D.getHits();
						if (!hits.isEmpty() && hits.get(0) == movedGeoPoint) {
							switchPointMoveMode();
						}
					}
					((EuclidianView3D) view).getCursor3D().setMoveMode(
							movedGeoPoint.getMoveMode());
					((EuclidianView3D) view).setDefaultCursorWillBeHitCursor();
				}
			}
		}

		if (movedGeoPoint instanceof GeoPoint3D) {
			GeoPoint3D movedGeoPoint3D = (GeoPoint3D) movedGeoPoint;
			movedGeoPoint3D.setWillingCoordsUndefined();
			movedGeoPoint3D.setWillingDirectionUndefined();
		}

		super.processReleaseForMovedGeoPoint(rightClick);

	}

	private int pointMoveMode = GeoPointND.MOVE_MODE_XY;

	@Override
	protected void switchPointMoveMode() {
		if (pointMoveMode == GeoPointND.MOVE_MODE_XY) {
			pointMoveMode = GeoPointND.MOVE_MODE_Z;
		} else {
			pointMoveMode = GeoPointND.MOVE_MODE_XY;
		}

	}

	private void cancelSwitchPointMoveModeIfNeeded() {
		if (!draggingOccurredBeforeRelease && movedGeoPoint != null
				&& movedGeoPoint.isIndependent()) {
			switchPointMoveMode();
		}
	}

	private void initPointMoveMode() {
		if (mode == EuclidianConstants.MODE_MOVE) {
			pointMoveMode = GeoPointND.MOVE_MODE_XY;
		} else {
			pointMoveMode = GeoPointND.MOVE_MODE_Z;
		}
	}

	/**
	 * 
	 * @return current tool point move mode
	 */
	public int getPointMoveMode() {
		return pointMoveMode;
	}

	/**
	 * update mouse moved, 3D mouse values, etc.
	 */
	public void updateInput3D() {
		// no input 3D
	}

	// /////////////////////////////////////////
	// mouse moved

	protected boolean mouseMoved = false;
	protected AbstractEvent mouseEvent = null;

	@Override
	public void wrapMousePressed(AbstractEvent e) {
		mouseMoved = false;
		// mousePressed = true;

		// maybe called by twoTouchEnd()
		// we don't want a "screen translate and scale"
		// refresh after that
		view3D.stopScreenTranslateAndScale();

		super.wrapMousePressed(e);
	}

	@Override
	protected void processMouseMoved(AbstractEvent e) {

		view3D.setHits3D(mouseLoc);

		// for next mouse move process
		setMouseMovedEvent(e);
		mouseMoved = true;

		// needed for non-animated renderers
		view3D.repaintView();

	}

	/**
	 * store the mouse move event
	 * 
	 * @param e
	 *            event
	 */
	protected void setMouseMovedEvent(AbstractEvent e) {
		mouseEvent = e;
	}

	/**
	 * update mouse moved after picking
	 */
	public void update() {
		processMouseMoved();
	}

	/**
	 * tells to proceed mouseMoved() (for synchronization with 3D renderer)
	 */
	protected void processMouseMoved() {

		if (mouseMoved && view3D.hasMouse()) {

			// make sure new GeoPoint3Ds aren't counted as 3D objects for uses3D
			// flag in XML
			kernel.getConstruction().setIgnoringNewTypes(true);

			((EuclidianView3D) view).updateCursor3D();
			super.processMouseMoved(mouseEvent);

			kernel.getConstruction().setIgnoringNewTypes(false);

			mouseMoved = false;
		}
	}

	@Override
	protected void initNewMode(int newMode) {

		super.initNewMode(newMode);

	}

	@Override
	protected Previewable switchPreviewableForInitNewMode(int previewMode) {

		Previewable previewDrawable = null;

		// maybe set previously by MODE_INTERSECTION_CURVE
		hideIntersection = false;

		// Log.debug(mode);

		switch (previewMode) {

		case EuclidianConstants.MODE_SPHERE_TWO_POINTS:
			previewDrawable = view3D.createPreviewSphere(getSelectedPointList());
			break;

		case EuclidianConstants.MODE_EXTRUSION:
			previewDrawable = view3D.createPreviewExtrusion(getSelectedPolygonList(),
					getSelectedConicNDList());
			break;

		case EuclidianConstants.MODE_CONIFY:
			previewDrawable = view3D.createPreviewConify(getSelectedPolygonList(),
					getSelectedConicNDList());
			break;

		case EuclidianConstants.MODE_PYRAMID:
		case EuclidianConstants.MODE_PRISM:
			previewDrawable = view3D.createPreviewPyramidOrPrism(
					getSelectedPointList(), getSelectedPolygonList(),
					previewMode);
			break;

		case EuclidianConstants.MODE_INTERSECTION_CURVE: // line through two
															// points
			break;

		default:
			previewDrawable = super.switchPreviewableForInitNewMode(
					previewMode);
			break;
		}

		return previewDrawable;

	}

	// not only moveable hits are selected in move mode
	@Override
	protected boolean getSelectables(Hits hits, boolean selPreview) {
		Hits top = hits.getTopHits(1);

		super.getSelectables(top, selPreview);

		// display correctly oriented 3D cursor
		GeoPointND point = (GeoPointND) top.getFirstHit(Test.GEOPOINTND);
		if (point != null)
			view3D.updateCursor3D(hits);

		return false;
	}

	/*
	 * protected void mouseClickedMode(MouseEvent e, int mode){
	 * 
	 * 
	 * switch (mode) { case EuclidianView3D.MODE_VIEW_IN_FRONT_OF:
	 * //Application.debug("ici"); Hits hits = view.getHits().getTopHits();
	 * if(!hits.isEmpty()){ GeoElement geo = (GeoElement)
	 * view.getHits().getTopHits().get(0); Coords vn = geo.getMainDirection();
	 * if (vn!=null){
	 * view3D.setRotAnimation(view3D.getCursor3D().getDrawingMatrix().getVz());
	 * } }
	 * 
	 * break; default: super.mouseClickedMode(e,mode); } }
	 */


	@Override
	public void processModeLock() {
		// TODO
	}

	// /////////////////////////////////////////
	// EMPTY METHODS IN EuclidianController USED FOR EuclidianView3D

	@Override
	protected void processRightPressFor3D(AbstractEvent event) {

		if (viewHasHitsForMouseDragged()) {
			// maybe needed if geo hitted is not moveable
			processPressForRotate3D();
			return;
		}
		temporaryMode = true;
		oldMode = mode; // remember current mode
		view.setMode(EuclidianConstants.MODE_ROTATEVIEW);
		switchModeForMousePressed(event);
	}

	private void processPressForRotate3D() {

		if (view3D.isRotAnimated()) {
			view3D.stopAnimation();
			viewRotationOccured = true;
		}

		// remembers mouse location
		startLoc = mouseLoc;
		view.rememberOrigins();
		view.setCursor(EuclidianCursor.DEFAULT);

		timeOld = app.getMillisecondTime();
		xOld = startLoc.x;
		animatedRotSpeed = 0;
	}

	/**
	 * right-drag the mouse makes 3D rotation
	 * 
	 * @return true
	 */
	@Override
	protected boolean processRotate3DView() {

		double time = app.getMillisecondTime();
		int x = mouseLoc.x;
		double dx = x - xOld;
		animatedRotSpeed = dx / (time - timeOld);
		timeOld = time;
		// Log.debug("animatedRotSpeed=" + animatedRotSpeed + "\nxOld = " + xOld
		// + "\nx=" + x);
		xOld = x;
		view.setCoordSystemFromMouseMove(mouseLoc.x - startLoc.x, mouseLoc.y
				- startLoc.y, MOVE_ROTATE_VIEW);
		viewRotationOccured = true;
		view.repaintView();
		return true;
	}

	@Override
	protected boolean allowSelectionRectangle() {
		return false;
	}

	/**
	 * right-release the mouse makes stop 3D rotation
	 * 
	 * @return true if a rotation occured
	 */
	@Override
	protected boolean processReleaseForRotate3D(PointerEventType type) {

		if (temporaryMode) {
			view.setMode(oldMode, ModeSetter.EXIT_TEMPORARY_MODE);
			temporaryMode = false;
			if (dontClearSelection == false) {
				clearSelections();
			}
			dontClearSelection = false;
		}

		if (viewRotationOccured) {
			viewRotationOccured = false;
			setViewHits(type);
			// Application.debug("hits"+view.getHits().toString());
			((EuclidianView3D) view).updateCursor3D();

			view.setCursor(EuclidianCursor.HIT);
			app.storeUndoInfo();


			setRotContinueAnimation();

			// Application.debug("animatedRotSpeed="+animatedRotSpeed);

			return true;
		}

		return false;
	}

	protected void setRotContinueAnimation() {
		((EuclidianView3D) view).setRotContinueAnimation(
				app.getMillisecondTime() - timeOld, animatedRotSpeed);
	}


	// /////////////////////////////////////////
	// PROCESS MODE

	@Override
	protected boolean switchModeForProcessMode(Hits hits,
			boolean isControlDown, AsyncOperation<Boolean> callback,
			boolean selectionPreview) {

		boolean changedKernel = false;

		GeoElement[] ret = null;

		switch (mode) {
		case EuclidianConstants.MODE_INTERSECTION_CURVE:
			ret = intersectionCurve(hits, selectionPreview);
			if (ret != null) { // remove current intersection curve
				intersectionCurveList.remove(resultedIntersectionCurve);
				view3D.setPreview(null);
			}
			break;
		case EuclidianConstants.MODE_PLANE_THREE_POINTS:
			ret = threePoints(hits, mode, selectionPreview);
			break;
		case EuclidianConstants.MODE_PLANE:
			ret = planeContaining(hits, selectionPreview);
			break;

		case EuclidianConstants.MODE_ORTHOGONAL_PLANE:
			ret = orthogonalPlane(hits, selectionPreview);
			break;

		case EuclidianConstants.MODE_PARALLEL_PLANE:
			ret = parallelPlane(hits, selectionPreview);
			break;

		case EuclidianConstants.MODE_EXTRUSION:
		case EuclidianConstants.MODE_CONIFY:
			ret = extrusionOrConify(hits, selectionPreview);
			if (!view3D.getRenderer().useLogicalPicking() && ret != null) {
				// we need to init hits since if users immediately clicks,
				// it will still use the old basis
				view3D.getHits3D().init();
			}
			break;

		case EuclidianConstants.MODE_TETRAHEDRON:
			ret = archimedeanSolid(hits, Commands.Tetrahedron,
					selectionPreview);
			break;

		case EuclidianConstants.MODE_CUBE:
			ret = archimedeanSolid(hits, Commands.Cube, selectionPreview);
			break;

		case EuclidianConstants.MODE_PYRAMID:
		case EuclidianConstants.MODE_PRISM:
			ret = pyramidOrPrism(hits, selectionPreview);
			break;

		case EuclidianConstants.MODE_SPHERE_TWO_POINTS:
			ret = circleOrSphere2(hits, mode, selectionPreview);
			break;
		case EuclidianConstants.MODE_SPHERE_POINT_RADIUS:
			changedKernel = spherePointRadius(hits, selectionPreview);
			break;

		case EuclidianConstants.MODE_CONE_TWO_POINTS_RADIUS:
			changedKernel = coneTwoPointsRadius(hits, selectionPreview);
			break;

		case EuclidianConstants.MODE_CYLINDER_TWO_POINTS_RADIUS:
			changedKernel = cylinderTwoPointsRadius(hits, selectionPreview);
			break;

		case EuclidianConstants.MODE_NET:
			ret = polyhedronNet(hits, selectionPreview);
			break;

		case EuclidianConstants.MODE_VIEW_IN_FRONT_OF:
			changedKernel = viewInFrontOf(hits, selectionPreview);
			break;

		case EuclidianConstants.MODE_CIRCLE_AXIS_POINT:
			ret = circleAxisPoint(hits, selectionPreview);
			break;

		case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS_DIRECTION:
			changedKernel = circlePointRadiusDirection(hits, selectionPreview);
			break;

		case EuclidianConstants.MODE_MIRROR_AT_PLANE:
			ret = mirrorAtPlane(hits.getTopHits(), selectionPreview);
			break;

		case EuclidianConstants.MODE_ROTATE_AROUND_LINE:
			ret = rotateAroundLine(hits.getTopHits(), selectionPreview);
			break;

		case EuclidianConstants.MODE_VOLUME:
			changedKernel = volume(hits, selectionPreview);
			break;

		default:
			return super
			.switchModeForProcessMode(hits, isControlDown, callback,
					selectionPreview);
		}

		return endOfSwitchModeForProcessMode(ret, changedKernel
				|| (ret != null), callback, selectionPreview);

	}

	/**
	 * for some modes, polygons are not to be removed
	 * 
	 * @param hits
	 *            hits
	 */
	@Override
	protected void switchModeForRemovePolygons(Hits hits) {

		switch (mode) {
		case EuclidianConstants.MODE_PARALLEL_PLANE:
			hits.removePolygonsIfNotOnlyCS2D();
			break;
		case EuclidianConstants.MODE_TETRAHEDRON:
		case EuclidianConstants.MODE_CUBE:
		case EuclidianConstants.MODE_PYRAMID:
		case EuclidianConstants.MODE_PRISM:
		case EuclidianConstants.MODE_AREA:
		case EuclidianConstants.MODE_VOLUME:
		case EuclidianConstants.MODE_EXTRUSION:
		case EuclidianConstants.MODE_NET:
		case EuclidianConstants.MODE_CONIFY:
			hits.removeAllPolygonsButOne();
			break;
		case EuclidianConstants.MODE_INTERSECTION_CURVE:
		case EuclidianConstants.MODE_INTERSECT:
			break;
		case EuclidianConstants.MODE_PLANE:
			break;
		default:
			super.switchModeForRemovePolygons(hits);
		}
	}

	@Override
	protected GeoElement[] switchModeForThreePoints(int threePointsMode) {

		switch (threePointsMode) {
		case EuclidianConstants.MODE_PLANE_THREE_POINTS:
			GeoPointND[] points = getSelectedPointsND();
			GeoPlane3D ret0 = (GeoPlane3D) getKernel().getManager3D().Plane3D(
					null, points[0], points[1], points[2]);
			GeoElement[] ret = { ret0 };
			return ret;
		default:
			return super.switchModeForThreePoints(threePointsMode);

		}

	}

	@Override
	protected GeoElement[] switchModeForCircleOrSphere2(int sphereNDMode) {

		switch (sphereNDMode) {
		case EuclidianConstants.MODE_SPHERE_TWO_POINTS:
			GeoPointND[] points = getSelectedPointsND();
			GeoElement[] ret = { null };
			ret[0] = getKernel().getManager3D().Sphere(null, points[0],
					points[1]);
			return ret;
		default:
			return super.switchModeForCircleOrSphere2(sphereNDMode);
		}
	}

	// /////////////////////////////////////////
	// MOUSE PRESSED

	@Override
	protected void createNewPointForModePoint(Hits hits, boolean complex) {
		// super.createNewPointForModePoint(hits, false);
		createNewPoint(hits, true, true, true, true, false);
	}

	@Override
	protected void createNewPointForModeOther(Hits hits) {
		createNewPoint(hits, true, true, true, true, false);
	}

	@Override
	protected void switchModeForMousePressed(AbstractEvent e) {

		// needed to stop animated rotation
		processPressForRotate3D();

		Hits hits;
		PointerEventType type = e.getType();
		switch (mode) {
		case EuclidianConstants.MODE_PLANE_THREE_POINTS:
		case EuclidianConstants.MODE_SPHERE_TWO_POINTS:
		case EuclidianConstants.MODE_SPHERE_POINT_RADIUS:
		case EuclidianConstants.MODE_CONE_TWO_POINTS_RADIUS:
		case EuclidianConstants.MODE_CYLINDER_TWO_POINTS_RADIUS:
			setViewHits(type);
			hits = view.getHits();
			hits.removePolygons();
			createNewPoint(hits, true, true, true, true, false);
			break;

		case EuclidianConstants.MODE_ORTHOGONAL_PLANE:
			setViewHits(type);
			hits = view.getHits();
			hits.removePolygons();
			createNewPoint(hits, false, false, true);
			break;

		case EuclidianConstants.MODE_PLANE:
			setViewHits(type);
			hits = view.getHits();
			break;

		case EuclidianConstants.MODE_PARALLEL_PLANE:
			setViewHits(type);
			hits = view.getHits();
			hits.removePolygons();
			createNewPoint(hits, true, false, false, true, false);
			break;

		case EuclidianConstants.MODE_EXTRUSION:
		case EuclidianConstants.MODE_CONIFY:
			setViewHits(type);
			hits = view.getHits();
			hits.removeAllPlanes();
			switchModeForRemovePolygons(hits);
			// Application.debug(hits.toString());
			extrusionOrConify(hits, false);
			view3D.updatePreviewable();
			break;

		case EuclidianConstants.MODE_TETRAHEDRON:
		case EuclidianConstants.MODE_CUBE:
			setViewHits(type);
			hits = view.getHits();
			// hits.removePolygons();
			boolean createPointAnywhere = false;
			if (selCS2D() == 1 || selPoints() != 0) { // create point anywhere
														// when direction has
														// been selected
				createPointAnywhere = true;
			} else {
				if (view3D.getCursor3DType() == EuclidianView3D.PREVIEW_POINT_REGION) {
					if (view3D.getCursor3D().getRegion() == kernel
							.getXOYPlane()) {
						createPointAnywhere = true;
					}
				}
			}
			if (createPointAnywhere) {
				createNewPoint(hits, true, true, true, true, false);
			} else {
				createNewPoint(hits, true, false, true, true, false);
			}
			break;

		case EuclidianConstants.MODE_PYRAMID:
		case EuclidianConstants.MODE_PRISM:
			setViewHits(type);
			hits = view.getHits();
			if (selPolygons() == 1 || hits.getPolyCount() == 0) {
				createNewPoint(hits, true, true, true, true, false);
			} else {
				switchModeForRemovePolygons(hits);
				createNewPoint(hits, true, false, false, true, false);
			}
			break;

		case EuclidianConstants.MODE_ROTATEVIEW:
			moveMode = MOVE_ROTATE_VIEW;
			break;

		case EuclidianConstants.MODE_CIRCLE_AXIS_POINT:
		case EuclidianConstants.MODE_CIRCLE_POINT_RADIUS_DIRECTION:
			setViewHits(type);
			hits = view.getHits();
			hits.removePolygons();
			if (hits.size() == 0)
				createNewPoint(hits, false, true, true);
			break;
		case EuclidianConstants.MODE_INTERSECTION_CURVE:
			// no need to do anything for preview when mouse is pressed
			break;
		case EuclidianConstants.MODE_VOLUME:
			setViewHits(type);
			hits = view.getHits();
			break;
		case EuclidianConstants.MODE_NET:
			setViewHits(type);
			hits = view.getHits();
			break;
		default:
			super.switchModeForMousePressedND(e);
		}

	}

	// /////////////////////////////////////////
	// MOUSE RELEASED

	@Override
	protected boolean switchModeForMouseReleased(int releaseMode, Hits hits,
			boolean changedKernel, boolean control, PointerEventType type) {
		switch (releaseMode) {
		case EuclidianConstants.MODE_PARALLEL_PLANE:
			return true;
		case EuclidianConstants.MODE_EXTRUSION:
			((DrawExtrusionOrConify3D) view3D.getPreviewDrawable())
					.createPolyhedron();
			return true;

		case EuclidianConstants.MODE_CONIFY:
			((DrawExtrusionOrConify3D) view3D.getPreviewDrawable())
					.createPolyhedron();
			return true;

		case EuclidianConstants.MODE_PYRAMID:
		case EuclidianConstants.MODE_PRISM:
			return changedKernel;

		case EuclidianConstants.MODE_MIRROR_AT_PLANE:
		case EuclidianConstants.MODE_ROTATE_AROUND_LINE:
			return true;

		case EuclidianConstants.MODE_VIEW_IN_FRONT_OF:
			// Application.debug("hop");
			// TODO implement choose geo
			return true;
		default:
			return super.switchModeForMouseReleased(releaseMode, hits,
					changedKernel,
					control, type);

		}

	}

	@Override
	protected Hits addPointCreatedForMouseReleased(Hits hits) {

		hits.add((GeoElement) pointCreated);
		return hits;
	}

	@Override
	public void showDrawingPadPopup(GPoint mouseLoc1) {
		app.getGuiManager().showDrawingPadPopup3D(view, mouseLoc1);
	}

	// /////////////////////////////////////////
	// INTERSECTIONS

	// /////////////////////////////////////////
	// INTERSECTIONS

	/**
	 * get two objects (lines or conics) and create intersection point
	 */
	@Override
	protected GeoElement[] intersect(Hits hits0, boolean selPreview) {
		Hits hits = hits0;
		// AppD.debug(hits);
		if (hits.isEmpty())
			return null;
		if (hits.containsGeoPoint()) {
			hits.clear();
			return null;
		}
		hits.removePolygonsIfSidePresent();

		if (goodHits == null)
			goodHits = new Hits3D();
		else
			goodHits.clear();

		GeoPointND singlePoint = null;

		if (selGeos() == 0) { // either single intersection point or single
								// highlighting
			
			// we may have a dependent point found by 3D cursor
			if (view3D.getCursor3DType() == EuclidianView3D.PREVIEW_POINT_DEPENDENT){
				singlePoint = singleIntersectionPoint;
			}else{
				singlePoint = getSingleIntersectionPoint(hits);
			}

			// single intersection succeeds, select one geo from hits
			if (singlePoint != null) {

				hits.clear();
				hits.add(((GeoElement) singlePoint).getParentAlgorithm()
						.getInput()[0]);
				hits.add(((GeoElement) singlePoint).getParentAlgorithm()
						.getInput()[1]);

			} else {
				hits.getHits(new Test[] { Test.GEOLINEND, Test.GEOCOORDSYS2D,
						Test.GEOQUADRICND }, false, goodHits);

				hits = hits.getHits(1);
			}
		} else if (selGeos() == 1) {

			// Should have selGeos() == 1. Need to optimize the hits
			// see what we had selected

			if (selCS2D() == 1 || selQuadric() == 1) {// 2d geo: can only
														// intersect with 1d
														// ones.
				hits.getHits(
						new Test[] { Test.GEOCOORDSYS2D, Test.GEOQUADRIC3D },
						true, goodHits);

			} else { // 1d geo: can intersect with 1d or 2d geo
				hits.getHits(new Test[] { Test.GEOLINEND, Test.GEOCOORDSYS2D,
						Test.GEOQUADRICND, Test.GEOIMPLICITSURFACE }, false,
						goodHits);

				// does not have to test this. we will select only the top
				// element!
				// we can only have at most one polygon
				/*
				 * ((Hits3D)goodHits).removeAllPolygonsButOne(); if
				 * (!(goodHits.size()>=2) ||
				 * !((GeoElement)goodHits.get(0)).isGeoConic() ||
				 * !((GeoElement)goodHits.get(1)).isGeoConic())
				 * ((Hits3D)goodHits).removeAllPolygonsAndQuadricsButOne();
				 */

			}

			// remove incidence. TODO: test incidence by construction, instead
			// of numerically.
			GeoElement selected = getSelectedGeoList().get(0);
			if (selected.isGeoLine()) {
				while (goodHits.size() >= 1) {
					if (goodHits.get(0).isGeoPlane()
							&& AlgoIntersectCS1D2D.getConfigLinePlane(
									(GeoLineND) selected,
									((GeoCoordSys2D) goodHits.get(0))) == ConfigLinePlane.CONTAINED)
						goodHits.remove(0);
					else
						break;
				}
			} else if (selected.isGeoConic()) {
				while (goodHits.size() >= 1) {
					if (goodHits.get(0).isGeoPlane()
							&& AlgoIntersectPlanes.getConfigPlanePlane(
									(((GeoConicND) selected).getCoordSys()),
									(((GeoCoordSys2D) goodHits.get(0))
											.getCoordSys())) == AlgoIntersectPlanes.RESULTCATEGORY_CONTAINED)
						goodHits.remove(0);
					else
						break;
				}
			} else if (selected.isGeoPolygon()) {
				while (goodHits.size() >= 1) {
					if (goodHits.get(0) instanceof GeoCoordSys2D
							&& AlgoIntersectPlanes.getConfigPlanePlane(
									(((GeoPolygon) selected).getCoordSys()),
									(((GeoCoordSys2D) goodHits.get(0))
											.getCoordSys())) == AlgoIntersectPlanes.RESULTCATEGORY_CONTAINED)
						goodHits.remove(0);
					else
						break;
				}
			}

			if (goodHits.size() == 0) {
				// return immediately, so that the selected geo is not fetched
				return null;
			}

			// we focus on the selected geo and a new one from goodHits
			hits.clear();
			hits.add(selected);
			hits.add(goodHits.get(0));

		}

		addSelectedLine(hits, 10, true, selPreview);
		addSelectedConic(hits, 10, true, selPreview);
		addSelectedPlane(hits, 1, true, selPreview);
		addSelectedPolygon(hits, 1, true, selPreview);
		addSelectedQuadric(hits, 1, true, selPreview);
		addSelectedImplicitSurface(hits, 1, true, selPreview);

		if (selLines() >= 2) {// two lines
			GeoLineND[] lines = getSelectedLinesND();
			GeoPointND point = getAlgoDispatcher().IntersectLines(null,
					lines[0], lines[1]);
			checkCoordCartesian3D(point);
			return new GeoElement[] { (GeoElement) point };

		} else if (selLines() == 1) {
			if (selConics() >= 1) {// line-conic
				GeoLineND line = getSelectedLinesND()[0];
				GeoConicND conic = getSelectedConicsND()[0];
				GeoElement[] ret = new GeoElement[2];

				GeoPointND[] points = getAlgoDispatcher().IntersectLineConic(
						null, line, conic);
				for (int i = 0; i < 2; i++)
					ret[i] = (GeoElement) points[i];

				return ret;
			} else if (selQuadric() == 1) { // line-quadric3D
				GeoLineND line = getSelectedLinesND()[0];
				GeoQuadricND quadric = getSelectedQuadric()[0];
				GeoElement[] ret = new GeoElement[2];

				GeoPointND[] points = getKernel().getManager3D()
						.IntersectLineQuadric(null, line, quadric);
				for (int i = 0; i < 2; i++)
					ret[i] = (GeoElement) points[i];
				return ret;
			} else if (selPolygons() == 1) {// line-polygon
				return getKernel().getManager3D().IntersectionPoint(
						new String[] { null }, getSelectedLinesND()[0],
						getSelectedPolygons()[0]);

			} else if (selPlanes() == 1) {// line-plane
				GeoElement[] ret = new GeoElement[1];
				ret[0] = getKernel().getManager3D().Intersect(null,
						getSelectedLinesND()[0],
						getSelectedPlanes()[0], false);
				return ret;
			} else if (selImplicitSurfaces() == 1) {// line-plane

				return getKernel().getAlgoDispatcher()
						.IntersectImplicitSurfaceLine(null,
						getSelectedImplicitSurface()[0],
								getSelectedLinesND()[0]);

			}
		} else if (selConics() >= 2) {// conic-conic
			GeoConicND[] conics = getSelectedConicsND();
			GeoElement[] ret = new GeoElement[4];
			GeoPointND[] points = getKernel().getAlgoDispatcher()
					.IntersectConics(null, conics[0], conics[1]);
			for (int i = 0; i < points.length; i++) {
				checkCoordCartesian3D(points[i]);
				ret[i] = (GeoElement) points[i];
			}
			return ret;
		} else if (selConics() >= 1 && selPlanes() == 1) {

			GeoPlaneND plane = getSelectedPlanes()[0];
			GeoConicND conic = getSelectedConicsND()[0];

			GeoElement[] ret = new GeoElement[2];

			GeoPointND[] points = getKernel().getManager3D()
					.IntersectPlaneConic(null, plane, conic);
			for (int i = 0; i < 2; i++)
				ret[i] = (GeoElement) points[i];

			return ret;
		} else if (selConics() >= 1 && selQuadric() == 1) {

			GeoQuadricND quadric = getSelectedQuadric()[0];
			GeoConicND conic = getSelectedConicsND()[0];

			GeoElement[] ret = new GeoElement[2];

			GeoPointND[] points = getKernel().getManager3D().IntersectConics(
					null, conic, quadric);
			for (int i = 0; i < 2; i++)
				ret[i] = (GeoElement) points[i];

			return ret;
		} else if (selPolygons() == 1 && selPlanes() == 1) { // plane-polygon
			return getKernel().getManager3D().IntersectionPoint(null,
					getSelectedPlanes()[0], getSelectedPolygons()[0]);
		}

		return null;
	}

	/**
	 * ensure that the point will show 3D cartesion coords
	 * 
	 * @param point
	 *            point
	 */
	private static void checkCoordCartesian3D(GeoPointND point) {
		if (point.getMode() != Kernel.COORD_CARTESIAN_3D) {
			point.setCartesian3D();
			point.updateRepaint();
		}
	}

	public ArrayList<IntersectionCurve> getIntersectionCurves() {
		return intersectionCurveList;
	}

	protected boolean mouseMovedForIntersectionCurve() {
		return mouseMoved;
	}

	/**
	 * 
	 * @param hits
	 * @return true if a curve is created
	 */
	protected GeoElement[] intersectionCurve(Hits hits, boolean selPreview) {

		if (hits == null) {
			resultedGeo = null;
			return null;
		}

		if (hits.isEmpty()) {
			resultedGeo = null;
			return null;
		}

		// add selected geo into consideration
		// if (selectedGeos.size()==1 && !hits.contains(selectedGeos.get(0)))
		// hits.addAll(0, selectedGeos);

		if (mouseMovedForIntersectionCurve() && view3D.hasMouse2D()) { // process new intersection
													// only if mouse has moved
			for (int i = 0; i < intersectionCurveList.size(); ++i) {
				intersectionCurveList.get(i).hitted = false;
			}

			// Log.debug(hits);

			for (int i = 0; i < hits.size(); ++i) {
				for (int j = i + 1; j < hits.size(); ++j) {
					this.createIntersectionCurve(hits.get(i), hits.get(j));
				}
			}

			/*
			 * debug String s = ">>>> BEFORE PICKING"; for (Drawable3D
			 * d:intersectionCurves){
			 * s+="\n=== geo="+d.getGeoElement()+"\nzPickMin="
			 * +d.zPickMin+"\nzPickMax="+d.zPickMax; } Application.debug(s);
			 * //end debug
			 */

			// calls the renderer to pick the curves
			view3D.getRenderer().pickIntersectionCurves();

			/*
			 * debug s = "AFTER PICKING <<<<"; for (Drawable3D
			 * d:intersectionCurves){
			 * s+="\n=== geo="+d.getGeoElement()+"\nzPickMin="
			 * +d.zPickMin+"\nzPickMax="+d.zPickMax; } Application.debug(s);
			 * //end debug
			 */

			decideIntersection(hits);
		}

		if (goodHits != null) {
			addSelectedPolygon(goodHits, 1, false, selPreview);
			addSelectedPlane(goodHits, 2, true, selPreview);
			addSelectedQuadric(goodHits, 2, true, selPreview);
			addSelectedPolyhedron(goodHits, 1, false, selPreview);
			addSelectedQuadricLimited(goodHits, 1, false, selPreview);

		} else {
			Hits firstSurface = hits.getFirstSurfaceBefore(getSelectedGeoList());
			addSelectedPolygon(firstSurface, 1, false, selPreview);
			addSelectedPlane(firstSurface, 2, false, selPreview);
			addSelectedQuadric(firstSurface, 2, false, selPreview);
			addSelectedPolyhedron(firstSurface, 1, false, selPreview);
			addSelectedQuadricLimited(firstSurface, 1, false, selPreview);
			addSelectedFunction2Var(firstSurface, 1, false, selPreview);
			addSelectedImplicitSurface(firstSurface, 1, false, selPreview);
		}
		if (selPlanes() == 1) {

			if (selQuadric() >= 1) { // plane-quadric
				GeoPlaneND plane = getSelectedPlanes()[0];
				GeoQuadricND quad = getSelectedQuadric()[0];
				GeoElement[] ret = { kernel.getManager3D().Intersect(null,
						plane, quad) };
				if (ret[0].isDefined()) {
					return ret;
				}
				return null;

			} else if (selPolyhedron() == 1) { // plane-polyhedron
				GeoElement[] ret = getKernel().getManager3D().IntersectRegion(
						new String[] { null }, getSelectedPlanes()[0],
						getSelectedPolyhedron()[0].toGeoElement(), null);
				if (ret[0].isDefined()) {
					return ret;
				}
				return null;

			} else if (selQuadricLimited() == 1) { // plane-limited quadric
				GeoElement[] ret = new GeoElement[1];
				ret[0] = kernel.getManager3D().IntersectQuadricLimited(null,
						getSelectedPlanes()[0],
						(GeoQuadricND) getSelectedQuadricLimited()[0]);
				if (!ret[0].isDefined()) {
					return null;
				}
				// also compute corner points
				kernel.getManager3D().Corner(null, (GeoConicSection) ret[0]);

				return ret;

			} else if (selPolygons() == 1) { // plane-polygon
				GeoPlaneND plane = getSelectedPlanes()[0];
				GeoPolygon poly = getSelectedPolygons()[0];
				GeoElement[] ret = getKernel().getManager3D().IntersectPath(
						new String[] { null }, plane, poly);
				if (ret[0].isDefined()) {
					// create also intersect points
					getKernel().getManager3D().IntersectionPoint(
							new String[] { null }, plane, poly);
					return ret;
				}
				return null;

			} else if (selFunctionsNVar() == 1) { // plane-function NVar
				GeoPlaneND plane = getSelectedPlanes()[0];
				GeoFunctionNVar funNVar = getSelectedFunctionsNVar()[0];
				return getKernel().getManager3D().IntersectPlaneFunctionNVar(
						null, plane, funNVar);
			} else if (selImplicitSurfaces() == 1) { // plane-function NVar
				Log.debug(selImplicitSurfaces() + "," + selPlanes());

				GeoPlaneND plane = getSelectedPlanes()[0];
				GeoImplicitSurfaceND surface = getSelectedImplicitSurface()[0];
				GeoElement[] ret = getKernel().getManager3D()
						.IntersectPlaneImplicitSurface(plane, surface);
				ret[0].setLabel(null);
				return ret;
			}

		} else if (selQuadric() >= 2) { // quadric-quadric : intersection
										// circles
			GeoQuadricND[] quads = getSelectedQuadric();
			GeoElement[] ret = kernel.getManager3D().IntersectAsCircle(null,
					quads[0], quads[1]);
			if (ret[0].isDefined()) {
				return ret;
			}
			return null;

		} else if (selPlanes() >= 2) { // plane-plane
			GeoPlaneND[] planes = getSelectedPlanes();
			return new GeoElement[] { kernel.getManager3D().IntersectPlanes(
					null, planes[0], planes[1]) };
		}

		// //////////////////////////////////////

		return null;
	}

	public boolean createIntersectionCurve(GeoElement A, GeoElement B) {
		boolean intersectable = false;

		for (int i = 0; i < intersectionCurveList.size(); ++i) {
			IntersectionCurve intersection = intersectionCurveList.get(i);
			if (intersection.geo1 == getMetaIfJustOne(A)
					&& intersection.geo2 == getMetaIfJustOne(B)
					|| intersection.geo1 == getMetaIfJustOne(B)
					&& intersection.geo2 == getMetaIfJustOne(A)) {
				intersection.hitted = true;
				intersection.drawable.setWaitForUpdate();
				return true;
			}
		}

		/*
		 * TODO line/polygon preview if (A.isGeoLine() && B.isGeoPolygon()) {
		 * //add intersection to tempArrayList} else
		 */
		if (A.isGeoPlane() && B.isGeoPlane()) {
			// add intersection to tempArrayList
			// if intersection of A,B does not exist, create it
			GeoElement[] ret = new GeoElement[1];

			// tells the kernel not to record the algo
			boolean oldSilentMode = getKernel().isSilentMode();
			getKernel().setSilentMode(true);
			ret[0] = getKernel().getManager3D().IntersectPlanes(
					(GeoPlaneND) A, (GeoPlaneND) B);
			getKernel().setSilentMode(oldSilentMode);

			Drawable3D d = new DrawLine3D(view3D, (GeoLineND) ret[0]);
			processIntersectionCurve(A, B, ret[0], d);
			intersectable = true;

			// plane - polyhedron
		} else if (A.isGeoPlane() && B.isGeoPolygon()) {
			createIntersectionCurvePlanePolygon(A, (GeoPolygon) B);
		} else if (B.isGeoPlane() && A.isGeoPolygon()) {
			createIntersectionCurvePlanePolygon(B, (GeoPolygon) A);
		} else if (A.isGeoPlane() && B.isGeoPolyhedron()) {
			createIntersectionCurvePlanePolyhedron(A, (GeoPolyhedron) B);
		} else if (B.isGeoPlane() && A.isGeoPolyhedron()) {
			createIntersectionCurvePlanePolyhedron(B, (GeoPolyhedron) A);

			// plane-quadric
		} else if (A.isGeoPlane() && B instanceof GeoQuadric3D) {
			intersectable = createIntersectionCurvePlaneQuadric(A, B);
		} else if (B.isGeoPlane() && A instanceof GeoQuadric3D) {
			intersectable = createIntersectionCurvePlaneQuadric(B, A);

			// plane-quadric limited
		} else if (A.isGeoPlane() && B instanceof GeoQuadric3DLimited) {
			intersectable = createIntersectionCurvePlaneQuadricLimited(A, B);
		} else if (B.isGeoPlane() && A instanceof GeoQuadric3DLimited) {
			intersectable = createIntersectionCurvePlaneQuadricLimited(B, A);

			// quadric-quadric : intersection circles
		} else if (A instanceof GeoQuadricND && B instanceof GeoQuadricND) {
			// add intersection to tempArrayList
			boolean oldSilentMode = getKernel().isSilentMode();
			getKernel().setSilentMode(true);// tells the kernel not to record
											// the algo
			GeoElement ret = kernel.getManager3D().IntersectAsCircle(
					(GeoQuadricND) A, (GeoQuadricND) B)[0];
			Drawable3D d = new DrawConic3D(view3D, (GeoConic3D) ret);
			getKernel().setSilentMode(oldSilentMode);
			processIntersectionCurve(A, B, ret, d);
			intersectable = true;
		
		
			// // plane-quadric
			// } else if (A.isGeoPlane() && B instanceof GeoFunctionNVar) {
			// intersectable = createIntersectionCurvePlaneFunctionNVar(
			// (GeoPlane3D) A, (GeoFunctionNVar) B);
			// } else if (B.isGeoPlane() && A instanceof GeoFunctionNVar) {
			// intersectable = createIntersectionCurvePlaneFunctionNVar(
			// (GeoPlane3D) B, (GeoFunctionNVar) A);

		}

		return intersectable;

	}

	private boolean createIntersectionCurvePlanePolygon(GeoElement A,
			GeoPolygon B) {

		// check first if B is linked to polyhedron
		if (B.getMetasLength() == 1) {

			GeoElement polyhedron = B.getMetas()[0];

			if (!polyhedron.isGeoPolyhedron()) { // e.g. for a net
				return false;
			}

			createIntersectionCurvePlanePolyhedron(A,
					(GeoPolyhedron) polyhedron);

			return true;

		}

		// if B is linked to no (or more than one) polyhedron, create
		// intersection segment(s)
		boolean oldSilentMode = getKernel().isSilentMode();
		getKernel().setSilentMode(true);// tells the kernel not to record the
										// algo

		GeoElement[] ret = kernel.getManager3D().IntersectPath((GeoPlaneND) A,
				B);

		DrawIntersectionCurve3D drawSegments = new DrawIntersectionCurve3D(
				view3D, ret[0]);
		for (GeoElement geo : ret) {
			DrawSegment3D d = new DrawSegment3D(view3D, (GeoSegmentND) geo);
			drawSegments.add(d);
			processIntersectionCurve(A, B, geo, drawSegments);
		}

		getKernel().setSilentMode(oldSilentMode);

		return true;

	}

	private void createIntersectionCurvePlanePolyhedron(GeoElement A,
			GeoPolyhedron polyhedron) {

		boolean oldSilentMode = getKernel().isSilentMode();
		getKernel().setSilentMode(true);// tells the kernel not to record
										// the algo

		GeoElement[] ret = kernel.getManager3D().IntersectRegion(
				(GeoPlaneND) A, polyhedron);

		boolean goAhead = true;
		DrawIntersectionCurve3D drawPolygons = new DrawIntersectionCurve3D(
				view3D, ret[0]);
		for (int i = 0; i < ret.length && goAhead; i++) {
			GeoElement geo = ret[i];
			if (geo instanceof GeoPolygon3D) {
				DrawPolygon3D d = new DrawPolygon3D(view3D, (GeoPolygon3D) geo);
				drawPolygons.add(d);
				processIntersectionCurve(A, polyhedron, geo, drawPolygons);
			} else {
				goAhead = false;
			}
		}

		getKernel().setSilentMode(oldSilentMode);
	}

	private boolean createIntersectionCurvePlaneQuadric(GeoElement A,
			GeoElement B) {
		// add intersection to tempArrayList
		boolean oldSilentMode = getKernel().isSilentMode();
		getKernel().setSilentMode(true);// tells the kernel not to record the
										// algo
		GeoElement ret;
		Drawable3D d;
		GeoQuadricND quad;
		if (B instanceof GeoQuadric3DPart) {
			quad = (GeoQuadric3DLimited) ((GeoQuadric3DPart) B).getMetas()[0];
			if (quad != null) {
				ret = kernel.getManager3D().IntersectQuadricLimited(
						(GeoPlaneND) A, quad);
				d = new DrawConicSection3D(view3D, (GeoConicSection) ret);
			} else {
				quad = (GeoQuadricND) B;
				ret = kernel.getManager3D().Intersect((GeoPlaneND) A, quad);
				d = new DrawConic3D(view3D, (GeoConicND) ret);
			}
		} else {
			quad = (GeoQuadric3D) B;
			ret = kernel.getManager3D().Intersect((GeoPlaneND) A, quad);
			d = new DrawConic3D(view3D, (GeoConicND) ret);
		}
		getKernel().setSilentMode(oldSilentMode);
		processIntersectionCurve(A, quad, ret, d);
		return true;
	}

	private boolean createIntersectionCurvePlaneQuadricLimited(GeoElement A,
			GeoElement B) {
		// add intersection to tempArrayList
		boolean oldSilentMode = getKernel().isSilentMode();
		getKernel().setSilentMode(true);// tells the kernel not to record the
										// algo

		GeoElement ret = kernel.getManager3D().IntersectQuadricLimited(
				(GeoPlaneND) A, (GeoQuadric3DLimited) B);
		Drawable3D d = new DrawConicSection3D(view3D, (GeoConicSection) ret);

		getKernel().setSilentMode(oldSilentMode);
		processIntersectionCurve(A, B, ret, d);
		return true;
	}

	private void processIntersectionCurve(GeoElement A, GeoElement B,
			GeoElement intersection, Drawable3D d) {
		intersection.setLineThickness(3);
		intersection.setIsPickable(false);
		intersection.setObjColor(ConstructionDefaults3D.colIntersectionCurve);
		intersectionCurveList.add(new IntersectionCurve(A, B, intersection,
				true, d));
	}

	private IntersectionCurve resultedIntersectionCurve;

	private static GeoElement getMetaIfJustOne(GeoElement geo) {
		if (geo instanceof FromMeta) {
			if (geo.getMetasLength() == 1) {
				return ((FromMeta) geo).getMetas()[0];
			}
		}

		return geo;
	}

	private void decideIntersection(Hits hits) {

		resultedGeo = null;

		// find the nearest intersection curve (if exists)
		double zNear = Double.NEGATIVE_INFINITY;
		// App.error(""+intersectionCurveList.size());
		for (IntersectionCurve intersectionCurve : intersectionCurveList) {
			Drawable3D d = intersectionCurve.drawable;
			// Log.debug("\n"+d+"\ntype: "+d.getPickingType()+"\nz="+d.getZPickNear()+"\ngeo1:"+intersectionCurve.geo1+"\ngeo2:"+intersectionCurve.geo2);
			if (d.getZPickNear() > zNear) {
				resultedGeo = d.getGeoElement();
				resultedIntersectionCurve = intersectionCurve;
				zNear = d.getZPickNear();
			}
		}

		// Log.debug("\n\n==== INTER: "+resultedGeo+"\nz="+zNear+"\n\n");
		/*
		 * if (resultedIntersectionCurve != null)
		 * Log.debug("\ngeo1:"+resultedIntersectionCurve
		 * .geo1+"\ngeo2:"+resultedIntersectionCurve.geo2);
		 */

		if (resultedGeo == null) {
			hideIntersection = true;
			view3D.setPreview(null);
			goodHits = null;
			return;
		}

		// check if the intersection curve is visible
		int i = 0;
		boolean checking = true;
		while (checking && i < hits.size()) {
			// while(checking && i<existingDrawables.size()){

			/*
			 * Drawable3D d = existingDrawables.get(i); GeoElement geo =
			 * d.getGeoElement();
			 */
			GeoElement geo = hits.get(i);
			Drawable3D d = (Drawable3D) view3D.getDrawableND(geo);
			// Log.debug("\nhits("+i+"): "+geo+"\nd="+d);
			if (d != null) {
				// Log.debug("\nd.getZPickNear()="+d.getZPickNear()+"\nzNear="+zNear);
				if (d.getZPickNear() < zNear) {
					// all next drawables are behind the intersection curve
					checking = false;
				} else if (d.getZPickNear() > zNear + 1 // check if existing geo
														// is really over the
														// curve, with 1 pixel
														// tolerance
						&& (!geo.isRegion() || geo.getAlphaValue() > MAX_TRANSPARENT_ALPHA_VALUE)) {
					// only non-region or non-transparent surfaces can hide the
					// curve
					checking = false;
					resultedGeo = null;
					// Application.debug("=== d.zPickMin<z: "+geo+"\nz-d.zPickMin="+(z-d.zPickMin));
				}
			}
			i++;
		}

		if (resultedGeo == null) {
			hideIntersection = true;
			view3D.setPreview(null);
			return;
		}

		// Log.debug("resultedGeo:"+resultedGeo);

		// Log.debug(hits+"\nA="+A+"\nB="+B);

		// Application.debug(hits);
		// for (int j=0; j<hits.size(); ++j) {
		// System.out.print(((GeoElement)hits.get(j)).isPickable()? "pickable "
		// : "non-pickable");
		// System.out.println(((GeoElement)hits.get(j)).getObjectColor());
		// }

		if (hits.size() == 0) {
			hideIntersection = true;
			return;
		}

		if (goodHits == null)
			goodHits = new Hits3D();
		else
			goodHits.clear();

		// since resultedGeo!=null, hits contains at least two element.
		// if one of the two is not one of A or B, we say that
		// resultedGeo is blocked by unrelated geo,
		// and then we just keep the first hit as goodHit and hide the
		// intersection
		if (hits.size() < 2 // check first if there are at least 2 geos
				|| (!(getMetaIfJustOne(hits.get(0)) == resultedIntersectionCurve.geo1 && getMetaIfJustOne(hits
						.get(1)) == resultedIntersectionCurve.geo2) && !(getMetaIfJustOne(hits
						.get(0)) == resultedIntersectionCurve.geo2 && getMetaIfJustOne(hits
						.get(1)) == resultedIntersectionCurve.geo1))) {
			addToGoodHits(hits.get(0));
			hideIntersection = true;
			return;
		}

		// else, we show the intersection, and add A,B to highligtedgeos
		hideIntersection = false;

		addToGoodHits(hits.get(0));
		addToGoodHits(hits.get(1));

		view3D.setPreview((Previewable) resultedIntersectionCurve.drawable);
		// resultedGeo.setIsPickable(false);

	}

	final private void addToGoodHits(GeoElement geo) {
		goodHits.add(getMetaIfJustOne(geo));
	}

	// /////////////////////////////////////////
	// POINT CAPTURING

	@Override
	public void transformCoords() {
		// TODO point capturing
	}

	// /////////////////////////////////////////
	// PASTE PREVIEW

	@Override
	protected void updatePastePreviewPosition() {
		GeoPoint3D p = view3D.getCursor3D();
		if (translationVec3D == null) {
			translationVec3D = new Coords(3);
		}
		translationVec3D.setX(p.getInhomX() - getStartPointX());
		translationVec3D.setY(p.getInhomY() - getStartPointY());
		translationVec3D.setZ(p.getInhomZ() - getStartPointZ());
		setStartPointLocation(p.getInhomX(), p.getInhomY(), p.getInhomZ());
		if (tmpCoordsL3 == null) {
			tmpCoordsL3 = new Coords(3);
		}
		tmpCoordsL3.setX(p.getInhomX());
		tmpCoordsL3.setY(p.getInhomY());
		tmpCoordsL3.setZ(p.getInhomZ());
		GeoElement.moveObjects(pastePreviewSelected, translationVec3D,
				tmpCoordsL3, view3D.getViewDirection(), view3D);
	}

	protected double startPointZ;

	protected double getStartPointZ() {
		return startPointZ;
	}

	protected void setStartPointLocation(double x, double y, double z) {
		setStartPointLocation(x, y);
		startPointZ = z;
	}

	// /////////////////////////////////////////
	// SELECTIONS

	// /////////////////////////////////////////
	// selectedCS2D list, similar to selectedCS1D

	/** selected 2D coord sys */
	/** add hits to selectedCS2D
	 * @param hits hits
	 * @param max max number of hits to add
	 * @param addMoreThanOneAllowed if adding more than one is allowed
	 * @return TODO
	 */
	final protected int addSelectedCS2D(Hits hits, int max,
			boolean addMoreThanOneAllowed, boolean selPreview) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed,
				selection.getSelectedCS2DList(), Test.GEOCOORDSYS2D, selPreview);
	}

	/**
	 * return number of selected 2D coord sys
	 * 
	 * @return number of selected 2D coord sys
	 */
	final int selCS2D() {
		return selection.getSelectedCS2DList().size();
	}

	/**
	 * return selected 2D coord sys also clear all selected 2D coord sys.
	 * 
	 * @return selected 2D coord sys
	 */
	final protected GeoCoordSys[] getSelectedCS2D() {
		GeoCoordSys[] planes = new GeoCoordSys[selection.getSelectedCS2DList()
				.size()];
		int i = 0;
		Iterator<GeoCoordSys> it = selection.getSelectedCS2DList().iterator();
		while (it.hasNext()) {
			planes[i] = it.next();
			i++;
		}
		clearSelection(selection.getSelectedCS2DList());
		return planes;
	}

	/**
	 * add hits to selectedPlane
	 * 
	 * @param hits
	 *            hits
	 * @param max
	 *            max number of hits to add
	 * @param addMoreThanOneAllowed
	 *            if adding more than one is allowed
	 * @return TODO
	 */
	final protected int addSelectedPlane(Hits hits, int max,
			boolean addMoreThanOneAllowed, boolean selPreview) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed,
				selection.getSelectedPlaneList(), Test.GEOPLANEND, selPreview);
	}

	/**
	 * return number of selected planes
	 * 
	 * @return number of selected planes
	 */
	final int selPlanes() {
		return selection.getSelectedPlaneList().size();
	}

	final int selImplicitSurfaces() {
		return getSelectedImplicitSurfaceList().size();
	}

	/**
	 * return selected planes also clear all selected planes.
	 * 
	 * @return selected planes
	 */
	final protected GeoPlaneND[] getSelectedPlanes() {
		GeoPlaneND[] planes = new GeoPlane3D[selection.getSelectedPlaneList()
				.size()];
		int i = 0;
		Iterator<GeoPlaneND> it = selection.getSelectedPlaneList().iterator();
		while (it.hasNext()) {
			planes[i] = it.next();
			i++;
		}
		clearSelection(selection.getSelectedPlaneList());
		return planes;
	}

	// /for quadric


	final int selQuadric() {
		return selection.getSelectedQuadricList().size();
	}

	final protected int addSelectedQuadric(Hits hits, int max,
			boolean addMoreThanOneAllowed, boolean selPreview) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed,
				selection.getSelectedQuadricList(), Test.GEOQUADRIC3D, selPreview);
	}

	final protected GeoQuadricND[] getSelectedQuadric() {
		GeoQuadricND[] quads = new GeoQuadricND[selection.getSelectedQuadricList()
				.size()];
		int i = 0;
		Iterator<GeoQuadricND> it = selection.getSelectedQuadricList().iterator();
		while (it.hasNext()) {
			quads[i] = it.next();
			i++;
		}
		clearSelection(selection.getSelectedQuadricList());
		return quads;
	}

	// /for quadric

	final int selQuadricLimited() {
		return selection.getSelectedQuadricLimitedList().size();
	}

	final protected int addSelectedQuadricLimited(Hits hits, int max,
			boolean addMoreThanOneAllowed, boolean selPreview) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed,
				selection.getSelectedQuadricLimitedList(),
				Test.GEOQUADRIC3DLIMITED,
				selPreview);
	}

	final protected GeoQuadric3DLimitedInterface[] getSelectedQuadricLimited() {
		GeoQuadric3DLimitedInterface[] quads = new GeoQuadric3DLimitedInterface[selection
				.getSelectedQuadricLimitedList()
				.size()];
		int i = 0;
		Iterator<GeoQuadric3DLimitedInterface> it = selection
				.getSelectedQuadricLimitedList()
				.iterator();
		while (it.hasNext()) {
			quads[i] = it.next();
			i++;
		}
		clearSelection(selection.getSelectedQuadricLimitedList());
		return quads;
	}

	// /for polyhedrons

	final int selPolyhedron() {
		return selection.getSelectedPolyhedronList().size();
	}

	final protected int addSelectedPolyhedron(Hits hits, int max,
			boolean addMoreThanOneAllowed, boolean selPreview) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed,
				selection.getSelectedPolyhedronList(), Test.GEOPOLYHEDRON,
				selPreview);
	}

	final protected GeoPolyhedronInterface[] getSelectedPolyhedron() {
		GeoPolyhedronInterface[] polyh = new GeoPolyhedron[selection
				.getSelectedPolyhedronList()
				.size()];
		int i = 0;
		Iterator<GeoPolyhedronInterface> it = selection
				.getSelectedPolyhedronList().iterator();
		while (it.hasNext()) {
			polyh[i] = it.next();
			i++;
		}
		clearSelection(selection.getSelectedPolyhedronList());
		return polyh;
	}

	@Override
	protected GeoElement chooseGeo(ArrayList<GeoElement> geos,
			boolean includeFixed) {
		return chooseGeo(geos, includeFixed, false);
	}

	@Override
	protected GeoElement chooseGeo(ArrayList<GeoElement> geos,
			boolean includeFixed, boolean includeConstants) {

		// Application.printStacktrace(((Hits) geos).toString());

		if (!geos.isEmpty()) {
			// if the geo hitted is one of view3D's geos, then chooseGeo return
			// null
			if (!includeConstants && view3D.owns(geos.get(0)))
				return null;

			// doesn't use choosing dialog TODO use choosing dialog ?
			// return super.chooseGeo(geos, includeFixed);
			// return first element : ordering done in hits
			GeoElement geo = geos.get(0);
			if (!includeFixed && geo.isFixed())
				return null;

			return geo;
		}

		return null;
	}

	// /////////////////////////////////////////
	//

	/*
	 * public void mouseWheelMoved(MouseWheelEvent e) {
	 * 
	 * double r = e.getWheelRotation();
	 * 
	 * switch (moveMode) { case MOVE_VIEW: default:
	 * view3D.setMoveCursor();//setZoomCursor
	 * view3D.setScale(view3D.getXscale()+r*10); view3D.updateMatrix();
	 * view.setHits(mouseLoc); ((EuclidianView3D) view).updateCursor3D();
	 * view3D.setHitCursor(); //((Kernel3D) getKernel()).notifyRepaint();
	 * 
	 * break;
	 * 
	 * case MOVE_POINT: case MOVE_POINT_WHEEL: /* TODO //p = p + r*vn
	 * Ggb3DVector p1 = (Ggb3DVector)
	 * movedGeoPoint3D.getCoords().add(EuclidianView3D.vz.mul(-r*0.1));
	 * movedGeoPoint3D.setCoords(p1);
	 * 
	 * 
	 * 
	 * 
	 * 
	 * objSelected.updateCascade();
	 * 
	 * 
	 * movedGeoPoint3D.updateRepaint();//for highlighting in algebraView
	 * //kernel3D.notifyRepaint();
	 * 
	 * 
	 * break;
	 * 
	 * 
	 * }
	 * 
	 * 
	 * 
	 * 
	 * }
	 */

	// ////////////////////////////////////
	// SELECTED GEOS
	// ////////////////////////////////////

	// ///////////////////////////////////////////////////
	//
	// CURSOR
	//
	// ///////////////////////////////////////////////////

	private static boolean isModeForMovingPoint(int mode) {
		switch (mode) {
		case EuclidianConstants.MODE_MOVE:
		case EuclidianConstants.MODE_ATTACH_DETACH:
			return true;
		default:
			return isModeForCreatingPoint(mode);
		}
	}

	private static boolean isModeForCreatingPoint(int mode) {
		switch (mode) {
		case EuclidianConstants.MODE_POINT:
		case EuclidianConstants.MODE_POINT_ON_OBJECT:

		case EuclidianConstants.MODE_JOIN:
		case EuclidianConstants.MODE_SEGMENT:

		case EuclidianConstants.MODE_RAY:
		case EuclidianConstants.MODE_VECTOR:

		case EuclidianConstants.MODE_POLYGON:
		case EuclidianConstants.MODE_POLYLINE:
		case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
		case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
		case EuclidianConstants.MODE_PLANE_THREE_POINTS:
		case EuclidianConstants.MODE_SPHERE_TWO_POINTS:
		case EuclidianConstants.MODE_SPHERE_POINT_RADIUS:
		case EuclidianConstants.MODE_CONE_TWO_POINTS_RADIUS:
		case EuclidianConstants.MODE_CYLINDER_TWO_POINTS_RADIUS:

		case EuclidianConstants.MODE_PYRAMID:
		case EuclidianConstants.MODE_PRISM:
			return true;
		default:
			return false;
		}
	}

	/**
	 * @param cursorType
	 *            type of the cursor
	 * @return if the 3D cursor is visible for current mode
	 */
	public boolean cursor3DVisibleForCurrentMode(int cursorType) {

		if (cursorType == EuclidianView3D.PREVIEW_POINT_ALREADY) {
			// cross arrows for moving point
			switch (mode) {
			// modes in which the result could be a dependent point
			case EuclidianConstants.MODE_MOVE:
			case EuclidianConstants.MODE_POINT:
				return true;
			default:
				if (isModeForMovingPoint(mode)) {
					// can only move the last created point
					GeoElement movedPoint = getMovedGeoPoint();
					if (movedPoint == null) {
						return false;
					}
					Hits hits = view3D.getHits();
					if (!hits.isEmpty() && hits.get(0) == movedPoint) {
						return true;
					}
					return false;
				}
				return false;
			}
		} else if (cursorType == EuclidianView3D.PREVIEW_POINT_DEPENDENT) {
			switch (mode) {
			// modes in which the result could be a dependent point
			case EuclidianConstants.MODE_POINT:
			case EuclidianConstants.MODE_INTERSECT:
			case EuclidianConstants.MODE_JOIN:
			case EuclidianConstants.MODE_SEGMENT:
			case EuclidianConstants.MODE_RAY:
			case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
			case EuclidianConstants.MODE_SPHERE_POINT_RADIUS:
			case EuclidianConstants.MODE_SPHERE_TWO_POINTS:
			case EuclidianConstants.MODE_CONE_TWO_POINTS_RADIUS:
			case EuclidianConstants.MODE_CYLINDER_TWO_POINTS_RADIUS:
			case EuclidianConstants.MODE_TETRAHEDRON:
			case EuclidianConstants.MODE_CUBE:
			case EuclidianConstants.MODE_PYRAMID:
			case EuclidianConstants.MODE_PRISM:
				return true;

			default:
				return false;
			}
		} else {
			switch (mode) {
			// modes where point can be created on path/region

			case EuclidianConstants.MODE_POINT:
			case EuclidianConstants.MODE_POINT_ON_OBJECT:

			case EuclidianConstants.MODE_JOIN:
			case EuclidianConstants.MODE_SEGMENT:

			case EuclidianConstants.MODE_RAY:
			case EuclidianConstants.MODE_VECTOR:

			case EuclidianConstants.MODE_POLYGON:
			case EuclidianConstants.MODE_POLYLINE:
			case EuclidianConstants.MODE_CIRCLE_THREE_POINTS:
			case EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS:
			case EuclidianConstants.MODE_PLANE_THREE_POINTS:
			case EuclidianConstants.MODE_SPHERE_TWO_POINTS:
			case EuclidianConstants.MODE_SPHERE_POINT_RADIUS:
			case EuclidianConstants.MODE_CONE_TWO_POINTS_RADIUS:
			case EuclidianConstants.MODE_CYLINDER_TWO_POINTS_RADIUS:
			case EuclidianConstants.MODE_VIEW_IN_FRONT_OF:
				return true;

			case EuclidianConstants.MODE_VECTOR_FROM_POINT:
				Hits hits = view3D.getHits();
				if (!hits.isEmpty() && hits.get(0).isGeoVector()) {
					return false; // no cursor if on a vector
				}
				return true;

			case EuclidianConstants.MODE_PYRAMID:
			case EuclidianConstants.MODE_PRISM:
				return (selPolygons() == 1) || (!polygonForPyramidBasis);

			case EuclidianConstants.MODE_TETRAHEDRON:
			case EuclidianConstants.MODE_CUBE:
				// show cursor when direction has been selected
				if (selCS2D() == 1 || selPoints() != 0) {
					return true;
				}

				hits = view3D.getHits();
				if (hits.isEmpty()) {
					return true;
				}

				GeoPoint3D point = view3D.getCursor3D();
				if (point.hasPath()) {
					return true;
				}
				if (point.hasRegion()) {
					if (point.getRegion() == kernel.getXOYPlane()) {
						return true;
					}
				}

				return false;

			case EuclidianConstants.MODE_ANGLE:
				point = view3D.getCursor3D();
				if (point.hasPath()) {
					return false;
				}
				if (point.hasRegion()) {
					return false;
				}
				return true;

			default:
				return false;
			}

		}

	}

	// //////////////////////////////////////
	// HANDLING PARTS OF PREVIEWABLES
	// //////////////////////////////////////

	private GeoElement handledGeo;

	/**
	 * sets the geo as an handled geo (for previewables)
	 * 
	 * @param geo
	 */
	public void setHandledGeo(GeoElement geo) {
		handledGeo = geo;
		if (handledGeo == null)
			return;
		setStartPointLocation();
		handledGeo.recordChangeableCoordParentNumbers(view3D);
	}

	@Override
	protected boolean viewHasHitsForMouseDragged() {
		// Application.debug(moveMode);
		if (moveMode == MOVE_POINT
				&& view3D.getCursor3DType() == EuclidianView3D.PREVIEW_POINT_ALREADY) {
			return view.getHits().containsGeoPoint(); // if a point is under the
														// mouse, don't try to
														// find another hit
		}

		Hits hits = view.getHits();

		if (hits.isEmpty()) {
			return false;
		}

		if (hits.get(0) == kernel.getXOYPlane()) {
			return false;
		}

		return true;
	}

	@Override
	public void wrapMouseDragged(AbstractEvent event, boolean startCapture) {

		if (handledGeo != null) {
			setMouseLocation(event);
			event.release();
			updateTranslationVector();
			handledGeo
					.moveFromChangeableCoordParentNumbers(translationVec3D,
							startPoint3D, view3D.getViewDirection(), null,
							null, view3D);
			// view3D.updatePreviewable();
			kernel.notifyRepaint();
			return;
		}

		setMouseMovedEvent(event);

		wrapMouseDraggedND(event, startCapture);
	}

	// //////////////////////////////////////
	// MOVE OBJECTS
	// //////////////////////////////////////

	private Coords startPoint3D = new Coords(0, 0, 0, 1);
	private Coords startPoint3DxOy = new Coords(0, 0, 0, 1);

	protected Coords translationVec3D = new Coords(4);

	/**
	 * update translation vector
	 */
	protected void updateTranslationVector() {
		Coords point = view3D.getPickPoint(mouseLoc);
		view3D.toSceneCoords3D(point);
		updateTranslationVector(point);
	}

	/**
	 * update translation vector from start point to current mouse pos
	 * 
	 * @param point
	 *            current mouse pos
	 */
	protected void updateTranslationVector(Coords point) {
		point.sub(startPoint3D, translationVec3D);
	}

	@Override
	public void setStartPointLocation() {
		udpateStartPoint();

		super.setStartPointLocation();
	}

	/**
	 * update start point to current mouse coords
	 */
	protected void udpateStartPoint() {
		if (mouseLoc == null)// case that it's algebra view calling
			return;

		updateStartPoint(view3D.getPickPoint(mouseLoc));

	}

	/**
	 * update start point to p coords
	 * 
	 * @param p
	 *            coords
	 */
	final protected void updateStartPoint(Coords p) {

		startPoint3D.set(p);
		view3D.toSceneCoords3D(startPoint3D);

		// Log.debug("\n"+startPoint3D);

		// project on xOy
		startPoint3D.projectPlaneThruVIfPossible(CoordMatrix4x4.IDENTITY,
				view3D.getHittingDirection(), startPoint3DxOy);
	}

	@Override
	protected void setTranslateStart(GeoElement geo, GeoVectorND vec) {
		super.setTranslateStart(geo, vec);
		startPoint3D.set(view3D.getCursor3D().getInhomCoordsInD3());
		translationVec3D.set(vec.getCoordsInD3());
		if (geo.isGeoPlane()) {
			translateDirection = geo.getMainDirection();
		} else {
			translateDirection = null;
		}

	}

	private void setTranslateFromPointStart(GeoElement geo, GeoPointND point) {
		startPoint3D.set(view3D.getCursor3D().getInhomCoordsInD3());
		translationVec3D.setSub(point.getInhomCoordsInD3(), startPoint3D);
		if (geo.isGeoPlane()) {
			translateDirection = geo.getMainDirection();
			if (point.isGeoElement3D()) {
				((GeoPoint3D) point).setMoveMode(GeoPointND.MOVE_MODE_Z);
			}
		} else {
			translateDirection = null;
			if (point.isGeoElement3D()) {
				((GeoPoint3D) point)
						.setMoveMode(GeoPointND.MOVE_MODE_TOOL_DEFAULT);
			}
		}

	}

	/**
	 * 
	 * @return current normal translation direction
	 */
	public Coords getNormalTranslateDirection() {
		if (translateDirection == null) {
			return Coords.VZ;
		}

		return translateDirection;
	}

	@Override
	protected void moveVector() {

		Coords o = view3D.getPickPoint(mouseLoc);
		view3D.toSceneCoords3D(o);
		if (translateDirection == null) {
			o.projectPlaneThruVIfPossible(Coords.VX, Coords.VY, Coords.VZ,
					startPoint3D, view3D.getHittingDirection(), tmpCoords);
		} else {
			startPoint3D.projectNearLine(o, view3D.getHittingDirection(),
					translateDirection, tmpCoords);
		}

		GeoPointND P = movedGeoVector.getStartPoint();
		if (P == null) {
			tmpCoords.setSub(tmpCoords, startPoint3D);
		} else {
			tmpCoords.setSub(tmpCoords, P.getInhomCoordsInD3());
		}

		tmpCoords.setAdd(tmpCoords, translationVec3D);
		
		// snap to grid
		((EuclidianController3DCompanion) companion).checkPointCapturingXYThenZ(tmpCoords);

		if (movedGeoVector.isGeoElement3D()) {
			((GeoVector3D) movedGeoVector).setCoords(tmpCoords);
		} else {
			moveVector(tmpCoords.getX(), tmpCoords.getY());
		}

	}

	private Coords translateDirection;

	@Override
	public void setStartPointLocationWithOrigin(double x, double y) {
		udpateStartPoint();
		// sub origin
		startPoint3DxOy.setX(startPoint3DxOy.getX() - x);
		startPoint3DxOy.setY(startPoint3DxOy.getY() - y);

		super.setStartPointLocationWithOrigin(x, y);
	}

	@Override
	protected void calcRWcoords() {
		Coords point = view3D.getPickPoint(mouseLoc);
		view3D.toSceneCoords3D(point);
		xRW = point.getX();
		yRW = point.getY();
	}

	@Override
	protected void moveDependent(boolean repaint) {

		updateTranslationVector();
		Coords end = startPoint3D;
		if (translateableGeos.size() > 0
				&& translateableGeos.get(0) instanceof GeoPointND) {
			GeoPointND g3d = (GeoPointND) translateableGeos.get(0).copy();

			if (g3d.getMoveMode() == GeoPointND.MOVE_MODE_Z
					|| (g3d.getMoveMode() == GeoPointND.MOVE_MODE_TOOL_DEFAULT && this
							.getPointMoveMode() == GeoPointND.MOVE_MODE_Z)) { // moves
				((EuclidianController3DCompanion) companion)
						.moveAlongZAxis(g3d);

			} else {
				getCurrentPlane().set(g3d.getCoordsInD3(), 4);
				movePointOnCurrentPlane(g3d, false);

			}


			end = g3d.getInhomCoordsInD3();
		}

		GeoElement.moveObjects(translateableGeos, translationVec3D,
				end, view3D.getHittingDirection(), view3D);

		kernel.notifyRepaint();
	}

	@Override
	protected void handleMovedElementMultiple() {
		// TODO
	}

	@Override
	final protected void handleMovedElementFree(PointerEventType type) {
		if (handleMovedElementFreePoint()) {
			translateDirection = null;
			return;
		}

		if (handleMovedElementFreePlane()) {
			return;
		}

		handleMovedElementFreeText();
	}

	/**
	 * 
	 * @return true if there is a free plane to move
	 */
	protected boolean handleMovedElementFreePlane() {
		return false;
	}

	@Override
	final protected void handleMovedElementDependent() {

		if (movedGeoElement.isTranslateable()) {
			AlgoElement algo = movedGeoElement.getParentAlgorithm();
			if (algo instanceof AlgoTranslate) {
				GeoElement[] input = algo.getInput();
				GeoElement in = input[1];
				if (in instanceof GeoVectorND) {
					if (in.isIndependent()) {
						movedGeoVector = (GeoVectorND) input[1];
						moveMode = MOVE_VECTOR_NO_GRID;
						setTranslateStart(movedGeoElement, movedGeoVector);
					} else if (in.getParentAlgorithm() instanceof AlgoVectorPoint) {
						AlgoVectorPoint algoVector = (AlgoVectorPoint) in
								.getParentAlgorithm();
						moveMode = MOVE_POINT_WITH_OFFSET;
						setMovedGeoPoint((GeoElement) algoVector.getP());
						setTranslateFromPointStart(movedGeoElement,
								movedGeoPoint);
					}
				}
				return;
			}
		}
			
		translateableGeos = null;
		handleMovedElementDependentWithChangeableCoordParentNumbers();
		handleMovedElementDependentInitMode();

	}

	@Override
	protected void movePointWithOffset(boolean repaint) {
		companion.movePoint(repaint, null);
	}

	@Override
	final protected GeoElement[] orthogonal(Hits hits, boolean selPreview) {

		if (hits.isEmpty())
			return null;

		boolean hitPoint = (addSelectedPoint(hits, 1, false, selPreview) != 0);

		if (!hitPoint) {
			if (selCS2D() == 0)
				addSelectedLine(hits, 1, false, selPreview);
			if (selLines() == 0)
				addSelectedCS2D(hits, 1, false, selPreview);
		}

		if (selPoints() == 1) {
			if (selCS2D() == 1) {
				// fetch selected point and plane
				GeoPointND[] points = getSelectedPointsND();
				GeoCoordSys[] cs = getSelectedCS2D();
				// create new line
				return new GeoElement[] { (GeoElement) getKernel()
						.getManager3D()
.OrthogonalLine3D(null, points[0],
								(GeoCoordSys2D) cs[0]) };
			} else if (selLines() == 1) {
				// fetch selected point and line
				GeoPointND[] points = getSelectedPointsND();
				GeoLineND[] lines = getSelectedLinesND();
				// create new line
				return new GeoElement[] { (GeoElement) getKernel()
						.getManager3D().OrthogonalLine3D(null, points[0],
								lines[0], kernel.getSpace()) };
			}
		}

		return null;
	}

	private final GeoElement[] rotateAroundLine(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		// Transformable
		int count = 0;
		if (selGeos() == 0) {
			Hits rotAbles = hits.getHits(Test.TRANSFORMABLE, tempArrayList);
			count = addSelectedGeo(rotAbles, 1, false, selPreview);
		}

		// polygon
		if (count == 0) {
			count = addSelectedPolygon(hits, 1, false, selPreview);
		}

		// rotation axis
		if (count == 0) {
			addSelectedLine(hits, 1, false, selPreview);
		}

		// we got the rotation center point
		if ((selLines() == 1) && (selGeos() > 0)) {

			GeoElement[] selGeos = getSelectedGeos();

			getDialogManager().showNumberInputDialogRotate(
					l10n.getMenu(EuclidianConstants.getModeText(mode)),
					getSelectedPolygons(), getSelectedLinesND(), selGeos, this);

			return null;

		}

		return null;
	}

	/**
	 * @param geoRot
	 *            rotated object
	 * @param phi
	 *            angle
	 * @param line
	 *            line
	 * @return rotated object
	 */
	public GeoElement[] rotateAroundLine(GeoElement geoRot, GeoNumberValue phi,
			GeoLineND line) {

		return kernel.getManager3D().Rotate3D(null, geoRot, phi, line);
	}

	/**
	 * 
	 * @param clockwise
	 *            user's choice
	 * @param line
	 *            rotation axis
	 * @return correct clockwise orientation resp. view/line
	 */
	public boolean viewOrientationForClockwise(boolean clockwise, GeoLineND line) {

		if (line.getDirectionInD3().dotproduct(view3D.getViewDirection()) > 0)
			return !clockwise;

		return clockwise;
	}

	private final GeoElement[] mirrorAtPlane(Hits hits, boolean selPreview) {
		if (hits.isEmpty()) {
			return null;
		}

		// Transformable
		int count = 0;
		if (selGeos() == 0) {
			Hits mirAbles = hits.getHits(Test.TRANSFORMABLE, tempArrayList);
			count = addSelectedGeo(mirAbles, 1, false, selPreview);
		}

		// plane = mirror
		if (count == 0) {
			addSelectedCS2D(hits, 1, false, selPreview);
		}

		// we got the mirror plane
		if (selCS2D() == 1) {
			if (selGeos() > 0) {
				// mirror all selected geos
				GeoElement[] geos = getSelectedGeos();
				GeoCoordSys2D plane = (GeoCoordSys2D) getSelectedCS2D()[0];
				GeoCoordSys2D mirror = plane;

				if (((GeoElement) plane).isGeoConic()) { // no override for
															// mirror at circle
					plane = kernel.getManager3D().Plane3D(mirror);
				}

				ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
				checkZooming();

				for (int i = 0; i < geos.length; i++) {
					if (geos[i] != mirror) {
						if (geos[i] instanceof Transformable) {
							ret.addAll(Arrays.asList(kernel.getManager3D()
									.Mirror3D(null, geos[i], plane)));
						} else if (geos[i].isGeoPolygon()) {
							ret.addAll(Arrays.asList(kernel.getManager3D()
									.Mirror3D(null, geos[i], plane)));
						}
					}
				}
				GeoElement[] retex = {};
				return ret.toArray(retex);
			}
		}
		return null;
	}

	/**
	 * show popup menu when no geo is selected
	 * 
	 * @param hits
	 *            hits on the mouse
	 */
	@Override
	protected void showPopupMenuChooseGeo(ArrayList<GeoElement> selectedGeos1,
			Hits hits) {
		if (app.isUsingFullGui() && app.getGuiManager() != null) {
			// if (geo != null) {

			app.getGuiManager().showPopupChooseGeo(selectedGeos1, hits, view3D,
					mouseLoc);

			/*
			 * Now overriden } else { // for 3D : if the geo hitted is xOyPlane,
			 * then // chooseGeo return null //
			 * app.getGuiManager().showDrawingPadPopup((EuclidianView) // view,
			 * mouseLoc); showDrawingPadPopup(mouseLoc); }
			 */
		}
	}

	/**
	 * update all drawables now
	 */
	public void updateOwnDrawablesNow() {
		for (IntersectionCurve intersectionCurve : intersectionCurveList)
			intersectionCurve.drawable.update();
	}

	@Override
	public void setMode(int newMode, ModeSetter ms) {

		// clear specific modes fields

		// clear intersections
		if (newMode != EuclidianConstants.MODE_INTERSECTION_CURVE) {
			intersectionCurveList.clear();
		}

		// clear pyramid/prism basis
		pyramidBasis = null;

		super.setMode(newMode, ms);

		if (!temporaryMode) {
			initPointMoveMode();
		}
	}

	@Override
	protected void setDragCursorIfMoveView() {
		// nothing to do, keep same cursor
	}

	@Override
	protected final void processSelectionRectangle(boolean alt,
			boolean isControlDown, boolean shift) {

		// TODO implement this
	}

	@Override
	protected int addSelectedPlanesForAngle(Hits hits, int count,
			boolean selPreview) {

		if (selVectors() == 0) {
			if (selLines() == 0) { // angle between two planes
				return addSelectedPlane(hits, 2, false, selPreview);
			} else if (selLines() == 1) { // angle between line and plane
				return addSelectedPlane(hits, 1, false, selPreview);
			}
		}

		return count;
	}

	@Override
	protected GeoAngle createAngle3D() {

		if (selPlanes() == 2) {
			GeoPlaneND[] planes = getSelectedPlanes();
			return kernel.getManager3D().Angle3D(null, planes[0], planes[1]);
		}

		if (selPlanes() == 1 && selLines() == 1) {
			GeoLineND[] lines = getSelectedLinesND();
			GeoPlaneND[] planes = getSelectedPlanes();
			return kernel.getManager3D().Angle3D(null, lines[0], planes[0]);
		}

		return null;
	}

	/**
	 * 
	 * @return true if there is a 3D input
	 */
	public boolean hasInput3D() {
		return false;
	}

	/**
	 * 
	 * @return true if we use depth for hitting
	 */
	public boolean useInputDepthForHitting() {
		return false;
	}

	@Override
	protected Coords getMouseLocRW() {
		return view3D.getCursor3D().getInhomCoordsInD3();
	}

	public static void rotateObject(final App app, final String rawInput,
			final boolean clockwise, final GeoPolygon[] polys,
			final GeoLineND[] lines, final GeoElement[] selGeos,
			final EuclidianController3D ec, final ErrorHandler eh,
			final AsyncOperation<String> callback) {

		final String angleText = rawInput;
		Kernel kernel = app.getKernel();

		// avoid labeling of num
		final Construction cons = kernel.getConstruction();
		final boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		String inputText = rawInput;
		// negative orientation ?
		if (ec.viewOrientationForClockwise(clockwise, lines[0])) {
			inputText = "-(" + inputText + ")";
		}

		kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(
				inputText, false, eh, true,
				new AsyncOperation<GeoElementND[]>() {

					@Override
					public void callback(GeoElementND[] result) {
						String defaultRotateAngle = Unicode.FORTY_FIVE_DEGREES;

						cons.setSuppressLabelCreation(oldVal);

						boolean success = result != null && result.length > 0
								&& result[0] instanceof GeoNumberValue;

						if (success) {
							// GeoElement circle = kernel.Circle(null,
							// geoPoint1,
							// ((NumberInputHandler)inputHandler).getNum());
							GeoNumberValue num = (GeoNumberValue) result[0];
							// geogebra.gui.AngleInputDialog dialog =
							// (geogebra.gui.AngleInputDialog) ob[1];

							// keep angle entered if it ends with 'degrees'
							if (angleText.endsWith(Unicode.DEGREE))
								defaultRotateAngle = angleText;

							if (polys.length == 1) {

								GeoElement[] geos = ec.rotateAroundLine(
										polys[0], num, lines[0]);
								if (geos != null) {
									app.storeUndoInfoAndStateForModeStarting();
									ec.memorizeJustCreatedGeos(geos);
								}
								if (callback != null) {
									callback.callback(defaultRotateAngle);
								}
								return;
							}

							ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
							for (int i = 0; i < selGeos.length; i++) {
								if (selGeos[i] != lines[0]) {
									if (selGeos[i] instanceof Transformable) {
										ret.addAll(Arrays.asList(
												ec.rotateAroundLine(selGeos[i],
														num, lines[0])));
									} else if (selGeos[i].isGeoPolygon()) {
										ret.addAll(Arrays.asList(
												ec.rotateAroundLine(selGeos[i],
														num, lines[0])));
									}
								}
							}
							if (!ret.isEmpty()) {
								app.storeUndoInfoAndStateForModeStarting();
								ec.memorizeJustCreatedGeos(ret);
							}
						} else {
							if (result != null && result.length > 0) {
								eh.showError(app.getLocalization()
										.getError("NumberExpected"));
							}
						}
						if (callback != null) {
							callback.callback(
									success ? defaultRotateAngle : null);
						}
					}

				});
	}

	@Override
	protected GeoElement[] polygon() {

		if (polygonMode == POLYGON_NORMAL) {
			// cancel last switch
			cancelSwitchPointMoveModeIfNeeded();
			view3D.disposePreview();
		}

		return super.polygon();

	}

	@Override
	protected GeoVectorND createVectorForTranslation() {
		return ((AlgoDispatcher3D) getAlgoDispatcher()).Vector3D();
	}

	@Override
	protected GeoVectorND createVectorForTranslation(String label) {
		return ((AlgoDispatcher3D) getAlgoDispatcher()).Vector3D(label);
	}
	
	@Override
	protected int getModeForShallMoveView(AbstractEvent event) {
		if (event.isShiftDown() || app.isMiddleClick(event)) {
			return EuclidianConstants.MODE_TRANSLATEVIEW;
		}
		return EuclidianConstants.MODE_ROTATEVIEW;
	}

	@Override
	protected boolean hasNoHitsDisablingModeForShallMoveView(Hits hits,
			AbstractEvent event) {
		if (hits.isEmpty()) {
			return true;
		}
		
		if (app.has(Feature.DRAGGING_NON_MOVEABLE_OBJECT_SPIN_THE_VIEW)) {
			GeoElement geoLabel = view.getLabelHitCheckRefresh(mouseLoc,
					event.getType());
			if (geoLabel != null) {
				return false;
			}

			for (GeoElement geo : hits) {
				if (isDraggable(geo, view3D)) {
					return false;
				}
			}

			// ok, let's spin the view
			return true;

		}
		return hits.get(0) == kernel.getXOYPlane();
	}

	/**
	 * 
	 * @param geo
	 *            geo
	 * @param view
	 *            view
	 * @return true if drag on this geo does something
	 */
	public static boolean isDraggable(GeoElement geo,
			EuclidianViewInterfaceSlim view) {
		// if geo is moveable
		if (geo.isMoveable(view)) {
			return true;
		}

		// if geo has translate parent algo
		if (geo.isTranslateable()) {
			AlgoElement algo = geo.getParentAlgorithm();
			if (algo instanceof AlgoTranslate) {
				return true;
			}
		}

		// e.g. for extruded pyramid or polyhedron net
		if (geo.hasChangeableCoordParentNumbers()) {
			return true;
		}
		
		return false;
	}

	@Override
	protected DrawList getComboBoxHit() {
		return null;
	}

	@Override
	protected boolean overComboBox(AbstractEvent event, GeoElement hit) {
		return false;
	}

	@Override
	public boolean textfieldJustFocused(int x, int y, PointerEventType type) {
		return false;
	}

	/**
	 * exit use of 3D input
	 */
	public void exitInput3D() {
		// use for 3D input
	}

	/**
	 * 
	 * @return true if uses zSpace
	 */
	public boolean isZSpace() {
		return false;
	}

	@Override
	public void onPinchPhone(int x, int y, double scaleFactor) {
		view3D.screenTranslateAndScale(
				x - twoTouchStartX,
				y - twoTouchStartY, scaleFactor);
	}

	@Override
	protected void hidePreviewForPhone() {
		if (!(view.getPreviewDrawable() instanceof DrawPolyLine3D)
				&& !(view.getPreviewDrawable() instanceof DrawPolygon3D)
				&& !(view.getPreviewDrawable() instanceof DrawPolyhedron3D)) {
			view.setPreview(null);
		}
	}

	private Coords scaleAxisVector = new Coords(2),
			scaleOrigin = new Coords(2);
	private double scaleOld, scaleDistanceInPixelsStart;

	@Override
	protected void setMoveModeIfAxis(Object hit) {
		if (app.has(Feature.DIFFERENT_AXIS_RATIO_3D)) {
			int newMode = -1;
			if (hit == kernel.getXAxis()) {
				newMode = MOVE_X_AXIS;
				scaleAxisVector.set2(view3D.getToScreenMatrix().getVx());
				scaleOld = view3D.getXscale();
			} else if (hit == kernel.getYAxis()) {
				newMode = MOVE_Y_AXIS;
				scaleAxisVector.set2(view3D.getToScreenMatrix().getVy());
				scaleOld = view3D.getYscale();
			} else if (hit == kernel.getZAxis3D()) {
				newMode = MOVE_Z_AXIS;
				scaleAxisVector.set2(view3D.getToScreenMatrix().getVz());
				scaleOld = view3D.getZscale();
			}

			if (newMode != -1) {
				// an axis was hit
				// check if axis is not quite orthogonal to screen
				scaleAxisVector.calcNorm();
				double norm = scaleAxisVector.getNorm();
				// Log.debug(norm / scaleOld);
				if (norm / scaleOld > 0.1) {
					scaleAxisVector.mulInside(1 / norm);
					scaleOrigin.set2(view3D.getToScreenMatrix().getOrigin());
					tmpCoords.setMul(view3D.getToScreenMatrix(), view3D
							.getCursor3D().getInhomCoordsInD3());
					scaleDistanceInPixelsStart = getDistanceForScale(
							tmpCoords.getX(), tmpCoords.getY());
					if (Math.abs(scaleDistanceInPixelsStart) > MIN_MOUSE_MOVE_FOR_AXIS_SCALE) {
						moveMode = newMode;
					}
				}
			}

		}
	}

	private double getDistanceForScale(double x, double y) {
		return (x - scaleOrigin.getX()) * scaleAxisVector.getX()
				+ (y - scaleOrigin.getY()) * scaleAxisVector.getY();
	}

	@Override
	protected void scaleXAxis(boolean repaint) {
		scaleAxis(repaint);
	}

	@Override
	protected void scaleYAxis(boolean repaint) {
		scaleAxis(repaint);
	}

	@Override
	protected void scaleZAxis(boolean repaint) {
		scaleAxis(repaint);
	}

	final private void scaleAxis(boolean repaint) {
		if (app.has(Feature.DIFFERENT_AXIS_RATIO_3D)) {
			if (repaint) {
				GPoint centeredMouse = new GPoint();
				view3D.setCenteredPosition(mouseLoc, centeredMouse);
				double distance = getDistanceForScale(centeredMouse.x,
						centeredMouse.y);
				
				// when mouse is close to origin
				if (scaleDistanceInPixelsStart > 0) {
					if (distance < MIN_MOUSE_MOVE_FOR_AXIS_SCALE) {
						distance = MIN_MOUSE_MOVE_FOR_AXIS_SCALE;
					}
				} else {
					if (distance > -MIN_MOUSE_MOVE_FOR_AXIS_SCALE) {
						distance = -MIN_MOUSE_MOVE_FOR_AXIS_SCALE;
					}
				}

				view3D.setCoordSystemFromAxisScale(distance
						/ scaleDistanceInPixelsStart, scaleOld, moveMode);
			}
		}
	}

	@Override
	public boolean penMode(int mode2) {
		// no pen mode in 3D for now
		return false;
	}

	@Override
	protected void setCursorForTranslateView(Hits hits) {
		view3D.setCursorForTranslateView(hits);
	}

}
