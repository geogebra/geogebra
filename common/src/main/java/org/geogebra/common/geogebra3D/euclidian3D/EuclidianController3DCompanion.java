package org.geogebra.common.geogebra3D.euclidian3D;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import org.geogebra.common.geogebra3D.euclidianFor3D.EuclidianControllerFor3DCompanion;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCursor3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.App;
import org.geogebra.common.main.DialogManager.CreateGeoForRotate;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.DoubleUtil;

/**
 * Euclidian controller creator for 3D controller
 * 
 * @author mathieu
 *
 */
public class EuclidianController3DCompanion
		extends EuclidianControllerFor3DCompanion {

	private EuclidianController3D ec3D;
	private Coords tmpCoords1 = new Coords(4);
	private Coords tmpCoords2 = new Coords(4);
	private Coords tmpCoordsForOrigin = new Coords(4);
	private Coords tmpCoordsForDirection = new Coords(4);
	private Coords captureCoords = Coords.createInhomCoorsInD3();

	/** precision in Z */
	static final private double AR_ROUNDING_PRECISION_PERCENTAGE = 10.0 / 100.0;

	/**
	 * constructor
	 * 
	 * @param ec
	 *            controller
	 */
	public EuclidianController3DCompanion(EuclidianController ec) {
		super(ec);
	}

	@Override
	protected void setEuclidianController(EuclidianController ec) {
		super.setEuclidianController(ec);
		ec3D = (EuclidianController3D) ec;
	}

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
					long maxDelay = EuclidianConstants.DRAGGING_DELAY_FOR_MOVING_POINT_ALONG_Z;
					if (!ec.isTemporaryMode() || ec
							.getElapsedTimeFromLastMousePressed() > maxDelay) {
						moveAlongZAxis(movedGeoPoint3D);
					}
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
				movedGeoPoint3D.updateRepaint(); // for highlighting in
												// algebraView
			} else {
				movedGeoPoint3D.updateCascade(); // TODO modify
												// movedGeoPoint3D.updateCascade()
			}

			// update previewable
			if (ec.getView().getPreviewDrawable() != null) {
				ec.getView().updatePreviewable();
			}

		} else { // 2D point
			ec3D.view3D.getHittingOrigin(ec.mouseLoc, tmpCoordsForOrigin);
			// TODO do this once
			// GgbVector v = new GgbVector(new double[] {0,0,1,0});
			// view3D.toSceneCoords3D(view3D.getViewDirection());
			ec3D.view3D.getHittingDirection(tmpCoordsForDirection);
			// TODO use current region instead of identity
			tmpCoordsForOrigin.projectPlaneThruVIfPossible(
					CoordMatrix4x4.IDENTITY, tmpCoordsForDirection,
					tmpCoords1, tmpCoords2);

			// capturing points
			checkPointCapturingXY(tmpCoords2);

			ec.setRwCoords(tmpCoords2);
			super.movePoint(repaint, ec3D.mouseEvent);

			ec3D.view3D.getCursor3D()
					.setCoords(ec.movedGeoPoint.getCoordsInD3(), false);

		}

		ec3D.view3D.enlargeClippingForPoint(ec3D.movedGeoPoint);
	}

	/**
	 * @param movedGeoPoint3D
	 *            moved point
	 */
	public void moveAlongZAxis(GeoPointND movedGeoPoint3D) {
		ec3D.view3D.getHittingOrigin(ec.mouseLoc, tmpCoordsForOrigin);
		ec3D.addOffsetForTranslation(tmpCoordsForOrigin);

		// getting new position of the point
		ec3D.view3D.getHittingDirection(tmpCoordsForDirection);
		movedGeoPoint3D.getCoords().projectNearLine(tmpCoordsForOrigin,
				tmpCoordsForDirection,
				ec3D.getNormalTranslateDirection(),
				tmpCoords1);

		if (!ec3D.view3D.isXREnabled() && ec.getMoveMode() == EuclidianController.MOVE_POINT) {
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
			EuclidianView3D view = (EuclidianView3D) ec.getView();
			if (!view.isGridOrAxesShown() && !view.getShowPlane()) {
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
			break;
		case EuclidianStyleConstants.POINT_CAPTURING_OFF:
			break;
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
				boolean changed = false;
				if (!DoubleUtil.isEqual(x, x0)) {
					coords.setX(x);
					changed = true;
				}
				if (!DoubleUtil.isEqual(y, y0)) {
					coords.setY(y);
					changed = true;
				}
				return changed;
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
				boolean changed = false;
				if (!DoubleUtil.isEqual(x, x0)) {
					coords.setX(x);
					changed = true;
				}
				if (!DoubleUtil.isEqual(y, y0)) {
					coords.setY(y);
					changed = true;
				}
				if (checkPointCapturingZ(coords)) {
					changed = true;
				}
				return changed;
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
			if (!DoubleUtil.isEqual(z, z0)) {
				coords.setZ(z);
				return true;
			}
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
			point3D = (GeoPoint3D) ec.getKernel().getManager3D().point3D(null,
					0, 0,
					0, false);
		} else {
            if (ec3D.view3D.isXREnabled() && !ec3D.view3D.getxOyPlane().isPlateVisible()
                    && !ec3D.view3D.getxOyPlane().isGridVisible()) {
                if (ec3D.view3D.getRenderer().getHittingFloorAR(tmpCoords1)) {
                    // round z coordinate and check if already existing hit
                    // to keep points at the same level
                    double rounding = DoubleUtil.round125(AR_ROUNDING_PRECISION_PERCENTAGE
                            * ec3D.view3D.getRenderer().getHittingDistanceAR()
                            / ec3D.view3D.getZscale());
                    if (DoubleUtil.isGreater(rounding, 0)) {
                        tmpCoords1.setZ(ec3D.view3D.getRenderer()
                                .checkHittingFloorZ(
                                        ((int) (tmpCoords1.getZ() / rounding)) * rounding));
                    }
                    tmpCoords1.setW(1);
                    // re-center it
                    ec3D.view3D.getHittingOrigin(ec.mouseLoc, tmpCoordsForOrigin);
                    ec3D.view3D.getHittingDirection(tmpCoordsForDirection);
                    tmpCoordsForOrigin.projectPlaneThruVIfPossible(Coords.VX, Coords.VY, Coords.VZ,
                            tmpCoords1, tmpCoordsForDirection, tmpCoords2);
                    // force z coordinate to be rounded as previously
                    tmpCoords2.setZ(tmpCoords1.getZ());
                    // set to 3D cursor
                    point3D = ec3D.view3D.getCursor3D();
                    point3D.setCoords(tmpCoords2);
                } else {
                    point3D = null;
                    ec3D.view3D.setCursor3DType(EuclidianView3D.PREVIEW_POINT_NONE);
                }
            } else {
                point3D = createNewFreePoint(complex);
            }
			if (point3D == null) {
				return null;
			}
			point3D.setPath(null);
			point3D.setRegion(null);
			ec3D.view3D
					.setCursor3DType(EuclidianView3D.PREVIEW_POINT_FREE);
			return point3D;
		}

		CoordMatrix4x4.identity(ec3D.getCurrentPlane());
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
			point3D = (GeoPoint3D) ec.getKernel().getManager3D().point3D(null,
					path, false);
		} else {
			point3D = ec3D.view3D.getCursor3D();
			((GeoCursor3D) point3D).setIsCaptured(false);
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
			if (point3D instanceof GeoCursor3D) {
				((GeoCursor3D) point3D).setIsCaptured(true);
			}
		}

		return point3D;
	}

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

		GeoCursor3D point3D = ec3D.view3D.getCursor3D();
		point3D.setIsCaptured(false);
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
			if (!ec3D.view3D.isXREnabled() && (coords.getX() < plane.getXmin()
					|| coords.getX() > plane.getXmax()
					|| coords.getY() < plane.getYmin()
					|| coords.getY() > plane.getYmax())) {
				ec3D.view3D
						.setCursor3DType(EuclidianView3D.PREVIEW_POINT_NONE);
				return null;
			}

			// try to capture coords
			captureCoords.setValues(coords, 2);
			if (checkPointCapturingXY(captureCoords)) {
				point3D.setCoords(captureCoords, false);
				point3D.setIsCaptured(true);
			}

			ec3D.view3D
					.setCursor3DType(EuclidianView3D.PREVIEW_POINT_REGION);

		} else {

			// try to capture coords
			tmpCoords1.set(point3D.getInhomCoordsInD3());
			if (checkPointCapturingXYThenZ(tmpCoords1)) {
				point3D.setWillingCoords(tmpCoords1);
				point3D.doRegion();
				point3D.setIsCaptured(true);
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
					.point3DIn(null, region, false);
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

	/**
	 * Updates point moving plane from new point coords
	 * 
	 * @param coords
	 *            current point position
	 * @param movedGeoPoint
	 *            moved point
	 * @param currentPlane
	 *            point moving plane
	 */
	protected void updateMovedGeoPointStartValues(Coords coords,
			GeoPointND movedGeoPoint, CoordMatrix4x4 currentPlane) {
		if (!movedGeoPoint.isPointOnPath() && !movedGeoPoint.hasRegion()) {

			CoordMatrix4x4.identity(currentPlane);
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

	/**
	 * Update willing coords from mouse location
	 * 
	 * @param point
	 *            point
	 * @param mouseLoc
	 *            mouse location
	 */
	protected void setMouseOrigin(GeoPoint3D point, GPoint mouseLoc) {
		// Michael Borcherds
		// move mouse fast, sometimes get mouseLoc = null
		if (mouseLoc == null) {
			return;
		}

		ec3D.view3D.getHittingOrigin(mouseLoc, tmpCoordsForOrigin);

		ec3D.addOffsetForTranslation(tmpCoordsForOrigin);
		point.setWillingCoords(tmpCoordsForOrigin);
	}

	/**
	 * @return the 3D view
	 */
	protected EuclidianView3D getView() {
		return (EuclidianView3D) ec.getView();
	}

	@Override
	public boolean viewOrientationForClockwise(boolean clockwise, CreateGeoForRotate creator) {
		if (creator.getPivot().isGeoPoint()) {
			return super.viewOrientationForClockwise(clockwise, creator);
		}
		return ec3D.viewOrientationForClockwise(clockwise, (GeoLineND) creator.getPivot());
	}

	@Override
	public GeoElement[] regularPolygon(GeoPointND geoPoint1,
			GeoPointND geoPoint2, GeoNumberValue value, GeoCoordSys2D direction) {
		if (geoPoint1.isGeoElement3D() || geoPoint2.isGeoElement3D()) {
			return ec.getKernel().getManager3D().regularPolygon(null, geoPoint1,
					geoPoint2, value,
					direction == null
							? ((EuclidianView3D) ec.getView()).getxOyPlane()
							: direction);
		}

		return ec.getKernel().getAlgoDispatcher().regularPolygon(null,
				geoPoint1, geoPoint2, value);
	}

}
