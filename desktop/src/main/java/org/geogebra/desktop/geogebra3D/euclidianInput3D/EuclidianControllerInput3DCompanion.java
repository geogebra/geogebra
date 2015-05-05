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

	private TreeSet<StickyPoint> stickyPoints;

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
			int threshold = 6;// ((EuclidianView3D)
								// ec.view).getCapturingThreshold(PointerEventType.MOUSE);
			// App.debug(""+threshold);
			CoordSys coordsys = new CoordSys(2);
			boolean isMadeCoordSys = false;
			double lastDistance = 0;
			for (StickyPoint sp : stickyPoints) {
				// App.debug("\n"+sp.point);
				if (!isMadeCoordSys
						&& !checkDistanceToStickyPoint(sp.getDistanceAbs(),
								sp.point, scale, threshold)) {
					// App.error("TOO FAR");
					break;
				}

				if (isMadeCoordSys) {
					// coordsys is made, check if we are far enough to next
					// point (to avoid plane jumping)
					Coords coords = sp.point.getInhomCoordsInD3();
					if (tmpCoordsInput3D1 == null) {
						tmpCoordsInput3D1 = new Coords(4);
					}
					coords.projectPlaneInPlaneCoords(
							coordsys.getMatrixOrthonormal(), tmpCoordsInput3D1);
					if (!Kernel.isZero(tmpCoordsInput3D1.getZ())) { // don't
																	// check
																	// this
																	// point if
																	// on
																	// current
																	// coord sys
						if (sp.distance * lastDistance < 0) { // don't check
																// this point if
																// is on same
																// side
							if (-sp.distance / lastDistance < 3) { // the point
																	// is too
																	// close:
																	// plane may
																	// jump
								// App.error("\n-- TOO CLOSE: "+sp.point+"\n"+sp.distance+"\n"+lastDistance);
								isMadeCoordSys = false;
							} else {
								// App.error("== FAR ENOUGH");
							}
							break;
						}

					}
				} else {
					coordsys.addPoint(sp.point.getInhomCoordsInD3());
					if (coordsys.isMadeCoordSys()) {
						// App.error("END");
						coordsys.makeOrthoMatrix(false, false);
						coordsys.makeEquationVector();
						isMadeCoordSys = true;
						lastDistance = sp.distance;
						// break;
					}
				}
			}

			if (isMadeCoordSys) {
				plane.getCoordSys().updateContinuous(coordsys);
			}

			// update
			plane.updateCascade();

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
