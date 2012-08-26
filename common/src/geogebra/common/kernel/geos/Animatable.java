/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.common.kernel.geos;

import geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Geos that can be animated
 * @author Markus
 *
 */
public interface Animatable extends GeoElementND {
	
	/**
	 * Performs the next animation step for this GeoElement. This may
	 * change the value of this GeoElement but will NOT call update() or updateCascade().
	 * 
	 * @param frameRate current frames/second used in animation
	 * @return whether the value of this GeoElement was changed
	 */
	public boolean doAnimationStep(double frameRate);
	/**
	 * @return true when animation is on
	 */
	public boolean isAnimating();

}
