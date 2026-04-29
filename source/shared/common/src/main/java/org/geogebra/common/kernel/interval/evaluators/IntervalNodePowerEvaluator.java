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

import static org.geogebra.common.kernel.interval.IntervalSetOps.connected;
import static org.geogebra.common.kernel.interval.IntervalSetOps.connectedInterval;
import static org.geogebra.common.kernel.interval.IntervalSetOps.empty;
import static org.geogebra.common.kernel.interval.IntervalSetOps.hasZero;
import static org.geogebra.common.kernel.interval.IntervalSetOps.leftRayFromInverted;
import static org.geogebra.common.kernel.interval.IntervalSetOps.negative;
import static org.geogebra.common.kernel.interval.IntervalSetOps.rightRayFromInverted;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalSet;
import org.geogebra.common.kernel.interval.IntervalSetOps;
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

	private static final class NegPowerResult {
		private final boolean handled;
		private final IntervalSet result;

		private NegPowerResult(boolean handled, IntervalSet result) {
			this.handled = handled;
			this.result = result;
		}

		private static NegPowerResult handled(IntervalSet result) {
			return new NegPowerResult(true, result);
		}

		private static NegPowerResult deferred() {
			return new NegPowerResult(false, empty());
		}
	}

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
	public IntervalSet handle(IntervalSet base, IntervalSet exponent, IntervalNode right) {
		if (exponent.isEmpty()) {
			return empty();
		}

		if (base.isConnected() && MyDouble.exactEqual(connectedInterval(base).getLow(), Math.E)) {
			return evaluator.expSet(exponent);
		}

		if (!IntervalSetOps.isPositive(base) && right.asExpressionNode() != null) {
			try {
				NegPowerResult negPower = calculateNegPowerSet(right.asExpressionNode(), base);
				if (negPower.handled) {
					return negPower.result;
				}
			} catch (Exception e) {
				Log.debug(e);
			}
		}

		return evaluator.powSet(base, exponent);
	}

	private NegPowerResult calculateNegPowerSet(IntervalExpressionNode node, IntervalSet base) {
		if (base.isEmpty()) {
			return NegPowerResult.handled(empty());
		}

		if (base.isWhole()) {
			return NegPowerResult.deferred();
		}

		if (base.isConnected()) {
			return calculateNegPowerConnected(node, base);
		}
		if (base.isInverted()) {
			return calculateNegPowerInverted(node, base);
		}
		return NegPowerResult.deferred();
	}

	private NegPowerResult calculateNegPowerInverted(IntervalExpressionNode node,
			IntervalSet base) {
		NegPowerResult left = calculateNegPowerConnected(node, leftRayFromInverted(base));
		NegPowerResult right = calculateNegPowerConnected(node, rightRayFromInverted(base));
		if (!left.handled || !right.handled) {
			return NegPowerResult.deferred();
		}
		return NegPowerResult.handled(combineRayResults(left.result, right.result));
	}

	private NegPowerResult calculateNegPowerConnected(IntervalExpressionNode node,
			IntervalSet base) {
		if (isPositiveFraction(node)) {
			return negativePowerConnected(base, node);
		} else if (isNegativeFraction(node)) {
			NegPowerResult negativePower =
					negativePowerConnected(base, node.getRight().asExpressionNode());
			if (!negativePower.handled) {
				return NegPowerResult.deferred();
			}
			return NegPowerResult.handled(evaluator.multiplicativeInverseSet(negativePower.result));
		}

		return NegPowerResult.deferred();
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

	private NegPowerResult negativePowerConnected(IntervalSet baseSet,
			IntervalExpressionNode node) {
		Interval nominator = node.getLeft().value();
		if (nominator.isSingletonInteger()) {
			Interval denominator = node.getRight().value();
			if (denominator.isUndefined()) {
				return NegPowerResult.handled(empty());
			} else if (denominator.isSingletonInteger()) {
				return NegPowerResult.handled(powerFractionConnected(baseSet,
						(long) nominator.getLow(), (long) denominator.getLow()));

			}
		}
		return NegPowerResult.deferred();
	}

	private IntervalSet powerFractionConnected(IntervalSet x, long a, long b) {
		IntervalSet posPower = powerFractionPositiveConnected(x, Math.abs(a), Math.abs(b));
		if (a * b < 0) {
			return evaluator.inverseSet(posPower);
		} else {
			return posPower;
		}
	}

	private IntervalSet powerFractionPositiveConnected(IntervalSet x, long a, long b) {
		long gcd = Kernel.gcd(a, b);
		if (gcd == 0) {
			return empty();
		}

		long nominator = a / gcd;
		long denominator = b / gcd;
		IntervalSet base = nominator == 1 ? x : evaluator.powSet(x, nominator);
		IntervalSet denominatorSet = connected(denominator, denominator);

		if (IntervalSetOps.isPositiveWithZero(base)) {
			return evaluator.nthRootSet(base, denominatorSet);
		}
		if (hasZero(base)) {
			Interval interval = connectedInterval(base);
			if (isOdd(denominator)) {
				return connected(-Math.pow(-interval.getLow(), 1d / denominator),
						Math.pow(interval.getHigh(), 1d / denominator));
			}

			return evaluator.nthRootSet(connected(0, interval.getHigh()), denominatorSet);
		}
		if (isOdd(denominator)) {
			return negative(evaluator.nthRootSet(negative(base), denominatorSet));
		}

		return empty();
	}

	private IntervalSet combineRayResults(IntervalSet left, IntervalSet right) {
		if (left.isEmpty()) {
			return right;
		}
		if (right.isEmpty()) {
			return left;
		}
		if (left.equals(right)) {
			return left;
		}
		if (left.isConnected() && right.isConnected()) {
			Interval leftInterval = connectedInterval(left);
			Interval rightInterval = connectedInterval(right);
			if (leftInterval.getHigh() < rightInterval.getLow()) {
				return IntervalSetOps.invertedGapFromSeparatedResults(left, right);
			}
			return evaluator.unionSet(left, right);
		}
		return empty();
	}

	private boolean isOdd(long value) {
		return (Math.abs(value) % 2) == 1;
	}
}
