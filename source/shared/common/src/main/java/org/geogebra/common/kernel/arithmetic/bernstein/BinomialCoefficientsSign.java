package org.geogebra.common.kernel.arithmetic.bernstein;

/**
 * Sign of the Bernstein coefficients. If all of them negative or positive, you can be sure, that
 * the polynomial has no solution. If the coefficients have both (mixed) signs, there might be a
 * solution, but more examination needed.
 */
public enum BinomialCoefficientsSign {
	None,
	AllPositive,
	AllNegative,
	Mixed;

	/**
	 * Creates the sign for a one variable Bernstein polynomial
	 *
	 * @param bernsteinCoeffs the coefficients to check.
	 * @param degree of the Bernstein polynomial.
	 * @return the sign.
	 */
	public static BinomialCoefficientsSign from1Var(double[] bernsteinCoeffs, int degree) {
		double count = 0;
		for (int i = 0; i < degree + 1; i++) {
			count += Math.signum(bernsteinCoeffs[i]);
		}

		if (Math.abs(count) == degree + 1) {
			return count < 0 ? AllNegative : AllPositive;
		}

		return Mixed;
	}

	/**
	 * Creates the sign for a two variable Bernstein polynomial
	 *
	 * @param bernsteinCoeffs the coefficients to check.
	 * @return the sign.
	 */
	public static BinomialCoefficientsSign from2Var(BernsteinPolynomial[] bernsteinCoeffs) {
		int positive = 0;
		int negative = 0;
		for (BernsteinPolynomial bcoeff : bernsteinCoeffs) {
			if (bcoeff != null) {
				switch (bcoeff.getSign()) {
				case AllPositive:
					positive++;
					break;
				case AllNegative:
					negative++;
					break;
				case None:
				case Mixed:
				}
			}
		}

		if (positive == bernsteinCoeffs.length) {
			return AllPositive;
		}

		if (negative == bernsteinCoeffs.length) {
			return AllNegative;
		}

		return Mixed;
	}

	/**
	 * Returns true if all coefficients are the positive or negative. In this case, the Bernstein
	 * polynomial has no solution. Otherwise, there might be a solution, but not guaranteed.
	 *
	 * @return if all coefficients are the same sign.
	 */
	public boolean monotonic() {
		return this == AllPositive || this == AllNegative;
	}
}