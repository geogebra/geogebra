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
 * animation for pinch zoom + translation
 *
 */
public class EuclidianView3DAnimationScreenScale extends EuclidianView3DAnimation {

	private static final double Z_MIN_FOR_TRANSLATE_Z = 0.85;
	private static final double Z_MAX_FOR_TRANSLATE_XY = 0.45;

	private double xZeroOld;
	private double yZeroOld;
	private double zZeroOld;

	private double xScaleStart;
	private double yScaleStart;
	private double zScaleStart;
	private double xScaleEnd;
	private double yScaleEnd;
	private double zScaleEnd;

	private double screenTranslateAndScaleDX;
	private double screenTranslateAndScaleDY;
	private double screenTranslateAndScaleDZ;
	private Coords tmpCoords1 = new Coords(4);

	/**
	 * 
	 * @param view3D 3D view
	 * @param animator animator
	 */
	EuclidianView3DAnimationScreenScale(EuclidianView3D view3D, EuclidianView3DAnimator animator) {
		super(view3D, animator);
	}

	/**
	 * 
	 * @param dx
	 *            translation x in screen coordinates
	 * @param dy
	 *            translation y in screen coordinates
	 * @param scaleFactor
	 *            scale factor
	 */
	public void set(double dx, double dy, double scaleFactor) {

		// dx and dy are translation in screen coords
		// dx moves along "visible left-right" axis on xOy plane
		// dy moves along "visible front-back" axis on xOy plane
		// or z-axis if this one "visibly" more than sqrt(2)*front-back axis
		tmpCoords1.set(Coords.VX);
		view3D.toSceneCoords3D(tmpCoords1);
		screenTranslateAndScaleDX = tmpCoords1.getX() * dx;
		screenTranslateAndScaleDY = tmpCoords1.getY() * dx;

		tmpCoords1.set(Coords.VY);
		view3D.toSceneCoords3D(tmpCoords1);
		double z = tmpCoords1.getZ() * view3D.getScale();
		if (z > Z_MIN_FOR_TRANSLATE_Z) {
			screenTranslateAndScaleDZ = tmpCoords1.getZ() * (-dy);
		} else if (z < Z_MAX_FOR_TRANSLATE_XY) {
			screenTranslateAndScaleDX += tmpCoords1.getX() * (-dy);
			screenTranslateAndScaleDY += tmpCoords1.getY() * (-dy);
			screenTranslateAndScaleDZ = 0;
		} else {
			screenTranslateAndScaleDZ = 0;
		}

		xScaleEnd = xScaleStart * scaleFactor;
		yScaleEnd = yScaleStart * scaleFactor;
		zScaleEnd = zScaleStart * scaleFactor;
	}

	/**
	 * store values at start
	 */
	public void rememberOrigins() {
		xZeroOld = view3D.getXZero();
		yZeroOld = view3D.getYZero();
		zZeroOld = view3D.getZZero();
		xScaleStart = view3D.getXscale();
		yScaleStart = view3D.getYscale();
		zScaleStart = view3D.getZscale();
	}

	@Override
	public void setupForStart() {
		// nothing to do
	}

	@Override
	public AnimationType getType() {
		return AnimationType.SCREEN_TRANSLATE_AND_SCALE;
	}

	@Override
	public void animate() {
		view3D.setXZero(xZeroOld + screenTranslateAndScaleDX);
		view3D.setYZero(yZeroOld + screenTranslateAndScaleDY);
		view3D.setZZero(zZeroOld + screenTranslateAndScaleDZ);
		view3D.getSettings().updateOriginFromView(view3D.getXZero(), view3D.getYZero(),
				view3D.getZZero());
		view3D.setScale(xScaleEnd, yScaleEnd, zScaleEnd);
		view3D.updateMatrix();
		view3D.setViewChangedByZoom();
		view3D.setViewChangedByTranslate();
		end();
	}

}
