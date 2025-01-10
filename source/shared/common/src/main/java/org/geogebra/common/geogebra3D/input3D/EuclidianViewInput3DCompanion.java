package org.geogebra.common.geogebra3D.input3D;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GPointWithZ;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianCursor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3DCompanion;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawSegment3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterCompletingCursor;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterCursor;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.input3D.Input3D.OutOfField;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3DConstant;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Companion for EuclidianView3D using Input3D
 *
 */
public class EuclidianViewInput3DCompanion extends EuclidianView3DCompanion {

	private Input3D input3D;

	private Coords completingCursorOrigin;

	private Coords mouse3DScreenPosition = null;
	private Coords mouse3DScenePosition;

	protected CoordMatrix4x4 tmpMatrix4x4_3 = CoordMatrix4x4.identity();
	private GeoSegment3D stylusBeam;
	private DrawSegment3D stylusBeamDrawable;
	boolean stylusBeamIsVisible;

	static private int STYLUS_BEAM_THICKNESS = 9;

	private HittedGeo hittedGeo = new HittedGeo();

	private StationaryCoords stationaryCoords = new StationaryCoords();

	private Coords tmpCoords1 = new Coords(4);

	final static protected float LONG_DELAY = 1500f;

	final private static double GRAY_SCALE_FOR_INPUT3D = 255 * 0.75;

	/**
	 * @param view
	 *            3D view
	 */
	public EuclidianViewInput3DCompanion(EuclidianView view) {
		super(view);

		mouse3DScenePosition = new Coords(4);
		mouse3DScenePosition.setW(1);
	}

	/**
	 * @param input3D
	 *            3D input
	 */
	public void setInput3D(Input3D input3D) {
		this.input3D = input3D;
		if (input3D.useCompletingDelay()) {
			completingCursorOrigin = Coords.createInhomCoorsInD3();
		}
	}

	@Override
	public void drawMouseCursor(Renderer renderer1) {
		if (input3D.currentlyUseMouse2D()) {
			super.drawMouseCursor(renderer1);
			return;
		}

		if (input3D.hasMouseDirection()) {
			return;
		}

		// use a 3D mouse position
		mouse3DScreenPosition = input3D.getMouse3DPosition();

		if (input3D.useCompletingDelay()) {
			if (drawCompletingCursor(renderer1)) {
				return;
			}

			// draw mouse cursor at the same place
			renderer1.drawMouseCursor();
			return;

		}

		getView().drawMouseCursor(renderer1, mouse3DScreenPosition);
	}

	/**
	 * @param renderer1
	 *            renderer
	 * @return false if we need also the mouse cursor
	 */
	private boolean drawCompletingCursor(Renderer renderer1) {

		// are we grabbing?
		float hittedGeoCompletingDelay = hittedGeo.getCompletingDelay();
		if (hittedGeoCompletingDelay > PlotterCompletingCursor.START_DRAW
				&& hittedGeoCompletingDelay <= PlotterCompletingCursor.END_DRAW) {
			CoordMatrix4x4.identity(tmpMatrix4x4_3);
			completingCursorOrigin
					.setValues(getView().getCursor3D().getInhomCoordsInD3(), 3);
			getView().toScreenCoords3D(completingCursorOrigin);
			return drawCompletingCursor(renderer1, completingCursorOrigin,
					hittedGeoCompletingDelay);
		}

		// are we releasing?
		float stationaryCoordsCompletingDelay = stationaryCoords
				.getCompletingDelay();
		if (stationaryCoordsCompletingDelay > PlotterCompletingCursor.START_DRAW
				&& stationaryCoordsCompletingDelay <= PlotterCompletingCursor.END_DRAW) {
			CoordMatrix4x4.identity(tmpMatrix4x4_3);
			completingCursorOrigin
					.setValues(stationaryCoords.getCurrentCoords(), 3);
			getView().toScreenCoords3D(completingCursorOrigin);
			drawCompletingCursor(renderer1, completingCursorOrigin,
					1 - stationaryCoordsCompletingDelay);
			return true;
		}

		// are we moving?
		if (stationaryCoordsCompletingDelay >= 0
				&& stationaryCoordsCompletingDelay <= PlotterCompletingCursor.START_DRAW) {
			CoordMatrix4x4.identity(tmpMatrix4x4_3);
			completingCursorOrigin
					.setValues(stationaryCoords.getCurrentCoords(), 3);
			getView().toScreenCoords3D(completingCursorOrigin);
			drawCompletingCursor(renderer1, completingCursorOrigin, 1);
			return true;
		}

		// are we over a moveable geo?
		if (hittedGeo.getGeo() != null
				&& hittedGeoCompletingDelay <= PlotterCompletingCursor.START_DRAW) {
			CoordMatrix4x4.identity(tmpMatrix4x4_3);
			completingCursorOrigin
					.setValues(getView().getCursor3D().getInhomCoordsInD3(), 3);
			getView().toScreenCoords3D(completingCursorOrigin);
			return drawCompletingCursor(renderer1, completingCursorOrigin, 0);
		}

		// nothing hitted
		completingCursorOrigin.setValues(mouse3DScreenPosition, 3);
		return drawCompletingCursor(renderer1, completingCursorOrigin, 0);

	}

