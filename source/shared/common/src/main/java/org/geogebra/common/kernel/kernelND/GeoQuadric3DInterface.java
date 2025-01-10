package org.geogebra.common.kernel.kernelND;

/** interface for 3D quadrics */
public interface GeoQuadric3DInterface {

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
