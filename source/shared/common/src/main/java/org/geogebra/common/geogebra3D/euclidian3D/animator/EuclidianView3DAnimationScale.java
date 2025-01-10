package org.geogebra.common.geogebra3D.euclidian3D.animator;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * animation for scale (no translation)
 *
 */
public class EuclidianView3DAnimationScale extends EuclidianView3DAnimationScaleAbstract {

	/**
	 * 
	 * @param view3D 3D view
	 * @param animator animator
	 */
	EuclidianView3DAnimationScale(EuclidianView3D view3D, EuclidianView3DAnimator animator) {
		super(view3D, animator);
	}

	@Override
	public void setupForStart() {
		// nothing to do
	}

	/**
	 * 
	 * @param newScale
	 *            new scale
	 */
	public void set(double newScale) {
		xScaleStart = view3D.getXscale();
		yScaleStart = view3D.getYscale();
		zScaleStart = view3D.getZscale();
		animatedScaleStartX = view3D.getXZero();
		animatedScaleStartY = view3D.getYZero();
		animatedScaleStartZ = view3D.getZZero();

		Coords v;
		if (view3D.getCursor3DType() == EuclidianView3D.PREVIEW_POINT_NONE) {
			// use cursor only if on point/path/region or xOy plane
			v = new Coords(-animatedScaleStartX, -animatedScaleStartY, -animatedScaleStartZ, 1);
			// takes center of the scene for fixed point
		} else {
			v = view3D.getCursor3D().getInhomCoords();
			double[] zRange = view3D.getClippingCubeDrawable().getMinMax()[2];
			if (!v.isDefined() || v.getZ() > zRange[1] || v.getZ() < zRange[0]) {
				v = new Coords(-animatedScaleStartX, -animatedScaleStartY, -animatedScaleStartZ, 1);
				// takes center of the scene for fixed point
			}
		}

		double factor = view3D.getXscale() / newScale;

		animatedScaleEndX = -v.getX() + (animatedScaleStartX + v.getX()) * factor;
		animatedScaleEndY = -v.getY() + (animatedScaleStartY + v.getY()) * factor;
		animatedScaleEndZ = -v.getZ() + (animatedScaleStartZ + v.getZ()) * factor;

		animatedScaleTimeStart = getMillisecondTime();
		xScaleEnd = xScaleStart / factor;
		yScaleEnd = yScaleStart / factor;
		zScaleEnd = zScaleStart / factor;

		animatedScaleTimeFactor = ANIMATION_DURATION;
	}

}
