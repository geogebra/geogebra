package org.geogebra.common.geogebra3D.input3D;

import java.util.ArrayList;
import java.util.TreeSet;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.draw.DrawPoint;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3DCompanion;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.input3D.EuclidianViewInput3DCompanion.StationaryCoords;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCursor3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.DoubleUtil;

/**
 * Euclidian controller creator for 3D controller with 3D input
 * 
 * @author mathieu
 *
 */
public class EuclidianControllerInput3DCompanion extends
		EuclidianController3DCompanion {

	static final private int DISTANCE_THRESHOLD = 6;
	static final private double COS_THRESHOLD = Math.sin(Math.PI * 7.5 / 180);

	private Input3D input3D;
	private double startZNearest;

	protected Coords movedGeoPointStartCoords = new Coords(0, 0, 0, 1);
	private TreeSet<StickyPoint> stickyPoints;
	private TreeSet<StickyPointForDirection> stickyPointsForDirection;
	protected GeoPlane3D movedGeoPlane;
	protected CoordSys movedGeoPlaneStartCoordSys;
	private Coords movedGeoStartPosition;

	private Coords tmpCoords3 = new Coords(4);

	protected ArrayList<GeoPointND> stickyPointsList;

	/**
	 * constructor
	 * 
	 * @param ec
	 *            controller
	 */
	public EuclidianControllerInput3DCompanion(EuclidianController ec) {
		super(ec);
	}

	public void setInput3D(Input3D input3D) {
		this.input3D = input3D;
	}

	private EuclidianViewInput3DCompanion getViewCompanion() {
		return (EuclidianViewInput3DCompanion) ec.getView().getCompanion();
	}

	@Override
	protected GeoPoint3D createNewFreePoint(boolean complex) {

		if (input3D.currentlyUseMouse2D()) {
			return super.createNewFreePoint(complex);
		}

		GeoCursor3D point3D = ((EuclidianView3D) ec.getView()).getCursor3D();
		point3D.setIsCaptured(false);
		point3D.setPath(null);
		point3D.setRegion(null);

		if (input3D.hasMouseDirection()) {
			tmpCoords3.set(0);
			tmpCoords3.setW(1);
			double beamLength;
			if (ec.getMode() == EuclidianConstants.MODE_MOVE) {
				beamLength = 1000;
			} else {
				beamLength = 400;
			}
			beamLength /= ((EuclidianView3D) ec.getView()).getScale();
			getViewCompanion()
					.getStylusBeamEnd(tmpCoords3,
					beamLength);
			getView().setZNearest(-beamLength);
		} else {
			((EuclidianView3D) ec.getView()).getPickPoint(ec.getMouseLoc(),
					tmpCoords3);
			((EuclidianView3D) ec.getView()).toSceneCoords3D(tmpCoords3);
		}
		if (checkPointCapturingXYThenZ(tmpCoords3)) {
			point3D.setIsCaptured(true);
		}
		point3D.setCoords(tmpCoords3);

		return point3D;
	}

	@Override
	public void movePoint(AbstractEvent event) {

		if (input3D.currentlyUseMouse2D() || (input3D
						.hasMouseDirection() && !ec.movedGeoPoint
						.isIndependent())) {
			super.movePoint(event);
		} else {
			Coords v = new Coords(4);
			if (input3D.hasMouseDirection()) {
				getViewCompanion().getStylusBeamEnd(v, startZNearest);
				v.setSub(v, movedGeoPointStartCoords);
			} else {
				v.set(input3D.getMouse3DPosition()
						.sub(input3D.getStartMouse3DPosition()));
				((EuclidianView3D) ec.getView()).toSceneCoords3D(v);
			}

			Coords coords = movedGeoPointStartCoords.add(v);
			checkPointCapturingXYThenZ(coords);
			ec.movedGeoPoint.setCoords(coords, true);
			ec.movedGeoPoint.updateCascade();

			if (ec.getMoveMode() == EuclidianController.MOVE_POINT
					&& ec.movedGeoPoint.isGeoElement3D()
					&& !ec.movedGeoPoint.isPointOnPath()
					&& !ec.movedGeoPoint.hasRegion()) {
				// update point decorations
				((EuclidianView3D) ec.getView()).updatePointDecorations();
			}

			if (input3D.hasCompletedGrabbingDelay()) {
				long time = System.currentTimeMillis();
				StationaryCoords stationaryCoords = getViewCompanion()
						.getStationaryCoords();
				stationaryCoords.setCoords(
						ec.movedGeoPoint.getInhomCoordsInD3(), time);
				if (stationaryCoords.hasLongDelay(time)) {
					releaseGrabbing();
				}
			}
		}

	}

	private static class StickyPoint implements Comparable<StickyPoint> {
		public GeoPointND point;
		public double distance;

		public StickyPoint(GeoPointND point, double distance) {
			this.point = point;
			this.distance = distance;
		}

		public double getDistanceAbs() {
			return Math.abs(distance);
		}

		@Override
		public int compareTo(StickyPoint sp) {

			// check distance
			if (DoubleUtil.isGreater(sp.getDistanceAbs(), this.getDistanceAbs())) {
				return -1;
			}

			if (DoubleUtil.isGreater(this.getDistanceAbs(), sp.getDistanceAbs())) {
				return 1;
			}

			// check construction index
			if (this.point.getConstructionIndex() < sp.point
					.getConstructionIndex()) {
				return -1;
			}

			if (this.point.getConstructionIndex() > sp.point
					.getConstructionIndex()) {
				return 1;
			}

			return 0;

		}

		@Override
		public boolean equals(Object sp) {
			if (sp instanceof StickyPoint) {
				return compareTo((StickyPoint) sp) == 0;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return DoubleUtil.hashCode(distance) ^ point.hashCode();
		}

	}

	private static class StickyPointForDirection
			implements
			Comparable<StickyPointForDirection> {
		public StickyPoint sp;
		public double distanceOrtho;
		public double distanceOrigin;

		public StickyPointForDirection(StickyPoint origin, StickyPoint sp,
				double distanceOrigin) {
			this.sp = sp;
			this.distanceOrtho = sp.distance - origin.distance;
			this.distanceOrigin = distanceOrigin;
		}

		public double getCosAbs() {
			return Math.abs(distanceOrtho / distanceOrigin);
		}

		@Override
		public int compareTo(StickyPointForDirection spd) {

			// compare cosinus
			if (DoubleUtil.isGreater(Math.abs(spd.distanceOrtho * distanceOrigin),
					Math.abs(distanceOrtho * spd.distanceOrigin))) {
				return -1;
			}

			if (DoubleUtil.isGreater(Math.abs(distanceOrtho * spd.distanceOrigin), Math
					.abs(spd.distanceOrtho * distanceOrigin))) {
				return 1;
			}

			// check construction index
			if (this.sp.point.getConstructionIndex() < spd.sp.point
					.getConstructionIndex()) {
				return -1;
			}

			if (this.sp.point.getConstructionIndex() > spd.sp.point
					.getConstructionIndex()) {
				return 1;
			}

			return 0;

		}
		
		@Override
		public boolean equals(Object spd) {
			if (spd instanceof StickyPointForDirection) {
				return compareTo((StickyPointForDirection) spd) == 0;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return DoubleUtil.hashCode(distanceOrtho)
					^ DoubleUtil.hashCode(distanceOrigin) ^ sp.hashCode();
		}

	}

	private boolean stickToPoints() {
		return ec.getView()
				.getPointCapturingMode() == EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC;
	}

	@Override
	protected void movePlane(boolean repaint, AbstractEvent event) {

		if (input3D.currentlyUseMouse2D()) {
			super.movePlane(repaint, event);
		} else {
			Coords v = new Coords(4);
			if (input3D.hasMouseDirection() && !input3D
							.currentlyUseMouse2D()) {
				getViewCompanion().getStylusBeamEnd(v, startZNearest);
				v.setSub(v, movedGeoPointStartCoords);
			} else {
				v.set(input3D.getMouse3DPosition()
						.sub(input3D.getStartMouse3DPosition()));
				((EuclidianView3D) ec.getView()).toSceneCoords3D(v);
			}

			final GeoPlane3D plane = movedGeoPlane;

			plane.setCoordSys(movedGeoPlaneStartCoordSys);

			input3D.calcCurrentRot();
			plane.rotate(input3D.getCurrentRotMatrix(),
					movedGeoPointStartCoords);

			plane.translate(v);

			if (stickToPoints()) {
				// check sticky points
				if (stickyPoints == null) {
					stickyPoints = new TreeSet<>();
				} else {
					stickyPoints.clear();
				}

				for (GeoPointND point : stickyPointsList) {
					StickyPoint sp = new StickyPoint(point,
							plane.distanceWithSign(point));
					stickyPoints.add(sp);
				}

				double scale = ((EuclidianView3D) ec.getView()).getScale();
				Coords origin = null, secondPoint = null, thirdPoint = null;
				int step = 0;

				if (!stickyPoints.isEmpty()) {
					StickyPoint sp = stickyPoints.pollFirst();
					if (checkDistanceToStickyPoint(sp.getDistanceAbs(), scale,
							DISTANCE_THRESHOLD)) {
						origin = sp.point.getInhomCoordsInD3();
						step++;

						// Log.debug("============== " + sp.point);

						// check directions
						if (!stickyPoints.isEmpty()) {

							if (stickyPointsForDirection == null) {
								stickyPointsForDirection = new TreeSet<>();
							} else {
								stickyPointsForDirection.clear();
							}

							for (StickyPoint sp2 : stickyPoints) {
								double distanceOrigin = sp2.point
										.distance(sp.point);
								// prevent same points
								if (!DoubleUtil.isZero(distanceOrigin)) {
									stickyPointsForDirection
											.add(new StickyPointForDirection(
													sp, sp2, distanceOrigin));
								}
							}

							if (!stickyPointsForDirection.isEmpty()) {
								StickyPointForDirection spd2 = stickyPointsForDirection
										.pollFirst();
								// Log.debug("spd2 : " + spd2.getCosAbs());
								if (spd2.getCosAbs() < COS_THRESHOLD) {
									secondPoint = spd2.sp.point
											.getInhomCoordsInD3();
									step++;

									if (!stickyPointsForDirection.isEmpty()) {
										StickyPointForDirection spd3 = stickyPointsForDirection
												.pollFirst();
										// Log.debug("spd3 : " +
										// spd3.getCosAbs());
										if (spd3.getCosAbs() < COS_THRESHOLD) {
											thirdPoint = spd3.sp.point
													.getInhomCoordsInD3();
											step++;

										}
									}
								}
							}
						}

					} else {
						step = -1;
						// Log.error("TOO FAR (first point)");
					}

					switch (step) {
					default:
						// do nothing
						break;
					case 1: // only origin
						plane.getCoordSys().updateToContainPoint(origin);
						break;
					case 2: // origin and second point
						plane.getCoordSys().updateContinuousPointVx(origin,
								secondPoint.sub(origin));
						break;
					case 3: // origin and two points
						CoordSys cs = new CoordSys(2);
						cs.addPoint(origin);
						cs.addPoint(secondPoint);
						cs.addPoint(thirdPoint);
						if (cs.isMadeCoordSys()) {
							cs.makeOrthoMatrix(false, false);
							cs.makeEquationVector();
							plane.getCoordSys().updateContinuous(cs);
						} else {
							plane.getCoordSys().updateContinuousPointVx(origin,
									secondPoint.sub(origin));
						}
						break;
					}
				}
			}

			// update
			plane.setDefinition(null);
			plane.updateCascade();

			if (input3D.hasCompletedGrabbingDelay()) {

				long time = System.currentTimeMillis();
				StationaryCoords stationaryCoords = getViewCompanion()
						.getStationaryCoords();
				stationaryCoords.setCoords(movedGeoPointStartCoords, v, time);
				if (stationaryCoords.hasLongDelay(time)) {
					releaseGrabbing();
				}
			}
		}
	}

	/**
	 * release current grabbing
	 */
	public void releaseGrabbing() {
		input3D.setHasCompletedGrabbingDelay(false);
		ec.getApplication().getSelectionManager().clearSelectedGeos(true);
		ec.endOfWrapMouseReleased(new Hits(), false, false, false,
				PointerEventType.TOUCH);
	}

	private static boolean checkDistanceToStickyPoint(double d, double scale,
			int threshold) {
		return d * scale < DrawPoint.getSelectionThreshold(threshold); // point.getPointSize()
																		// +
																		// threshold;
	}

	@Override
	protected boolean specificPointCapturingAutomatic() {
		return ((EuclidianController3D) ec).isZSpace()
				&& !input3D.currentlyUseMouse2D();
	}

	@Override
	protected void updateMovedGeoPointStartValues(Coords coords,
			GeoPointND movedGeoPoint, CoordMatrix4x4 currentPlane) {
		if (input3D.currentlyUseMouse2D()) {
			super.updateMovedGeoPointStartValues(coords, movedGeoPoint,
					currentPlane);
		} else {
			movedGeoPointStartCoords.set(coords);
			if (input3D.hasMouseDirection()) {
				startZNearest = getViewCompanion().getZNearest();
			}
		}
	}

	/**
	 * set plane to move
	 * 
	 * @param geo
	 *            moved geo
	 */
	public void setMovedGeoPlane(GeoElement geo) {

		movedGeoPlane = (GeoPlane3D) geo;

		if (movedGeoPlaneStartCoordSys == null) {
			movedGeoPlaneStartCoordSys = new CoordSys(2);
		}
		movedGeoPlaneStartCoordSys.set(movedGeoPlane.getCoordSys());

		if (movedGeoStartPosition == null) {
			movedGeoStartPosition = new Coords(4);
		}
		movedGeoStartPosition.set(input3D.getMouse3DPosition());

		((EuclidianController3D) ec).updateMovedGeoPointStartValues(
				getView().getCursor3D().getInhomCoordsInD(3));

		getView().setDragCursor();

		// set sticky points
		if (stickyPointsList == null) {
			stickyPointsList = new ArrayList<>();
		} else {
			stickyPointsList.clear();
		}

		for (GeoElement geo1 : geo.getConstruction()
				.getGeoSetConstructionOrder()) {
			if (geo1.isGeoPoint() && geo1.isVisibleInView3D()
					&& !geo1.isChildOf(geo)) {
				stickyPointsList.add((GeoPointND) geo1);
			}
		}

	}

	@Override
	final protected boolean handleMovedElementFreePlane(
			GeoElement movedGeoElement) {
		if (movedGeoElement.isGeoPlane()) {
			setMovedGeoPlane(movedGeoElement);
			return true;
		}

		return false;
	}

	@Override
	public void setMouseLocation(AbstractEvent event) {
		if (input3D.currentlyUseMouse2D()) {
			super.setMouseLocation(event);
		} else {
			ec.mouseLoc = event.getPoint();
		}
	}

	@Override
	protected void setMouseOrigin(GeoPoint3D point, GPoint mouseLoc) {

		if (input3D.hasMouseDirection()
				&& !input3D.currentlyUseMouse2D()) {
			point.setWillingCoords(input3D.getMouse3DScenePosition());
		} else {
			super.setMouseOrigin(point, mouseLoc);
		}
	}

	@Override
	public double getPointCapturingPercentage() {
		return 2 * super.getPointCapturingPercentage();
	}
}
