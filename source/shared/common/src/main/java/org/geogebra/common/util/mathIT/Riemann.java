// vendored
/*
 * Riemann.java - Program providing the Riemann zeta function.
 *
 * Copyright (C) 2004-2015 Andreas de Vries
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301  USA
 * 
 * As a special exception, the copyright holders of this program give you permission 
 * to link this program with independent modules to produce an executable, 
 * regardless of the license terms of these independent modules, and to copy and 
 * distribute the resulting executable under terms of your choice, provided that 
 * you also meet, for each linked independent module, the terms and conditions of 
 * the license of that module. An independent module is a module which is not derived 
 * from or based on this program. If you modify this program, you may extend 
 * this exception to your version of the program, but you are not obligated to do so. 
 * If you do not wish to do so, delete this exception statement from your version.
 */

package org.geogebra.common.util.mathIT;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static org.geogebra.common.util.mathIT.Complex.ONE_;
import static org.geogebra.common.util.mathIT.Complex.add;
import static org.geogebra.common.util.mathIT.Complex.divide;
import static org.geogebra.common.util.mathIT.Complex.exp;
import static org.geogebra.common.util.mathIT.Complex.gamma;
import static org.geogebra.common.util.mathIT.Complex.lnGamma;
import static org.geogebra.common.util.mathIT.Complex.lnSin;
import static org.geogebra.common.util.mathIT.Complex.multiply;
import static org.geogebra.common.util.mathIT.Complex.power;
import static org.geogebra.common.util.mathIT.Complex.sin;
import static org.geogebra.common.util.mathIT.Complex.subtract;

/**
 * This class provides the Riemann zeta function &#950;(<i>s</i>) for any
 * complex number <i>s</i> &#8712; <span style="font-size:large;">\u2102</span>
 * and the Riemann-Siegel functions <i>Z</i> and &theta;.
 *
 * @author Andreas de Vries, GeoGebra
 * 
 *         forked from
 *         https://github.com/vriesa/mathIT/blob/master/src/org/mathIT/numbers/
 *         Riemann.java
 */
public final class Riemann {
	/**
	 * The predefined accuracy up to which infinite sums are approximated.
	 * 
	 * @see #zeta(double[])
	 */
	public static final double EPSILON = 1e-6;

	// calculate infinite series to n terms
	private static final int maxTermNo = 70;

	private static final double[] dkn = new double[maxTermNo];

	private static final double dnn;

	static {
		double[] dterm = new double[maxTermNo + 1];
		double tempdnn = 0;

		dterm[0] = 1;
		for (int k = 1; k <= maxTermNo; k++) {
			dterm[k] = dterm[k - 1] * 2.0 * (maxTermNo + k - 1)
					* (maxTermNo - k + 1) / ((2 * k - 1) * k);
			tempdnn += dterm[k];
		}

		dnn = tempdnn;

		double tempdkn = 1 - dnn;

		dkn[0] = -tempdkn;

		int plusminus = 1;
		for (int k = 1; k < maxTermNo; k++) {
			tempdkn += dterm[k];
			dkn[k] = plusminus * tempdkn;
			plusminus = -plusminus;
		}
	}

	// Suppresses default constructor, ensuring non-instantiability.
	private Riemann() {
	}

