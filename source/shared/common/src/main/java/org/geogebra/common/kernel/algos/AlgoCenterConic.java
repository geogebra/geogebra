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
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Center of a conic section.
 */
public class AlgoCenterConic extends AlgoCenterQuadricND {

	/**
	 * Constructor
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param c
	 *            conic
	 */
	public AlgoCenterConic(Construction cons, String label, GeoConicND c) {
		super(cons, c);
		midpoint.setLabel(label);
	}

	@Override
	public GeoPointND newGeoPoint(Construction cons1) {
		return new GeoPoint(cons1);
	}

	@Override
	public GeoPointND getPoint() {
		return midpoint;
	}

	@Override
	public void setCoords() {

		switch (c.type) {
		case GeoConicNDConstants.CONIC_CIRCLE:
		case GeoConicNDConstants.CONIC_ELLIPSE:
		case GeoConicNDConstants.CONIC_HYPERBOLA:
		case GeoConicNDConstants.CONIC_SINGLE_POINT:
		case GeoConicNDConstants.CONIC_INTERSECTING_LINES:
			GeoVec2D b = ((GeoConicND) c).getB();
			setCoords(b.getX(), b.getY());
			break;

		default:
			// midpoint undefined
			midpoint.setUndefined();
		}
	}

	/**
	 * set the coords of the midpoint
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 */
	protected void setCoords(double x, double y) {
		midpoint.setCoords(x, y, 1.0d);
	}

}
