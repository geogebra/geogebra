package org.geogebra.common.geogebra3D.euclidian3D.animator;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.animator.EuclidianView3DAnimator.AnimationType;

/**
 * animation for scale
 *
 */
abstract public class EuclidianView3DAnimationScaleAbstract extends EuclidianView3DAnimation {

	@SuppressWarnings("javadoc")
	double xScaleStart, yScaleStart, zScaleStart, xScaleEnd, yScaleEnd, zScaleEnd, animatedScaleTimeFactor,
			animatedScaleTimeStart, animatedScaleStartX, animatedScaleStartY, animatedScaleStartZ, animatedScaleEndX,
			animatedScaleEndY, animatedScaleEndZ;


	/**
	 * 
	 * @param view3D 3D view
	 * @param animator animator
	 */
	EuclidianView3DAnimationScaleAbstract(EuclidianView3D view3D, EuclidianView3DAnimator animator) {
		super(view3D, animator);
	}

	public AnimationType getType() {
		return AnimationType.ANIMATED_SCALE;
	}

	public void animate() {
		double t;
		boolean ending = false;
		if (animatedScaleTimeFactor == 0) {
			t = 1;
			ending = true;
		} else {
			t = (view3D.getApplication().getMillisecondTime() - animatedScaleTimeStart) * animatedScaleTimeFactor;
			t += 0.2; // starting at 1/4

			if (t >= 1) {
				t = 1;
				ending = true;
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

		if (ending) {
			end();
		}
	}

}
