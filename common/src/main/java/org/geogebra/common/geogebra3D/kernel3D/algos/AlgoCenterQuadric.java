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
	 * @param label
	 * @param c
	 */
	public AlgoCenterQuadric(Construction cons, String label, GeoQuadric3D c) {
		super(cons, label, c);
	}

	@Override
	public GeoPointND newGeoPoint(Construction cons) {
		return new GeoPoint3D(cons);
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
			((GeoPoint3D) midpoint).setCoords(c.getMidpoint3D());
			break;

		default:
			// midpoint undefined
			midpoint.setUndefined();

		}
	}

}
