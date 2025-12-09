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
