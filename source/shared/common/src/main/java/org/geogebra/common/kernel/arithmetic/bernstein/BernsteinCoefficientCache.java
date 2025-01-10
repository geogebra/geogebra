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
		System.arraycopy(current, 0, last, 0, last.length);
	}

	/**
	 * Sets the ith current coefficient
	 * @param i index of the coefficient
	 * @param coeffs the value.
	 */
	public void set(int i, double[] coeffs) {
		current[i] = coeffs;
	}

	/**
	 * Sets the ith last coefficient
	 * @param i index of the coefficient
	 * @param coeffs the value.
	 */
	public void setLast(int i, double[] coeffs) {
		last[i] = coeffs;
	}
}