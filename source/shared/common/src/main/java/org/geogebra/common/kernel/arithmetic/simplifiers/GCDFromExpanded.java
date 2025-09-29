package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isIntegerValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.plugin.Operation;

/**
 * <p>It factorizes GCD from expanded expression<br>
 * <b>a + b sqrt(m) + c sqrt(n) - d sqrt(o) &#8594;
 * gcd * (g + h sqrt(m) + j sqrt(n) + k sqrt(o))</b><br>
 * where gcd is the general common divisor of a, b, c and d.</p>
 *
 * <p>if no such non-trivial gcd, it returns the node in reduced form
 * @see SimplifyUtils#reduceExpressions(List) </p>
 *
 * <p>It is used for the nominator after expanding and before canceling GCD in a fraction</p>
 */
public final class GCDFromExpanded implements SimplifyNode {
	private final SimplifyUtils utils;
	private List<ExpressionValue> flatten;
	private final Map<Integer, Integer> sqrtMap = new HashMap<>();

	public GCDFromExpanded(@Nonnull SimplifyUtils utils) {
		this.utils = utils;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return true;
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		flatten = new ArrayList<>();
		node.any(this::flattenNode);
		ExpressionNode reduceExpressions = utils.reduceExpressions(flatten);
		int sumOfInts = 0;
		ExpressionNode rest = null;
		for (ExpressionValue value : flatten) {
			if (isIntegerValue(value)) {
				sumOfInts += (int) value.evaluateDouble();
			} else {
				if (ExpressionValueUtils.isSqrtNode(value)) {
					rest = addOrLet(rest, value.wrap());
				} else if (value.isOperation(Operation.MULTIPLY)) {
					ExpressionNode product = utils.reduceProduct(value.wrap()).wrap();
					if (isIntegerValue(product.getLeft()) && ExpressionValueUtils.isSqrtNode(
							product.getRightTree())) {
						int radicand = (int) product.getRightTree().getLeft().evaluateDouble();
						int amount = (int) product.getLeft().evaluateDouble();
						if (Math.abs(amount) == 1 && !sqrtMap.containsKey(radicand)) {
							rest = addOrLet(rest,
									amount == 1
											? utils.newSqrt(radicand)
											: utils.newSqrt(radicand).multiplyR(-1));
						} else {
							addToSqrtMap(radicand, amount);
						}
					}
				}
			}
		}
		if (rest != null) {
			return reduceExpressions;
		}
		if (sumOfInts == 0 || sqrtMap.isEmpty()) {
			return node;
		}

		if (sqrtMap.size() == 1) {
			Integer key = sqrtMap.keySet().iterator().next();
			return sqrtFromMap(key).plus(utils.newDouble(sumOfInts).wrap());
		}

		long gcd = 1;
		for (Integer sqrt : sqrtMap.values()) {
			gcd = Math.min(gcd, Kernel.gcd(sumOfInts, sqrt));
		}

		long gcd1 = gcd == -1 ? 1 : Kernel.gcd(sumOfInts, gcd);
		if (gcd1 == 1) {
			ExpressionNode result = utils.newDouble(sumOfInts).wrap();
			for (Map.Entry<Integer, Integer> entry : sqrtMap.entrySet()) {
				ExpressionNode newSqrt = utils.newSqrt(entry.getKey());
				result = addOrLet(result, utils.multiplyR(newSqrt, entry.getValue()));
			}

			return result;
		}
		ExpressionNode sum = null;
		ExpressionNode mod = null;
		for (Map.Entry<Integer, Integer> entry : sqrtMap.entrySet()) {
			long multiplier = entry.getValue() / gcd1;
			ExpressionNode newSqrt = utils.newSqrt(entry.getKey());
			if (multiplier != 0 && Math.abs(entry.getValue()) >= Math.abs(multiplier * gcd1)) {
				ExpressionNode gcdSqrt = newGCDSqrt(entry.getKey(), multiplier);
				sum = addOrLet(sum, Math.abs(entry.getValue()) == Math.abs(gcd1)
						? newSqrt.multiplyR(Math.signum((double) multiplier))
						: gcdSqrt);
			}
			if (entry.getValue() != multiplier * gcd1) {
				long restAmount = entry.getValue() - multiplier * gcd1;
				if (restAmount == -1) {
					newSqrt = newSqrt.multiplyR(-1);
				}

				ExpressionNode modSqrt = Math.abs(restAmount) == 1
						? newSqrt : utils.newMultiply(utils.newDouble(restAmount), newSqrt);
				mod = addOrLet(mod, modSqrt);
			}
		}

		ExpressionNode gcdInts = utils.newDouble((double) sumOfInts / gcd1).wrap();
		sum = addOrLet(sum, gcdInts);

		if (mod != null) {
			return reduceExpressions;
		}

		return utils.newMultiply(utils.newDouble(gcd1), sum);
	}

	private ExpressionNode sqrtFromMap(Integer key) {
		Integer multiplicative = sqrtMap.get(key);
		ExpressionNode sqrtExp = utils.newSqrt(key);
		return multiplicative == 1
				? sqrtExp
				: utils.newMultiply(utils.newDouble(multiplicative), sqrtExp);
	}

	/**
	 * Adds expression value to result, or let result be ev if it was null.
	 * @param result to add to.
	 * @param ev to add.
	 * @return result.add(ev) or ev if result is null.
	 */
	static ExpressionNode addOrLet(ExpressionValue result, ExpressionValue ev) {
		return result != null ? result.wrap().plus(ev) : ev.wrap();
	}

	private void addToSqrtMap(Integer sqrtKey, Integer amount) {
		sqrtMap.put(sqrtKey, sqrtMap.getOrDefault(sqrtKey, 0) + amount);
	}

	private ExpressionNode newGCDSqrt(Integer radicand, long multiplier) {
		if (multiplier == 1) {
			return utils.newSqrt(radicand);
		}

		if (multiplier == -1) {
			return utils.newSqrt(radicand).multiplyR(-1);
		}

		return utils.newMultiply(
				utils.newDouble(multiplier),
				utils.newSqrt(radicand));
	}

	private boolean flattenNode(ExpressionValue value) {
		if (value.isOperation(Operation.PLUS)) {
			ExpressionValue left = simplifyIfProduct(value.wrap().getLeftTree());
			ExpressionValue right = simplifyIfProduct(value.wrap().getRightTree());
			return left.any(this::flattenNode) && right.any(this::flattenNode);
		}

		if (value.isLeaf() || ExpressionValueUtils.isSqrtNode(value)) {
			flatten.add(value);
		} else if (value.isOperation(Operation.MULTIPLY)) {
			flatten.add(utils.reduceProduct(value.wrap()));
		}
		return true;
	}

	private ExpressionValue simplifyIfProduct(ExpressionValue value) {
		ExpressionNode node = value.wrap();
		return node.isOperation(Operation.MULTIPLY) ? utils.reduceProduct(node) : node;
	}
}
