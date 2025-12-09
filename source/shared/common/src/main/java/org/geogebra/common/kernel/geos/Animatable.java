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

import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Geos that can be animated
 * 
 * @author Markus
 *
 */
public interface Animatable extends GeoElementND {

	/**
	 * Performs the next animation step for this GeoElement. This may change the
	 * value of this GeoElement but will NOT call update() or updateCascade().
	 * 
	 * @param frameRate
	 *            current frames/second used in animation
	 * @param parent
	 *            parent list
	 * @return null if nothing changed or changed element otherwise
	 */
	public GeoElementND doAnimationStep(double frameRate, GeoList parent);

	/**
	 * @return true when animation is on
	 */
	public boolean isAnimating();

}