	/**
	 * @return false if we need also the mouse cursor
	 */
	private boolean drawCompletingCursor(Renderer renderer1, Coords origin,
			float completingDelay) {

		switch (input3D.getOutOfField()) {
		case RIGHT:
			origin.setX(renderer1.getRight());
			origin.setY(0);
			origin.setZ(0);
			break;
		case LEFT:
			origin.setX(renderer1.getLeft());
			origin.setY(0);
			origin.setZ(0);
			break;
		case TOP:
			origin.setX(0);
			origin.setY(renderer1.getTop());
			origin.setZ(0);
			break;
		case BOTTOM:
			origin.setX(0);
			origin.setY(renderer1.getBottom());
			origin.setZ(0);
			break;
		case FAR:
			origin.setX(0);
			origin.setY(0);
			origin.setZ(renderer1.getFar());
			break;
		default:
		case NEAR:
			origin.setX(0);
			origin.setY(0);
			origin.setZ(renderer1.getNear());
			break;
		}

		// draw at the mouse location
		if (input3D.getOutOfField() == OutOfField.NO) {
			tmpMatrix4x4_3.setOrigin(origin);
			renderer1.setMatrix(tmpMatrix4x4_3);
			renderer1.drawCompletingCursor(completingDelay, false);
			return false;
		}

		// draw warner
		tmpMatrix4x4_3.setOrigin(origin);
		renderer1.setMatrix(tmpMatrix4x4_3);
		renderer1.drawCompletingCursor(completingDelay, true);
		return true;

		// Log.debug("" + input3D.getOutOfField());

	}

	@Override
	public void drawFreeCursor(Renderer renderer1) {

		if (input3D.currentlyUseMouse2D()) {
			super.drawFreeCursor(renderer1);
		} else {
			// free point in space
			renderer1.drawCursor(PlotterCursor.Type.CROSS3D);
		}
	}

	@Override
	public GeoElement getLabelHit(GPoint p, PointerEventType type) {
		if (input3D.currentlyUseMouse2D()) {
			return super.getLabelHit(p, type);
		}
		return null;
	}

	@Override
	public void setHits(PointerEventType type) {

		if (!input3D.currentlyUseMouse2D() && (input3D.isRightPressed()
				|| input3D.isThirdButtonPressed())) {
			return;
		}

		super.setHits(type);

		if (input3D.currentlyUseMouse2D()) {
			return;
		}

		// not moving a geo : see if user stays on the same hit to select it
		if (input3D.useCompletingDelay()
				&& getView().getEuclidianController()
						.getMoveMode() == EuclidianController.MOVE_NONE
				&& !input3D.hasCompletedGrabbingDelay()) {
			long time = System.currentTimeMillis();
			hittedGeo.setHitted(
					getView().getHits3D().getTopHits()
							.getFirstGeo6dofMoveable(),
					time, mouse3DScreenPosition);
			// reset hits
			GeoElement geoToHit = hittedGeo.getGeo();
			getView().getHits3D().init(geoToHit);
			getView().updateCursor3D(getView().getHits());
			getView().getApplication().setMode(EuclidianConstants.MODE_MOVE);
			if (hittedGeo.hasLongDelay(time)) {
				input3D.setHasCompletedGrabbingDelay(true);
				getView().getEuclidianController().handleMovedElement(geoToHit,
						false, PointerEventType.TOUCH);
			}
		}

	}

	@Override
	public boolean isMoveable(GeoElement geo) {

		if (input3D.currentlyUseMouse2D()) {
			return super.isMoveable(geo);
		}

		if (geo.isGeoPlane() && geo.isIndependent()
				&& !(geo instanceof GeoPlane3DConstant)) {
			return true;
		}

		return super.isMoveable(geo);
	}

