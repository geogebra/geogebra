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
import org.geogebra.common.kernel.algos.AlgoCenterConic;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Center of 3D conic
 * 
 * @author mathieu
 *
 */
public class AlgoCenterConic3D extends AlgoCenterConic {

	/**
	 * constructor
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param c
	 *            conic
	 */
	public AlgoCenterConic3D(Construction cons, String label, GeoConicND c) {
		super(cons, label, c);
	}

	@Override
	public GeoPointND newGeoPoint(Construction cons1) {
		return new GeoPoint3D(cons1);
	}

	@Override
	protected void setCoords(double x, double y) {
		((GeoPoint3D) midpoint)
				.setCoords(((GeoConicND) c).getCoordSys().getPoint(x, y));
	}
}
