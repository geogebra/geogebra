package org.geogebra.common.kernel.polynomial;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;

import org.geogebra.common.util.debug.Log;

public class BigComplex {
	BigDecimal real;
	BigDecimal imag;

	public static final BigComplex ZERO = new BigComplex(BigDecimal.ZERO);
	public static final BigComplex ONE = new BigComplex(BigDecimal.ONE);

	public BigComplex(BigDecimal real) {
		this(real, BigDecimal.ZERO);
	}

	public BigComplex(BigDecimal real, BigDecimal imag) {
		this.real = real;
		this.imag = imag;
	}

	public BigComplex add(BigDecimal d, MathContext mc) {
		return new BigComplex(real.add(d, mc), imag);
	}

	public BigComplex add(BigComplex b, MathContext mc) {
		return new BigComplex(real.add(b.real, mc), imag.add(b.imag, mc));
	}

	public BigComplex subtract(BigComplex b, MathContext mc) {
		return new BigComplex(real.subtract(b.real, mc), imag.subtract(b.imag,
				mc));
	}

	public BigComplex multiply(BigComplex b, MathContext mc) {
		BigDecimal r1;
		try {
			r1 = real.multiply(b.real, mc);
		} catch (ArithmeticException e) {
			r1 = BigDecimal.ZERO;
		}
		BigDecimal r2;
		try {
			r2 = imag.multiply(b.imag, mc);
		} catch (ArithmeticException e) {
			r2 = BigDecimal.ZERO;
		}
		BigDecimal r;
		try {
			r = r1.subtract(r2, mc);
		} catch (ArithmeticException e1) {
			r = BigDecimal.ZERO;
		}
		BigDecimal i1;
		try {
			i1 = real.multiply(b.imag, mc);
		} catch (ArithmeticException e) {
			i1 = BigDecimal.ZERO;
		}
		BigDecimal i2;
		try {
			i2 = imag.multiply(b.real, mc);
		} catch (ArithmeticException e) {
			i2 = BigDecimal.ZERO;
		}
		BigDecimal i;
		try {
			i = i1.add(i2, mc);
		} catch (ArithmeticException e) {
			i = BigDecimal.ZERO;
		}
		return new BigComplex(r, i);
		// return new
		// BigComplex(real.multiply(b.real,mc).subtract(imag.multiply(b.imag,mc),mc),
		// real.multiply(b.imag,mc).add(imag.multiply(b.real,mc),mc));
	}

	public BigComplex divide(BigComplex b, MathContext mc) {
		BigDecimal absReal = b.real.abs(mc);
		BigDecimal absImag = b.imag.abs(mc);
		if (absReal.compareTo(absImag) >= 0) {
			BigDecimal dOverC = b.imag.divide(b.real, mc);
			BigDecimal div = b.real.add(b.imag.multiply(dOverC, mc), mc);
			return new BigComplex(real.add(imag.multiply(dOverC, mc), mc)
					.divide(div, mc), imag.subtract(real.multiply(dOverC, mc),
					mc).divide(div, mc));
		}
		BigDecimal cOverD = b.real.divide(b.imag, mc);
		BigDecimal div = b.real.multiply(cOverD, mc).add(b.imag, mc);
		return new BigComplex(real.multiply(cOverD, mc).add(imag, mc)
				.divide(div, mc), imag.multiply(cOverD, mc).subtract(real, mc)
				.divide(div, mc));

		// BigDecimal div=b.real.multiply(b.real).add(b.imag.multiply(b.imag));
		// return new
		// BigComplex(real.multiply(b.real).add(imag.multiply(b.imag).divide(div)),
		// real.multiply(b.imag).subtract(imag.multiply(b.real)).divide(div));
	}

	public BigComplex multiply(BigDecimal d, MathContext mc) {
		return new BigComplex(real.multiply(d, mc), imag.multiply(d, mc));
	}

	public BigComplex negate() {
		return new BigComplex(real.negate(), imag.negate());
	}

	public BigDecimal abs(MathContext mc) {
		BigDecimal absReal = real.abs(mc);
		BigDecimal absImag = imag.abs(mc);
		if (absReal.compareTo(BigDecimal.ONE) < 0
				&& absImag.compareTo(BigDecimal.ONE) < 0) {
			return sqrt(
					absReal.multiply(absReal, mc).add(
							absImag.multiply(absImag, mc), mc), mc);
		}
		if (absReal.compareTo(absImag) >= 0) {
			BigDecimal q = imag.divide(real, mc);
			return absReal.multiply(
					sqrt(BigDecimal.ONE.add(q.multiply(q, mc), mc), mc), mc);
		}
		BigDecimal q = real.divide(imag, mc);
		return absImag.multiply(
				sqrt(BigDecimal.ONE.add(q.multiply(q, mc), mc), mc), mc);
	}

