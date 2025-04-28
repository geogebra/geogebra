package org.geogebra.common.kernel.arithmetic.bernstein;

public abstract class BernsteinPolynomial<T> {

	private BinomialCoefficientsSign sign;

	/**
	 *
	 * @return if the polynomial is a simple constant number.
	 */
	public abstract boolean isConstant();

	/**
	 *
	 * @return true if the polynomial has no solution.
	 */
	public boolean hasNoSolution() {
		return sign.monotonic();
	}

	/**
	 *
	 * @return sign of the coefficients {+, - or mixed}.
	 */
	BinomialCoefficientsSign getSign() {
		return sign;
	}

	protected void setSign(BinomialCoefficientsSign sign) {
		this.sign = sign;
	}
}
