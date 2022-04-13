package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalOperands.pow;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MinusOne;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

/**
 * Class to evaluate expressions on an interval that has power in it.
 */
public class IntervalPowerEvaluator {
	private final ExpressionNode node;

	/**
	 *
	 * @param node expression to evaluate.
	 */
	public IntervalPowerEvaluator(ExpressionNode node) {
		this.node = node;
	}

	/**
	 *
	 * @return if this class can handle the expression.
	 */
	public boolean isAccepted() {
		return node.getOperation().equals(Operation.POWER);
	}

	/**
	 *
	 * @param x interval
	 * @return power expression evaluated on x.
	 */
	public Interval handle(Interval x) {
		Interval leftEvaluated = IntervalFunction.evaluate(x, node.getLeft());
		ExpressionValue right = node.getRight();
		Interval rightEvaluated = IntervalFunction.evaluate(x, right);
		return handle(leftEvaluated, rightEvaluated, right);
	}

	private Interval handle(Interval base, Interval exponent, ExpressionValue right) {
		if (MyDouble.exactEqual(base.getLow(), Math.E)) {
			return IntervalOperands.exp(exponent);
		}

		if (!base.isPositive() && right.isExpressionNode()) {
			try {
				Interval negPower = calculateNegPower(right.wrap(), base);
				if (!negPower.isUndefined()) {
					return negPower;
				}
			} catch (Exception e) {
				Log.debug(e);
			}
		}

		return pow(base, exponent);
	}

	private Interval calculateNegPower(ExpressionNode node, Interval base) throws Exception {
		if (isPositiveFraction(node)) {
			return negativePower(base, node);
		} else if (isNegativeFraction(node)) {
			return negativePower(base, node.getRight().wrap())
					.multiplicativeInverse();
		}

		return undefined();
	}

	private boolean isPositiveFraction(ExpressionNode node) {
		return node.isOperation(Operation.DIVIDE);
	}

	private boolean isNegativeFraction(ExpressionNode node) {
		return node.getOperation() == Operation.MULTIPLY
				&& node.getLeft() instanceof MinusOne
				&& node.getRight().isOperation(Operation.DIVIDE);
	}

	private Interval negativePower(Interval base, ExpressionNode node) {
		Interval nominator = IntervalFunction.evaluate(base, node.getLeft());
		if (nominator.isSingletonInteger()) {
			Interval denominator = IntervalFunction.evaluate(base, node.getRight());
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
			return posPower.multiplicativeInverse();
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
				: pow(interval, nominator);

		if (base.isPositiveWithZero()) {
			return pow(base, 1d / denominator);
		}
		if (base.contains(0)) {
			if (isOdd(denominator)) {
				Interval ret = new Interval(-Math.pow(-base.getLow(), 1d / denominator),
						Math.pow(base.getHigh(), 1d / denominator));
				return ret;
			}

			return pow(new Interval(0, base.getHigh()), 1d / denominator);
		}
		if (isOdd(denominator)) {
			return pow(base.negative(), 1d / denominator).negative();
		}

		return undefined();
	}

	private boolean isOdd(long value) {
		return (Math.abs(value) % 2) == 1;
	}
}
