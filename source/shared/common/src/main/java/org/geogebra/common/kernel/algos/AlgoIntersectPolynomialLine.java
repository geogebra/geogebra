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

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 * Finds intersection points of a polynomial and a line (using the roots of
 * their difference)
 * 
 * @author Markus Hohenwarter
 */
public class AlgoIntersectPolynomialLine extends AlgoRootsPolynomial {

	/**
	 * @param cons
	 *            construction
	 * @param f
	 *            function
	 * @param g
	 *            line
	 */
	public AlgoIntersectPolynomialLine(Construction cons, GeoFunctionable f,
			GeoLine g) {
		super(cons, f, g);
		addIncidence();
	}

	/**
	 * @author Tam
	 * 
	 *         for special cases of e.g. AlgoIntersectLineConic
	 */
	private void addIncidence() {
		// TODO
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
		return getLoc().getPlainDefault("IntersectionOfAandB",
				"Intersection of %0, %1",
				input[0].getLabel(tpl), input[1].getLabel(tpl));

	}

}
