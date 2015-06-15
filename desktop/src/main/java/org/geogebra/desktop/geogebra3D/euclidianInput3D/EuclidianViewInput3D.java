package org.geogebra.desktop.geogebra3D.euclidianInput3D;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.euclidian3D.Input3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.geogebra3D.euclidian3D.draw.DrawSegment3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterCursor;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3DConstant;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.kernel.Matrix.CoordMatrix;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.desktop.geogebra3D.awt.GPointWithZ;
import org.geogebra.desktop.geogebra3D.euclidian3D.EuclidianView3DD;
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
	}
	
	@Override
	protected void start(){
		input3D = ((EuclidianControllerInput3D) euclidianController).input3D;
		super.start();
	}

	private Coords mouse3DScreenPosition = null;
	private Coords mouse3DScenePosition;

	@Override
	public void drawMouseCursor(Renderer renderer1) {

		if (input3D.currentlyUseMouse2D() || input3D.hasMouseDirection()) {
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
		}

		drawMouseCursor(renderer1, mouse3DScreenPosition);

	}
	
	@Override
	public void drawCursor(Renderer renderer1) {
		
		super.drawCursor(renderer1);
	}

	static public Coords CompletingCursorColorGrabbing = new Coords(0f, 0.5f,
			0f, 1f);
	static public Coords CompletingCursorColorRelease = new Coords(1f, 0f, 0f,
			1f);

	private boolean drawCompletingCursor(Renderer renderer1) {

		float completingDelay = hittedGeo.getCompletingDelay();

		if (completingDelay > 0.5f && completingDelay <= 1f) {
			CoordMatrix4x4.Identity(tmpMatrix4x4_3);
			Coords origin = getCursor3D().getInhomCoordsInD3().copyVector();
			toScreenCoords3D(origin);
			tmpMatrix4x4_3.setOrigin(origin);
			renderer1.setMatrix(tmpMatrix4x4_3);
			renderer1.drawCompletingCursor(completingDelay,
					CompletingCursorColorGrabbing);
			return true;
		}

		completingDelay = stationaryCoords.getCompletingDelay();
		if (completingDelay > 0.5f && completingDelay <= 1f) {
			CoordMatrix4x4.Identity(tmpMatrix4x4_3);
			Coords origin = stationaryCoords.getCurrentCoords().copyVector();
			toScreenCoords3D(origin);
			tmpMatrix4x4_3.setOrigin(origin);
			renderer1.setMatrix(tmpMatrix4x4_3);
			renderer1.drawCompletingCursor(completingDelay,
					CompletingCursorColorRelease);
			return true;
		}

		return false;
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
		setXZero(XZeroOld + translation.getX());
		setYZero(YZeroOld + translation.getY());
		setZZero(ZZeroOld + translation.getZ());

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
	public GeoElement getLabelHit(org.geogebra.common.awt.GPoint p,
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
				&& !input3D.getLeftButton()) {
			long time = System.currentTimeMillis();
			hittedGeo.setHitted(getHits3D(), time, mouse3DScreenPosition);
			// reset hits
			getHits3D().init(hittedGeo.getGeo());
			app.setMode(EuclidianConstants.MODE_MOVE);
			if (hittedGeo.hasLongDelay(time)) {
				input3D.setLeftButtonPressed(true);
			}
		}

	}

	static protected float LONG_DELAY = 1500f;

	private class HittedGeo {

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

		public void setHitted(Hits hits, long time, Coords mousePosition) {
			// App.debug("\nHittedGeo:\n"+getHits3D());
			if (hits.isEmpty() || mousePosition == null) { // reinit geo
				if (forgetCurrent(time)) {
					geo = null;
					delay = -1;
					// App.debug("\n -- geo = null");
				}
			} else {
				GeoElement newGeo = hits.get(0);
				if (newGeo.is6dofMoveable()) {
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
				} else if (forgetCurrent(time)) {
					geo = null;
					delay = -1;
				}
				// App.debug("\n "+(time-startTime)+"-- geo = "+geo);
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
				geo = null; // consume event
				delay = -1;
				return true;
			}

			return false;
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

		public StationaryCoords() {
			startCoords.setUndefined();
			delay = -1;
		}

		public void setCoords(Coords coords, long time) {

			if (startCoords.isDefined()) {
				double distance = Math.abs(startCoords.getX() - coords.getX())
						+ Math.abs(startCoords.getY() - coords.getY())
						+ Math.abs(startCoords.getZ() - coords.getZ());
				// App.debug("\n -- "+(distance * ((EuclidianView3D)
				// ec.view).getScale()));
				if (distance * getScale() > 30) {
					startCoords.set(coords);
					startTime = time;
					delay = -1;
					// App.debug("\n -- startCoords =\n"+startCoords);
				} else {
					currentCoords.set(coords);
				}
			} else {
				startCoords.set(coords);
				startTime = time;
				delay = -1;
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
				delay = time - startTime;
				if (delay > LONG_DELAY) {
					startCoords.setUndefined(); // consume event
					delay = -1;
					return true;
				}
			} else {
				delay = -1;
			}

			return false;
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
	public boolean stereoGlassesDetected() {
		return input3D.glassedDetected();
	}

	@Override
	public Coords getHittingDirection() {
		if (input3D.hasMouseDirection()) {
			return ((EuclidianControllerInput3D) euclidianController)
					.getMouse3DDirection();
		}
		return super.getHittingDirection();
	}

	@Override
	public Coords getHittingOrigin(GPoint mouse) {
		if (input3D.hasMouseDirection()) {
			return ((EuclidianControllerInput3D) euclidianController)
					.getMouse3DScenePosition();
		}
		return super.getHittingOrigin(mouse);
	}

	private GeoSegment3D stylusBeam;
	private DrawSegment3D stylusBeamDrawable;

	@Override
	public void initAxisAndPlane() {
		super.initAxisAndPlane();

		if (input3D.hasMouseDirection()) {
			stylusBeam = new GeoSegment3D(getKernel().getConstruction());
			stylusBeam.setCoord(Coords.O, Coords.VX);
			stylusBeam.setObjColor(GColor.GREEN);

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
		if (input3D.hasMouseDirection()) {
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
			stylusBeamDrawable.setWaitForUpdateVisualStyle();
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
		return input3D.hasMouseDirection() && !input3D.isRightPressed()
				&& !input3D.isThirdButtonPressed()
				&& hasMouse();
	}

	@Override
	protected boolean decorationVisible() {
		return !input3D.hasMouseDirection() && super.decorationVisible();
	}

	@Override
	protected boolean drawCrossForFreePoint() {
		return !input3D.hasMouseDirection();
	}

	@Override
	protected void drawPointAlready(GeoPoint3D point) {
		if (point.hasRegion()) {
			super.drawPointAlready(point);
		}
	}

	@Override
	public boolean usesInteriorRadiusToClip() {
		return true;
	}
}
