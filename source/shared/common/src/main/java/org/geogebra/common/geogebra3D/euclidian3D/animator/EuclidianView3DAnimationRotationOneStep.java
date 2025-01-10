package org.geogebra.common.geogebra3D.euclidian3D.animator;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.animator.EuclidianView3DAnimator.AnimationType;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.util.DoubleUtil;

/**
 * animation for rotation in one step
 *
 */
public class EuclidianView3DAnimationRotationOneStep extends EuclidianView3DAnimation {

	private boolean checkSameValues;
	private double aNew;
	private double bNew;

	/**
	 * @param view3D 3D view
	 * @param animator animator
	 * @param aN new angle around Oz
	 * @param bN new xOy plane tilting
	 * @param checkSameValues if we want to check when new values are equal to current
	 * @param storeUndo if undo should be stored at the end of animation
	 */
	EuclidianView3DAnimationRotationOneStep(EuclidianView3D view3D,
			EuclidianView3DAnimator animator, double aN, double bN, boolean checkSameValues,
			boolean storeUndo) {

		super(view3D, animator, storeUndo);
		this.checkSameValues = checkSameValues;
		aNew = aN;
		bNew = bN;
	}

	@Override
	public void setupForStart() {
		// if (aNew,bNew)=(0degrees,90degrees), then change it to
		// (90degrees,90degrees) to have correct
		// xOy orientation
		if (DoubleUtil.isEqual(aNew, 0, Kernel.STANDARD_PRECISION)
				&& DoubleUtil.isEqual(Math.abs(bNew), 90, Kernel.STANDARD_PRECISION)) {
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

			if (DoubleUtil.isEqual(aOld, aNew, Kernel.STANDARD_PRECISION)) {
				if (DoubleUtil.isEqual(bOld, bNew, Kernel.STANDARD_PRECISION)) {
					if (!DoubleUtil.isEqual(Math.abs(bNew), 90, Kernel.STANDARD_PRECISION)) {
						aNew += 180;
					}
					bNew *= -1;
				}
			}
		}
	}

	@Override
	public AnimationType getType() {
		return AnimationType.ROTATION_NO_ANIMATION;
	}

	@Override
	public void animate() {
		view3D.setRotXYinDegrees(aNew, bNew);
		view3D.updateMatrix();
		view3D.setViewChangedByRotate();
		end();
	}

	@Override
	protected boolean animationAllowed() {
		return true;
	}
}
