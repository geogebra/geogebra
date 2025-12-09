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

import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.Rotatable;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Elements rotatable around arbitrary 3D object (point or line)
 */
public interface RotatableND extends Rotatable {

	/**
	 * Rotates this element around
	 * 
	 * @param r
	 *            angle
	 * @param S
	 *            center
	 * @param orientation
	 *            orientation for the rotation
	 */
	public void rotate(NumberValue r, Coords S, GeoDirectionND orientation);

}
