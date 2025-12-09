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
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.geos.GeoLocusND;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;

/**
 * @author mathieu
 * 
 *         Drawable for locus
 * 
 */
public class DrawImplicitCurve3D extends DrawLocus3D {

	/**
	 * @param a_view3d
	 *            the 3D view where the curve is drawn
	 * @param curve
	 *            the curve to draw
	 */
	public DrawImplicitCurve3D(EuclidianView3D a_view3d, GeoImplicit curve) {
		super(a_view3d, curve.getLocus(), curve.toGeoElement(),
				curve.getTransformedCoordSys());

	}

	@Override
	protected GeoLocusND<? extends MyPoint> getLocus() {
		return ((GeoImplicitCurve) getGeoElement()).getLocus();
	}

}
