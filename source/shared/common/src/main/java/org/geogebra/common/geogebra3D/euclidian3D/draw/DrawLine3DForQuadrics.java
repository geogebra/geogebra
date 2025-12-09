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
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Draw line for quadrics
 * 
 * @author mathieu
 *
 */
public class DrawLine3DForQuadrics extends DrawLine3D {

	private GeoQuadric3D quadric;

	/**
	 * @param a_view3D
	 *            view
	 * @param line
	 *            line
	 * @param quadric
	 *            quadric
	 */
	public DrawLine3DForQuadrics(EuclidianView3D a_view3D, GeoLine3D line,
			GeoQuadric3D quadric) {
		super(a_view3D, line, quadric);
	}

	@Override
	protected void init(GeoElement line, GeoElement geo2) {

		super.init(line);
		this.quadric = (GeoQuadric3D) geo2;

	}

	@Override
	public GeoElement getGeoElement() {
		return quadric;
	}

	@Override
	protected GeoLine3D getLine() {
		return (GeoLine3D) super.getGeoElement();
	}

}