	@Override
	public int getCapturingThreshold(PointerEventType type) {
		if (input3D.currentlyUseMouse2D()) {
			return super.getCapturingThreshold(type);
		}
		return 5 * super.getCapturingThreshold(type);
	}

	@Override
	public boolean hasMouse() {

		if (input3D.currentlyUseMouse2D()) {
			return super.hasMouse();
		}

		return input3D.hasMouse(getView());
	}

	@Override
	public void initAxisAndPlane() {

		if (input3D.hasMouseDirection()) {
			stylusBeam = new GeoSegment3D(
					getView().getKernel().getConstruction());
			stylusBeam.setCoord(Coords.O, Coords.VX);
			stylusBeam.setObjColor(GColor.GREEN);
			stylusBeam.setLineThickness(STYLUS_BEAM_THICKNESS);

			stylusBeamIsVisible = false;
			stylusBeamDrawable = new DrawSegment3D(getView(), stylusBeam) {
				@Override
				public boolean isVisible() {
					return stylusBeamIsVisible;
				}
			};
		}

	}

	@Override
	public void setZNearest(double zNear) {
		if (Double.isNaN(zNear)) {
			zNearest = 4;
		} else {
			zNearest = -zNear;
		}
		updateStylusBeam();
	}

	/**
	 * update stylus beam for moved geo
	 */
	@Override
	public void updateStylusBeamForMovedGeo() {

		if (getView().getEuclidianController()
				.getMoveMode() == EuclidianController.MOVE_NONE) {
			return;
		}

		if (getView().getEuclidianController()
				.getMoveMode() != EuclidianController.MOVE_PLANE) {
			getView().getCursor3D().setCoords(input3D.getMouse3DScenePosition(),
					false);
			GeoElement movedGeo = getView().getEuclidianController()
					.getMovedGeoElement();
			if (movedGeo != null) {
				zNearest = movedGeo.distance(getView().getCursor3D());
			}
		}

		updateStylusBeam();
	}

	private void updateStylusBeam() {
		if (input3D.hasMouseDirection() && !input3D.currentlyUseMouse2D()) {
			stylusBeam.setCoord(input3D.getMouse3DScenePosition(),
					input3D.getMouse3DDirection().mul(zNearest));
			stylusBeamDrawable.setWaitForUpdate();
			stylusBeamDrawable.update();
		}

	}

	/**
	 * set coords to stylus end for given length
	 * 
	 * @param coords
	 *            returned coords
	 * @param l
	 *            length
	 */
	public void getStylusBeamEnd(Coords coords, double l) {
		coords.setAdd(input3D.getMouse3DScenePosition(),
				coords.setMul(input3D.getMouse3DDirection(), l));
	}

	@Override
	public void resetAllVisualStyles() {
		if (input3D.hasMouseDirection()) {
			stylusBeamDrawable.setWaitForUpdateVisualStyle(null);
		}
	}

	@Override
	public void resetOwnDrawables() {
		if (input3D.hasMouseDirection()) {
			stylusBeamDrawable.setWaitForReset();
		}
	}

	@Override
	public void update() {
		if (input3D.hasMouseDirection()) {
			if (input3D.currentlyUseMouse2D()) {
				stylusBeamIsVisible = false;
			} else {
				if (input3D.isLeftPressed()) {
					// show stylus beam only if object is moved
					if (getView().getEuclidianController()
							.getMoveMode() == EuclidianController.MOVE_NONE) {
						stylusBeamIsVisible = false;
					} else {
						stylusBeamIsVisible = hasMouse();
					}
				} else if (input3D.isRightPressed()
						|| input3D.isThirdButtonPressed()) {
					stylusBeamIsVisible = false;
				} else {
					stylusBeamIsVisible = hasMouse();
				}
			}
			stylusBeamDrawable.update();
		}
	}

	@Override
	public void drawPointAlready(GeoPoint3D point) {

		if (input3D.currentlyUseMouse2D()) {
			super.drawPointAlready(point);
			return;
		}

		if (point.hasRegion()) {
			super.drawPointAlready(point);
		} else if (!point.isPointOnPath()
				&& point.getMoveMode() != GeoPointND.MOVE_MODE_NONE) {
			getView().getRenderer().drawCursor(PlotterCursor.Type.ALREADY_XYZ);
		}
	}

