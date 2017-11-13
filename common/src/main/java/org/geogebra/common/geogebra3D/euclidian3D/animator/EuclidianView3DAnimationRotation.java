package org.geogebra.common.geogebra3D.euclidian3D.animator;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.animator.EuclidianView3DAnimator.AnimationType;
import org.geogebra.common.kernel.Kernel;

/**
 * animation for rotation
 *
 */
public class EuclidianView3DAnimationRotation extends EuclidianView3DAnimation {

	private double animatedRotTimeStart;
	private double aOld, bOld;
	private boolean checkSameValues;
	private double aNew, bNew;

	/**
	 * 
	 * @param view3D
	 * @param animator
	 * @param aN
	 * @param bN
	 * @param checkSameValues
	 */
	public EuclidianView3DAnimationRotation(EuclidianView3D view3D, EuclidianView3DAnimator animator, double aN,
			double bN, boolean checkSameValues) {

		super(view3D, animator);
		this.checkSameValues = checkSameValues;

		aNew = aN;
		bNew = bN;
	}

	public void setupForStart() {
		aOld = view3D.getAngleA() % 360;
		bOld = view3D.getAngleB() % 360;


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

	public AnimationType getType() {
		return AnimationType.ROTATION;
	}

	public void animate() {
		double t = (view3D.getApplication().getMillisecondTime() - animatedRotTimeStart) * 0.001;
		t *= t;
		// t+=0.2; //starting at 1/4
		if (t >= 1) {
			t = 1;
			end();
		}
		view3D.setRotXYinDegrees(aOld * (1 - t) + aNew * t, bOld * (1 - t) + bNew * t);
		view3D.updateMatrix();
		view3D.setViewChangedByRotate();
	}


}
