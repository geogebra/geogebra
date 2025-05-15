package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isIntegerValue;
import static org.geogebra.common.kernel.arithmetic.simplifiers.ExpressionValueUtils.isNodeSupported;
import static org.geogebra.common.util.DoubleUtil.isInteger;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.OperationCountChecker;
import org.geogebra.common.plugin.Operation;

/**
 * Class that rationalizes a fraction if it is supported.
 *
 * <p>The most complicated fraction that supported is <b>(a + sqrt(b)) / (c + sqrt(d))</b></p>
 *
 * It also gets the simplest, extracted form.
 * Example: (-10 + sqrt(5)) / (-2 + sqrt(5)) &#8594; -15 - 8sqrt(5)
 *
 */
public final class RationalizableFraction {
	private final ExpressionSimplifiers simplifiers;
	private final SimplifyUtils utils;
	private ExpressionNode root;

	private static final OperationCountChecker sqrtCountChecker =
			new OperationCountChecker(Operation.SQRT);

	RationalizableFraction(ExpressionNode root) {
		Kernel kernel = root.getKernel();
		this.root = root.deepCopy(kernel);
		utils = new SimplifyUtils(kernel);
		simplifiers = new ExpressionSimplifiers(utils, false);
	}

	ExpressionNode simplify() {
		if (!isSupported(root)) {
			return null;
		}

		root = simplifiers.runFirst(root);

		double rootValue = root.evaluateDouble();

		if (!Double.isFinite(rootValue)) {
			return root;
		}

		if (isIntegerValue(root)) {
			return root.evaluate(StringTemplate.defaultTemplate).wrap();
		}

		double denominatorValue = root.getRightTree().evaluateDouble();

		ExpressionValue resolution = isInteger(denominatorValue)
				? simplifyNormalizedRadicalFraction((int) denominatorValue)
				: rationalizeFraction();

		return simplifiers.run(resolution);
	}

	/**
	 * <p>Decides if geo is a rationalizable fraction and supported.</p>
	 * For what is supported, @see RationalizableFraction
	 *
	 * @return if geo is supported.
	 */
	public boolean isSupported(ExpressionNode root) {
		if (root == null) {
			return false;
		}

		if (isIntegerValue(root)) {
			return true;
		}

		if (!ExpressionValueUtils.isDivNode(root)) {
			return false;
		}

		int sqrtsInNumerator = getSquareRootCount(root.getLeft());
		int sqrtsInDenominator = getSquareRootCount(root.getRight());
		if (sqrtsInDenominator == 0) {
			return false;
		}
		if ((sqrtsInNumerator > 1 || sqrtsInDenominator > 1)
				|| (sqrtsInNumerator + sqrtsInDenominator == 0)) {
			return false;
		}

		ExpressionNode numerator = stripFromLeftMultiplier(root.getLeftTree());
		ExpressionNode denominator = stripFromLeftMultiplier(root.getRightTree());

		return isNodeSupported(numerator) && isNodeSupported(denominator);
	}

	private ExpressionNode stripFromLeftMultiplier(ExpressionNode node) {
		return ExpressionValueUtils.getLeftMultiplier(node) == 1 ? node : node.getRightTree();
	}

	private static int getSquareRootCount(ExpressionValue node) {
		sqrtCountChecker.reset();
		node.any(sqrtCountChecker);
		return sqrtCountChecker.getCount();
	}

	private ExpressionNode rationalizeFraction() {
		ExpressionNode copy = utils.deepCopy(root);
		RationalizeFractionAlgo algo =
				new RationalizeFractionAlgo(utils, copy.getLeftTree(),
						copy.getRightTree());
		return algo.compute();
	}

	private ExpressionValue simplifyNormalizedRadicalFraction(int denominatorValue) {
		if (denominatorValue == 0) {
			return utils.newDouble(Double.NEGATIVE_INFINITY);
		}
		if (denominatorValue == 1) {
			return new ExpressionNode(root.getLeftTree());
		}
		if (denominatorValue == -1) {
			return (new ExpressionNode(root.getLeftTree())).multiplyR(-1);
		}
		return root;
	}
}