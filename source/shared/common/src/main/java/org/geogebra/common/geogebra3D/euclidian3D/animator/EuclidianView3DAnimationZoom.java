package org.geogebra.common.geogebra3D.euclidian3D.animator;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;

/**
 * animation for zoom
 *
 */
public class EuclidianView3DAnimationZoom extends EuclidianView3DAnimationScaleAbstract {

	private double zoomFactor;
	
	/**
	 * 
	 * @param view3D 3D view
	 * @param animator animator
	 * @param zoomFactor zoom factor
	 */
	EuclidianView3DAnimationZoom(EuclidianView3D view3D, EuclidianView3DAnimator animator,
			double zoomFactor) {
		super(view3D, animator);
		this.zoomFactor = zoomFactor;
	}

	@Override
	public void setupForStart() {
		animatedScaleStartX = view3D.getXZero();
		animatedScaleStartY = view3D.getYZero();
		animatedScaleStartZ = view3D.getZZero();

		animatedScaleEndX = animatedScaleStartX;
		animatedScaleEndY = animatedScaleStartY;
		animatedScaleEndZ = animatedScaleStartZ;

		animatedScaleTimeStart = getMillisecondTime();

		xScaleStart = view3D.getXscale();
		yScaleStart = view3D.getYscale();
		zScaleStart = view3D.getZscale();
		xScaleEnd = xScaleStart * zoomFactor;
		yScaleEnd = yScaleStart * zoomFactor;
		zScaleEnd = zScaleStart * zoomFactor;

		animatedScaleTimeFactor = ANIMATION_DURATION;
	}

}
