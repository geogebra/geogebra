/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 * Finds intersection points of two polynomials (using the roots of their
 * difference)
 * 
 * @author Markus Hohenwarter
 */
public class AlgoIntersectPolynomials extends AlgoRootsPolynomial {

	public AlgoIntersectPolynomials(Construction cons, GeoFunction f,
			GeoFunction g) {
		super(cons, f, g);
	}

	@Override
	public Commands getClassName() {
		return Commands.Intersect;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_INTERSECT;
	}

	@Override
	public GeoPoint[] getIntersectionPoints() {
		return super.getRootPoints();
	}

	@Override
	public final String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-31
		// simplified to allow better translation
		return getLoc().getPlain("IntersectionPointOfAB",
				input[0].getLabel(tpl), input[1].getLabel(tpl));

	}

}
