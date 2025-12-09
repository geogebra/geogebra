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

package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.geogebra3D.kernel3D.transform.MirrorableAtPlane;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.geos.Dilateable;
import org.geogebra.common.kernel.geos.Rotatable;
import org.geogebra.common.kernel.geos.Transformable;
import org.geogebra.common.kernel.geos.Translateable;

/**
 * @author mathieu
 *
 *         Interface for surfaces in any dimension
 */
public interface GeoImplicitSurfaceND extends Translateable, Dilateable,
		Rotatable, MirrorableAtPlane, Transformable {

	/**
	 * @return surface defining function (LHS-RHS)
	 */
	FunctionNVar getExpression();

}