	@Override
	public void setDefaultRotAnimation() {
		getView().setRotAnimation(input3D.getDefaultRotationOz(),
				input3D.getDefaultRotationXOY(), false);
	}

	@Override
	protected void getXMLForStereo(StringBuilder sb, int eyeDistance, int sep) {
		if (input3D.shouldStoreStereoToXML()) {
			super.getXMLForStereo(sb, eyeDistance, sep);
		}
	}

	@Override
	protected void setBackground(GColor color) {

		if (input3D.needsGrayBackground()) {
			double grayScale = color.getGrayScale();
			if (grayScale > GRAY_SCALE_FOR_INPUT3D) {
				double factor = GRAY_SCALE_FOR_INPUT3D / grayScale;
				GColor darker = GColor.newColor((int) (color.getRed() * factor),
						(int) (color.getGreen() * factor),
						(int) (color.getBlue() * factor), 255);
				getView().setBackground(color, darker);
				return;
			}
		}

		super.setBackground(color);

	}

	@Override
	public boolean handleSpaceKey() {
		if (getView().getEuclidianController()
				.getMoveMode() == EuclidianController.MOVE_NONE) {

			hittedGeo.setHitted(getView().getHits3D().getTopHits()
					.getFirstGeo6dofMoveable());
			// reset hits
			GeoElement geoToHit = hittedGeo.getGeo();
			getView().getHits3D().init(geoToHit);
			getView().updateCursor3D(getView().getHits());
			getView().getApplication().setMode(EuclidianConstants.MODE_MOVE);
			if (geoToHit != null) {
				hittedGeo.consumeLongDelay();
				input3D.setHasCompletedGrabbingDelay(true);
				getView().getEuclidianController().handleMovedElement(geoToHit,
						false, PointerEventType.TOUCH);
				return true;
			}

			return false;
		}

		releaseGrabbing();

		return true;
	}

	final private void releaseGrabbing() {
		getStationaryCoords().consumeLongDelay();
		input3D.setHasCompletedGrabbingDelay(false);
		getView().getApplication().getSelectionManager()
				.clearSelectedGeos(true);
		getView().getEuclidianController().endOfWrapMouseReleased(new Hits(),
				false, false, false, PointerEventType.TOUCH);
	}

	@Override
	public void setMode(int mode, ModeSetter m) {

		if (input3D.useHandGrabbing() && getView().getEuclidianController()
				.getMoveMode() != EuclidianController.MOVE_NONE) {
			releaseGrabbing();
		}

		super.setMode(mode, m);
	}

	@Override
	protected boolean moveCursorIsVisible() {
		if (!input3D.hasMouseDirection() || input3D.currentlyUseMouse2D()) {
			return super.moveCursorIsVisible();
		}

		return input3D.isThirdButtonPressed() || input3D.isRightPressed();
	}

