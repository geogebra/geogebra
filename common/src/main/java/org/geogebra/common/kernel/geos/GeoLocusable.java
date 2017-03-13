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

/**
 * Allow Freehand Functions to behave like GeoLocus for some things eg
 * Length[f], First[f, n]
 * 
 * @author Michael
 *
 */
public interface GeoLocusable {
	/**
	 * @return this as GeoFunction
	 */
	// public GeoFunction getGeoFunction();

	/**
	 * @return this as GeoElement
	 */
	public GeoElement toGeoElement();

	public boolean isDefined();

	public int getPointLength();

	public ArrayList<MyPoint> getPoints();
}
