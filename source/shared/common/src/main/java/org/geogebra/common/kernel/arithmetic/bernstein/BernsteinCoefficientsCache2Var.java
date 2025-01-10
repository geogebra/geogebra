package org.geogebra.common.kernel.arithmetic.bernstein;

public final class BernsteinCoefficientsCache2Var {
	BernsteinPolynomial[][] current;
	BernsteinPolynomial[][] last;

	/**
	 * Cache with 2 rows
	 * @param size number of cached polynomials per row
	 */
	public BernsteinCoefficientsCache2Var(int size) {
		current = new BernsteinPolynomial[size][size];
		last = new BernsteinPolynomial[size][size];
	}

	public void update() {
		System.arraycopy(current, 0, last, 0, last.length);
	}

	public void set(int i, BernsteinPolynomial[] coeffs) {
		current[i] = coeffs;
	}

	public void setLast(int i, BernsteinPolynomial[] coeffs) {
		last[i] = coeffs;
	}
}
