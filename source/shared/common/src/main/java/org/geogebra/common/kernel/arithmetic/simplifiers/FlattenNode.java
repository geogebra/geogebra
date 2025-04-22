package org.geogebra.common.kernel.arithmetic.simplifiers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.plugin.Operation;

public final class FlattenNode implements Inspecting, Iterable<ExpressionValue> {
	private List<ExpressionValue> flatten = new ArrayList<>();
	private List<Integer> multipliers = new ArrayList<>();
	private boolean minus;
	private SimplifyUtils utils;

	/**
	 *
	 * @param node to flatten
	 * @param utils {@link SimplifyUtils}
	 */
	public FlattenNode(ExpressionNode node, SimplifyUtils utils) {
		this.utils = utils;
		node.any(this);
	}

	@Override
	public boolean check(ExpressionValue ev) {
		if (ev.isLeaf() || ev.isOperation(Operation.MULTIPLY) || ExpressionValueUtils.isSqrtNode(
				ev)) {
			flatten.add(ev);
			int multiplier = utils.getNumberForGCD(ev.wrap());
			multipliers.add(minus ? -multiplier : multiplier);
			minus = false;
			return true;

		}
		if (ev.isOperation(Operation.PLUS) || ev.isOperation(Operation.MINUS)) {
			minus = false;
			boolean leftInspect = ev.wrap().getLeft().any(this);
			minus = ev.isOperation(Operation.MINUS);
			boolean rightInspect = ev.wrap().getRight().any(this);
			return leftInspect
					&& rightInspect;
		}
		return false;
	}

	/**
	 * Gets number of terms.
	 * @return number of terms.
	 */
	public int size() {
		return flatten.size();
	}

	/**
	 * @param index index
	 * @return term of the flattened node
	 */
	public ExpressionValue get(int index) {
		return flatten.get(index);
	}

	public ExpressionNode getReducedExpression() {
		return utils.reduceExpressions(flatten);
	}

	@Override
	public Iterator<ExpressionValue> iterator() {
		return flatten.iterator();
	}

	@Override
	public void forEach(Consumer<? super ExpressionValue> action) {
		flatten.forEach(action);
	}

	@Override
	public Spliterator<ExpressionValue> spliterator() {
		return flatten.spliterator();
	}
}
