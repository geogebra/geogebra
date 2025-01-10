package org.geogebra.common.geogebra3D.euclidian3D.animator;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.animator.EuclidianView3DAnimator.AnimationType;

/**
 * animation for scale
 *
 */
abstract public class EuclidianView3DAnimationScaleAbstract extends EuclidianView3DAnimation {

	protected double xScaleStart;
	protected double yScaleStart;
	protected double zScaleStart;
	protected double xScaleEnd;
	protected double yScaleEnd;
	protected double zScaleEnd;
	protected double animatedScaleTimeFactor;
	protected double animatedScaleTimeStart;
	protected double animatedScaleStartX;
	protected double animatedScaleStartY;
	protected double animatedScaleStartZ;
	protected double animatedScaleEndX;
	protected double animatedScaleEndY;
	protected double animatedScaleEndZ;

	private static final double TIME_SHIFT = 0.2;

	/**
	 * 
	 * @param view3D 3D view
	 * @param animator animator
	 */
	EuclidianView3DAnimationScaleAbstract(EuclidianView3D view3D,
			EuclidianView3DAnimator animator) {
		super(view3D, animator);
	}

	@Override
	public AnimationType getType() {
		return AnimationType.ANIMATED_SCALE;
	}

	@Override
	public void animate() {
		double t;
		boolean ending = false;
		if (animatedScaleTimeFactor == 0) {
			t = 1;
			ending = true;
		} else {
			t = (getMillisecondTime() - animatedScaleTimeStart)
					* animatedScaleTimeFactor;
			t += TIME_SHIFT;

			if (t >= 1) {
				t = 1;
				ending = true;
			}
		}
		view3D.setScale(xScaleStart * (1 - t) + xScaleEnd * t,
				yScaleStart * (1 - t) + yScaleEnd * t, zScaleStart * (1 - t) + zScaleEnd * t);
		view3D.setXZero(animatedScaleStartX * (1 - t) + animatedScaleEndX * t);
		view3D.setYZero(animatedScaleStartY * (1 - t) + animatedScaleEndY * t);
		view3D.setZZero(animatedScaleStartZ * (1 - t) + animatedScaleEndZ * t);
		view3D.getSettings().updateOriginFromView(view3D.getXZero(), view3D.getYZero(),
				view3D.getZZero());

		view3D.updateMatrix();
		view3D.setViewChangedByZoom();
		view3D.setViewChangedByTranslate();

		if (ending) {
			end();
		}
	}

}
