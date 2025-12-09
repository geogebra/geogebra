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

package org.geogebra.common.kernel.arithmetic;

import static org.geogebra.common.kernel.arithmetic.MyDouble.isNumberImprecise;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Binary operation supporting both fixed and arbitrary precision computation.
 */
public enum DoubleOperation {
	PLUS {
		@Override
		protected BigDecimal evalDecimal(BigDecimal num1, BigDecimal num2) {
			return num1.add(num2);
		}

		@Override
		protected double evalDouble(double num1, double num2) {
			return num1 + num2;
		}
	}, MINUS {
		@Override
		protected BigDecimal evalDecimal(BigDecimal num1, BigDecimal num2) {
			return num1.subtract(num2);
		}

		@Override
		protected double evalDouble(double num1, double num2) {
			return num1 - num2;
		}
	}, MULTIPLY {
		@Override
		protected BigDecimal evalDecimal(BigDecimal num1, BigDecimal num2) {
			return num1.multiply(num2);
		}

		@Override
		protected double evalDouble(double num1, double num2) {
			return num1 * num2;
		}
	}, DIVIDE {
		@Override
		protected BigDecimal evalDecimal(BigDecimal num1, BigDecimal num2) {
			return num1.divide(num2, MathContext.DECIMAL128);
		}

		@Override
		protected double evalDouble(double num1, double num2) {
			return num1 / num2;
		}
	}, INT_POWER {
		@Override
		protected BigDecimal evalDecimal(BigDecimal num1, BigDecimal num2) {
			// limit precision here for very high powers
			return num1.pow((int) Math.round(num2.doubleValue()), MathContext.DECIMAL128);
		}

		@Override
		protected double evalDouble(double num1, double num2) {
			return MyDouble.pow(num1, num2);
		}
	};

	/**
	 * @param num1 first operand
	 * @param num2 second operand
	 * @param result result (can be the same object as num1)
	 */
	public void apply(MyDouble num1, NumberValue num2, MyDouble result) {
		if (isNumberImprecise(num1) || isNumberImprecise(num2)) {
			result.set(evalDouble(num1.getDouble(), num2.getDouble()));
			result.setImprecise(true);
			return;
		}

		BigDecimal numerator = num1.toDecimal();
		if (numerator != null) {
			BigDecimal denominator = num2.toDecimal();
			if (denominator != null) {
				result.set(evalDecimal(numerator, denominator));
				return;
			}
		}
		result.set(evalDouble(num1.getDouble(), num2.getDouble()));
	}

	protected abstract BigDecimal evalDecimal(BigDecimal num1, BigDecimal num2);

	protected abstract double evalDouble(double num1, double num2);
}
