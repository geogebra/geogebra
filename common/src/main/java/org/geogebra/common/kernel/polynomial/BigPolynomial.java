package org.geogebra.common.kernel.polynomial;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;

public class BigPolynomial implements Cloneable {

	protected BigDecimal[] coefficients;
	protected String representation;
	protected MathContext mc;
	protected BigDecimal plusEps;
	protected BigDecimal minusEps;

	private BigPolynomial() {
		representation = "";
		mc = MathContext.UNLIMITED;
	}

	private BigPolynomial(BigPolynomial p) {
		this(p, p.getCoefficients());
	}

	/**
	 * 
	 * @param p
	 *            BigPolynomial to copy precision etc. from
	 * @param coeff
	 *            used as the coefficients of this polynomial (directly, no
	 *            clone)
	 */
	protected BigPolynomial(BigPolynomial p, BigDecimal[] coeff) {
		this();
		mc = p.mc;
		plusEps = p.plusEps;
		minusEps = p.minusEps;
		coefficients = coeff;
	}

	private BigPolynomial(BigDecimal[] coeff) {
		this();
		BigDecimal[] coefficients_clone = new BigDecimal[coeff.length];
		for (int i = 0; i < coeff.length; i++)
			coefficients_clone[i] = new BigDecimal(coeff[i].unscaledValue(),
					coeff[i].scale(), new MathContext(coeff[i].precision()));
		coefficients = coefficients_clone;
	}

	public BigPolynomial(BigDecimal[] coeff, BigDecimal eps, MathContext mc) {
		this(coeff);
		plusEps = eps;
		minusEps = plusEps.negate();
		this.mc = mc;
	}

	public BigPolynomial(double[] coeff, MathContext mc) {
		this();
		coefficients = new BigDecimal[coeff.length];
		for (int i = 0; i < coeff.length; i++) {
			coefficients[i] = new BigDecimal(coeff[i], mc);
		}
		this.mc = mc;
		plusEps = new BigDecimal(BigInteger.ONE, mc.getPrecision() / 2);
		minusEps = plusEps.negate();
	}

	public BigPolynomial(double[] coeff, int precision) {
		this(coeff, new MathContext(precision));
	}

	public BigPolynomial(double constant, int precision) {
		this(new double[] { constant }, precision);
	}

	public BigPolynomial copy() {
		return new BigPolynomial(this);
	}

	public Object clone() {
		return copy();
	}

	public int degree() {
		return coefficients.length - 1;
	}

	public BigDecimal eval(BigDecimal val) {
		if (coefficients.length > 0) {
			BigDecimal sum = coefficients[coefficients.length - 1];
			for (int i = coefficients.length - 1; i >= 0; i--) {
				sum = sum.multiply(val, mc);
				sum = sum.add(coefficients[i], mc);
			}
			return sum;
		}
		return BigDecimal.ZERO;
	}

	public double eval(double d) {
		return eval(new BigDecimal(d)).doubleValue();
	}

	public BigPolynomial add(BigPolynomial p) {
		BigDecimal[] coeff = new BigDecimal[] { BigDecimal.ZERO };// =new
																	// BigDecimal[Math.max(coefficients.length,
																	// p.coefficients.length)];
		for (int i = Math.max(coefficients.length, p.coefficients.length) - 1; i >= 0; i--) {
			BigDecimal newCoeff = BigDecimal.ZERO;
			if (i < coefficients.length && i < p.coefficients.length) {
				newCoeff = coefficients[i].add(p.coefficients[i], mc);
			} else {
				if (i >= coefficients.length) {
					newCoeff = p.coefficients[i];
				} else {
					newCoeff = coefficients[i];
				}
			}
			if (!smallerEps(newCoeff)) {
				if (coeff.length == 1) {
					coeff = new BigDecimal[i + 1];
				}
				coeff[i] = newCoeff;
			} else {
				if (coeff.length > 1) {
					coeff[i] = BigDecimal.ZERO;
				}
			}
		}
		// BigPolynomial a=new BigPolynomial(this);
		// a.coefficients=coeff;
		return new BigPolynomial(this, coeff);
	}

