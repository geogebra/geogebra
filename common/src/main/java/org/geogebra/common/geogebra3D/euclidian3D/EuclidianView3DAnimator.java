package org.geogebra.common.geogebra3D.euclidian3D;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.CoordMatrix4x4;
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
		OFF, ANIMATED_SCALE, SCALE, CONTINUE_ROTATION, ROTATION, ROTATION_NO_ANIMATION, SCREEN_TRANSLATE_AND_SCALE, MOUSE_MOVE, AXIS_SCALE
	}

	private EuclidianView3D view3D;

	private double xZeroOld, yZeroOld, zZeroOld;
	private double aOld, bOld;
	private double xScaleStart, yScaleStart, zScaleStart;
	private double xScaleEnd, yScaleEnd, zScaleEnd;
	private double animatedScaleTimeFactor, animatedScaleTimeStart;
	private double animatedScaleStartX, animatedScaleStartY, animatedScaleStartZ;
	private double animatedScaleEndX, animatedScaleEndY, animatedScaleEndZ;
	private double animatedRotSpeed, animatedRotTimeStart;
	private double axisScaleFactor, axisScaleOld;
	private int axisScaleMode;
	private int mouseMoveDX, mouseMoveDY, mouseMoveMode;
	private double screenTranslateAndScaleDX, screenTranslateAndScaleDY, screenTranslateAndScaleDZ;

	private double aNew, bNew;

	private Coords tmpCoords1 = new Coords(4);

	private AnimationType animationType = AnimationType.OFF;

	/**
	 * 
	 * @param view3D
	 *            3D view
	 */
	public EuclidianView3DAnimator(EuclidianView3D view3D) {
		this.view3D = view3D;
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
		aOld = view3D.getAngleA();
		bOld = view3D.getAngleB();
		zZeroOld = view3D.getZZero();
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
		rememberOrigins();
		animatedScaleStartX = view3D.getXZero();
		animatedScaleStartY = view3D.getYZero();
		animatedScaleStartZ = view3D.getZZero();

		animatedScaleEndX = x;
		animatedScaleEndY = y;
		animatedScaleEndZ = z;

		animatedScaleTimeStart = view3D.getApplication().getMillisecondTime();
		xScaleEnd = newScale;
		yScaleEnd = newScale;
		zScaleEnd = newScale;
		animationType = AnimationType.ANIMATED_SCALE;

		animatedScaleTimeFactor = 0.0003 * steps;
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

		rememberOrigins();
		animatedScaleStartX = view3D.getXZero();
		animatedScaleStartY = view3D.getYZero();
		animatedScaleStartZ = view3D.getZZero();

		Coords v;
		if (view3D.getCursor3DType() == EuclidianView3D.PREVIEW_POINT_NONE) { // use cursor only if on
			// point/path/region or xOy plane
			v = new Coords(-animatedScaleStartX, -animatedScaleStartY, -animatedScaleStartZ, 1);
			// takes center of the scene for fixed point
		} else {
			v = view3D.getCursor3D().getInhomCoords();
			if (!v.isDefined()) {
				v = new Coords(-animatedScaleStartX, -animatedScaleStartY, -animatedScaleStartZ, 1);
				// takes center of the scene for fixed point
			}
		}

		// Application.debug(v);

		double factor = view3D.getXscale() / newScale;

		animatedScaleEndX = -v.getX() + (animatedScaleStartX + v.getX()) * factor;
		animatedScaleEndY = -v.getY() + (animatedScaleStartY + v.getY()) * factor;
		animatedScaleEndZ = -v.getZ() + (animatedScaleStartZ + v.getZ()) * factor;

		animatedScaleTimeStart = view3D.getApplication().getMillisecondTime();
		xScaleEnd = xScaleStart / factor;
		yScaleEnd = yScaleStart / factor;
		zScaleEnd = zScaleStart / factor;
		animationType = AnimationType.ANIMATED_SCALE;

		animatedScaleTimeFactor = 0.005; // it will take about 1/2s to achieve it

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

		double rotSpeed2 = rotSpeed;
		// if last drag occured more than 200ms ago, then no animation
		if (delay > 200) {
			return;
		}

		// if speed is too small, no animation
		if (Math.abs(rotSpeed2) < 0.01) {
			stopAnimation();
			return;
		}

		// if speed is too large, use max speed
		if (rotSpeed2 > 0.1) {
			rotSpeed2 = 0.1;
		} else if (rotSpeed2 < -0.1) {
			rotSpeed2 = -0.1;
		}
		view3D.getSettings().setRotSpeed(0);
		animationType = AnimationType.CONTINUE_ROTATION;
		animatedRotSpeed = -rotSpeed2;
		animatedRotTimeStart = view3D.getApplication().getMillisecondTime() - delay;
		bOld = view3D.getAngleB();
		aOld = view3D.getAngleA();
		view3D.rememberOrigins();
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
		mouseMoveDX = dx;
		mouseMoveDY = dy;
		mouseMoveMode = mode;
		animationType = AnimationType.MOUSE_MOVE;
	}

	final private void processSetCoordSystemFromMouseMove() {
		switch (mouseMoveMode) {
		default:
			// do nothing
			break;
		case EuclidianController.MOVE_ROTATE_VIEW:
			view3D.setRotXYinDegrees(aOld - mouseMoveDX, bOld + mouseMoveDY);
			view3D.updateMatrix();
			view3D.setViewChangedByRotate();
			view3D.setWaitForUpdate();
			break;
		case EuclidianController.MOVE_VIEW:
			Coords v = new Coords(mouseMoveDX, -mouseMoveDY, 0, 0);
			view3D.toSceneCoords3D(v);

			if (view3D.cursorOnXOYPlane.getRealMoveMode() == GeoPointND.MOVE_MODE_XY) {
				v.projectPlaneThruVIfPossible(CoordMatrix4x4.IDENTITY, view3D.getViewDirection(), tmpCoords1);
				view3D.setXZero(xZeroOld + tmpCoords1.getX());
				view3D.setYZero(yZeroOld + tmpCoords1.getY());
			} else {
				v.projectPlaneInPlaneCoords(CoordMatrix4x4.IDENTITY, tmpCoords1);
				view3D.setZZero(zZeroOld + tmpCoords1.getZ());
			}
			view3D.getSettings().updateOriginFromView(view3D.getXZero(), view3D.getYZero(), view3D.getZZero());
			view3D.updateMatrix();
			view3D.setViewChangedByTranslate();
			view3D.setWaitForUpdate();
			break;
		}
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
		axisScaleFactor = factor;
		axisScaleOld = scaleOld;
		axisScaleMode = mode;
		animationType = AnimationType.AXIS_SCALE;
	}

	final private void processSetCoordSystemFromAxisScale() {

		switch (axisScaleMode) {
		default:
			// do nothing
			break;
		case EuclidianController.MOVE_X_AXIS:
			view3D.setXZero(xZeroOld / axisScaleFactor);
			view3D.getSettings().setXscaleValue(axisScaleFactor * axisScaleOld);
			break;
		case EuclidianController.MOVE_Y_AXIS:
			view3D.setYZero(yZeroOld / axisScaleFactor);
			view3D.getSettings().setYscaleValue(axisScaleFactor * axisScaleOld);
			break;
		case EuclidianController.MOVE_Z_AXIS:
			view3D.setZZero(zZeroOld / axisScaleFactor);
			view3D.getSettings().setZscaleValue(axisScaleFactor * axisScaleOld);
			break;

		}

		view3D.getSettings().updateOriginFromView(view3D.getXZero(), view3D.getYZero(), view3D.getZZero());

		view3D.updateMatrix();
		view3D.setViewChangedByTranslate();
		view3D.setViewChangedByZoom();
		view3D.setWaitForUpdate();

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
		aOld = view3D.getAngleA() % 360;
		bOld = view3D.getAngleB() % 360;

		aNew = aN;
		bNew = bN;

		// if (aNew,bNew)=(0degrees,90degrees), then change it to
		// (90degrees,90degrees) to have correct
		// xOy orientation
		if (Kernel.isEqual(aNew, 0, Kernel.STANDARD_PRECISION)
				&& Kernel.isEqual(Math.abs(bNew), 90, Kernel.STANDARD_PRECISION)) {
			aNew = -90;
		}

		// looking for the smallest path
		if (aOld - aNew > 180) {
			aOld -= 360;
		} else if (aOld - aNew < -180) {
			aOld += 360;
		}

		if (checkSameValues) {
			if (Kernel.isEqual(aOld, aNew, Kernel.STANDARD_PRECISION)) {
				if (Kernel.isEqual(bOld, bNew, Kernel.STANDARD_PRECISION)) {
					if (!Kernel.isEqual(Math.abs(bNew), 90, Kernel.STANDARD_PRECISION)) {
						aNew += 180;
					}
					bNew *= -1;
				}
			}
		}

		if (bOld > 180) {
			bOld -= 360;
		}

		animatedRotTimeStart = view3D.getApplication().getMillisecondTime();

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

		animatedScaleEndX = view3D.getXscale() * zoomFactor;
		animatedScaleEndY = view3D.getYscale() * zoomFactor;
		animatedScaleEndZ = view3D.getZscale() * zoomFactor;
		animationType = AnimationType.SCALE;

	}

	/**
	 * zoom y & z axes ratio regarding x axis
	 * 
	 * @param zoomFactorY
	 * @param zoomFactorZ
	 */
	public void zoomAxesRatio(double zoomFactorY, double zoomFactorZ) {

		rememberOrigins();
		animatedScaleStartX = view3D.getXZero();
		animatedScaleStartY = view3D.getYZero();
		animatedScaleStartZ = view3D.getZZero();

		animatedScaleEndX = animatedScaleStartX;
		animatedScaleEndY = animatedScaleStartY;
		animatedScaleEndZ = animatedScaleStartZ;

		animatedScaleTimeStart = view3D.getApplication().getMillisecondTime();

		xScaleEnd = view3D.getXscale();
		if (Double.isNaN(zoomFactorY) || Kernel.isGreaterEqual(0, zoomFactorY)) {
			yScaleEnd = view3D.getYscale();
		} else {
			yScaleEnd = view3D.getXscale() * zoomFactorY;
		}
		if (Double.isNaN(zoomFactorZ) || Kernel.isGreaterEqual(0, zoomFactorZ)) {
			zScaleEnd = view3D.getZscale();
		} else {
			zScaleEnd = view3D.getXscale() * zoomFactorZ;
		}
		animationType = AnimationType.ANIMATED_SCALE;
		animatedScaleTimeFactor = 0.005; // it will take about 1/2s to achieve

	}

	/**
	 * animate the view for changing scale, orientation, etc.
	 */
	public void animate() {
		switch (animationType) {
		default:
			// do nothing
			break;
		case SCALE:
			view3D.setScale(animatedScaleEndX, animatedScaleEndY, animatedScaleEndZ);
			view3D.updateMatrix();
			stopAnimation();
			break;
		case ANIMATED_SCALE:
			double t;
			if (animatedScaleTimeFactor == 0) {
				t = 1;
				stopAnimation();
			} else {
				t = (view3D.getApplication().getMillisecondTime() - animatedScaleTimeStart) * animatedScaleTimeFactor;
				t += 0.2; // starting at 1/4

				if (t >= 1) {
					t = 1;
					stopAnimation();
				}
			}
			view3D.setScale(xScaleStart * (1 - t) + xScaleEnd * t, yScaleStart * (1 - t) + yScaleEnd * t,
					zScaleStart * (1 - t) + zScaleEnd * t);
			view3D.setXZero(animatedScaleStartX * (1 - t) + animatedScaleEndX * t);
			view3D.setYZero(animatedScaleStartY * (1 - t) + animatedScaleEndY * t);
			view3D.setZZero(animatedScaleStartZ * (1 - t) + animatedScaleEndZ * t);
			view3D.getSettings().updateOriginFromView(view3D.getXZero(), view3D.getYZero(), view3D.getZZero());

			view3D.updateMatrix();
			view3D.setViewChangedByZoom();
			view3D.setViewChangedByTranslate();
			view3D.getEuclidianController().onCoordSystemChanged();
			// euclidianController3D.setFlagMouseMoved();
			break;

		case CONTINUE_ROTATION:
			double da = (view3D.getApplication().getMillisecondTime() - animatedRotTimeStart) * animatedRotSpeed;

			view3D.shiftRotAboutZ(da);
			break;

		case ROTATION:
			t = (view3D.getApplication().getMillisecondTime() - animatedRotTimeStart) * 0.001;
			t *= t;
			// t+=0.2; //starting at 1/4

			if (t >= 1) {
				t = 1;
				stopAnimation();
			}

			view3D.setRotXYinDegrees(aOld * (1 - t) + aNew * t, bOld * (1 - t) + bNew * t);

			view3D.updateMatrix();
			view3D.setViewChangedByRotate();
			break;

		case ROTATION_NO_ANIMATION:
			stopAnimation();
			view3D.setRotXYinDegrees(aNew, bNew);
			view3D.updateMatrix();
			view3D.setViewChangedByRotate();
			break;

		case SCREEN_TRANSLATE_AND_SCALE:
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
			break;

		case MOUSE_MOVE:
			processSetCoordSystemFromMouseMove();
			stopAnimation();
			break;

		case AXIS_SCALE:
			processSetCoordSystemFromAxisScale();
			stopAnimation();
			break;
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
