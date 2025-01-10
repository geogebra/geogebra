/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package org.geogebra.common.kernel.geos;

import java.util.ArrayList;

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Allow Freehand Functions to behave like GeoLocus for some things eg
 * Length[f], First[f, n]
 * 
 * @author Michael
 *
 */
public interface GeoLocusable extends GeoElementND {
	/**
	 * @return number of points
	 */
	public int getPointLength();

	/**
	 * @return list of points
	 */
	public ArrayList<? extends MyPoint> getPoints();
}
