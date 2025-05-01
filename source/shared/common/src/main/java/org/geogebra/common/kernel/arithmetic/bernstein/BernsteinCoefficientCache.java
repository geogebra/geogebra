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