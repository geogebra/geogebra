package org.geogebra.common.geogebra3D.euclidian3D.animator;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.animator.EuclidianView3DAnimator.AnimationType;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * animation for mouse move
 *
 */
public class EuclidianView3DAnimationMouseMove extends EuclidianView3DAnimation {

	private int mouseMoveDX;
	private int mouseMoveDY;
	private int mouseMoveMode;
	private double aOld;
	private double bOld;
	private double xZeroOld;
	private double yZeroOld;
	private double zZeroOld;
	private Coords tmpCoords1 = new Coords(4);
	private Coords hittingDirection = new Coords(4);
	private Coords hittingOrigin = Coords.createInhomCoorsInD3();
	private Coords startTouchOnXOYPlane = Coords.createInhomCoorsInD3();
	private boolean canUseStartTouchOnXOYPlane;
	private Coords moveTouchOnXOYPlane = Coords.createInhomCoorsInD3();
	private Coords translation = Coords.createInhomCoorsInD3();

	/**
	 *
	 * @param view3D 3D view
	 * @param animator animator
	 */
	EuclidianView3DAnimationMouseMove(EuclidianView3D view3D, EuclidianView3DAnimator animator) {
		super(view3D, animator);
	}

	/**
	 * remembers original values
	 */
	public void rememberOrigins() {
		aOld = view3D.getAngleA();
		bOld = view3D.getAngleB();
		xZeroOld = view3D.getXZero();
		yZeroOld = view3D.getYZero();
		zZeroOld = view3D.getZZero();
		if (view3D.isXREnabled()) {
			view3D.getHittingDirection(hittingDirection);
			view3D.getHittingOrigin(null, hittingOrigin);
            hittingOrigin.projectPlaneThruV(CoordMatrix4x4.IDENTITY, hittingDirection,
                    startTouchOnXOYPlane);
            // maybe hittingDirection is parallel to xOy plane
            canUseStartTouchOnXOYPlane = !DoubleUtil.isZero(startTouchOnXOYPlane.getW());
		}
	}

	/**
	 *
	 *
	 * @param dx
	 *            mouse delta x
	 * @param dy
	 *            mouse delta y
	 * @param mode
	 *            mouse mode
	 */
	public void set(int dx, int dy, int mode) {
		mouseMoveDX = dx;
		mouseMoveDY = dy;
		mouseMoveMode = mode;
	}

	@Override
	public void setupForStart() {
		// nothing to do
	}

	@Override
	public AnimationType getType() {
		return AnimationType.MOUSE_MOVE;
	}

	@Override
	public void animate() {
		switch (mouseMoveMode) {
		case EuclidianController.MOVE_ROTATE_VIEW:
			view3D.setRotXYinDegrees(aOld - mouseMoveDX, bOld + mouseMoveDY);
			view3D.updateMatrix();
			view3D.setViewChangedByRotate();
			break;
		case EuclidianController.MOVE_VIEW:
			if (view3D.isZoomable()) {
				boolean changed = false;
				if (view3D.getCursorOnXOYPlane().getRealMoveMode() == GeoPointND.MOVE_MODE_XY) {
					if (view3D.isXREnabled()) {
						if (canUseStartTouchOnXOYPlane) {
							view3D.getHittingOrigin(null, hittingOrigin);
							view3D.getHittingDirection(hittingDirection);
							hittingOrigin.projectPlaneThruV(CoordMatrix4x4.IDENTITY,
									hittingDirection,
									moveTouchOnXOYPlane);
							// maybe hittingDirection is parallel to xOy plane
							if (!DoubleUtil.isZero(moveTouchOnXOYPlane.getW())) {
								translation.setSub3(moveTouchOnXOYPlane, startTouchOnXOYPlane);
								xZeroOld += translation.getX();
								yZeroOld += translation.getY();
								view3D.setXZero(xZeroOld);
								view3D.setYZero(yZeroOld);
								changed = true;
							}
						}
					} else {
						setTranslationFromMouseMove();
						translation.projectPlaneThruVIfPossible(CoordMatrix4x4.IDENTITY, view3D
								.getViewDirection(), tmpCoords1);
						view3D.setXZero(xZeroOld + tmpCoords1.getX());
						view3D.setYZero(yZeroOld + tmpCoords1.getY());
						changed = true;
					}
				} else {
					setTranslationFromMouseMove();
					translation.projectPlaneInPlaneCoords(CoordMatrix4x4.IDENTITY, tmpCoords1);
					view3D.setZZero(zZeroOld + tmpCoords1.getZ());
					changed = true;
				}
				if (changed) {
					view3D.getSettings()
							.updateOriginFromView(view3D.getXZero(), view3D.getYZero(),
									view3D.getZZero());
					view3D.updateMatrix();
					view3D.setViewChangedByTranslate();
				}
			}
			break;
		default:
			// do nothing
			break;
		}
		end();
	}

	private void setTranslationFromMouseMove() {
	    translation.setX(mouseMoveDX);
	    translation.setY(-mouseMoveDY);
	    translation.setZ(0);
	    translation.setW(0);
        view3D.toSceneCoords3D(translation);
    }

	@Override
	protected boolean animationAllowed() {
		return true;
	}
}