	public BigPolynomial subtract(BigPolynomial p) {
		BigDecimal[] coeff = new BigDecimal[] { BigDecimal.ZERO };// new
																	// BigDecimal[Math.max(coefficients.length,
																	// p.coefficients.length)];
		for (int i = Math.max(coefficients.length, p.coefficients.length) - 1; i >= 0; i--) {
			BigDecimal newCoeff = BigDecimal.ZERO;
			if (i < coefficients.length && i < p.coefficients.length) {
				newCoeff = coefficients[i].subtract(p.coefficients[i], mc);
			} else {
				if (i >= coefficients.length) {
					newCoeff = p.coefficients[i].negate();
				} else {
					newCoeff = coefficients[i];
				}
			}
			if (!smallerEps(newCoeff)) {
				if (coeff.length == 1) {
					coeff = new BigDecimal[i + 1];
				}
				coeff[i] = newCoeff;
			} else {
				if (coeff.length > 1) {
					coeff[i] = BigDecimal.ZERO;
				}
			}
		}
		// BigPolynomial a=new BigPolynomial(this);
		// a.coefficients=coeff;
		return new BigPolynomial(this, coeff);
	}

	public BigPolynomial multiply(BigPolynomial p) {
		BigDecimal[] coeff = new BigDecimal[] { BigDecimal.ZERO };// new
																	// BigDecimal[(coefficients.length+p.coefficients.length)-1];
		for (int i = (coefficients.length + p.coefficients.length) - 2; i >= 0; i--) {
			BigDecimal sum = BigDecimal.ZERO;
			for (int j = 0; j <= i; j++) {
				if (j < coefficients.length && (i - j) < p.coefficients.length) {
					sum = sum
							.add(coefficients[j].multiply(
									p.coefficients[i - j], mc), mc);
				}
				// else if (j>=coefficients.length){
				// sum=sum.add(p.coefficients[i-j]);
				// }else{
				// sum=sum.add(coefficients[j]);
				// }
			}
			if (!smallerEps(sum)) {
				if (coeff.length == 1) {
					coeff = new BigDecimal[i + 1];
				}
				coeff[i] = sum;
			} else {
				if (coeff.length > 1) {
					coeff[i] = BigDecimal.ZERO;
				}
			}
		}
		// BigPolynomial a=new BigPolynomial(this);
		// a.coefficients=coeff;
		return new BigPolynomial(this, coeff);
	}

	public BigPolynomial divide(BigPolynomial divisor) {
		BigDecimal[] quotient;
		int degD = divisor.degree();
		// while(degD>=0&&Kernel.isZero(cd[degD])){
		// degD--;
		// }
		if (degD < 0) { // => division by zero
			throw new ArithmeticException("divide by zero polynomial");
		}
		BigDecimal[] remainder = getCoefficients();
		int k = remainder.length - 1;
		if (k < degD) {
			return new BigPolynomial(this, new BigDecimal[] { BigDecimal.ZERO });
		}
		quotient = new BigDecimal[] { BigDecimal.ZERO };// new
														// BigDecimal[k+1-degD];
		BigDecimal lcd = divisor.coefficients[degD];
		for (int i = k - degD; i >= 0; i--) {
			BigDecimal q = remainder[k].divide(lcd, mc);
			if (!smallerEps(q)) {
				if (quotient.length == 1) {
					quotient = new BigDecimal[i + 1];
				}
				quotient[i] = q;
				for (int j = 0; j <= degD - 1; j++) {
					remainder[j + i] = remainder[j + i].subtract(
							quotient[i].multiply(divisor.coefficients[j], mc),
							mc);
				}
			} else {
				if (quotient.length > 1) {
					quotient[i] = BigDecimal.ZERO;
				}
			}
			k--;
		}
		return new BigPolynomial(this, quotient);
	}

	public double[] getCoefficientsDouble() {
		double[] ret = new double[coefficients.length];
		for (int i = 0; i < coefficients.length; i++) {
			ret[i] = coefficients[i].doubleValue();
		}
		return ret;
	}

	public BigDecimal[] getCoefficients() {
		BigDecimal[] coefficients_clone = new BigDecimal[coefficients.length];
		for (int i = 0; i < coefficients.length; i++)
			coefficients_clone[i] = new BigDecimal(
					coefficients[i].unscaledValue(), coefficients[i].scale(),
					new MathContext(coefficients[i].precision()));
		return coefficients_clone;
	}