	/**
	 * Returns the value &#967;(<i>s</i>) for a complex number <i>s</i> &#8712;
	 * <span style="font-size:large;">\u2102</span>, such that &#950;(<i>s</i>)
	 * = &#967;(<i>s</i>) &#950;(1 - <i>s</i>). It is defined as
	 * <table style="margin:auto;"><caption></caption>
	 * <tr>
	 * <td>&#967;(<i>s</i>)</td>
	 * <td>&nbsp; = &nbsp;</td>
	 * <td>2<sup><i>s</i></sup> &pi;<sup><i>s</i> - 1</sup> sin(
	 * <i>s</i> &pi;/2) &Gamma;(1 - <i>s</i>)</td>
	 * <td>&nbsp; = &nbsp;</td>
	 * <td>&#960;<sup><i>s</i> - 1/2</sup></td>
	 * <td>
	 * <table border=""><caption></caption>
	 * <tr>
	 * <td>&#915;((1 - <i>s</i>)/2)</td>
	 * </tr>
	 * <tr>
	 * <td style="height:1px;">
	 * <hr>
	 * </td>
	 * </tr>
	 * <tr>
	 * <td>&#915;(<i>s</i>/2)</td>
	 * </tr>
	 * </table>
	 * </td>
	 * </tr>
	 * </table>
	 * <p>
	 * We have &#967;(<i>s</i>) &#967;(1 - <i>s</i>) = 1. [Eqs.
	 * (2.1.10)-(2.1.12) in E.C. Titchmarsh: <i>The Theory of the Riemann
	 * Zeta-function.</i> 2nd Edition, Oxford University Press, Oxford 1986],
	 * <a href="https://books.google.com/books?id=1CyfApMt8JYC&pg=PA16" target=
	 * "_new">https://books.google.com/books?id=1CyfApMt8JYC&amp;pg=PA16</a>
	 * </p>
	 * <p>
	 * Moreover &chi; is related to the Riemann-Siegel theta function
	 * {@link #theta(double) &theta;} by the equation
	 * </p>
	 * <p style="text-align:center;">
	 * &chi;(&frac12; + i<i>t</i>) = e<sup>-2i&theta;(<i>t</i>)</sup>,
	 * </p>
	 * <p>
	 * see E.C. Titchmarsh: <i>The Theory of the Riemann Zeta-function.</i> 2nd
	 * Edition, Oxford University Press, Oxford 1986, p. 89
	 * <a href="https://books.google.com/books?id=1CyfApMt8JYC&pg=PA89" target=
	 * "_new">https://books.google.com/books?id=1CyfApMt8JYC&amp;pg=PA89</a>.
	 * </p>
	 * 
	 * @param s
	 *            a complex value
	 * @return &#967;(<i>s</i>)
	 * @see #zeta(double[])
	 * @see #theta(double)
	 * @see Complex#gamma(double[])
	 */
	public static double[] chi(double[] s) {
		// /*
		if (s[0] > .5) { // gamma is approximated fast only for Re s <= .5 <=>
							// Re(1 - s) >= .5
			return divide(1.0, chi(subtract(ONE_, s)));
		}
		double[] result;
		if (PI * abs(s[1]) > 709) { // for large imaginary parts use
									// log-versions
			// s ln 2 + (s-1) ln pi + lnGamma(1-s) + lnSin (pi s/2)
			result = multiply(log(2), s);
			result = add(result, multiply(log(PI), subtract(s, ONE_)));
			result = add(result, lnGamma(subtract(ONE_, s)));
			result = add(result, lnSin(multiply(PI / 2, s)));
			return exp(result);
		}
		// 2^s pi^(s-1) Gamma(1-s) sin(pi s/2):
		result = gamma(subtract(ONE_, s));
		result = multiply(result, sin(multiply(PI / 2, s)));
		result = multiply(result, power(PI, subtract(s, ONE_)));
		result = multiply(result, power(2, s));
		return result;
	}

