package org.geogebra.desktop.geogebra3D.euclidianInput3D;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.euclidian3D.Input3D;
import org.geogebra.common.euclidian3D.Input3D.OutOfField;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawSegment3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterCompletingCursor;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterCursor;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3DConstant;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.Matrix.CoordMatrix;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.desktop.geogebra3D.awt.GPointWithZ;
import org.geogebra.desktop.geogebra3D.euclidian3D.EuclidianView3DD;
import org.geogebra.desktop.geogebra3D.euclidian3D.opengl.RendererJogl;
import org.geogebra.desktop.geogebra3D.euclidian3D.opengl.RendererLogicalPickingGL2;

/**
 * EuclidianView3D with controller using 3D input
 * 
 * @author mathieu
 * 
 */
public class EuclidianViewInput3D extends EuclidianView3DD {

	private Input3D input3D;

	/**
	 * constructor
	 * 
	 * @param ec
	 *            euclidian controller
	 * @param settings
	 *            settings
	 */
	public EuclidianViewInput3D(EuclidianController3D ec,
			EuclidianSettings settings) {
		super(ec, settings);


		mouse3DScenePosition = new Coords(4);
		mouse3DScenePosition.setW(1);

		startPos = new Coords(4);
		startPos.setW(1);

		if (input3D.useCompletingDelay()) {
			completingCursorOrigin = Coords.createInhomCoorsInD3();
		}
	}
	
	@Override
	protected void start(){
		input3D = ((EuclidianControllerInput3D) euclidianController).input3D;
		input3D.init(this);
		super.start();
	}

	private Coords mouse3DScreenPosition = null;
	private Coords mouse3DScenePosition;

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
		mouse3DScreenPosition = ((EuclidianControllerInput3D) getEuclidianController())
				.getMouse3DPosition();

		if (((EuclidianControllerInput3D) getEuclidianController())
				.useInputDepthForHitting()) {
			mouse3DScenePosition.set(mouse3DScreenPosition);
			toSceneCoords3D(mouse3DScenePosition);
			for (int i = 1; i <= 3; i++) {
				transparentMouseCursorMatrix.set(i, i, 1 / getScale());
			}
			transparentMouseCursorMatrix.setOrigin(mouse3DScenePosition);
		}

		if (input3D.useCompletingDelay()){
			if (drawCompletingCursor(renderer1)) {
				return;
			}

			// draw mouse cursor at the same place
			renderer1.drawMouseCursor();
			return;

		}

