package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MinusOne;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.plugin.Operation;

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
	public Interval handle(Interval x) throws Exception {
		Interval leftEvaluated = IntervalFunction.evaluate(x, node.getLeft());
		ExpressionValue right = node.getRight();
		Interval rightEvaluated = IntervalFunction.evaluate(x, right);
		return handle(leftEvaluated, rightEvaluated, right);
	}

	private Interval handle(Interval base, Interval exponent, ExpressionValue right) {
		if (MyDouble.exactEqual(base.getLow(), Math.E)) {
			return exponent.exp();
		}

		if (base.isNegative() && right.isExpressionNode()) {
			try {
				return calculateNegPower(right.wrap(), base);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return base.pow(exponent);
	}

	private Interval calculateNegPower(ExpressionNode node, Interval base) throws Exception {
		if (node.isOperation(Operation.DIVIDE)) {
			return negPower(base, node);
		} else if (node.getOperation() == Operation.MULTIPLY
				&& node.getLeft() instanceof MinusOne
				&& node.getRight().isOperation(Operation.DIVIDE)) {
			return negPower(base, node.getRight().wrap()).multiplicativeInverse();
		}
		return undefined();
	}

	private Interval negPower(Interval base, ExpressionNode node) throws Exception {
		Interval nominator = IntervalFunction.evaluate(base, node.getLeft());
		if (nominator.isSingletonInteger()) {
			Interval denominator = IntervalFunction.evaluate(base, node.getRight());
			if (denominator.isUndefined()) {
				return undefined();
			} else if (denominator.isSingletonInteger()) {
				return powerFraction(base, nominator, denominator);

			}
		}
		return undefined();
	}

	private Interval powerFraction(Interval base, Interval a, Interval b) {
		long al = (long) a.getLow();
		long bl = (long) b.getLow();
		long gcd = Kernel.gcd(al, bl);
		// fix for java.lang.ArithmeticException: divide by zero
		// https://play.google.com/apps/publish/?dev_acc=05873811091523087820#ErrorClusterDetailsPlace:p=org.geogebra.android&et=CRASH&lr=LAST_7_DAYS&ecn=java.lang.ArithmeticException&tf=SourceFile&tc=org.geogebra.common.kernel.arithmetic.ExpressionNodeEvaluator&tm=negPower&nid&an&c&s=new_status_desc&ed=0
		if (gcd == 0) {
			return undefined();
		}

		al = al / gcd;
		bl = bl / gcd;

		// we will now evaluate (x^a)^(1/b) instead of
		// x^(a/b)
		// set base = x^a
		if (al != 1) {
			base = base.pow(al);
		}
		if (base.isPositive()) {
			// base > 0 => base^(1/b) is no problem
			return base.pow(1d / bl);
		}
		boolean oddB = (Math.abs(bl) % 2) == 1;
		if (oddB) {
			// base < 0 and b odd: (base)^(1/b) =
			// -(-base^(1/b))
			return base.negative().pow(1d / bl);
		}
		// base < 0 and a & b even: (base)^(1/b)
		// = undefined
		return undefined();
	}

}
