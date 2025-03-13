package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isIntegerValue;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.plugin.Operation;

final class SurdAddition {
	ExpressionNode a;
	ExpressionNode b;
	private final ExpressionNode node;
	private final SimplifyUtils utils;

	SurdAddition(ExpressionNode node, SimplifyUtils utils) {
		a = isIntegerValue(node.getLeftTree()) ? utils.newDouble(node.getLeft()).wrap()
				: node.getLeftTree();
		b = node.getRightTree();
		this.node = node;
		this.utils = utils;
		if (node.getOperation() == Operation.MINUS) {
			if (b.isOperation(Operation.MULTIPLY)) {
				b = b.getLeft().evaluateDouble() == -1 ? b.getRightTree()
						: b.multiplyR(-1);
			} else {
				b = node.getRightTree().multiplyR(-1);
			}
		}

	}

	@CheckForNull
	public ExpressionNode factorOut() {
		ensureIntegerFirst();
		if (ExpressionValueUtils.isSqrtNode(b) || ExpressionValueUtils.isNegativeSqrt(b)) {
			return null;
		}
		long fa = utils.getNumberForGCD(a);
		ExpressionValue factorB = getFactor(b);
		long fb = (long) factorB.wrap().getLeft().evaluateDouble();
		long gcd = Kernel.gcd(fa, fb);
		if (gcd == 1) {
			return null;
		}
		ExpressionNode node;
		long fbDivGcd = fb / gcd;
		ExpressionValue v2 = fbDivGcd == 1 ? factorB.wrap().getRight()
				: utils.newMultiply(
				utils.newDouble(fbDivGcd),
				factorB.wrap().getRight()
		);
		if (gcd < 0 && fb < 0 && fa > 0) {
			ExpressionNode num = isIntegerValue(a)
					? utils.newDouble((double) fa / -gcd).wrap()
					: divWithGcd(a, -gcd);
			node = v2 != null ? num.subtract(v2) : num;

			return utils.newDouble(-gcd).wrap().multiplyR(node);
		} else {
			ExpressionNode num = isIntegerValue(a)
					? utils.newDouble((double) fa / gcd).wrap()
					: divWithGcd(a, gcd);

			node = v2 != null ? num.plus(v2) : num;
			return utils.newDouble(gcd).wrap().multiplyR(node);
		}

	}

	private ExpressionNode divWithGcd(ExpressionNode node, long gcd) {
		long multiplier = ExpressionValueUtils.getLeftMultiplier(node) / gcd;
		if (multiplier == 1) {
			return node.getRightTree();
		}
		return node.getRightTree().multiplyR(multiplier);
	}

	private void ensureIntegerFirst() {
		if (isIntegerValue(b)) {
			ExpressionNode tmp = b;
			b = a;
			a = tmp;
		}
	}

	private ExpressionValue getFactor(ExpressionNode node) {
		if (isIntegerValue(node)) {
			return utils.newDouble(node.evaluateDouble());
		}
		if (node.isOperation(Operation.MULTIPLY)) {
			int multiplier = ExpressionValueUtils.getLeftMultiplier(node);
			if (multiplier == -1) {
				ExpressionValue right = node.getRight();
				if (right.isOperation(Operation.MULTIPLY) && isIntegerValue(
						right.wrap().getLeft())) {
					return utils.newMultiply(
							utils.newDouble(-1 * right.wrap().getLeft().evaluateDouble()),
							right.wrap().getRight()
					);
				}
				return utils.newDouble(multiplier);
			}
		}
		return node;
	}

	public ExpressionValue multiply(double multiplier) {
		a = multiply(a, multiplier).wrap();
		b = multiply(b, multiplier).wrap();
		Operation operation = node.getOperation();
		if (a.evaluateDouble() < 0 && (node.isOperation(Operation.MINUS)
				|| b.evaluateDouble() < 0)) {
			return utils.newNode(utils.flipSign(a.wrap()), Operation.inverse(operation), b)
					.multiplyR(-1);
		}
		boolean flipNeeded =
				multiplier < 0 && operation == Operation.MINUS && b.evaluateDouble() > 0;
		return utils.newNode(a, flipNeeded ? Operation.inverse(operation) : operation, b);
	}

	private ExpressionValue multiply(ExpressionValue ev, double multiplier) {
		if (isIntegerValue(ev)) {
			return utils.newDouble(ev.evaluateDouble() * multiplier);
		}
		ExpressionNode node = ev.wrap();
		int leftMultiplier = ExpressionValueUtils.getLeftMultiplier(node);
		if (leftMultiplier != 1) {
			double mul = multiplier * leftMultiplier;
			return mul == 1
					? node.getRight()
					: utils.newMultiply(utils.newDouble(mul), node.getRight());
		}
		return node.multiplyR(multiplier);
	}

	public ExpressionValue multiply(ExpressionValue ev) {
		ExpressionValue a1 = utils.reduceProduct(a.multiply(ev));
		ExpressionValue b1 = utils.reduceProduct(b.multiply(ev));
		return utils.newNode(a1, Operation.PLUS, b1);
	}
}