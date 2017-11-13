package org.geogebra.common.geogebra3D.euclidian3D.animator;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.animator.EuclidianView3DAnimator.AnimationType;
import org.geogebra.common.kernel.Kernel;

/**
 * animation for rotation in one step
 *
 */
public class EuclidianView3DAnimationRotationOneStep extends EuclidianView3DAnimation {

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
	public EuclidianView3DAnimationRotationOneStep(EuclidianView3D view3D, EuclidianView3DAnimator animator, double aN,
			double bN, boolean checkSameValues) {

		super(view3D, animator);
		this.checkSameValues = checkSameValues;
		aNew = aN;
		bNew = bN;
	}

	public void setupForStart() {
		// if (aNew,bNew)=(0degrees,90degrees), then change it to
		// (90degrees,90degrees) to have correct
		// xOy orientation
		if (Kernel.isEqual(aNew, 0, Kernel.STANDARD_PRECISION)
				&& Kernel.isEqual(Math.abs(bNew), 90, Kernel.STANDARD_PRECISION)) {
			aNew = -90;
		}

		if (checkSameValues) {
			double aOld = view3D.getAngleA() % 360;
			double bOld = view3D.getAngleB() % 360;
			// looking for the smallest path
			if (aOld - aNew > 180) {
				aOld -= 360;
			} else if (aOld - aNew < -180) {
				aOld += 360;
			}

			if (Kernel.isEqual(aOld, aNew, Kernel.STANDARD_PRECISION)) {
				if (Kernel.isEqual(bOld, bNew, Kernel.STANDARD_PRECISION)) {
					if (!Kernel.isEqual(Math.abs(bNew), 90, Kernel.STANDARD_PRECISION)) {
						aNew += 180;
					}
					bNew *= -1;
				}
			}
		}
	}

	public AnimationType getType() {
		return AnimationType.ROTATION_NO_ANIMATION;
	}

	public void animate() {
		view3D.setRotXYinDegrees(aNew, bNew);
		view3D.updateMatrix();
		view3D.setViewChangedByRotate();
		end();
	}

}
