package org.geogebra.common.geogebra3D.euclidian3D.animator;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.animator.EuclidianView3DAnimator.AnimationType;
import org.geogebra.common.kernel.Matrix.Coords;

/**
 * animation for pinch zoom + translation
 *
 */
public class EuclidianView3DAnimationScreenScale extends EuclidianView3DAnimation {

	private double xZeroOld, yZeroOld, zZeroOld;
	private double xScaleStart, yScaleStart, zScaleStart;
	private double xScaleEnd, yScaleEnd, zScaleEnd;
	private double screenTranslateAndScaleDX, screenTranslateAndScaleDY, screenTranslateAndScaleDZ;
	private Coords tmpCoords1 = new Coords(4);

	/**
	 * 
	 * @param view3D 3D view
	 * @param animator animator
	 */
	EuclidianView3DAnimationScreenScale(EuclidianView3D view3D, EuclidianView3DAnimator animator) {

		super(view3D, animator);

	}

	/**
	 * 
	 * @param dx
	 *            translation x in screen coordinates
	 * @param dy
	 *            translation y in screen coordinates
	 * @param scaleFactor
	 *            scale factor
	 */
	public void set(double dx, double dy, double scaleFactor) {

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
	}

	/**
	 * store values at start
	 */
	public void rememberOrigins() {
		xZeroOld = view3D.getXZero();
		yZeroOld = view3D.getYZero();
		zZeroOld = view3D.getZZero();
		xScaleStart = view3D.getXscale();
		yScaleStart = view3D.getYscale();
		zScaleStart = view3D.getZscale();
	}

	public void setupForStart() {
		// nothing to do
	}

	public AnimationType getType() {
		return AnimationType.SCREEN_TRANSLATE_AND_SCALE;
	}

	public void animate() {
		view3D.setXZero(xZeroOld + screenTranslateAndScaleDX);
		view3D.setYZero(yZeroOld + screenTranslateAndScaleDY);
		view3D.setZZero(zZeroOld + screenTranslateAndScaleDZ);
		view3D.getSettings().updateOriginFromView(view3D.getXZero(), view3D.getYZero(), view3D.getZZero());
		view3D.setScale(xScaleEnd, yScaleEnd, zScaleEnd);
		view3D.updateMatrix();
		view3D.setViewChangedByZoom();
		view3D.setViewChangedByTranslate();

		end();
	}

}
