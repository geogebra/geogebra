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
 * animation for zoom
 *
 */
public class EuclidianView3DAnimationZoom extends EuclidianView3DAnimationScaleAbstract {

	private double zoomFactor;
	
	/**
	 * 
	 * @param view3D 3D view
	 * @param animator animator
	 * @param zoomFactor zoom factor
	 */
	EuclidianView3DAnimationZoom(EuclidianView3D view3D, EuclidianView3DAnimator animator,
			double zoomFactor) {
		super(view3D, animator);
		this.zoomFactor = zoomFactor;
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
		xScaleEnd = xScaleStart * zoomFactor;
		yScaleEnd = yScaleStart * zoomFactor;
		zScaleEnd = zScaleStart * zoomFactor;

		animatedScaleTimeFactor = ANIMATION_DURATION;
	}

}
