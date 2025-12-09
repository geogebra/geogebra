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

package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Helper for drawing planes, for quadrics
 * 
 * @author Proprietaire
 *
 */
public class DrawPlane3DForQuadrics extends DrawPlane3D {

	private GeoQuadric3D quadric;

	/**
	 * @param a_view3D
	 *            view
	 * @param a_plane3D
	 *            plane
	 * @param quadric
	 *            quadric
	 */
	public DrawPlane3DForQuadrics(EuclidianView3D a_view3D,
			GeoPlane3D a_plane3D, GeoQuadric3D quadric) {
		super(a_view3D, a_plane3D, quadric);
	}

	@Override
	protected void init(GeoElement a_plane3D, GeoElement geo2) {
		super.init(a_plane3D);
		this.quadric = (GeoQuadric3D) geo2;
	}

	@Override
	public GeoElement getGeoElement() {
		return quadric;
	}

	@Override
	protected GeoPlane3D getPlane() {
		return (GeoPlane3D) super.getGeoElement();
	}

	@Override
	protected int getGridThickness() {
		return 0;
	}

}
