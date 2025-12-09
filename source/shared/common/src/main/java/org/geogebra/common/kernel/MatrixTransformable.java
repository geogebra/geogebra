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

package org.geogebra.common.kernel;

import org.geogebra.common.kernel.geos.Rotatable;

/**
 * GeoElement which supports matrix transformations
 */
public interface MatrixTransformable extends Rotatable {
	/**
	 * Transforms the object using the matrix a00 a01 a10 a11
	 *
	 * @param a00
	 *            a00
	 * @param a01
	 *            a01
	 * @param a10
	 *            a10
	 * @param a11
	 *            a11
	 */
	void matrixTransform(double a00, double a01, double a10, double a11);

	/**
	 * Transforms the object using the matrix a00 a01 a02 a10 a11 a12 a20 a21
	 * a22
	 *
	 * @param a00
	 *            a00
	 * @param a01
	 *            a01
	 * @param a02
	 *            a02
	 * @param a10
	 *            a10
	 * @param a11
	 *            a11
	 * @param a12
	 *            a12
	 * @param a20
	 *            a20
	 * @param a21
	 *            a21
	 * @param a22
	 *            a22
	 */
	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22);

}