	/**
	 * Riemann zeta function &#950;(<i>s</i>) for <i>s</i> &#8712;
	 * <span style="font-size:large;">\u2102</span>. It is computed by
	 * <table style="margin:auto;"><caption></caption>
	 * <tr>
	 * <td>&#950;(<i>s</i>)</td>
	 * <td>&nbsp; = &nbsp;</td>
	 * <td>
	 * <table border=""><caption></caption>
	 * <tr>
	 * <td>1</td>
	 * </tr>
	 * <tr>
	 * <td style="height:1px;">
	 * <hr>
	 * </td>
	 * </tr>
	 * <tr>
	 * <td>1 - 2<sup>1 - <i>s</i></sup></td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td>
	 * <table border=""><caption></caption>
	 * <tr>
	 * <td class="small">&#8734;</td>
	 * </tr>
	 * <tr>
	 * <td style="font-size:xx-large;">&#931;</td>
	 * </tr>
	 * <tr>
	 * <td class="small"><i>n</i>=1</td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td>
	 * <table border=""><caption></caption>
	 * <tr>
	 * <td>(-1)<sup><i>n</i> - 1</sup></td>
	 * </tr>
	 * <tr>
	 * <td style="height:1px;">
	 * <hr>
	 * </td>
	 * </tr>
	 * <tr>
	 * <td><i>n<sup>s</sup></i><sub>&nbsp;</sub></td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td colspan="2"></td>
	 * <td>&nbsp; &nbsp; &nbsp; if Re <i>s</i> &gt; 0,</td>
	 * </tr>
	 * <tr>
	 * <td>&nbsp;</td>
	 * </tr>
	 * <tr>
	 * <td>&#950;(<i>s</i>)</td>
	 * <td>&nbsp; = &nbsp;</td>
	 * <td>
	 * <table border=""><caption></caption>
	 * <tr>
	 * <td>1</td>
	 * </tr>
	 * <tr>
	 * <td style="height:1px;">
	 * <hr>
	 * </td>
	 * </tr>
	 * <tr>
	 * <td>1 - 2<sup>1 - <i>s</i></sup></td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td>
	 * <table border=""><caption></caption>
	 * <tr>
	 * <td class="small">&#8734;</td>
	 * </tr>
	 * <tr>
	 * <td style="font-size:xx-large;">&#931;</td>
	 * </tr>
	 * <tr>
	 * <td class="small"><i>n</i>=1</td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td>
	 * <table border=""><caption></caption>
	 * <tr>
	 * <td>1</td>
	 * </tr>
	 * <tr>
	 * <td style="height:1px;">
	 * <hr>
	 * </td>
	 * </tr>
	 * <tr>
	 * <td>2<sup><i>n</i> + 1</sup></td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td>
	 * <table border=""><caption></caption>
	 * <tr>
	 * <td class="small"><i>n</i></td>
	 * </tr>
	 * <tr>
	 * <td style="font-size:xx-large;">&#931;</td>
	 * </tr>
	 * <tr>
	 * <td class="small"><i>k</i>=0</td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td>
	 * <table border=""><caption></caption>
	 * <tr>
	 * <td>(-1)<sup><i>k</i></sup></td>
	 * <td style="font-size:xx-large;">(</td>
	 * <td>
	 * <table border=""><caption></caption>
	 * <tr>
	 * <td><i>n</i></td>
	 * </tr>
	 * <tr>
	 * <td><i>k</i></td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td style="font-size:xx-large;">)</td>
	 * <td>(<i>k</i> + 1)<sup><i>-s</i></sup></td>
	 * </table>
	 * </td>
	 * <td>&nbsp; &nbsp; &nbsp; otherwise.</td>
	 * </tr>
	 * </table>
	 * <p>
	 * However, in this method the algorithm is used which is documented as
	 * "Algorithm 1" in Borwein et al, <i>The Riemann Hypothesis</i>, Springer,
	 * Berlin 2008, p 35 (
	 * <a href="https://books.google.com/books?id=Qm1aZA-UwX4C&pg=PA35" target=
	 * "_new">https://books.google.com/books?id=Qm1aZA-UwX4C&amp;pg=PA35</a>)
	 * </p>
	 * <p>
	 * The functions &zeta;, <i>Z</i> and &theta; are related by the equality
	 * <i>Z</i>(<i>t</i>) = e<sup>i &#952;(<i>t</i>)</sup> &#950;(&#189; + i
	 * <i>t</i>). Cf. H.M. Edwards: <i>Riemann's Zeta Function.</i> Academic
	 * Press, New York 1974, &#167;6.5 (
	 * <a href="https://books.google.de/books?id=ruVmGFPwNhQC&pg=PA119" target=
	 * "_new">https://books.google.de/books?id=ruVmGFPwNhQC&amp;pg=PA119</a>).
	 * </p>
	 * 
	 * @param s
	 *            the argument
	 * @return the zeta function value &zeta;(<i>s</i>)
	 * @see #z(double)
	 * @see #theta(double)
	 */
	public static double[] zeta(double[] s) {
		double[] sum = new double[2];

		if (abs(s[0] - 1) < EPSILON && abs(s[1]) < EPSILON) {
			sum[0] = Double.POSITIVE_INFINITY;
			sum[1] = Double.POSITIVE_INFINITY; // in fact: not defined!!
		} else if (abs(s[0]) < EPSILON && abs(s[1]) < EPSILON) {
			// zeta(0) = -1/2:
			sum[0] = -.5;
			return sum;
		} else if (s[0] < 0 && abs(s[0] % 2) < EPSILON && abs(s[1]) < EPSILON) {
			// negative evens return zero, zeta(-2n) = 0:
			return sum;
		} else if (s[0] < 0) { // actually: s[0] < 0.5, but the result is not
			// satisfactory ...
			// use the reflection functional equation zeta(s) = chi(s)
			// zeta(1-s):
			return multiply(chi(s), zeta(subtract(ONE_, s)));
		} else if (s[0] > 56) {
			// pointless to calculate as the algorithm below is incorrect
			// for larger numbers due to numerical errors
			sum[0] = 1;
			sum[1] = 0;
			return sum;
		} else if (abs(s[0] - 0.5) < EPSILON && abs(s[1]) > 10) {
			double temp = abs(s[1]);
			double[] ret = multiply(z(temp),
					exp(new double[] { 0, -theta(temp) }));

			if (s[1] < 0) {
				ret[1] = -ret[1];
			}

			return ret;
		} else {
			// Algorithm according to Borwein et al (2008), p 35:
			sum[0] = dkn[0];
			for (int k = 1; k < maxTermNo; k++) {
				sum = add(sum, divide(dkn[k], power(k + 1, s)));
			}

			sum = divide(sum,
					multiply(dnn, subtract(ONE_, power(2, subtract(ONE_, s)))));
		}
		return sum;
	}