	@Override
	protected void drawTranslateViewCursor(Renderer renderer1,
			EuclidianCursor cursor, GeoPoint3D cursorOnXOYPlane,
			CoordMatrix4x4 cursorMatrix) {
		if (!input3D.hasMouseDirection()) {
			super.drawTranslateViewCursor(renderer1, cursor, cursorOnXOYPlane,
					cursorMatrix);
		} else {
			if (input3D.currentlyUseMouse2D()) {
				GPoint mouseLoc = getView().getEuclidianController()
						.getMouseLoc();
				if (mouseLoc == null) {
					super.drawTranslateViewCursor(renderer1, cursor,
							cursorOnXOYPlane, cursorMatrix);
				} else {

					Coords v;
					if (getView()
							.getCursor3DType() == EuclidianView3D.CURSOR_DEFAULT) {
						// if mouse is over nothing, use mouse coords and screen
						// for depth
						v = new Coords(mouseLoc.x + renderer1.getLeft(),
								-mouseLoc.y + renderer1.getTop(), 0, 1);
					} else {
						// if mouse is over an object, use its depth and mouse
						// coords
						Coords eye = renderer1.getPerspEye();
						double z = getView().getToScreenMatrix()
								.mul(getView().getCursor3D().getCoords()).getZ()
								+ 20; // to
										// be
										// over
						double eyeSep = renderer1.getEyeSep();

						double x = mouseLoc.x + renderer1.getLeft() + eyeSep
								- eye.getX();
						double y = -mouseLoc.y + renderer1.getTop()
								- eye.getY();
						double dz = eye.getZ() - z;
						double coeff = dz / eye.getZ();

						v = new Coords(x * coeff - eyeSep + eye.getX(),
								y * coeff + eye.getY(), z, 1);
					}

					tmpMatrix4x4_3.setDiagonal3(1 / getView().getScale());
					tmpCoords1.setMul(getView().getToSceneMatrix(), v);
					tmpMatrix4x4_3.setOrigin(tmpCoords1);

					renderer1.setMatrix(tmpMatrix4x4_3);
					getView().drawPointAlready(
							cursorOnXOYPlane.getRealMoveMode());
					renderer1.drawCursor(PlotterCursor.Type.CUBE);
				}
			} else {
				if (input3D.isThirdButtonPressed()) { // third button: translate
														// view
					// let's scale it a bit more
					tmpMatrix4x4_3.setDiagonal3(1.5 / getView().getScale());
					// show the cursor at mid beam
					input3D.getMouse3DPositionShifted(tmpCoords1);
					tmpMatrix4x4_3.setOrigin(tmpCoords1);

					renderer1.setMatrix(tmpMatrix4x4_3);
					renderer1.drawCursor(PlotterCursor.Type.ALREADY_XYZ);
					renderer1.drawCursor(PlotterCursor.Type.CUBE);
				} else { // right button: rotate view

					// let's scale it a bit more
					tmpMatrix4x4_3.setDiagonal3(1.5 / getView().getScale());

					tmpCoords1.setMul(getView().getToSceneMatrix(),
							input3D.getRightDragElevation().val);
					tmpCoords1.setW(0);
					tmpCoords1.addInside(
							getView().getToSceneMatrix().getOrigin());
					tmpMatrix4x4_3.setOrigin(tmpCoords1);

					renderer1.setMatrix(tmpMatrix4x4_3);
					renderer1.drawCursor(PlotterCursor.Type.ROTATION);
				}
			}
		}
	}

	private static class HittedGeo {

		private GeoElement geo;

		private long startTime;
		private long lastTime;

		private long delay = -1;

		private Coords startMousePosition = new Coords(3);

		/**
		 * say if we should forget current
		 * 
		 * @param time
		 *            current time
		 * @return true if from last time enough delay has passed to forget
		 *         current
		 */
		private boolean forgetCurrent(long time) {
			return (time - lastTime) * 8 > LONG_DELAY;
		}

		public void setHitted(GeoElement newGeo, long time,
				Coords mousePosition) {
			// Log.debug("\nHittedGeo:\n"+getHits3D());
			if (newGeo == null || mousePosition == null) { // reinit geo
				if (forgetCurrent(time)) {
					geo = null;
					delay = -1;
					// Log.debug("\n -- geo = null");
				}
			} else {
				if (newGeo == geo) { // remember last time
					// check if mouse has changed too much: reset the timer
					int threshold = 30; // getCapturingThreshold(PointerEventType.TOUCH);
					if (Math.abs(mousePosition.getX()
							- startMousePosition.getX()) > threshold
							|| Math.abs(mousePosition.getY()
									- startMousePosition.getY()) > threshold
							|| Math.abs(mousePosition.getZ()
									- startMousePosition.getZ()) > threshold) {
						startTime = time;
						startMousePosition.setValues(mousePosition, 3);
					} else {
						lastTime = time;
					}
				} else if (geo == null || forgetCurrent(time)) { // change
					// geo
					geo = newGeo;
					startTime = time;
					startMousePosition.setValues(mousePosition, 3);
				}
				// Log.debug("\n "+(time-startTime)+"-- geo = "+geo);
			}
		}

		/**
		 * set hitted geo
		 * 
		 * @param newGeo
		 *            hitted geo
		 */
		public void setHitted(GeoElement newGeo) {
			geo = newGeo;
			if (newGeo == null) {
				delay = -1;
			}
		}

		/**
		 * 
		 * @return current geo
		 */
		public GeoElement getGeo() {
			return geo;
		}

		/**
		 * 
		 * @param time
		 *            current time
		 * @return true if hit was long enough to process left press
		 */
		public boolean hasLongDelay(long time) {

			if (geo == null) {
				delay = -1;
				return false;
			}

			delay = time - startTime;
			if (delay > LONG_DELAY) {
				consumeLongDelay();
				return true;
			}

			return false;
		}

		public void consumeLongDelay() {
			geo = null; // consume event
			delay = -1;
		}

		public float getCompletingDelay() {
			return delay / LONG_DELAY;
		}

	}

