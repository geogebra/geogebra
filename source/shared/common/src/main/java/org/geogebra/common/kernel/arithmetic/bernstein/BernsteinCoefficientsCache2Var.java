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

public final class BernsteinCoefficientsCache2Var {
	BernsteinPolynomial1D[][] current;
	BernsteinPolynomial1D[][] last;
	private final int size;

	/**
	 * Cache with 2 rows
	 * @param size number of cached polynomials per row
	 */
	public BernsteinCoefficientsCache2Var(int size) {
		current = new BernsteinPolynomial1D[size][size];
		last = new BernsteinPolynomial1D[size][size];
		this.size = size;
	}

	/**
	 * Move data from current to last.
	 */
	public void update() {
		System.arraycopy(current, 0, last, 0, last.length);
	}

	/**
	 * @param i index
	 * @param coeffs coefficients
	 */
	public void set(int i, BernsteinPolynomial1D[] coeffs) {
		current[i] = coeffs;
	}

	/**
	 * @param i index
	 * @param coeffs coefficients
	 */
	public void setLast(int i, BernsteinPolynomial1D[] coeffs) {
		last[i] = coeffs;
	}

	/**
	 * @return array size
	 */
	public int size() {
		return size;
	}
}
