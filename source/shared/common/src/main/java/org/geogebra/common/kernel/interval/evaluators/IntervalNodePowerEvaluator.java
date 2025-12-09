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

package org.geogebra.common.kernel.interval.evaluators;

import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.node.IntervalExpressionNode;
import org.geogebra.common.kernel.interval.node.IntervalNode;
import org.geogebra.common.kernel.interval.node.IntervalOperation;
import org.geogebra.common.kernel.interval.operators.IntervalNodeEvaluator;
import org.geogebra.common.util.debug.Log;

/**
 * Class to evaluate expressions on an interval that has power in it.
 */
public class IntervalNodePowerEvaluator {

	private final IntervalNodeEvaluator evaluator;

	/**
	 *
	 * @param evaluator {@link IntervalNodeEvaluator}
	 */
	public IntervalNodePowerEvaluator(IntervalNodeEvaluator evaluator) {
		this.evaluator = evaluator;
	}

	/**
	 * Handles the power computation.
	 *
	 * @param base of the power.
	 * @param exponent of the power.
	 * @param right node to compute.
	 * @return the value of the power.
	 */
	public Interval handle(Interval base, Interval exponent, IntervalNode right) {
		if (exponent.isUndefined()) {
			return undefined();
		}

		if (MyDouble.exactEqual(base.getLow(), Math.E)) {
			return evaluator.exp(exponent);
		}

		if (!base.isPositive() && right.asExpressionNode() != null) {
			try {
				Interval negPower = calculateNegPower(right.asExpressionNode(), base);
				if (!negPower.isUndefined()) {
					return negPower;
				}
			} catch (Exception e) {
				Log.debug(e);
			}
		}

		return evaluator.pow(base, exponent);
	}

	private Interval calculateNegPower(IntervalExpressionNode node, Interval base) {
		if (isPositiveFraction(node)) {
			return negativePower(base, node);
		} else if (isNegativeFraction(node)) {
			return evaluator.inverse(negativePower(base,
					node.getRight().asExpressionNode()));
		}

		return undefined();
	}

	private boolean isPositiveFraction(IntervalExpressionNode node) {
		return node.isOperation(IntervalOperation.DIVIDE);
	}

	private boolean isNegativeFraction(IntervalExpressionNode node) {
		return node.isOperation(IntervalOperation.MULTIPLY)
				&& isMinusOne(node.getLeft())
				&& node.getRight().asExpressionNode().isOperation(IntervalOperation.DIVIDE);
	}

	private boolean isMinusOne(IntervalNode node) {
		return node != null && node.value().isMinusOne();
	}

	private Interval negativePower(Interval base, IntervalExpressionNode node) {
		Interval nominator = node.getLeft().value();
		if (nominator.isSingletonInteger()) {
			Interval denominator = node.getRight().value();
			if (denominator.isUndefined()) {
				return undefined();
			} else if (denominator.isSingletonInteger()) {
				return powerFraction(base, (long) nominator.getLow(),
						(long) denominator.getLow());

			}
		}
		return undefined();
	}

	private Interval powerFraction(Interval x, long a, long b) {
		Interval posPower = powerFractionPositive(x, Math.abs(a), Math.abs(b));
		posPower.setInverted(x.isInverted());
		if (a * b < 0) {
			return evaluator.inverse(posPower);
		} else {
			return posPower;
		}
	}

	private Interval powerFractionPositive(Interval x, long a, long b) {
		long gcd = Kernel.gcd(a, b);
		if (gcd == 0) {
			return undefined();
		}

		long nominator = a / gcd;
		long denominator = b / gcd;
		Interval interval = new Interval(x);
		Interval base = nominator == 1
				? interval
				: evaluator.pow(interval, nominator);

		if (base.isPositiveWithZero()) {
			return evaluator.pow(base, 1d / denominator);
		}
		if (base.contains(0)) {
			if (isOdd(denominator)) {
				return new Interval(-Math.pow(-base.getLow(), 1d / denominator),
						Math.pow(base.getHigh(), 1d / denominator));
			}

			return evaluator.pow(new Interval(0, base.getHigh()), 1d / denominator);
		}
		if (isOdd(denominator)) {
			return evaluator.pow(base.negative(), 1d / denominator).negative();
		}

		return undefined();
	}

	private boolean isOdd(long value) {
		return (Math.abs(value) % 2) == 1;
	}
}
