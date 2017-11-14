package org.geogebra.common.geogebra3D.euclidian3D.animator;

import java.util.LinkedList;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.Feature;
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
	private LinkedList<EuclidianView3DAnimation> animationList;
	private EuclidianView3DAnimation animation;
	private EuclidianView3DAnimationMouseMove animationMouse;
	private EuclidianView3DAnimationAxisScale animationAxis;
	private EuclidianView3DAnimationScreenScale animationScreenScale;
	private EuclidianView3DAnimationScale animationScale;

	/**
	 * 
	 * @param view3D
	 *            3D view
	 */
	public EuclidianView3DAnimator(EuclidianView3D view3D) {
		this.view3D = view3D;
		animationList = new LinkedList<EuclidianView3DAnimation>();
		animationMouse = new EuclidianView3DAnimationMouseMove(view3D, this);
		animationAxis = new EuclidianView3DAnimationAxisScale(view3D, this);
		animationScreenScale = new EuclidianView3DAnimationScreenScale(view3D, this);
		animationScale = new EuclidianView3DAnimationScale(view3D, this);
	}

	/**
	 * Store values before animation
	 */
	public void rememberOrigins() {
		animationMouse.rememberOrigins();
		animationAxis.rememberOrigins();
		animationScreenScale.rememberOrigins();
		stopAnimation();
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
		addAnimation(new EuclidianView3DAnimationScaleTranslate(view3D, this, x, y, z, newScale, steps));
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
		stopAnimation();
		animation = animationScale;
		animationScale.set(newScale);
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

		addAnimation(new EuclidianView3DAnimationContinueRotation(view3D, this, delay, rotSpeed));
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

		if (animated) {
			addAnimation(new EuclidianView3DAnimationRotation(view3D, this, aN, bN, checkSameValues));
		} else {
			addAnimation(new EuclidianView3DAnimationRotationOneStep(view3D, this, aN, bN, checkSameValues));
		}

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
		addAnimation(new EuclidianView3DAnimationZoom(view3D, this, zoomFactor));
	}

	/**
	 * zoom y & z axes ratio regarding x axis
	 * 
	 * @param zoomFactorY
	 * @param zoomFactorZ
	 */
	public void zoomAxesRatio(double zoomFactorY, double zoomFactorZ) {
		addAnimation(new EuclidianView3DAnimationAxesRatio(view3D, this, zoomFactorY, zoomFactorZ));
	}

	/**
	 * 
	 * @param point
	 *            point to center the view about
	 */
	public void centerView(GeoPointND point) {
		addAnimation(new EuclidianView3DAnimationCenter(view3D, this, point.getInhomCoordsInD3()));
	}

	/**
	 * animate the view for changing scale, orientation, etc.
	 */
	public void animate() {
		animation.animate();
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
		animationScreenScale.set(dx, dy, scaleFactor);
		animation = animationScreenScale;
	}

	/**
	 * stop screen translate and scale
	 */
	public void stopScreenTranslateAndScale() {
		if (getAnimationType() == AnimationType.SCREEN_TRANSLATE_AND_SCALE) {
			stopAnimation();
		}
	}

	/**
	 * stops the animations
	 */
	synchronized public void stopAnimation() {
		if (view3D.getApplication().has(Feature.MOB_STANDARD_VIEW_BUTTON_3D)) {
			view3D.getEuclidianController().onCoordSystemChanged();
		}
		animation = null;
		animationList.clear();
	}

	/**
	 * ends the current animation
	 */
	synchronized public void endAnimation() {
		if (view3D.getApplication().has(Feature.MOB_STANDARD_VIEW_BUTTON_3D)) {
			view3D.getEuclidianController().onCoordSystemChanged();
		}
		animation = animationList.poll();
		if (animation != null) {
			animation.setupForStart();
		}
	}

	/**
	 * 
	 * @return animation type
	 */
	public AnimationType getAnimationType() {
		if (animation != null) {
			return animation.getType();
		}
		return AnimationType.OFF;
	}

	synchronized private void addAnimation(EuclidianView3DAnimation anim) {
		if (animation == null || animation.getType() == AnimationType.CONTINUE_ROTATION) {
			animation = anim;
			animation.setupForStart();
		} else {
			animationList.add(anim);
		}
	}

}
