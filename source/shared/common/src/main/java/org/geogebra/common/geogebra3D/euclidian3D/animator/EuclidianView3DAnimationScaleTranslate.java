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

/**
 * animation for scale and translate
 *
 */
public class EuclidianView3DAnimationScaleTranslate extends EuclidianView3DAnimationScaleAbstract {

	private static final double STEPS_TO_TIME_FACTOR = 0.0003;

	/**
	 * 
	 * @param view3D 3D view
	 * @param animator animator
	 * @param x translation in x
	 * @param y translation in y
	 * @param z translation in z
	 * @param newScale new scale
	 * @param steps steps for animation
	 */
	EuclidianView3DAnimationScaleTranslate(EuclidianView3D view3D, EuclidianView3DAnimator animator,
			double x, double y, double z, double newScale, int steps) {
		super(view3D, animator);
		animatedScaleEndX = x;
		animatedScaleEndY = y;
		animatedScaleEndZ = z;
		xScaleEnd = newScale;
		yScaleEnd = newScale;
		zScaleEnd = newScale;
		animatedScaleTimeFactor = STEPS_TO_TIME_FACTOR * steps;
	}

	@Override
	public void setupForStart() {
		xScaleStart = view3D.getXscale();
		yScaleStart = view3D.getYscale();
		zScaleStart = view3D.getZscale();
		animatedScaleStartX = view3D.getXZero();
		animatedScaleStartY = view3D.getYZero();
		animatedScaleStartZ = view3D.getZZero();

		animatedScaleTimeStart = getMillisecondTime();
	}

	@Override
	protected boolean animationAllowed() {
		return true;
	}
}
