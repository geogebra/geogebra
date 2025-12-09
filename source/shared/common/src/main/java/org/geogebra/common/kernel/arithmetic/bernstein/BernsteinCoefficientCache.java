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

package org.geogebra.common.kernel.arithmetic.bernstein;

/**
 * Cache coefficients of the one variable Bernstein polynomial
 * current and last values.
 * It is used for computations in split()
 */
public class BernsteinCoefficientCache {
	double[][] current;
	double[][] last;

	/**
	 *
	 * @param size of the coefficients
	 */
	public BernsteinCoefficientCache(int size) {
		current = new double[size + 1][size + 1];
		last = new double[size + 1][size + 1];
	}

	/**
	 * Make current coefficients as last.
	 */
	public void update() {
		last = current;
	}

	/**
	 * Sets the ith current coefficient
	 *
	 * @param i index of the coefficient
	 * @param coeffs the value
	 * @param newLength the new length of the ith current coefficient
	 */
	public void set(int i, double[] coeffs, int size, int newLength) {
		current[i] = new double[size];
		for (int j = 0; j < newLength; j++) {
			current[i][j] = coeffs[j];
		}
	}

	/**
	 * Sets the ith last coefficient
	 * @param i index of the coefficient
	 * @param coeffs the value.
	 */
	public void setLastNoCopy(int i, double[] coeffs) {
		last[i] = coeffs;
	}

	/**
	 * @return size of the current part
	 */
	public int size() {
		return current.length;
	}

}