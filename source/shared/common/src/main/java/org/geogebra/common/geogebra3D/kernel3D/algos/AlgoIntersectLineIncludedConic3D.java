/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoTangentLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Helper algo to compute intersect points of a line in the conic coord sys
 * 
 * @author mathieu
 */
public class AlgoIntersectLineIncludedConic3D extends AlgoIntersectConic3D {

	@Override
	public Commands getClassName() {
		return Commands.Intersect;
	}

	/**
	 * @param cons
	 *            construction
	 * @param g
	 *            line
	 * @param c
	 *            conic
	 */
	AlgoIntersectLineIncludedConic3D(Construction cons, GeoLine g,
			GeoConicND c) {
		super(cons, g, c);

	}

	@Override
	public void compute() {
		intersectLineIncluded(c, P, c.getCoordSys(), getLine());
	}

	/**
	 * 
	 * @return line input
	 */
	GeoLine getLine() {
		return (GeoLine) getFirstGeo();
	}

	@Override
	protected Coords getFirstGeoStartInhomCoords() {
		return getLine().getStartInhomCoords();
	}

	@Override
	protected Coords getFirstGeoDirectionInD3() {
		return getLine().getDirectionInD3();
	}

	@Override
	protected boolean getFirstGeoRespectLimitedPath(Coords p) {
		return true;
	}

	@Override
	protected void checkIsOnFirstGeo(GeoPoint3D p) {
		// nothing to do
	}
}
