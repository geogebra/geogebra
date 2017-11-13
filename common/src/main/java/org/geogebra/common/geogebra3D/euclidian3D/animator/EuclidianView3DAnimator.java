package org.geogebra.common.geogebra3D.euclidian3D.animator;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.debug.Log;

/**
 * Animator for 3D view
 *
 */
public class EuclidianView3DAnimator {

	@SuppressWarnings("javadoc")
	public enum AnimationType {
		OFF, ANIMATED_SCALE, CONTINUE_ROTATION, ROTATION, ROTATION_NO_ANIMATION, TRANSLATION, SCREEN_TRANSLATE_AND_SCALE, MOUSE_MOVE, AXIS_SCALE
	}

	private EuclidianView3D view3D;
	private EuclidianView3DAnimation animation;
	private EuclidianView3DAnimationMouseMove animationMouse;
	private EuclidianView3DAnimationAxisScale animationAxis;

	private double xZeroOld, yZeroOld, zZeroOld;
	private double xScaleStart, yScaleStart, zScaleStart;
	private double xScaleEnd, yScaleEnd, zScaleEnd;
	private double screenTranslateAndScaleDX, screenTranslateAndScaleDY, screenTranslateAndScaleDZ;

	private Coords tmpCoords1 = new Coords(4);

	private AnimationType animationType = AnimationType.OFF;

	/**
	 * 
	 * @param view3D
	 *            3D view
	 */
	public EuclidianView3DAnimator(EuclidianView3D view3D) {
		this.view3D = view3D;
		animationMouse = new EuclidianView3DAnimationMouseMove(view3D, this);
		animationAxis = new EuclidianView3DAnimationAxisScale(view3D, this);
	}

