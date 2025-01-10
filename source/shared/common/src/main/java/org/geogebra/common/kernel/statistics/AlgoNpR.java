package org.geogebra.common.kernel.statistics;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.geogebra.common.util.MyMath2;

/**
 * Helper for nPr operation
 *
 */
public class AlgoNpR {

	/**
	 * @param n
	 *            size of the set
	 * @param r
	 *            size of the permutation
	 * @return number of r-element permutations in a n-element set
	 */
	public static double nPr(double n, double r) {
		double INFINITY = Double.POSITIVE_INFINITY;
		try {
			if (n == 0d && r == 0d) {
				return 1d;
			}
			if (n < 1d || r < 0d || n < r) {
				return 0d;
			}
			if (Math.floor(n) != n || Math.floor(r) != r) {
				return 0d;
			}

			double ncr = nPrLog(n, r);
			if (ncr == INFINITY) {
				return INFINITY; // check to stop needless slow calculations
			}

			// NpRLog is not exact for some values
			// (determined by trial and error) eg 17P16
			if (n <= 16) {
				return ncr;
			// if (r<2.8+Math.exp((250-n)/100) && n<59000) return ncr;
			}

			// NpRBig is more accurate but slower
			// (but cannot be exact if the answer has more than about 16
			// significant digits)
			return nPrBig(n, r);
		} catch (Exception e) {
			return INFINITY;
		}
	}

	private static double nPrBig(double n, double r) {
		BigInteger ncr = BigInteger.ONE, nn, nr;
		// nn=BigInteger.valueOf((long)n);
		// rr=BigInteger.valueOf((long)r);

		// need a long-winded conversion in case n>10^18
		String nnn = Double.toString(n);
		String rrr = Double.toString(n - r);
		nn = (new BigDecimal(nnn)).toBigInteger();
		nr = (new BigDecimal(rrr)).toBigInteger();
		nr = nr.add(BigInteger.ONE);

		while (nr.compareTo(nn) <= 0) {
			ncr = ncr.multiply(nr);

			nr = nr.add(BigInteger.ONE);

		}
		return ncr.doubleValue();
	}

	private static double nPrLog(double n, double r) {
		// exact for n<=37
		// also if r<2.8+Math.exp((250-n)/100) && n<59000
		// eg NpR2(38,19) is wrong

		return Math.floor(0.5 + Math
				.exp(MyMath2.logGamma(n + 1d) - MyMath2.logGamma(n - r + 1)));

	}

}
