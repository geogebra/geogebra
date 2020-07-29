/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 * Finds intersection points of two functions numerically (using the roots of
 * their difference)
 * 
 * @author Hans-Petter Ulven
 * @version 10.03.2011
 */
public class AlgoIntersectFunctions extends AlgoRoots {

	public AlgoIntersectFunctions(Construction cons, String[] labels,
			GeoFunctionable f, GeoFunctionable g, GeoNumberValue left,
			GeoNumberValue right) {
		super(cons, labels, f, g, left, right);
	}

	@Override
	public Commands getClassName() {
		return Commands.Intersect;
	}

	public GeoPoint[] getIntersectionPoints() {
		return super.getRootPoints();
	}
}
