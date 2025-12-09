/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.geogebra3D.euclidian3D.animator;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.animator.EuclidianView3DAnimator.AnimationType;

/**
 * animation for continue rotation
 *
 */
public class EuclidianView3DAnimationContinueRotation extends EuclidianView3DAnimation {

	private static final double MAX_ROT_SPEED = 0.1;

	private double animatedRotSpeed;
	private double animatedRotTimeStart;
	private double aOld;
	private double bOld;

	/**
	 * 
	 * @param view3D 3D view
	 * @param animator animator
	 * @param delay delay occurring between user interaction and animation start
	 * @param rotSpeed rotation speed
	 */
	EuclidianView3DAnimationContinueRotation(EuclidianView3D view3D,
			EuclidianView3DAnimator animator, double delay, double rotSpeed) {

		super(view3D, animator);
		double rotSpeed2 = rotSpeed;
		// if speed is too large, use max speed
		if (rotSpeed2 > MAX_ROT_SPEED) {
			rotSpeed2 = MAX_ROT_SPEED;
		} else if (rotSpeed2 < -MAX_ROT_SPEED) {
			rotSpeed2 = -MAX_ROT_SPEED;
		}
		view3D.getSettings().setRotSpeed(0);
		animatedRotSpeed = -rotSpeed2;
		animatedRotTimeStart = -delay;
	}

	@Override
	public void setupForStart() {
		animatedRotTimeStart += getMillisecondTime();
		aOld = view3D.getAngleA();
		bOld = view3D.getAngleB();
	}

	@Override
	public AnimationType getType() {
		return AnimationType.CONTINUE_ROTATION;
	}

	@Override
	public void animate() {
		double da = (getMillisecondTime() - animatedRotTimeStart)
				* animatedRotSpeed;
		view3D.setRotXYinDegrees(aOld + da, bOld);
		view3D.updateRotationAndScaleMatrices();
		view3D.setGlobalMatrices();
		view3D.setViewChangedByRotate();
	}

	@Override
	protected boolean animationAllowed() {
		return true;
	}
}
