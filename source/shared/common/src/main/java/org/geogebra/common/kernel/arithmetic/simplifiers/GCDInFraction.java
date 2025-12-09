/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;

public class GCDInFraction {

	private final long gcd;
	private final SimplifyUtils utils;
	private final long numerator;
	private final long denominator;
	private long reducedNumerator;
	private long reducedDenominator;

	/**
	 *
	 * @param utils {@link SimplifyUtils}
	 * @param numerator of the fraction to compute GCD of.
	 * @param denominator of the fraction to compute GCD of.
	 */
	public GCDInFraction(SimplifyUtils utils, long numerator, long denominator) {
		this.utils = utils;
		this.numerator = numerator;
		this.denominator = denominator;
		gcd = Kernel.gcd(numerator, denominator);
		reducedNumerator = numerator / gcd;
		reducedDenominator = denominator / gcd;
	}

	/**
	 *
	 * @return the greatest common divisor of numerator and denominator
	 */
	public long gcd() {
		return gcd;
	}

	/**
	 *
	 * @return true if gcd is 1 or -1.
	 */
	public boolean isTrivial() {
		return Math.abs(gcd) == 1;
	}

	/**
	 *
	 * @return numerator / gcd
	 */
	public long reducedNumerator() {
		return reducedNumerator;
	}

	/**
	 *
	 * @return denominator / gcd
	 */
	public long reducedDenominator() {
		return reducedDenominator;
	}

	/**
	 *
	 * @return reducedNumerator as {@link ExpressionValue}
	 */
	public ExpressionValue reducedNumeratorValue() {
		return utils.newDouble(reducedNumerator);
	}

	/**
	 *
	 * @return reducedDenominator as {@link ExpressionValue}
	 */
	public ExpressionValue reducedDenominatorValue() {
		return utils.newDouble(reducedDenominator);
	}

	/**
	 * flips sign when reduced denominator is negative
	 */
	public void flipIfNegative() {
		if (reducedDenominator < 0) {
			reducedDenominator = -reducedDenominator;
			reducedNumerator = -reducedNumerator;
		}
	}

	/**
	 *
	 * @return if gcd is the denominator
	 */
	public boolean isEqualDenominator() {
		return gcd == denominator;
	}

	/**
	 *
	 * @return if gcd is the numerator
	 */
	public boolean isEqualNumerator() {
		return gcd == numerator;
	}

	/**
	 *
	 * @return the {@link ExpressionNode} contains the reduced div.
	 */
	public ExpressionNode reducedFraction() {
		return utils.newDiv(reducedNumeratorValue(), reducedDenominatorValue());
	}

	/**
	 *
	 * @param value to check
	 * @return if gcd is the given value.
	 */
	public boolean isEqual(long value) {
		return gcd == value;
	}
}
