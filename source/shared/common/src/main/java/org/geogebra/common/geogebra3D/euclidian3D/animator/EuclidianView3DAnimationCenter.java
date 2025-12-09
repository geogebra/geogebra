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
import org.geogebra.common.kernel.matrix.Coords;

/**
 * animation for centering view
 *
 */
public class EuclidianView3DAnimationCenter extends EuclidianView3DAnimation {

	private double xEnd;
	private double yEnd;
	private double zEnd;

	/**
	 * 
	 * @param view3D 3D view
	 * @param animator animator
	 * @param p
	 *            point to center about
	 */
	EuclidianView3DAnimationCenter(EuclidianView3D view3D, EuclidianView3DAnimator animator,
			Coords p) {

		super(view3D, animator);
		xEnd = -p.getX();
		yEnd = -p.getY();
		zEnd = -p.getZ();
	}

	@Override
	public void setupForStart() {
		// nothing to do
	}

	@Override
	public AnimationType getType() {
		return AnimationType.TRANSLATION;
	}

	@Override
	public void animate() {
		view3D.setXZero(xEnd);
		view3D.setYZero(yEnd);
		view3D.setZZero(zEnd);
		view3D.getSettings().updateOriginFromView(xEnd, yEnd, zEnd);

		// update the view
		view3D.updateTranslationMatrices();
		view3D.setGlobalMatrices();

		view3D.setViewChangedByTranslate();
		end();
	}

}