	/**
	 * C terms for Riemann-Siegel coefficients of remainder terms <i>C</i>
	 * <sub>0</sub>, -<i>C</i><sub>1</sub>, <i>C</i><sub>2</sub>, -<i>C</i>
	 * <sub>3</sub>, <i>C</i><sub>4</sub>, cf. H.M. Edwards, Riemann's Zeta
	 * Function. Academic Press, Ne York 1974, p 158.
	 * 
	 * @author Jose Menez (https://gist.github.com/cab1729/1317706).
	 * @param n
	 *            the index of the coefficient <i>C<sub>n</sub></i>
	 * @param z
	 *            the variable to which the Taylor series is expanded
	 * @return the error correction term <i>C<sub>n</sub></i>
	 */
	private static double termC(int n, double z) {
		if (n == 0) {
			return .38268343236508977173 * pow(z, 0.0)
					+ .43724046807752044936 * pow(z, 2.0)
					+ .13237657548034352332 * pow(z, 4.0)
					- .01360502604767418865 * pow(z, 6.0)
					- .01356762197010358089 * pow(z, 8.0)
					- .00162372532314446528 * pow(z, 10.0)
					+ .00029705353733379691 * pow(z, 12.0)
					+ .00007943300879521470 * pow(z, 14.0)
					+ .00000046556124614505 * pow(z, 16.0)
					- .00000143272516309551 * pow(z, 18.0)
					- .00000010354847112313 * pow(z, 20.0)
					+ .00000001235792708386 * pow(z, 22.0)
					+ .00000000178810838580 * pow(z, 24.0)
					- .00000000003391414390 * pow(z, 26.0)
					- .00000000001632663390 * pow(z, 28.0)
					- .00000000000037851093 * pow(z, 30.0)
					+ .00000000000009327423 * pow(z, 32.0)
					+ .00000000000000522184 * pow(z, 34.0)
					- .00000000000000033507 * pow(z, 36.0)
					- .00000000000000003412 * pow(z, 38.0)
					+ .00000000000000000058 * pow(z, 40.0)
					+ .00000000000000000015 * pow(z, 42.0);
		} else if (n == 1) {
			return -.02682510262837534703 * pow(z, 1.0)
					+ .01378477342635185305 * pow(z, 3.0)
					+ .03849125048223508223 * pow(z, 5.0)
					+ .00987106629906207647 * pow(z, 7.0)
					- .00331075976085840433 * pow(z, 9.0)
					- .00146478085779541508 * pow(z, 11.0)
					- .00001320794062487696 * pow(z, 13.0)
					+ .00005922748701847141 * pow(z, 15.0)
					+ .00000598024258537345 * pow(z, 17.0)
					- .00000096413224561698 * pow(z, 19.0)
					- .00000018334733722714 * pow(z, 21.0)
					+ .00000000446708756272 * pow(z, 23.0)
					+ .00000000270963508218 * pow(z, 25.0)
					+ .00000000007785288654 * pow(z, 27.0)
					- .00000000002343762601 * pow(z, 29.0)
					- .00000000000158301728 * pow(z, 31.0)
					+ .00000000000012119942 * pow(z, 33.0)
					+ .00000000000001458378 * pow(z, 35.0)
					- .00000000000000028786 * pow(z, 37.0)
					- .00000000000000008663 * pow(z, 39.0)
					- .00000000000000000084 * pow(z, 41.0)
					+ .00000000000000000036 * pow(z, 43.0)
					+ .00000000000000000001 * pow(z, 45.0);
		} else if (n == 2) {
			return +.00518854283029316849 * pow(z, 0.0)
					+ .00030946583880634746 * pow(z, 2.0)
					- .01133594107822937338 * pow(z, 4.0)
					+ .00223304574195814477 * pow(z, 6.0)
					+ .00519663740886233021 * pow(z, 8.0)
					+ .00034399144076208337 * pow(z, 10.0)
					- .00059106484274705828 * pow(z, 12.0)
					- .00010229972547935857 * pow(z, 14.0)
					+ .00002088839221699276 * pow(z, 16.0)
					+ .00000592766549309654 * pow(z, 18.0)
					- .00000016423838362436 * pow(z, 20.0)
					- .00000015161199700941 * pow(z, 22.0)
					- .00000000590780369821 * pow(z, 24.0)
					+ .00000000209115148595 * pow(z, 26.0)
					+ .00000000017815649583 * pow(z, 28.0)
					- .00000000001616407246 * pow(z, 30.0)
					- .00000000000238069625 * pow(z, 32.0)
					+ .00000000000005398265 * pow(z, 34.0)
					+ .00000000000001975014 * pow(z, 36.0)
					+ .00000000000000023333 * pow(z, 38.0)
					- .00000000000000011188 * pow(z, 40.0)
					- .00000000000000000416 * pow(z, 42.0)
					+ .00000000000000000044 * pow(z, 44.0)
					+ .00000000000000000003 * pow(z, 46.0);
		} else if (n == 3) {
			return -.00133971609071945690 * pow(z, 1.0)
					+ .00374421513637939370 * pow(z, 3.0)
					- .00133031789193214681 * pow(z, 5.0)
					- .00226546607654717871 * pow(z, 7.0)
					+ .00095484999985067304 * pow(z, 9.0)
					+ .00060100384589636039 * pow(z, 11.0)
					- .00010128858286776622 * pow(z, 13.0)
					- .00006865733449299826 * pow(z, 15.0)
					+ .00000059853667915386 * pow(z, 17.0)
					+ .00000333165985123995 * pow(z, 19.0)
					+ .00000021919289102435 * pow(z, 21.0)
					- .00000007890884245681 * pow(z, 23.0)
					- .00000000941468508130 * pow(z, 25.0)
					+ .00000000095701162109 * pow(z, 27.0)
					+ .00000000018763137453 * pow(z, 29.0)
					- .00000000000443783768 * pow(z, 31.0)
					- .00000000000224267385 * pow(z, 33.0)
					- .00000000000003627687 * pow(z, 35.0)
					+ .00000000000001763981 * pow(z, 37.0)
					+ .00000000000000079608 * pow(z, 39.0)
					- .00000000000000009420 * pow(z, 41.0)
					- .00000000000000000713 * pow(z, 43.0)
					+ .00000000000000000033 * pow(z, 45.0)
					+ .00000000000000000004 * pow(z, 47.0);
		} else {
			return +.00046483389361763382 * pow(z, 0.0)
					- .00100566073653404708 * pow(z, 2.0)
					+ .00024044856573725793 * pow(z, 4.0)
					+ .00102830861497023219 * pow(z, 6.0)
					- .00076578610717556442 * pow(z, 8.0)
					- .00020365286803084818 * pow(z, 10.0)
					+ .00023212290491068728 * pow(z, 12.0)
					+ .00003260214424386520 * pow(z, 14.0)
					- .00002557906251794953 * pow(z, 16.0)
					- .00000410746443891574 * pow(z, 18.0)
					+ .00000117811136403713 * pow(z, 20.0)
					+ .00000024456561422485 * pow(z, 22.0)
					- .00000002391582476734 * pow(z, 24.0)
					- .00000000750521420704 * pow(z, 26.0)
					+ .00000000013312279416 * pow(z, 28.0)
					+ .00000000013440626754 * pow(z, 30.0)
					+ .00000000000351377004 * pow(z, 32.0)
					- .00000000000151915445 * pow(z, 34.0)
					- .00000000000008915418 * pow(z, 36.0)
					+ .00000000000001119589 * pow(z, 38.0)
					+ .00000000000000105160 * pow(z, 40.0)
					- .00000000000000005179 * pow(z, 42.0)
					- .00000000000000000807 * pow(z, 44.0)
					+ .00000000000000000011 * pow(z, 46.0)
					+ .00000000000000000004 * pow(z, 48.0);
		}
	}

