package org.geogebra.common.kernel.arithmetic.simplifiers;

import static java.util.Comparator.comparing;
import static org.geogebra.common.util.DoubleUtil.isInteger;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.plugin.Operation;

public class ExpandNode implements SimplifyNode {

	private final SimplifyUtils utils;

	public ExpandNode(SimplifyUtils utils) {
		this.utils = utils;
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		if (!isAccepted(node)) {
			return node;
		}
		ExpressionNode tagA = node.getLeftTree();
		ExpressionValue a1 = tagA.getLeft();
		ExpressionValue a2 = tagA.getRight();
		ExpressionNode tagB = node.getRightTree();
		ExpressionValue b1 = tagB.getLeft();
		ExpressionValue b2 = tagB.getRight();
		int signA2 = tagA.isOperation(Operation.MINUS) ? -1 : 1;
		int signB2 = tagB.isOperation(Operation.MINUS) ? -1 : 1;
		ExpressionNode a1b1 = multiplyValues(a1, b1, 1).wrap();
		ExpressionNode a1b2 = multiplyValues(a1, b2, signB2).wrap();
		ExpressionNode a2b1 = multiplyValues(a2, b1, signA2).wrap();
		ExpressionNode a2b2 = multiplyValues(a2, b2, signA2 * signB2).wrap();
		List<ExpressionNode> list = Arrays.asList(a1b1, a1b2, a2b1, a2b2);
		list.sort(comparing(e -> SimplifyUtils.isIntegerValue((ExpressionValue) e))
				.thenComparing(n -> ((ExpressionNode) n).evaluateDouble())
				.reversed());
		double num = 0;
		int i = 0;
		double eval = list.get(0).evaluateDouble();
		while (i < list.size() && isInteger(eval)) {
			num += eval;
			i++;
			eval = list.get(i).evaluateDouble();
		}

		ExpressionNode rest = utils.newDouble(num).wrap();

		while (i < list.size()) {
			ExpressionNode item = list.get(i).wrap();
			if (utils.isMultiplyNode(item) && item.evaluateDouble() == -1) {
				rest = rest.subtract(utils.getSurdsOrSame(item.getRight()));
			} else {
				rest = rest.plus(utils.getSurdsOrSame(item));
			}
			i++;
		}

		return rest;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return node.isOperation(Operation.MULTIPLY) && isTag(node.getLeftTree())
				&& isTag(node.getRightTree());
	}

	private boolean isTag(ExpressionNode node) {
		return node.getLeftTree() != null && node.getRightTree() != null
			&& (node.isOperation(Operation.PLUS) || node.isOperation(Operation.MINUS));
	}

	ExpressionValue multiplyValues(ExpressionValue a, ExpressionValue b, int sign) {
		boolean aIsNumber = a instanceof NumberValue;
		boolean bIsNumber = b instanceof NumberValue;
		ExpressionValue product = null;
		if (aIsNumber && bIsNumber) {
			double v = a.evaluateDouble() * b.evaluateDouble() * sign;
			product = utils.newDouble(v);
		} else if (aIsNumber) {
			product = b.wrap().multiplyR(a.evaluateDouble() * sign);
		} else if (bIsNumber) {
			product = a.wrap().multiplyR(b.evaluateDouble() * sign);
		} else if (a.isOperation(Operation.SQRT) && b.isOperation(Operation.SQRT)) {
			double v = a.wrap().getLeftTree().evaluateDouble()
					* b.wrap().getLeftTree().evaluateDouble();
			ExpressionNode sqrtNode = utils.newSqrt(v);
			product = utils.getSurdsOrSame(sqrtNode).wrap().multiplyR(sign);
		} else if (utils.evaluateMinusOne(b)) {
			product = utils.negate(a.wrap()).multiplyR(sign);
		} else {
			product = a.wrap().multiply(b).multiplyR(sign);
		}

		double v1 = product.evaluateDouble();
		if (isInteger(v1)) {
			return utils.newDouble(v1);
		}
		return product;
	}
}