		drawMouseCursor(renderer1, mouse3DScreenPosition);

	}
	
	@Override
	public void drawCursor(Renderer renderer1) {
		
		super.drawCursor(renderer1);
	}

	private Coords completingCursorOrigin;

	/**
	 * 
	 * @param renderer1
	 * @return false if we need also the mouse cursor
	 */
	private boolean drawCompletingCursor(Renderer renderer1) {

		// are we grabbing?
		float hittedGeoCompletingDelay = hittedGeo.getCompletingDelay();
		if (hittedGeoCompletingDelay > PlotterCompletingCursor.START_DRAW
				&& hittedGeoCompletingDelay <= PlotterCompletingCursor.END_DRAW) {
			CoordMatrix4x4.Identity(tmpMatrix4x4_3);
			completingCursorOrigin.setValues(
					getCursor3D().getInhomCoordsInD3(), 3);
			toScreenCoords3D(completingCursorOrigin);
			return drawCompletingCursor(renderer1, completingCursorOrigin,
					hittedGeoCompletingDelay);
		}

		// are we releasing?
		float stationaryCoordsCompletingDelay = stationaryCoords
				.getCompletingDelay();
		if (stationaryCoordsCompletingDelay > PlotterCompletingCursor.START_DRAW
				&& stationaryCoordsCompletingDelay <= PlotterCompletingCursor.END_DRAW) {
			CoordMatrix4x4.Identity(tmpMatrix4x4_3);
			completingCursorOrigin.setValues(
					stationaryCoords.getCurrentCoords(), 3);
			toScreenCoords3D(completingCursorOrigin);
			drawCompletingCursor(renderer1, completingCursorOrigin,
					1 - stationaryCoordsCompletingDelay);
			return true;
		}

		// are we moving?
		if (stationaryCoordsCompletingDelay >= 0
				&& stationaryCoordsCompletingDelay <= PlotterCompletingCursor.START_DRAW) {
			CoordMatrix4x4.Identity(tmpMatrix4x4_3);
			completingCursorOrigin.setValues(
					stationaryCoords.getCurrentCoords(), 3);
			toScreenCoords3D(completingCursorOrigin);
			drawCompletingCursor(renderer1, completingCursorOrigin, 1);
			return true;
		}

		// are we over a moveable geo?
		if (hittedGeo.getGeo() != null
				&& hittedGeoCompletingDelay <= PlotterCompletingCursor.START_DRAW) {
			CoordMatrix4x4.Identity(tmpMatrix4x4_3);
			completingCursorOrigin.setValues(
					getCursor3D().getInhomCoordsInD3(), 3);
			toScreenCoords3D(completingCursorOrigin);
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

	private CoordMatrix4x4 transparentMouseCursorMatrix = new CoordMatrix4x4();

	@Override
	public void drawTransp(Renderer renderer1) {

		// sphere for mouse cursor
		if (((EuclidianControllerInput3D) getEuclidianController())
				.useInputDepthForHitting() && mouse3DScreenPosition != null) {
			renderer1.setMatrix(transparentMouseCursorMatrix);
			if (getCursor3DType() == PREVIEW_POINT_FREE) {
				renderer1.drawCursor(PlotterCursor.TYPE_SPHERE);
			} else {
				renderer1.drawCursor(PlotterCursor.TYPE_SPHERE_HIGHLIGHTED);
			}
		}

		super.drawTransp(renderer1);

	}

	@Override
	public void drawHiding(Renderer renderer1) {

		// sphere for mouse cursor
		if (((EuclidianControllerInput3D) getEuclidianController())
				.useInputDepthForHitting() && mouse3DScreenPosition != null) {
			renderer1.setMatrix(transparentMouseCursorMatrix);
			renderer1.drawCursor(PlotterCursor.TYPE_SPHERE);
		}

		super.drawHiding(renderer1);

	}

	@Override
	public boolean isPolarized() {
		return input3D.useInterlacedPolarization();
	}

	@Override
	public double getScreenZOffset() {
		if (input3D != null && input3D.useScreenZOffset()) {
			return clippingCubeDrawable.getHorizontalDiagonal() / 2;
		}

		return super.getScreenZOffset();
	}

	private Coords startPos;

	private CoordMatrix4x4 startTranslation = CoordMatrix4x4.Identity();

	// private CoordMatrix4x4 startTranslationScreen =
	// CoordMatrix4x4.Identity();

	/**
	 * set mouse start pos
	 * 
	 * @param screenStartPos
	 *            mouse start pos (screen)
	 */
	public void setStartPos(Coords screenStartPos) {

		startPos.set(screenStartPos);
		toSceneCoords3D(startPos);
		startTranslation.setOrigin(screenStartPos.add(startPos));

	}

	/**
	 * set the coord system regarding 3D mouse move
	 * 
	 * @param startPos1
	 *            start 3D position (screen)
	 * @param newPos
	 *            current 3D position (screen)
	 * @param rotX
	 *            relative mouse rotate around x (screen)
	 * @param rotZ
	 *            relative mouse rotate around z (view)
	 */
	public void setCoordSystemFromMouse3DMove(Coords startPos1, Coords newPos,
			double rotX, double rotZ) {

		// translation
		Coords v = new Coords(4);
		v.set(newPos.sub(startPos1));
		toSceneCoords3D(v);

		// rotation
		setRotXYinDegrees(aOld + rotX, bOld + rotZ);

		updateRotationAndScaleMatrices();

		// center rotation on pick point ( + v for translation)
		CoordMatrix m1 = rotationAndScaleMatrix.inverse().mul(startTranslation)
				.mul(rotationAndScaleMatrix);
		Coords t1 = m1.getOrigin();
		setXZero(t1.getX() - startPos.getX() + v.getX());
		setYZero(t1.getY() - startPos.getY() + v.getY());
		setZZero(t1.getZ() - startPos.getZ() + v.getZ());
		getSettings().updateOriginFromView(getXZero(), getYZero(), getZZero());
		// update the view
		updateTranslationMatrix();
		updateUndoTranslationMatrix();
		setGlobalMatrices();

		setViewChangedByTranslate();
		setViewChangedByRotate();
		setWaitForUpdate();

	}

	/**
	 * set the coord system regarding 3D mouse move
	 * 
	 * @param translation
	 *            translation vector
	 */
	public void setCoordSystemFromMouse3DMove(Coords translation) {
		setXZero(xZeroOld + translation.getX());
		setYZero(yZeroOld + translation.getY());
		setZZero(zZeroOld + translation.getZ());

		// update the view
		updateTranslationMatrix();
		updateUndoTranslationMatrix();
		setGlobalMatrices();

		setViewChangedByTranslate();
		setWaitForUpdate();
	}

	@Override
	protected void setPickPointFromMouse(GPoint mouse) {
		super.setPickPointFromMouse(mouse);

		if (input3D.currentlyUseMouse2D()) {
			return;
		}

		if (mouse instanceof GPointWithZ) {
			pickPoint.setZ(((GPointWithZ) mouse).getZ());
		}
	}

	@Override
	protected void drawFreeCursor(Renderer renderer1) {

		if (input3D.currentlyUseMouse2D()) {
			super.drawFreeCursor(renderer1);
		} else {
			// free point in space
			renderer1.drawCursor(PlotterCursor.TYPE_CROSS3D);
		}
	}

	@Override
	public GeoElement getLabelHit(GPoint p,
			PointerEventType type) {
		if (input3D.currentlyUseMouse2D()) {
			return super.getLabelHit(p, type);
		}
		return null;
	}

	@Override
	public int getMousePickWidth() {
		if (input3D.currentlyUseMouse2D()) {
			return super.getMousePickWidth();
		}
		return Renderer.MOUSE_PICK_DEPTH;
	}

	@Override
	public void setHits(PointerEventType type) {

		if (!input3D.currentlyUseMouse2D()
				&& (input3D.isRightPressed() || input3D.isThirdButtonPressed())) {
			return;
		}

		super.setHits(type);

		if (input3D.currentlyUseMouse2D()) {
			return;
		}
		
		// not moving a geo : see if user stays on the same hit to select it
		if (input3D.useCompletingDelay()
				&& getEuclidianController().getMoveMode() == EuclidianController.MOVE_NONE
				&& !input3D.hasCompletedGrabbingDelay()) {
			long time = System.currentTimeMillis();
			hittedGeo.setHitted(getHits3D().getTopHits()
					.getFirstGeo6dofMoveable(), time, mouse3DScreenPosition);
			// reset hits
			GeoElement geoToHit = hittedGeo.getGeo();
			getHits3D().init(geoToHit);
			updateCursor3D(getHits());
			app.setMode(EuclidianConstants.MODE_MOVE);
			if (hittedGeo.hasLongDelay(time)) {
				input3D.setHasCompletedGrabbingDelay(true);
				euclidianController.handleMovedElement(geoToHit,
						false, PointerEventType.TOUCH);
			}
		}

	}

	final static protected float LONG_DELAY = 1500f;

	private static class HittedGeo {

		private GeoElement geo;

		private long startTime, lastTime;

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

		public void setHitted(GeoElement newGeo, long time, Coords mousePosition) {
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
					int threshold = 30;// getCapturingThreshold(PointerEventType.TOUCH);
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

	private HittedGeo hittedGeo = new HittedGeo();

	public class StationaryCoords {

		private Coords startCoords = new Coords(4), currentCoords = new Coords(
				4);
		private long startTime;
		private long delay;
		private Coords newCoords = Coords.createInhomCoorsInD3();

		public StationaryCoords() {
			startCoords.setUndefined();
			delay = -1;
		}

		public void setCoords(Coords start, Coords translation, long time) {
			newCoords.setValues(start, 3);
			newCoords.addInside(translation);
			updateCoords(time);
		}

		public void setCoords(Coords start, long time) {
			newCoords.setValues(start, 3);
			updateCoords(time);
		}

		public void updateCoords(long time) {

			if (startCoords.isDefined()) {
				double distance = Math.abs(startCoords.getX()
						- newCoords.getX())
						+ Math.abs(startCoords.getY() - newCoords.getY())
						+ Math.abs(startCoords.getZ() - newCoords.getZ());
				// Log.debug("\n -- "+(distance * ((EuclidianView3D)
				// ec.view).getScale()));
				if (distance * getScale() > 30) {
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

	private StationaryCoords stationaryCoords = new StationaryCoords();

	public StationaryCoords getStationaryCoords() {
		return stationaryCoords;
	}

	@Override
	protected Renderer createRenderer() {
		RendererJogl.setDefaultProfile();
		return new RendererLogicalPickingGL2(this, !app.isApplet());

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

	/*
	 * @Override protected void setDefault2DCursor() { setTransparentCursor(); }
	 */

	@Override
	public boolean hasMouse() {

		if (input3D.currentlyUseMouse2D()) {
			return super.hasMouse();
		}

		return input3D.hasMouse(this);
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
	public Coords getHittingDirection() {
		if (input3D.hasMouseDirection() && !input3D.currentlyUseMouse2D()) {
			return ((EuclidianControllerInput3D) euclidianController)
					.getMouse3DDirection();
		}
		return super.getHittingDirection();
	}

	@Override
	public Coords getHittingOrigin(GPoint mouse) {
		if (input3D.hasMouseDirection() && !input3D.currentlyUseMouse2D()) {
			return ((EuclidianControllerInput3D) euclidianController)
					.getMouse3DScenePosition();
		}
		return super.getHittingOrigin(mouse);
	}

	private GeoSegment3D stylusBeam;
	private DrawSegment3D stylusBeamDrawable;

	static private int STYLUS_BEAM_THICKNESS = 9;

	@Override
	public void initAxisAndPlane() {
		super.initAxisAndPlane();

		if (input3D.hasMouseDirection()) {
			stylusBeam = new GeoSegment3D(getKernel().getConstruction());
			stylusBeam.setCoord(Coords.O, Coords.VX);
			stylusBeam.setObjColor(GColor.GREEN);
			stylusBeam.setLineThickness(STYLUS_BEAM_THICKNESS);

			stylusBeamDrawable = new DrawSegment3D(this, stylusBeam) {
				@Override
				protected boolean isVisible() {
					return true;
				}
			};
		}


	}


	private double zNearest = 4;

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
	 * 
	 * @return current z nearest hit
	 */
	public double getZNearest() {
		return zNearest;
	}

	/**
	 * update stylus beam for moved geo
	 */
	public void updateStylusBeamForMovedGeo() {
		if (euclidianController.getMoveMode() != EuclidianController.MOVE_PLANE) {
			getCursor3D()
					.setCoords(
							((EuclidianControllerInput3D) euclidianController)
									.getMouse3DScenePosition(),
							false);
			GeoElement movedGeo = euclidianController.getMovedGeoElement();
			if (movedGeo != null) {
				zNearest = movedGeo.distance(getCursor3D());
			}
		}

		updateStylusBeam();
	}


	private void updateStylusBeam() {
		if (input3D.hasMouseDirection() && !input3D.currentlyUseMouse2D()) {
			stylusBeam.setCoord(
					((EuclidianControllerInput3D) euclidianController)
							.getMouse3DScenePosition(),
					((EuclidianControllerInput3D) euclidianController)
							.getMouse3DDirection().mul(zNearest));
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
	public void getStylusBeamEnd(Coords coords, double l){
		coords.setAdd(((EuclidianControllerInput3D) euclidianController)
				.getMouse3DScenePosition(), coords.setMul(
				((EuclidianControllerInput3D) euclidianController)
						.getMouse3DDirection(), l));
	}

	@Override
	public void resetAllVisualStyles() {
		super.resetAllVisualStyles();
		if (input3D.hasMouseDirection()) {
			stylusBeamDrawable.setWaitForUpdateVisualStyle(null);
		}
	}

	@Override
	public void resetOwnDrawables() {
		super.resetOwnDrawables();

		if (input3D.hasMouseDirection()) {
			stylusBeamDrawable.setWaitForReset();
		}
	}

	@Override
	public void update() {
		super.update();

		if (input3D.hasMouseDirection()) {
			stylusBeamDrawable.update();
		}
	}

	@Override
	public void draw(Renderer renderer1) {
		super.draw(renderer1);

		if (drawStylusBeam()) {
			stylusBeamDrawable.drawOutline(renderer1);
		}

	}

	@Override
	public void drawHidden(Renderer renderer1) {
		super.drawHidden(renderer1);

		if (drawStylusBeam()) {
			stylusBeamDrawable.drawHidden(renderer1);
		}
	}

	private boolean drawStylusBeam() {
		if (!input3D.hasMouseDirection() || input3D.currentlyUseMouse2D()) {
			return false;
		}

		if (input3D.isLeftPressed()) { // show stylus beam only if object is
										// moved
			if (euclidianController.getMoveMode() == EuclidianController.MOVE_NONE) {
				return false;
			}
			return hasMouse();
		}

		if (input3D.isRightPressed() || input3D.isThirdButtonPressed()) {
			return false;
		}

		return hasMouse();
	}

	@Override
	protected boolean decorationVisible() {
		return (!input3D.hasMouseDirection() || input3D.currentlyUseMouse2D())
				&& super.decorationVisible();
	}

	@Override
	protected boolean drawCrossForFreePoint() {
		return !input3D.hasMouseDirection() || input3D.currentlyUseMouse2D();
	}

	@Override
	protected void drawPointAlready(GeoPoint3D point) {

		if (input3D.currentlyUseMouse2D()) {
			super.drawPointAlready(point);
			return;
		}

		if (point.hasRegion()) {
			super.drawPointAlready(point);
		} else if (!point.hasPath()
				&& point.getMoveMode() != GeoPointND.MOVE_MODE_NONE) {
			renderer.drawCursor(PlotterCursor.TYPE_ALREADY_XYZ);
		}
	}


	@Override
	public void setDefaultRotAnimation() {
		setRotAnimation(input3D.getDefaultRotationOz(),
				input3D.getDefaultRotationXOY(), false);
	}

	@Override
	protected void getXMLForStereo(StringBuilder sb) {
		if (input3D.shouldStoreStereoToXML()) {
			super.getXMLForStereo(sb);
		}
	}

	final private static double GRAY_SCALE_FOR_INPUT3D = 255 * 0.75;

	@Override
	public void setBackground(GColor color) {

		if (input3D.needsGrayBackground()) {
			double grayScale = color.getGrayScale();
			if (grayScale > GRAY_SCALE_FOR_INPUT3D) {
				double factor = GRAY_SCALE_FOR_INPUT3D / grayScale;
				GColor darker = GColor.newColor(
						(int) (color.getRed() * factor),
						(int) (color.getGreen() * factor),
						(int) (color.getBlue() * factor),
						255);
				super.setBackground(color, darker);
				return;
			}
		}


		super.setBackground(color);

	}

	@Override
	public boolean useHandGrabbing() {
		return input3D.useHandGrabbing() && !input3D.currentlyUseMouse2D();
	}

	@Override
	public boolean handleSpaceKey(){
		if (euclidianController.getMoveMode() == EuclidianController.MOVE_NONE) {

			hittedGeo.setHitted(getHits3D().getTopHits()
					.getFirstGeo6dofMoveable());
			// reset hits
			GeoElement geoToHit = hittedGeo.getGeo();
			getHits3D().init(geoToHit);
			updateCursor3D(getHits());
			app.setMode(EuclidianConstants.MODE_MOVE);
			if (geoToHit != null) {
				hittedGeo.consumeLongDelay();
				input3D.setHasCompletedGrabbingDelay(true);
				euclidianController.handleMovedElement(geoToHit, false,
						PointerEventType.TOUCH);
				return true;
			}

			return false;
		}

		((EuclidianControllerInput3D) euclidianController).releaseGrabbing();

		return true;
	}

	@Override
	public void setMode(int mode, ModeSetter m) {

		if (input3D.useHandGrabbing()
				&& euclidianController.getMoveMode() != EuclidianController.MOVE_NONE) {
			((EuclidianControllerInput3D) euclidianController)
					.releaseGrabbing();
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
	protected void drawTranslateViewCursor(Renderer renderer1) {
		if (!input3D.hasMouseDirection()) {
			super.drawTranslateViewCursor(renderer1);
		} else {
			if (input3D.currentlyUseMouse2D()) {
				GPoint mouseLoc = euclidianController.getMouseLoc();
				if (mouseLoc == null) {
					super.drawTranslateViewCursor(renderer1);
				} else {

					Coords v;
					if (getCursor3DType() == CURSOR_DEFAULT) {
						// if mouse is over nothing, use mouse coords and screen
						// for depth
						v = new Coords(mouseLoc.x + renderer1.getLeft(),
								-mouseLoc.y + renderer1.getTop(), 0, 1);
					} else {
						// if mouse is over an object, use its depth and mouse
						// coords
						Coords eye = renderer1.getPerspEye();
						double z = getToScreenMatrix().mul(
								getCursor3D().getCoords()).getZ() + 20; // to be
																		// over
						double eyeSep = renderer1.getEyeSep();

						double x = mouseLoc.x + renderer1.getLeft() + eyeSep
								- eye.getX();
						double y = -mouseLoc.y + renderer1.getTop()
								- eye.getY();
						double dz = eye.getZ() - z;
						double coeff = dz / eye.getZ();

						v = new Coords(x * coeff - eyeSep + eye.getX(), y
								* coeff + eye.getY(), z, 1);
					}

					tmpMatrix4x4_3.setDiagonal3(1 / getScale());
					tmpCoords1.setMul(getToSceneMatrix(), v);
					tmpMatrix4x4_3.setOrigin(tmpCoords1);

					renderer1.setMatrix(tmpMatrix4x4_3);
					drawPointAlready(cursorOnXOYPlane.getRealMoveMode());
					renderer1.drawCursor(PlotterCursor.TYPE_CUBE);
				}
			} else {
				if (input3D.isThirdButtonPressed()) { // third button: translate
														// view
					// let's scale it a bit more
					tmpMatrix4x4_3.setDiagonal3(1.5 / getScale());
					// show the cursor at mid beam
					((EuclidianControllerInput3D) euclidianController)
							.getMouse3DPositionShifted(tmpCoords1);
					tmpMatrix4x4_3.setOrigin(tmpCoords1);

					renderer1.setMatrix(tmpMatrix4x4_3);
					renderer1.drawCursor(PlotterCursor.TYPE_ALREADY_XYZ);
					renderer1.drawCursor(PlotterCursor.TYPE_CUBE);
				} else { // right button: rotate view

					// let's scale it a bit more
					tmpMatrix4x4_3.setDiagonal3(1.5 / getScale());

					tmpCoords1.setMul(getToSceneMatrix(),
							((EuclidianControllerInput3D) euclidianController)
									.getRightDragElevation().val);
					tmpCoords1.setW(0);
					tmpCoords1.addInside(getToSceneMatrix().getOrigin());
					tmpMatrix4x4_3.setOrigin(tmpCoords1);

					renderer1.setMatrix(tmpMatrix4x4_3);
					renderer1.drawCursor(PlotterCursor.TYPE_ROTATION);
				}
			}
		}
	}

}
