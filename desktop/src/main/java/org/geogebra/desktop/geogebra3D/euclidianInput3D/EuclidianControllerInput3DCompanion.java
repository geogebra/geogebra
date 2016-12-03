package org.geogebra.desktop.geogebra3D.euclidianInput3D;

import java.util.TreeSet;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.draw.DrawPoint;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3DCompanion;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.Matrix.Quaternion;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.geogebra3D.euclidianInput3D.EuclidianViewInput3D.StationaryCoords;

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

	/**
	 * constructor
	 * 
	 * @param ec
	 *            controller
	 */
	public EuclidianControllerInput3DCompanion(EuclidianController ec) {
		super(ec);
	}


	@Override
	protected GeoPoint3D createNewFreePoint(boolean complex) {

		if (((EuclidianControllerInput3D) ec).input3D.currentlyUseMouse2D()) {
			return super.createNewFreePoint(complex);
		}

		GeoPoint3D point3D = ((EuclidianView3D) ec.view).getCursor3D();
		point3D.setPath(null);
		point3D.setRegion(null);

		Coords coords;
		if (((EuclidianControllerInput3D) ec).input3D.hasMouseDirection()) {
			coords = Coords.createInhomCoorsInD3();
			double beamLength;
			if (ec.getMode() == EuclidianConstants.MODE_MOVE) {
				beamLength = 1000;
			} else {
				beamLength = 400;
			}
			beamLength /= ((EuclidianView3D) ec.view).getScale();
			((EuclidianViewInput3D) ec.view).getStylusBeamEnd(coords,
					beamLength);
			((EuclidianViewInput3D) ec.view).setZNearest(-beamLength);
		} else {
			coords = ((EuclidianView3D) ec.view).getPickPoint(ec.getMouseLoc())
					.copyVector();
			((EuclidianView3D) ec.view).toSceneCoords3D(coords);
		}
		checkPointCapturingXYThenZ(coords);
		point3D.setCoords(coords);

		return point3D;
	}

	@Override
	public void movePoint(boolean repaint, AbstractEvent event) {

		if (((EuclidianControllerInput3D) ec).input3D.currentlyUseMouse2D()
				|| (((EuclidianControllerInput3D) ec).input3D
						.hasMouseDirection() && !ec.movedGeoPoint
						.isIndependent())) {
			super.movePoint(repaint, event);
		} else {
			Coords v = new Coords(4);
			if (((EuclidianControllerInput3D) ec).input3D.hasMouseDirection()) {
				((EuclidianViewInput3D) ec.view).getStylusBeamEnd(v,
						((EuclidianControllerInput3D) ec).startZNearest);
				v.setSub(
						v,
						((EuclidianControllerInput3D) ec).movedGeoPointStartCoords);
			} else {
				v.set(((EuclidianControllerInput3D) ec).mouse3DPosition
						.sub(((EuclidianControllerInput3D) ec).startMouse3DPosition));
				((EuclidianView3D) ec.view).toSceneCoords3D(v);
			}

			Coords coords = ((EuclidianControllerInput3D) ec).movedGeoPointStartCoords
					.add(v);
			checkPointCapturingXYThenZ(coords);
			ec.movedGeoPoint.setCoords(coords, true);
			ec.movedGeoPoint.updateCascade();

			if (ec.getMoveMode() == EuclidianController.MOVE_POINT
					&& ec.movedGeoPoint.isGeoElement3D()
					&& !ec.movedGeoPoint.hasPath()
					&& !ec.movedGeoPoint.hasRegion()) {
				// update point decorations
				((EuclidianView3D) ec.view).updatePointDecorations();
			}

			if (((EuclidianControllerInput3D) ec).input3D.hasCompletedGrabbingDelay()) {
				long time = System.currentTimeMillis();
				StationaryCoords stationaryCoords = ((EuclidianViewInput3D) ec.view)
						.getStationaryCoords();
				stationaryCoords.setCoords(
						ec.movedGeoPoint.getInhomCoordsInD3(), time);
				if (stationaryCoords.hasLongDelay(time)) {
					releaseGrabbing();
				}
			}
		}

	}

	private StationaryQuaternion stationaryQuaternion = new StationaryQuaternion();

	private static class StationaryQuaternion {

		private Quaternion startCoords = new Quaternion();
		private long startTime;

		public StationaryQuaternion() {
			startCoords.setUndefined();
		}

		public void setQuaternion(Quaternion q, long time) {

			if (startCoords.isDefined()) {
				double distance = startCoords.distance(q);
				// Log.debug("\n -- "+(distance * ((EuclidianView3D)
				// ec.view).getScale()));
				if (distance > 0.05) { // angle < 25.8degrees
					startCoords.set(q);
					startTime = time;
					// Log.debug("\n -- startCoords =\n"+startCoords);
				} else {
					// Log.debug("\n -- same coords "+(time-startTime));
				}
			} else {
				startCoords.set(q);
				startTime = time;
				// Log.debug("\n -- startCoords =\n"+startCoords);
			}
		}

		/**
		 * 
		 * @param time
		 *            current time
		 * @return true if hit was long enough to process left release
		 */
		public boolean hasLongDelay(long time) {

			if (startCoords.isDefined()) {
				int delay = (int) ((time - startTime) / 100);
				String s = "";
				for (int i = 0; i < 10 - delay; i++) {
					s += "=";
				}
				for (int i = 10 - delay; i <= 10; i++) {
					s += " ";
				}
				s += "|";
				Log.error("\n rot delay : " + s);
				if ((time - startTime) > 1000) {
					startCoords.setUndefined(); // consume event
					return true;
				}
			}

			return false;
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

		public int compareTo(StickyPoint sp) {

			// check distance
			if (this.getDistanceAbs() < sp.getDistanceAbs()) {
				return -1;
			}

			if (this.getDistanceAbs() > sp.getDistanceAbs()) {
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

		public int compareTo(StickyPointForDirection spd) {

			// compare cosinus
			if (Math.abs(distanceOrtho * spd.distanceOrigin) < Math
					.abs(spd.distanceOrtho * distanceOrigin)) {
				return -1;
			}

			if (Math.abs(distanceOrtho * spd.distanceOrigin) > Math
					.abs(spd.distanceOrtho * distanceOrigin)) {
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

	}

	private TreeSet<StickyPoint> stickyPoints;
	private TreeSet<StickyPointForDirection> stickyPointsForDirection;

	private boolean stickToPoints() {
		return ec.view.getPointCapturingMode() == EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC;
	}

	@Override
	protected void movePlane(boolean repaint, AbstractEvent event) {

		if (((EuclidianControllerInput3D) ec).input3D.currentlyUseMouse2D()) {
			super.movePlane(repaint, event);
		} else {
			Coords v = new Coords(4);
			if (((EuclidianControllerInput3D) ec).input3D.hasMouseDirection()
					&& !((EuclidianControllerInput3D) ec).input3D
							.currentlyUseMouse2D()) {
				((EuclidianViewInput3D) ec.view).getStylusBeamEnd(v,
						((EuclidianControllerInput3D) ec).startZNearest);
				v.setSub(
						v,
						((EuclidianControllerInput3D) ec).movedGeoPointStartCoords);
			} else {
				v.set(((EuclidianControllerInput3D) ec).mouse3DPosition
						.sub(((EuclidianControllerInput3D) ec).startMouse3DPosition));
				((EuclidianView3D) ec.view).toSceneCoords3D(v);
			}

			final GeoPlane3D plane = ((EuclidianControllerInput3D) ec).movedGeoPlane;

			plane.setCoordSys(((EuclidianControllerInput3D) ec).movedGeoPlaneStartCoordSys);

			((EuclidianControllerInput3D) ec).calcCurrentRot();
			plane.rotate(
					((EuclidianControllerInput3D) ec).getCurrentRotMatrix(),
					((EuclidianControllerInput3D) ec).movedGeoPointStartCoords);

			plane.translate(v);


			if (stickToPoints()) {
				// check sticky points
				if (stickyPoints == null) {
					stickyPoints = new TreeSet<StickyPoint>();
				} else {
					stickyPoints.clear();
				}

				for (GeoPointND point : ((EuclidianControllerInput3D) ec).stickyPoints) {
					StickyPoint sp = new StickyPoint(point,
							plane.distanceWithSign(point));
					stickyPoints.add(sp);
				}

				double scale = ((EuclidianView3D) ec.view).getScale();
				Coords origin = null, secondPoint = null, thirdPoint = null;
				int step = 0;

				if (!stickyPoints.isEmpty()) {
					StickyPoint sp = stickyPoints.pollFirst();
					if (checkDistanceToStickyPoint(sp.getDistanceAbs(),
							sp.point, scale, DISTANCE_THRESHOLD)) {
						origin = sp.point.getInhomCoordsInD3();
						step++;

						// Log.debug("============== " + sp.point);

						// check directions
						if (!stickyPoints.isEmpty()) {

							if (stickyPointsForDirection == null) {
								stickyPointsForDirection = new TreeSet<StickyPointForDirection>();
							} else {
								stickyPointsForDirection.clear();
							}

							for (StickyPoint sp2 : stickyPoints) {
								double distanceOrigin = sp2.point
										.distance(sp.point);
								// prevent same points
								if (!Kernel.isZero(distanceOrigin)) {
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


			if (((EuclidianControllerInput3D) ec).input3D.hasCompletedGrabbingDelay()) {

				long time = System.currentTimeMillis();
				StationaryCoords stationaryCoords = ((EuclidianViewInput3D) ec.view)
						.getStationaryCoords();
				stationaryCoords
						.setCoords(
								((EuclidianControllerInput3D) ec).movedGeoPointStartCoords,
								v, time);
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
		((EuclidianControllerInput3D) ec).input3D
				.setHasCompletedGrabbingDelay(false);
		ec.getApplication().getSelectionManager().clearSelectedGeos(true);
		ec.endOfWrapMouseReleased(new Hits(), false, false,
				PointerEventType.TOUCH);
	}

	private Coords tmpCoordsInput3D1, tmpCoordsInput3D2, tmpCoordsInput3D3,
			tmpCoordsInput3D4;

	private static boolean checkDistanceToStickyPoint(double d,
			GeoPointND point, double scale, int threshold) {
		return d * scale < DrawPoint.getSelectionThreshold(threshold);// point.getPointSize()
																		// +
																		// threshold;
	}

	@Override
	protected boolean specificPointCapturingAutomatic() {
		return ((EuclidianController3D) ec).isZSpace()
				&& !((EuclidianControllerInput3D) ec).input3D
						.currentlyUseMouse2D();
	}
}
