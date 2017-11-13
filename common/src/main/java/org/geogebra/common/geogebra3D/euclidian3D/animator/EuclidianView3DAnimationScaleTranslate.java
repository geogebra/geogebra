package org.geogebra.common.geogebra3D.euclidian3D.animator;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;

/**
 * animation for scale and translate
 *
 */
public class EuclidianView3DAnimationScaleTranslate extends EuclidianView3DAnimationScaleAbstract {

	/**
	 * 
	 * @param view3D
	 * @param animator
	 * @param x
	 * @param y
	 * @param z
	 * @param newScale
	 * @param steps
	 */
	public EuclidianView3DAnimationScaleTranslate(EuclidianView3D view3D, EuclidianView3DAnimator animator, double x, double y,
			double z, double newScale, int steps) {
		super(view3D, animator);
		animatedScaleEndX = x;
		animatedScaleEndY = y;
		animatedScaleEndZ = z;
		xScaleEnd = newScale;
		yScaleEnd = newScale;
		zScaleEnd = newScale;
		animatedScaleTimeFactor = 0.0003 * steps;
	}

	public void setupForStart() {
		xScaleStart = view3D.getXscale();
		yScaleStart = view3D.getYscale();
		zScaleStart = view3D.getZscale();
		animatedScaleStartX = view3D.getXZero();
		animatedScaleStartY = view3D.getYZero();
		animatedScaleStartZ = view3D.getZZero();

		animatedScaleTimeStart = view3D.getApplication().getMillisecondTime();
	}

}
