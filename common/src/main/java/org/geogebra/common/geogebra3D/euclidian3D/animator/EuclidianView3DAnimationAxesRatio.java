package org.geogebra.common.geogebra3D.euclidian3D.animator;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.Kernel;

/**
 * animation for zoom
 *
 */
public class EuclidianView3DAnimationAxesRatio extends EuclidianView3DAnimationScaleAbstract {
	
	private double zoomFactorY, zoomFactorZ;

	/**
	 * 
	 * @param view3D
	 * @param animator
	 * @param zoomFactorY
	 * @param zoomFactorZ
	 */
	public EuclidianView3DAnimationAxesRatio(EuclidianView3D view3D, EuclidianView3DAnimator animator,
			double zoomFactorY, double zoomFactorZ) {

		super(view3D, animator);

		// zoomAxesRatio - 3
		this.zoomFactorY = zoomFactorY;
		this.zoomFactorZ = zoomFactorZ;

	}

	public void setupForStart() {
		animatedScaleStartX = view3D.getXZero();
		animatedScaleStartY = view3D.getYZero();
		animatedScaleStartZ = view3D.getZZero();

		animatedScaleEndX = animatedScaleStartX;
		animatedScaleEndY = animatedScaleStartY;
		animatedScaleEndZ = animatedScaleStartZ;

		animatedScaleTimeStart = view3D.getApplication().getMillisecondTime();

		xScaleStart = view3D.getXscale();
		yScaleStart = view3D.getYscale();
		zScaleStart = view3D.getZscale();
		xScaleEnd = xScaleStart;
		if (Double.isNaN(zoomFactorY) || Kernel.isGreaterEqual(0, zoomFactorY)) {
			yScaleEnd = yScaleStart;
		} else {
			yScaleEnd = xScaleStart * zoomFactorY;
		}
		if (Double.isNaN(zoomFactorZ) || Kernel.isGreaterEqual(0, zoomFactorZ)) {
			zScaleEnd = zScaleStart;
		} else {
			zScaleEnd = xScaleStart * zoomFactorZ;
		}
		animatedScaleTimeFactor = 0.005; // it will take about 1/2s to achieve

	}

}
