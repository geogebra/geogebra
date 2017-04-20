package org.geogebra.common.geogebra3D.euclidian3D;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import org.geogebra.common.geogebra3D.euclidianFor3D.EuclidianControllerFor3DCompanion;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EuclidianStyleConstants;

/**
 * Euclidian controller creator for 3D controller
 * 
 * @author mathieu
 *
 */
public class EuclidianController3DCompanion
		extends EuclidianControllerFor3DCompanion {

	/**
	 * constructor
	 * 
	 * @param ec
	 *            controller
	 */
	public EuclidianController3DCompanion(EuclidianController ec) {
		super(ec);
	}

	private EuclidianController3D ec3D;

	@Override
	protected void setEuclidianController(EuclidianController ec) {
		super.setEuclidianController(ec);
		ec3D = (EuclidianController3D) ec;
	}

	private Coords tmpCoords1 = new Coords(4), tmpCoords2 = new Coords(4);

	@Override
	public void movePoint(boolean repaint, AbstractEvent event) {

		// Application.debug("movePointMode="+movePointMode);
		if (ec.movedGeoPoint instanceof GeoPoint3D) {
			GeoPoint3D movedGeoPoint3D = (GeoPoint3D) ec.movedGeoPoint;

			if (movedGeoPoint3D.isPointOnPath()) {

				ec3D.setMouseInformation(movedGeoPoint3D);
				movedGeoPoint3D.doPath();

				Coords coords = movedGeoPoint3D.getInhomCoordsInD(3);
				if (checkPointCapturingXYThenZ(coords)) {
					movedGeoPoint3D.setWillingCoords(coords);
					movedGeoPoint3D.setWillingDirectionUndefined();
					movedGeoPoint3D.doPath();
				}

			} else if (movedGeoPoint3D.hasRegion()) {

				ec3D.setMouseInformation(movedGeoPoint3D);
				hitRegion(movedGeoPoint3D);
				movedGeoPoint3D.doRegion();

				boolean changed = false;

				Coords coords = movedGeoPoint3D.getCoords();
				if (movedGeoPoint3D.getRegion() == ec.getKernel()
						.getXOYPlane()) {
					changed = ec3D.checkXYMinMax(coords);
				}
				if (checkPointCapturingXYThenZ(coords) || changed) {
					movedGeoPoint3D.setWillingCoords(coords);
					movedGeoPoint3D.setWillingDirectionUndefined();
					movedGeoPoint3D.doRegion();
				}

				ec3D.view3D.getCursor3D()
						.setMoveNormalDirection(movedGeoPoint3D
								.getRegionParameters().getNormal());

			} else {

				// if (isShiftDown && mouseLoc != null){ //moves the point along
				// z-axis
				if (ec.movedGeoPoint.getMoveMode() == GeoPointND.MOVE_MODE_Z
						|| (ec.movedGeoPoint
								.getMoveMode() == GeoPointND.MOVE_MODE_TOOL_DEFAULT
								&& ec3D.getPointMoveMode() == GeoPointND.MOVE_MODE_Z)) { // moves
					moveAlongZAxis(movedGeoPoint3D);

				} else {

					ec3D.movePointOnCurrentPlane(movedGeoPoint3D, false);

				}

				// update point decorations
				if (ec.getMoveMode() == EuclidianController.MOVE_POINT) {
					ec3D.view3D.updatePointDecorations();
				}

			}

			// update 3D cursor coordinates (false : no path or region update)
			ec3D.view3D.getCursor3D()
					.setCoords(movedGeoPoint3D.getCoords(), false);
			ec3D.view3D.updateMatrixForCursor3D();

			if (repaint) {
				movedGeoPoint3D.updateRepaint();// for highlighting in
												// algebraView
			} else {
				movedGeoPoint3D.updateCascade();// TODO modify
												// movedGeoPoint3D.updateCascade()
			}

			// update previewable
			if (ec.getView().getPreviewDrawable() != null) {
				ec.getView().updatePreviewable();
			}

		} else { // 2D point
			Coords o = ec3D.view3D
					.getPickPoint(ec.mouseLoc);
			ec3D.view3D.toSceneCoords3D(o);
			// TODO do this once
			// GgbVector v = new GgbVector(new double[] {0,0,1,0});
			// view3D.toSceneCoords3D(view3D.getViewDirection());
			o.projectPlaneThruVIfPossible(CoordMatrix4x4.IDENTITY,
					ec3D.view3D.getHittingDirection(),
					tmpCoords1, tmpCoords2); // TODO
												// use
												// current
												// region
												// instead
												// of
												// identity

			// capturing points
			checkPointCapturingXY(tmpCoords2);

			ec.xRW = tmpCoords2.getX();
			ec.yRW = tmpCoords2.getY();
			super.movePoint(repaint, ec3D.mouseEvent);

			ec3D.view3D.getCursor3D()
					.setCoords(ec.movedGeoPoint.getCoordsInD3(), false);

		}
	}

	public void moveAlongZAxis(GeoPointND movedGeoPoint3D) {
		// along
		// z-axis

		/*
		 * //getting current pick point and direction v if (movePointMode !=
		 * MOVE_POINT_MODE_Z){ mouseLocOld = (Point) mouseLoc.clone();
		 * positionOld = movedGeoPoint3D.getCoords().copyVector(); movePointMode
		 * = MOVE_POINT_MODE_Z; }
		 */
		Coords o = ec3D.view3D
				.getPickPoint(ec.mouseLoc);
		ec3D.view3D.toSceneCoords3D(o);
		ec3D.addOffsetForTranslation(o);
		// GgbVector o =
		// view3D.getPickFromScenePoint(positionOld,mouseLoc.x-mouseLocOld.x,mouseLoc.y-mouseLocOld.y);
		// view3D.toSceneCoords3D(o);

		// getting new position of the point
		movedGeoPoint3D.getCoords().projectNearLine(o,
				ec3D.view3D.getHittingDirection(),
				ec3D.getNormalTranslateDirection(),
				tmpCoords1);

		if (ec.getMoveMode() == EuclidianController.MOVE_POINT) {
			// max z value
			if (tmpCoords1.getZ() > ec3D.zMinMax[1]) {
				tmpCoords1.setZ(ec3D.zMinMax[1]);
			} else if (tmpCoords1
					.getZ() < ec3D.zMinMax[0]) {
				tmpCoords1.setZ(ec3D.zMinMax[0]);
			}
		}

		// capturing points
		switch (ec.getView().getPointCapturingMode()) {
		case EuclidianStyleConstants.POINT_CAPTURING_STICKY_POINTS:
			// TODO
		default:
		case EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC:
			if (!ec.getView().isGridOrAxesShown()) {
				break;
			}
		case EuclidianStyleConstants.POINT_CAPTURING_ON:
		case EuclidianStyleConstants.POINT_CAPTURING_ON_GRID:
			double z0 = tmpCoords1.getZ();
			double gz = ec.getView().getGridDistances(2);
			double z = Kernel.roundToScale(z0, gz);
			if (ec.getView()
					.getPointCapturingMode() == EuclidianStyleConstants.POINT_CAPTURING_ON_GRID
					|| Math.abs(z - z0) < gz
							* ec.getPointCapturingPercentage()) {
				tmpCoords1.setZ(z);
			}
		}

		// set point coords
		movedGeoPoint3D.setCoords(tmpCoords1, true);

		// update the moving plane altitude
		ec3D.getCurrentPlane()
				.set(movedGeoPoint3D.getCoords(), 4);

	}

	/**
	 * capture coords regarding capture mode
	 * 
	 * @param coords
	 *            (x,y) coords
	 * @return true if coords have been changed
	 */
	public boolean checkPointCapturingXY(Coords coords) {
		// capturing points
		switch (ec.getView().getPointCapturingMode()) {
		case EuclidianStyleConstants.POINT_CAPTURING_STICKY_POINTS:
			// TODO
		case EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC:
			if (!ec.getView().isGridOrAxesShown()) {
				return false;
			}
		case EuclidianStyleConstants.POINT_CAPTURING_ON:
		case EuclidianStyleConstants.POINT_CAPTURING_ON_GRID:
			double x0 = coords.getX();
			double y0 = coords.getY();
			double gx = ec.getView().getGridDistances(0);
			double gy = ec.getView().getGridDistances(1);
			double x = Kernel.roundToScale(x0, gx);
			double y = Kernel.roundToScale(y0, gy);
			// Log.debug("\n"+x+"\n"+y+"\np=\n"+project);
			if (ec.getView()
					.getPointCapturingMode() == EuclidianStyleConstants.POINT_CAPTURING_ON_GRID
					|| (Math.abs(x - x0) < gx * ec.getPointCapturingPercentage()
							&& Math.abs(y - y0) < gy
									* ec.getPointCapturingPercentage())) {
				coords.setX(x);
				coords.setY(y);
				return true;
			}
			return false;
		}

		return false;
	}

	/**
	 * capture coords regarding capture mode
	 * 
	 * @param coords
	 *            (x,y) coords
	 * @return true if coords have been changed
	 */
	public boolean checkPointCapturingXYThenZ(Coords coords) {
		// capturing points
		switch (ec.getView().getPointCapturingMode()) {
		case EuclidianStyleConstants.POINT_CAPTURING_STICKY_POINTS:
			// TODO
		case EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC:
			if (!ec.getView().isGridOrAxesShown()) {
				if (specificPointCapturingAutomatic()) {
					return checkPointCapturingZto0(coords);
				}
				return false;
			}
		case EuclidianStyleConstants.POINT_CAPTURING_ON:
		case EuclidianStyleConstants.POINT_CAPTURING_ON_GRID:
			double x0 = coords.getX();
			double y0 = coords.getY();
			double gx = ec.getView().getGridDistances(0);
			double gy = ec.getView().getGridDistances(1);
			double x = Kernel.roundToScale(x0, gx);
			double y = Kernel.roundToScale(y0, gy);
			// Log.debug("\nx="+x+"\ny="+y+"\nz=\n"+z);
			if (ec.getView()
					.getPointCapturingMode() == EuclidianStyleConstants.POINT_CAPTURING_ON_GRID
					|| (Math.abs(x - x0) < gx * ec.getPointCapturingPercentage()
							&& Math.abs(y - y0) < gy
									* ec.getPointCapturingPercentage())

			) {
				coords.setX(x);
				coords.setY(y);
				checkPointCapturingZ(coords);
				return true;
			}

			return checkPointCapturingZ(coords);
		}

		return false;
	}

	/**
	 * 
	 * @return true e.g. when using stylus
	 */
	protected boolean specificPointCapturingAutomatic() {
		return false;
	}

	private boolean checkPointCapturingZto0(Coords coords) {
		return checkPointCapturingZ(coords, 0);
	}

	private boolean checkPointCapturingZ(Coords coords) {
		return checkPointCapturingZ(coords, coords.getZ());
	}

	private boolean checkPointCapturingZ(Coords coords, double zStick) {
		double z0 = coords.getZ();
		double gz = ec.getView().getGridDistances(2);
		double z = Kernel.roundToScale(zStick, gz);
		if (ec.getView()
				.getPointCapturingMode() == EuclidianStyleConstants.POINT_CAPTURING_ON_GRID
				|| Math.abs(z - z0) < gz * ec.getPointCapturingPercentage()) {
			coords.setZ(z);
			return true;
		}
		return false;
	}

	/**
	 * create a new free point or update the preview point
	 */
	@Override
	protected GeoPointND createNewPoint(boolean forPreviewable,
			boolean complex) {

		GeoPoint3D point3D;

		if (!forPreviewable) {
			// if there's "no" 3D cursor, no point is created
			if (ec3D.view3D
					.getCursor3DType() == EuclidianView3D.PREVIEW_POINT_NONE) {
				return null;
			}
			point3D = (GeoPoint3D) ec.kernel.getManager3D().Point3D(null, 0, 0,
					0, false);
		} else {
			point3D = createNewFreePoint(complex);
			if (point3D == null) {
				return null;
			}
			point3D.setPath(null);
			point3D.setRegion(null);
			ec3D.view3D
					.setCursor3DType(EuclidianView3D.PREVIEW_POINT_FREE);
			return point3D;
		}

		CoordMatrix4x4.Identity(ec3D.getCurrentPlane());
		ec3D.movePointOnCurrentPlane(point3D, false);

		return point3D;
	}

	/**
	 * create a new path point or update the preview point
	 */
	@Override
	protected GeoPointND createNewPoint(boolean forPreviewable, Path path,
			boolean complex) {

		GeoPoint3D point3D;

		if (!forPreviewable) {
			point3D = (GeoPoint3D) ec.getKernel().getManager3D().Point3D(null,
					path, false);
		} else {
			point3D = ec3D.view3D.getCursor3D();
			point3D.setPath(path);
			point3D.setRegion(null);
			ec3D.view3D
					.setCursor3DType(EuclidianView3D.PREVIEW_POINT_PATH);
		}

		ec3D.setMouseInformation(point3D);
		point3D.doPath();

		// try to capture point
		tmpCoords1.set(point3D.getInhomCoordsInD3());
		if (checkPointCapturingXYThenZ(tmpCoords1)) {
			point3D.setWillingCoords(tmpCoords1);
			point3D.doPath();
		}

		return point3D;
	}

	private Coords captureCoords = Coords.createInhomCoorsInD3();

	private void hitRegion(GeoPoint3D point3D) {
		DrawableND d = ec3D.view3D
				.getDrawableND((GeoElement) point3D.getRegion());
		if (d != null) {
			Hitting hitting = ec3D.view3D.getRenderer().getHitting();
			hitting.setOriginDirectionThreshold(point3D.getWillingCoords(),
					point3D.getWillingDirection(), App.DEFAULT_THRESHOLD);
			// try to set the parameters from drawable hit process
			((Drawable3D) d).hit(hitting);
		}
	}

	/**
	 * create a new region point or update the preview point
	 */
	@Override
	protected GeoPointND createNewPoint(boolean forPreviewable, Region region,
			boolean complex) {

		GeoPoint3D point3D = ec3D.view3D.getCursor3D();
		point3D.setPath(null);
		point3D.setRegion(region);

		ec3D.setMouseInformation(point3D);
		hitRegion(point3D);
		point3D.doRegion();
		point3D.setMoveNormalDirection(
				point3D.getRegionParameters().getNormal());

		if (region == ec.getKernel().getXOYPlane()) {
			Coords coords = point3D.getInhomCoords();
			GeoPlane3D plane = (GeoPlane3D) region;
			if (coords.getX() < plane.getXmin()
					|| coords.getX() > plane.getXmax()
					|| coords.getY() < plane.getYmin()
					|| coords.getY() > plane.getYmax()) {
				ec3D.view3D
						.setCursor3DType(EuclidianView3D.PREVIEW_POINT_NONE);
				return null;
			}

			// try to capture coords
			captureCoords.setValues(coords, 2);
			if (checkPointCapturingXY(captureCoords)) {
				point3D.setCoords(captureCoords, false);
			}

			ec3D.view3D
					.setCursor3DType(EuclidianView3D.PREVIEW_POINT_REGION);

		} else {

			// try to capture coords
			tmpCoords1.set(point3D.getInhomCoordsInD3());
			if (checkPointCapturingXYThenZ(tmpCoords1)) {
				point3D.setWillingCoords(tmpCoords1);
				point3D.doRegion();
			}

			GeoElement geo = (GeoElement) region;
			if (geo.isGeoQuadric() && ((GeoQuadric3D) geo)
					.getType() == GeoQuadricNDConstants.QUADRIC_LINE) {
				ec3D.view3D.setCursor3DType(
						EuclidianView3D.PREVIEW_POINT_REGION_AS_PATH);
			} else {
				ec3D.view3D
						.setCursor3DType(EuclidianView3D.PREVIEW_POINT_REGION);
			}

		}

		if (!forPreviewable) {

			GeoPoint3D ret = (GeoPoint3D) ec.getKernel().getManager3D()
					.Point3DIn(null, region, false);
			ret.set(point3D);
			// ret.setRegion(region);
			ret.doRegion();

			return ret;
		}

		return point3D;

	}

	/**
	 * 
	 * @param complex
	 *            says if complex coords are wanted
	 * @return new free point (eventually on xOy plane with 2D mouse)
	 */
	protected GeoPoint3D createNewFreePoint(boolean complex) {
		return (GeoPoint3D) createNewPoint(true, ec.getKernel().getXOYPlane(),
				complex);
	}

	@Override
	public boolean setCoordsToMouseLoc(GeoPointND loc) {
		loc.setCoords(ec.mouseLoc.x, ec.mouseLoc.y, 1.0);
		return false;
	}

	protected void updateMovedGeoPointStartValues(Coords coords,
			GeoPointND movedGeoPoint, CoordMatrix4x4 currentPlane) {
		if (!movedGeoPoint.isPointOnPath() && !movedGeoPoint.hasRegion()) {

			CoordMatrix4x4.Identity(currentPlane);
			// update the moving plane altitude
			currentPlane.set(coords, 4);

		}
	}

	/**
	 * 
	 * @param movedGeoElement
	 *            moved element
	 * @return true if there is a free plane to move
	 */
	protected boolean handleMovedElementFreePlane(GeoElement movedGeoElement) {
		return false;
	}

	protected void setMouseOrigin(GeoPoint3D point, GPoint mouseLoc) {
		// Michael Borcherds
		// move mouse fast, sometimes get mouseLoc = null
		if (mouseLoc == null) {
			return;
		}

		Coords o = getView().getPickPoint(mouseLoc);
		getView().toSceneCoords3D(o);

		ec3D.addOffsetForTranslation(o);
		point.setWillingCoords(o);
	}

	protected EuclidianView3D getView() {
		return (EuclidianView3D) ec.getView();
	}

}
