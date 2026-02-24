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

package org.geogebra.common.kernel.geos;

/**
 * Interface for sliders usable for e.g. animated gif export
 *
 */
public interface AnimationExportSlider {

	/**
	 * 
	 * @return string displayed in slider combo box
	 */
	@Override
	String toString();

	/**
	 * will be called before each frame rendering
	 */
	void updateRepaint();

	/**
	 * @return animation type (ANIMATION_*)
	 */
	int getAnimationType();

	/**
	 * 
	 * @return slider min value
	 */
	double getIntervalMin();

	/**
	 * 
	 * @return slider max value
	 */
	double getIntervalMax();

	/**
	 * 
	 * @return slider step value
	 */
	double getAnimationStep();

	/**
	 * 
	 * @param x
	 *            slider value
	 */
	void setValue(double x);

}
