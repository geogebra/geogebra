package org.geogebra.common.kernel.arithmetic.simplifiers;

import java.util.List;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.util.debug.Log;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Simplifier list to run on {@link ExpressionNode}
 */
public class ExpressionSimplifiers {

	private final List<SimplifyNode> preItems;
	private final List<SimplifyNode> postItems;
	private final boolean logEnabled;

	/**
	 *
	 * @param utils {@link SimplifyUtils}
	 * @param logEnabled pass true to enable the process log of simplifiers.
	 */
	public ExpressionSimplifiers(@Nonnull SimplifyUtils utils, boolean logEnabled) {
		this.logEnabled = logEnabled;

		// it is checked before any simplification and after all were run if they produced a
		// trivial node.
		CheckIfTrivial checkIfTrivial = new CheckIfTrivial(utils);
		preItems = List.of(
				checkIfTrivial
		);

		postItems = List.of(
				new ReduceRoot(utils),
				new ExpandAndFactorOutGCD(utils),
				new FactorOutGCDFromSurd(utils),
				new CancelGCDInFraction(utils),
				new PositiveDenominator(utils),
				new PlusTagOrder(utils),
				new DistributeMultiplier(utils),
				new MoveMinusInOut(utils),
				checkIfTrivial
		);
	}

	/**
	 * Run the simplifiers on node.
	 * @param node to run on.
	 * @return the simplified node.
	 */
	public ExpressionNode run(@Nullable ExpressionValue node) {
		if (node == null) {
			return null;
		}

		return simplifyWith(postItems, node.wrap());
	}

	private ExpressionNode simplifyWith(List<SimplifyNode> simplifiers, ExpressionNode inputNode) {
		ExpressionNode node = inputNode;
		for (SimplifyNode simplifier : simplifiers) {
			if (simplifier.isAccepted(node)) {
				String before = node == null ? ""
						: node.toValueString(StringTemplate.defaultTemplate);
				node = simplifier.apply(node);
				logProgress(simplifier.name(), before, node);

			}
		}
		return node;
	}

	/**
	 * Log if a simplifier has changed anything on node.
	 * @param name of the simplifier.
	 * @param before the serialized value of the node before the simplifier ran.
	 * @param node the node after the simplifier ran.
	 */
	private void logProgress(String name, String before, ExpressionNode node) {
		if (!logEnabled) {
			return;
		}
		String after = node.toValueString(StringTemplate.defaultTemplate);
		if (!after.equals(before)) {
			Log.debug(name + ": " + after
					+ "( =" + node.evaluateDouble() + ")");
		}

	}

	/**
	 * Run this on root node before further simplifications.
	 * @param root to simplify
	 * @return the simplified result
	 */
	public ExpressionNode runFirst(ExpressionNode root) {
		return simplifyWith(preItems, root);
	}
}
