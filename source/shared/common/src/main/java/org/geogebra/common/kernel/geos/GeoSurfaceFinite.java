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

package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.kernelND.Region3D;

/**
 * This interface is to be used by 2D and 3D elements, such as GeoPolygon,
 * GeoConicND (esp. circle and ellipse), GeoQuadric3DPart (not
 * GeoQuadric3DLimited)
 * 
 */
public interface GeoSurfaceFinite extends Region3D {
	/**
	 * 
	 * @return area of this surface
	 */
	double getArea();
}
