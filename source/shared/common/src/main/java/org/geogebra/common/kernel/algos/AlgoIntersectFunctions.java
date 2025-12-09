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
