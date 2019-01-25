package org.geogebra.common.geogebra3D.euclidian3D.animator;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.util.DoubleUtil;

/**
 * animation for zoom
 *
 */
public class EuclidianView3DAnimationAxesRatio extends EuclidianView3DAnimationScaleAbstract {
	
	private double zoomFactorY;
	private double zoomFactorZ;

	/**
	 * 
	 * @param view3D 3D view
	 * @param animator animator
	 * @param zoomFactorY zoom factor (y over x)
	 * @param zoomFactorZ zoom factor (z over x)
	 */
	EuclidianView3DAnimationAxesRatio(EuclidianView3D view3D, EuclidianView3DAnimator animator,
			double zoomFactorY, double zoomFactorZ) {

		super(view3D, animator);
		this.zoomFactorY = zoomFactorY;
		this.zoomFactorZ = zoomFactorZ;
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
		xScaleEnd = xScaleStart;
		if (Double.isNaN(zoomFactorY) || DoubleUtil.isGreaterEqual(0, zoomFactorY)) {
			yScaleEnd = yScaleStart;
		} else {
			yScaleEnd = xScaleStart * zoomFactorY;
		}
		if (Double.isNaN(zoomFactorZ) || DoubleUtil.isGreaterEqual(0, zoomFactorZ)) {
			zScaleEnd = zScaleStart;
		} else {
			zScaleEnd = xScaleStart * zoomFactorZ;
		}
		animatedScaleTimeFactor = ANIMATION_DURATION;

	}

}