	/**
	 * Riemann-Siegel Z-function <i>Z</i>(<i>t</i>). It is determined by the
	 * formula
	 * <table style="margin:auto;"><caption></caption>
	 * <tr>
	 * <td><i>Z</i>(<i>t</i>)</td>
	 * <td>&nbsp; = &nbsp;</td>
	 * <td>2</td>
	 * <td>
	 * <table border=""><caption></caption>
	 * <tr>
	 * <td class="small"><i>m</i></td>
	 * </tr>
	 * <tr>
	 * <td style="font-size:xx-large;">&#931;</td>
	 * </tr>
	 * <tr>
	 * <td class="small"><i>n</i>=1</td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td>
	 * <table border=""><caption></caption>
	 * <tr>
	 * <td>cos[&#952;(<i>t</i>) - <i>t</i> ln <i>n</i>]</td>
	 * </tr>
	 * <tr>
	 * <td style="height:1px;">
	 * <hr>
	 * </td>
	 * </tr>
	 * <tr>
	 * <td>&#8730;<i>n</i></td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td colspan="2"></td>
	 * <td>&nbsp; + <i>O</i>(|<i>t</i>|<sup>-1/4</sup>).</td>
	 * </tr>
	 * </table>
	 * <p>
	 * where
	 * </p>
	 * <table style="margin:auto;"><caption></caption>
	 * <tr>
	 * <td><i>m</i> &nbsp; = &nbsp; <i>m</i>(<i>t</i>)</td>
	 * <td>&nbsp; = &nbsp;</td>
	 * <td><span style="font-size:xx-large;">\u23a3</span></td>
	 * <td>
	 * <table border=""><caption></caption>
	 * <tr>
	 * <td>|<i>t</i>|</td>
	 * </tr>
	 * <tr>
	 * <td style="height:1px;">
	 * <hr>
	 * </td>
	 * </tr>
	 * <tr>
	 * <td>2 &#960;</td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td><span style="font-size:xx-large;">\u23a6</span></td>
	 * </table>
	 * <p>
	 * and &theta; denotes the Riemann-Siegel {@link #theta(double) theta
	 * function}.
	 * </p>
	 * <p>
	 * The functions &zeta;, <i>Z</i> and &theta; are related by the equality
	 * <i>Z</i>(<i>t</i>) = e<sup>i &#952;(<i>t</i>)</sup> &#950;(&#189; + i
	 * <i>t</i>). Cf. H.M. Edwards: <i>Riemann's Zeta Function.</i> Academic
	 * Press, New York 1974, &#167;6.5 (
	 * <a href="https://books.google.de/books?id=ruVmGFPwNhQC&pg=PA119" target=
	 * "_new">https://books.google.de/books?id=ruVmGFPwNhQC&amp;pg=PA119</a>).
	 * </p>
	 * 
	 * @param t
	 *            value on the critical line <i>s</i> = &#189; + i<i>t</i>.
	 * @return <i>Z</i>(<i>t</i>)
	 * @see #zeta(double[])
	 * @see #theta(double)
	 */
	public static double z(double t) {
		double theta = theta(t);
		long m = (long) sqrt(t / (2 * PI));
		double sum = 0;
		for (long n = 1; n <= m; n++) {
			sum += cos(theta - t * log(n)) / sqrt(n);
		}
		sum *= 2;

		// correction term (Edwards 1974, pp 154):
		double p = sqrt(t / (2 * PI)) % 1; // fractional part
		double R = 0; // pow(2*PI/t, 0.25) * cos(2*PI * (p*p - p - 1./16)) /
						// cos(2*PI*p);

		// add remainder R here
		double pi2t = 2 * PI / t;
		for (int k = 0; k <= 4; k++) {
			R = R + termC(k, 2 * p - 1) * pow(pi2t, 0.5 * k);
		}

		sum += (m % 2 == 0) ? -pow(pi2t, 0.25) * R : pow(pi2t, 0.25) * R;

		return sum;
	}