	/**
	 * Store values before animation
	 */
	public void rememberOrigins() {
		xZeroOld = view3D.getXZero();
		yZeroOld = view3D.getYZero();
		zZeroOld = view3D.getZZero();
		xScaleStart = view3D.getXscale();
		yScaleStart = view3D.getYscale();
		zScaleStart = view3D.getZscale();

		animationMouse.rememberOrigins();
		animationAxis.rememberOrigins();
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param newScale
	 * @param steps
	 */
	public void setAnimatedCoordSystem(double x, double y, double z, double newScale, int steps) {
		animation = new EuclidianView3DAnimationScaleTranslate(view3D, this, x, y, z, newScale, steps);
		animation.setupForStart();
		animationType = AnimationType.ANIMATED_SCALE;
	}

	/**
	 * @param ox
	 * @param oy
	 * @param f
	 * @param newScale
	 * @param steps
	 * @param storeUndo
	 */
	public void setAnimatedCoordSystem(double ox, double oy, double f, double newScale, int steps, boolean storeUndo) {
		animation = new EuclidianView3DAnimationScale(view3D, this, newScale);
		animation.setupForStart();
		animationType = AnimationType.ANIMATED_SCALE;

	}

	/**
	 * sets a continued animation for rotation if delay is too long, no animation if
	 * speed is too small, no animation
	 *
	 * @param delay
	 *            delay since last drag
	 * @param rotSpeed
	 *            speed of rotation
	 */
	public void setRotContinueAnimation(double delay, double rotSpeed) {

		if (Double.isNaN(rotSpeed)) {
			Log.error("NaN values for setRotContinueAnimation");
			stopAnimation();
			return;
		}

		// if last drag occured more than 200ms ago, then no animation
		if (delay > 200) {
			return;
		}

		// if speed is too small, no animation
		if (Math.abs(rotSpeed) < 0.01) {
			stopAnimation();
			return;
		}

		animationType = AnimationType.CONTINUE_ROTATION;
		animation = new EuclidianView3DAnimationContinueRotation(view3D, this, delay, rotSpeed);
		animation.setupForStart();
	}

	/**
	 * Sets coordinate system from mouse move
	 * 
	 * @param dx
	 *            delta x
	 * @param dy
	 *            delta y
	 * @param mode
	 *            mouse move mode
	 */
	public void setCoordSystemFromMouseMove(int dx, int dy, int mode) {
		animation = animationMouse;
		animationMouse.set(dx, dy, mode);
		animationType = AnimationType.MOUSE_MOVE;
	}

	/**
	 * @param factor
	 *            scale factor
	 * @param scaleOld
	 *            old scale
	 * @param mode
	 *            scale mode
	 */
	final public void setCoordSystemFromAxisScale(double factor, double scaleOld, int mode) {
		animation = animationAxis;
		animationAxis.set(factor, scaleOld, mode);
		animationType = AnimationType.AXIS_SCALE;
	}


	/**
	 * rotate to new angles
	 * 
	 * @param aN
	 *            new Oz angle
	 * @param bN
	 *            new xOy angle
	 * @param checkSameValues
	 *            toggle orientation if same values
	 * @param animated
	 *            if animated
	 */
	public void setRotAnimation(double aN, double bN, boolean checkSameValues, boolean animated) {

		if (Double.isNaN(aN) || Double.isNaN(bN)) {
			Log.error("NaN values for setRotAnimation");
			return;
		}

		animationType = animated ? AnimationType.ROTATION : AnimationType.ROTATION_NO_ANIMATION;
		if (animated) {
			animation = new EuclidianView3DAnimationRotation(view3D, this, aN, bN, checkSameValues);
		} else {
			animation = new EuclidianView3DAnimationRotationOneStep(view3D, this, aN, bN, checkSameValues);
		}
		animation.setupForStart();

	}

	/**
	 * Zooms around fixed point (px, py)
	 * 
	 * @param px
	 *            x coordinate
	 * @param py
	 *            y coordinate
	 * @param zoomFactor
	 *            zoom factor
	 * @param steps
	 *            steps
	 * @param storeUndo
	 *            if needs to store undo info
	 */
	public void zoom(double px, double py, double zoomFactor, int steps, boolean storeUndo) {
		animation = new EuclidianView3DAnimationZoom(view3D, this, zoomFactor);
		animation.setupForStart();
		animationType = AnimationType.ANIMATED_SCALE;
	}

	/**
	 * zoom y & z axes ratio regarding x axis
	 * 
	 * @param zoomFactorY
	 * @param zoomFactorZ
	 */
	public void zoomAxesRatio(double zoomFactorY, double zoomFactorZ) {
		animation = new EuclidianView3DAnimationAxesRatio(view3D, this, zoomFactorY, zoomFactorZ);
		animation.setupForStart();
		animationType = AnimationType.ANIMATED_SCALE;

	}

	/**
	 * 
	 * @param point
	 *            point to center the view about
	 */
	public void centerView(GeoPointND point) {
		animation = new EuclidianView3DAnimationCenter(view3D, this, point.getInhomCoordsInD3());
		animation.setupForStart();
		animationType = AnimationType.TRANSLATION;
	}

	/**
	 * animate the view for changing scale, orientation, etc.
	 */
	public void animate() {
		if (animationType == AnimationType.SCREEN_TRANSLATE_AND_SCALE) {
			view3D.setXZero(xZeroOld + screenTranslateAndScaleDX);
			view3D.setYZero(yZeroOld + screenTranslateAndScaleDY);
			view3D.setZZero(zZeroOld + screenTranslateAndScaleDZ);
			view3D.getSettings().updateOriginFromView(view3D.getXZero(), view3D.getYZero(), view3D.getZZero());
			view3D.setScale(xScaleEnd, yScaleEnd, zScaleEnd);
			view3D.updateMatrix();
			view3D.setViewChangedByZoom();
			view3D.setViewChangedByTranslate();
			view3D.setWaitForUpdate();

			stopAnimation();
		} else {
			animation.animate();
		}
	}

	/**
	 * @param dx
	 *            translation in screen coordinates
	 * @param dy
	 *            translation in screen coordinates
	 * @param scaleFactor
	 *            scale factor
	 */
	public void screenTranslateAndScale(double dx, double dy, double scaleFactor) {

		// dx and dy are translation in screen coords
		// dx moves along "visible left-right" axis on xOy plane
		// dy moves along "visible front-back" axis on xOy plane
		// or z-axis if this one "visibly" more than sqrt(2)*front-back axis
		tmpCoords1.set(Coords.VX);
		view3D.toSceneCoords3D(tmpCoords1);
		screenTranslateAndScaleDX = tmpCoords1.getX() * dx;
		screenTranslateAndScaleDY = tmpCoords1.getY() * dx;

		tmpCoords1.set(Coords.VY);
		view3D.toSceneCoords3D(tmpCoords1);
		double z = tmpCoords1.getZ() * view3D.getScale();
		if (z > 0.85) {
			screenTranslateAndScaleDZ = tmpCoords1.getZ() * (-dy);
		} else if (z < 0.45) {
			screenTranslateAndScaleDX += tmpCoords1.getX() * (-dy);
			screenTranslateAndScaleDY += tmpCoords1.getY() * (-dy);
			screenTranslateAndScaleDZ = 0;
		} else {
			screenTranslateAndScaleDZ = 0;
		}

		xScaleEnd = xScaleStart * scaleFactor;
		yScaleEnd = yScaleStart * scaleFactor;
		zScaleEnd = zScaleStart * scaleFactor;
		animationType = AnimationType.SCREEN_TRANSLATE_AND_SCALE;
	}

	/**
	 * stop screen translate and scale
	 */
	public void stopScreenTranslateAndScale() {
		if (animationType == AnimationType.SCREEN_TRANSLATE_AND_SCALE) {
			animationType = AnimationType.OFF;
		}
	}

	/**
	 * stops the animations
	 */
	public void stopAnimation() {
		animationType = AnimationType.OFF;
	}

	/**
	 * 
	 * @return animation type
	 */
	public AnimationType getAnimationType() {
		return animationType;
	}

}
