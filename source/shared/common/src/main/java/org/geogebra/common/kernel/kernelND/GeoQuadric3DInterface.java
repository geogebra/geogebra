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

import org.geogebra.common.kernel.QuadraticEquationRepresentable;

/** interface for 3D quadrics */
public interface GeoQuadric3DInterface extends QuadraticEquationRepresentable {

	/**
	 * sets quadric's matrix from coefficients of equation from array
	 * 
	 * @param coeffs
	 *            Array of coefficients
	 */
	void setMatrixFromXML(double[] coeffs);

	/**
	 * @param x0
	 *            x(e0)
	 * @param y0
	 *            y(e0)
	 * @param z0
	 *            z(e0)
	 * @param x1
	 *            x(e1)
	 * @param y1
	 *            y(e1)
	 * @param z1
	 *            z(e1)
	 * @param x2
	 *            x(e2)
	 * @param y2
	 *            y(e2)
	 * @param z2
	 *            z(e2)
	 */
	void setEigenvectors(double x0, double y0, double z0, double x1, double y1,
			double z1, double x2, double y2, double z2);

	/**
	 * Classify quadric if it wasn't done previously
	 */
	void ensureClassified();

	/**
	 * Hide quadrics that were not showable in old versions of 3D
	 */
	void hideIfNotSphere();
}
