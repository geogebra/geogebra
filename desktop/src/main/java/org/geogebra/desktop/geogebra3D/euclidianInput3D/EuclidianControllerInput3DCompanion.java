package org.geogebra.desktop.geogebra3D.euclidianInput3D;

import java.util.TreeSet;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.draw.DrawPoint;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3DCompanion;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.Matrix.Quaternion;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
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
	static final private double COS_THRESHOLD = Math.sin(Math.PI * 5 / 180);

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

		Coords coords = ((EuclidianView3D) ec.view).getPickPoint(
				ec.getMouseLoc()).copyVector();
		((EuclidianView3D) ec.view).toSceneCoords3D(coords);
		checkPointCapturingXYThenZ(coords);
		point3D.setCoords(coords);

		return point3D;
	}

	@Override
	public void movePoint(boolean repaint, AbstractEvent event) {

		if (((EuclidianControllerInput3D) ec).input3D.hasMouseDirection()
				|| ((EuclidianControllerInput3D) ec).input3D
						.currentlyUseMouse2D()) {
			super.movePoint(repaint, event);
		} else {
			Coords v = new Coords(4);
			v.set(((EuclidianControllerInput3D) ec).mouse3DPosition
					.sub(((EuclidianControllerInput3D) ec).startMouse3DPosition));
			((EuclidianView3D) ec.view).toSceneCoords3D(v);

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
				((EuclidianView3D) ec.view)
						.updatePointDecorations((GeoPoint3D) ec.movedGeoPoint);
			}

			if (((EuclidianControllerInput3D) ec).input3D.getLeftButton()) {
				long time = System.currentTimeMillis();
				StationaryCoords stationaryCoords = ((EuclidianViewInput3D) ec.view)
						.getStationaryCoords();
				stationaryCoords.setCoords(
						ec.movedGeoPoint.getInhomCoordsInD3(), time);
				if (stationaryCoords.hasLongDelay(time)) {
					((EuclidianControllerInput3D) ec).input3D
							.setLeftButtonPressed(false);
				}
			}
		}

	}

	private StationaryQuaternion stationaryQuaternion = new StationaryQuaternion();

	private class StationaryQuaternion {

		private Quaternion startCoords = new Quaternion();
		private long startTime;

		public StationaryQuaternion() {
			startCoords.setUndefined();
		}

		public void setQuaternion(Quaternion q, long time) {

			if (startCoords.isDefined()) {
				double distance = startCoords.distance(q);
				// App.debug("\n -- "+(distance * ((EuclidianView3D)
				// ec.view).getScale()));
				if (distance > 0.05) { // angle < 25.8degrees
					startCoords.set(q);
					startTime = time;
					// App.debug("\n -- startCoords =\n"+startCoords);
				} else {
					// App.debug("\n -- same coords "+(time-startTime));
				}
			} else {
				startCoords.set(q);
				startTime = time;
				// App.debug("\n -- startCoords =\n"+startCoords);
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
				App.error("\n rot delay : " + s);
				if ((time - startTime) > 1000) {
					startCoords.setUndefined(); // consume event
					return true;
				}
			}

			return false;
		}
	}

	private class StickyPoint implements Comparable<StickyPoint> {
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

	private class StickyPointForDirection implements
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

	@Override
	protected void movePlane(boolean repaint, AbstractEvent event) {

		if (((EuclidianControllerInput3D) ec).input3D.currentlyUseMouse2D()) {
			super.movePlane(repaint, event);
		} else {
			Coords v = new Coords(4);
			v.set(((EuclidianControllerInput3D) ec).mouse3DPosition
					.sub(((EuclidianControllerInput3D) ec).startMouse3DPosition));
			((EuclidianView3D) ec.view).toSceneCoords3D(v);

			final GeoPlane3D plane = ((EuclidianControllerInput3D) ec).movedGeoPlane;

			plane.setCoordSys(((EuclidianControllerInput3D) ec).movedGeoPlaneStartCoordSys);

			((EuclidianControllerInput3D) ec).calcCurrentRot();
			plane.rotate(
					((EuclidianControllerInput3D) ec).getCurrentRotMatrix(),
					((EuclidianControllerInput3D) ec).movedGeoPointStartCoords);

			plane.translate(v);

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
				if (checkDistanceToStickyPoint(sp.getDistanceAbs(), sp.point,
						scale, DISTANCE_THRESHOLD)) {
					origin = sp.point.getInhomCoordsInD3();
					step++;

					// App.debug("============== " + sp.point);

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
										.add(new StickyPointForDirection(sp,
												sp2, distanceOrigin));
							}
						}

						// for (StickyPointForDirection spd :
						// stickyPointsForDirection) {
						// App.debug("" + spd.sp.point);
						// }

						App.debug("" + COS_THRESHOLD);

						if (!stickyPointsForDirection.isEmpty()) {
							StickyPointForDirection spd2 = stickyPointsForDirection
									.pollFirst();
							if (spd2.getCosAbs() < COS_THRESHOLD) {
								App.debug("spd2 : " + spd2.getCosAbs());
								secondPoint = spd2.sp.point
										.getInhomCoordsInD3();
								step++;

								if (!stickyPointsForDirection.isEmpty()) {
									StickyPointForDirection spd3 = stickyPointsForDirection
											.pollFirst();
									if (spd3.getCosAbs() < COS_THRESHOLD) {
										App.debug("spd3 : " + spd3.getCosAbs());
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
					App.error("TOO FAR (first point)");
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


				// update
				plane.updateCascade();

			}

			if (((EuclidianControllerInput3D) ec).input3D.getLeftButton()) {

				Coords p = Coords.createInhomCoorsInD3();
				p.setValues(((EuclidianControllerInput3D) ec).mouse3DPosition,
						3);
				((EuclidianView3D) ec.view).toSceneCoords3D(p);

				long time = System.currentTimeMillis();
				StationaryCoords stationaryCoords = ((EuclidianViewInput3D) ec.view)
						.getStationaryCoords();
				stationaryCoords.setCoords(p, time);
				if (stationaryCoords.hasLongDelay(time)) {
					((EuclidianControllerInput3D) ec).input3D
							.setLeftButtonPressed(false);
				}
			}

		}
	}

	private Coords tmpCoordsInput3D1, tmpCoordsInput3D2, tmpCoordsInput3D3,
			tmpCoordsInput3D4;

	private static boolean checkDistanceToStickyPoint(double d,
			GeoPointND point, double scale, int threshold) {
		return d * scale < DrawPoint.getSelectionThreshold(threshold);// point.getPointSize()
																		// +
																		// threshold;
	}

}
