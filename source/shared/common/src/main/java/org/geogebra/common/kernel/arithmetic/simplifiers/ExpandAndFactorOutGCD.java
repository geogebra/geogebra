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

import static java.util.Comparator.comparing;
import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isIntegerValue;
import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isSqrtNode;
import static org.geogebra.common.util.DoubleUtil.isInteger;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.plugin.Operation;

/**
 * <p>It expands products as a result of factorization, reduces it and factor out a GCD
 * to simplify the fraction with, if possible. @see also {@link GCDFromExpanded}</p>
 *
 * For example:
 * <ul>
 *     <li> (-8 + 2sqrt(2) (-2 - sqrt(6)) / 16
 *         &#8594; -4 (sqrt(2) + sqrt(3) -  2sqrt(6) - 4) / 16</li>
 *     <li>(-8 - sqrt(10))(sqrt(4) + 7) &#8594; -9sqrt(10) - 72 where no GCD can be factored out
 *     (but can be highly reduced).</li>
 * </ul>
 *
 */
public class ExpandAndFactorOutGCD implements SimplifyNode {

	private enum NodeType {
		ONE_TAG_MULTIPLY,
		TWO_TAG_MULTIPLY,
		NONE
	}

	private final SimplifyUtils utils;

	/**
	 *
	 * @param utils {@link SimplifyUtils}
	 */
	public ExpandAndFactorOutGCD(SimplifyUtils utils) {
		this.utils = utils;
	}

	@Override
	public ExpressionNode apply(ExpressionNode node) {
		NodeType nodeType = getNodeType(node);
		if (node.isOperation(Operation.DIVIDE)) {
			return utils.newDiv(apply(node.getLeftTree()), node.getRightTree());
		}

		ExpressionNode rest = null;
		if (nodeType == NodeType.TWO_TAG_MULTIPLY) {
			List<ExpressionValue> list = getTagByTagProducts(node);
			rest = utils.reduceExpressions(list);
		} else if (nodeType == NodeType.ONE_TAG_MULTIPLY) {
			rest = multiplyOneTag(node);
		}

		GCDFromExpanded gcdFromExpanded = new GCDFromExpanded(utils);
		return rest != null ? gcdFromExpanded.apply(rest) : null;
	}

	private ExpressionNode multiplyOneTag(ExpressionNode node) {
		boolean tagLeft = ExpressionValueUtils.isAddSubNode(node.getLeft());
		SurdAddition
				tag = new SurdAddition(tagLeft ? node.getLeftTree() : node.getRightTree(), utils);
		ExpressionValue ev = tagLeft ? node.getRight() : node.getLeft();
		return tag.multiply(ev).wrap();
	}

	private List<ExpressionValue> getTagByTagProducts(ExpressionNode node) {
		SurdAddition tag1 = new SurdAddition(node.getLeftTree(), utils);
		SurdAddition tag2 = new SurdAddition(node.getRightTree(), utils);
		ExpressionNode a1b1 = multiply(tag1.a, tag2.a).wrap();
		ExpressionNode a1b2 = multiply(tag1.a, tag2.b).wrap();
		ExpressionNode a2b1 = multiply(tag1.b, tag2.a).wrap();
		ExpressionNode a2b2 = multiply(tag1.b, tag2.b).wrap();
		List<ExpressionValue> list = Arrays.asList(a1b1, a1b2, a2b1, a2b2);
		list.sort(comparing(e -> isIntegerValue((ExpressionValue) e))
				.thenComparing(n -> ((ExpressionNode) n).evaluateDouble())
				.reversed());
		return list;
	}

	@Override
	public boolean isAccepted(ExpressionNode node) {
		return getNodeType(node) != NodeType.NONE;
	}

	private NodeType getNodeType(ExpressionNode node) {
		if (node.isOperation(Operation.MULTIPLY)) {
			boolean tag1 = ExpressionValueUtils.isAddSubNode(node.getLeftTree());
			boolean tag2 = ExpressionValueUtils.isAddSubNode(node.getRightTree());
			if (tag1 && tag2) {
				return NodeType.TWO_TAG_MULTIPLY;
			}

			if (tag1 || tag2) {
				return NodeType.ONE_TAG_MULTIPLY;
			}
		}

		if (node.isOperation(Operation.DIVIDE)) {
			return getNodeType(node.getLeftTree());
		}
		return NodeType.NONE;
	}

	ExpressionValue multiply(ExpressionValue a, ExpressionValue b) {
		ExpressionNode product = multiplyExpressionValues(a, b).wrap();
		ExpressionNode result =
				product.isOperation(Operation.MULTIPLY) ? utils.reduceProduct(product) : product;
		if (isSqrtNode(result.getRight())
				&& isSqrtNode(result.getLeftTree().getRight())) {
			ExpressionValue sqrt1 = result.getRight();
			ExpressionValue sqrt2 = result.getLeftTree().getRight();
			double radicand = sqrt1.wrap().getLeft().evaluateDouble()
					* sqrt2.wrap().getLeft().evaluateDouble();
			double multiplier = result.wrap().getLeftTree().getLeft().evaluateDouble();
			ExpressionNode sqrtProduct = utils.getSurdsOrSame(utils.newSqrt(radicand)).wrap();
			result = multiplier == 1 ? sqrtProduct : utils.multiplyR(sqrtProduct, multiplier);
		}
		return utils.getSurdsOrSame(result);
	}

	ExpressionValue multiplyExpressionValues(ExpressionValue a, ExpressionValue b) {
		if (ExpressionValueUtils.isMinusOne(a)) {
			return b.wrap().multiplyR(-1);
		}
		if (ExpressionValueUtils.isMinusOne(b)) {
			return a.wrap().multiplyR(-1);
		}
		ExpressionValue product = null;

		if (a.isOperation(Operation.SQRT) && b.isOperation(Operation.SQRT)) {
			double v = a.wrap().getLeftTree().evaluateDouble()
					* b.wrap().getLeftTree().evaluateDouble();
			ExpressionNode sqrtNode = utils.newSqrt(v);
			product = utils.getSurdsOrSame(sqrtNode).wrap();
		} else {
			product = a.wrap().multiply(b);
		}

		double v1 = product.evaluateDouble();
		if (isInteger(v1)) {
			return utils.newDouble(v1);
		}
		return product;
	}
}