	public BigComplex sqrt(MathContext mc) {
		if (real.compareTo(BigDecimal.ZERO) == 0) {
			if (imag.compareTo(BigDecimal.ZERO) == 0) {
				return BigComplex.ZERO;
			}
		}
		BigDecimal absReal = real.abs(mc);
		BigDecimal absImag = imag.abs(mc);
		BigDecimal w;
		if (absReal.compareTo(absImag) >= 0) {
			BigDecimal q = imag.divide(real, mc);
			BigDecimal p = sqrt(BigDecimal.ONE.add(q.multiply(q, mc), mc), mc);
			w = sqrt(absReal, mc).multiply(
					sqrt(BigDecimal.ONE.add(p, mc).divide(
							BigDecimal.valueOf(2), mc), mc), mc);
		} else {
			BigDecimal q = real.divide(imag, mc);
			BigDecimal p = sqrt(BigDecimal.ONE.add(q.multiply(q, mc), mc), mc);
			w = sqrt(absImag, mc)
					.multiply(
							sqrt(q.abs(mc).add(p, mc)
									.divide(BigDecimal.valueOf(2), mc), mc), mc);
		}
		if (w.compareTo(BigDecimal.ZERO) == 0) {
			return BigComplex.ZERO;
		}
		if (real.compareTo(BigDecimal.ZERO) >= 0) {
			return new BigComplex(w, imag.divide(BigDecimal.valueOf(2)
					.multiply(w, mc), mc));
		} else if (imag.compareTo(BigDecimal.ZERO) >= 0) {
			return new BigComplex(absImag.divide(BigDecimal.valueOf(2)
					.multiply(w, mc), mc), w);
		} else {
			return new BigComplex(absImag.divide(BigDecimal.valueOf(2)
					.multiply(w, mc), mc), w.negate());
		}
	}

	private static final double LOG10OF2 = Math.log10(2.);

	public static BigDecimal sqrt(BigDecimal d, MathContext mc) {
		if (d.signum() == -1) {
			throw new ArithmeticException("Squareroot of negative number.");
		}
		int scale = d.scale();
		BigInteger bi = d.unscaledValue();
		int bitLength = bi.bitLength();
		if (bitLength == 0) {
			return BigDecimal.ZERO;
		}
		int k = (int) Math.floor((LOG10OF2 * (bitLength - 1) - scale) / 2);
		BigDecimal n = d.scaleByPowerOfTen(-2 * k);
		double nDouble = 1 / n.doubleValue();

		BigDecimal x = new BigDecimal(Math.sqrt(nDouble), mc); // initial
																// approximation
		x = x.scaleByPowerOfTen(-k);
		if (x.compareTo(BigDecimal.ZERO) == 0) {
			x = BigDecimal.ONE;
		}
		int i = 0;
		BigDecimal distOne = null;
		while (i < 10) {
			BigDecimal s = BigDecimal.valueOf(3)
					.subtract(x.multiply(x, mc).multiply(d, mc), mc)
					.divide(BigDecimal.valueOf(2), mc);
			BigDecimal newDistOne = s.subtract(BigDecimal.ONE, mc).abs(mc);
			if (s.compareTo(BigDecimal.ONE) == 0
					|| (distOne != null && distOne.compareTo(newDistOne) <= 0)) {
				break;
			}

			x = x.multiply(s, mc);

			distOne = newDistOne;
			i++;
		}
		if (i == 10) {
			// Application.debug()
			// AbstractApplication.debug(String.format("s^2=%s,1/s=%s,s=%s",d,x,d.multiply(x,mc)));//GWT
			// limitation
			Log.debug("s^2=" + d + ",1/s=" + x + ",s="
					+ d.multiply(x, mc));
		}
		return d.multiply(x, mc);
	}

	@Override
	public String toString() {
		return real.toPlainString() + " + i " + imag.toPlainString();
		// return String.format("%s + i %s", real.toPlainString(),
		// imag.toPlainString());//GWT limitation
	}

	/* Testing */
	public static void main(String[] args) {
		MathContext mc = new MathContext(30, RoundingMode.HALF_EVEN);
		BigDecimal a = new BigDecimal(4);
		Log.debug(sqrt(a, mc));
		a = new BigDecimal(2);
		Log.debug(sqrt(a, mc));
		a = new BigDecimal(Double.MAX_VALUE);
		a = a.multiply(a);
		Log.debug(sqrt(a, mc));
		BigComplex c = new BigComplex(BigDecimal.valueOf(3),
				BigDecimal.valueOf(4));
		Log.debug(c.abs(mc));
		Log.debug(c.sqrt(mc));
		c = new BigComplex(BigDecimal.valueOf(4));
		Log.debug(c.sqrt(mc));
		c = new BigComplex(BigDecimal.ONE);
		Log.debug(c.sqrt(mc));
		c = new BigComplex(BigDecimal.ZERO, BigDecimal.valueOf(4));
		Log.debug(c.sqrt(mc));

		BigPolynomial b = new BigPolynomial(new double[] { 1, 0, 1 }, mc);
		Log.debug(Arrays.deepToString(b.getRootsLaguerre(mc)));

		b = new BigPolynomial(new double[] { 1, 1, 0, -1, -1 }, mc);
		Log.debug(Arrays.deepToString(b.getRootsLaguerre(mc)));

		b = new BigPolynomial(new double[] { 1, 1, 0, -1, -1 }, mc);
		double[] dr = b.getRealRootsDouble(20);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < dr.length; i++) {
			sb.append(",");
			sb.append(dr[i]);
		}
		Log.debug(sb.toString());

		b = new BigPolynomial(new double[] { -148.413, -469.075, -1062.1,
				-1287.92, -1145.84, -268.747, 223.29, 520.898, 111.839,
				4.04776, -140.187, 14.33, -5.2737, 20.9335, -8.59141, 1. }, mc);
		dr = b.getRealRootsDouble(20);
		sb = new StringBuilder();
		for (int i = 0; i < dr.length; i++) {
			sb.append(",");
			sb.append(dr[i]);
		}
		Log.debug(sb.toString());
	}

}