	/**
	 * Riemann-Siegel theta function. In this method the value theta(t) is
	 * approximated using the Stirling series
	 * <table style="margin:auto;"><caption></caption>
	 * <tr>
	 * <td><i>&#952;</i>(<i>t</i>)</td>
	 * <td>&nbsp; = &nbsp;</td>
	 * <td>
	 * <table border=""><caption></caption>
	 * <tr>
	 * <td><i>t</i></td>
	 * </tr>
	 * <tr>
	 * <td style="height:1px;">
	 * <hr>
	 * </td>
	 * </tr>
	 * <tr>
	 * <td>2</td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td>ln</td>
	 * <td>
	 * <table border=""><caption></caption>
	 * <tr>
	 * <td><i>t</i></td>
	 * </tr>
	 * <tr>
	 * <td style="height:1px;">
	 * <hr>
	 * </td>
	 * </tr>
	 * <tr>
	 * <td>2 &#960;</td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td>&nbsp; - &nbsp;</td>
	 * <td>
	 * <table border=""><caption></caption>
	 * <tr>
	 * <td><i>t</i></td>
	 * </tr>
	 * <tr>
	 * <td style="height:1px;">
	 * <hr>
	 * </td>
	 * </tr>
	 * <tr>
	 * <td>2</td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td>&nbsp; - &nbsp;</td>
	 * <td>
	 * <table border=""><caption></caption>
	 * <tr>
	 * <td>&#960;</td>
	 * </tr>
	 * <tr>
	 * <td style="height:1px;">
	 * <hr>
	 * </td>
	 * </tr>
	 * <tr>
	 * <td>8</td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td>&nbsp; + &nbsp;</td>
	 * <td>
	 * <table border=""><caption></caption>
	 * <tr>
	 * <td>1</td>
	 * </tr>
	 * <tr>
	 * <td style="height:1px;">
	 * <hr>
	 * </td>
	 * </tr>
	 * <tr>
	 * <td>48<i>t</i></td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td>+</td>
	 * <td>
	 * <table border=""><caption></caption>
	 * <tr>
	 * <td>7</td>
	 * </tr>
	 * <tr>
	 * <td style="height:1px;">
	 * <hr>
	 * </td>
	 * </tr>
	 * <tr>
	 * <td>5760 <i>t</i><sup>3</sup></td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td></td>
	 * <td>+</td>
	 * <td>
	 * <table border=""><caption></caption>
	 * <tr>
	 * <td>31</td>
	 * </tr>
	 * <tr>
	 * <td style="height:1px;">
	 * <hr>
	 * </td>
	 * </tr>
	 * <tr>
	 * <td>80640 <i>t</i><sup>5</sup></td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td>+</td>
	 * <td>
	 * <table border=""><caption></caption>
	 * <tr>
	 * <td>127</td>
	 * </tr>
	 * <tr>
	 * <td style="height:1px;">
	 * <hr>
	 * </td>
	 * </tr>
	 * <tr>
	 * <td>430080 <i>t</i><sup>7</sup></td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td></td>
	 * <td>&nbsp; + <i>R</i>(<i>t</i>) &nbsp; &nbsp; with |<i>R</i>(<i>t</i>)|
	 * &lt;</td>
	 * <td>
	 * <table border=""><caption></caption>
	 * <tr>
	 * <td>1</td>
	 * </tr>
	 * <tr>
	 * <td style="height:1px;">
	 * <hr>
	 * </td>
	 * </tr>
	 * <tr>
	 * <td><i>t</i><sup>9</sup></td>
	 * </tr>
	 * </table>
	 * </td>
	 * </tr>
	 * </table>
	 * <p>
	 * <!-- up to the order of <i>t</i><sup>-7</sup>. --> The functions &zeta;,
	 * <i>Z</i> and &theta; are related by the equality <i>Z</i>(<i>t</i>) = e
	 * <sup>i &#952;(<i>t</i>)</sup> &#950;(&#189; + i<i>t</i>). Cf. H.M.
	 * Edwards: <i>Riemann's Zeta Function.</i> Academic Press, New York 1974,
	 * &#167;6.5 (
	 * <a href="https://books.google.de/books?id=ruVmGFPwNhQC&pg=PA119" target=
	 * "_new">https://books.google.de/books?id=ruVmGFPwNhQC&amp;pg=PA119</a>).
	 * </p>
	 * 
	 * @param t
	 *            value on the critical line <i>s</i> = &#189; + i<i>t</i>.
	 * @return <i>&theta;</i>(<i>t</i>)
	 * @see #zeta(double[])
	 * @see #z(double)
	 */
	public static double theta(double t) {
		return -0.5 * t * (1 + log(2) + log(PI) + log(1 / t)) - PI / 8
				+ 1 / (48 * t) + 7 / (5760 * t * t * t)
				+ 31 / (80640 * t * t * t * t * t)
				+ 127 / (430080 * t * t * t * t * t * t * t);
	}
}
