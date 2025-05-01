package org.geogebra.common.kernel.arithmetic.bernstein;

public final class BernsteinPolynomialCache {
	BernsteinPolynomial1D[] current;
	BernsteinPolynomial1D[] last;

	/**
	 * Cache with 2 rows
	 * @param size number of cached polynomials per row
	 */
	public BernsteinPolynomialCache(int size) {
		current = new BernsteinPolynomial1D[size];
		last = new BernsteinPolynomial1D[size];
	}

	/**
	 * Move data from current to last.
	 */
	public void update() {
		System.arraycopy(current, 0, last, 0, last.length);
	}

	/**
	 * @param i index
	 * @param bernsteinPolynomial polynomial
	 */
	public void set(int i, BernsteinPolynomial1D bernsteinPolynomial) {
		current[i] = bernsteinPolynomial;
	}

	/**
	 * @param i index
	 * @param bernsteinPolynomial polynomial
	 */
	public void setLast(int i, BernsteinPolynomial1D bernsteinPolynomial) {
		last[i] = bernsteinPolynomial;
	}
}
