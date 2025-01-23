package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.geogebra.common.kernel.arithmetic.simplifiers.SimplifyUtils.isIntegerValue;
import static org.geogebra.common.kernel.arithmetic.simplifiers.SimplifyUtils.isNodeSupported;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.OperationCountChecker;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

import edu.umd.cs.findbugs.annotations.NonNull;

public final class RationalizableFraction {
	public static final boolean LOG_ENABLED = true;
	private final ExpressionSimplifiers simplifiers;
	private final SimplifyUtils utils;
	private ExpressionNode root;

	private static final OperationCountChecker sqrtCountChecker =
			new OperationCountChecker(Operation.SQRT);
	private double rootValue;

	private RationalizableFraction(ExpressionNode root) {
		Kernel kernel = root.getKernel();
		this.root = root.deepCopy(kernel);
		utils = new SimplifyUtils(kernel);
		simplifiers = new ExpressionSimplifiers(utils, LOG_ENABLED);
	}

	/**
	 * Rationalize the fraction.
	 * @param node to rationalize
	 * @return the rationalized fraction.
	 */
	public static ExpressionValue getResolution(@NonNull ExpressionNode node) {
		RationalizableFraction fraction = new RationalizableFraction(node);
		return fraction.simplify();
	}

	private ExpressionNode simplify() {
		ExpressionNode first = simplifiers.runFirst(root);
		if (!isSupported(root)) {
			return null;
		}

		root = first;

		rootValue = root.evaluateDouble();

		if (!Double.isFinite(rootValue)) {
			return root;
		}

		if (isIntegerValue(root)) {
			return utils.newDouble(root.evaluateDouble()).wrap();
		}

		double denominatorValue = root.getRightTree().evaluateDouble();

		ExpressionValue resolution = SimplifyUtils.isIntegerValue(denominatorValue)
				? simplifyNormalizedRadicalFraction(denominatorValue)
				: rationalizeFraction();

	return simplifiers.run(resolution);
	}

	/**
	 * Decides if geo is a rationalizable fraction and supported by this algo.
	 * The most complicated fraction that supported is:
	 *     (a + sqrt(b)) / (c + sqrt(d))
	 *
	 * @return if geo is supported.
	 */
	public boolean isSupported(ExpressionNode root) {
		if (root == null) {
			return false;
		}

		if (SimplifyUtils.isIntegerValue(root)) {
			return true;
		}

		if (!utils.isDivNode(root)) {
			return false;
		}

		int sqrtsInNumerator = getSquareRootCount(root.getLeft());
		int sqrtsInDenominator = getSquareRootCount(root.getRight());
		if (sqrtsInDenominator == 0) {
			return true;
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
		return utils.getLeftMultiplier(node) == 1 ? node : node.getRightTree();
	}

	private static int getSquareRootCount(ExpressionValue node) {
		sqrtCountChecker.reset();
		node.inspect(sqrtCountChecker);
		return sqrtCountChecker.getCount();
	}

	private ExpressionNode rationalizeFraction() {
		logRootExpression();
		ExpressionNode copy = utils.deepCopy(root);
		RationalizeFractionAlgo algo =
				new RationalizeFractionAlgo(utils, copy.getLeftTree(),
						copy.getRightTree());
		ExpressionNode computed = algo.compute();
		logRationalizedNode(computed);
		return computed;
	}

	private void logRootExpression() {
		debug("start: " + root.toValueString(StringTemplate.defaultTemplate)
				+ "(=" + root.evaluateDouble() + ")");
	}

	private void debug(String msg) {
		if (LOG_ENABLED) {
			Log.debug(msg);
		}
	}

	private void logRationalizedNode(ExpressionNode node) {
		debug("rationalized: " + node.toValueString(StringTemplate.defaultTemplate));

	}

	private ExpressionValue simplifyNormalizedRadicalFraction(double denominatorValue) {
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