	public BigDecimal getCoeff(int index) {
		return coefficients[index];
	}

	public double getCoeffDouble(int index) {
		return getCoeff(index).doubleValue();
	}

	@Override
	public String toString() {
		if (representation.length() == 0) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < coefficients.length; i++) {
				String c = coefficients[i].toPlainString();
				if (c.charAt(0) != '-' && i != 0)
					sb.append('+');
				sb.append(c);
				sb.append(" x^");
				sb.append(i);
			}
			representation = sb.toString();
		}
		return representation;
	}

	public boolean isZeroPolynomial() {
		return smallerEps(coefficients[coefficients.length - 1]);
	}

	// public BigPolynomial cropLeadingZeros(BigDecimal epsilon){
	// for (int i=coefficients.length-1;i>=0;i--){
	// if (coefficients[i].abs().compareTo(epsilon)>0){
	// BigDecimal[] coeff=new BigDecimal[i+1];
	// System.arraycopy(coefficients, 0, coeff, 0, i+1);
	// return new BigPolynomial(coeff);
	// }
	// }
	// return new BigPolynomial(new BigDecimal[]{BigDecimal.ZERO});
	// }
	//
	// public BigPolynomial cropLeadingZeros(int precision){
	// return cropLeadingZeros(new BigDecimal(BigInteger.ONE,precision));
	// }

	protected boolean smallerEps(BigDecimal d) {
		// minusEps <= d <= plusEps
		return (minusEps.compareTo(d) <= 0) && (d.compareTo(plusEps) <= 0);
	}

	public BigComplex rootPolishing(BigComplex x) {
		// BigComplex x=c;
		int MAX_ITER = 10;
		BigComplex oldX = x;
		BigDecimal lastErr = null;
		for (int i = 0; i < MAX_ITER; i++) {
			int n = coefficients.length - 1;
			BigComplex px = new BigComplex(coefficients[n]);
			BigComplex dpx = BigComplex.ZERO;
			for (int j = n - 1; j >= 0; j--) {
				dpx = x.multiply(dpx, mc).add(px, mc);
				px = px.multiply(x, mc).add(coefficients[j], mc);
			}
			BigDecimal err = px.real.abs(mc).add(px.imag.abs(mc), mc);
			if (i > 0 && lastErr.compareTo(err) <= 0) {
				x = oldX;
				break;
			}
			// if (smallerEps()){
			// break;
			// }
			oldX = x;
			x = x.subtract(px.divide(dpx, mc), mc);
			lastErr = err;
		}
		return x;
	}

	public double[] getRealRootsDouble(int precision) {
		MathContext mc = new MathContext(precision);
		BigComplex[] roots = getRootsLaguerre(mc);
		double[] doubleRoots = new double[roots.length];
		int c = 0;
		for (int i = 0; i < roots.length; i++) {
			BigComplex root = rootPolishing(roots[i]);
			double imag = root.imag.doubleValue();
			if (Kernel.isEqual(imag, 0., 10E-5)) {
				doubleRoots[c++] = root.real.doubleValue();
			}
		}
		if (c < roots.length) {
			double[] doubleRootsFinal = new double[c];
			System.arraycopy(doubleRoots, 0, doubleRootsFinal, 0, c);
			return doubleRootsFinal;
		}
		return doubleRoots;

	}

	public BigComplex[] getRootsLaguerre(MathContext mc) {
		if (coefficients.length <= 1) {
			return new BigComplex[0];
		}
		BigComplex[] coeff = new BigComplex[coefficients.length];
		BigComplex[] roots = new BigComplex[coefficients.length - 1];
		for (int i = 0; i < coeff.length; i++) {
			coeff[i] = new BigComplex(coefficients[i]);
		}
		for (int i = 0; i < roots.length - 1; i++) {
			roots[i] = getRoot(coeff, BigComplex.ONE, mc);
			BigComplex[] newCoeff = new BigComplex[coeff.length - 1];
			int n = coeff.length - 1;
			BigComplex s = coeff[n];
			newCoeff[n - 1] = s;
			for (int j = n - 2; j >= 0; j--) {
				s = s.multiply(roots[i], mc);
				s = s.add(coeff[j + 1], mc);
				newCoeff[j] = s;
			}
			coeff = newCoeff;
		}
		roots[roots.length - 1] = coeff[0].negate().divide(coeff[1], mc);
		return roots;
	}

	private static int MAX_ITERATIONS = 20;

	public BigComplex getRoot(BigComplex[] coeff, BigComplex start,
			MathContext mc) {
		BigComplex x = start;
		for (int i = 0; i < MAX_ITERATIONS; i++) {

			int n = coeff.length - 1;
			BigComplex px = coeff[n];
			BigComplex dpx = BigComplex.ZERO;
			BigComplex ddpx = BigComplex.ZERO;

			BigDecimal err = px.abs(mc);
			BigDecimal abx = x.abs(mc);
			for (int j = n - 1; j >= 0; j--) {
				ddpx = x.multiply(ddpx, mc).add(dpx, mc);
				dpx = x.multiply(dpx, mc).add(px, mc);
				px = coeff[j].add(px.multiply(x, mc), mc);
				err = px.abs(mc).add(abx.multiply(err, mc), mc);
			}
			// ddpx=p''(x)/2 => *2
			ddpx = ddpx.add(ddpx, mc);
			// px=p(x)
			// dpx=p'(x)
			// ddpx=p''(x)
			err = err.multiply(plusEps, mc);
			if (px.abs(mc).compareTo(err) <= 0) {
				return x;
			}

			BigComplex g = dpx.divide(px, mc);
			BigComplex g2 = g.multiply(g, mc);
			BigComplex h = g2.subtract(ddpx.divide(px, mc), mc);
			BigComplex sq = h.multiply(BigDecimal.valueOf(n), mc)
					.subtract(g2, mc).multiply(BigDecimal.valueOf(n - 1), mc)
					.sqrt(mc);
			BigComplex gp = g.add(sq, mc);
			BigComplex gm = g.subtract(sq, mc);
			BigDecimal abp = gp.abs(mc);
			BigDecimal abm = gm.abs(mc);
			if (abp.compareTo(abm) < 0) {
				gp = gm;
				abp = abm;
			}
			BigComplex dx;
			if (abp.compareTo(BigDecimal.ZERO) > 0) {
				dx = (new BigComplex(BigDecimal.valueOf(n))).divide(gp, mc);
			} else {
				// TODO handle case
				App.debug("unhandled case");
				dx = BigComplex.ZERO;
			}
			if (dx.abs(mc).compareTo(plusEps) <= 0) {
				return x;
			}
			x = x.subtract(dx, mc);

		}
		Log.warn("Max Iterations exceeded");
		return x;
	}

	// public BigComplex evalPoly(BigComplex[] poly,BigComplex val){
	// if (poly.length==0){
	// return BigComplex.ZERO;
	// }
	// BigComplex sum=poly[poly.length-1];
	// for (int i=poly.length-2;i>=0;i--){
	// sum=poly[i].add(sum.multiply(val));
	// }
	// return sum;
	// }
	//
	// public BigComplex evalDiffPoly(BigComplex[] poly,BigComplex val){
	// if (poly.length<=1){
	// return BigComplex.ZERO;
	// }
	// BigComplex sum=poly[poly.length-1].multiply(new
	// BigDecimal(poly.length-1));
	// for (int i=poly.length-2;i>=1;i--){
	// sum=poly[i].multiply(new BigDecimal(i)).add(sum.multiply(val));
	// }
	// return sum;
	// }
	//
	// public BigComplex evalDiff2Poly(BigComplex[] poly,BigComplex val){
	// if (poly.length<=2){
	// return BigComplex.ZERO;
	// }
	// BigComplex sum=poly[poly.length-1].multiply(new
	// BigDecimal((poly.length-1)*(poly.length-2)));
	// for (int i=poly.length-2;i>=2;i--){
	// sum=poly[i].multiply(new BigDecimal(i*(i-1))).add(sum.multiply(val));
	// }
	// return sum;
	// }

	// public BigComplex evalDiffPoly(BigComplex[] poly,BigComplex val){
	// if (poly.length<=1){
	// return BigComplex.ZERO;
	// }
	//
	// }

}
