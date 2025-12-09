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
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoCenterQuadricND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;

/**
 * center of quadric
 * 
 * @author mathieu
 *
 */
public class AlgoCenterQuadric extends AlgoCenterQuadricND {

	/**
	 * constructor
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param c
	 *            quadric
	 */
	public AlgoCenterQuadric(Construction cons, String label, GeoQuadric3D c) {
		super(cons, c);
		midpoint.setLabel(label);
	}

	@Override
	public GeoPointND newGeoPoint(Construction cons1) {
		return new GeoPoint3D(cons1);
	}

	@Override
	protected void setCoords() {
		switch (c.type) {

		case GeoQuadricNDConstants.QUADRIC_SINGLE_POINT:
		case GeoQuadricNDConstants.QUADRIC_INTERSECTING_LINES:
		case GeoQuadricNDConstants.QUADRIC_ELLIPSOID:
		case GeoQuadricNDConstants.QUADRIC_SPHERE:
		case GeoQuadricNDConstants.QUADRIC_HYPERBOLOID:
		case GeoQuadricNDConstants.QUADRIC_CONE:
		case GeoQuadricNDConstants.QUADRIC_HYPERBOLOID_ONE_SHEET:
		case GeoQuadricNDConstants.QUADRIC_HYPERBOLOID_TWO_SHEETS:
			((GeoPoint3D) midpoint).setCoords(c.getMidpoint3D());
			break;

		default:
			// midpoint undefined
			midpoint.setUndefined();

		}
	}

}
