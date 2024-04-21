package org.apache.commons.math3.util;

import org.geogebra.common.util.DoubleConsts;

/*
 * Copyright (c) 2003, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

public class GWTMath {

	/**
	 * Returns the size of an ulp of the argument. An ulp, unit in the last
	 * place, of a {@code double} value is the positive distance between this
	 * floating-point value and the {@code
	 * double} value next larger in magnitude. Note that for non-NaN <i>x</i>,
	 * <code>ulp(-<i>x</i>) == ulp(<i>x</i>)</code>.
	 *
	 * <p>
	 * Special Cases:
	 * <ul>
	 * <li>If the argument is NaN, then the result is NaN.
	 * <li>If the argument is positive or negative infinity, then the result is
	 * positive infinity.
	 * <li>If the argument is positive or negative zero, then the result is
	 * {@code Double.MIN_VALUE}.
	 * <li>If the argument is &plusmn;{@code Double.MAX_VALUE}, then the result
	 * is equal to 2<sup>971</sup>.
	 * </ul>
	 *
	 * @param d
	 *            the floating-point value whose ulp is to be returned
	 * @return the size of an ulp of the argument
	 * @author Joseph D. Darcy
	 * @since 1.5
	 */
	public static double ulp(double d) {
		int exp = getExponent(d);

		switch (exp) {
		case Double.MAX_EXPONENT + 1: // NaN or infinity
			return Math.abs(d);

		case Double.MIN_EXPONENT - 1: // zero or subnormal
			return Double.MIN_VALUE;

		default:
			assert exp <= Double.MAX_EXPONENT
					&& exp >= Double.MIN_EXPONENT;

			// ulp(x) is usually 2^(SIGNIFICAND_WIDTH-1)*(2^ilogb(x))
			exp = exp - (DoubleConsts.SIGNIFICAND_WIDTH - 1);
			if (exp >= Double.MIN_EXPONENT) {
				return powerOfTwoD(exp);
			} else {
				// return a subnormal result; left shift integer
				// representation of Double.MIN_VALUE appropriate
				// number of positions
				return Double.longBitsToDouble(
						1L << (exp - (Double.MIN_EXPONENT
								- (DoubleConsts.SIGNIFICAND_WIDTH - 1))));
			}
		}
	}

	/**
	 * Returns a floating-point power of two in the normal range.
	 */
	static double powerOfTwoD(int n) {
		assert (n >= Double.MIN_EXPONENT
				&& n <= Double.MAX_EXPONENT);
		return Double.longBitsToDouble((((long) n
				+ (long) DoubleConsts.EXP_BIAS) << (DoubleConsts.SIGNIFICAND_WIDTH
						- 1))
				& DoubleConsts.EXP_BIT_MASK);
	}

	// http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/8u40-b25/java/lang/Math.java#Math.getExponent%28double%29
	/**
	 * Returns the unbiased exponent used in the representation of a
	 * {@code double}. Special cases:
	 *
	 * <ul>
	 * <li>If the argument is NaN or infinite, then the result is
	 * {@link Double#MAX_EXPONENT} + 1.
	 * <li>If the argument is zero or subnormal, then the result is
	 * {@link Double#MIN_EXPONENT} -1.
	 * </ul>
	 * 
	 * @param d
	 *            a {@code double} value
	 * @return the unbiased exponent of the argument
	 * @since 1.6
	 */
	public static int getExponent(double d) {
		/*
		 * Bitwise convert d to long, mask out exponent bits, shift to the right
		 * and then subtract out double's bias adjust to get true exponent
		 * value.
		 */
		// changed from doubleToRawLongBits() for GWT
		return (int) (((Double.doubleToLongBits(d)
				& DoubleConsts.EXP_BIT_MASK) >> (DoubleConsts.SIGNIFICAND_WIDTH
						- 1))
				- DoubleConsts.EXP_BIAS);
	}

	// from
	// https://groups.google.com/forum/#!topic/google-web-toolkit-contributors/I50Ry-x8ur0
	public static double IEEEremainder(double f1, double f2) {
		double r = Math.abs(f1 % f2);
		if (Double.isNaN(r) || r == f2 || r <= Math.abs(f2) / 2.0) {
			return r;
		} else {
			return Math.signum(f1) * (r - f2);
		}
	}

}