	public class StationaryCoords {

		private Coords startCoords = new Coords(4);
		private Coords currentCoords = new Coords(4);
		private long startTime;
		private long delay;
		private Coords newCoords = Coords.createInhomCoorsInD3();

		/**
		 * New coords.
		 */
		public StationaryCoords() {
			startCoords.setUndefined();
			delay = -1;
		}

		/**
		 * @param start
		 *            start coordinates
		 * @param translation
		 *            direction
		 * @param time
		 *            time
		 */
		public void setCoords(Coords start, Coords translation, long time) {
			newCoords.setValues(start, 3);
			newCoords.addInside(translation);
			updateCoords(time);
		}

		/**
		 * @param start
		 *            start coords
		 * @param time
		 *            timestamp
		 */
		public void setCoords(Coords start, long time) {
			newCoords.setValues(start, 3);
			updateCoords(time);
		}

		/**
		 * @param time
		 *            time
		 */
		public void updateCoords(long time) {
			if (startCoords.isDefined()) {
				double distance = Math
						.abs(startCoords.getX() - newCoords.getX())
						+ Math.abs(startCoords.getY() - newCoords.getY())
						+ Math.abs(startCoords.getZ() - newCoords.getZ());
				// Log.debug("\n -- "+(distance * ((EuclidianView3D)
				// ec.view).getScale()));
				if (distance * getView().getScale() > 30) {
					startCoords.set(newCoords);
					startTime = time;
					delay = -1;
					// Log.debug("\n -- startCoords =\n"+startCoords);
				} else {
					currentCoords.set(newCoords);
				}
			} else {
				startCoords.set(newCoords);
				startTime = time;
				delay = -1;
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
				delay = time - startTime;
				if (delay > LONG_DELAY) {
					consumeLongDelay();
					return true;
				}
			} else {
				delay = -1;
			}

			return false;
		}

		/**
		 * consume long delay (reset this)
		 */
		public void consumeLongDelay() {
			startCoords.setUndefined(); // consume event
			delay = -1;
		}

		public float getCompletingDelay() {
			return delay / LONG_DELAY;
		}

		public Coords getCurrentCoords() {
			return currentCoords;
		}
	}

	public StationaryCoords getStationaryCoords() {
		return stationaryCoords;
	}

	@Override
	public boolean useHandGrabbing() {
		return input3D.useHandGrabbing() && !input3D.currentlyUseMouse2D();
	}

	@Override
	protected void getHittingOrigin(GPoint mouse, Coords ret) {
		if (input3D.hasMouseDirection() && !input3D.currentlyUseMouse2D()) {
			ret.set4(input3D.getMouse3DScenePosition());
		} else {
			super.getHittingOrigin(mouse, ret);
		}
	}

	@Override
	public void getHittingDirection(Coords ret) {
		if (input3D.hasMouseDirection() && !input3D.currentlyUseMouse2D()) {
			ret.set4(input3D.getMouse3DDirection());
		} else {
			super.getHittingDirection(ret);
		}
	}

	@Override
	protected void setPickPointFromMouse(GPoint mouse, Coords pickPoint) {
		super.setPickPointFromMouse(mouse, pickPoint);

		if (input3D.currentlyUseMouse2D()) {
			return;
		}

		if (mouse instanceof GPointWithZ) {
			pickPoint.setZ(((GPointWithZ) mouse).getZ());
		}
	}

	@Override
	protected boolean decorationVisible() {
		return (!input3D.hasMouseDirection() || input3D.currentlyUseMouse2D())
				&& super.decorationVisible();
	}

	@Override
	protected void setPointDecorations(GeoPointND point) {
		// no point decoration if using stylus-like input
		if (!input3D.hasMouseDirection() || input3D.currentlyUseMouse2D()) {
			super.setPointDecorations(point);
		}
	}

	@Override
	protected boolean drawCrossForFreePoint() {
		return !input3D.hasMouseDirection() || input3D.currentlyUseMouse2D();
	}

	@Override
	public boolean isStereoBuffered() {
		return input3D.isStereoBuffered();
	}

	@Override
	public boolean wantsStereo() {
		return input3D.wantsStereo();
	}

	@Override
	public boolean useOnlyProjectionGlasses() {
		return input3D.useOnlyProjectionGlasses();
	}
	
	@Override
	public boolean shouldDrawCursor() {
		return super.shouldDrawCursor() && hasMouse();
	}

}
